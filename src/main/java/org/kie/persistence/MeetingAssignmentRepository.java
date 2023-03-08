package org.kie.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.kie.domain.MeetingAssignment;
import org.kie.domain.Person;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class MeetingAssignmentRepository implements PanacheRepository<MeetingAssignment> {
}
