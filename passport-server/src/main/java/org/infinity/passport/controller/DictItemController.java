package org.infinity.passport.controller;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.DictItem;
import org.infinity.passport.dto.DictItemDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.DictItemRepository;
import org.infinity.passport.repository.DictRepository;
import org.infinity.passport.service.DictItemService;
import org.infinity.passport.service.DictService;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@Api(tags = "数据字典项")
public class DictItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictItemController.class);

    @Autowired
    private DictRepository dictRepository;

    @Autowired
    private DictService dictService;

    @Autowired
    private DictItemRepository dictItemRepository;

    @Autowired
    private DictItemService dictItemService;

    @Autowired
    private HttpHeaderCreator httpHeaderCreator;

    @ApiOperation("创建数据字典项")
    @ApiResponses(value = {@ApiResponse(code = SC_CREATED, message = "成功创建"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典code不存在或相同的字典code和字典项已存在")})
    @PostMapping("/api/dict-item/items")
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> create(
            @ApiParam(value = "数据字典项信息", required = true) @Valid @RequestBody DictItemDTO dto) {
        LOGGER.debug("REST request to create dict item: {}", dto);
        // 判断dictCode是否存在
        dictRepository.findOneByDictCode(dto.getDictCode())
                .orElseThrow(() -> new FieldValidationException("dictItemDTO", "dictCode", dto.getDictCode(),
                        "error.dict.not.exist", dto.getDictCode()));
        // 根据dictItemCode与dictCode查询记录是否存在
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

    @ApiOperation("获取数据字典项分页列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/dict-item/items")
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<List<DictItemDTO>> find(Pageable pageable,
                                                  @ApiParam(value = "字典代码", required = false) @RequestParam(value = "dictCode", required = false) String dictCode,
                                                  @ApiParam(value = "字典项名称", required = false) @RequestParam(value = "dictItemName", required = false) String dictItemName)
            throws URISyntaxException {
        Page<DictItem> dictItems = dictItemService.findByDictCodeAndDictItemNameCombinations(pageable, dictCode,
                dictItemName);
        Map<String, String> dictCodeDictNameMap = dictService.findDictCodeDictNameMap();
        List<DictItemDTO> dictItemDTOs = dictItems.getContent().stream().map(entity -> {
            DictItemDTO dto = entity.asDTO();
            dto.setDictName(dictCodeDictNameMap.get(dto.getDictCode()));
            return entity.asDTO();
        }).collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(dictItems, "/api/dict-item/items");
        return new ResponseEntity<>(dictItemDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据数据字典项ID检索数据字典项信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典项不存在")})
    @GetMapping("/api/dict-item/items/{id}")
    @Secured({Authority.USER})
    @Timed
    public ResponseEntity<DictItemDTO> findById(
            @ApiParam(value = "数据字典项ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to get dict item : {}", id);
        DictItem entity = dictItemRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        return new ResponseEntity<>(entity.asDTO(), HttpStatus.OK);
    }

    @ApiOperation("根据数据字典代码检索数据字典项信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/dict-item/dict-code/{dictCode}")
    @Secured({Authority.USER})
    @Timed
    public ResponseEntity<List<DictItemDTO>> findByDictCode(
            @ApiParam(value = "字典编号", required = true) @PathVariable String dictCode) {
        LOGGER.debug("REST request to get dict item : {}", dictCode);
        // 根据dictCode查询数据字典项信息
        List<DictItem> dictItems = dictItemRepository.findByDictCode(dictCode);
        if (CollectionUtils.isEmpty(dictItems)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        List<DictItemDTO> dictItemDTOs = dictItems.stream().map(dictItem -> dictItem.asDTO())
                .collect(Collectors.toList());
        return new ResponseEntity<>(dictItemDTOs, HttpStatus.OK);
    }

    @ApiOperation("更新数据字典项信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典项不存在")})
    @PutMapping("/api/dict-item/items")
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> update(
            @ApiParam(value = "新的数据字典项信息", required = true) @Valid @RequestBody DictItemDTO dto) {
        LOGGER.debug("REST request to update dict item: {}", dto);
        dictItemRepository.findById(dto.getId()).orElseThrow(() -> new NoDataException(dto.getId()));
        dictItemService.update(dto.getId(), dto.getDictCode(), dto.getDictItemCode(), dto.getDictItemName(),
                dto.getRemark(), dto.getEnabled());
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.dict.item.updated", dto.getDictItemName()))
                .build();
    }

    @ApiOperation(value = "根据数据字典编号与字典项编号删除数据字典信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典项不存在")})
    @DeleteMapping("/api/dict-item/items/{id}")
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<Void> delete(@ApiParam(value = "数据字典项ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete dict item: {}", id);
        DictItem dictItem = dictItemRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        dictItemRepository.deleteById(id);
        return ResponseEntity.ok().headers(
                httpHeaderCreator.createSuccessHeader("notification.dict.item.deleted", dictItem.getDictItemName()))
                .build();
    }
}
