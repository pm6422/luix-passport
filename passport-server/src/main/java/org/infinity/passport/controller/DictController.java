package org.infinity.passport.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.Dict;
import org.infinity.passport.dto.DictDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.DictRepository;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@Api(tags = "数据字典")
@Slf4j
public class DictController {

    private final DictRepository    dictRepository;
    private final HttpHeaderCreator httpHeaderCreator;

    public DictController(DictRepository dictRepository, HttpHeaderCreator httpHeaderCreator) {
        this.dictRepository = dictRepository;
        this.httpHeaderCreator = httpHeaderCreator;
    }

    @ApiOperation("创建数据字典")
    @ApiResponses(value = {@ApiResponse(code = SC_CREATED, message = "成功创建"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典名已存在")})
    @PostMapping("/api/dict/dicts")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<Void> create(@ApiParam(value = "数据字典信息", required = true) @Valid @RequestBody DictDTO dto) {
        log.debug("REST request to create dict: {}", dto);
        dictRepository.findOneByDictCode(dto.getDictCode()).ifPresent((existingEntity) -> {
            throw new FieldValidationException("dictDTO", "dictCode", dto.getDictCode(), "error.dict.exists",
                    dto.getDictCode());
        });
        dictRepository.save(Dict.of(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("notification.dict.created", dto.getDictName())).build();
    }

    @ApiOperation("获取数据字典分页列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/dict/dicts")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<List<DictDTO>> find(Pageable pageable,
                                              @ApiParam(value = "字典名称") @RequestParam(value = "dictName", required = false) String dictName)
            throws URISyntaxException {
        Page<Dict> dicts = StringUtils.isEmpty(dictName) ? dictRepository.findAll(pageable)
                : dictRepository.findByDictName(pageable, dictName);
        List<DictDTO> DTOs = dicts.getContent().stream().map(Dict::asDTO).collect(Collectors.toList());
        HttpHeaders headers = generatePageHeaders(dicts, "/api/dict/dicts");
        return ResponseEntity.ok().headers(headers).body(DTOs);
    }

    @ApiOperation("根据字典ID检索数据字典信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "数据字典不存在")})
    @GetMapping("/api/dict/dicts/{id}")
    @Secured({Authority.DEVELOPER, Authority.USER})
    public ResponseEntity<DictDTO> findById(@ApiParam(value = "字典编号", required = true) @PathVariable String id) {
        Dict entity = dictRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        return ResponseEntity.ok(entity.asDTO());
    }

    @ApiOperation("根据字典的状态获取数据字典")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/dict/all")
    @Secured({Authority.DEVELOPER, Authority.USER})
    public ResponseEntity<List<DictDTO>> findByEnabled(
            @ApiParam(value = "是否可用,null代表全部", allowableValues = "false,true,null") @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<Dict> dicts;
        if (enabled == null) {
            dicts = dictRepository.findAll();
        } else {
            dicts = dictRepository.findByEnabled(enabled);
        }
        List<DictDTO> dictDTOs = dicts.stream().map(Dict::asDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dictDTOs);
    }

    @ApiOperation("更新数据字典信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "数据字典不存在")})
    @PutMapping("/api/dict/dicts")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<Void> update(@ApiParam(value = "新的数据字典信息", required = true) @Valid @RequestBody DictDTO dto) {
        log.debug("REST request to update dict: {}", dto);
        dictRepository.findById(dto.getId()).orElseThrow(() -> new NoDataException(dto.getId()));
        dictRepository.save(Dict.of(dto));
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.dict.updated", dto.getDictName())).build();
    }

    @ApiOperation(value = "根据字典ID删除数据字典信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "数据字典不存在")})
    @DeleteMapping("/api/dict/dicts/{id}")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<Void> delete(@ApiParam(value = "字典编号", required = true) @PathVariable String id) {
        log.debug("REST request to delete dict: {}", id);
        Dict dict = dictRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        dictRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.dict.deleted", dict.getDictName()))
                .build();
    }
}
