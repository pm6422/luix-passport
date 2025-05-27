package cn.luixtech.passport.server.service.impl;


import cn.luixtech.passport.server.domain.DataDict;
import cn.luixtech.passport.server.domain.TableSeqNumber;
import cn.luixtech.passport.server.persistence.Tables;
import cn.luixtech.passport.server.repository.TableSeqNumberRepository;
import cn.luixtech.passport.server.service.TableSeqNumberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TableSeqNumberServiceImpl implements TableSeqNumberService {
    private final DSLContext               dslContext;
    private final TableSeqNumberRepository tableSeqNumberRepository;

    @Override
    public void init() {
        upsertSeqNumber(Tables.DATA_DICT.getName());
    }

    @Override
    public long getNextSeqNumber(String table) {
        Optional<TableSeqNumber> tableSeqNumber = tableSeqNumberRepository.findOneByTableName(table);
        if (tableSeqNumber.isEmpty()) {
            throw new IllegalStateException("Failed to get the table seq number");
        }
        tableSeqNumber.get().setMaxSeqNum(tableSeqNumber.get().getMaxSeqNum() + 1);
        tableSeqNumberRepository.save(tableSeqNumber.get());
        return tableSeqNumber.get().getMaxSeqNum();
    }

    @Transactional(rollbackFor = Exception.class)
    protected void upsertSeqNumber(String table) {
        String maxNumberStr = getMaxNumberStr(table);

        Optional<TableSeqNumber> existingOne = tableSeqNumberRepository.findOneByTableName(table);
        if (existingOne.isEmpty()) {
            // insert
            TableSeqNumber newOne = new TableSeqNumber();
            newOne.setTableName(table);
            newOne.setMaxSeqNum(StringUtils.isEmpty(maxNumberStr) ? 0L : Long.parseLong(maxNumberStr.substring(3)));
            tableSeqNumberRepository.save(newOne);
            return;
        }

        // update
        long maxValue = Long.parseLong(maxNumberStr.substring(3));
        log.info("Current table {}'s max number is {}", table, maxValue);

        existingOne.get().setMaxSeqNum(maxValue);
        tableSeqNumberRepository.save(existingOne.get());
    }

    private String getMaxNumberStr(String table) {
        Record exsitingRecord = dslContext.selectFrom(table)
                .orderBy(DSL.field("num desc"))
                .limit(1)
                .fetchOne();
        return exsitingRecord == null ? null : (String) exsitingRecord.get(DSL.field("num"));
    }
}
