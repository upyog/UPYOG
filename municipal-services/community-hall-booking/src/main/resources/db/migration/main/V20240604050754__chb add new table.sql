CREATE TABLE IF NOT EXISTS COMMUNITY_HALL_BOOKING_INIT(
  booking_id character varying(64) NOT NULL,
  tenant_id character varying(10) NOT NULL,
  community_hall_id character varying(64) NOT NULL, 
  booking_status character varying(15) NOT NULL,
  booking_details jsonb,
  createdBy character varying(64) NOT NULL,
  createdTime bigint  NOT NULL,
  lastModifiedBy character varying(64),
  lastModifiedTime bigint,
  CONSTRAINT COMMUNITY_HALL_BOOKING_INIT_PK PRIMARY KEY (booking_id)
);


--TODO : Remove drop table statement before merging PR
--TODO : Need to add index for colums used in Query
--DROP TABLE  IF EXISTS COMMUNITY_HALL_BOOKING_DETAILS cascade;
CREATE TABLE IF NOT EXISTS COMMUNITY_HALL_BOOKING_DETAILS(
  booking_id character varying(64) NOT NULL,
  booking_no character varying(64),
  booking_date bigint,
  approval_no  character varying(64),
  approval_date bigint,
  tenant_id character varying(64) NOT NULL,
  community_hall_id integer NOT NULL, 
  booking_status character varying(15) NOT NULL,
  resident_type character varying(64) NOT NULL,
  special_category character varying(64) NOT NULL,
  purpose character varying(64) NOT NULL,
  purpose_description character varying(100)  NOT NULL,
  event_name character varying(64),
  event_organized_by character varying(64),
  createdBy character varying(64) NOT NULL,
  createdTime bigint  NOT NULL,
  lastModifiedBy character varying(64),
  lastModifiedTime bigint,
  CONSTRAINT COMMUNITY_HALL_BOOKING_DETAILS_PK PRIMARY KEY (booking_id)
);

--DROP TABLE  IF EXISTS BOOKING_SLOT_DETAILS;

CREATE TABLE IF NOT EXISTS BOOKING_SLOT_DETAILS(
   slot_id character varying(64) NOT NULL,
   booking_id character varying(64) NOT NULL,
   hallcode character varying(64) NOT NULL,
   booking_slot_dateTime bigint NOT NULL,
   status character varying(15) NOT NULL,
   createdBy character varying(64) NOT NULL,
   createdTime bigint  NOT NULL,
   lastModifiedBy character varying(64),
   lastModifiedTime bigint,
   CONSTRAINT BOOKING_SLOT_DETAILS_SLOT_ID_PK PRIMARY KEY (slot_id),
   CONSTRAINT BOOKING_SLOT_DETAILS_BOOKING_ID_FK 
   FOREIGN KEY (booking_id) REFERENCES COMMUNITY_HALL_BOOKING_DETAILS (booking_id)
     ON UPDATE CASCADE
     ON DELETE CASCADE
);

--DROP TABLE  IF EXISTS BANK_ACCOUNT_DETAILS;
--TODO check cascade type before committing
CREATE TABLE IF NOT EXISTS BANK_ACCOUNT_DETAILS(
   bank_detail_id character varying(64) NOT NULL,
   booking_id character varying(64) NOT NULL,
   account_no character varying(20) NOT NULL,
   ifsc_code character varying(20) NOT NULL,
   bank_name character varying(50) NOT NULL,
   bank_branch_name character varying(100) NOT NULL,
   account_holder_name character varying(100) NOT NULL,
   refund_status  character varying(30),
  createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
   CONSTRAINT BANK_ACCOUNT_DETAILS_ID_PK PRIMARY KEY (bank_detail_id),
   CONSTRAINT BANK_ACCOUNT_DETAILS_BOOKING_ID_FK 
   FOREIGN KEY (booking_id) REFERENCES COMMUNITY_HALL_BOOKING_DETAILS (booking_id)
     ON UPDATE CASCADE
     ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS  COMMUNITY_HALL_BOOKING_DOCUMENT_DETAILS(
    document_detail_id character varying(64)  NOT NULL,
    booking_id character varying(64),
    document_type character varying(64),
    filestore_id character varying(64),
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    CONSTRAINT COMMUNITY_HALL_BOOKING_DOCUMENT_PK PRIMARY KEY (document_detail_id),
    CONSTRAINT COMMUNITY_HALL_BOOKING_DOCUMENT_BOOKING_ID_FK 
    FOREIGN KEY (booking_id) REFERENCES COMMUNITY_HALL_BOOKING_DETAILS (booking_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);
--TODO : Need to add audit details table


CREATE SEQUENCE seq_chb_booking_id;
