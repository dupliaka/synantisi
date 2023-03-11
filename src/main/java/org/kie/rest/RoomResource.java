package org.kie.rest;

import org.kie.domain.Room;
import org.kie.persistence.RoomRepository;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;
import io.quarkus.rest.data.panache.ResourceProperties;

@ResourceProperties(path = "rooms")
public interface RoomResource extends PanacheRepositoryResource<RoomRepository, Room, Long> {

}
