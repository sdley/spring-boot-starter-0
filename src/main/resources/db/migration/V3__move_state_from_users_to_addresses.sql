alter table users
    drop column state;

alter table addresses
    add column state varchar(255) null;