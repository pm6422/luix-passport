/*
 * This file is generated by jOOQ.
 */
package cn.luixtech.passport.server.persistence.tables;


import cn.luixtech.passport.server.persistence.Keys;
import cn.luixtech.passport.server.persistence.Public;
import cn.luixtech.passport.server.persistence.tables.records.UserRoleRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
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
public class UserRole extends TableImpl<UserRoleRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.user_role</code>
     */
    public static final UserRole USER_ROLE = new UserRole();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserRoleRecord> getRecordType() {
        return UserRoleRecord.class;
    }

    /**
     * The column <code>public.user_role.id</code>.
     */
    public final TableField<UserRoleRecord, String> ID = createField(DSL.name("id"), SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>public.user_role.user_id</code>.
     */
    public final TableField<UserRoleRecord, String> USER_ID = createField(DSL.name("user_id"), SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>public.user_role.role</code>.
     */
    public final TableField<UserRoleRecord, String> ROLE = createField(DSL.name("role"), SQLDataType.VARCHAR(36).nullable(false), this, "");

    private UserRole(Name alias, Table<UserRoleRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserRole(Name alias, Table<UserRoleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.user_role</code> table reference
     */
    public UserRole(String alias) {
        this(DSL.name(alias), USER_ROLE);
    }

    /**
     * Create an aliased <code>public.user_role</code> table reference
     */
    public UserRole(Name alias) {
        this(alias, USER_ROLE);
    }

    /**
     * Create a <code>public.user_role</code> table reference
     */
    public UserRole() {
        this(DSL.name("user_role"), null);
    }

    public <O extends Record> UserRole(Table<O> child, ForeignKey<O, UserRoleRecord> key) {
        super(child, key, USER_ROLE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<UserRoleRecord> getPrimaryKey() {
        return Keys.USER_ROLE_PKEY;
    }

    @Override
    public List<ForeignKey<UserRoleRecord, ?>> getReferences() {
        return Arrays.asList(Keys.USER_ROLE__FK_USER_ROLE_USER_ID);
    }

    private transient User _user;

    /**
     * Get the implicit join path to the <code>public.user</code> table.
     */
    public User user() {
        if (_user == null)
            _user = new User(this, Keys.USER_ROLE__FK_USER_ROLE_USER_ID);

        return _user;
    }

    @Override
    public UserRole as(String alias) {
        return new UserRole(DSL.name(alias), this);
    }

    @Override
    public UserRole as(Name alias) {
        return new UserRole(alias, this);
    }

    @Override
    public UserRole as(Table<?> alias) {
        return new UserRole(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public UserRole rename(String name) {
        return new UserRole(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserRole rename(Name name) {
        return new UserRole(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserRole rename(Table<?> name) {
        return new UserRole(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super String, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
