package org.kie.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.kie.SessionController;
import org.kie.bootstrap.DemoDataGenerator;
import org.kie.domain.Meeting;
import org.kie.domain.MeetingSchedule;
import org.kie.persistence.MeetingRepository;
import org.kie.persistence.RoomRepository;
import org.kie.persistence.TimeslotRepository;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;

import io.quarkus.panache.common.Sort;

import java.util.List;

@Path("schedule")
public class MeetingScheduleResource {
    @Inject
    TimeslotRepository timeslotRepository;
    @Inject
    RoomRepository roomRepository;
    @Inject
    MeetingRepository meetingRepository;
    @Inject
    SessionController sessionController;
    @Inject
    DemoDataGenerator demoDataGenerator;
    @Inject
    SolverManager<MeetingSchedule, String> solverManager;
    @Inject
    ScoreManager<MeetingSchedule, HardSoftScore> scoreManager;

    @Inject
    MeetingResource meetingResource;

    @GET
    public MeetingSchedule getMeetingSchedule(@CookieParam("JSESSIONID") String sessionId) {
        if (sessionId == null) {
            throw new IllegalStateException("Undefined Session Id");
        }
        sessionController.setSessionId(sessionId);//Session controller initialisation
        SolverStatus solverStatus = getSolverStatus(sessionId);
        MeetingSchedule solution = findBySessionId(sessionId);
        scoreManager.updateScore(solution);
        solution.setSolverStatus(solverStatus);
        return solution;
    }

    @GET
    @Path("share")
    public MeetingSchedule share(@CookieParam("JSESSIONID") String sessionId){
        return new MeetingSchedule(
                meetingRepository.findShared(sessionId),
                roomRepository.findShared(sessionId),
                timeslotRepository.findShared(sessionId));
    }

    @POST
    @Path("upload")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public void uploadMeetingSchedule(String scheduleJson, @CookieParam("JSESSIONID") String sessionId) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        MeetingSchedule meetingSchedule = objectMapper.readValue(scheduleJson, MeetingSchedule.class);

        meetingSchedule.getMeetingList()
                .forEach(meeting -> meeting.setSessionId(sessionId));

        meetingSchedule.getTimeSlotList()
                .forEach(timeslot -> timeslot.setSessionId(sessionId));

        meetingSchedule.getRoomList()
                        .forEach(room -> room.setSessionId(sessionId));

        meetingRepository.persist(meetingSchedule.getMeetingList());
        timeslotRepository.persist(meetingSchedule.getTimeSlotList());
        roomRepository.persist(meetingSchedule.getRoomList());
    }

    @POST
    @Path("session")
    public void sessionStart() {
        sessionController.init();
    }

    @POST
    @Path("solve")
    public void solve(@CookieParam("JSESSIONID") String sessionId) {
        solverManager.solveAndListen(sessionId,
                this::findBySessionId,
                this::save);
    }

    @Transactional
    protected MeetingSchedule findBySessionId(String sessionId) {
        return new MeetingSchedule(
                meetingRepository.list("sessionId", Sort.by("id"), sessionId),
                roomRepository.list("sessionId", Sort.by("name").and("id"), sessionId),
                timeslotRepository.list("sessionId", Sort.by("dayOfWeek").and("startTime").and("endTime").and("id"), sessionId));
    }

    @Transactional
    protected void save(MeetingSchedule meetingSchedule) {
        for (Meeting meeting : meetingSchedule.getMeetingList()) {
            // TODO this is awfully naive: optimistic locking causes issues if called by the SolverManager
            Meeting attachedMeeting = meetingRepository.findById(meeting.getId());
            if (attachedMeeting == null) {
                throw new RuntimeException("No meeting found with id: " + meeting.getId());
            }
            attachedMeeting.setTimeslot(meeting.getTimeslot());
            attachedMeeting.setRoom(meeting.getRoom());
        }
    }

    public SolverStatus getSolverStatus(String sessionId) {
        return solverManager.getSolverStatus(sessionId);
    }

    @POST
    @Path("stopSolving")
    public void stopSolving(@CookieParam("JSESSIONID") String sessionId) {
        solverManager.terminateEarly(sessionId);
    }

    @POST
    @Path("reset")
    @Transactional
    public void reset(@CookieParam("JSESSIONID") String sessionId){
        sessionController.removeSessionDataById(sessionId);
    }

    @DELETE
    @Path("timeslot/{id}")
    @Transactional
    public void deleteScheduledTimeslots(@PathParam("id") Long id){
        meetingRepository.update("room_id = null where timeslot_id = ?1", id);
        meetingRepository.update("timeslot_id = null where timeslot_id = ?1", id);
        timeslotRepository.deleteById(id);
    }

    @GET
    @Path("timeslot/{id}")
    public List<Meeting> getMeetingsAssignedToTimeslots(@PathParam("id") Long id){
        return meetingRepository.list("timeslot_id", id);
    }

    @DELETE
    @Path("room/{id}")
    @Transactional
    public void deleteScheduledRooms(@PathParam("id") Long id){
        meetingRepository.update("room_id = null where room_id = ?1", id);
        meetingRepository.update("timeslot_id = null where room_id = ?1", id);
        roomRepository.deleteById(id);
    }

    @GET
    @Path("room/{id}")
    public List<Meeting> getMeetingsAssignedToRooms(@PathParam("id") Long id){
        return meetingRepository.list("room_id", id);
    }
}
