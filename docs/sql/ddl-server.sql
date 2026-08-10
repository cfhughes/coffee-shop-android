create table favorite
(
    profile_id uuid not null,
    shop_id    uuid not null,
    primary key (profile_id, shop_id)
);
create table interest
(
    id       uuid not null,
    category varchar(127),
    primary key (id)
);
create table preference
(
    importance  numeric(38, 2),
    interest_id uuid not null,
    profile_id  uuid not null,
    primary key (interest_id, profile_id)
);
create table profile
(
    id               uuid         not null,
    name             varchar(63)  not null,
    email            varchar(127) not null unique,
    activation_token bpchar(32),
    password_hash    bpchar(97)   not null,
    primary key (id)
);
create table rating
(
    value       numeric(38, 2),
    interest_id uuid not null,
    visit_id    uuid not null,
    primary key (interest_id, visit_id)
);
create table shop
(
    lat       numeric(9, 6),
    lng       numeric(9, 6),
    id        uuid        not null,
    phone     varchar(31),
    name      varchar(63) not null,
    address   varchar(255),
    image_url varchar(255),
    hours     json        not null,
    primary key (id)
);
create table visit
(
    created_at timestamp(6) with time zone not null,
    id         uuid                        not null,
    profile_id uuid                        not null,
    shop_id    uuid                        not null,
    primary key (id)
);
alter table if exists favorite
    add constraint FK5bljmnj70l1vrcxi2iv64gbgr foreign key (shop_id) references shop;
alter table if exists favorite
    add constraint FKbacg9rtul0wcu8rfkoi5p9t6n foreign key (profile_id) references profile;
alter table if exists preference
    add constraint FKrbyw2ipb6y9fweeo9mmenro0f foreign key (interest_id) references interest;
alter table if exists preference
    add constraint FKt9nx3if254ip3siixlnehlpj7 foreign key (profile_id) references profile;
alter table if exists rating
    add constraint FKdybvimsie4une4hbtpysmj81q foreign key (interest_id) references interest;
alter table if exists rating
    add constraint FK756divrad1df9q07rlykbnugu foreign key (visit_id) references visit;
alter table if exists visit
    add constraint FKep572lg2887p0k1osx0l69ouc foreign key (profile_id) references profile;
alter table if exists visit
    add constraint FK1vcxus8c2jkqrdgtfb4owh16a foreign key (shop_id) references shop;
