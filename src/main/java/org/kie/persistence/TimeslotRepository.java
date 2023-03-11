package org.kie.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.kie.domain.Timeslot;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TimeslotRepository implements PanacheRepository<Timeslot> {
}
