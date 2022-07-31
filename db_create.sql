create sequence hibernate_sequence start with 1 increment by 1;
create table phone (id integer not null, citycode varchar(255) not null, countrycode varchar(255) not null, is_active boolean not null, number varchar(255) not null, user_id binary(255) not null, primary key (id));
create table rol (id integer not null, is_active boolean not null, name varchar(255) not null, primary key (id));
create table user (id binary(255) not null, created timestamp not null, email varchar(255) not null, is_active boolean not null, last_login timestamp, modified timestamp, name varchar(255) not null, password varchar(255) not null, token varchar(255), primary key (id));
create table user_has_role (user_id binary(255) not null, role_id integer not null, primary key (user_id, role_id));
alter table phone add constraint FKb0niws2cd0doybhib6srpb5hh foreign key (user_id) references user;
alter table user_has_role add constraint FKkohj47wi7enet9snaqb0ptdqb foreign key (role_id) references rol;
alter table user_has_role add constraint FKdtkvc2iy3ph1rkvd67yl2t13m foreign key (user_id) references user;
create sequence hibernate_sequence start with 1 increment by 1;
create table phone (id integer not null, citycode varchar(255) not null, countrycode varchar(255) not null, is_active boolean not null, number varchar(255) not null, user_id binary(255) not null, primary key (id));
create table rol (id integer not null, is_active boolean not null, name varchar(255) not null, primary key (id));
create table user (id binary(255) not null, created timestamp not null, email varchar(255) not null, is_active boolean not null, last_login timestamp, modified timestamp, name varchar(255) not null, password varchar(255) not null, token varchar(255), primary key (id));
create table user_has_role (user_id binary(255) not null, role_id integer not null, primary key (user_id, role_id));
alter table phone add constraint FKb0niws2cd0doybhib6srpb5hh foreign key (user_id) references user;
alter table user_has_role add constraint FKkohj47wi7enet9snaqb0ptdqb foreign key (role_id) references rol;
alter table user_has_role add constraint FKdtkvc2iy3ph1rkvd67yl2t13m foreign key (user_id) references user;
create sequence hibernate_sequence start with 1 increment by 1;

    create table PHONE (
       id integer not null,
        citycode varchar(255) not null,
        countrycode varchar(255) not null,
        is_active boolean not null,
        number varchar(255) not null,
        user_id binary(255) not null,
        primary key (id)
    );

    create table ROL (
       id integer not null,
        is_active boolean not null,
        name varchar(255) not null,
        primary key (id)
    );

    create table USER (
       id binary(255) not null,
        created timestamp not null,
        email varchar(255) not null,
        is_active boolean not null,
        last_login timestamp,
        modified timestamp,
        name varchar(255) not null,
        password varchar(255) not null,
        token varchar(255),
        primary key (id)
    );

    create table USER_HAS_ROLE (
       user_id binary(255) not null,
        role_id integer not null,
        primary key (user_id, role_id)
    );

    alter table PHONE 
       add constraint FKtbyf36jnq9q992mpecv51e8re 
       foreign key (user_id) 
       references USER;

    alter table USER_HAS_ROLE 
       add constraint FKewipwjgauya14upj8uigbpcov 
       foreign key (role_id) 
       references ROL;

    alter table USER_HAS_ROLE 
       add constraint FKl4j01ufs0h5b2wsfbi8a2lk6c 
       foreign key (user_id) 
       references USER;
