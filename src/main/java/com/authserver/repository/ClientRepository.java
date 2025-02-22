package com.authserver.repository;

import com.authserver.model.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, String> {
    Client findClientById(String clientId);
}
