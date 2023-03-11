package org.kie.rest;

import org.kie.domain.Timeslot;
import org.kie.persistence.TimeslotRepository;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;
import io.quarkus.rest.data.panache.ResourceProperties;

@ResourceProperties(path = "timeslots")
public interface TimeSlotResource extends PanacheRepositoryResource<TimeslotRepository, Timeslot, Long> {

}
