INSERT INTO public."role" ("name") VALUES
	 ('ROLE_PKI_ADMIN'),
	 ('ROLE_CERTIFICATE_USER'),
	 ('ROLE_CERTIFICATE_USER_CHANGE_PASSWORD');


INSERT INTO public.account (id,email,"password",salt) VALUES
	 ('20d0dad6-bd22-4898-917f-935c080bab76','admin@gmail.com','$2a$10$SdVFUY0Ev0vJVqWNE6GRwuJLhbYWFjUmqIMJ7xcU9myNm6ObGsX5S','salt'),
	 ('05606b28-a4e9-43e1-a7df-dedd9e76375a','s@gmail.com','password','salt'),
	 ('05ca48ff-9e67-4be6-b46b-a3373fbb22f2','s2@gmail.com','password','salt'),
	 ('094dda90-edfc-4099-b601-1cc2962c0655','in@gmail.com','password','salt'),
	 ('e8cf14e5-89d7-4625-bff8-fa9ca154d23e','en@gmail.com','password','salt');


INSERT INTO public.account_role (account_id,role_id) VALUES
	 ('20d0dad6-bd22-4898-917f-935c080bab76',1),
	 ('05606b28-a4e9-43e1-a7df-dedd9e76375a',3),
	 ('05ca48ff-9e67-4be6-b46b-a3373fbb22f2',3),
	 ('094dda90-edfc-4099-b601-1cc2962c0655',3),
	 ('e8cf14e5-89d7-4625-bff8-fa9ca154d23e',3);


INSERT INTO public.keystore_row_info (id,aes_init_vector,alias,certificate_serial_number,keystore_name,"password",row_password,account_id) VALUES
	 ('8eb12edb-5896-4f5a-8517-7cff59f05b60',decode('1AAE6C32A295BC55C2B746C63BCD2B4E','hex'),'9c4ba8d6-00ba-465a-811a-7cb78fd5cb53',3902041381.00,'9c4ba8d6-00ba-465a-811a-7cb78fd5cb53_keystore.jks',decode('9CAEEFEE051A30E535640D42325F4AA7','hex'),'sP#i-;$q/HR&|c\','05606b28-a4e9-43e1-a7df-dedd9e76375a'),
	 ('cff78b1d-7ba2-499e-bccf-f19bca72d066',decode('7A0F4A24967F978418A6F8F80C239662','hex'),'c2750533-8a2d-4daf-bb7e-3ce5e4bcf04a',2644184320.00,'c2750533-8a2d-4daf-bb7e-3ce5e4bcf04a_keystore.jks',decode('A294D68983C642188D997DC0608D58B4','hex'),'0Q>XF]/;jqb,3Vl','05ca48ff-9e67-4be6-b46b-a3373fbb22f2'),
	 ('ec823f49-2563-4518-9ee2-855d5686d9fa',decode('B48DEEE4196B40427A7BC8EE338B0948','hex'),'0edcdc9b-4f84-4040-ae0c-b546f252fe7f',3502365007.00,'9c4ba8d6-00ba-465a-811a-7cb78fd5cb53_keystore.jks',decode('AD7A5AA3A16B597DB497578DE0599FC0','hex'),'JNUVNGNby]iaX>6','094dda90-edfc-4099-b601-1cc2962c0655'),
	 ('a2e7f949-0c2e-42af-835c-56dc8fd05b79',decode('2C8C01BBF2779F9A0E3546BC6705DAF1','hex'),'5c24835a-d0af-4e05-8cfb-7ef30ea9b9e0',3204597111.00,'9c4ba8d6-00ba-465a-811a-7cb78fd5cb53_keystore.jks',decode('EDDAA958BE32C98E60B486D18D765A16','hex'),'oH:<xg,}#o^"WWs','e8cf14e5-89d7-4625-bff8-fa9ca154d23e');
