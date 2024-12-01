CREATE TABLE eg_chb_payment_timer (
  booking_id CHARACTER VARYING(64) NOT NULL UNIQUE,
  createdBy CHARACTER VARYING(64) NOT NULL,
  createdTime BIGINT NOT NULL,
  lastModifiedBy CHARACTER VARYING(64),
  lastModifiedTime BIGINT
);
