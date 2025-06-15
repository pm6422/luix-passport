package cn.luixtech.passport.server.aspect;

import cn.luixtech.passport.server.annotation.SchedulerExecutionLog;
import cn.luixtech.passport.server.domain.ScheduleExecutionLog;
import cn.luixtech.passport.server.repository.ScheduleExecutionLogRepository;
import com.luixtech.utilities.network.AddressUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
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
@Order(Ordered.LOWEST_PRECEDENCE - 1) // 确保比ShedLock的切面优先级低
public class ScheduleExecutionLogAspect {
    private final Environment                    env;
    private final ScheduleExecutionLogRepository scheduleExecutionLogRepository;
    private final LockProvider                   lockProvider;

    @Around("@annotation(schedulerExecutionLog)")
    public Object logScheduleExecution(ProceedingJoinPoint joinPoint, SchedulerExecutionLog schedulerExecutionLog) throws Throwable {
        if (schedulerExecutionLog.integrateWithShedLock() && !isShedLockHeld(joinPoint)) {
            // 如果启用了ShedLock集成且当前未持有锁，则直接跳过
            return null;
        }

        String scheduleName = !schedulerExecutionLog.name().isEmpty() ?
                schedulerExecutionLog.name() :
                joinPoint.getSignature().getName();

        ScheduleExecutionLog domain = new ScheduleExecutionLog();
        domain.setScheduleName(scheduleName);
        domain.setStartTime(Instant.now());
        domain.setStatus(STATUS_RUNNING);
        domain.setNodeIp(AddressUtils.getIntranetIp());
        domain.setPriority(schedulerExecutionLog.priority().name());

        if (schedulerExecutionLog.logParameters()) {
            domain.setParameters(parseParameters(joinPoint));
        }

        try {
            // save initial record
            scheduleExecutionLogRepository.save(domain);

            // proceed to execute the target method
            Object result = joinPoint.proceed();

            domain.setStatus(STATUS_SUCCESS);
            domain.setEndTime(Instant.now());
            long millis = Duration.between(domain.getStartTime(), domain.getEndTime()).toMillis();
            domain.setDurationMs(millis);
            return result;
        } catch (Exception e) {
            domain.setStatus(STATUS_FAILURE);
            domain.setMessage(e.getMessage());
            domain.setEndTime(Instant.now());
            throw e;
        } finally {
            // update to final status
            scheduleExecutionLogRepository.save(domain);
        }
    }

    private boolean isShedLockHeld(ProceedingJoinPoint joinPoint) {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            SchedulerLock lockAnnotation = method.getAnnotation(SchedulerLock.class);
            if (lockAnnotation == null) {
                return true;
            }

            // 构建与ShedLock相同的锁配置
            LockConfiguration config = new LockConfiguration(
                    Instant.now(),
                    lockAnnotation.name(),
                    parseDuration(lockAnnotation.lockAtMostFor()),
                    parseDuration(lockAnnotation.lockAtLeastFor())
            );

            // 直接通过LockProvider检查锁状态
            return lockProvider.lock(config).isPresent();
        } catch (Exception e) {
            log.error("Check lock failed", e);
            return false;
        }
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

    private Duration parseDuration(String durationStr) {
        try {
            return Duration.parse("PT" + durationStr.toUpperCase());
        } catch (Exception e) {
            // 默认10分钟
            return Duration.ofMinutes(10);
        }
    }
}