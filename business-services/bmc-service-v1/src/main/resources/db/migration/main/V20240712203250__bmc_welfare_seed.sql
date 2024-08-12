TRUNCATE TABLE eg_bmc_document RESTART IDENTITY CASCADE;

Insert into eg_bmc_document(name,description, createdon,modifiedon,createdby,modifiedby) values
(
	'Voter Card',
    'Voter ID Card',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);
Insert into eg_bmc_document(name,description, createdon,modifiedon,createdby,modifiedby) values
(
	'Passport',
    'Passport',  
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);
Insert into eg_bmc_document(name,description, createdon,modifiedon,createdby,modifiedby) values
(
	'Income Certificate',
    'Income Certificate',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);
Insert into eg_bmc_document(name,description, createdon,modifiedon,createdby,modifiedby) values
(
	'Domicile Certificate',
    'Domicile Certificate',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);
Insert into eg_bmc_document(name,description, createdon,modifiedon,createdby,modifiedby) values
(
	'Pancard',
    'Pancard',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);
Insert into eg_bmc_document(name,description, createdon,modifiedon,createdby,modifiedby) values
(
	'Passbook',
    'Passbook',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);


