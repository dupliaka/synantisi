package org.kie.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Timeslot {

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    static private final LocalDateTime f2fStartDate = LocalDateTime.of(2023,3,6,0,0);// TODO: set F2F period in the beginning of the session
    private String sessionId;

    // No-arg constructor required for Hibernate
    public Timeslot() {
    }

    public Timeslot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Timeslot(Long id, DayOfWeek dayOfWeek, LocalTime startTime) {
        this(dayOfWeek, startTime, startTime.plusMinutes(50));
        this.id = id;
    }



    public Timeslot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String sessionId) {
        this(dayOfWeek, startTime, endTime);
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @JsonIgnore
    public LocalDateTime getStartDateTime(){

        int daysToAdd = dayOfWeek.getValue() - f2fStartDate.getDayOfWeek().getValue();
        if (daysToAdd < 0) {
            daysToAdd += 7;
        }

        return startTime.atDate(LocalDate.from(f2fStartDate.plusDays(daysToAdd)));
    }
    @JsonIgnore
    public int getTimeDifferenceFromStartDate(){
        return Math.toIntExact(Math.abs(ChronoUnit.HOURS.between(getStartDateTime(), f2fStartDate)));
    }

    public Timeslot(Timeslot other){
        this.dayOfWeek = other.dayOfWeek;
        this.startTime = other.startTime;
        this.endTime = other.endTime;
    }

}
