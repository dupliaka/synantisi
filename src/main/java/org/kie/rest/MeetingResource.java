package org.kie.rest;

import org.kie.domain.Meeting;
import org.kie.persistence.MeetingRepository;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;
import io.quarkus.rest.data.panache.ResourceProperties;

@ResourceProperties(path = "meetings")
public interface MeetingResource extends PanacheRepositoryResource<MeetingRepository, Meeting, Long> {

}
