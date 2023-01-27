package org.kie.domain;

public class Person extends AbstractPersistable {

    private String fullName;

    public Person() {
    }

    public Person(long id, String fullName) {
        super(id);
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return fullName;
    }

}
