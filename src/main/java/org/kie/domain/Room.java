package org.kie.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.optaplanner.core.api.domain.lookup.PlanningId;

@Entity
public class Room {

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String sessionId;

    public Room() {
    }

    public Room(String name, String sessionId) {
        this.name = name;
        this.sessionId = sessionId;
    }

    public Room(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return name;
    }

}
