CREATE TABLE if not exists "users"(
    id int primary key GENERATED ALWAYS AS IDENTITY,
    username varchar(100) not null,
    password varchar(100) not null,
    email varchar(50) not null,
    role varchar(20) not null
);