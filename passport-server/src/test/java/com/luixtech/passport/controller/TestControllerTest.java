package com.luixtech.passport.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@Slf4j
public class TestControllerTest {
    @Resource
    private MockMvc mockMvc;

    @Test
    public void testThreadSafe() throws InterruptedException {
        StopWatch watch = new StopWatch();
        watch.start();
        int requestCount = 2000;
        int threadPoolSize = 20;
        ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);

        IntStream.range(0, requestCount).forEach(i -> threadPool.execute(() -> {
            log.debug("Active thread count: {}", Thread.activeCount());
            String key = UUID.randomUUID().toString().replaceAll("-", "");
            try {
                this.mockMvc.perform(get("/open-api/tests/threadsafe?key=" + key).accept(MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        threadPool.shutdown();
        // Blocks until all tasks have completed execution after a shutdown request
        if (threadPool.awaitTermination(1, TimeUnit.HOURS)) {
            watch.stop();
            log.debug("Total: {} s", watch.getTotalTimeMillis() / 1000);
            log.debug("Mean: {} ms", watch.getTotalTimeMillis() / requestCount);
            log.debug("TPS: {}", requestCount / (watch.getTotalTimeMillis() / 1000));
        }
    }
}
