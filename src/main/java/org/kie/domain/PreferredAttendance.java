package org.kie.domain;

import javax.persistence.Entity;

@Entity
public class PreferredAttendance extends Attendance {

    public PreferredAttendance() {
    }

    public PreferredAttendance(long id, Meeting meeting) {
        super(id, meeting);
    }
}
