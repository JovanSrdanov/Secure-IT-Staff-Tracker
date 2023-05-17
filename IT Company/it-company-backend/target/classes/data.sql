INSERT INTO public.account
(id, email, employee_id, "password", salt, status)
VALUES('c46b1a28-fe30-4f6b-834c-72fe7de6ee7f', 'mail2', 'aafbd593-de86-4510-8c63-0d912e95b3b6', '$2y$10$.MBsfYd7UyNHzszZPlzvn.JN4WOdAcnwyBUvsBAmi4u/.4MZTjA5W', 'a', 1);

INSERT INTO public.privilege
(id, "name")
VALUES('8fdec74e-b0ee-4928-a962-4bfee21da797', 'permisija');

INSERT INTO public."role"
(id, "name")
VALUES('79113e08-0b50-41ee-a8ea-42559259d44e', 'ROLE_USER');

INSERT INTO public.users_roles
(user_id, role_id)
VALUES('c46b1a28-fe30-4f6b-834c-72fe7de6ee7f', '79113e08-0b50-41ee-a8ea-42559259d44e');

INSERT INTO public.roles_privileges
(role_id, privilege_id)
VALUES('79113e08-0b50-41ee-a8ea-42559259d44e', '8fdec74e-b0ee-4928-a962-4bfee21da797');

