package org.kie.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.kie.domain.Meeting;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

@ApplicationScoped
public class MeetingRepository implements PanacheRepository<Meeting> {

    public List<Meeting> findBySessionId(String sessionId){
        return list("sessionId", sessionId);
    }
}
