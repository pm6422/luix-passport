package org.infinity.passport.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@RestController
@Api(tags = "性能测试")
public class PerformanceTestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTestController.class);

    private static final Set<String>        PARAM_SET = new ConcurrentSkipListSet<>();// 存储已有参数，用于判断参数是否重复，从而判断线程是否安全
    @Autowired
    private              Environment        env;
    // 自动注入request，Spring并没有注入一个request对象，而是注入了一个代理（proxy）；当Bean中需要使用request对象时，通过该代理获取request对象。
    // 代理的实现参见AutowireUtils的内部类ObjectFactoryDelegatingInvocationHandler
    // 当我们调用request的方法method时，实际上是调用了由objectFactory.getObject()生成的对象的method方法；objectFactory.getObject()生成的对象才是真正的request对象。
    // 发现objectFactory的类型为WebApplicationContextUtils的内部类RequestObjectFactory
    // 通过代码可以看出，生成的RequestAttributes对象是线程局部变量（ThreadLocal），因此request对象也是线程局部变量；这就保证了request对象的线程安全性。
    @Autowired
    private              HttpServletRequest request;

    @GetMapping("/open-api/performance/req-thread-safe")
    public void test(HttpServletRequest request) throws InterruptedException {
        // request对象为局部变量，是线程安全的
        String value = request.getParameter("key");
        if (PARAM_SET.contains(value)) {
            LOGGER.error(value + "重复出现，request并发不安全！");
        } else {
            LOGGER.debug(value);
            PARAM_SET.add(value);
        }

//        Thread.sleep(1000);// 模拟程序执行了一段时间
    }

    @ApiOperation(value = "测试Request对象线程安全", notes = "1万以下不要用parallel")
    @GetMapping("/api/performance/req-thread-safe-iteration")
    public ResponseEntity<Void> testReqThreadSafeIteration(@ApiParam(value = "样本数量", required = true, defaultValue = "1000") @RequestParam(value = "sampleCount", required = true) Integer sampleCount) {
        String prefix = UUID.randomUUID().toString().replaceAll("-", "") + "::";
        for (int i = 0; i < sampleCount; i++) {
            final String value = prefix + i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        HttpGet httpGet = new HttpGet("http://localhost:" + env.getProperty("server.port") + "/open-api/performance/req-thread-safe?key=" + value);
                        httpClient.execute(httpGet);
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        return ResponseEntity.ok(null);
    }
}
