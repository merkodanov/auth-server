package com.authserver.repository;

import com.authserver.model.OidcAuthorizationCodeGrantAuthorization;
import org.springframework.data.repository.CrudRepository;

public interface OidcAuthorizationCodeGrantAuthorizationRepository
        extends CrudRepository<OidcAuthorizationCodeGrantAuthorization, String> {
    OidcAuthorizationCodeGrantAuthorization findByIdTokenTokenValue(String idToken);
}
