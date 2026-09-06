create table users
(
    id       bigint auto_increment
        primary key,
    name     varchar(255) not null,
    email    varchar(255) not null,
    password varchar(255) not null
);

create table addresses
(
    id      bigint auto_increment
        primary key,
    street  varchar(255) null,
    city    varchar(255) null,
    zip     varchar(255) null,
    user_id bigint       null,
    constraint addresses_users_id_fk
        foreign key (user_id) references users (id)
);

