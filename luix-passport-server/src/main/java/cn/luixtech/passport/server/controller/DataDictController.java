package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.DataDict;
import cn.luixtech.passport.server.pojo.BatchUpdateDataDict;
import cn.luixtech.passport.server.repository.DataDictRepository;
import cn.luixtech.passport.server.service.DataDictService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_DEVELOPER;
import static com.luixtech.springbootframework.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_DEVELOPER + "\")")
@Slf4j
public class DataDictController {
    private final DataDictRepository dataDictRepository;
    private final DataDictService    dataDictService;

    @Operation(summary = "create new data dict")
    @PostMapping("/api/data-dicts")
    public ResponseEntity<Void> create(@Parameter(description = "domain", required = true) @Valid @RequestBody DataDict domain) {
        dataDictRepository.save(domain);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "find data dict list")
    @GetMapping("/api/data-dicts")
    public ResponseEntity<List<DataDict>> find(@ParameterObject Pageable pageable,
                                               @RequestParam(value = "num", required = false) String num,
                                               @RequestParam(value = "categoryCode", required = false) String categoryCode,
                                               @RequestParam(value = "enabled", required = false) Boolean enabled) {
        Page<DataDict> domains = dataDictService.find(pageable, num, categoryCode, enabled);
        return ResponseEntity.ok().headers(generatePageHeaders(domains)).body(domains.getContent());
    }

    @Operation(summary = "find data dict by id")
    @GetMapping("/api/data-dicts/{id}")
    public ResponseEntity<DataDict> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        DataDict domain = dataDictRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "update data dict")
    @PutMapping("/api/data-dicts")
    public ResponseEntity<Void> update(@Parameter(description = "domain", required = true) @Valid @RequestBody DataDict domain) {
        dataDictRepository.save(domain);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "batch update data dict")
    @PutMapping("/api/data-dicts/batch-update")
    public ResponseEntity<Void> batchUpdate(@Parameter(description = "target", required = true) @Valid @RequestBody BatchUpdateDataDict target) {
        dataDictService.batchUpdateCategoryCode(target.getIds(), target.getTargetCategoryCode());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "delete data dict by id")
    @DeleteMapping("/api/data-dicts/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        dataDictRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "import data dicts", description = "file format should be JSON")
    @PostMapping(value = "/api/data-dicts/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void importData(@Parameter(description = "file", required = true) @RequestPart MultipartFile file) throws IOException {
        String jsonStr = StreamUtils.copyToString(file.getInputStream(), StandardCharsets.UTF_8);
        List<DataDict> records = JSON.parseArray(jsonStr, DataDict.class);
        List<DataDict> all = new ArrayList<>(records.size());
        records.forEach(record -> {
            record.setId(null);
            record.setCreatedAt(LocalDateTime.now());
            record.setModifiedAt(record.getCreatedAt());
            all.add(record);
        });
        dataDictRepository.saveAll(all);
    }

    @Operation(summary = "download import template")
    @GetMapping("/api/data-dicts/import-template")
    public ResponseEntity<ByteArrayResource> getImportTemplate() {
        DataDict dataDict = dataDictRepository.findFirstByOrderByIdAsc();
        byte[] data = JSON.toJSONString(Arrays.asList(dataDict), JSONWriter.Feature.PrettyFormat).getBytes();
        ByteArrayResource resource = new ByteArrayResource(data);
        String fileName = "data-dict-" + DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()) + ".json";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }
}
