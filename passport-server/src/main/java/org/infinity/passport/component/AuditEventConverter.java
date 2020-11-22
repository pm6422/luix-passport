package org.infinity.passport.component;

import org.infinity.passport.domain.PersistentAuditEvent;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuditEventConverter {

    /**
     * Convert a list of PersistentAuditEvent to a list of AuditEvent
     *
     * @param persistentAuditEvents the list to convert
     * @return the converted list
     */
    public List<AuditEvent> convertToAuditEvent(List<PersistentAuditEvent> persistentAuditEvents) {
        if (Objects.isNull(persistentAuditEvents)) {
            return Collections.emptyList();
        }
        return persistentAuditEvents.stream().map(x -> convertToAuditEvent(x)).collect(Collectors.toList());
    }

    /**
     * Convert a PersistentAuditEvent to an AuditEvent
     *
     * @param persistentAuditEvent the event to convert
     * @return the converted list
     */
    private AuditEvent convertToAuditEvent(PersistentAuditEvent persistentAuditEvent) {
        if (Objects.isNull(persistentAuditEvent)) {
            return null;
        }
        return new AuditEvent(persistentAuditEvent.getAuditEventDate(), persistentAuditEvent.getPrincipal(),
                persistentAuditEvent.getAuditEventType(), convertDataToObjects(persistentAuditEvent.getData()));
    }

    /**
     * Internal conversion. This is needed to support the current SpringBoot actuator AuditEventRepository interface
     *
     * @param data the data to convert
     * @return a map of String, Object
     */
    private Map<String, Object> convertDataToObjects(Map<String, String> data) {
        if (Objects.isNull(data)) {
            return Collections.emptyMap();
        }
        return data.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Internal conversion. This method will allow to save additional data.
     * By default, it will save the object as string
     *
     * @param data the data to convert
     * @return a map of String, String
     */
    public Map<String, String> convertDataToStrings(Map<String, Object> data) {
        if (Objects.isNull(data)) {
            return Collections.emptyMap();
        }

        Map<String, String> results = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Object object = entry.getValue();

            // Extract the data that will be saved.
            if (object instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) object;
                results.put("remoteAddress", authenticationDetails.getRemoteAddress());
                results.put("sessionId", authenticationDetails.getSessionId());
            } else if (object != null) {
                results.put(entry.getKey(), object.toString());
            } else {
                results.put(entry.getKey(), "null");
            }
        }
        return results;
    }
}
