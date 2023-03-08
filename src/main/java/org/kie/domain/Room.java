package org.kie.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
@Entity
public class Room {

    @PlanningId
    @Id
    @GeneratedValue
    protected Long id;
    private String name;
    private int capacity;

    public Room() {
    }

    public Room(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Room(long id, String name, int capacity) {
        this(id, name);
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return name;
    }

}
