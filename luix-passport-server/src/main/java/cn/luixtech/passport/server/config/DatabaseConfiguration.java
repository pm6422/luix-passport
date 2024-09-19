package cn.luixtech.passport.server.config;

import cn.luixtech.passport.server.PassportServerApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackageClasses = PassportServerApplication.class)
@EnableTransactionManagement
public class DatabaseConfiguration {
}
