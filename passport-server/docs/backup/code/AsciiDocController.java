//package org.infinity.ssoserver.controller;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import com.codahale.metrics.annotation.Timed;
//import org.infinity.ssoserver.config.ApplicationProperties;
//
//import io.github.robwin.markup.builder.MarkupLanguage;
//import io.github.robwin.swagger2markup.Swagger2MarkupConverter;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//
///**
// * REST controller for generating static API docs.
// */
//@RestController
//@Api(tags = "接口文档")
//public class AsciiDocController {
//
//    @Autowired
//    private Environment           env;
//
//    @Autowired
//    private ApplicationProperties applicationProperties;
//
//    /**
//     * generate the API Ascii doc.
//     */
//    @ApiOperation("生成API Ascii doc")
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功创建") })
//    @RequestMapping(value = "/api/generate/api", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public void generateApiDoc() throws IOException {
//        String host = "http://127.0.0.1:" + env.getProperty("server.port");
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> responseEntity = restTemplate.getForEntity(host + "/v2/api-docs?group=api-group",
//                String.class);
//        Swagger2MarkupConverter.fromString(responseEntity.getBody()).withMarkupLanguage(MarkupLanguage.ASCIIDOC).build()
//                .intoFolder(applicationProperties.getDirectory().getAsciiDoc());
//    }
//
//    /**
//     * generate the Open API Ascii doc.
//     */
//    @ApiOperation("生成Open API Ascii doc")
//    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功创建") })
//    @RequestMapping(value = "/api/generate/open-api", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public void generateOpenApiDoc() throws IOException {
//        String host = "http://127.0.0.1:" + env.getProperty("server.port");
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> responseEntity = restTemplate.getForEntity(host + "/v2/api-docs?group=open-api-group",
//                String.class);
//        Swagger2MarkupConverter.fromString(responseEntity.getBody()).withMarkupLanguage(MarkupLanguage.ASCIIDOC).build()
//                .intoFolder(applicationProperties.getDirectory().getAsciiDoc());
//    }
//}
