
ALTER TABLE eg_fm_applicantpersonal_log
ADD COLUMN applicantcategory varchar;

ALTER TABLE eg_fm_applicantpersonal_log
ADD COLUMN dateofbirth varchar;

ALTER TABLE eg_fm_applicantpersonal_log
ADD COLUMN bankaccountno varchar;

ALTER TABLE eg_fm_applicantaddress_log
ADD COLUMN residenceassociationno varchar;

ALTER TABLE eg_fm_applicantaddress_log
ADD COLUMN localplace varchar;

ALTER TABLE eg_fm_applicantaddress_log
ADD COLUMN mainplace varchar;

ALTER TABLE eg_fm_applicantaddress_log
ADD COLUMN wardno varchar;

ALTER TABLE eg_fm_applicantservicedocument_log
ADD COLUMN applicationdetails varchar;