
create table eg_adv_booking_detail(
  booking_id character varying(64) NOT NULL,
  booking_no character varying(64) UNIQUE,
  payment_date bigint,
  application_date bigint not null,
  tenant_id character varying(64) NOT NULL,
  booking_status character varying(30) NOT NULL,
  receipt_no character varying(64),
  createdBy character varying(64) NOT NULL,
  createdTime bigint  NOT NULL,
  lastModifiedBy character varying(64),
  lastModifiedTime bigint,
  permission_letter_filestore_id character varying(64),
  payment_receipt_filestore_id character varying(64),
  constraint eg_adv_booking_detail_pk primary key (booking_id)
);

CREATE INDEX IF NOT EXISTS idx_eg_adv_booking_detail_booking_no ON eg_adv_booking_detail(booking_no);
CREATE INDEX IF NOT EXISTS idx_eg_adv_booking_detail_createdBy ON eg_adv_booking_detail(createdBy);
CREATE INDEX IF NOT EXISTS idx_eg_adv_booking_detail_tenant_id ON eg_adv_booking_detail(tenant_id);


create table eg_adv_booking_detail_audit(
  booking_id character varying(64) NOT NULL,
  booking_no character varying(64),
  payment_date bigint,
  application_date bigint not null,
  tenant_id character varying(64) NOT NULL,
  booking_status character varying(30) NOT NULL,
  receipt_no character varying(64),
  createdBy character varying(64) NOT NULL,
  createdTime bigint  NOT NULL,
  lastModifiedBy character varying(64),
  lastModifiedTime bigint,
   permission_letter_filestore_id character varying(64),
  payment_receipt_filestore_id character varying(64)
);


CREATE TABLE IF NOT EXISTS public.eg_adv_cart_detail
(
    cart_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    booking_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    add_type character varying(64) COLLATE pg_catalog."default" NOT NULL,
    location character varying(64) COLLATE pg_catalog."default" NOT NULL,
    face_area character varying(64) COLLATE pg_catalog."default" NOT NULL,
    booking_date date NOT NULL,
    booking_from_time time without time zone NOT NULL,
    booking_to_time time without time zone NOT NULL,
    status character varying(30) COLLATE pg_catalog."default" NOT NULL,
    night_light BOOLEAN DEFAULT FALSE, 
    createdby character varying(64) COLLATE pg_catalog."default" NOT NULL,
    createdtime bigint NOT NULL,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    CONSTRAINT eg_adv_cart_detail_cart_id_pk PRIMARY KEY (cart_id),
    CONSTRAINT eg_adv_cart_detail_booking_id_fk FOREIGN KEY (booking_id)
        REFERENCES public.eg_adv_booking_detail (booking_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.eg_adv_cart_detail_audit
(
    cart_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    booking_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    booking_date date NOT NULL,
    booking_from_time time without time zone NOT NULL,
    booking_to_time time without time zone NOT NULL,
    add_type character varying(64) COLLATE pg_catalog."default" NOT NULL,
    location character varying(20) COLLATE pg_catalog."default" NOT NULL,
    face_area character varying(20) COLLATE pg_catalog."default" NOT NULL,
    night_light BOOLEAN DEFAULT FALSE, 
    status character varying(30) COLLATE pg_catalog."default" NOT NULL,
    createdby character varying(64) COLLATE pg_catalog."default" NOT NULL,
    createdtime bigint NOT NULL,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint
);


CREATE INDEX IF NOT EXISTS idx_eg_adv_cart_detail_status ON eg_adv_cart_detail(status);
CREATE INDEX IF NOT EXISTS idx_eg_adv_cart_detail_booking_date ON eg_adv_cart_detail(booking_date);
CREATE INDEX IF NOT EXISTS idx_eg_adv_cart_detail_booking_id ON eg_adv_cart_detail(booking_id);


create table eg_adv_applicant_detail(
   applicant_detail_id character varying(64) NOT NULL,
   booking_id character varying(64) NOT NULL,
   applicant_name  character varying(300) NOT NULL,
   applicant_email_id  character varying(300) NOT NULL,
   applicant_mobile_no  character varying(150) NOT NULL,
   applicant_alternate_mobile_no  character varying(150),
   createdby character varying(64),
   lastmodifiedby character varying(64),
   createdtime bigint,
   lastmodifiedtime bigint,
   constraint eg_adv_applicant_detail_id_pk PRIMARY KEY (applicant_detail_id),
   constraint eg_adv_bank_detail_booking_id_fk 
   FOREIGN KEY (booking_id) REFERENCES eg_adv_booking_detail (booking_id)
     ON UPDATE NO ACTION
     ON DELETE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_eg_adv_applicant_detail_booking_id ON eg_adv_applicant_detail(booking_id);
CREATE INDEX IF NOT EXISTS idx_eg_adv_applicant_detail_applicant_mobile_no ON eg_adv_applicant_detail(applicant_mobile_no);

create table  eg_adv_document_detail(
    document_detail_id character varying(64)  NOT NULL,
    booking_id character varying(64)  NOT NULL,
    document_type character varying(64),
    filestore_id character varying(64)  NOT NULL,
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    constraint eg_adv_document_detail_id_pk PRIMARY KEY (document_detail_id),
    constraint eg_adv_document_detail_booking_id_fk 
    FOREIGN KEY (booking_id) REFERENCES eg_adv_booking_detail (booking_id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_eg_adv_document_detail_booking_id ON eg_adv_document_detail(booking_id);

create table eg_adv_address_detail (
    address_id character varying(64)  NOT NULL,
    applicant_detail_id character varying(64)  NOT NULL,  -- Foreign Key
    door_no character varying(100),
    house_no character varying(100),
    street_name character varying(150),
    address_line_1 character varying(150),
    address_line_2 character varying(150),
    landmark character varying(150),
    city character varying(100)  NOT NULL,
    city_code character varying(10)  NOT NULL,
    locality character varying(100)  NOT NULL,
    locality_code character varying(20)  NOT NULL,
    pincode VARCHAR(12)   NOT NULL,
    constraint eg_adv_address_detail_id_pk PRIMARY KEY (address_id),
    constraint eg_adv_address_applicant_detail_id_fk 
    FOREIGN KEY (applicant_detail_id) REFERENCES eg_adv_applicant_detail (applicant_detail_id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_eg_adv_address_detail_applicant_detail_id ON eg_adv_address_detail(applicant_detail_id);


CREATE SEQUENCE IF NOT EXISTS seq_adv_booking_id;




