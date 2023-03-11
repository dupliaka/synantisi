package org.kie.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.solver.SolverStatus;

@PlanningSolution
public class MeetingSchedule {
    @PlanningEntityCollectionProperty
    private List<Meeting> meetingList;
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeslotRange")
    private List<Timeslot> timeslotList;
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "roomRange")
    private List<Room> roomList;
    @PlanningScore
    private HardMediumSoftScore score;

    private SolverStatus solverStatus;

    public MeetingSchedule() {
    }

    public MeetingSchedule(List<Meeting> meetingList, List<Timeslot> timeslotList, List<Room> roomList) {
        this.meetingList = meetingList;
        this.timeslotList = timeslotList;
        this.roomList = roomList;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************
    public SolverStatus getSolverStatus() {
        return solverStatus;
    }

    public void setSolverStatus(SolverStatus solverStatus) {
        this.solverStatus = solverStatus;
    }

    public List<Meeting> getMeetingList() {
        return meetingList;
    }

    public void setMeetingList(List<Meeting> meetingList) {
        this.meetingList = meetingList;
    }

    public List<Timeslot> getTimeGrainList() {
        return timeslotList;
    }

    public void setTimeGrainList(List<Timeslot> timeslotList) {
        this.timeslotList = timeslotList;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }

}
