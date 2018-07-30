package org.infinity.passport.controller;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.HanlpPersonName;
import org.infinity.passport.repository.HanlpPersonNameRepository;
import org.infinity.passport.utils.MongoDbIOAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Hanlp人名")
public class HanlpPersonNameController {

    @Autowired
    private HanlpPersonNameRepository hanlpPersonNameRepository;

    @Autowired
    private MongoDbIOAdapter          mongoDbIOAdapter;

    @ApiOperation(value = "导入参数值")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功导入") })
    @PostMapping("/api/hanlp-person-name/import")
    public ResponseEntity<Void> importData() throws IOException {
        File file = new File(
                System.getProperty("user.home") + File.separatorChar + "HanlpData" + File.separatorChar + "人名词典.txt");
        List<String> lines = IOUtils.readLines(new FileInputStream(file), StandardCharsets.UTF_8);
        int i = 1;
        List<HanlpPersonName> list = new ArrayList<HanlpPersonName>();
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                String[] splitResults = line.split(" ");

                HanlpPersonName entity = new HanlpPersonName();
                entity.setName(splitResults[0]);
                entity.setPos(splitResults[1]);
                entity.setFrequency(Integer.parseInt(splitResults[2]));
                list.add(entity);
            }
            if (i % (lines.size() / 10000) == 0) {
                hanlpPersonNameRepository.saveAll(list);
                list = new ArrayList<>();
            }
            i++;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("分词")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功获取") })
    @GetMapping("/api/hanlp-person-name/segment")
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<List<Term>> segment(
            @ApiParam(value = "inputText", required = true, defaultValue = "买买提·亚合甫多大的") @RequestParam(value = "inputText", required = false) String inputText) {
        Segment segment = HanLP.newSegment().enableNameRecognize(true);
        String[] dictionaries = { HanlpPersonName.class.getName() };
        // 用户自定义词典中，第一个是主词典，其他是副词典。主词典会被缓存存储为.bin的文件中。
        HanLP.Config.CustomDictionaryPath = dictionaries;
        HanLP.Config.IOAdapter = mongoDbIOAdapter;
        List<Term> termList = segment.seg(inputText);
        return new ResponseEntity<>(termList, HttpStatus.OK);
    }
}
