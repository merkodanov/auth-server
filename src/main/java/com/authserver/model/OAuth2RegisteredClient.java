package com.authserver.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@RedisHash("oauth2_registered_client")
@RequiredArgsConstructor
@Getter
public class OAuth2RegisteredClient {
    @Id
    private final String id;
    @Indexed
    private final String clientId;
    private final Instant clientIdIssuedAt;
    private final String clientSecret;
    private final Instant clientSecretExpiresAt;
    private final String clientName;
    private final Set<ClientAuthenticationMethod> clientAuthenticationMethods;
    private final Set<AuthorizationGrantType> authorizationGrantTypes;
    private final Set<String> redirectUris;
    private final Set<String> postLogoutRedirectUris;
    private final Set<String> scopes;
    private final ClientSettings clientSettings;
    private final TokenSettings tokenSettings;

    @Getter
    public static class ClientSettings {

        private final boolean requireProofKey;

        private final boolean requireAuthorizationConsent;

        private final String jwkSetUrl;

        private final JwsAlgorithm tokenEndpointAuthenticationSigningAlgorithm;

        private final String x509CertificateSubjectDN;

        public ClientSettings(boolean requireProofKey, boolean requireAuthorizationConsent, String jwkSetUrl,
                              JwsAlgorithm tokenEndpointAuthenticationSigningAlgorithm, String x509CertificateSubjectDN) {
            this.requireProofKey = requireProofKey;
            this.requireAuthorizationConsent = requireAuthorizationConsent;
            this.jwkSetUrl = jwkSetUrl;
            this.tokenEndpointAuthenticationSigningAlgorithm = tokenEndpointAuthenticationSigningAlgorithm;
            this.x509CertificateSubjectDN = x509CertificateSubjectDN;
        }

        public static ClientSettings fromMap(Map<String, Object> settings) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.convertValue(settings, new TypeReference<>() {
                });
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

        public Map<String, Object> toMap() {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.convertValue(this, new TypeReference<>() {
                });
            }
            catch (Exception ex){
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }
    }

    @Getter
    public static class TokenSettings {

        private final Duration authorizationCodeTimeToLive;

        private final Duration accessTokenTimeToLive;

        private final OAuth2TokenFormat accessTokenFormat;

        private final Duration deviceCodeTimeToLive;

        private final boolean reuseRefreshTokens;

        private final Duration refreshTokenTimeToLive;

        private final SignatureAlgorithm idTokenSignatureAlgorithm;

        private final boolean x509CertificateBoundAccessTokens;

        public TokenSettings(Duration authorizationCodeTimeToLive, Duration accessTokenTimeToLive,
                             OAuth2TokenFormat accessTokenFormat, Duration deviceCodeTimeToLive, boolean reuseRefreshTokens,
                             Duration refreshTokenTimeToLive, SignatureAlgorithm idTokenSignatureAlgorithm,
                             boolean x509CertificateBoundAccessTokens) {
            this.authorizationCodeTimeToLive = authorizationCodeTimeToLive;
            this.accessTokenTimeToLive = accessTokenTimeToLive;
            this.accessTokenFormat = accessTokenFormat;
            this.deviceCodeTimeToLive = deviceCodeTimeToLive;
            this.reuseRefreshTokens = reuseRefreshTokens;
            this.refreshTokenTimeToLive = refreshTokenTimeToLive;
            this.idTokenSignatureAlgorithm = idTokenSignatureAlgorithm;
            this.x509CertificateBoundAccessTokens = x509CertificateBoundAccessTokens;
        }

        public static TokenSettings fromMap(Map<String, Object> settings){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.convertValue(settings, new TypeReference<>() {
                });
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

        public Map<String, Object> toMap() {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.convertValue(this, new TypeReference<>() {
                });
            }
            catch (Exception ex){
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }

    }

}
