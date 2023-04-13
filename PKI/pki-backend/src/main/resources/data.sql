INSERT INTO ROLE (name)
VALUES ('ROLE_PKI_ADMIN');
INSERT INTO ROLE (name)
VALUES ('ROLE_CERTIFICATE_USER');
INSERT INTO ROLE (name)
VALUES ('ROLE_CERTIFICATE_USER_CHANGE_PASSWORD');

INSERT INTO "account" (id, email, password, salt)
VALUES ('20d0dad6-bd22-4898-917f-935c080bab76', 'admin@gmail.com',
        '$2a$10$SdVFUY0Ev0vJVqWNE6GRwuJLhbYWFjUmqIMJ7xcU9myNm6ObGsX5S', 'salt');


INSERT INTO "account_role" ("account_id", "role_id")
VALUES ('20d0dad6-bd22-4898-917f-935c080bab76', '1');