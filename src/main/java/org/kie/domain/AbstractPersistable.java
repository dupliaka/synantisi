package org.kie.domain;


import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public abstract class AbstractPersistable {

    @Id
    @GeneratedValue
    protected Long id;

    protected AbstractPersistable() {
    }

    protected AbstractPersistable(long id) {
        this.id = id;
    }

    @PlanningId
    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getName().replaceAll(".*\\.", "") + "-" + id;
    }

}
