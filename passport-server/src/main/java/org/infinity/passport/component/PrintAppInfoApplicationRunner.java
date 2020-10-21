package org.infinity.passport.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.infinity.passport.utils.NetworkIpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import java.util.Date;

@Component
@Slf4j
public class PrintAppInfoApplicationRunner implements ApplicationRunner {
    @Autowired
    private Environment env;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String appBanner = StreamUtils.copyToString(new ClassPathResource("config/banner-app.txt").getInputStream(),
                Charset.defaultCharset());
        log.info(appBanner, env.getProperty("spring.application.name"),
                StringUtils.isEmpty(env.getProperty("server.ssl.key-store")) ? "http" : "https",
                NetworkIpUtils.INTRANET_IP,
                env.getProperty("server.port"),
                StringUtils.defaultString(env.getProperty("server.servlet.context-path")),
                StringUtils.isEmpty(env.getProperty("server.ssl.key-store")) ? "http" : "https",
                NetworkIpUtils.INTERNET_IP,
                env.getProperty("server.port"),
                StringUtils.defaultString(env.getProperty("server.servlet.context-path")),
                org.springframework.util.StringUtils.arrayToCommaDelimitedString(env.getActiveProfiles()),
                env.getProperty("PID"),
                Charset.defaultCharset(),
                env.getProperty("logging.level.root"),
                env.getProperty("LOG_PATH") + "-" + DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()) + ".log");
    }
}
