TRUNCATE TABLE eg_bmc_Criteria RESTART IDENTITY;

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	'Gender',
	'FEMALE',
    '=',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	'Gender',
	'MALE',
    '=',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	'Gender',
	'TRANSGENDER',
    '=',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	'Age',
	'18',
    '>=',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	'Age',
	'60',
    '<=',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	'Income',
	'100000',
    '<=',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	'Disability',
	'40',
    '>=',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	'Disability',
	'80',
    '>=',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

Insert into eg_bmc_Criteria(CriteriaType,CriteriaValue,CriteriaCondition, createdon,modifiedon,createdby,modifiedby) values
(
	'Disability',
	'80',
    '<=',
	extract(epoch from now()),
	extract(epoch from now()),
	'system',
	'system'
);

