package org.kie.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.kie.domain.Timeslot;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TimeslotRepository implements PanacheRepository<Timeslot> {
    public List<Timeslot> findShared(String sessionId){
        List<Timeslot> sharedList = list("sessionId", sessionId);

        return sharedList.stream()
                .map(Timeslot::new)
                .collect(Collectors.toList());
    }
}
