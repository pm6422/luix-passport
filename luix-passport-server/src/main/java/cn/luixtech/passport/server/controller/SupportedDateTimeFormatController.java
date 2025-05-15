package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.SupportedDateTimeFormat;
import cn.luixtech.passport.server.repository.SupportedDateTimeFormatRepository;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for supported date time format.
 */
@RestController
@AllArgsConstructor
@Slf4j
public class SupportedDateTimeFormatController {
    private final SupportedDateTimeFormatRepository supportedDateTimeFormatRepository;

    @Operation(summary = "find user by id")
    @GetMapping("/api/supported-date-time-formats/{id}")
    public ResponseEntity<SupportedDateTimeFormat> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        return ResponseEntity.ok(supportedDateTimeFormatRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id)));
    }
}
