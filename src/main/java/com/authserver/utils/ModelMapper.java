package com.authserver.utils;

import com.authserver.model.OAuth2AuthorizationCodeGrantAuthorization;
import com.authserver.model.OAuth2AuthorizationGrantAuthorization;
import com.authserver.model.OAuth2RegisteredClient;
import com.authserver.model.OidcAuthorizationCodeGrantAuthorization;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelMapper {

    public static OAuth2RegisteredClient convertOAuth2RegisteredClient(RegisteredClient registeredClient) {
        return new OAuth2RegisteredClient(registeredClient.getId(),
                registeredClient.getClientId(), registeredClient.getClientIdIssuedAt(), registeredClient.getClientSecret(),
                registeredClient.getClientSecretExpiresAt(), registeredClient.getClientName(), registeredClient.getClientAuthenticationMethods(),
                registeredClient.getAuthorizationGrantTypes(), registeredClient.getRedirectUris(), registeredClient.getRedirectUris(),
                registeredClient.getScopes(), OAuth2RegisteredClient.ClientSettings.fromMap(registeredClient.getClientSettings().getSettings()),
                OAuth2RegisteredClient.TokenSettings.fromMap(registeredClient.getClientSettings().getSettings()));
    }

    public static RegisteredClient convertRegisteredClient(OAuth2RegisteredClient client) {
        Set<String> clientAuthenticationMethods = client.getClientAuthenticationMethods()
                .stream().map(ClientAuthenticationMethod::getValue).collect(Collectors.toSet());
        Set<String> authorizationGrantTypes = client.getAuthorizationGrantTypes()
                .stream().map(AuthorizationGrantType::getValue).collect(Collectors.toSet());
        Set<String> redirectUris = client.getRedirectUris();
        Set<String> postLogoutRedirectUris = client.getPostLogoutRedirectUris();
        Set<String> clientScopes = client.getScopes();

        RegisteredClient.Builder builder = RegisteredClient.withId(client.getId())
                .clientId(client.getClientId())
                .clientIdIssuedAt(client.getClientIdIssuedAt())
                .clientSecret(client.getClientSecret())
                .clientSecretExpiresAt(client.getClientSecretExpiresAt())
                .clientName(client.getClientName())
                .clientAuthenticationMethods(authenticationMethods ->
                        clientAuthenticationMethods.forEach(authenticationMethod ->
                                authenticationMethods.add(resolveClientAuthenticationMethod(authenticationMethod))))
                .authorizationGrantTypes((grantTypes) ->
                        authorizationGrantTypes.forEach(grantType ->
                                grantTypes.add(resolveAuthorizationGrantType(grantType))))
                .redirectUris((uris) -> uris.addAll(redirectUris))
                .postLogoutRedirectUris((uris) -> uris.addAll(postLogoutRedirectUris))
                .scopes((scopes) -> scopes.addAll(clientScopes));

        Map<String, Object> clientSettingsMap = client.getClientSettings().toMap();
        builder.clientSettings(ClientSettings.withSettings(clientSettingsMap).build());

        Map<String, Object> tokenSettingsMap = client.getTokenSettings().toMap();
        builder.tokenSettings(TokenSettings.withSettings(tokenSettingsMap).build());
        return builder.build();
    }


    public static OAuth2AuthorizationGrantAuthorization convertOAuth2AuthorizationToOidcGrant(OAuth2Authorization authorization) {

        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = authorization.getAttribute("OAuth2AuthorizationRequest");
        Objects.requireNonNull(oAuth2AuthorizationRequest, "Authorization Request is null");

        OidcAuthorizationCodeGrantAuthorization.IdToken idToken = getOidcTokenForEntity(authorization);

        OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode authorizationCode = getAuthorizationCodeForEntity(authorization);

        OAuth2AuthorizationGrantAuthorization.AccessToken accessToken = getAccessTokenForEntity(authorization);

        OAuth2AuthorizationGrantAuthorization.RefreshToken refreshToken = getRefreshTokenForEntity(authorization);

        return new OidcAuthorizationCodeGrantAuthorization(
                authorization.getId(), authorization.getRegisteredClientId(), authorization.getPrincipalName(),
                authorization.getAuthorizedScopes(), accessToken, refreshToken,
                authorization.getAttribute("java.security.Principal"), authorization.getAuthorizationGrantType(),
                oAuth2AuthorizationRequest, authorizationCode, oAuth2AuthorizationRequest.getState(),
                idToken);
    }

    public static void convertOAuth2AuthorizationGrantAuthorizationToOAuth2Authorization(OAuth2AuthorizationGrantAuthorization
                                                                                                 authorizationGrantAuthorization,
                                                                                         OAuth2Authorization.Builder
                                                                                                 oAuth2AuthorizationBuilder) {
        oAuth2AuthorizationBuilder
                .id(authorizationGrantAuthorization.getId())
                .principalName(authorizationGrantAuthorization.getPrincipalName())
                .authorizationGrantType(authorizationGrantAuthorization.getAuthorizationGrantType())
                .token(authorizationGrantAuthorization.getAccessToken())
                .token(authorizationGrantAuthorization.getRefreshToken());

        if (authorizationGrantAuthorization instanceof OAuth2AuthorizationCodeGrantAuthorization
                oAuth2AuthorizationCodeGrantAuthorization) {
            if (oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationCode() != null) {
                oAuth2AuthorizationBuilder.token(oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationCode());
            }
        }

        if (authorizationGrantAuthorization instanceof OidcAuthorizationCodeGrantAuthorization
                oidcAuthorizationCodeGrantAuthorization) {
            if (oidcAuthorizationCodeGrantAuthorization.getIdToken() != null) {
                oAuth2AuthorizationBuilder.token(oidcAuthorizationCodeGrantAuthorization.getIdToken());
            }
        }
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        } else if (AuthorizationGrantType.DEVICE_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.DEVICE_CODE;
        }
        return new AuthorizationGrantType(authorizationGrantType);
    }

    private static ClientAuthenticationMethod resolveClientAuthenticationMethod(String clientAuthenticationMethod) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(clientAuthenticationMethod);
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
