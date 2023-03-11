package org.kie.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.kie.domain.Room;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class RoomRepository implements PanacheRepository<Room> {
}
