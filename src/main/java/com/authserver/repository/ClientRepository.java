package com.authserver.repository;

import com.authserver.model.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, String> {
    Optional<Client> findClientById(String clientId);
}
