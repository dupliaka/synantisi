package org.kie.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.kie.domain.Meeting;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class MeetingRepository implements PanacheRepository<Meeting> {
}
