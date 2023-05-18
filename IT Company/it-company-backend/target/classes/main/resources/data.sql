INSERT INTO public.account
(id, email, employee_id, "password", salt, status, is_activated)
values
    ('c46b1a28-fe30-4f6b-834c-72fe7de6ee7f', 'user', 'aafbd593-de86-4510-8c63-0d912e95b3b6', '$2y$10$.MBsfYd7UyNHzszZPlzvn.JN4WOdAcnwyBUvsBAmi4u/.4MZTjA5W', 'a', 1, true),
    ('9d0dc40b-a0c6-4610-ac51-23ed75b94a9a', 'admin', '6b977f75-db33-44fd-8b8f-19bf87d82c65', '$2y$10$.MBsfYd7UyNHzszZPlzvn.JN4WOdAcnwyBUvsBAmi4u/.4MZTjA5W', 'a', 1, true);

INSERT INTO public.privilege
(id, "name")
values
    ('8fdec74e-b0ee-4928-a962-4bfee21da797', 'permisija'),
    ('fc784caa-1a09-459f-9fef-d2ce4b1b89e6', 'chagneAccStatus'),
    ('ecf70d65-5b85-4ef9-971f-611bad77076e', 'updatePrivilege'),
    ('355c6b04-6db3-4330-8ab0-e42538dabe90', 'getPrivilege'),
    ('9e17c976-789a-47e2-a65c-26a0929036f8', 'createProject'),
    ('69111b7d-910d-49a6-8a43-2c28762b1a25', 'getAllProject');



INSERT INTO public."role"
(id, "name")
values
    ('79113e08-0b50-41ee-a8ea-42559259d44e', 'ROLE_USER'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'ROLE_ADMIN');

INSERT INTO public.roles_privileges
(role_id, privilege_id)
values
    ('79113e08-0b50-41ee-a8ea-42559259d44e', '8fdec74e-b0ee-4928-a962-4bfee21da797'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'fc784caa-1a09-459f-9fef-d2ce4b1b89e6'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'ecf70d65-5b85-4ef9-971f-611bad77076e'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', '355c6b04-6db3-4330-8ab0-e42538dabe90'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', '9e17c976-789a-47e2-a65c-26a0929036f8'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', '69111b7d-910d-49a6-8a43-2c28762b1a25');




INSERT INTO public.users_roles
(user_id, role_id)
values
    ('c46b1a28-fe30-4f6b-834c-72fe7de6ee7f', '79113e08-0b50-41ee-a8ea-42559259d44e'),
    ('9d0dc40b-a0c6-4610-ac51-23ed75b94a9a', '037bbd08-1f2c-4f9d-80af-1710d90efb01');
