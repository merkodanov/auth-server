package com.authserver.repository;

import com.authserver.model.OAuth2AuthorizationGrantAuthorization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuth2AuthorizationGrantAuthorizationRepository
        extends CrudRepository<OAuth2AuthorizationGrantAuthorization, String> {

    OAuth2AuthorizationGrantAuthorization findByAccessTokenTokenValue(String accessToken);

    OAuth2AuthorizationGrantAuthorization findByRefreshTokenTokenValue(String refreshToken);

    OAuth2AuthorizationGrantAuthorization findByAccessTokenTokenValueOrRefreshTokenTokenValue(String accessToken, String refreshToken);

}