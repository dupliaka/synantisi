package org.kie.domain;

public class Room extends AbstractPersistable  {

    private String name;
    private int capacity;

    public Room() {
    }

    public Room(long id, String name) {
        super(id);
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
