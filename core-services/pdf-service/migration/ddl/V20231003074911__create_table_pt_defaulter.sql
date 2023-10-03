create table egov_defaulter_notice_pdf_info
(
jobid character varying(100) not null,
uuid character varying(256) not null,
recordscompleted bigint,
totalrecords bigint,
createdtime bigint,
filestoreid character varying(50),
lastmodifiedby character varying(256),
lastmodifiedtime bigint,
tenantid character varying(50),
locality character varying(50),
propertytype character varying(50),
businessservice character varying(23),
consumercode character varying(50),
isconsolidated boolean ,
status character varying(50)
);