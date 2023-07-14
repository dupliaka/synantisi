package org.kie.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.kie.domain.Meeting;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MeetingRepository implements PanacheRepository<Meeting> {

    public List<Meeting> findBySessionId(String sessionId){
        return list("sessionId", sessionId);
    }
    public List<Meeting> findShared(String sessionId){
        List<Meeting> sharedList = list("sessionId", sessionId);

        return sharedList.stream()
                .map(Meeting::new)
                .collect(Collectors.toList());
    }
}
