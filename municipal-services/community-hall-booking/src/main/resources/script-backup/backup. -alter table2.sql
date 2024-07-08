
create table eg_chb_booking_detail_init(
  booking_id character varying(64) NOT NULL,
  tenant_id character varying(10) NOT NULL,
  community_hall_id character varying(64) NOT NULL, 
  booking_status character varying(15) NOT NULL,
  booking_details jsonb,
  createdBy character varying(64) NOT NULL,
  createdTime bigint  NOT NULL,
  lastModifiedBy character varying(64),
  lastModifiedTime bigint,
  constraint eg_chb_booking_details_init primary key (booking_id)
);


-- TODO : Remove drop table statement before merging PR
-- TODO : Need to add index for colums used in Query
-- DROP TABLE  eg_chb_booking_detail cascade;
create table eg_chb_booking_detail(
  booking_id character varying(64) NOT NULL,
  booking_no character varying(64),
  payment_date bigint,
  application_date bigint not null,
  tenant_id character varying(64) NOT NULL,
  community_hall_code integer NOT NULL, 
  booking_status character varying(15) NOT NULL,
 -- resident_type character varying(64) NOT NULL,
  special_category character varying(60) NOT NULL,
  purpose character varying(60) NOT NULL,
  purpose_description character varying(100)  NOT NULL,
  -- event_name character varying(64),
  -- event_organized_by character varying(64),
  createdBy character varying(64) NOT NULL,
  createdTime bigint  NOT NULL,
  lastModifiedBy character varying(64),
  lastModifiedTime bigint,
  constraint eg_chb_booking_detail_pk primary key (booking_id)
);

-- DROP TABLE   eg_chb_slot_detail;

create table eg_chb_slot_detail(
   slot_id character varying(64) NOT NULL,
   booking_id character varying(64) NOT NULL,
   hall_code character varying(64) NOT NULL,
   booking_date character varying(20) NOT NULL,
   booking_from_time character varying(20) NOT NULL,
   booking_to_time character varying(20) NOT NULL,
   status character varying(15) NOT NULL,
   createdBy character varying(64) NOT NULL,
   createdTime bigint  NOT NULL,
   lastModifiedBy character varying(64),
   lastModifiedTime bigint,
   constraint eg_chb_slot_detail_slot_id_pk PRIMARY KEY (slot_id),
   constraint eg_chb_slot_detail_booking_id_fk 
   FOREIGN KEY (booking_id) REFERENCES eg_chb_booking_detail (booking_id)
     ON UPDATE NO ACTION
     ON DELETE NO ACTION
);

-- DROP TABLE  IF EXISTS eg_chb_bank_detail;
-- TODO check cascade type before committing
create table eg_chb_applicant_detail(
   applicant_detail_id character varying(64) NOT NULL,
   booking_id character varying(64) NOT NULL,
   applicant_name  character varying(300) NOT NULL,
   applicant_email_id  character varying(300) NOT NULL,
   applicant_mobile_no  character varying(150) NOT NULL,
   applicant_alternate_mobile_no  character varying(150),
   account_no character varying(200) NOT NULL,
   ifsc_code character varying(200) NOT NULL,
   bank_name character varying(300) NOT NULL,
   bank_branch_name character varying(300) NOT NULL,
   account_holder_name character varying(300) NOT NULL,
   refund_status  character varying(30),
   refund_type  character varying(15),-- SECURITY, CANCELLATION
   createdby character varying(64),
   lastmodifiedby character varying(64),
   createdtime bigint,
   lastmodifiedtime bigint,
   constraint eg_chb_applicant_detail_id_pk PRIMARY KEY (applicant_detail_id),
   constraint eg_chb_bank_detail_booking_id_fk 
   FOREIGN KEY (booking_id) REFERENCES eg_chb_booking_detail (booking_id)
     ON UPDATE NO ACTION
     ON DELETE NO ACTION
);

create table  eg_chb_document_detail(
    document_detail_id character varying(64)  NOT NULL,
    booking_id character varying(64),
    document_type character varying(64),
    filestore_id character varying(64),
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    constraint eg_chb_document_detail_id_pk PRIMARY KEY (document_detail_id),
    constraint eg_chb_document_detail_booking_id_fk 
    FOREIGN KEY (booking_id) REFERENCES eg_chb_booking_detail (booking_id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);


create table eg_chb_address_detail (
    address_id character varying(64),
    applicant_detail_id character varying(64),  -- Foreign Key
    door_no character varying(100),
    house_no character varying(100),
    address_line_1 character varying(150),
    landmark character varying(150),
    city character varying(100),
    pincode VARCHAR(12),
    street_name character varying(150),
    locality_code character varying(20),
    constraint eg_chb_address_detail_id_pk PRIMARY KEY (address_id),
    constraint eg_chb_address_applicant_detail_id_fk 
    FOREIGN KEY (applicant_detail_id) REFERENCES eg_chb_applicant_detail (applicant_detail_id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);


CREATE SEQUENCE IF NOT EXISTS seq_chb_booking_id;
