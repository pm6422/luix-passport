package org.infinity.passport.config.dbmigrations;//package org.infinity.passport.setup;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.infinity.passport.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Creates the initial database
 */
@ChangeUnit(id = "InitialSetupMigration", order = "01")
public class InitialSetupMigration {

    private static final String          APP_NAME       = "passport-server";
    public static final  String          USERNAME       = "louis";
    public static final  String          PASSWORD       = "louis";
    private static final String          MENU_PARENT_ID = "0";
    private final        MongoTemplate   mongoTemplate;
    private final        PasswordEncoder passwordEncoder;

    public InitialSetupMigration(MongoTemplate mongoTemplate,
                                 PasswordEncoder passwordEncoder) {
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Execution
    public void execute() {
        addApps();
        addAuthorities();
        addUserAndAuthorities();
        addAuthorityAdminMenu();
        addOAuth2Client();
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.getDb().drop();
    }

    public void addApps() {
        App app = new App(APP_NAME, true);
        mongoTemplate.save(app);
    }

    public void addAuthorities() {
        mongoTemplate.save(new Authority(Authority.USER, true, true));
        mongoTemplate.save(new Authority(Authority.ADMIN, true, true));
        mongoTemplate.save(new Authority(Authority.DEVELOPER, true, true));
        mongoTemplate.save(new Authority(Authority.ANONYMOUS, true, true));

        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.USER));
        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.ADMIN));
        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.DEVELOPER));
        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.ANONYMOUS));
    }

    public void addUserAndAuthorities() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Creates 'user' user and corresponding authorities
        User userRoleUser = new User();
        userRoleUser.setUserName("user");
        userRoleUser.setFirstName("User");
        userRoleUser.setLastName("User");
        userRoleUser.setEmail("user@localhost");
        userRoleUser.setMobileNo("15000899479");
        // Raw password: user
        userRoleUser.setPasswordHash(passwordEncoder.encode("user"));
        userRoleUser.setActivated(true);
        userRoleUser.setActivationKey(null);
        userRoleUser.setResetKey(null);
        userRoleUser.setResetTime(null);
        userRoleUser.setEnabled(true);
        mongoTemplate.save(userRoleUser);

        mongoTemplate.save(new UserAuthority(userRoleUser.getId(), Authority.USER));

        // Creates 'admin' user and corresponding authorities
        User adminRoleUser = new User();
        adminRoleUser.setUserName("admin");
        adminRoleUser.setFirstName("Admin");
        adminRoleUser.setLastName("Admin");
        adminRoleUser.setEmail("admin@localhost");
        adminRoleUser.setMobileNo("15000899477");
        // Raw password: admin
        adminRoleUser.setPasswordHash(passwordEncoder.encode("admin"));
        adminRoleUser.setActivated(true);
        adminRoleUser.setActivationKey(null);
        adminRoleUser.setResetKey(null);
        adminRoleUser.setResetTime(null);
        adminRoleUser.setEnabled(true);
        mongoTemplate.save(adminRoleUser);

        mongoTemplate.save(new UserAuthority(adminRoleUser.getId(), Authority.USER));
        mongoTemplate.save(new UserAuthority(adminRoleUser.getId(), Authority.ADMIN));

        // Creates 'system' user and corresponding authorities
        User adminRoleSystemUser = new User();
        adminRoleSystemUser.setUserName("system");
        adminRoleSystemUser.setFirstName("System");
        adminRoleSystemUser.setLastName("System");
        adminRoleSystemUser.setEmail("system@localhost");
        adminRoleSystemUser.setMobileNo("15000899422");
        // Raw password: system
        adminRoleSystemUser.setPasswordHash(passwordEncoder.encode("system"));
        adminRoleSystemUser.setActivated(true);
        adminRoleSystemUser.setActivationKey(null);
        adminRoleSystemUser.setResetKey(null);
        adminRoleSystemUser.setResetTime(null);
        adminRoleSystemUser.setEnabled(true);
        mongoTemplate.save(adminRoleSystemUser);

        mongoTemplate.save(new UserAuthority(adminRoleSystemUser.getId(), Authority.USER));
        mongoTemplate.save(new UserAuthority(adminRoleSystemUser.getId(), Authority.ADMIN));

        // Creates 'louis' user and corresponding authorities
        User developerRoleUser = new User();
        developerRoleUser.setUserName(USERNAME);
        developerRoleUser.setFirstName("Louis");
        developerRoleUser.setLastName("Lau");
        developerRoleUser.setEmail("louis@luixtech.com");
        developerRoleUser.setMobileNo("15000899488");
        // Raw password: louis
        developerRoleUser.setPasswordHash(passwordEncoder.encode(PASSWORD));
        developerRoleUser.setActivated(true);
        developerRoleUser.setActivationKey(null);
        developerRoleUser.setResetKey(null);
        developerRoleUser.setResetTime(null);
        developerRoleUser.setEnabled(true);
        mongoTemplate.save(developerRoleUser);

        mongoTemplate.save(new UserAuthority(developerRoleUser.getId(), Authority.USER));
        mongoTemplate.save(new UserAuthority(developerRoleUser.getId(), Authority.ADMIN));
        mongoTemplate.save(new UserAuthority(developerRoleUser.getId(), Authority.DEVELOPER));
    }

    public void addAuthorityAdminMenu() {
        AdminMenu userAuthority = new AdminMenu(APP_NAME, "user-authority", "用户权限", 1, "user-authority", 100, MENU_PARENT_ID);
        mongoTemplate.save(userAuthority);

        AdminMenu authorityList = new AdminMenu(APP_NAME, "authority-list", "权限", 2, "user-authority.authority-list",
                101, userAuthority.getId());
        mongoTemplate.save(authorityList);

        AdminMenu userList = new AdminMenu(APP_NAME, "user-list", "用户", 2, "user-authority.user-list", 102,
                userAuthority.getId());
        mongoTemplate.save(userList);

        AdminMenu app = new AdminMenu(APP_NAME, "app", "应用系统", 1, "app", 200, MENU_PARENT_ID);
        mongoTemplate.save(app);

        AdminMenu appList = new AdminMenu(APP_NAME, "app-list", "应用", 2, "app.app-list", 201, app.getId());
        mongoTemplate.save(appList);

        AdminMenu adminMenuAuthority = new AdminMenu(APP_NAME, "admin-menu-authority", "菜单权限", 1,
                "admin-menu-authority", 300, MENU_PARENT_ID);
        mongoTemplate.save(adminMenuAuthority);

        AdminMenu adminMenuList = new AdminMenu(APP_NAME, "admin-menu-list", "管理菜单", 2,
                "admin-menu-authority.admin-menu-list", 301, adminMenuAuthority.getId());
        mongoTemplate.save(adminMenuList);

        AdminMenu authorityAdminMenu = new AdminMenu(APP_NAME, "authority-admin-menu", "权限管理菜单", 2,
                "admin-menu-authority.authority-admin-menu", 302, adminMenuAuthority.getId());
        mongoTemplate.save(authorityAdminMenu);

        AdminMenu security = new AdminMenu(APP_NAME, "security", "安全信息", 1, "security", 400, MENU_PARENT_ID);
        mongoTemplate.save(security);

        AdminMenu oAuth2ClientDetails = new AdminMenu(APP_NAME, "oauth2-client-list", "单点登录客户端", 2,
                "security.oauth2-client-list", 401, security.getId());
        mongoTemplate.save(oAuth2ClientDetails);

        AdminMenu oAuth2AccessTokenDetails = new AdminMenu(APP_NAME, "oauth2-access-token-list", "访问令牌", 2,
                "security.oauth2-access-token-list", 402, security.getId());
        mongoTemplate.save(oAuth2AccessTokenDetails);

        AdminMenu oAuth2RefreshTokenDetails = new AdminMenu(APP_NAME, "oauth2-refresh-token-list", "刷新令牌", 2,
                "security.oauth2-refresh-token-list", 403, security.getId());
        mongoTemplate.save(oAuth2RefreshTokenDetails);

        AdminMenu oAuth2ApprovalDetails = new AdminMenu(APP_NAME, "oauth2-approval-list", "登录授权", 2,
                "security.oauth2-approval-list", 404, security.getId());
        mongoTemplate.save(oAuth2ApprovalDetails);

        //AuthorityAdminMenu
        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, userAuthority.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, authorityList.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, userList.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, app.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, appList.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, adminMenuAuthority.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, adminMenuList.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, authorityAdminMenu.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, security.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, oAuth2ClientDetails.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, oAuth2AccessTokenDetails.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, oAuth2RefreshTokenDetails.getId()));

        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, oAuth2ApprovalDetails.getId()));
    }

    public void addOAuth2Client() {
        OAuth2Client oAuth2Client1 = new OAuth2Client();
        oAuth2Client1.setClientId(OAuth2Client.AUTH_CODE_CLIENT_ID);
        oAuth2Client1.setClientSecret(passwordEncoder.encode(OAuth2Client.INTERNAL_RAW_CLIENT_SECRET));
        oAuth2Client1.setClientAuthenticationMethods(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue()));
        oAuth2Client1.setAuthorizationGrantTypes(
                // 最安全的流程，需要用户的参与
                Set.of(AuthorizationGrantType.AUTHORIZATION_CODE.getValue(),
                        AuthorizationGrantType.REFRESH_TOKEN.getValue()));
        // Note: localhost and 127.0.0.1 must be save twice.
        oAuth2Client1.setRedirectUris(Set.of("http://127.0.0.1:9020", "http://localhost:9020", "https://www.baidu.com"));
        oAuth2Client1.setScopes(Set.of(OAuth2Client.OAuth2Scope.builder().scope(OidcScopes.OPENID).description("openid").build(),
                OAuth2Client.OAuth2Scope.builder().scope(OidcScopes.PROFILE).description("profile").build()));
        OAuth2Client.OAuth2ClientSettings oAuth2ClientSettings = new OAuth2Client.OAuth2ClientSettings();
        oAuth2ClientSettings.setRequireAuthorizationConsent(true);
        oAuth2Client1.setOAuth2ClientSettings(oAuth2ClientSettings);

        oAuth2Client1.setClientIdIssuedAt(Instant.now());
        oAuth2Client1.setClientSecretExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        mongoTemplate.save(oAuth2Client1);

        OAuth2Client oAuth2Client2 = new OAuth2Client();
        oAuth2Client2.setClientId(OAuth2Client.INTERNAL_CLIENT_ID);
        oAuth2Client2.setClientSecret(passwordEncoder.encode(OAuth2Client.INTERNAL_RAW_CLIENT_SECRET));
        oAuth2Client2.setClientAuthenticationMethods(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue()));
        oAuth2Client2.setAuthorizationGrantTypes(
                // 全局只有一个账号密码，使用这一个账号便可以访问资源，一般用于内部系统间调用
                Set.of(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(),
                        // 每个用户有不同的账号密码，每个用户可以使用自己的账号访问资源
                        AuthorizationGrantType.PASSWORD.getValue(),
                        // 根据refresh token可以重新生成access token
                        AuthorizationGrantType.REFRESH_TOKEN.getValue()));
        oAuth2Client2.setScopes(Set.of(OAuth2Client.OAuth2Scope.builder().scope(OidcScopes.OPENID).description("openid").build(),
                OAuth2Client.OAuth2Scope.builder().scope("message:read").description("read").build(),
                OAuth2Client.OAuth2Scope.builder().scope("message:write").description("write").build()));
        oAuth2Client2.setClientIdIssuedAt(Instant.now());
        oAuth2Client2.setClientSecretExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        mongoTemplate.save(oAuth2Client2);
    }
}
