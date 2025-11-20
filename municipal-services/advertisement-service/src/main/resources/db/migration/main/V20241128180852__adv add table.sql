CREATE TABLE eg_adv_payment_timer (
  booking_id VARCHAR(64) NOT NULL,
  created_by VARCHAR(64) NOT NULL,
  created_time BIGINT NOT NULL,
  status CHARACTER VARYING(50),
  booking_no VARCHAR(255),
  last_modified_by VARCHAR(64),
  last_modified_time BIGINT,
  CONSTRAINT pk_eg_adv_payment_timer PRIMARY KEY (booking_id)
);