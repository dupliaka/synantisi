package org.kie.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
@Entity
public class PreferredAttendance extends Attendance {
    @Id
    @GeneratedValue
    protected Long id;

    public PreferredAttendance() {
    }

    public PreferredAttendance(Meeting meeting) {
        super( meeting);
    }
}
