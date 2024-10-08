/*
 * This file is generated by jOOQ.
 */
package cn.luixtech.passport.server.persistence.tables.records;


import cn.luixtech.passport.server.persistence.tables.OrgUser;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class OrgUserRecord extends UpdatableRecordImpl<OrgUserRecord> implements Record3<String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.org_user.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.org_user.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.org_user.org_id</code>.
     */
    public void setOrgId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.org_user.org_id</code>.
     */
    public String getOrgId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.org_user.user_id</code>.
     */
    public void setUserId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.org_user.user_id</code>.
     */
    public String getUserId() {
        return (String) get(2);
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
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<String, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return OrgUser.ORG_USER.ID;
    }

    @Override
    public Field<String> field2() {
        return OrgUser.ORG_USER.ORG_ID;
    }

    @Override
    public Field<String> field3() {
        return OrgUser.ORG_USER.USER_ID;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getOrgId();
    }

    @Override
    public String component3() {
        return getUserId();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getOrgId();
    }

    @Override
    public String value3() {
        return getUserId();
    }

    @Override
    public OrgUserRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public OrgUserRecord value2(String value) {
        setOrgId(value);
        return this;
    }

    @Override
    public OrgUserRecord value3(String value) {
        setUserId(value);
        return this;
    }

    @Override
    public OrgUserRecord values(String value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OrgUserRecord
     */
    public OrgUserRecord() {
        super(OrgUser.ORG_USER);
    }

    /**
     * Create a detached, initialised OrgUserRecord
     */
    public OrgUserRecord(String id, String orgId, String userId) {
        super(OrgUser.ORG_USER);

        setId(id);
        setOrgId(orgId);
        setUserId(userId);
        resetChangedOnNotNull();
    }
}
