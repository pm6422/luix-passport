package cn.luixtech.passport.server.aspect;

import cn.luixtech.passport.server.annotation.SchedulerExecutionLog;
import cn.luixtech.passport.server.domain.ScheduleExecutionLog;
import cn.luixtech.passport.server.repository.ScheduleExecutionLogRepository;
import cn.luixtech.passport.server.service.SchedulerLockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import net.javacrumbs.shedlock.support.Utils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import static cn.luixtech.passport.server.domain.ScheduleExecutionLog.*;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 1)// keep this aspect lower than ShedLock
public class ScheduleExecutionLogAspect {
    private final ScheduleExecutionLogRepository scheduleExecutionLogRepository;
    private final SchedulerLockService           schedulerLockService;

    @Around("@annotation(schedulerExecutionLog)")
    public Object logScheduleExecution(ProceedingJoinPoint joinPoint, SchedulerExecutionLog schedulerExecutionLog) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SchedulerLock schedulerLockAnnotation = method.getAnnotation(SchedulerLock.class);

        if (schedulerLockAnnotation != null && !isShedLockHeld(schedulerLockAnnotation.name())) {
            // if shed lock is not held, skip execution
            return null;
        }

        String scheduleName = !schedulerExecutionLog.name().isEmpty() ?
                schedulerExecutionLog.name() :
                joinPoint.getSignature().getName();

        ScheduleExecutionLog domain = new ScheduleExecutionLog();
        domain.setScheduleName(scheduleName);
        domain.setStartAt(Instant.now());
        domain.setStatus(STATUS_RUNNING);
        domain.setNode(Utils.getHostname());

        if (schedulerExecutionLog.logParameters()) {
            domain.setParameters(parseParameters(joinPoint));
        }

        try {
            // save initial record
            scheduleExecutionLogRepository.save(domain);

            // proceed to execute the target method
            Object result = joinPoint.proceed();

            domain.setStatus(STATUS_SUCCESS);
            domain.setEndAt(Instant.now());
            long millis = Duration.between(domain.getStartAt(), domain.getEndAt()).toMillis();
            domain.setDurationMs(millis);
            return result;
        } catch (Exception e) {
            domain.setStatus(STATUS_FAILURE);
            domain.setError(e.getMessage());
            domain.setEndAt(Instant.now());
            throw e;
        } finally {
            // update to final status
            scheduleExecutionLogRepository.save(domain);
        }
    }

    private boolean isShedLockHeld(String id) {
        return schedulerLockService.isLockHeld(id, Utils.getHostname());
    }

    private String parseParameters(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            return null;
        }
        try {
            return Arrays.stream(args)
                    .map(arg -> arg != null ? arg.toString() : "null")
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "parameter serialization failed";
        }
    }
}