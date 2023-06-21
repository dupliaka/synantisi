package org.kie.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.solver.SolverStatus;

@PlanningSolution
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingSchedule {
    @PlanningEntityCollectionProperty
    private List<Meeting> meetingList;
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "roomRange")
    private List<Room> roomList;
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeslotRange")
    private List<Timeslot> timeslotList;
    @PlanningScore
    private HardMediumSoftScore score;
    private SolverStatus solverStatus;

    public MeetingSchedule() {
    }

    public MeetingSchedule(List<Meeting> meetingList, List<Room> roomList, List<Timeslot> timeslotList) {
        this.meetingList = meetingList;
        this.roomList = roomList;
        this.timeslotList = timeslotList;
    }

    public MeetingSchedule(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        MeetingSchedule meetingSchedule = objectMapper.readValue(jsonString, MeetingSchedule.class);
        this.meetingList = meetingSchedule.getMeetingList();
        this.roomList = meetingSchedule.getRoomList();
        this.timeslotList = meetingSchedule.getTimeSlotList();
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

    public List<Timeslot> getTimeSlotList() {
        return timeslotList;
    }

    public void setTimeSlotList(List<Timeslot> timeslotList) {
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
