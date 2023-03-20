package org.kie.fileio;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kie.domain.Meeting;
import org.kie.domain.MeetingSchedule;
import org.kie.domain.Room;
import org.kie.domain.Timeslot;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class MeetingSchedulingXlsx implements SolutionFileIO<MeetingSchedule> {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("E yyyy-MM-dd", Locale.ENGLISH);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private static final int GRAIN_LENGTH_IN_MINUTES = 60;
    private static final Pattern VALID_TAG_PATTERN = Pattern
            .compile("(?U)^[\\w&\\-\\.\\/\\(\\)\\'][\\w&\\-\\.\\/\\(\\)\\' ]*[\\w&\\-\\.\\/\\(\\)\\']?$");
    private static final Pattern VALID_NAME_PATTERN = VALID_TAG_PATTERN;
    private MeetingSchedule solution;
    private XSSFSheet currentSheet;
    private Iterator<Row> currentRowIterator;
    private int currentRowNumber;
    private int currentColumnNumber;
    private XSSFRow currentRow;

    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    @Override
    public MeetingSchedule read(File file) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            return readMeeting(workbook);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputScheduleFile ("
                    + file + ").", e);
        }
    }

    private MeetingSchedule readMeeting(XSSFWorkbook workbook) {
        solution = new MeetingSchedule();
        readDayList(workbook);
        readRoomList(workbook);
        readMeetingList(workbook);
        return solution;
    }

    private void readMeetingList(XSSFWorkbook workbook) {
        nextSheet(workbook, "Meetings");
        nextRow(false);
        readHeaderCell("Topic");
        readHeaderCell("Speaker");
        readHeaderCell("Attendees");
        List<Meeting> meetingList = new ArrayList<>(currentSheet.getLastRowNum() - 1);

        while (nextRow()) {
            Meeting meeting = new Meeting();
            meeting.setTopic(nextStringCell().getStringCellValue());
            meeting.setSpeaker(nextStringCell().getStringCellValue());
            meeting.setAttendees(nextStringCell().getStringCellValue());
            meetingList.add(meeting);
        }

        solution.setMeetingList(meetingList);
    }

    private void readRoomList(XSSFWorkbook workbook) {
        nextSheet(workbook, "Rooms");
        nextRow();
        readHeaderCell("Name");
        readHeaderCell("Capacity");
        List<Room> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);

        while (nextRow()) {
            String name = nextStringCell().getStringCellValue();
            if (!VALID_NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalStateException(
                        currentPosition() + ": The room name (" + name
                                + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
            }
            double capacityDouble = nextNumericCell().getNumericCellValue();
            if (capacityDouble <= 0 || capacityDouble != Math.floor(capacityDouble)) {
                throw new IllegalStateException(
                        currentPosition() + ": The room with name (" + name
                                + ") has a capacity (" + capacityDouble
                                + ") that isn't a strictly positive integer number.");
            }
            Room room = new Room(name, (int) capacityDouble);
            roomList.add(room);
        }
        solution.setRoomList(roomList);
    }

    protected XSSFCell nextNumericCell() {
        XSSFCell cell = nextCell();
        if (cell.getCellType() == CellType.STRING) {
            throw new IllegalStateException(currentPosition() + ": The cell with value ("
                    + cell.getStringCellValue() + ") has a string type but should be numeric.");
        }
        return cell;
    }

    private void readDayList(XSSFWorkbook workbook) {
        nextSheet(workbook, "Days");
        nextRow(false);
        readHeaderCell("Day");
        readHeaderCell("Start");
        readHeaderCell("End");
        List<Timeslot> timeslotList = new ArrayList<>(currentSheet.getLastRowNum() - 1);

        while (nextRow()) {

            DayOfWeek dayOfWeek = LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER).getDayOfWeek();
            LocalTime startTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
            LocalTime endTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
            LocalTime lunchHourStartTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);

            LocalTime startTimeslot = startTime;
            while (startTimeslot.isBefore(endTime)) {
                LocalTime endTimeslot = startTimeslot.plusMinutes(GRAIN_LENGTH_IN_MINUTES);
                if (!startTimeslot.equals(lunchHourStartTime)) {
                    Timeslot timeslot = new Timeslot(dayOfWeek, startTimeslot, endTimeslot);
                    timeslotList.add(timeslot);
                }
                startTimeslot = endTimeslot;
            }
        }
        solution.setTimeSlotList(timeslotList);
    }


    protected boolean nextRow() {
        return nextRow(true);
    }

    protected boolean nextRow(boolean skipEmptyRows) {
        currentRowNumber++;
        currentColumnNumber = -1;
        if (!currentRowIterator.hasNext()) {
            currentRow = null;
            return false;
        }
        currentRow = (XSSFRow) currentRowIterator.next();
        while (skipEmptyRows && currentRowIsEmpty()) {
            if (!currentRowIterator.hasNext()) {
                currentRow = null;
                return false;
            }
            currentRow = (XSSFRow) currentRowIterator.next();
        }
        if (currentRow.getRowNum() != currentRowNumber) {
            if (currentRow.getRowNum() == currentRowNumber + 1) {
                currentRowNumber++;
            } else {
                throw new IllegalStateException(currentPosition() + ": The next row (" + currentRow.getRowNum()
                        + ") has a gap of more than 1 empty line with the previous.");
            }
        }
        return true;
    }

    protected void readHeaderCell(String value) {
        XSSFCell cell = currentRow == null ? null : nextStringCell();
        if (cell == null || !cell.getStringCellValue().equals(value)) {
            throw new IllegalStateException(currentPosition() + ": The cell ("
                    + (cell == null ? null : cell.getStringCellValue())
                    + ") does not contain the expected value (" + value + ").");
        }
    }

    protected XSSFCell nextStringCell() {
        XSSFCell cell = nextCell();
        if (cell.getCellType() == CellType.NUMERIC) {
            throw new IllegalStateException(currentPosition() + ": The cell with value ("
                    + cell.getNumericCellValue() + ") has a numeric type but should be a string.");
        }
        return cell;
    }

    protected XSSFCell nextCell() {
        currentColumnNumber++;
        XSSFCell cell = currentRow.getCell(currentColumnNumber);
        // TODO HACK to workaround the fact that LibreOffice and Excel automatically remove empty trailing cells
        if (cell == null) {
            // Return dummy cell
            return currentRow.createCell(currentColumnNumber);
        }
        return cell;
    }

    @Override
    public void write(MeetingSchedule meetingSchedule, File file) {
        //TODO: create a writer to xlsx
    }

    protected String currentPosition() {
        return "Sheet (" + currentSheet.getSheetName() + ") cell ("
                + (currentRowNumber + 1) + CellReference.convertNumToColString(currentColumnNumber) + ")";
    }

    protected boolean currentRowIsEmpty() {
        if (currentRow.getPhysicalNumberOfCells() == 0) {
            return true;
        }
        for (Cell cell : currentRow) {
            if (cell.getCellType() == CellType.STRING) {
                if (!cell.getStringCellValue().isEmpty()) {
                    return false;
                }
            } else if (cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    protected void nextSheet(XSSFWorkbook workbook, String sheetName) {
        currentSheet = workbook.getSheet(sheetName);
        if (currentSheet == null) {
            throw new IllegalStateException("The workbook does not contain a sheet with name ("
                    + sheetName + ").");
        }
        currentRowIterator = currentSheet.rowIterator();
        if (currentRowIterator == null) {
            throw new IllegalStateException(currentPosition() + ": The sheet has no rows.");
        }
        currentRowNumber = -1;
    }
}
