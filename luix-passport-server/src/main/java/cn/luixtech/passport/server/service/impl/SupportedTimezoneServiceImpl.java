package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.repository.SupportedTimezoneRepository;
import cn.luixtech.passport.server.service.SupportedTimezoneService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
@AllArgsConstructor
@Slf4j
public class SupportedTimezoneServiceImpl implements SupportedTimezoneService {
    private final SupportedTimezoneRepository supportedTimezoneRepository;

    @Override
    public void updateUtcOffset() {
        supportedTimezoneRepository.findAll().forEach(timezone -> {
            try {
                // 获取时区
                ZoneId zoneId = ZoneId.of(timezone.getId());
                // 计算当前时间的 UTC 偏移量
                OffsetDateTime now = OffsetDateTime.now(zoneId);
                String utcOffset = formatOffset(now.getOffset().getTotalSeconds());
                // 更新数据库
                timezone.setUtcOffset(utcOffset);
                supportedTimezoneRepository.save(timezone);
            } catch (Exception e) {
                log.error("Failed to update offset for timezone: {}", timezone.getId());
            }
        });
    }

    private String formatOffset(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = Math.abs(totalSeconds % 3600) / 60;
        return String.format("%s%02d:%02d", hours >= 0 ? "+" : "-", Math.abs(hours), minutes);
    }
}
