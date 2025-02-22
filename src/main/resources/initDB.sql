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