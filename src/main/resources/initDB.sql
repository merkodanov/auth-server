CREATE TABLE if not exists "users"(
    id int primary key GENERATED ALWAYS AS IDENTITY,
    username varchar(100) not null,
    password varchar(100) not null,
    email varchar(50) not null,
    role varchar(20) not null
);
CREATE TABLE if not exists client(
                        id varchar(255) NOT NULL,
                        clientId varchar(255) NOT NULL,
                        clientIdIssuedAt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        clientName varchar(255) NOT NULL,
                        clientAuthenticationMethods varchar(1000) NOT NULL,
                        authorizationGrantTypes varchar(1000) NOT NULL,
                        redirectUris varchar(1000) DEFAULT NULL,
                        postLogoutRedirectUris varchar(1000) DEFAULT NULL,
                        scopes varchar(1000) NOT NULL,
                        clientSettings varchar(2000) NOT NULL,
                        tokenSettings varchar(2000) NOT NULL,
                        PRIMARY KEY (id)
);