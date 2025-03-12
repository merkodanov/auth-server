package com.authserver.repository;

import com.authserver.model.AuthorizationConsent;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthorizationConsentRepository extends CrudRepository<AuthorizationConsent, AuthorizationConsent.AuthorizationConsentId> {
    Optional<AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}