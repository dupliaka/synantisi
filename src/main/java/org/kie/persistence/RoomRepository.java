package org.kie.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.kie.domain.Meeting;
import org.kie.domain.Room;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RoomRepository implements PanacheRepository<Room> {
    public List<Room> findShared(String sessionId){
        List<Room> sharedList = list("sessionId", sessionId);

        return sharedList.stream()
                .map(Room::new)
                .collect(Collectors.toList());
    }
}
