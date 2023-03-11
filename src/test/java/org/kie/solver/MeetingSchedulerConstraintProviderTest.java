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

@QuarkusTest
public class MeetingSchedulerConstraintProviderTest {

    private static final Room ROOM1 = new Room(1L, "Room1");
    private static final Room ROOM2 = new Room(2L, "Room2");
    private static final Timeslot TIMESLOT1 = new Timeslot(1L, DayOfWeek.MONDAY, LocalTime.NOON);
    private static final Timeslot TIMESLOT2 = new Timeslot(2L, DayOfWeek.TUESDAY, LocalTime.NOON);

    @Inject
    ConstraintVerifier<MeetingScheduleConstraintProvider, MeetingSchedule> constraintVerifier;

    @Test
    void roomConflict() {
        Meeting firstmeeting = new Meeting(1L, "Subject1", "Speaker1", "Attendees1", TIMESLOT1, ROOM1);
        Meeting conflictingmeeting = new Meeting(2L, "Subject2", "Speaker2", "Attendees2", TIMESLOT1, ROOM1);
        Meeting nonConflictingMeeting = new Meeting(3L, "Subject3", "Speaker3", "Attendees3", TIMESLOT2, ROOM1);
        constraintVerifier.verifyThat(MeetingScheduleConstraintProvider::roomConflict)
                .given(firstmeeting, conflictingmeeting, nonConflictingMeeting)
                .penalizesBy(1);
    }

    @Test
    void speakerConflict() {
        String conflictingSpeaker = "Speaker1";
        Meeting firstMeeting = new Meeting(1L, "Subject1", conflictingSpeaker, "Attendees1", TIMESLOT1, ROOM1);
        Meeting conflictingMeeting = new Meeting(2L, "Subject2", conflictingSpeaker, "Attendees2", TIMESLOT1, ROOM2);
        Meeting nonConflictingMeeting = new Meeting(3L, "Subject3", "Speaker2", "Attendees3", TIMESLOT2, ROOM1);
        constraintVerifier.verifyThat(MeetingScheduleConstraintProvider::speakerConflict)
                .given(firstMeeting, conflictingMeeting, nonConflictingMeeting)
                .penalizesBy(1);
    }

    @Test
    void attendanceConflict() {
        String attendees = "Attendees1";
        Meeting firstMeeting = new Meeting(1L, "Subject1", "Speaker1", attendees, TIMESLOT1, ROOM1);
        Meeting conflictingMeeting = new Meeting(2L, "Subject2", "Speaker2", attendees, TIMESLOT1, ROOM2);
        Meeting nonConflictingMeeting = new Meeting(3L, "Subject3", "Speaker3", "Attendees2", TIMESLOT2, ROOM1);
        constraintVerifier.verifyThat(MeetingScheduleConstraintProvider::attendanceConflict)
                .given(firstMeeting, conflictingMeeting, nonConflictingMeeting)
                .penalizesBy(1);
    }
}
