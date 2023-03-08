package org.kie.rest;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.kie.domain.MeetingAssignment;
import org.kie.domain.MeetingSchedule;
import org.kie.persistence.MeetingAssignmentRepository;
import org.kie.persistence.MeetingSchedulingXlsxFileIO;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("meetingSchedule")
public class MeetingScheduleResource {
    public static final Long SINGLETON_TIME_TABLE_ID = 1L;

    @Inject
    MeetingAssignmentRepository meetingAssignmentRepository;

//    @Inject
//    SolverManager<MeetingSchedule, Long> solverManager;
//
//    @Inject
//    ScoreManager<MeetingSchedule, HardSoftScore> scoreManager;

    @POST
    public void upload(@RequestBody byte[] body) {
        MeetingSchedulingXlsxFileIO meetingSchedulingXlsxFileIO = new MeetingSchedulingXlsxFileIO();
        MeetingSchedule unsolved = meetingSchedulingXlsxFileIO.readFromByteArray(body);
        unsolved.setId(SINGLETON_TIME_TABLE_ID);
        save(unsolved);
    }

    @Transactional
    protected void save(MeetingSchedule meetingSchedule) {
        for (MeetingAssignment assignment : meetingSchedule.getMeetingAssignmentList()) {
            // TODO this is awfully naive: optimistic locking causes issues if called by the SolverManager
            MeetingAssignment meetingAssignment = meetingAssignmentRepository.findById(assignment.getId());
            meetingAssignment.setMeeting(assignment.getMeeting());
            meetingAssignment.setRoom(assignment.getRoom());
            meetingAssignment.setPinned(assignment.isPinned());
            meetingAssignment.setStartingTimeGrain(assignment.getStartingTimeGrain());
        }
    }
}
