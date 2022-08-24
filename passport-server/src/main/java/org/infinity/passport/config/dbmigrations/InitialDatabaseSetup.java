package org.infinity.passport.config.dbmigrations;

import lombok.AllArgsConstructor;
import org.infinity.passport.domain.oauth2.*;
import org.infinity.passport.repository.oauth2.OAuth2ClientRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Creates the initial database
 */
@Component
@AllArgsConstructor
public class InitialDatabaseSetup implements ApplicationRunner {

    public static final  String                 USERNAME       = "louis";
    public static final  String                 PASSWORD       = "louis";
    private static final String                 APP_NAME       = "passport-server";
    private static final String                 MENU_PARENT_ID = "0";
    private              PasswordEncoder        passwordEncoder;
    private              OAuth2ClientRepository oauth2ClientRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        addApps();
//        addAuthorities();
//        addUserAndAuthorities();
//        addAuthorityAdminMenu();
//        addOAuth2Client();
    }

    //
//    @RollbackExecution
//    public void rollback() {
//        mongoTemplate.getDb().drop();
//    }
//
//    public void addApps() {
//        App app = new App(APP_NAME, true);
//        mongoTemplate.save(app);
//    }
//
//    public void addAuthorities() {
//        mongoTemplate.save(new Authority(Authority.USER, true, true));
//        mongoTemplate.save(new Authority(Authority.ADMIN, true, true));
//        mongoTemplate.save(new Authority(Authority.DEVELOPER, true, true));
//        mongoTemplate.save(new Authority(Authority.ANONYMOUS, true, true));
//
//        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.USER));
//        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.ADMIN));
//        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.DEVELOPER));
//        mongoTemplate.save(new AppAuthority(APP_NAME, Authority.ANONYMOUS));
//    }
//
//    public void addUserAndAuthorities() {
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        // Creates 'user' user and corresponding authorities
//        User userRoleUser = new User();
//        userRoleUser.setUsername("user");
//        userRoleUser.setFirstName("User");
//        userRoleUser.setLastName("User");
//        userRoleUser.setEmail("user@localhost");
//        userRoleUser.setMobileNo("15000899479");
//        // Raw password: user
//        userRoleUser.setPasswordHash(passwordEncoder.encode("user"));
//        userRoleUser.setActivated(true);
//        userRoleUser.setActivationKey(null);
//        userRoleUser.setResetKey(null);
//        userRoleUser.setResetTime(null);
//        userRoleUser.setEnabled(true);
//        mongoTemplate.save(userRoleUser);
//
//        mongoTemplate.save(new UserAuthority(userRoleUser.getId(), Authority.USER));
//
//        // Creates 'admin' user and corresponding authorities
//        User adminRoleUser = new User();
//        adminRoleUser.setUsername("admin");
//        adminRoleUser.setFirstName("Admin");
//        adminRoleUser.setLastName("Admin");
//        adminRoleUser.setEmail("admin@localhost");
//        adminRoleUser.setMobileNo("15000899477");
//        // Raw password: admin
//        adminRoleUser.setPasswordHash(passwordEncoder.encode("admin"));
//        adminRoleUser.setActivated(true);
//        adminRoleUser.setActivationKey(null);
//        adminRoleUser.setResetKey(null);
//        adminRoleUser.setResetTime(null);
//        adminRoleUser.setEnabled(true);
//        mongoTemplate.save(adminRoleUser);
//
//        mongoTemplate.save(new UserAuthority(adminRoleUser.getId(), Authority.USER));
//        mongoTemplate.save(new UserAuthority(adminRoleUser.getId(), Authority.ADMIN));
//
//        // Creates 'system' user and corresponding authorities
//        User adminRoleSystemUser = new User();
//        adminRoleSystemUser.setUsername("system");
//        adminRoleSystemUser.setFirstName("System");
//        adminRoleSystemUser.setLastName("System");
//        adminRoleSystemUser.setEmail("system@localhost");
//        adminRoleSystemUser.setMobileNo("15000899422");
//        // Raw password: system
//        adminRoleSystemUser.setPasswordHash(passwordEncoder.encode("system"));
//        adminRoleSystemUser.setActivated(true);
//        adminRoleSystemUser.setActivationKey(null);
//        adminRoleSystemUser.setResetKey(null);
//        adminRoleSystemUser.setResetTime(null);
//        adminRoleSystemUser.setEnabled(true);
//        mongoTemplate.save(adminRoleSystemUser);
//
//        mongoTemplate.save(new UserAuthority(adminRoleSystemUser.getId(), Authority.USER));
//        mongoTemplate.save(new UserAuthority(adminRoleSystemUser.getId(), Authority.ADMIN));
//
//        // Creates 'louis' user and corresponding authorities
//        User developerRoleUser = new User();
//        developerRoleUser.setUsername(USERNAME);
//        developerRoleUser.setFirstName("Louis");
//        developerRoleUser.setLastName("Lau");
//        developerRoleUser.setEmail("louis@luixtech.com");
//        developerRoleUser.setMobileNo("15000899488");
//        // Raw password: louis
//        developerRoleUser.setPasswordHash(passwordEncoder.encode(PASSWORD));
//        developerRoleUser.setActivated(true);
//        developerRoleUser.setActivationKey(null);
//        developerRoleUser.setResetKey(null);
//        developerRoleUser.setResetTime(null);
//        developerRoleUser.setEnabled(true);
//        mongoTemplate.save(developerRoleUser);
//
//        mongoTemplate.save(new UserAuthority(developerRoleUser.getId(), Authority.USER));
//        mongoTemplate.save(new UserAuthority(developerRoleUser.getId(), Authority.ADMIN));
//        mongoTemplate.save(new UserAuthority(developerRoleUser.getId(), Authority.DEVELOPER));
//    }
//
//    public void addAuthorityAdminMenu() {
//        AdminMenu userAuthority = new AdminMenu(APP_NAME, "user-authority", "User authority", 1, "user-authority", 100, MENU_PARENT_ID);
//        mongoTemplate.save(userAuthority);
//
//        AdminMenu authorityList = new AdminMenu(APP_NAME, "authority-list", "Authority", 2, "user-authority.authority-list",
//                101, userAuthority.getId());
//        mongoTemplate.save(authorityList);
//
//        AdminMenu userList = new AdminMenu(APP_NAME, "user-list", "User", 2, "user-authority.user-list", 102,
//                userAuthority.getId());
//        mongoTemplate.save(userList);
//
//        AdminMenu app = new AdminMenu(APP_NAME, "app", "Application", 1, "app", 200, MENU_PARENT_ID);
//        mongoTemplate.save(app);
//
//        AdminMenu appList = new AdminMenu(APP_NAME, "app-list", "Application", 2, "app.app-list", 201, app.getId());
//        mongoTemplate.save(appList);
//
//        AdminMenu adminMenuAuthority = new AdminMenu(APP_NAME, "admin-menu-authority", "Menu authority", 1,
//                "admin-menu-authority", 300, MENU_PARENT_ID);
//        mongoTemplate.save(adminMenuAuthority);
//
//        AdminMenu adminMenuList = new AdminMenu(APP_NAME, "admin-menu-list", "Menu", 2,
//                "admin-menu-authority.admin-menu-list", 301, adminMenuAuthority.getId());
//        mongoTemplate.save(adminMenuList);
//
//        AdminMenu authorityAdminMenu = new AdminMenu(APP_NAME, "authority-admin-menu", "Authority menu", 2,
//                "admin-menu-authority.authority-admin-menu", 302, adminMenuAuthority.getId());
//        mongoTemplate.save(authorityAdminMenu);
//
//        AdminMenu security = new AdminMenu(APP_NAME, "security", "Security", 1, "security", 400, MENU_PARENT_ID);
//        mongoTemplate.save(security);
//
//        AdminMenu oAuth2ClientDetails = new AdminMenu(APP_NAME, "oauth2-client-list", "OAuth2 client", 2,
//                "security.oauth2-client-list", 401, security.getId());
//        mongoTemplate.save(oAuth2ClientDetails);
//
//        AdminMenu oAuth2AccessTokenDetails = new AdminMenu(APP_NAME, "oauth2-access-token-list", "Access token", 2,
//                "security.oauth2-access-token-list", 402, security.getId());
//        mongoTemplate.save(oAuth2AccessTokenDetails);
//
//        AdminMenu oAuth2RefreshTokenDetails = new AdminMenu(APP_NAME, "oauth2-refresh-token-list", "Refresh token", 2,
//                "security.oauth2-refresh-token-list", 403, security.getId());
//        mongoTemplate.save(oAuth2RefreshTokenDetails);
//
//        AdminMenu oAuth2ApprovalDetails = new AdminMenu(APP_NAME, "oauth2-approval-list", "OAuth2 approval", 2,
//                "security.oauth2-approval-list", 404, security.getId());
//        mongoTemplate.save(oAuth2ApprovalDetails);
//
//        //AuthorityAdminMenu
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, userAuthority.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, authorityList.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, userList.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, app.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, appList.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, adminMenuAuthority.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, adminMenuList.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, authorityAdminMenu.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, security.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, oAuth2ClientDetails.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, oAuth2AccessTokenDetails.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, oAuth2RefreshTokenDetails.getId()));
//
//        mongoTemplate.save(AuthorityAdminMenu.of(Authority.ADMIN, oAuth2ApprovalDetails.getId()));
//    }
//
    public void addOAuth2Client() {
        oauth2ClientRepository.deleteAll();

        OAuth2Client oAuth2Client1 = new OAuth2Client();
        oAuth2Client1.setClientId(OAuth2Client.AUTH_CODE_CLIENT_ID);
        oAuth2Client1.setClientSecret(passwordEncoder.encode(OAuth2Client.INTERNAL_RAW_CLIENT_SECRET));
        oAuth2Client1.setClientAuthenticationMethods(Set.of(ClientAuthMethod.of(oAuth2Client1.getClientId(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue())));
        oAuth2Client1.setAuthorizationGrantTypes(
                // 最安全的流程，需要用户的参与
                Set.of(OAuth2GrantType.of(oAuth2Client1.getClientId(), AuthorizationGrantType.AUTHORIZATION_CODE.getValue()),
                        OAuth2GrantType.of(oAuth2Client1.getClientId(), AuthorizationGrantType.REFRESH_TOKEN.getValue())));
        // Note: localhost and 127.0.0.1 must be saved twice.
        oAuth2Client1.setRedirectUris(Set.of(RedirectUri.of(oAuth2Client1.getClientId(), "http://127.0.0.1:9020"),
                RedirectUri.of(oAuth2Client1.getClientId(), "http://localhost:9020"),
                RedirectUri.of(oAuth2Client1.getClientId(), "https://www.baidu.com")));
        oAuth2Client1.setScopes(Set.of(OAuth2Scope.of(oAuth2Client1.getClientId(), OidcScopes.OPENID, "openid"),
                OAuth2Scope.of(oAuth2Client1.getClientId(), OidcScopes.PROFILE, "profile")));

        OAuth2ClientSettings clientSettings1 = new OAuth2ClientSettings();
        clientSettings1.setClientId(oAuth2Client1.getClientId());
        clientSettings1.setRequireAuthorizationConsent(true);
        oAuth2Client1.setClientSettings(clientSettings1);

        OAuth2TokenSettings tokenSettings1 = new OAuth2TokenSettings();
        tokenSettings1.setClientId(oAuth2Client1.getClientId());
        tokenSettings1.setAccessTokenTimeToLive(Duration.of(7, ChronoUnit.DAYS));
        tokenSettings1.setRefreshTokenTimeToLive(Duration.of(30, ChronoUnit.DAYS));
        tokenSettings1.setTokenFormat("self-contained");
        tokenSettings1.setReuseRefreshTokens(true);
        tokenSettings1.setIdTokenSignatureAlgorithm("RS256");
        oAuth2Client1.setTokenSettings(tokenSettings1);

        oAuth2Client1.setClientIdIssuedAt(Instant.now());
        oAuth2Client1.setClientSecretExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));

        oauth2ClientRepository.save(oAuth2Client1);

        OAuth2Client oAuth2Client2 = new OAuth2Client();
        oAuth2Client2.setClientId(OAuth2Client.INTERNAL_CLIENT_ID);
        oAuth2Client2.setClientSecret(passwordEncoder.encode(OAuth2Client.INTERNAL_RAW_CLIENT_SECRET));
        oAuth2Client2.setClientAuthenticationMethods(Set.of(ClientAuthMethod.of(oAuth2Client2.getClientId(),
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue())));
        oAuth2Client2.setAuthorizationGrantTypes(
                // 全局只有一个账号密码，使用这一个账号便可以访问资源，一般用于内部系统间调用
                Set.of(OAuth2GrantType.of(oAuth2Client2.getClientId(), AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()),
                        // 每个用户有不同的账号密码，每个用户可以使用自己的账号访问资源
                        OAuth2GrantType.of(oAuth2Client2.getClientId(), AuthorizationGrantType.PASSWORD.getValue()),
                        // 根据refresh token可以重新生成access token
                        OAuth2GrantType.of(oAuth2Client2.getClientId(), AuthorizationGrantType.REFRESH_TOKEN.getValue())));
        oAuth2Client2.setScopes(Set.of(OAuth2Scope.of(oAuth2Client2.getClientId(), OidcScopes.OPENID, "openid"),
                OAuth2Scope.of(oAuth2Client2.getClientId(), "message:read", "read"),
                OAuth2Scope.of(oAuth2Client2.getClientId(), "message:write", "write")));

        OAuth2ClientSettings clientSettings2 = new OAuth2ClientSettings();
        clientSettings2.setClientId(oAuth2Client2.getClientId());
        clientSettings2.setRequireAuthorizationConsent(true);
        oAuth2Client2.setClientSettings(clientSettings2);

        OAuth2TokenSettings tokenSettings2 = new OAuth2TokenSettings();
        tokenSettings2.setClientId(oAuth2Client2.getClientId());
        tokenSettings2.setAccessTokenTimeToLive(Duration.of(7, ChronoUnit.DAYS));
        tokenSettings2.setRefreshTokenTimeToLive(Duration.of(30, ChronoUnit.DAYS));
        tokenSettings2.setTokenFormat("self-contained");
        tokenSettings2.setReuseRefreshTokens(true);
        tokenSettings2.setIdTokenSignatureAlgorithm("RS256");
        oAuth2Client2.setTokenSettings(tokenSettings2);

        oAuth2Client2.setClientIdIssuedAt(Instant.now());
        oAuth2Client2.setClientSecretExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        oauth2ClientRepository.save(oAuth2Client2);
    }
}
