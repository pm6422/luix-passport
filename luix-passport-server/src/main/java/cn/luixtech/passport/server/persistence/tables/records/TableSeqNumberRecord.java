/*
 * This file is generated by jOOQ.
 */
package cn.luixtech.passport.server.persistence.tables.records;


import cn.luixtech.passport.server.persistence.tables.TableSeqNumber;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TableSeqNumberRecord extends UpdatableRecordImpl<TableSeqNumberRecord> implements Record3<String, String, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.table_seq_number.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.table_seq_number.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.table_seq_number.table_name</code>.
     */
    public void setTableName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.table_seq_number.table_name</code>.
     */
    public String getTableName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.table_seq_number.max_seq_num</code>.
     */
    public void setMaxSeqNum(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.table_seq_number.max_seq_num</code>.
     */
    public Long getMaxSeqNum() {
        return (Long) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<String, String, Long> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return TableSeqNumber.TABLE_SEQ_NUMBER.ID;
    }

    @Override
    public Field<String> field2() {
        return TableSeqNumber.TABLE_SEQ_NUMBER.TABLE_NAME;
    }

    @Override
    public Field<Long> field3() {
        return TableSeqNumber.TABLE_SEQ_NUMBER.MAX_SEQ_NUM;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getTableName();
    }

    @Override
    public Long component3() {
        return getMaxSeqNum();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getTableName();
    }

    @Override
    public Long value3() {
        return getMaxSeqNum();
    }

    @Override
    public TableSeqNumberRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public TableSeqNumberRecord value2(String value) {
        setTableName(value);
        return this;
    }

    @Override
    public TableSeqNumberRecord value3(Long value) {
        setMaxSeqNum(value);
        return this;
    }

    @Override
    public TableSeqNumberRecord values(String value1, String value2, Long value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TableSeqNumberRecord
     */
    public TableSeqNumberRecord() {
        super(TableSeqNumber.TABLE_SEQ_NUMBER);
    }

    /**
     * Create a detached, initialised TableSeqNumberRecord
     */
    public TableSeqNumberRecord(String id, String tableName, Long maxSeqNum) {
        super(TableSeqNumber.TABLE_SEQ_NUMBER);

        setId(id);
        setTableName(tableName);
        setMaxSeqNum(maxSeqNum);
        resetChangedOnNotNull();
    }
}
