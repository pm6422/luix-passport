package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.DataDict;
import cn.luixtech.passport.server.persistence.Tables;
import cn.luixtech.passport.server.repository.DataDictRepository;
import cn.luixtech.passport.server.service.DataDictService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import static cn.luixtech.passport.server.domain.DataDict.CATEGORY_CODE_TIMEZONE;
import static cn.luixtech.passport.server.persistence.tables.DataDict.DATA_DICT;


@Service
@AllArgsConstructor
@Slf4j
public class DataDictServiceImpl implements DataDictService {
    private final DSLContext         dslContext;
    private final DataDictRepository dataDictRepository;

    @Override
    public Page<DataDict> find(Pageable pageable, String num, String categoryCode, Boolean enabled) {
        // Ignore a query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        DataDict criteria = new DataDict();
        criteria.setNum(num);
        criteria.setCategoryCode(categoryCode);
        criteria.setEnabled(enabled);
        Example<DataDict> queryExample = Example.of(criteria, matcher);
        return dataDictRepository.findAll(queryExample, pageable);
    }

    @Override
    public void batchUpdateCategoryCode(List<String> ids, String targetCategoryCode) {
        dslContext.update(Tables.DATA_DICT)
                .set(DATA_DICT.CATEGORY_CODE, targetCategoryCode)
                .where(DATA_DICT.ID.in(ids))
                .execute();
    }

    @Override
    public List<DataDict> findAllTimezone() {
        List<DataDict> timezones = dataDictRepository.findByCategoryCodeAndEnabledIsTrue(CATEGORY_CODE_TIMEZONE);
        return timezones.stream()
                .sorted(Comparator.comparingDouble(timezone -> parseUtcOffsetToHours(timezone.getDictName())))
                .toList();
    }

    /**
     * Execute every day at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Override
    public void updateTimezoneUtcOffset() {
        dataDictRepository.findByCategoryCode(CATEGORY_CODE_TIMEZONE).forEach(timezone -> {
            try {
                // 更新数据库
                timezone.setDictName(getOffset(ZoneId.of(timezone.getId())));
                dataDictRepository.save(timezone);
            } catch (Exception e) {
                log.error("Failed to update offset for timezone: {}", timezone.getId());
            }
        });
    }

    private double parseUtcOffsetToHours(String utcOffset) {
        if (utcOffset == null || !utcOffset.matches("[+-]\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Invalid utcOffset format: " + utcOffset);
        }

        String sign = utcOffset.substring(0, 1);
        String[] parts = utcOffset.substring(1).split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        double offsetHours = hours + (minutes / 60.0);
        return sign.equals("+") ? offsetHours : -offsetHours;
    }

    private String getOffset(ZoneId zoneId) {
        // 计算当前时间的 UTC 偏移量
        OffsetDateTime now = OffsetDateTime.now(zoneId);
        return formatOffset(now.getOffset().getTotalSeconds());
    }

    private String formatOffset(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = Math.abs(totalSeconds % 3600) / 60;
        return String.format("%s%02d:%02d", hours >= 0 ? "+" : "-", Math.abs(hours), minutes);
    }
}
