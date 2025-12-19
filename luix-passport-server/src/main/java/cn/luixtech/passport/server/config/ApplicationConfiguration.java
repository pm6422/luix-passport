package cn.luixtech.passport.server.config;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public UserAgentAnalyzer userAgentAnalyzer() {
    return UserAgentAnalyzer
        .newBuilder()
        .withCache(1000)
        .build();
  }
}
