package cn.luixtech.passport.server.aspect;

import cn.luixtech.passport.server.annotation.SchedulerExecutionLog;
import cn.luixtech.passport.server.domain.ScheduleExecutionLog;
import cn.luixtech.passport.server.repository.ScheduleExecutionLogRepository;
import com.luixtech.utilities.network.AddressUtils;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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
public class ScheduleExecutionLogAspect {
    private final Environment                    env;
    private final ScheduleExecutionLogRepository scheduleExecutionLogRepository;

    @Around("@annotation(schedulerExecutionLog)")
    public Object logScheduleExecution(ProceedingJoinPoint joinPoint, SchedulerExecutionLog schedulerExecutionLog) throws Throwable {
        // 如果启用了ShedLock集成且当前未持有锁，则直接跳过
        if (schedulerExecutionLog.integrateWithShedLock() && !isShedLockHeld(joinPoint)) {
            return null;
        }

        String taskName = !schedulerExecutionLog.taskName().isEmpty() ?
                schedulerExecutionLog.taskName() :
                joinPoint.getSignature().getName();

        ScheduleExecutionLog domain = new ScheduleExecutionLog();
        domain.setTaskName(taskName);
        domain.setStartTime(Instant.now());
        domain.setStatus(STATUS_RUNNING);
        domain.setNodeIp(AddressUtils.getIntranetIp());
        domain.setPriority(schedulerExecutionLog.priority().name());

        if (schedulerExecutionLog.integrateWithShedLock()) {
            domain.setLockId(getShedLockId(joinPoint));
        }

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
            domain.setDurationMs(calculateDuration(domain));
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

    // 检查ShedLock是否已被当前节点获取
    private boolean isShedLockHeld(ProceedingJoinPoint joinPoint) {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            SchedulerLock lockAnnotation = method.getAnnotation(SchedulerLock.class);
            if (lockAnnotation == null) {
                return true;
            }
            // 查询数据库中的锁记录
            return scheduleExecutionLogRepository.existsByLockIdAndStatus(getShedLockId(joinPoint), STATUS_RUNNING);
        } catch (Exception e) {
            return false;
        }
    }

    private String getShedLockId(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SchedulerLock lockAnnotation = method.getAnnotation(SchedulerLock.class);
        if (lockAnnotation == null) {
            return null;
        }

        String lockedBy = env.getProperty("spring.application.name", "unknown")
                + "-" + AddressUtils.getIntranetIp();
        return lockAnnotation.name() + "_" + lockedBy;
    }

    private String parseParameters(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) return null;

        try {
            return Arrays.stream(args)
                    .map(arg -> arg != null ? arg.toString() : "null")
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "parameter serialization failed";
        }
    }

    private long calculateDuration(ScheduleExecutionLog log) {
        return Duration.between(log.getStartTime(), log.getEndTime()).toMillis();
    }
}