package com.authserver.model;

import lombok.Getter;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.security.Principal;
import java.time.Instant;
import java.util.Set;

@Getter
public class OAuth2AuthorizationCodeGrantAuthorization extends OAuth2AuthorizationGrantAuthorization {

    private final Principal principal;

    private final OAuth2AuthorizationRequest authorizationRequest;

    private final AuthorizationCode authorizationCode;

    @Indexed
    private final String state;

    public OAuth2AuthorizationCodeGrantAuthorization(String id, String registeredClientId, String principalName,
                                                     Set<String> authorizedScopes, AccessToken accessToken, RefreshToken refreshToken, AuthorizationGrantType authorizationGrantType,
                                                     Principal principal, OAuth2AuthorizationRequest authorizationRequest, AuthorizationCode authorizationCode, String state) {
        super(id, registeredClientId, principalName, authorizedScopes, accessToken, refreshToken, authorizationGrantType);
        this.principal = principal;
        this.authorizationRequest = authorizationRequest;
        this.authorizationCode = authorizationCode;
        this.state = state;
    }

    public static class AuthorizationCode extends AbstractToken {

        public AuthorizationCode(String tokenValue, Instant issuedAt, Instant expiresAt, boolean invalidated) {
            super(tokenValue, issuedAt, expiresAt, invalidated);
        }

    }

}
