package org.kie.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningEntity
@Entity
public class Meeting {

    @Id
    @GeneratedValue
    @PlanningId
    protected Long id;
    private String topic;
    private String speaker;
    private String attendees;
    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    @ManyToOne
    private Timeslot timeslot;
    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    @ManyToOne
    private Room room;

    // No-arg constructor required for Hibernate and OptaPlanner
    public Meeting() {
    }

    public Meeting(String topic, String speaker, String attendance) {
        this.topic = topic;
        this.speaker = speaker;
        this.attendees = attendance;
    }

    public Meeting(Long id, String topic, String speaker, String attendees, Timeslot timeslot, Room room) {
        this.id = id;
        this.topic = topic;
        this.speaker = speaker;
        this.attendees = attendees;
        this.timeslot = timeslot;
        this.room = room;
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

    @JsonIgnore
    public List<String> getAttendeesList() {
        return List.of(attendees.split(", "));
    }

    public Long getId() {
        return id;
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
}
