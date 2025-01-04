DROP TABLE IF EXISTS eg_adv_payment_timer;
CREATE TABLE eg_adv_payment_timer (
  booking_id VARCHAR(64) NOT NULL,
  booking_no VARCHAR(255),
  tenant_id character varying(64),
  add_type character varying(64) COLLATE pg_catalog."default" NOT NULL,
  location character varying(64) COLLATE pg_catalog."default" NOT NULL,
  face_area character varying(64) COLLATE pg_catalog."default" NOT NULL,
  booking_start_date date,
  booking_end_date date,
  night_light BOOLEAN DEFAULT FALSE,
  status CHARACTER VARYING(50),
  createdby VARCHAR(64) NOT NULL,
  createdtime BIGINT NOT NULL,
  lastmodifiedby VARCHAR(64),
  lastmodifiedtime BIGINT,
  CONSTRAINT pk_eg_adv_payment_timer PRIMARY KEY (booking_id)
);