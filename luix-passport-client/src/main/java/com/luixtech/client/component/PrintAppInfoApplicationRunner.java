package com.luixtech.client.component;

import com.luixtech.client.utils.NetworkUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import java.util.Date;

@Component
@AllArgsConstructor
@Slf4j
public class PrintAppInfoApplicationRunner implements ApplicationRunner {
    private final Environment env;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String appBanner = StreamUtils.copyToString(new ClassPathResource("config/banner-app.txt").getInputStream(),
                Charset.defaultCharset());
        log.info(appBanner, env.getProperty("spring.application.name"),
                "http",
                "127.0.0.1",
                env.getProperty("server.port"),
                StringUtils.defaultString(env.getProperty("server.servlet.context-path")),
                "http",
                NetworkUtils.INTRANET_IP,
                env.getProperty("server.port"),
                StringUtils.defaultString(env.getProperty("server.servlet.context-path")),
                org.springframework.util.StringUtils.arrayToCommaDelimitedString(env.getActiveProfiles()),
                env.getProperty("PID"),
                Charset.defaultCharset(),
                env.getProperty("logging.level.root"),
                env.getProperty("LOG_PATH") + "-" + DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()) + ".log");
    }
}
