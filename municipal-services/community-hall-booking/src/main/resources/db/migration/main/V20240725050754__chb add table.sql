
create table  IF NOT EXISTS eg_chb_booking_detail_init(
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


create table eg_chb_booking_detail(
  booking_id character varying(64) NOT NULL,
  booking_no character varying(64) UNIQUE,
  payment_date bigint,
  application_date bigint not null,
  tenant_id character varying(64) NOT NULL,
  community_hall_code character varying(64) NOT NULL, 
  booking_status character varying(15) NOT NULL,
  special_category character varying(60) NOT NULL,
  purpose character varying(60) NOT NULL,
  purpose_description character varying(100) NOT NULL,
  receipt_no character varying(64),
  createdBy character varying(64) NOT NULL,
  createdTime bigint  NOT NULL,
  lastModifiedBy character varying(64),
  lastModifiedTime bigint,
  permission_letter_filestore_id character varying(64),
  payment_receipt_filestore_id character varying(64),
  constraint eg_chb_booking_detail_pk primary key (booking_id)
);

CREATE INDEX IF NOT EXISTS idx_eg_chb_booking_detail_booking_no ON eg_chb_booking_detail(booking_no);
CREATE INDEX IF NOT EXISTS idx_eg_chb_booking_detail_community_hall_code ON eg_chb_booking_detail(community_hall_code);
CREATE INDEX IF NOT EXISTS idx_eg_chb_booking_detail_createdBy ON eg_chb_booking_detail(createdBy);
CREATE INDEX IF NOT EXISTS idx_eg_chb_booking_detail_tenant_id ON eg_chb_booking_detail(tenant_id);


create table eg_chb_booking_detail_audit(
  booking_id character varying(64) NOT NULL,
  booking_no character varying(64),
  payment_date bigint,
  application_date bigint not null,
  tenant_id character varying(64) NOT NULL,
  community_hall_code character varying(64) NOT NULL, 
  booking_status character varying(15) NOT NULL,
  special_category character varying(60) NOT NULL,
  purpose character varying(60) NOT NULL,
  purpose_description character varying(100) NOT NULL,
  receipt_no character varying(64),
  createdBy character varying(64) NOT NULL,
  createdTime bigint  NOT NULL,
  lastModifiedBy character varying(64),
  lastModifiedTime bigint,
   permission_letter_filestore_id character varying(64),
  payment_receipt_filestore_id character varying(64)
);

create table eg_chb_slot_detail(
   slot_id character varying(64) NOT NULL,
   booking_id character varying(64) NOT NULL,
   hall_code character varying(64) NOT NULL,
   capacity character varying(20) NOT NULL,
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

create table eg_chb_slot_detail_audit(
   slot_id character varying(64) NOT NULL,
   booking_id character varying(64) NOT NULL,
   hall_code character varying(64) NOT NULL,
   capacity character varying(20) NOT NULL,
   booking_date character varying(20) NOT NULL,
   booking_from_time character varying(20) NOT NULL,
   booking_to_time character varying(20) NOT NULL,
   status character varying(15) NOT NULL,
   createdBy character varying(64) NOT NULL,
   createdTime bigint  NOT NULL,
   lastModifiedBy character varying(64),
   lastModifiedTime bigint
);

CREATE INDEX IF NOT EXISTS idx_eg_chb_slot_detail_status ON eg_chb_slot_detail(status);
CREATE INDEX IF NOT EXISTS idx_eg_chb_slot_detail_booking_date ON eg_chb_slot_detail(booking_date);
CREATE INDEX IF NOT EXISTS idx_eg_chb_slot_detail_hall_code ON eg_chb_slot_detail(hall_code);
CREATE INDEX IF NOT EXISTS idx_eg_chb_slot_detail_booking_id ON eg_chb_slot_detail(booking_id);


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

CREATE INDEX IF NOT EXISTS idx_eg_chb_applicant_detail_booking_id ON eg_chb_applicant_detail(booking_id);
CREATE INDEX IF NOT EXISTS idx_eg_chb_applicant_detail_applicant_mobile_no ON eg_chb_applicant_detail(applicant_mobile_no);

create table  eg_chb_document_detail(
    document_detail_id character varying(64)  NOT NULL,
    booking_id character varying(64)  NOT NULL,
    document_type character varying(64),
    filestore_id character varying(64)  NOT NULL,
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

CREATE INDEX IF NOT EXISTS idx_eg_chb_document_detail_booking_id ON eg_chb_document_detail(booking_id);

create table eg_chb_address_detail (
    address_id character varying(64)  NOT NULL,
    applicant_detail_id character varying(64)  NOT NULL,  -- Foreign Key
    door_no character varying(100),
    house_no character varying(100),
    street_name character varying(150),
    address_line_1 character varying(150),
    landmark character varying(150),
    city character varying(100)  NOT NULL,
    city_code character varying(10)  NOT NULL,
    locality character varying(100)  NOT NULL,
    locality_code character varying(20)  NOT NULL,
    pincode VARCHAR(12)   NOT NULL,
    constraint eg_chb_address_detail_id_pk PRIMARY KEY (address_id),
    constraint eg_chb_address_applicant_detail_id_fk 
    FOREIGN KEY (applicant_detail_id) REFERENCES eg_chb_applicant_detail (applicant_detail_id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_eg_chb_address_detail_applicant_detail_id ON eg_chb_address_detail(applicant_detail_id);


CREATE SEQUENCE IF NOT EXISTS seq_chb_booking_id;




