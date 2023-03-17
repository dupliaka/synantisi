package org.kie.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.panache.common.Sort;

@Path("schedule")
public class MeetingScheduleResource {

    public static final Long SINGLETON_TIME_TABLE_ID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MeetingScheduleResource.class);
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

    @GET

    public MeetingSchedule getMeetingSchedule(@CookieParam("JSESSIONID") String sessionId) {
        sessionController.setSessionId(sessionId);//Session controller initialisation
        SolverStatus solverStatus = getSolverStatus(sessionId);
        MeetingSchedule solution = findById(sessionId);
        scoreManager.updateScore(solution);
        solution.setSolverStatus(solverStatus);
        return solution;
    }

    @POST
    @Path("demo")

    public void uploadDemoData(@CookieParam("JSESSIONID") String sessionId) {
        LOGGER.info("Session " + sessionId);

        if (meetingRepository.findBySessionId(sessionId).isEmpty()) {
            demoDataGenerator.generateDemoData(sessionId);
        }
    }

    @POST
    @Path("solve")

    public void solve(@CookieParam("JSESSIONID") String sessionId) {
        solverManager.solveAndListen(sessionId,
                this::findById,
                this::save);
    }

    @Transactional
    protected MeetingSchedule findById(String sessionId) {
        return new MeetingSchedule(
                meetingRepository.list("sessionId", Sort.by("id"), sessionId),
                timeslotRepository.list("sessionId", Sort.by("dayOfWeek").and("startTime").and("endTime").and("id"), sessionId),
                roomRepository.list("sessionId", Sort.by("name").and("id"), sessionId));
    }

    @Transactional
    protected void save(MeetingSchedule meetingSchedule) {
        for (Meeting meeting : meetingSchedule.getMeetingList()) {
            // TODO this is awfully naive: optimistic locking causes issues if called by the SolverManager
            Meeting attachedMeeting = meetingRepository.findById(meeting.getId());
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
}
