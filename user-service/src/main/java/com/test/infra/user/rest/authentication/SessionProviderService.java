package com.test.infra.user.rest.authentication;

import com.test.domain.user.api.IUser;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionProviderService<T extends IUser> {

    private Map<String, T> sessions;

    public SessionProviderService(@Value("${session.timeout}") long sessionTimeout, @Value("${session.chronoUnit}") ChronoUnit chronoUnit) {
        sessions = new PassiveExpiringMap<>(Duration.of(sessionTimeout, chronoUnit).toMillis());
    }

    public String newSessionId(){
        return UUID.randomUUID().toString();
    }

    public void put(String sessionId, T user) {
        synchronized (this) {
            sessions.put(sessionId, user);
        }
    }

    public void renew(String sessionId) {
        synchronized (this) {
            T user = sessions.get(sessionId);
            sessions.remove(sessionId);
            put(sessionId, user);
        }
    }

    public Optional<T> get(String sessionId) {
        synchronized (this) {
            return Optional.ofNullable(sessions.get(sessionId));
        }
    }
}
