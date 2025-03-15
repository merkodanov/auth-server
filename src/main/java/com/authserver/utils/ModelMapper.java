package com.authserver.utils;

import com.authserver.model.OAuth2AuthorizationCodeGrantAuthorization;
import com.authserver.model.OAuth2AuthorizationGrantAuthorization;
import com.authserver.model.OidcAuthorizationCodeGrantAuthorization;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;

import java.util.Objects;

public class ModelMapper {

    public static OAuth2AuthorizationGrantAuthorization convertOAuth2AuthorizationToOidcGrant(OAuth2Authorization authorization) {

        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = authorization.getAttribute("org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest");
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
                .token(authorizationGrantAuthorization.getAccessToken(), accessTokenMetadata ->
                {
                    accessTokenMetadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, authorizationGrantAuthorization.getAccessToken().getClaims());
                    accessTokenMetadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false);
                    accessTokenMetadata.put(String.valueOf(OAuth2TokenFormat.class), OAuth2TokenFormat.SELF_CONTAINED);
                })
                .authorizedScopes(authorizationGrantAuthorization.getAccessToken().getScopes())
                .token(authorizationGrantAuthorization.getRefreshToken(), refreshTokenMetadata ->
                        refreshTokenMetadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false));

        if (authorizationGrantAuthorization instanceof OAuth2AuthorizationCodeGrantAuthorization
                oAuth2AuthorizationCodeGrantAuthorization) {
            if (oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationCode() != null) {
                oAuth2AuthorizationBuilder.token(oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationCode());
            }
            if (oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationRequest() != null) {
                oAuth2AuthorizationBuilder.attribute("org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest", oAuth2AuthorizationCodeGrantAuthorization.getAuthorizationRequest());
            }
            if (oAuth2AuthorizationCodeGrantAuthorization.getPrincipal() != null) {
                oAuth2AuthorizationBuilder.attribute("java.security.Principal", oAuth2AuthorizationCodeGrantAuthorization.getPrincipal());
            }
        }

        if (authorizationGrantAuthorization instanceof OidcAuthorizationCodeGrantAuthorization
                oidcAuthorizationCodeGrantAuthorization) {
            if (oidcAuthorizationCodeGrantAuthorization.getIdToken() != null) {
                oAuth2AuthorizationBuilder.token(oidcAuthorizationCodeGrantAuthorization.getIdToken(), oidcTokenMetadata ->
                {
                    oidcTokenMetadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, oidcAuthorizationCodeGrantAuthorization.getIdToken().getClaims());
                    oidcTokenMetadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false);
                });
            }
        }
    }

    private static OidcAuthorizationCodeGrantAuthorization.IdToken getOidcTokenForEntity(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        if (oidcIdToken == null){
            return null;
        }
        OAuth2AuthorizationGrantAuthorization.ClaimsHolder claimsHolder = new OAuth2AuthorizationGrantAuthorization.ClaimsHolder(
                Objects.requireNonNull(oidcIdToken, "OIDC TOKEN IS NULL").getClaims());
        return new OidcAuthorizationCodeGrantAuthorization.IdToken(oidcIdToken.getToken().getTokenValue(),
                oidcIdToken.getToken().getIssuedAt(), oidcIdToken.getToken().getExpiresAt(), oidcIdToken.isInvalidated(), claimsHolder);
    }

    private static OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode getAuthorizationCodeForEntity(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> oAuth2AuthorizationCodeToken = authorization.getToken(OAuth2AuthorizationCode.class);
        if (oAuth2AuthorizationCodeToken == null){
            return null;
        }
        return new OAuth2AuthorizationCodeGrantAuthorization.AuthorizationCode(
                oAuth2AuthorizationCodeToken.getToken().getTokenValue(), oAuth2AuthorizationCodeToken.getToken().getIssuedAt(),
                oAuth2AuthorizationCodeToken.getToken().getExpiresAt(), oAuth2AuthorizationCodeToken.isInvalidated());
    }

    private static OAuth2AuthorizationGrantAuthorization.AccessToken getAccessTokenForEntity(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2AccessToken> oAuth2AuthorizationAccessToken = authorization.getAccessToken();
        if (oAuth2AuthorizationAccessToken == null){
            return null;
        }
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
                oAuth2AuthorizationAccessToken.getMetadata(String.valueOf(OAuth2TokenFormat.class)),
                claimsHolder
        );
    }

    private static OAuth2AuthorizationGrantAuthorization.RefreshToken getRefreshTokenForEntity(OAuth2Authorization authorization) {
        OAuth2Authorization.Token<OAuth2RefreshToken> oAuth2AuthorizationRefreshToken = authorization.getRefreshToken();
        if (oAuth2AuthorizationRefreshToken == null){
            return null;
        }
        return new OAuth2AuthorizationGrantAuthorization.RefreshToken(
                oAuth2AuthorizationRefreshToken.getToken().getTokenValue(),
                oAuth2AuthorizationRefreshToken.getToken().getIssuedAt(),
                oAuth2AuthorizationRefreshToken.getToken().getExpiresAt(),
                oAuth2AuthorizationRefreshToken.isInvalidated()
        );
    }
}
