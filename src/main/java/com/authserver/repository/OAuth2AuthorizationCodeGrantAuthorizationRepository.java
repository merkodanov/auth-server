package com.authserver.repository;

import com.authserver.model.OAuth2AuthorizationCodeGrantAuthorization;
import org.springframework.data.repository.CrudRepository;


public interface OAuth2AuthorizationCodeGrantAuthorizationRepository
        extends CrudRepository<OAuth2AuthorizationCodeGrantAuthorization, String> {

    OAuth2AuthorizationCodeGrantAuthorization findByAuthorizationCodeTokenValue(String authorizationCode);

    OAuth2AuthorizationCodeGrantAuthorization findByState(String state);
}
