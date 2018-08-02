package org.infinity.passport.config;

import com.github.mongobee.Mongobee;
import com.mongodb.MongoClient;
import org.infinity.passport.config.oauth2.OAuth2AccessTokenReadConverter;
import org.infinity.passport.config.oauth2.OAuth2AuthenticationReadConverter;
import org.infinity.passport.config.oauth2.OAuth2GrantedAuthorityTokenReadConverter;
import org.infinity.passport.config.oauth2.OAuth2RefreshTokenReadConverter;
import org.infinity.passport.setup.DatabaseInitialSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoRepositories(ApplicationConstants.BASE_PACKAGE + ".repository")
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class MongoConfiguration {

    private final Logger         LOGGER = LoggerFactory.getLogger(MongoConfiguration.class);

    @Autowired
    private MongoMappingContext  mongoMappingContext;

    @Autowired
    private SimpleMongoDbFactory mongoDbFactory;

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new OAuth2AccessTokenReadConverter());
        converters.add(new OAuth2RefreshTokenReadConverter());
        converters.add(new OAuth2AuthenticationReadConverter());
        converters.add(new OAuth2GrantedAuthorityTokenReadConverter());
        return new MongoCustomConversions(converters);
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter() {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        converter.setCustomConversions(customConversions());
        // remove _class field
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory, mappingMongoConverter());
    }

    @Bean
    public Mongobee mongobee(MongoClient mongoClient, MongoTemplate mongoTemplate) {
        LOGGER.debug("Configuring Mongobee");
        Mongobee mongobee = new Mongobee(mongoClient);
        // For embedded mongo
        mongobee.setDbName(mongoClient.listDatabaseNames().first());
        mongobee.setMongoTemplate(mongoTemplate);
        mongobee.setChangeLogsScanPackage(DatabaseInitialSetup.class.getPackage().getName());
        mongobee.setEnabled(true);
        LOGGER.debug("Configured Mongobee");
        return mongobee;
    }
}
