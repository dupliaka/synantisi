package org.kie;

import javax.enterprise.context.SessionScoped;
import java.util.UUID;

@SessionScoped
public class SessionController {

    private UUID problemId;
    private String sessionId;

    public SessionController() {
    }

    public SessionController(UUID problemId, String sessionId) {
        this.problemId = problemId;
        this.sessionId = sessionId;
    }

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
}
