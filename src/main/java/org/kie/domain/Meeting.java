package org.kie.domain;


import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
@Entity
public class Meeting {

    @PlanningId
    @Id
    @GeneratedValue
    protected Long id;
    private String topic;
    private List<Person> speakerList;
    private String content;
    private boolean entireGroupMeeting;
    /**
     * Multiply by {@link TimeGrain#GRAIN_LENGTH_IN_MINUTES} to get duration in minutes.
     */
    private int durationInGrains;

    private List<RequiredAttendance> requiredAttendanceList;
    private List<PreferredAttendance> preferredAttendanceList;

    public Meeting() {
    }

    public Meeting(long id) {
        this.id = id;
    }

    public Meeting(long id, String topic, int durationInGrains) {
        this(id);
        this.topic = topic;
        this.durationInGrains = durationInGrains;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<Person> getSpeakerList() {
        return speakerList;
    }

    public void setSpeakerList(List<Person> speakerList) {
        this.speakerList = speakerList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isEntireGroupMeeting() {
        return entireGroupMeeting;
    }

    public void setEntireGroupMeeting(boolean entireGroupMeeting) {
        this.entireGroupMeeting = entireGroupMeeting;
    }

    public int getDurationInGrains() {
        return durationInGrains;
    }

    public void setDurationInGrains(int durationInGrains) {
        this.durationInGrains = durationInGrains;
    }

    public List<RequiredAttendance> getRequiredAttendanceList() {
        return requiredAttendanceList;
    }

    public void setRequiredAttendanceList(List<RequiredAttendance> requiredAttendanceList) {
        this.requiredAttendanceList = requiredAttendanceList;
    }

    public List<PreferredAttendance> getPreferredAttendanceList() {
        return preferredAttendanceList;
    }

    public void setPreferredAttendanceList(List<PreferredAttendance> preferredAttendanceList) {
        this.preferredAttendanceList = preferredAttendanceList;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getRequiredCapacity() {
        return requiredAttendanceList.size() + preferredAttendanceList.size();
    }

    public String getDurationString() {
        return (durationInGrains * TimeGrain.GRAIN_LENGTH_IN_MINUTES) + " minutes";
    }

    public String getLabel() {
        return topic;
    }

    @Override
    public String toString() {
        return topic;
    }

    public Long getId() {
        return id;
    }
}
