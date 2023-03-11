package org.kie.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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

@Path("schedule")
public class MeetingScheduleResource {
    public static final Long SINGLETON_TIME_TABLE_ID = 1L;

    @Inject
    TimeslotRepository timeslotRepository;
    @Inject
    RoomRepository roomRepository;
    @Inject
    MeetingRepository meetingRepository;

    @Inject
    DemoDataGenerator demoDataGenerator;
    @Inject
    SolverManager<MeetingSchedule, Long> solverManager;
    @Inject
    ScoreManager<MeetingSchedule, HardSoftScore> scoreManager;

    @GET
    public MeetingSchedule getMeetingSchedule() {
        // Get the solver status before loading the solution
        // to avoid the race condition that the solver terminates between them
        SolverStatus solverStatus = getSolverStatus();
        MeetingSchedule solution = findById(SINGLETON_TIME_TABLE_ID);
        scoreManager.updateScore(solution); // Sets the score
        solution.setSolverStatus(solverStatus);
        return solution;
    }

    @POST
    @Path("demo")
    public void uploadDemoData() {
        demoDataGenerator.generateDemoData();
    }

    @POST
    @Path("solve")
    public void solve() {
        solverManager.solveAndListen(SINGLETON_TIME_TABLE_ID,
                this::findById,
                this::save);
    }

    @Transactional
    protected MeetingSchedule findById(Long id) {
        if (!SINGLETON_TIME_TABLE_ID.equals(id)) {
            throw new IllegalStateException("There is no timeTable with id (" + id + ").");
        }
        // Occurs in a single transaction, so each initialized meeting references the same timeslot/room instance
        // that is contained by the timeTable's timeslotList/roomList.
        return new MeetingSchedule(
                meetingRepository.listAll(Sort.by("id")),
                timeslotRepository.listAll(Sort.by("dayOfWeek").and("startTime").and("endTime").and("id")),
                roomRepository.listAll(Sort.by("name").and("id")));
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

    public SolverStatus getSolverStatus() {
        return solverManager.getSolverStatus(SINGLETON_TIME_TABLE_ID);
    }

    @POST
    @Path("stopSolving")
    public void stopSolving() {
        solverManager.terminateEarly(SINGLETON_TIME_TABLE_ID);
    }
}
