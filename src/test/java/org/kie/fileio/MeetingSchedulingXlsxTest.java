package org.kie.fileio;

import org.junit.jupiter.api.Test;
import org.kie.domain.Meeting;
import org.kie.domain.MeetingSchedule;
import org.kie.domain.Room;
import org.kie.domain.Timeslot;

import javax.inject.Inject;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeetingSchedulingXlsxTest {

    String xlsxPath = "src/test/resources/4meetings-5timegrains-2rooms.xlsx";

    @Test
    public void TimeSlotCheck(){
        MeetingSchedulingXlsx meetingSchedulingXlsx = new MeetingSchedulingXlsx();
        MeetingSchedule meetingSchedule = meetingSchedulingXlsx.read(new File(xlsxPath));

        List<Timeslot> timeslots = meetingSchedule.getTimeSlotList();

        assertEquals(9, timeslots.size());
        assertTrue(timeslots.stream().allMatch(s -> s.getDayOfWeek().compareTo(DayOfWeek.SATURDAY) == 0));
        assertTrue(timeslots.stream().noneMatch(s->s.getStartTime().compareTo(LocalTime.of(12,00))==0));
        assertEquals(0, timeslots.get(0).getStartTime().compareTo(LocalTime.of(8, 00)));
        assertEquals(0, timeslots.get(8).getEndTime().compareTo(LocalTime.of(18, 00)));


        List<Room> rooms = meetingSchedule.getRoomList();
        assertEquals(2,rooms.size());
        assertEquals("R 1",rooms.get(0).getName());
        assertEquals(30,rooms.get(0).getCapacity());

        List<Meeting> meetings = meetingSchedule.getMeetingList();

        assertEquals(4, meetings.size());
        assertEquals("Code Like a Boss", meetings.get(0).getTopic());
        assertEquals("L. Torvalds", meetings.get(0).getSpeaker());
        assertEquals("E. Mask, M. Zuckerberg", meetings.get(0).getAttendees());

    }

}