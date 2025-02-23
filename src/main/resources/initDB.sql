CREATE TABLE if not exists "users"(
    id int primary key GENERATED ALWAYS AS IDENTITY,
    username varchar(100) not null,
    password varchar(100) not null,
    email varchar(50) not null,
    role varchar(20) not null
);
CREATE TABLE if not exists client(
                        id varchar(255) NOT NULL,
                        client_id varchar(255) NOT NULL,
                        client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        client_secret varchar(255) DEFAULT NULL,
                        client_secret_expires_at timestamp DEFAULT NULL,
                        client_name varchar(255) NOT NULL,
                        client_authentication_methods varchar(1000) NOT NULL,
                        authorization_grant_types varchar(1000) NOT NULL,
                        redirect_uris varchar(1000) DEFAULT NULL,
                        post_logout_redirect_uris varchar(1000) DEFAULT NULL,
                        scopes varchar(1000) NOT NULL,
                        client_settings varchar(2000) NOT NULL,
                        token_settings varchar(2000) NOT NULL,
                        PRIMARY KEY (id)
);
CREATE TABLE if not exists "authorization" (
                               id varchar(255) NOT NULL,
                               registeredClientId varchar(255) NOT NULL,
                               principalName varchar(255) NOT NULL,
                               authorizationGrantType varchar(255) NOT NULL,
                               authorizedScopes varchar(1000) DEFAULT NULL,
                               attributes varchar(4000) DEFAULT NULL,
                               state varchar(500) DEFAULT NULL,
                               authorizationCodeValue varchar(4000) DEFAULT NULL,
                               authorizationCodeIssuedAt timestamp DEFAULT NULL,
                               authorizationCodeExpiresAt timestamp DEFAULT NULL,
                               authorizationCodeMetadata varchar(2000) DEFAULT NULL,
                               accessTokenValue varchar(4000) DEFAULT NULL,
                               accessTokenIssuedAt timestamp DEFAULT NULL,
                               accessTokenExpiresAt timestamp DEFAULT NULL,
                               accessTokenMetadata varchar(2000) DEFAULT NULL,
                               accessTokenType varchar(255) DEFAULT NULL,
                               accessTokenScopes varchar(1000) DEFAULT NULL,
                               refreshTokenValue varchar(4000) DEFAULT NULL,
                               refreshTokenIssuedAt timestamp DEFAULT NULL,
                               refreshTokenExpiresAt timestamp DEFAULT NULL,
                               refreshTokenMetadata varchar(2000) DEFAULT NULL,
                               oidcIdTokenValue varchar(4000) DEFAULT NULL,
                               oidcIdTokenIssuedAt timestamp DEFAULT NULL,
                               oidcIdTokenExpiresAt timestamp DEFAULT NULL,
                               oidcIdTokenMetadata varchar(2000) DEFAULT NULL,
                               oidcIdTokenClaims varchar(2000) DEFAULT NULL,
                               userCodeValue varchar(4000) DEFAULT NULL,
                               userCodeIssuedAt timestamp DEFAULT NULL,
                               userCodeExpiresAt timestamp DEFAULT NULL,
                               userCodeMetadata varchar(2000) DEFAULT NULL,
                               deviceCodeValue varchar(4000) DEFAULT NULL,
                               deviceCodeIssuedAt timestamp DEFAULT NULL,
                               deviceCodeExpiresAt timestamp DEFAULT NULL,
                               deviceCodeMetadata varchar(2000) DEFAULT NULL,
                               PRIMARY KEY (id)
);
CREATE TABLE authorizationConsent (
                                      registeredClientId varchar(255) NOT NULL,
                                      principalName varchar(255) NOT NULL,
                                      authorities varchar(1000) NOT NULL,
                                      PRIMARY KEY (registeredClientId, principalName)
);