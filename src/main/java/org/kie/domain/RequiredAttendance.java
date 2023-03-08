package org.kie.domain;

import javax.persistence.Entity;

@Entity
public class RequiredAttendance extends Attendance {

    public RequiredAttendance() {
    }

    public RequiredAttendance(long id, Meeting meeting) {
        super(id, meeting);
    }

}
