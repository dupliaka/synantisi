package org.kie.bootstrap;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.domain.Meeting;
import org.kie.domain.Room;
import org.kie.domain.Timeslot;
import org.kie.persistence.MeetingRepository;
import org.kie.persistence.RoomRepository;
import org.kie.persistence.TimeslotRepository;

@SessionScoped
public class DemoDataGenerator {
    private final List<DayOfWeek> DAY_OF_WEEK_LIST = List.of(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY);
    private final List<LocalTime> START_TIME_LIST = List.of(
            LocalTime.of(8, 30),
            LocalTime.of(9, 30),
            LocalTime.of(10, 30),
            LocalTime.of(13, 30),
            LocalTime.of(14, 30),
            LocalTime.of(11, 30),
            LocalTime.of(15, 30),
            LocalTime.of(16, 30));
    private final List<Meeting> MEETING_LIST = List.of(
            new Meeting("Code like a Boss", "L.Torvalds", "E.Mask, M.Zuckerberg"),
            new Meeting("Geek out with Open Source", "R.Stallman", "J.Bezos"),
            new Meeting("The future of Open Source Infrastructure", "S.Hykes", "T.Hicks, P.Cormier"),
            new Meeting("DevOps party", "R.Stallman", "T.Hicks, T.Cook"));
    @Inject
    TimeslotRepository timeslotRepository;
    @Inject
    RoomRepository roomRepository;
    @Inject
    MeetingRepository meetingRepository;
    @ConfigProperty(name = "demo-data.meeting-count", defaultValue = "4")
    int meetingCount;

    @Transactional
    public void generateDemoData(String sessionId) {

        if (meetingCount == 0) {
            return;
        }
        int idealTimeslotCount = Math.min(DAY_OF_WEEK_LIST.size() * START_TIME_LIST.size(),
                Math.max(2, meetingCount / 2));
        int startTimeCount = Math.max(Math.min(5, idealTimeslotCount), idealTimeslotCount / DAY_OF_WEEK_LIST.size());
        int dayOfWeekCount = Math.max(1, (idealTimeslotCount + startTimeCount - 1) / startTimeCount);
        int timeslotCount = dayOfWeekCount * startTimeCount;
        // The minimum needed rooms + 10% (rounded down) + 1
        int roomCount = meetingCount == 4 ? 2 : (((meetingCount + timeslotCount - 1) / timeslotCount) * 11 / 10) + 1;

        List<Timeslot> timeslotList = new ArrayList<>(timeslotCount);
        for (DayOfWeek dayOfWeek : DAY_OF_WEEK_LIST.subList(0, dayOfWeekCount)) {
            for (LocalTime startTime : START_TIME_LIST.subList(0, startTimeCount)) {
                LocalTime endTime = startTime.plusHours(1);
                timeslotList.add(new Timeslot(dayOfWeek, startTime, endTime, sessionId));
            }
        }
        timeslotRepository.persist(timeslotList);

        List<Room> roomList = new ArrayList<>(roomCount);
        for (int i = 0; i < roomCount; i++) {
            // Few rooms: A, B, C, ... - Many rooms: AA, AB, AC, ...
            String name = "Room " + Character.toString(roomCount <= 26 ? ('A' + i) : ('A' + (i / 26)) + ('A' + (i % 26)));
            roomList.add(new Room(name, sessionId));
        }
        roomRepository.persist(roomList);

        List<Meeting> meetingList = MEETING_LIST.subList(0, meetingCount);
        meetingList.forEach(s->s.setSessionId(sessionId));

        meetingRepository. persist(MEETING_LIST);
    }
}
