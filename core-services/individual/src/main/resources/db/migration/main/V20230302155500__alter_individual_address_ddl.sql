ALTER TABLE INDIVIDUAL ADD COLUMN individualId character varying(64) UNIQUE;
ALTER TABLE INDIVIDUAL ADD COLUMN relationship character varying(100);

ALTER TABLE ADDRESS ADD COLUMN wardCode character varying(256);