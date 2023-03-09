package org.kie.domain;


public abstract class Attendance extends AbstractPersistable {

    private Person person;
    private Meeting meeting;

    protected Attendance() {
    }

    protected Attendance( Meeting meeting) {
        this.meeting = meeting;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    @Override
    public String toString() {
        return person + "-" + meeting;
    }

}
