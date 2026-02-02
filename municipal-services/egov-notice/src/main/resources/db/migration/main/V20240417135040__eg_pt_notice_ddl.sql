DROP TABLE IF EXISTS eg_pt_notice;
DROP TABLE IF EXISTS eg_pt_notice_comment;


CREATE TABLE public.eg_pt_notice (
noticeType varchar(256),
tenantId varchar(256),
uuid varchar(256),
noticeNumber varchar(256),
name varchar(256),
address varchar(256),
propertyId varchar(256),
assessmentYear varchar(256),
acknowledgementNumber varchar(256),
dateOfAnnualRet varchar(256),
entryDate varchar(256),
entryTime varchar(256),
place varchar(256),
perticulars varchar(256),
asreturnfiled varchar(256),
aspermunispality varchar(256),
resolutionon varchar(256),
dated varchar(256),
designation varchar(256),
authorisedpersonname varchar(256),
mobilenumber varchar(256),
penaltyamount varchar(256),
appealno varchar(256),
CONSTRAINT pk_eg_pt_notice PRIMARY KEY (uuid)
);


CREATE TABLE public.eg_pt_notice_comment (
noticeid varchar(256),
uuid varchar(256),
comment varchar(256),
CONSTRAINT pk_eg_pt_comment PRIMARY KEY (uuid)
);


ALTER TABLE public.eg_pt_notice_comment ADD CONSTRAINT fk_eg_pt_notice_comment FOREIGN KEY (noticeid) REFERENCES public.eg_pt_notice(uuid);
