//package cn.luixtech.passport.server.config;
//
//import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Configuration
//public class JacksonConfiguration {
//    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
//
////    @Bean
////    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
////        return builder -> {
////            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
////        };
////    }
//
//    /**
//     * Support for Java date and time API.
//     *
//     * @return the corresponding Jackson module.
//     */
//    @Bean
//    public JavaTimeModule javaTimeModule() {
//        return new JavaTimeModule();
//    }
//
//    @Bean
//    public Jdk8Module jdk8TimeModule() {
//        return new Jdk8Module();
//    }
//}
