package org.infinity.passport.controller;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.time.DateUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.PersistentAuditEvent;
import org.infinity.passport.repository.PersistenceAuditEventRepository;
import org.infinity.passport.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

/**
 * REST controller for managing the user audit events.
 */
@RestController
@Api(tags = "用户审计")
public class UserAuditEventController {

    @Autowired
    private PersistenceAuditEventRepository persistenceAuditEventRepository;

    @ApiOperation("获取用户审计事件分页列表")
    @GetMapping("/api/user-audit-event/user-audit-events")
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<List<PersistentAuditEvent>> getUserAuditEvents(Pageable pageable,
            @ApiParam(value = "开始日期", required = false) @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @ApiParam(value = "结束日期", required = false) @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to)
            throws URISyntaxException {
        Date fromTime = DateUtils.setHours(from, 0);
        fromTime = DateUtils.setMinutes(fromTime, 0);
        fromTime = DateUtils.setSeconds(fromTime, 0);
        Date toTime = DateUtils.setHours(to, 23);
        toTime = DateUtils.setMinutes(toTime, 59);
        toTime = DateUtils.setSeconds(toTime, 59);

        Page<PersistentAuditEvent> userAuditEvents = persistenceAuditEventRepository
                .findByAuditEventDateBetween(pageable, fromTime.toInstant(), toTime.toInstant());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(userAuditEvents,
                "/api/user-audit-event/user-audit-events");
        return ResponseEntity.ok().headers(headers).body(userAuditEvents.getContent());
    }
}
