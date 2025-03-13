package com.authserver.utils;

import com.authserver.model.OAuth2AuthorizationCodeGrantAuthorization;
import com.authserver.model.OAuth2AuthorizationGrantAuthorization;
import com.authserver.model.OidcAuthorizationCodeGrantAuthorization;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;

import java.util.Objects;

public class ModelMapper {
    // Аттрибуты
    //{"@class":"java.util.Collections$UnmodifiableMap",
    // "org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest":
    // {"@class":"org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest",
    // "authorizationUri":"http://authserver:9000/oauth2/authorize",
    // "authorizationGrantType":{"value":"authorization_code"},
    // "responseType":{"value":"code"},
    // "clientId":"todolist",
    // "redirectUri":"http://127.0.0.1:9090/login/oauth2/code/todolist-client",
    // "scopes":["java.util.Collections$UnmodifiableSet",["readTask","updateTask","openid","profile","deleteTask","addTask"]],
    // "state":"x-qgYcev42KdfwEQYCrW_m2TBm1kNvMLON7yr-68TUk=",
    // "additionalParameters":{"@class":"java.util.Collections$UnmodifiableMap","nonce":"NdR0Ae6W0ORP1LQyptEz-sSYMRcOd0zd_PdMK-k7ASg"},
    // "authorizationRequestUri":"http://authserver:9000/oauth2/authorize?response_type=code&client_id=todolist&scope=readTask%20updateTask%20openid%20profile%20deleteTask%20addTask&state=x-qgYcev42KdfwEQYCrW_m2TBm1kNvMLON7yr-68TUk%3D&redirect_uri=http://127.0.0.1:9090/login/oauth2/code/todolist-client&nonce=NdR0Ae6W0ORP1LQyptEz-sSYMRcOd0zd_PdMK-k7ASg",
    // "attributes":{"@class":"java.util.Collections$UnmodifiableMap"}},

    // "java.security.Principal":{
    // "@class":"org.springframework.security.authentication.UsernamePasswordAuthenticationToken",
    // "authorities":["java.util.Collections$UnmodifiableRandomAccessList",[{"@class":"org.springframework.security.core.authority.SimpleGrantedAuthority","authority":"ROLE_USER"}]],
    // "details":{"@class":"org.springframework.security.web.authentication.WebAuthenticationDetails",
    // "remoteAddress":"127.0.0.1",
    // "sessionId":"11F188BC4AA4529E4C937C527C91AD8E"},
    // "authenticated":true,
    // "principal":
    // {"@class":"com.authserver.model.User",
    // "id":0,
    // "username":"Vova",
    // "password":"$2a$10$jqhddbeAb2xUGuRt.GmfFOed5E8n.NZkjo5FMt5OXUmicnyBCt90m",
    // "email":"vladislavik259@gmail.com",
    // "role":"ROLE_USER",
    // "enabled":true,
    // "authorities":["java.util.ImmutableCollections$List12",
    // [{"@class":"org.springframework.security.core.authority.SimpleGrantedAuthority","authority":"ROLE_USER"}]],
    // "accountNonLocked":true,
    // "accountNonExpired":true,"credentialsNonExpired":true},
    // "credentials":null}}

    //TODO Добавить аттрибуты и метадату!!!
    public static OAuth2AuthorizationGrantAuthorization convertOAuth2AuthorizationToOidcGrant(OAuth2Authorization authorization) {

        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = authorization.getAttribute("authorizationRequest");
        Objects.requireNonNull(oAuth2AuthorizationRequest, "Authorization Request is null");

        OidcAuthorizationCodeGrantAuthorization.IdToken idToken = getOidcTokenForEntity(authorization);

        OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode authorizationCode = getAuthorizationCodeForEntity(authorization);

        OAuth2AuthorizationGrantAuthorization.AccessToken accessToken = getAccessTokenForEntity(authorization);

        OAuth2AuthorizationGrantAuthorization.RefreshToken refreshToken = getRefreshTokenForEntity(authorization);

        return new OidcAuthorizationCodeGrantAuthorization(
                authorization.getId(), authorization.getRegisteredClientId(), authorization.getPrincipalName(),
                authorization.getAuthorizedScopes(), accessToken, refreshToken,
                authorization.getAttribute("principal"), authorization.getAuthorizationGrantType(),
                oAuth2AuthorizationRequest, authorizationCode, oAuth2AuthorizationRequest.getState(),
                idToken);
    }

    //TODO Добавить аттрибуты и метадату!!!
    public static void convertOAuth2AuthorizationGrantAuthorizationToOAuth2Authorization(OAuth2AuthorizationGrantAuthorization
                                                                                                 authorizationGrantAuthorization,
                                                                                         OAuth2Authorization.Builder
                                                                                                 oAuth2AuthorizationBuilder) {
        oAuth2AuthorizationBuilder
                .id(authorizationGrantAuthorization.getId())
                .principalName(authorizationGrantAuthorization.getPrincipalName())
                .authorizationGrantType(authorizationGrantAuthorization.getAuthorizationGrantType())
                .token(authorizationGrantAuthorization.getAccessToken())
                .authorizedScopes(authorizationGrantAuthorization.getAccessToken().getScopes())
                .token(authorizationGrantAuthorization.getRefreshToken());

        if (authorizationGrantAuthorization instanceof OAuth2AuthorizationCodeGrantAuthorization
                oAuth2AuthorizationCodeGrantAuthorization) {
            if (oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationCode() != null) {
                oAuth2AuthorizationBuilder.token(oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationCode());
            }
            if (oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationRequest() != null){
                oAuth2AuthorizationBuilder.attribute("authorizationRequest", oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationRequest());
            }
            if (oAuth2AuthorizationCodeGrantAuthorization.getPrincipal() != null){
                oAuth2AuthorizationBuilder.attribute("principal", oAuth2AuthorizationCodeGrantAuthorization.getPrincipal());
            }
        }

        if (authorizationGrantAuthorization instanceof OidcAuthorizationCodeGrantAuthorization
                oidcAuthorizationCodeGrantAuthorization) {
            if (oidcAuthorizationCodeGrantAuthorization.getIdToken() != null) {
                oAuth2AuthorizationBuilder.token(oidcAuthorizationCodeGrantAuthorization.getIdToken());
            }
        }
    }

    private static OidcAuthorizationCodeGrantAuthorization.IdToken getOidcTokenForEntity(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        OAuth2AuthorizationGrantAuthorization.ClaimsHolder claimsHolder = new OAuth2AuthorizationGrantAuthorization.ClaimsHolder(
                Objects.requireNonNull(oidcIdToken, "OIDC TOKEN IS NULL").getToken().getClaims());
        return new OidcAuthorizationCodeGrantAuthorization.IdToken(oidcIdToken.getToken().getTokenValue(),
                oidcIdToken.getToken().getIssuedAt(), oidcIdToken.getToken().getExpiresAt(), oidcIdToken.isInvalidated(), claimsHolder);
    }

    private static OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode getAuthorizationCodeForEntity(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> oAuth2AuthorizationCodeToken = authorization.getToken(OAuth2AuthorizationCode.class);
        Objects.requireNonNull(oAuth2AuthorizationCodeToken, "AUTHORIZATION CODE TOKEN IS NULL");
        return new OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode(
                oAuth2AuthorizationCodeToken.getToken().getTokenValue(), oAuth2AuthorizationCodeToken.getToken().getIssuedAt(),
                oAuth2AuthorizationCodeToken.getToken().getExpiresAt(), oAuth2AuthorizationCodeToken.isInvalidated());
    }

    private static OAuth2AuthorizationGrantAuthorization.AccessToken getAccessTokenForEntity(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AccessToken> oAuth2AuthorizationAccessToken = authorization.getAccessToken();
        OAuth2AuthorizationGrantAuthorization.ClaimsHolder claimsHolder = new OAuth2AuthorizationGrantAuthorization.ClaimsHolder(
                oAuth2AuthorizationAccessToken.getClaims()
        );
        return new OAuth2AuthorizationGrantAuthorization.AccessToken(
                oAuth2AuthorizationAccessToken.getToken().getTokenValue(),
                oAuth2AuthorizationAccessToken.getToken().getIssuedAt(),
                oAuth2AuthorizationAccessToken.getToken().getExpiresAt(),
                oAuth2AuthorizationAccessToken.isInvalidated(),
                oAuth2AuthorizationAccessToken.getToken().getTokenType(),
                oAuth2AuthorizationAccessToken.getToken().getScopes(),
                OAuth2TokenFormat.SELF_CONTAINED,
                claimsHolder
        );
    }

    private static OAuth2AuthorizationGrantAuthorization.RefreshToken getRefreshTokenForEntity(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2RefreshToken> oAuth2AuthorizationRefreshToken = authorization.getRefreshToken();
        Objects.requireNonNull(oAuth2AuthorizationRefreshToken, "REFRESH TOKEN IS NULL");
        return new OAuth2AuthorizationGrantAuthorization.RefreshToken(
                oAuth2AuthorizationRefreshToken.getToken().getTokenValue(),
                oAuth2AuthorizationRefreshToken.getToken().getIssuedAt(),
                oAuth2AuthorizationRefreshToken.getToken().getExpiresAt(),
                oAuth2AuthorizationRefreshToken.isInvalidated()
        );
    }
}
