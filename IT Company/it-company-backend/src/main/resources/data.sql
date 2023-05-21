INSERT INTO public.address (id, city, country, street, street_number)
VALUES ('d3adcff2-ae88-495b-91f6-234d6eba6d11', 'Nova Pazova', 'Srbija', 'Zlatne Grede', '12'),
       ('85015489-7336-4636-b6e1-68343be88f09', 'Loznica', 'Srbija', 'Lekarska', '10');


INSERT INTO public.project_manager (id, name, phone_number, profession, surname, address_id)
VALUES ('cf11ba92-dea3-4d80-b2f0-8187060cb831', 'Smilja', '123456', 'Proffessional air breather', 'Uskokovic',
        'd3adcff2-ae88-495b-91f6-234d6eba6d11');


INSERT INTO public.software_engineer (id, name, phone_number, profession, surname, date_of_employment, address_id,
                                      cv_id)
VALUES ('c390e0cf-9c9d-41b7-80f9-b55939cc11e8', 'Petar', '123456', '.Net Senior', 'Popovic', NULL,
        '85015489-7336-4636-b6e1-68343be88f09', NULL);


INSERT INTO public.account
    (id, email, employee_id, "password", salt, status, is_activated)
values ('9d0dc40b-a0c6-4610-ac51-23ed75b94a9a', 'jovansrdanov2000+1@gmail.com', '6b977f75-db33-44fd-8b8f-19bf87d82c65',
        '$2y$10$lFp.7xd3uHzrVnftpUcCFOPlEdsU8n76eUy4A/e8c0Eg/.AAc9M8y', 'a', 1, true),
       ('be256f3c-48a3-449d-86e5-4bb1165122b3', 'jovansrdanov2000+3@gmail.com', 'cf11ba92-dea3-4d80-b2f0-8187060cb831',
        '$2y$10$lFp.7xd3uHzrVnftpUcCFOPlEdsU8n76eUy4A/e8c0Eg/.AAc9M8y', 'a', 1, true),
       ('a7c0173e-0bcc-4df7-96b4-481d582dea60', 'jovansrdanov2000+2@gmail.com', 'c390e0cf-9c9d-41b7-80f9-b55939cc11e8',
        '$2y$10$lFp.7xd3uHzrVnftpUcCFOPlEdsU8n76eUy4A/e8c0Eg/.AAc9M8y', 'a', 1, true);


INSERT INTO public.privilege
    (id, "name")
values ('fc784caa-1a09-459f-9fef-d2ce4b1b89e6', 'changeAccStatusAccept'),
       ('2a616b1b-9018-4cf1-aa89-7995a54a2e88', 'changeAccStatusReject'),
       ('1c0ad581-3e6c-4eb0-9d3c-4bef1ac07c25', 'allPendingApproval'),
       ('ecf70d65-5b85-4ef9-971f-611bad77076e', 'updatePrivilege'),
       ('355c6b04-6db3-4330-8ab0-e42538dabe90', 'getAllPrivilege'),
       ('9e17c976-789a-47e2-a65c-26a0929036f8', 'createProject'),
       ('69111b7d-910d-49a6-8a43-2c28762b1a25', 'getAllProject'),
       ('f60d3289-526a-4aae-b720-5409f472cd2b', 'addSwEngineerToProject'),
       ('da6aaa1b-6e8e-472a-8598-a27edc2be510', 'dismissSwEngineerFromProject'),
       ('c4640c3d-e9fe-40b3-9158-004fb119b8f6', 'getSwEngineersOnProject'),
       ('250da776-58ea-4889-b777-531f826787b1', 'addPrManagerToProject'),
       ('dad8fcfd-4422-4a3a-b79d-6db3d25e965e', 'dismissPrManagerFromProject'),
       ('7facde86-1695-4281-aeb9-34fda5913f05', 'getPrManagersOnProject'),
       ('b3adb8f2-4ea3-41c2-a3e1-709b9e7ba7a8', 'getPrManagersProjects'),
       ('9c571498-c945-4089-a529-8e3746d0a4b3', 'getSwEngineersProjects'),
       ('c7133aa4-d7e9-4f2f-839d-e4524ebd3bb4', 'changeSwEngineersJobDescription'),
       ('c7877299-353d-4938-95da-0e6b97aadd6f', 'addSkillSwEngineer'),
       ('e020ef1f-eb11-4d1d-bedf-b87c033de306', 'getAllPrivilegeForRole'),
       ('fc797a4e-009a-44de-aec0-d7ca52c64609', 'getRoles'),
       ('d270db68-12fb-4dce-a9f0-7ef64d091731', 'adminPasswordChange'),
       ('b7b7775c-e81a-4ec7-bfbf-81bdd3c15100', 'removeSkillSwEngineer');



INSERT INTO public."role"
    (id, "name")
values ('79113e08-0b50-41ee-a8ea-42559259d44e', 'ROLE_ENGINEER'),
       ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'ROLE_ADMIN'),
       ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'ROLE_PROJECT_MANAGER'),
       ('407d5496-2b68-4052-9219-f87ed4126fc9', 'ROLE_HR_MANAGER'),
       ('153c2366-eb2d-4ce2-b55e-42c83609da8a', 'ROLE_ADMIN_PASSWORD_CHANGE');

INSERT INTO public.users_roles
    (user_id, role_id)
values ('9d0dc40b-a0c6-4610-ac51-23ed75b94a9a', '037bbd08-1f2c-4f9d-80af-1710d90efb01'),
       ('be256f3c-48a3-449d-86e5-4bb1165122b3', '2cdfba8e-78a3-46a9-b414-96a41d1a5c62'),
       ('a7c0173e-0bcc-4df7-96b4-481d582dea60', '79113e08-0b50-41ee-a8ea-42559259d44e');

INSERT INTO public.roles_privileges
    (role_id, privilege_id)
values
-- ADMIN
('037bbd08-1f2c-4f9d-80af-1710d90efb01', '1c0ad581-3e6c-4eb0-9d3c-4bef1ac07c25'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'e020ef1f-eb11-4d1d-bedf-b87c033de306'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'fc797a4e-009a-44de-aec0-d7ca52c64609'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', '2a616b1b-9018-4cf1-aa89-7995a54a2e88'),
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
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'b3adb8f2-4ea3-41c2-a3e1-709b9e7ba7a8'),
-- SOFTWARE ENGINEER
('79113e08-0b50-41ee-a8ea-42559259d44e', '9c571498-c945-4089-a529-8e3746d0a4b3'),
('79113e08-0b50-41ee-a8ea-42559259d44e', 'c7133aa4-d7e9-4f2f-839d-e4524ebd3bb4'),
('79113e08-0b50-41ee-a8ea-42559259d44e', 'c7877299-353d-4938-95da-0e6b97aadd6f'),
('79113e08-0b50-41ee-a8ea-42559259d44e', 'b7b7775c-e81a-4ec7-bfbf-81bdd3c15100'),
-- ADMIN_PASSWORD_CHANGE
('153c2366-eb2d-4ce2-b55e-42c83609da8a', 'd270db68-12fb-4dce-a9f0-7ef64d091731');

INSERT INTO public.project (id, start_date, end_date, "name")
VALUES ('6eeb9244-9c87-4545-a940-fe39bf3a268f', '2023-05-25 02:00:00', '2023-09-25 02:00:00', 'Mega projekat');
