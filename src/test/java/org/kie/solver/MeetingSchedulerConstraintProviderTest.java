package org.kie.solver;

import java.time.DayOfWeek;
import java.time.LocalTime;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.kie.domain.Meeting;
import org.kie.domain.MeetingSchedule;
import org.kie.domain.Room;
import org.kie.domain.Timeslot;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class MeetingSchedulerConstraintProviderTest {

    private static final Room ROOM1 = new Room(1L, "Room1");
    private static final Room ROOM2 = new Room(2L, "Room2");
    private static final Timeslot TIMESLOT1 = new Timeslot(1L, DayOfWeek.MONDAY, LocalTime.NOON, LocalTime.NOON.plusHours(1));
    private static final Timeslot TIMESLOT2 = new Timeslot(2L, DayOfWeek.TUESDAY, LocalTime.NOON,  LocalTime.NOON.plusHours(1));
    private static final Timeslot TIMESLOT3 = new Timeslot(3L, DayOfWeek.MONDAY, LocalTime.NOON.plusHours(5),  LocalTime.NOON.plusHours(6));

    @Inject
    ConstraintVerifier<MeetingScheduleConstraintProvider, MeetingSchedule> constraintVerifier;

    @Test
    void roomConflict() {
        Meeting firstmeeting = new Meeting(1L, "Subject1", "Speaker1", "Attendees1", "sessionid1",0L, TIMESLOT1, ROOM1);
        Meeting conflictingmeeting = new Meeting(2L, "Subject2", "Speaker2", "Attendees2", "sessionid1",0L, TIMESLOT1, ROOM1);
        Meeting nonConflictingMeeting = new Meeting(3L, "Subject3", "Speaker3", "Attendees3", "sessionid1",0L, TIMESLOT2, ROOM1);
        constraintVerifier.verifyThat(MeetingScheduleConstraintProvider::roomConflict)
                .given(firstmeeting, conflictingmeeting, nonConflictingMeeting)
                .penalizesBy(1);
    }

    @Test
    void speakerConflict() {
        String conflictingSpeaker = "Speaker1";
        Meeting firstMeeting = new Meeting(1L, "Subject1", conflictingSpeaker, "Attendees1", "sessionid1",0L,  TIMESLOT1, ROOM1);
        Meeting conflictingMeeting = new Meeting(2L, "Subject2", conflictingSpeaker, "Attendees2", "sessionid1",0L, TIMESLOT1, ROOM2);
        Meeting nonConflictingMeeting = new Meeting(3L, "Subject3", "Speaker2", "Attendees3", "sessionid1",0L, TIMESLOT2, ROOM1);
        constraintVerifier.verifyThat(MeetingScheduleConstraintProvider::speakerConflict)
                .given(firstMeeting, conflictingMeeting, nonConflictingMeeting)
                .penalizesBy(1);
    }

    @Test
    void attendanceConflict() {
        String attendees = "Attendees1";
        Meeting firstMeeting = new Meeting(1L, "Subject1", "Speaker1", attendees, "sessionid1",0L, TIMESLOT1, ROOM1);
        Meeting conflictingMeeting = new Meeting(2L, "Subject2", "Speaker2", attendees, "sessionid1",0L, TIMESLOT1, ROOM2);
        Meeting nonConflictingMeeting = new Meeting(3L, "Subject3", "Speaker3", "Attendees2", "sessionid1",0L, TIMESLOT2, ROOM1);
        constraintVerifier.verifyThat(MeetingScheduleConstraintProvider::attendanceConflict)
                .given(firstMeeting, conflictingMeeting, nonConflictingMeeting)
                .penalizesBy(1);
    }

    @Test
    void prioritizedMeetingsFirst(){
        Meeting firstMeeting = new Meeting(1L, "Subject1", "Speaker1", "Attendees1", "sessionid1",2L, TIMESLOT1, ROOM1);
        Meeting conflictingMeeting = new Meeting(2L, "Subject2", "Speaker2", "Attendees2", "sessionid1",3L, TIMESLOT1, ROOM2);
        Meeting nonConflictingMeeting = new Meeting(3L, "Subject3", "Speaker3", "Attendees3", "sessionid1",0L, TIMESLOT2, ROOM1);
        Meeting nonConflictingMeeting2 = new Meeting(4L, "Subject4", "Speaker4", "Attendees4", "sessionid1",1L, TIMESLOT3, ROOM2);
        constraintVerifier.verifyThat(MeetingScheduleConstraintProvider::prioritizedMeetingsFirst)
                .given(firstMeeting, conflictingMeeting, nonConflictingMeeting,nonConflictingMeeting2)
                .penalizesBy(0);
    }
    @Test
    void openingTimeConflict(){
        Meeting nonConflictingMeeting = new Meeting(1L, "Subject1", "Speaker1", "Attendees1", "sessionid1",0L, TIMESLOT1, ROOM1);
        Meeting conflictingMeeting = new Meeting(2L, "Subject2", "Speaker2", "Attendees2", "sessionid1",0L, TIMESLOT3, ROOM1);
        constraintVerifier.verifyThat(MeetingScheduleConstraintProvider::openingTime)
                .given(conflictingMeeting, nonConflictingMeeting)
                .penalizesBy(29);
    }
}
