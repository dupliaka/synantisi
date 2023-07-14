package org.kie.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.optaplanner.core.api.domain.lookup.PlanningId;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Room {

    @PlanningId
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Integer capacity;
    private String sessionId;

    public Room() {
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return name;
    }

    public Room(Room other) {
        this.name = other.name;
        this.capacity = other.capacity;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
