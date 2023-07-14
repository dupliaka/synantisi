package org.kie.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.kie.domain.Meeting;
import org.kie.domain.MeetingSchedule;
import io.quarkus.test.junit.QuarkusTest;
import org.optaplanner.core.api.solver.SolverStatus;

@QuarkusTest
class MeetingScheduleResourceTest {

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
    void solve() throws InterruptedException {

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
        for (Meeting meeting : schedule.getMeetingList()) {
            assertNotNull(meeting.getTimeslot());
            assertNotNull(meeting.getRoom());
        }
    }

}