package org.kie.rest;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;
import io.quarkus.rest.data.panache.ResourceProperties;
import org.kie.domain.Meeting;
import org.kie.domain.Person;
import org.kie.persistence.MeetingRepository;
import org.kie.persistence.PersonRepository;

@ResourceProperties(path = "meetings")

public interface MeetingResource extends PanacheRepositoryResource<MeetingRepository, Meeting, Long> {
}
