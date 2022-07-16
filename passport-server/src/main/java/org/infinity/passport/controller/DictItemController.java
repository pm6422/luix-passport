package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.DictItem;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.repository.DictItemRepository;
import org.infinity.passport.service.DictItemService;
import org.infinity.passport.service.DictService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@Tag(name = "数据字典项")
@Slf4j
public class DictItemController {

    @Resource
    private DictService        dictService;
    @Resource
    private DictItemRepository dictItemRepository;
    @Resource
    private DictItemService    dictItemService;
    @Resource
    private HttpHeaderCreator  httpHeaderCreator;

    @Operation(summary = "创建数据字典项")
    @PostMapping("/api/dict-items")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<Void> create(
            @Parameter(description = "数据字典项", required = true) @Valid @RequestBody DictItem domain) {
        log.debug("REST request to create dict item: {}", domain);
        DictItem dictItem = dictItemService.insert(domain);
        return ResponseEntity.status(HttpStatus.CREATED).headers(
                        httpHeaderCreator.createSuccessHeader("SM1001", dictItem.getDictItemName()))
                .build();
    }

    @Operation(summary = "分页检索数据字典项列表")
    @GetMapping("/api/dict-items")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<List<DictItem>> find(Pageable pageable,
                                               @Parameter(description = "字典代码") @RequestParam(value = "dictCode", required = false) String dictCode,
                                               @Parameter(description = "字典项名称") @RequestParam(value = "dictItemName", required = false) String dictItemName) {
        Page<DictItem> dictItems = dictItemService.find(pageable, dictCode, dictItemName);
        Map<String, String> dictCodeDictNameMap = dictService.findDictCodeDictNameMap();
        List<DictItem> domains = dictItems.getContent().stream().map(domain -> {
            domain.setDictName(dictCodeDictNameMap.get(domain.getDictCode()));
            return domain;
        }).collect(Collectors.toList());
        HttpHeaders headers = generatePageHeaders(dictItems);
        return ResponseEntity.ok().headers(headers).body(domains);
    }

    @Operation(summary = "根据ID检索数据字典项")
    @GetMapping("/api/dict-items/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    public ResponseEntity<DictItem> findById(
            @Parameter(description = "数据字典项ID", required = true) @PathVariable String id) {
        log.debug("REST request to get dict item : {}", id);
        DictItem domain = dictItemRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "更新数据字典项")
    @PutMapping("/api/dict-items")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "新的数据字典项", required = true) @Valid @RequestBody DictItem domain) {
        log.debug("REST request to update dict item: {}", domain);
        dictItemService.update(domain);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getDictItemName()))
                .build();
    }

    @Operation(summary = "更新数据字典项", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/dict-items/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "数据字典项ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete dict item: {}", id);
        DictItem dictItem = dictItemRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        dictItemRepository.deleteById(id);
        return ResponseEntity.ok().headers(
                        httpHeaderCreator.createSuccessHeader("SM1003", dictItem.getDictItemName()))
                .build();
    }
}
