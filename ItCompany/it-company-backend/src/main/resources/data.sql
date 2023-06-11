INSERT INTO public."role"
(id, "name")
values ('79113e08-0b50-41ee-a8ea-42559259d44e', 'ROLE_ENGINEER'),
       ('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'ROLE_ADMIN'),
       ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'ROLE_PROJECT_MANAGER'),
       ('407d5496-2b68-4052-9219-f87ed4126fc9', 'ROLE_HR_MANAGER'),
       ('153c2366-eb2d-4ce2-b55e-42c83609da8a', 'ROLE_ADMIN_PASSWORD_CHANGE');

INSERT INTO public.privilege
(id, "name")
values ('fc784caa-1a09-459f-9fef-d2ce4b1b89e6', 'changeAccStatusAccept'),
       ('2a616b1b-9018-4cf1-aa89-7995a54a2e88', 'changeAccStatusReject'),
       ('1c0ad581-3e6c-4eb0-9d3c-4bef1ac07c25', 'allPendingApproval'),
       ('ecf70d65-5b85-4ef9-971f-611bad77076e', 'updatePrivilege'),
       ('355c6b04-6db3-4330-8ab0-e42538dabe90', 'getAllPrivilege'), ('9e17c976-789a-47e2-a65c-26a0929036f8', 'createProject'), ('69111b7d-910d-49a6-8a43-2c28762b1a25', 'getAllProject'),
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
       ('9f82f05e-1e85-4c4b-b112-6701c8993929', 'registerAdmin'),
       ('d270db68-12fb-4dce-a9f0-7ef64d091731', 'adminPasswordChange'),
       ('b7b7775c-e81a-4ec7-bfbf-81bdd3c15100', 'removeSkillSwEngineer'),
       ('44c764a7-fa8e-4820-8c31-5d882514f65c', 'getAllEmployee'),
       ('aaf59fa1-4d0f-49ef-bd45-755a5985f61d', 'getAllUnemployedOnProjectEngineer'),
       ('931fcaa3-e336-4ba7-b5ba-c136c440bfc2', 'getAllUnemployedOnProjectPRManager'),
       ('602a2482-a824-4b52-91e1-a30b3c0e710d', 'getMySeniority'),
       ('85deb19a-6bbb-4a97-93f1-7d118f17c014', 'getLoggedInInfo'),
       ('ab494841-f5aa-4e4e-b6e2-5d8c1085eb3f', 'updateLoggedInInfo'),
       ('f7fe6f68-08d4-42f0-804f-d49107dfd9ba', 'updateProjectInfo'),
       ('1ebf0eda-ac58-4548-9cf0-69d33508a5a0', 'getPrManagersProjectsById'),
       ('9edb2d3e-3ad5-45d5-bca9-d69b4161988d', 'getSwEngineersProjectsById'),
       ('437bfba6-7add-4e5c-8354-d3bee0d09e0c', 'getAllSkillSwEngineerById'),
       ('a8bc9435-22b3-45d0-8d74-a28f64f388da', 'getAllSkillSwEngineer'),
       ('37667806-a976-43ab-bd1f-76b194a925be', 'getAllLogs'),
       ('e8ef411d-cb88-43eb-8860-6bbf939e09c8', 'uploadCv'),
       ('32f43732-3b5b-4731-aac2-08274efd8eaf', 'readCv'),
       ('3cd5a5f9-dad6-42a0-bcfa-cf2bf787be30', 'getTwoFactorAuthQr');


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
('037bbd08-1f2c-4f9d-80af-1710d90efb01', '44c764a7-fa8e-4820-8c31-5d882514f65c'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'aaf59fa1-4d0f-49ef-bd45-755a5985f61d'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', '931fcaa3-e336-4ba7-b5ba-c136c440bfc2'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'da6aaa1b-6e8e-472a-8598-a27edc2be510'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'ab494841-f5aa-4e4e-b6e2-5d8c1085eb3f'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', '85deb19a-6bbb-4a97-93f1-7d118f17c014'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', 'b3adb8f2-4ea3-41c2-a3e1-709b9e7ba7a8'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', '9f82f05e-1e85-4c4b-b112-6701c8993929'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', '37667806-a976-43ab-bd1f-76b194a925be'),
('037bbd08-1f2c-4f9d-80af-1710d90efb01', '3cd5a5f9-dad6-42a0-bcfa-cf2bf787be30'),
-- PR MANAGER
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'f60d3289-526a-4aae-b720-5409f472cd2b'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'c4640c3d-e9fe-40b3-9158-004fb119b8f6'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'b3adb8f2-4ea3-41c2-a3e1-709b9e7ba7a8'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'aaf59fa1-4d0f-49ef-bd45-755a5985f61d'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'f60d3289-526a-4aae-b720-5409f472cd2b'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'da6aaa1b-6e8e-472a-8598-a27edc2be510'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', '85deb19a-6bbb-4a97-93f1-7d118f17c014'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'ab494841-f5aa-4e4e-b6e2-5d8c1085eb3f'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', 'f7fe6f68-08d4-42f0-804f-d49107dfd9ba'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', '32f43732-3b5b-4731-aac2-08274efd8eaf'),
('2cdfba8e-78a3-46a9-b414-96a41d1a5c62', '3cd5a5f9-dad6-42a0-bcfa-cf2bf787be30'),
-- SOFTWARE ENGINEER
('79113e08-0b50-41ee-a8ea-42559259d44e', '9c571498-c945-4089-a529-8e3746d0a4b3'),
('79113e08-0b50-41ee-a8ea-42559259d44e', 'c7133aa4-d7e9-4f2f-839d-e4524ebd3bb4'),
('79113e08-0b50-41ee-a8ea-42559259d44e', 'c7877299-353d-4938-95da-0e6b97aadd6f'),
('79113e08-0b50-41ee-a8ea-42559259d44e', 'b7b7775c-e81a-4ec7-bfbf-81bdd3c15100'),
('79113e08-0b50-41ee-a8ea-42559259d44e', 'a8bc9435-22b3-45d0-8d74-a28f64f388da'),
('79113e08-0b50-41ee-a8ea-42559259d44e', '602a2482-a824-4b52-91e1-a30b3c0e710d'),
('79113e08-0b50-41ee-a8ea-42559259d44e', '85deb19a-6bbb-4a97-93f1-7d118f17c014'),
('79113e08-0b50-41ee-a8ea-42559259d44e', 'ab494841-f5aa-4e4e-b6e2-5d8c1085eb3f'),
('79113e08-0b50-41ee-a8ea-42559259d44e', 'e8ef411d-cb88-43eb-8860-6bbf939e09c8'),
('79113e08-0b50-41ee-a8ea-42559259d44e', '32f43732-3b5b-4731-aac2-08274efd8eaf'),
('79113e08-0b50-41ee-a8ea-42559259d44e', '3cd5a5f9-dad6-42a0-bcfa-cf2bf787be30'),
-- HR MANAGER
('407d5496-2b68-4052-9219-f87ed4126fc9', '85deb19a-6bbb-4a97-93f1-7d118f17c014'),
('407d5496-2b68-4052-9219-f87ed4126fc9', 'ab494841-f5aa-4e4e-b6e2-5d8c1085eb3f'),
('407d5496-2b68-4052-9219-f87ed4126fc9', '1ebf0eda-ac58-4548-9cf0-69d33508a5a0'),
('407d5496-2b68-4052-9219-f87ed4126fc9', '9edb2d3e-3ad5-45d5-bca9-d69b4161988d'),
('407d5496-2b68-4052-9219-f87ed4126fc9', '437bfba6-7add-4e5c-8354-d3bee0d09e0c'),
('407d5496-2b68-4052-9219-f87ed4126fc9', '44c764a7-fa8e-4820-8c31-5d882514f65c'),
('407d5496-2b68-4052-9219-f87ed4126fc9', '32f43732-3b5b-4731-aac2-08274efd8eaf'),
('407d5496-2b68-4052-9219-f87ed4126fc9', '3cd5a5f9-dad6-42a0-bcfa-cf2bf787be30'),
-- ADMIN_PASSWORD_CHANGE
('153c2366-eb2d-4ce2-b55e-42c83609da8a', 'd270db68-12fb-4dce-a9f0-7ef64d091731');

-- PROJECT
INSERT INTO public.project (id,end_date,start_date,"name") VALUES
    ('1ff88c0c-a1b3-45d4-9c3a-de7a3ceb23c0','2023-06-03 13:18:07.067','2023-06-02 13:18:07.067',decode('C30D040703027114666983AEFFB86DD23C019A61824A56F3BF3C1E5219CA9D4ABDB04F619E0E0D7068D0F0F173F54AC7FD40B94762D0A7BBAF384A59621CE88A20898D833BFF14DA8E3EFDB570','hex'));


-- USER RELATED
-- email: busep2023+n@gmail.com n ∈ [0,3] password: Password1!, mejlovi idu redom: admin, engineer, project manager, hr manager
INSERT INTO public.account (id,email,employee_id,is_activated,is_blocked,"password",salt,status,totp_secret_key) VALUES
                                                                                                                     ('f2d4d90c-49d3-42a1-808e-51bb467de416',decode('C30D040703026352A959037DF54179D2460152B447DAD5B316A1D4D47F392B99EDA1C521BA81FB20AFBE7A641C5387A713690DEF9838C334C09297989DD356B7ACE5DC8D50E4906E126078DDA347156ECD633E5EB29F4E','hex'),'6d8c22fd-967a-4ad4-b065-dc4aa84be30f',true,false,'$2a$10$x1EFrPJcAukqDW0Vudup0eX49au/Cj2aAl00ruT6mNOu2fP790JBe','TgRLnK0C',1,NULL),
                                                                                                                     ('0e58e182-2c76-4ca2-8f9d-c4be5ac99d9f',decode('C30D040703022332B622167AA0837CD2460171DC36AA0A8DD71C2B6DD8D3854E84BD388FF8D19D9C8A76C725FB4351DF80654E367A41B638395BEB9EA6F35F85AF0E942F64F787ACA705241E4CDBEA2E28429684237B7F','hex'),'2a237984-a3ee-423a-bafa-40b5a64a50e8',true,false,'$2a$10$vDum1IDIuQ6mo8ZRZhdiIuUkP9LVneTkEmSajvP4xDlZdqdq1bn7q','1O7APlBk',1,NULL),
                                                                                                                     ('22df719a-21b4-4057-96d3-b882cfcc348f',decode('C30D040703020C288AF22EFD858170D2460156F8C89BB481AC95DE678C3026F306F13C1CDC977FF533F81A4E0A2AE6AA32449981B79E817881541C438114568606EB718246673BCFA04FD9EC9B0CF91DF4EA40F61A700B','hex'),'82a1b113-caf1-4a73-bd13-7472ca89c062',true,false,'$2a$10$83XaZ0TDgmR3t3QZkncSoujKFbH77MBtKso5UnANnnWv5/G/8f7PW','nCHwRQwM',1,NULL),
                                                                                                                     ('304579ea-72f8-4b4a-adc6-4a3e53f90203',decode('C30D040703024753C13E6B5A752060D24601BB12D702E2E6F38A66CC8DC8E022F8918F89B9A3BD3E4D31AFDA05E4771F2C079CCB3AB4391BA632F9036B9E4491B5AAC1262E438ABA30DEBA063BB4B0DE359FEC1AF5ABF4','hex'),'2ff13a71-f324-49e2-b391-33dad4a5b883',true,false,'$2a$10$3h5bH6nLL/6gWYrP8uX4o.cEis/y7/MkclkS7ecq7dgMHGjzwA5y2','Y5UyQOxP',1,NULL),
                                                                                                                     ('79a5d214-1ba4-44f2-8428-6a107265af62',decode('C30D040703023C614586C11EAEDB69D24601BD0B3F1CDDB6E58914DE8D4F42FE7DEAE2E8972ED5DAB7930DD4FB996950FE09AA3DD1697B8D1D59E62F4BFCE848A10D7C6F3580EBE111454E995248085B7A7B61E4ADF864','hex'),'0945a2bd-6fad-4027-bc95-b847411743f9',true,false,'$2a$10$tDmwCkGrqW06aPA9T6k3RexPKdVxldpnsEFbL7M8kKOlNJVN29XTu','Bo4U2AWR',1,decode('C30D04070302DEF540AA5BF119EA63D25101AA33B421B2E9B7AAB4D5600C7A71794CC6786513E180E4F55D117EA7678C152B837FAFA0039CC2404A0EDFCCA1DFBDEF1254C9A58A4CE6E96FCF7212764C290C6B1114F2609E157F973AFAD9AE6CF9A3','hex'));
INSERT INTO public.address (id,city,country,street,street_number) VALUES
                                                                      ('6c74bc31-826e-4911-8e21-4549bccecee6',decode('C30D04070302B35708005479CEAE60D23901546E45EA7C7DA340326DC459EBD11A1AC8CC3E1F0D774DC20B846BE469792E395C3977192F2D68E39F91178C99AF364A829BAD99DA1AB41A','hex'),decode('C30D04070302120A5290CF7EFC8877D2370182411308E29AE8496506E54522320E8BF1FB46E4E528C3F10F67EE38187CBED3A6696E98A63670741414674458FF81A89BD66D2AE277','hex'),decode('C30D04070302C2C881CEBC3DB7FD7DD23901F4DB9C8C132AA7C367CB8164B9A8EBB41C2489211932A6C57E54742F169CFCC342E2E98E8B8C663AA613A2074EF00F95AC0CC7219BAC1222','hex'),decode('C30D04070302C55B082C7BAD380A7DD23201357E888000E6C39189F765992C60BAB9AAC2778BDD1C59D367ED27ECDF43410A71C28C5C3831C46E17A5FFB34F05A2A404','hex')),
                                                                      ('7a000bbf-cb17-4dfb-a5b0-ea906c5cbb83',decode('C30D0407030224D30719F52657A772D23401F75D2A6C73831C3D5833D919A2E3A59FAC71528F86FF4795D7E5F96E07DFDCC603013AFD2E539B4B0FADA39DC9213BA182F13C','hex'),decode('C30D04070302AA21910FC8518AF27CD2370142FD8C6EE46E8B325E087763E4738131730F144BF4D201BDB882EE55915524E1C60CD9691D4B123942056EFB5C31FF19E9947B2BF273','hex'),decode('C30D040703027A0F72802F021CD773D23C01478F6F723CDF540E0E72E86B0D2629AF171F90BE792AA1CE4F36B1A2F589402C6DBB9459273447BE593150B0A5F06B4A5A9778BCAF1908CE837C4C','hex'),decode('C30D04070302DB58A2C8CCA54DB676D233010772FB71F67D26FCD57905C8E76252BF186297C46D09100A097AD28A55D40FFEC20FE39631D408F091E5D0B1FB9AF3F812D4','hex')),
                                                                      ('59b87cbe-973b-43ca-8b2c-dd40d68ea738',decode('C30D04070302208D4DBDF2CF02DC6BD23A016A9D13989101F51DAB8C09B6D8CC4CF8F7CCB30B9A1B1730D8DFDDCD0EEF6E8A450FCB4DF257422305717373F61CE9161C07F51838FBCD1BBB','hex'),decode('C30D040703025A5FEED0BD0497586CD23701E3DCCA59338101B0450AAB9A22DD0DF8941824D708570CDE61B45BBCECCADD5B32FD9939CDCFB8C85DC53B573F9A1EB14123EA15B54C','hex'),decode('C30D04070302A2F64F8F11E532EB68D2390118563C8247CB68B789F988D756366C50B0552D3177630BF73485BD6E28B4DEE7063CEEE3884434040DE42D2ACB1A90348D8A60DC8396C4C7','hex'),decode('C30D040703028B6CE2AEEC388E2B74D233018317813518A63CD74A16C97F29EE2CCE97EB8281DD34ED51AC86F478B9501C5D190C2CED754636B23AA58A9EFCE53963DA99','hex')),
                                                                      ('3d5bc7aa-bf49-44f0-9482-4e4440b04d0f',decode('C30D04070302F798FE4C71AA542275D2390166613EAFA19842CC75E688368FCD172A5F41F05A1A7461962A82EBD8CB5BD20497528AA99019BE483785A537AA94855E8CACC1AF0BDDBCB5','hex'),decode('C30D04070302D7845431046C470070D2370132D4A8D1ABE48594B9BC4B2BFCBD22CBC296DAE284F2136A3BFB3EF2B8AEFE9A5E1A9797D37DA869AE766991D71DA6BB674603569DBC','hex'),decode('C30D0407030280FECE35FDF843AC64D2390112B5D34F10866EDE3A0A89AA108991BD5F7859EB0AD92789DF2339B66A9E104C04A0086FB346EC9B675232BE20F4F78610DF71E11B8AC7CC','hex'),decode('C30D0407030225798188D90088F564D23301D28C874E05164B1CC27B02C9DEBB6518A42F4593F10038A89CE8CF98E05432EB477316E9B499636EDBBA4ACFFA2799A2E883','hex')),
                                                                      ('66549ac0-07a5-4099-ade0-07999c6b010a',decode('C30D0407030204F67BC34D0794A67DD23801AEEDA489D7264B75F22AEC7A6A3E10912D4656D5B3AF9563DBF3B3EF20346F080AD61190EFF8C37A01C30D0E65DF7F7B23DAEDFD899364','hex'),decode('C30D04070302A6B6617748C2CE5068D237012D2F5753E951B89B67A43FB8E2A168B0B574C62E5E1BB7F8C050B8B01CA00C21986E3CC858111D070C103A50CC15CCEEC0B3D8C5CED7','hex'),decode('C30D04070302F2B3FE1726C712E86CD239017F4F72F33D34F66605DACF19F378D1F8E70E321B5D9CB89A547A780E13191539DB5B54A62D99E45BAFAD91571FE003236006A29C404F2D4E','hex'),decode('C30D04070302F7C3CCF73EC1CB2B72D2340171A6448FCE8A4B1B33D0F012321C893D50819BDDD3F70288B389AF6A08B273268351A027289275BF1AE3650BFAEEE4AC8625B8','hex'));
INSERT INTO public.roles_privileges (role_id,privilege_id) VALUES
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','1c0ad581-3e6c-4eb0-9d3c-4bef1ac07c25'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','e020ef1f-eb11-4d1d-bedf-b87c033de306'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','fc797a4e-009a-44de-aec0-d7ca52c64609'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','2a616b1b-9018-4cf1-aa89-7995a54a2e88'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','fc784caa-1a09-459f-9fef-d2ce4b1b89e6'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','ecf70d65-5b85-4ef9-971f-611bad77076e'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','355c6b04-6db3-4330-8ab0-e42538dabe90'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','9e17c976-789a-47e2-a65c-26a0929036f8'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','69111b7d-910d-49a6-8a43-2c28762b1a25'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','f60d3289-526a-4aae-b720-5409f472cd2b');
INSERT INTO public.roles_privileges (role_id,privilege_id) VALUES
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','c4640c3d-e9fe-40b3-9158-004fb119b8f6'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','250da776-58ea-4889-b777-531f826787b1'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','dad8fcfd-4422-4a3a-b79d-6db3d25e965e'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','7facde86-1695-4281-aeb9-34fda5913f05'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','44c764a7-fa8e-4820-8c31-5d882514f65c'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','aaf59fa1-4d0f-49ef-bd45-755a5985f61d'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','931fcaa3-e336-4ba7-b5ba-c136c440bfc2'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','da6aaa1b-6e8e-472a-8598-a27edc2be510'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','ab494841-f5aa-4e4e-b6e2-5d8c1085eb3f'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','85deb19a-6bbb-4a97-93f1-7d118f17c014');
INSERT INTO public.roles_privileges (role_id,privilege_id) VALUES
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','b3adb8f2-4ea3-41c2-a3e1-709b9e7ba7a8'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','9f82f05e-1e85-4c4b-b112-6701c8993929'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','37667806-a976-43ab-bd1f-76b194a925be'),
                                                               ('037bbd08-1f2c-4f9d-80af-1710d90efb01','3cd5a5f9-dad6-42a0-bcfa-cf2bf787be30'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','f60d3289-526a-4aae-b720-5409f472cd2b'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','c4640c3d-e9fe-40b3-9158-004fb119b8f6'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','b3adb8f2-4ea3-41c2-a3e1-709b9e7ba7a8'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','aaf59fa1-4d0f-49ef-bd45-755a5985f61d'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','f60d3289-526a-4aae-b720-5409f472cd2b'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','da6aaa1b-6e8e-472a-8598-a27edc2be510');
INSERT INTO public.roles_privileges (role_id,privilege_id) VALUES
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','85deb19a-6bbb-4a97-93f1-7d118f17c014'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','ab494841-f5aa-4e4e-b6e2-5d8c1085eb3f'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','f7fe6f68-08d4-42f0-804f-d49107dfd9ba'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','32f43732-3b5b-4731-aac2-08274efd8eaf'),
                                                               ('2cdfba8e-78a3-46a9-b414-96a41d1a5c62','3cd5a5f9-dad6-42a0-bcfa-cf2bf787be30'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','9c571498-c945-4089-a529-8e3746d0a4b3'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','c7133aa4-d7e9-4f2f-839d-e4524ebd3bb4'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','c7877299-353d-4938-95da-0e6b97aadd6f'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','b7b7775c-e81a-4ec7-bfbf-81bdd3c15100'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','a8bc9435-22b3-45d0-8d74-a28f64f388da');
INSERT INTO public.roles_privileges (role_id,privilege_id) VALUES
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','602a2482-a824-4b52-91e1-a30b3c0e710d'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','85deb19a-6bbb-4a97-93f1-7d118f17c014'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','ab494841-f5aa-4e4e-b6e2-5d8c1085eb3f'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','e8ef411d-cb88-43eb-8860-6bbf939e09c8'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','32f43732-3b5b-4731-aac2-08274efd8eaf'),
                                                               ('79113e08-0b50-41ee-a8ea-42559259d44e','3cd5a5f9-dad6-42a0-bcfa-cf2bf787be30'),
                                                               ('407d5496-2b68-4052-9219-f87ed4126fc9','85deb19a-6bbb-4a97-93f1-7d118f17c014'),
                                                               ('407d5496-2b68-4052-9219-f87ed4126fc9','ab494841-f5aa-4e4e-b6e2-5d8c1085eb3f'),
                                                               ('407d5496-2b68-4052-9219-f87ed4126fc9','1ebf0eda-ac58-4548-9cf0-69d33508a5a0'),
                                                               ('407d5496-2b68-4052-9219-f87ed4126fc9','9edb2d3e-3ad5-45d5-bca9-d69b4161988d');
INSERT INTO public.roles_privileges (role_id,privilege_id) VALUES
                                                               ('407d5496-2b68-4052-9219-f87ed4126fc9','437bfba6-7add-4e5c-8354-d3bee0d09e0c'),
                                                               ('407d5496-2b68-4052-9219-f87ed4126fc9','44c764a7-fa8e-4820-8c31-5d882514f65c'),
                                                               ('407d5496-2b68-4052-9219-f87ed4126fc9','32f43732-3b5b-4731-aac2-08274efd8eaf'),
                                                               ('407d5496-2b68-4052-9219-f87ed4126fc9','3cd5a5f9-dad6-42a0-bcfa-cf2bf787be30'),
                                                               ('153c2366-eb2d-4ce2-b55e-42c83609da8a','d270db68-12fb-4dce-a9f0-7ef64d091731');
INSERT INTO public.software_engineer (id,name,phone_number,profession,surname,date_of_employment,address_id,cv_id) VALUES
                                                                                                                       ('6d8c22fd-967a-4ad4-b065-dc4aa84be30f',decode('C30D040703028CA0E4F9DC073E016BD23601D375C4C8DC71A60AD062B5ADCBF9236CB755062E7C1E31436AC172A6E3824CF310DEB1152E221FAC25481C473A0DF22E99C8786155','hex'),decode('C30D04070302FC3E6E24C372E1BB6ED23A01981C7253BBF544D47F5543488AD0545F45210B44BD7BA8B47C2A04B42B3E0CB04F1EFCF4D5451BA926CA874BFF521DB12FF1122D2C7F4DFBF1','hex'),decode('C30D040703021999A092169349E76BD243016A3B2FD9A563792E68A4DD6A93C575DF41A2E883389E163F832161183758AF5DB1B811AF86EEF1F475BF9200B4E04B0AAC238DAAE91B5A8ABA6B88B3F5B31596B483','hex'),decode('C30D0407030246A7F11B7E1580BC69D2380189E71E7754EFF073FE28EB854CA4878C2EC9302F0199F697FB6024D23E8A919D69F0BA0CA4B9392634AD0F19634D5DC6654420081A40B2','hex'),decode('C30D04070302264A118A03E15B3C71D24B01AC342508B22207231A1CC304296359B21160F73E05A557673E8FBF7132E6148C404FE522FF45FD2001809BEB57A6325896C44D5940EE73782E280DB981D0E01294E8C61FCA73340956B2','hex'),'6c74bc31-826e-4911-8e21-4549bccecee6',NULL),
                                                                                                                       ('0945a2bd-6fad-4027-bc95-b847411743f9',decode('C30D04070302E1B2B0FE4F6AD53577D2360139C213C9C65F878AF292C31A14A01794E762618C4E98709F1EF073465769E38F401EA8D2A43C447C7B2A06C0B1F594CABB5F033150','hex'),decode('C30D0407030288F943FA984C10E179D23D018BA194FEE8D54E714FEDB86FB83E6D5686B259447DDE59EBE79DE86DB2443C8C8B7DFF33842FDC077913F6DA6173D7DA26C1771A171B9715360B40B0','hex'),decode('C30D04070302A60488BBABEFF69C7AD23C01CED91CDFEC9AF2175C4DCB7FC6D7B3FC027F628D2D7B839FB3143B3084BE424AF97473539D47614A6E742E0C549290411780D9D051101ACD73BA62','hex'),decode('C30D04070302726DBBDFCB5CF8CC7FD238013D54ABE57A98256DF00074E3C404DE42F5A5763EB802B7E6363795883532BDB34EE917EE922DDD8D754A4E2D514568A54DC5D749A23933','hex'),decode('C30D04070302663E6F65E03414F371D24B01AFD243E8D380DBA6360F0C7E12AC7B322AFACFDEDD32E0B93F58CF5A451BE74C0A637DF196FD0961ADED05D998400F8BC0CFCA6F245A7D9869B32FBDC72ECC56C2AE29C87887A230A078','hex'),'66549ac0-07a5-4099-ade0-07999c6b010a',NULL);
INSERT INTO public.users_roles (user_id,role_id) VALUES
                                                     ('f2d4d90c-49d3-42a1-808e-51bb467de416','79113e08-0b50-41ee-a8ea-42559259d44e'),
                                                     ('0e58e182-2c76-4ca2-8f9d-c4be5ac99d9f','2cdfba8e-78a3-46a9-b414-96a41d1a5c62'),
                                                     ('22df719a-21b4-4057-96d3-b882cfcc348f','037bbd08-1f2c-4f9d-80af-1710d90efb01'),
                                                     ('304579ea-72f8-4b4a-adc6-4a3e53f90203','407d5496-2b68-4052-9219-f87ed4126fc9'),
                                                     ('79a5d214-1ba4-44f2-8428-6a107265af62','79113e08-0b50-41ee-a8ea-42559259d44e');
