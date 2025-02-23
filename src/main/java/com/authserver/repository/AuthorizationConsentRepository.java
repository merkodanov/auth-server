package com.authserver.repository;
import java.util.Optional;

import com.authserver.model.AuthorizationConsent;
import org.springframework.data.repository.CrudRepository;

public interface AuthorizationConsentRepository extends CrudRepository<AuthorizationConsent, AuthorizationConsent.AuthorizationConsentId> {
    Optional<AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);
}