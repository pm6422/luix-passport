/*
 * This file is generated by jOOQ.
 */
package cn.luixtech.passport.server.persistence.tables;


import cn.luixtech.passport.server.persistence.Keys;
import cn.luixtech.passport.server.persistence.Public;
import cn.luixtech.passport.server.persistence.tables.records.SpringSessionRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function7;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row7;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class SpringSession extends TableImpl<SpringSessionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.spring_session</code>
     */
    public static final SpringSession SPRING_SESSION = new SpringSession();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SpringSessionRecord> getRecordType() {
        return SpringSessionRecord.class;
    }

    /**
     * The column <code>public.spring_session.primary_id</code>.
     */
    public final TableField<SpringSessionRecord, String> PRIMARY_ID = createField(DSL.name("primary_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>public.spring_session.session_id</code>.
     */
    public final TableField<SpringSessionRecord, String> SESSION_ID = createField(DSL.name("session_id"), SQLDataType.CHAR(36).nullable(false), this, "");

    /**
     * The column <code>public.spring_session.creation_time</code>.
     */
    public final TableField<SpringSessionRecord, Long> CREATION_TIME = createField(DSL.name("creation_time"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.spring_session.last_access_time</code>.
     */
    public final TableField<SpringSessionRecord, Long> LAST_ACCESS_TIME = createField(DSL.name("last_access_time"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.spring_session.max_inactive_interval</code>.
     */
    public final TableField<SpringSessionRecord, Integer> MAX_INACTIVE_INTERVAL = createField(DSL.name("max_inactive_interval"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.spring_session.expiry_time</code>.
     */
    public final TableField<SpringSessionRecord, Long> EXPIRY_TIME = createField(DSL.name("expiry_time"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.spring_session.principal_name</code>.
     */
    public final TableField<SpringSessionRecord, String> PRINCIPAL_NAME = createField(DSL.name("principal_name"), SQLDataType.VARCHAR(100), this, "");

    private SpringSession(Name alias, Table<SpringSessionRecord> aliased) {
        this(alias, aliased, null);
    }

    private SpringSession(Name alias, Table<SpringSessionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.spring_session</code> table reference
     */
    public SpringSession(String alias) {
        this(DSL.name(alias), SPRING_SESSION);
    }

    /**
     * Create an aliased <code>public.spring_session</code> table reference
     */
    public SpringSession(Name alias) {
        this(alias, SPRING_SESSION);
    }

    /**
     * Create a <code>public.spring_session</code> table reference
     */
    public SpringSession() {
        this(DSL.name("spring_session"), null);
    }

    public <O extends Record> SpringSession(Table<O> child, ForeignKey<O, SpringSessionRecord> key) {
        super(child, key, SPRING_SESSION);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<SpringSessionRecord> getPrimaryKey() {
        return Keys.SPRING_SESSION_PKEY;
    }

    @Override
    public List<UniqueKey<SpringSessionRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.SPRING_SESSION_SESSION_ID_KEY);
    }

    @Override
    public SpringSession as(String alias) {
        return new SpringSession(DSL.name(alias), this);
    }

    @Override
    public SpringSession as(Name alias) {
        return new SpringSession(alias, this);
    }

    @Override
    public SpringSession as(Table<?> alias) {
        return new SpringSession(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public SpringSession rename(String name) {
        return new SpringSession(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SpringSession rename(Name name) {
        return new SpringSession(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public SpringSession rename(Table<?> name) {
        return new SpringSession(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<String, String, Long, Long, Integer, Long, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function7<? super String, ? super String, ? super Long, ? super Long, ? super Integer, ? super Long, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function7<? super String, ? super String, ? super Long, ? super Long, ? super Integer, ? super Long, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
