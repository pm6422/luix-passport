package com.luixtech.passport.server.config;

import com.luixtech.passport.server.PassportServerApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackageClasses = PassportServerApplication.class)
@EnableTransactionManagement
public class DatabaseConfiguration {
}
