package com.authserver.service;

import com.authserver.model.OAuth2AuthorizationGrantAuthorization;
import com.authserver.repository.OAuth2AuthorizationCodeGrantAuthorizationRepository;
import com.authserver.repository.OAuth2AuthorizationGrantAuthorizationRepository;
import com.authserver.repository.OidcAuthorizationCodeGrantAuthorizationRepository;
import com.authserver.utils.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final RegisteredClientRepository registeredClientRepository;

    private final OAuth2AuthorizationGrantAuthorizationRepository authorizationGrantAuthorizationRepository;
    private final OAuth2AuthorizationCodeGrantAuthorizationRepository authorizationCodeGrantAuthorizationRepository;
    private final OidcAuthorizationCodeGrantAuthorizationRepository oidcAuthorizationCodeGrantAuthorizationRepository;

    @Autowired
    public RedisOAuth2AuthorizationService(RegisteredClientRepository registeredClientRepository,
                                           OAuth2AuthorizationGrantAuthorizationRepository authorizationGrantAuthorizationRepository,
                                           OAuth2AuthorizationCodeGrantAuthorizationRepository authorizationCodeGrantAuthorizationRepository,
                                           OidcAuthorizationCodeGrantAuthorizationRepository oidcAuthorizationCodeGrantAuthorizationRepository) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        Assert.notNull(authorizationGrantAuthorizationRepository,
                "authorizationGrantAuthorizationRepository cannot be null");
        Assert.notNull(authorizationCodeGrantAuthorizationRepository,
                "authorizationCodeGrantAuthorizationRepository cannot be null");
        Assert.notNull(oidcAuthorizationCodeGrantAuthorizationRepository,
                "oidcAuthorizationCodeGrantAuthorizationRepository cannot be null");
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationGrantAuthorizationRepository = authorizationGrantAuthorizationRepository;
        this.authorizationCodeGrantAuthorizationRepository = authorizationCodeGrantAuthorizationRepository;
        this.oidcAuthorizationCodeGrantAuthorizationRepository = oidcAuthorizationCodeGrantAuthorizationRepository;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        // OIDC 
        OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization = ModelMapper
                .convertOAuth2AuthorizationToOidcGrant(authorization);
        this.authorizationGrantAuthorizationRepository.save(authorizationGrantAuthorization);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        this.authorizationGrantAuthorizationRepository.deleteById(authorization.getId());
    }

    @Nullable
    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return this.authorizationGrantAuthorizationRepository.findById(id)
                .map(this::toOAuth2Authorization)
                .orElse(null);
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization = null;
        if (tokenType == null) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                    .findByStateOrAuthorizationCode(token, token);
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                        .findByAccessTokenOrRefreshToken(token, token);
            }
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                        .findByIdToken(token);
            }
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                    .findByAccessTokenTokenValue(token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            authorizationGrantAuthorization = this.authorizationCodeGrantAuthorizationRepository
                    .findByAuthorizationCodeTokenValue(token);
        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            authorizationGrantAuthorization = this.oidcAuthorizationCodeGrantAuthorizationRepository.findByIdTokenTokenValue(token);
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                    .findByRefreshTokenTokenValue(token);
        }
        return authorizationGrantAuthorization != null ? toOAuth2Authorization(authorizationGrantAuthorization) : null;
    }

    private OAuth2Authorization toOAuth2Authorization(
            OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization) {
        RegisteredClient registeredClient = this.registeredClientRepository
                .findById(authorizationGrantAuthorization.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + authorizationGrantAuthorization.getRegisteredClientId() +
                            "' was not found in the RegisteredClientRepository.");
        }
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
        ModelMapper.convertOAuth2AuthorizationGrantAuthorizationToOAuth2Authorization(authorizationGrantAuthorization, builder);
        return builder.build();
    }

}