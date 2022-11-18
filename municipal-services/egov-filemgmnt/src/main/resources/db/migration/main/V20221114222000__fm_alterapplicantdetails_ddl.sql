
ALTER TABLE eg_fm_servicedetails RENAME TO eg_fm_servicedetail;

ALTER TABLE eg_fm_filedetails RENAME TO eg_fm_filedetail;

ALTER TABLE eg_fm_applicantservicedocuments RENAME TO eg_fm_applicantservicedocument;

ALTER TABLE eg_fm_applicantdocuments RENAME TO eg_fm_applicantdocument;


ALTER TABLE eg_fm_applicantpersonal
ADD COLUMN applicantcategory varchar;

ALTER TABLE eg_fm_applicantpersonal
ADD COLUMN dateofbirth varchar;

ALTER TABLE eg_fm_applicantpersonal
ADD COLUMN bankaccountno varchar;

ALTER TABLE eg_fm_applicantaddress
ADD COLUMN residenceassociationno varchar;

ALTER TABLE eg_fm_applicantaddress
ADD COLUMN localplace varchar;

ALTER TABLE eg_fm_applicantaddress
ADD COLUMN mainplace varchar;

ALTER TABLE eg_fm_applicantaddress
ADD COLUMN wardno varchar;

ALTER TABLE eg_fm_applicantservicedocument
ADD COLUMN applicationdetails varchar;








