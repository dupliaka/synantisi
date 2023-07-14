package org.kie.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Objects;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.kie.domain.Meeting;
import org.kie.domain.MeetingSchedule;
import org.optaplanner.core.api.solver.SolverStatus;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MeetingScheduleResourceTest {

    public static final String JSESSIONID = "JSESSIONID";
    public static final String SESSION_KEY = "dCWkP1qnh0mR7-SDXT0KLeGWkB6ai0dShg2B_IfI";

    private static MeetingSchedule getSchedule() {
        return given()
                .cookie(JSESSIONID, SESSION_KEY)
                .when()
                .get("/schedule")
                .then()
                .statusCode(200)
                .extract().body().as(MeetingSchedule.class);
    }

    private static void resetSchedule() {
        given()
                .cookie(JSESSIONID, SESSION_KEY)
                .when()
                .post("/schedule/reset")
                .then()
                .statusCode(204);
    }

    @Test
    void getMeetingSchedule() {
        MeetingSchedule meetingSchedule = getSchedule();

        assertFalse(meetingSchedule.getMeetingList().isEmpty());
        assertFalse(meetingSchedule.getTimeSlotList().isEmpty());
        assertFalse(meetingSchedule.getRoomList().isEmpty());
        assertFalse(meetingSchedule.getScore().isZero());

        assertEquals("E.Mask, M.Zuckerberg", meetingSchedule.getMeetingList().get(0).getAttendees());
        assertEquals("L.Torvalds", meetingSchedule.getMeetingList().get(0).getSpeaker());
        assertEquals("Code like a Boss", meetingSchedule.getMeetingList().get(0).getTopic());

        assertEquals("Room A", meetingSchedule.getRoomList().get(0).getName());

        assertEquals("MONDAY", meetingSchedule.getTimeSlotList().get(0).getDayOfWeek().name());
        assertEquals("09:30", meetingSchedule.getTimeSlotList().get(0).getEndTime().toString());
        assertEquals("08:30", meetingSchedule.getTimeSlotList().get(0).getStartTime().toString());

        resetSchedule();
        assertTrue(getSchedule().getMeetingList().isEmpty());
    }

    @Test
    public void solve() throws InterruptedException {

        given()
                .cookie(JSESSIONID, SESSION_KEY)
                .when()
                .post("/schedule/solve")
                .then()
                .statusCode(204);

        //the solving time is set in properties and usually gets interrupted by the user
        MeetingSchedule meetingSchedule;
        do { // Use do-while to give the solver some time and avoid retrieving an early infeasible solution.
             // Quick polling (not a Test Thread Sleep anti-pattern)
             // Test is still fast on fast machines and doesn't randomly fail on slow machines.
            Thread.sleep(20L);
            meetingSchedule = getSchedule();
        } while (meetingSchedule.getSolverStatus() != SolverStatus.NOT_SOLVING || !meetingSchedule.getScore().isFeasible());

        given()
                .cookie(JSESSIONID, SESSION_KEY)
                .when()
                .post("/schedule/stopSolving")
                .then()
                .statusCode(204);

        MeetingSchedule schedule = getSchedule();

        assertTrue(schedule.getScore().isFeasible());
        assertFalse(schedule.getMeetingList().isEmpty());
        //check solution: assert that all the meetings have been assigned to timeslot and room
        assertAssignment(schedule, "Code like a Boss", "MONDAY 09:30", "Room A");
        assertAssignment(schedule, "The future of Open Source Infrastructure", "MONDAY 08:30", "Room A");
        assertAssignment(schedule, "Geek out with Open Source", "MONDAY 08:30", "Room B");
        assertAssignment(schedule, "DevOps party", "MONDAY 09:30", "Room B");
        assertMeetingsAssignedToTimeslots();
        assertMeetingsAssignedToRooms();
        deleteScheduledRooms();
        deleteScheduledTimeslots();
    }

    private void assertAssignment(MeetingSchedule schedule, String meetingName, String timeslot, String room) {
        Meeting meeting =
                schedule.getMeetingList().stream().filter(ml -> Objects.equals(ml.getTopic(), meetingName)).findFirst().get();

        assertEquals(meeting.getTimeslot().toString(), timeslot);
        assertEquals(meeting.getRoom().toString(), room);
    }

    @Test
    void testGetMeetingSchedule() {
        assertThrows(IllegalStateException.class, () -> (new MeetingScheduleResource()).getMeetingSchedule(null));
    }

    void deleteScheduledTimeslots() {
        final int timeslotId = 6;

        given()
                .cookie(JSESSIONID, SESSION_KEY)
                .when()
                .delete("/schedule/timeslot/" + timeslotId)
                .then()
                .statusCode(204);

        MeetingSchedule ms = getSchedule();
        assertTrue(ms.getMeetingList().stream().filter(m -> m.getTimeslot() != null)
                .noneMatch(m -> m.getTimeslot().getId() == timeslotId));
        assertTrue(ms.getMeetingList().stream().filter(m -> m.getTimeslot() == null).allMatch(m -> m.getRoom() == null));

    }

    void assertMeetingsAssignedToTimeslots() throws InterruptedException {
        final int timeslotIDExpected = 6;
        Meeting[] response =
                given()
                        .cookie(JSESSIONID, SESSION_KEY)
                        .when()
                        .get("/schedule/timeslot/" + timeslotIDExpected)
                        .as(Meeting[].class);

        assertNotEquals(response.length, 0);
        for (Meeting meeting : response) {
            assertEquals(meeting.getTimeslot().getId(), timeslotIDExpected);
        }
    }

    void deleteScheduledRooms() {
        final int roomId = 5;
        given()
                .cookie(JSESSIONID, SESSION_KEY)
                .when()
                .delete("/schedule/room/" + roomId)
                .then()
                .statusCode(204);

        MeetingSchedule ms = getSchedule();
        assertTrue(ms.getMeetingList().stream().filter(m -> m.getRoom() != null).noneMatch(m -> m.getRoom().getId() == roomId));
        assertTrue(ms.getMeetingList().stream().filter(m -> m.getRoom() == null).allMatch(m -> m.getTimeslot() == null));

    }

    void assertMeetingsAssignedToRooms() {
        final int roomIDExpected = 4;
        Meeting[] response =
                given()
                        .cookie(JSESSIONID, SESSION_KEY)
                        .when()
                        .get("/schedule/room/" + roomIDExpected)
                        .as(Meeting[].class);

        assertNotEquals(response.length, 0);
        for (Meeting meeting : response) {
            assertEquals(meeting.getRoom().getId(), roomIDExpected);
        }
    }

    @Test
    public void testUploadMeetingSchedule() {
        resetSchedule();

        String scheduleJson =
                "{\"meetingList\":[{\"topic\":\"Binary Bloopers: Laughing at Programming Fails\",\"speaker\":\"J. Carmack\",\"attendees\":\"Sam Altman\",\"priority\":0}],\"roomList\":[{\"name\":\"Room D\"}],\"timeSlotList\":[]}";

        // Perform the upload
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(scheduleJson)
                .cookie(JSESSIONID, SESSION_KEY)
                .when()
                .post("/schedule/upload")
                .then()
                .statusCode(204);

        MeetingSchedule meetingSchedule = getSchedule();
        assertEquals(1, meetingSchedule.getMeetingList().size());
        assertTrue(meetingSchedule.getMeetingList().stream()
                .anyMatch(m -> Objects.equals(m.getTopic(), "Binary Bloopers: Laughing at Programming Fails")));
        assertTrue(meetingSchedule.getRoomList().stream().anyMatch(m -> Objects.equals(m.getName(), "Room D")));
        assertTrue(meetingSchedule.getTimeSlotList().stream().noneMatch(m -> Objects.equals(m.toString(), "WEDNESDAY 08:30")));

        String sharedJson = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(scheduleJson)
                .cookie(JSESSIONID, SESSION_KEY)
                .when()
                .get("/schedule/share")
                .asString();

        assertEquals(scheduleJson, sharedJson);
    }
}