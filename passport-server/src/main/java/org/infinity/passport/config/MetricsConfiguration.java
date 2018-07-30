package org.infinity.passport.config;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class MetricsConfiguration extends MetricsConfigurerAdapter {

    private static final Logger   LOGGER                      = LoggerFactory.getLogger(MetricsConfiguration.class);

    private static final String   PROP_METRIC_REG_JVM_MEMORY  = "jvm.memory";

    private static final String   PROP_METRIC_REG_JVM_GARBAGE = "jvm.garbage";

    private static final String   PROP_METRIC_REG_JVM_THREADS = "jvm.threads";

    private static final String   PROP_METRIC_REG_JVM_FILES   = "jvm.files";

    private static final String   PROP_METRIC_REG_JVM_BUFFERS = "jvm.buffers";

    private MetricRegistry        metricRegistry              = new MetricRegistry();

    private HealthCheckRegistry   healthCheckRegistry         = new HealthCheckRegistry();

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    @Override
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    @Bean
    @Override
    public HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    @PostConstruct
    public void init() {
        LOGGER.debug("Registering JVM gauges");
        metricRegistry.register(PROP_METRIC_REG_JVM_MEMORY, new MemoryUsageGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_THREADS, new ThreadStatesGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_FILES, new FileDescriptorRatioGauge());
        metricRegistry.register(PROP_METRIC_REG_JVM_BUFFERS,
                new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        LOGGER.debug("Registered JVM gauges");
        if (applicationProperties.getMetrics().getJmx().isEnabled()) {
            LOGGER.debug("Initializing Metrics JMX reporting");
            JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
            jmxReporter.start();
            LOGGER.debug("Initialized Metrics JMX reporting");
        }

        if (applicationProperties.getMetrics().getLogs().isEnabled()) {
            LOGGER.info("Initializing Metrics Log reporting");
            Marker metricsMarker = MarkerFactory.getMarker("metrics");
            final Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry)
                    .outputTo(LoggerFactory.getLogger("metrics")).markWith(metricsMarker)
                    .convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
            reporter.start(applicationProperties.getMetrics().getLogs().getReportFrequency(), TimeUnit.SECONDS);
            LOGGER.info("Initialized Metrics Log reporting");
        }
    }
}
