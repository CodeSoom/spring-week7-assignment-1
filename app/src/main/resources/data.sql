insert into authority (authority_name) values ('ADMIN');
insert into authority (authority_name) values ('USER');

insert into user (email, name, password, deleted) values ('admin@test.com', '관리자', '$2a$12$tzOHOi6uamWHFvFnSLEsiuDp/hqh91RFnwO5FPDe5QzrkDp4fgjCK', false);
insert into user (email, name, password, deleted) values ('test@test.com', '사용자', '$2a$12$tzOHOi6uamWHFvFnSLEsiuDp/hqh91RFnwO5FPDe5QzrkDp4fgjCK', false);

insert into user_authority (user_id, authority_name) values (1, 'ADMIN');
insert into user_authority (user_id, authority_name) values (1, 'USER');
insert into user_authority (user_id, authority_name) values (2, 'USER');
