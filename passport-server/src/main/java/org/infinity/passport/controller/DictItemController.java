package org.infinity.passport.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.DictItem;
import org.infinity.passport.dto.DictItemDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.DictItemRepository;
import org.infinity.passport.repository.DictRepository;
import org.infinity.passport.service.DictItemService;
import org.infinity.passport.service.DictService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@Api(tags = "数据字典项")
@Slf4j
public class DictItemController {

    private final DictRepository dictRepository;

    private final DictService dictService;

    private final DictItemRepository dictItemRepository;

    private final DictItemService dictItemService;

    private final HttpHeaderCreator httpHeaderCreator;

    public DictItemController(DictRepository dictRepository,
                              DictService dictService,
                              DictItemRepository dictItemRepository,
                              DictItemService dictItemService,
                              HttpHeaderCreator httpHeaderCreator) {
        this.dictRepository = dictRepository;
        this.dictService = dictService;
        this.dictItemRepository = dictItemRepository;
        this.dictItemService = dictItemService;
        this.httpHeaderCreator = httpHeaderCreator;
    }

    @ApiOperation("创建数据字典项")
    @ApiResponses(value = {@ApiResponse(code = SC_CREATED, message = "成功创建"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典code不存在或相同的字典code和字典项已存在")})
    @PostMapping("/api/dict-item/items")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<Void> create(
            @ApiParam(value = "数据字典项", required = true) @Valid @RequestBody DictItemDTO dto) {
        log.debug("REST request to create dict item: {}", dto);
        // 判断dictCode是否存在
        dictRepository.findOneByDictCode(dto.getDictCode())
                .orElseThrow(() -> new FieldValidationException("dictItemDTO", "dictCode", dto.getDictCode(),
                        "error.dict.not.exist", dto.getDictCode()));
        // 根据dictItemCode与dictCode检索记录是否存在
        List<DictItem> existingDictItems = dictItemRepository.findByDictCodeAndDictItemCode(dto.getDictCode(),
                dto.getDictItemCode());
        if (CollectionUtils.isNotEmpty(existingDictItems)) {
            throw new FieldValidationException("dictItemDTO", "dictCode+dictItemCode",
                    MessageFormat.format("dictCode: {0}, dictItemCode: {1}", dto.getDictCode(), dto.getDictItemCode()),
                    "error.dict.item.exist",
                    MessageFormat.format("dictCode: {0}, dictItemCode: {1}", dto.getDictCode(), dto.getDictItemCode()));
        }
        DictItem dictItem = dictItemService.insert(dto.getDictCode(), dto.getDictItemCode(), dto.getDictItemName(),
                dto.getRemark(), dto.getEnabled());
        return ResponseEntity.status(HttpStatus.CREATED).headers(
                httpHeaderCreator.createSuccessHeader("notification.dict.item.created", dictItem.getDictItemName()))
                .build();
    }

    @ApiOperation("分页检索数据字典项列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/api/dict-item/items")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<List<DictItemDTO>> find(Pageable pageable,
                                                  @ApiParam(value = "字典代码") @RequestParam(value = "dictCode", required = false) String dictCode,
                                                  @ApiParam(value = "字典项名称") @RequestParam(value = "dictItemName", required = false) String dictItemName)
            throws URISyntaxException {
        Page<DictItem> dictItems = dictItemService.find(pageable, dictCode, dictItemName);
        Map<String, String> dictCodeDictNameMap = dictService.findDictCodeDictNameMap();
        List<DictItemDTO> DTOs = dictItems.getContent().stream().map(entity -> {
            DictItemDTO dto = entity.asDTO();
            dto.setDictName(dictCodeDictNameMap.get(dto.getDictCode()));
            return entity.asDTO();
        }).collect(Collectors.toList());
        HttpHeaders headers = generatePageHeaders(dictItems, "/api/dict-item/items");
        return ResponseEntity.ok().headers(headers).body(DTOs);
    }

    @ApiOperation("根据ID检索数据字典项")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典项不存在")})
    @GetMapping("/api/dict-item/items/{id}")
    @Secured({Authority.USER})
    public ResponseEntity<DictItemDTO> findById(
            @ApiParam(value = "数据字典项ID", required = true) @PathVariable String id) {
        log.debug("REST request to get dict item : {}", id);
        DictItem entity = dictItemRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        return ResponseEntity.ok(entity.asDTO());
    }

    @ApiOperation("根据数据字典代码检索数据字典项")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/api/dict-item/dict-code/{dictCode}")
    @Secured({Authority.USER})
    public ResponseEntity<List<DictItemDTO>> findByDictCode(
            @ApiParam(value = "字典编号", required = true) @PathVariable String dictCode) {
        log.debug("REST request to get dict item : {}", dictCode);
        // 根据dictCode检索数据字典项
        List<DictItem> dictItems = dictItemRepository.findByDictCode(dictCode);
        if (CollectionUtils.isEmpty(dictItems)) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<DictItemDTO> dictItemDTOs = dictItems.stream().map(DictItem::asDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dictItemDTOs);
    }

    @ApiOperation("更新数据字典项")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典项不存在")})
    @PutMapping("/api/dict-item/items")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<Void> update(
            @ApiParam(value = "新的数据字典项", required = true) @Valid @RequestBody DictItemDTO dto) {
        log.debug("REST request to update dict item: {}", dto);
        dictItemRepository.findById(dto.getId()).orElseThrow(() -> new NoDataException(dto.getId()));
        dictItemService.update(dto.getId(), dto.getDictCode(), dto.getDictItemCode(), dto.getDictItemName(),
                dto.getRemark(), dto.getEnabled());
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.dict.item.updated", dto.getDictItemName()))
                .build();
    }

    @ApiOperation(value = "根据ID删除数据字典项", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典项不存在")})
    @DeleteMapping("/api/dict-item/items/{id}")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<Void> delete(@ApiParam(value = "数据字典项ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete dict item: {}", id);
        DictItem dictItem = dictItemRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        dictItemRepository.deleteById(id);
        return ResponseEntity.ok().headers(
                httpHeaderCreator.createSuccessHeader("notification.dict.item.deleted", dictItem.getDictItemName()))
                .build();
    }
}
