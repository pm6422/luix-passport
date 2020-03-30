package org.infinity.passport.repository;

import org.infinity.passport.domain.PersistentHttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of Spring Boot's HttpTraceRepository.
 */
@Repository
public class CustomTraceRepository implements HttpTraceRepository {

    private final PersistenceHttpTraceRepository persistenceHttpTraceRepository;

    public CustomTraceRepository(PersistenceHttpTraceRepository persistenceHttpTraceRepository) {
        this.persistenceHttpTraceRepository = persistenceHttpTraceRepository;
    }

    @Override
    public List<HttpTrace> findAll() {
        return persistenceHttpTraceRepository.findAll().stream()
                .map(trace -> {
                    HttpTrace.Request request = new HttpTrace.Request(null, URI.create(trace.getUri()), Collections.emptyMap(), null);
                    HttpTrace.Response response = new HttpTrace.Response(trace.getStatus(), Collections.emptyMap());
                    return new HttpTrace(request, response, trace.getTimestamp(), null, null, trace.getTimeTaken());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void add(HttpTrace trace) {
        PersistentHttpTrace persistentHttpTrace = new PersistentHttpTrace();
        persistentHttpTrace.setUri(trace.getRequest().getUri().toString());
        persistentHttpTrace.setTimestamp(trace.getTimestamp());
        persistentHttpTrace.setTimeTaken(trace.getTimeTaken());
        persistentHttpTrace.setStatus(trace.getResponse().getStatus());
        persistenceHttpTraceRepository.save(persistentHttpTrace);
    }
}