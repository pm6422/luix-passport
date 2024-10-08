/*
 * This file is generated by jOOQ.
 */
package cn.luixtech.passport.server.persistence;


import cn.luixtech.passport.server.persistence.tables.DataDict;
import cn.luixtech.passport.server.persistence.tables.Oauth2Authorization;
import cn.luixtech.passport.server.persistence.tables.Oauth2AuthorizationConsent;
import cn.luixtech.passport.server.persistence.tables.Oauth2RegisteredClient;
import cn.luixtech.passport.server.persistence.tables.Org;
import cn.luixtech.passport.server.persistence.tables.OrgUser;
import cn.luixtech.passport.server.persistence.tables.SpringSession;
import cn.luixtech.passport.server.persistence.tables.SpringSessionAttributes;
import cn.luixtech.passport.server.persistence.tables.TableSeqNumber;
import cn.luixtech.passport.server.persistence.tables.User;
import cn.luixtech.passport.server.persistence.tables.UserAuthEvent;
import cn.luixtech.passport.server.persistence.tables.UserLogin;
import cn.luixtech.passport.server.persistence.tables.UserPermission;
import cn.luixtech.passport.server.persistence.tables.UserProfilePic;
import cn.luixtech.passport.server.persistence.tables.UserRole;
import cn.luixtech.passport.server.persistence.tables.records.DataDictRecord;
import cn.luixtech.passport.server.persistence.tables.records.Oauth2AuthorizationConsentRecord;
import cn.luixtech.passport.server.persistence.tables.records.Oauth2AuthorizationRecord;
import cn.luixtech.passport.server.persistence.tables.records.Oauth2RegisteredClientRecord;
import cn.luixtech.passport.server.persistence.tables.records.OrgRecord;
import cn.luixtech.passport.server.persistence.tables.records.OrgUserRecord;
import cn.luixtech.passport.server.persistence.tables.records.SpringSessionAttributesRecord;
import cn.luixtech.passport.server.persistence.tables.records.SpringSessionRecord;
import cn.luixtech.passport.server.persistence.tables.records.TableSeqNumberRecord;
import cn.luixtech.passport.server.persistence.tables.records.UserAuthEventRecord;
import cn.luixtech.passport.server.persistence.tables.records.UserLoginRecord;
import cn.luixtech.passport.server.persistence.tables.records.UserPermissionRecord;
import cn.luixtech.passport.server.persistence.tables.records.UserProfilePicRecord;
import cn.luixtech.passport.server.persistence.tables.records.UserRecord;
import cn.luixtech.passport.server.persistence.tables.records.UserRoleRecord;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<DataDictRecord> DATA_DICT_PKEY = Internal.createUniqueKey(DataDict.DATA_DICT, DSL.name("data_dict_pkey"), new TableField[] { DataDict.DATA_DICT.ID }, true);
    public static final UniqueKey<DataDictRecord> UK_DATA_DICT_CATE_CODE_DICT_CODE = Internal.createUniqueKey(DataDict.DATA_DICT, DSL.name("uk_data_dict_cate_code_dict_code"), new TableField[] { DataDict.DATA_DICT.CATEGORY_CODE, DataDict.DATA_DICT.DICT_CODE }, true);
    public static final UniqueKey<Oauth2AuthorizationRecord> OAUTH2_AUTHORIZATION_PKEY = Internal.createUniqueKey(Oauth2Authorization.OAUTH2_AUTHORIZATION, DSL.name("oauth2_authorization_pkey"), new TableField[] { Oauth2Authorization.OAUTH2_AUTHORIZATION.ID }, true);
    public static final UniqueKey<Oauth2AuthorizationConsentRecord> OAUTH2_AUTHORIZATION_CONSENT_PKEY = Internal.createUniqueKey(Oauth2AuthorizationConsent.OAUTH2_AUTHORIZATION_CONSENT, DSL.name("oauth2_authorization_consent_pkey"), new TableField[] { Oauth2AuthorizationConsent.OAUTH2_AUTHORIZATION_CONSENT.REGISTERED_CLIENT_ID, Oauth2AuthorizationConsent.OAUTH2_AUTHORIZATION_CONSENT.PRINCIPAL_NAME }, true);
    public static final UniqueKey<Oauth2RegisteredClientRecord> OAUTH2_REGISTERED_CLIENT_PKEY = Internal.createUniqueKey(Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT, DSL.name("oauth2_registered_client_pkey"), new TableField[] { Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT.ID }, true);
    public static final UniqueKey<OrgRecord> ORG_PKEY = Internal.createUniqueKey(Org.ORG, DSL.name("org_pkey"), new TableField[] { Org.ORG.ID }, true);
    public static final UniqueKey<OrgUserRecord> ORG_USER_PKEY = Internal.createUniqueKey(OrgUser.ORG_USER, DSL.name("org_user_pkey"), new TableField[] { OrgUser.ORG_USER.ID }, true);
    public static final UniqueKey<SpringSessionRecord> SPRING_SESSION_PKEY = Internal.createUniqueKey(SpringSession.SPRING_SESSION, DSL.name("spring_session_pkey"), new TableField[] { SpringSession.SPRING_SESSION.PRIMARY_ID }, true);
    public static final UniqueKey<SpringSessionRecord> SPRING_SESSION_SESSION_ID_KEY = Internal.createUniqueKey(SpringSession.SPRING_SESSION, DSL.name("spring_session_session_id_key"), new TableField[] { SpringSession.SPRING_SESSION.SESSION_ID }, true);
    public static final UniqueKey<SpringSessionAttributesRecord> SPRING_SESSION_ATTRIBUTES_PKEY = Internal.createUniqueKey(SpringSessionAttributes.SPRING_SESSION_ATTRIBUTES, DSL.name("spring_session_attributes_pkey"), new TableField[] { SpringSessionAttributes.SPRING_SESSION_ATTRIBUTES.SESSION_PRIMARY_ID, SpringSessionAttributes.SPRING_SESSION_ATTRIBUTES.ATTRIBUTE_NAME }, true);
    public static final UniqueKey<TableSeqNumberRecord> TABLE_SEQ_NUMBER_PKEY = Internal.createUniqueKey(TableSeqNumber.TABLE_SEQ_NUMBER, DSL.name("table_seq_number_pkey"), new TableField[] { TableSeqNumber.TABLE_SEQ_NUMBER.ID }, true);
    public static final UniqueKey<UserRecord> USER_EMAIL_KEY = Internal.createUniqueKey(User.USER, DSL.name("user_email_key"), new TableField[] { User.USER.EMAIL }, true);
    public static final UniqueKey<UserRecord> USER_MOBILE_NO_KEY = Internal.createUniqueKey(User.USER, DSL.name("user_mobile_no_key"), new TableField[] { User.USER.MOBILE_NO }, true);
    public static final UniqueKey<UserRecord> USER_PKEY = Internal.createUniqueKey(User.USER, DSL.name("user_pkey"), new TableField[] { User.USER.ID }, true);
    public static final UniqueKey<UserRecord> USER_USERNAME_KEY = Internal.createUniqueKey(User.USER, DSL.name("user_username_key"), new TableField[] { User.USER.USERNAME }, true);
    public static final UniqueKey<UserAuthEventRecord> USER_AUTH_EVENT_PKEY = Internal.createUniqueKey(UserAuthEvent.USER_AUTH_EVENT, DSL.name("user_auth_event_pkey"), new TableField[] { UserAuthEvent.USER_AUTH_EVENT.ID }, true);
    public static final UniqueKey<UserLoginRecord> USER_LOGIN_PKEY = Internal.createUniqueKey(UserLogin.USER_LOGIN, DSL.name("user_login_pkey"), new TableField[] { UserLogin.USER_LOGIN.ID }, true);
    public static final UniqueKey<UserPermissionRecord> USER_PERMISSION_PKEY = Internal.createUniqueKey(UserPermission.USER_PERMISSION, DSL.name("user_permission_pkey"), new TableField[] { UserPermission.USER_PERMISSION.ID }, true);
    public static final UniqueKey<UserProfilePicRecord> USER_PROFILE_PIC_PKEY = Internal.createUniqueKey(UserProfilePic.USER_PROFILE_PIC, DSL.name("user_profile_pic_pkey"), new TableField[] { UserProfilePic.USER_PROFILE_PIC.ID }, true);
    public static final UniqueKey<UserRoleRecord> USER_ROLE_PKEY = Internal.createUniqueKey(UserRole.USER_ROLE, DSL.name("user_role_pkey"), new TableField[] { UserRole.USER_ROLE.ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<OrgUserRecord, UserRecord> ORG_USER__FK_ORG_USER_USER_ID = Internal.createForeignKey(OrgUser.ORG_USER, DSL.name("fk_org_user_user_id"), new TableField[] { OrgUser.ORG_USER.USER_ID }, Keys.USER_PKEY, new TableField[] { User.USER.ID }, true);
    public static final ForeignKey<UserLoginRecord, UserRecord> USER_LOGIN__FK_USER_LOGIN_USER_ID = Internal.createForeignKey(UserLogin.USER_LOGIN, DSL.name("fk_user_login_user_id"), new TableField[] { UserLogin.USER_LOGIN.USER_ID }, Keys.USER_PKEY, new TableField[] { User.USER.ID }, true);
    public static final ForeignKey<UserPermissionRecord, UserRecord> USER_PERMISSION__FK_USER_PERMISSION_USER_ID = Internal.createForeignKey(UserPermission.USER_PERMISSION, DSL.name("fk_user_permission_user_id"), new TableField[] { UserPermission.USER_PERMISSION.USER_ID }, Keys.USER_PKEY, new TableField[] { User.USER.ID }, true);
    public static final ForeignKey<UserProfilePicRecord, UserRecord> USER_PROFILE_PIC__FK_USER_PROFILE_PIC_USER_ID = Internal.createForeignKey(UserProfilePic.USER_PROFILE_PIC, DSL.name("fk_user_profile_pic_user_id"), new TableField[] { UserProfilePic.USER_PROFILE_PIC.ID }, Keys.USER_PKEY, new TableField[] { User.USER.ID }, true);
    public static final ForeignKey<UserRoleRecord, UserRecord> USER_ROLE__FK_USER_ROLE_USER_ID = Internal.createForeignKey(UserRole.USER_ROLE, DSL.name("fk_user_role_user_id"), new TableField[] { UserRole.USER_ROLE.USER_ID }, Keys.USER_PKEY, new TableField[] { User.USER.ID }, true);
}
