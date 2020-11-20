package org.infinity.passport.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.PersistentAuditEvent;
import org.infinity.passport.repository.PersistenceAuditEventRepository;
import org.infinity.passport.utils.PaginationUtils;
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
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing the user audit events.
 */
@RestController
@Api(tags = "用户审计")
public class UserAuditEventController {

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;

    public UserAuditEventController(PersistenceAuditEventRepository persistenceAuditEventRepository) {
        this.persistenceAuditEventRepository = persistenceAuditEventRepository;
    }

    @ApiOperation("获取用户审计事件分页列表")
    @GetMapping("/api/user-audit-event/user-audit-events")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<List<PersistentAuditEvent>> getUserAuditEvents(Pageable pageable,
                                                                         @ApiParam(value = "开始日期") @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                                         @ApiParam(value = "结束日期") @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws URISyntaxException {
        Page<PersistentAuditEvent> userAuditEvents = persistenceAuditEventRepository.findByAuditEventDateBetween(pageable, from, to);
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(userAuditEvents, "/api/user-audit-event/user-audit-events");
        return ResponseEntity.ok().headers(headers).body(userAuditEvents.getContent());
    }
}
