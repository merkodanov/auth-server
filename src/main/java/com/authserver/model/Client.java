package com.authserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;

@Entity(name = "client")
@Data
public class Client {
    @Id
    private String id;
    @Column(nullable = false)
    private String clientId;
    @Column(nullable = false)
    private Instant clientIdIssuedAt;
    @Column(nullable = false)
    private String clientName;
    @Column(length = 1000)
    private String clientAuthenticationMethods;
    @Column(length = 1000)
    private String redirectUris;
    @Column(nullable = false)
    private String scopes;
    @Column(nullable = false, length = 2000)
    private String clientSettings;
    @Column(nullable = false, length = 2000)
    private String tokenSettings;
}
