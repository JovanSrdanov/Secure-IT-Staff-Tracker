INSERT INTO public.address (id,city,country,street,street_number) VALUES
    ('d3adcff2-ae88-495b-91f6-234d6eba6d11','Nova Pazova','Srbija','Zlatne Grede','12');

INSERT INTO public.project_manager (id,name,phone_number,profession,surname,address_id) VALUES
    ('cf11ba92-dea3-4d80-b2f0-8187060cb831','Smilja','123456','Proffessional air breather','Uskokovic','d3adcff2-ae88-495b-91f6-234d6eba6d11');


INSERT INTO public.account
(id, email, employee_id, "password", salt, status, is_activated)
values
    ('c46b1a28-fe30-4f6b-834c-72fe7de6ee7f', 'user', 'aafbd593-de86-4510-8c63-0d912e95b3b6', '$2y$10$.MBsfYd7UyNHzszZPlzvn.JN4WOdAcnwyBUvsBAmi4u/.4MZTjA5W', 'a', 1, true),
    ('9d0dc40b-a0c6-4610-ac51-23ed75b94a9a', 'admin', '6b977f75-db33-44fd-8b8f-19bf87d82c65', '$2y$10$.MBsfYd7UyNHzszZPlzvn.JN4WOdAcnwyBUvsBAmi4u/.4MZTjA5W', 'a', 1, true),
    ('be256f3c-48a3-449d-86e5-4bb1165122b3','pmanager','cf11ba92-dea3-4d80-b2f0-8187060cb831','$2a$10$0novzsHN77x0.ks6uCT2SuyXnkH2xnVZ12e5GcYTakKDuXAWbCViG','8AajvCjO', 1,true);

INSERT INTO public.privilege
(id, "name")
values
    ('8fdec74e-b0ee-4928-a962-4bfee21da797', 'permisija'),
    ('fc784caa-1a09-459f-9fef-d2ce4b1b89e6', 'chagneAccStatus'),
    ('ecf70d65-5b85-4ef9-971f-611bad77076e', 'updatePrivilege'),
    ('355c6b04-6db3-4330-8ab0-e42538dabe90', 'getPrivilege'),
    ('9e17c976-789a-47e2-a65c-26a0929036f8', 'createProject'),
    ('69111b7d-910d-49a6-8a43-2c28762b1a25', 'getAllProject'),
    ('f60d3289-526a-4aae-b720-5409f472cd2b', 'addSwEngineerToProject'),
    ('da6aaa1b-6e8e-472a-8598-a27edc2be510', 'dismissSwEngineerFromProject'),
    ('c4640c3d-e9fe-40b3-9158-004fb119b8f6', 'getSwEngineersOnProject'),
    ('250da776-58ea-4889-b777-531f826787b1', 'addPrManagerToProject'),
    ('dad8fcfd-4422-4a3a-b79d-6db3d25e965e', 'dismissPrManagerFromProject'),
    ('7facde86-1695-4281-aeb9-34fda5913f05', 'getPrManagersOnProject'),
    ('b3adb8f2-4ea3-41c2-a3e1-709b9e7ba7a8', 'getPrManagersProjects');



INSERT INTO public."role"
(id, "name")
values
    ('79113e08-0b50-41ee-a8ea-42559259d44e', 'ROLE_ENGINEER'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'ROLE_ADMIN'),
    ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'ROLE_PROJECT_MANAGER'),
    ('407d5496-2b68-4052-9219-f87ed4126fc9', 'ROLE_HR_MANAGER');

INSERT INTO public.users_roles
(user_id, role_id)
values
    ('c46b1a28-fe30-4f6b-834c-72fe7de6ee7f', '79113e08-0b50-41ee-a8ea-42559259d44e'),
    ('9d0dc40b-a0c6-4610-ac51-23ed75b94a9a', '037bbd08-1f2c-4f9d-80af-1710d90efb01'),
    ('be256f3c-48a3-449d-86e5-4bb1165122b3', '2cdfba8e-78a3-46a9-b414-96a41d1a5c62');

INSERT INTO public.roles_privileges
(role_id, privilege_id)
values
-- ADMIN
    ('79113e08-0b50-41ee-a8ea-42559259d44e', '8fdec74e-b0ee-4928-a962-4bfee21da797'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'fc784caa-1a09-459f-9fef-d2ce4b1b89e6'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'ecf70d65-5b85-4ef9-971f-611bad77076e'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', '355c6b04-6db3-4330-8ab0-e42538dabe90'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', '9e17c976-789a-47e2-a65c-26a0929036f8'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', '69111b7d-910d-49a6-8a43-2c28762b1a25'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'f60d3289-526a-4aae-b720-5409f472cd2b'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'c4640c3d-e9fe-40b3-9158-004fb119b8f6'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', '250da776-58ea-4889-b777-531f826787b1'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'dad8fcfd-4422-4a3a-b79d-6db3d25e965e'),
    ('037bbd08-1f2c-4f9d-80af-1710d90efb01', '7facde86-1695-4281-aeb9-34fda5913f05'),
-- PR MANAGER
    ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'da6aaa1b-6e8e-472a-8598-a27edc2be510'),
    ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'b3adb8f2-4ea3-41c2-a3e1-709b9e7ba7a8');




INSERT INTO public.users_roles
(user_id, role_id)
values
    ('c46b1a28-fe30-4f6b-834c-72fe7de6ee7f', '79113e08-0b50-41ee-a8ea-42559259d44e'),
    ('9d0dc40b-a0c6-4610-ac51-23ed75b94a9a', '037bbd08-1f2c-4f9d-80af-1710d90efb01');



--TODO OBRISI
INSERT INTO public.software_engineer ("id", "name", "phone_number", "profession", "surname")
VALUES('d2cdc04e-a199-46d3-b202-4289c6b1ea3b', 'Marko', '1234567', 'DevOps', 'Markovic' );

INSERT INTO public.project (id,start_date,end_date,"name") VALUES
    ('6eeb9244-9c87-4545-a940-fe39bf3a268f','2023-05-25 02:00:00','2023-09-25 02:00:00','Mega projekat');
