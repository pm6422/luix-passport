package org.infinity.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.Dict;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.exception.DuplicationException;
import org.infinity.passport.repository.DictRepository;
import org.infinity.passport.service.DictService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@Tag(name = "数据字典")
@Slf4j
public class DictController {

    @Resource
    private DictRepository    dictRepository;
    @Resource
    private DictService       dictService;
    @Resource
    private HttpHeaderCreator httpHeaderCreator;

    @Operation(summary = "创建数据字典")
    @PostMapping("/api/dicts")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<Void> create(@Parameter(description = "数据字典", required = true) @Valid @RequestBody Dict domain) {
        log.debug("REST request to create dict: {}", domain);
        dictRepository.findOneByDictCode(domain.getDictCode()).ifPresent((existingEntity) -> {
            throw new DuplicationException(ImmutableMap.of("dictCode", domain.getDictCode()));
        });
        dictRepository.insert(domain);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", domain.getDictName())).build();
    }

    @Operation(summary = "分页检索数据字典列表")
    @GetMapping("/api/dicts")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<List<Dict>> find(Pageable pageable,
                                           @Parameter(description = "字典名称") @RequestParam(value = "dictName", required = false) String dictName,
                                           @Parameter(description = "是否可用,null代表全部", schema = @Schema(allowableValues = "false,true,null")) @RequestParam(value = "enabled", required = false) Boolean enabled) {
        Page<Dict> dicts = dictService.find(pageable, dictName, enabled);
        HttpHeaders headers = generatePageHeaders(dicts);
        return ResponseEntity.ok().headers(headers).body(dicts.getContent());
    }

    @Operation(summary = "根据ID检索数据字典")
    @GetMapping("/api/dicts/{id}")
    @PreAuthorize("hasAnyAuthority(\"" + Authority.DEVELOPER + "\", \"" + Authority.USER + "\")")
    public ResponseEntity<Dict> findById(@Parameter(description = "字典编号", required = true) @PathVariable String id) {
        Dict domain = dictRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "更新数据字典")
    @PutMapping("/api/dicts")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<Void> update(@Parameter(description = "新的数据字典", required = true) @Valid @RequestBody Dict domain) {
        log.debug("REST request to update dict: {}", domain);
        dictRepository.findById(domain.getId()).orElseThrow(() -> new DataNotFoundException(domain.getId()));
        dictRepository.save(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getDictName())).build();
    }

    @Operation(summary = "根据ID删除数据字典", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/dicts/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "字典编号", required = true) @PathVariable String id) {
        log.debug("REST request to delete dict: {}", id);
        Dict dict = dictRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        dictRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", dict.getDictName())).build();
    }
}
