package org.kie;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.kie.persistence.MeetingRepository;
import org.kie.persistence.RoomRepository;
import org.kie.persistence.TimeslotRepository;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@SessionScoped
public class SessionController {

    private static Logger LOGGER = LoggerFactory.getLogger(SessionController.class);

    @Inject
    TimeslotRepository timeslotRepository;
    @Inject
    RoomRepository roomRepository;
    @Inject
    MeetingRepository meetingRepository;

    private UUID problemId;
    private String sessionId;

    public SessionController() {
    }

    public SessionController(UUID problemId, String sessionId) {
        this.problemId = problemId;
        this.sessionId = sessionId;
    }
    public void init(){}

    public UUID getProblemId() {
        return problemId;
    }

    public void setProblemId(UUID problemId) {
        this.problemId = problemId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @PreDestroy
    void destroySessionData() {
      removeSessionDataById(sessionId);
    }
    @Transactional
    public void removeSessionDataById(String sessionId) {
        meetingRepository.delete("sessionId", sessionId);
        roomRepository.delete("sessionId", sessionId);
        timeslotRepository.delete("sessionId", sessionId);
    }
}
