package com.authserver.model;

import lombok.Getter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.security.Principal;
import java.time.Instant;
import java.util.Set;

@Getter
public class OidcAuthorizationCodeGrantAuthorization extends OAuth2AuthorizationCodeGrantAuthorization {

    private final IdToken idToken;

    public OidcAuthorizationCodeGrantAuthorization(String id, String registeredClientId, String principalName,
                                                   Set<String> authorizedScopes, AccessToken accessToken, RefreshToken refreshToken, Principal principal,
                                                   AuthorizationGrantType authorizationGrantType,
                                                   OAuth2AuthorizationRequest authorizationRequest, AuthorizationCode authorizationCode, String state,
                                                   IdToken idToken) {
        super(id, registeredClientId, principalName, authorizedScopes, accessToken, refreshToken, authorizationGrantType, principal,
                authorizationRequest, authorizationCode, state);
        this.idToken = idToken;
    }

    @Getter
    public static class IdToken extends AbstractToken {

        private final ClaimsHolder claims;

        public IdToken(String tokenValue, Instant issuedAt, Instant expiresAt, boolean invalidated,
                       ClaimsHolder claims) {
            super(tokenValue, issuedAt, expiresAt, invalidated);
            this.claims = claims;
        }

    }

}