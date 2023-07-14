package org.kie.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningEntity
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meeting {

    @Id
    @GeneratedValue
    @PlanningId
    protected Long id;
    private String topic;
    private String speaker;
    private String attendees;
    private Long priority;
    private String sessionId;
    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    @ManyToOne
    private Timeslot timeslot;
    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    @ManyToOne
    private Room room;

    // No-arg constructor required for Hibernate and OptaPlanner
    public Meeting() {
    }

    public Meeting(Long id, String topic, String speaker, String attendees, String sessionId, Long priority, Timeslot timeslot,
            Room room) {
        this.id = id;
        this.topic = topic;
        this.speaker = speaker;
        this.attendees = attendees;
        this.sessionId = sessionId;
        this.timeslot = timeslot;
        this.room = room;
        this.priority = priority;
    }

    public Meeting(Meeting other) {
        this.topic = other.topic;
        this.speaker = other.speaker;
        this.attendees = other.attendees;
        this.priority = other.priority;
    }

    @JsonIgnore
    public List<String> getAttendeesList() {
        return List.of(attendees.split(", "));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }
}
