create table eg_adv_draft_detail (
		draft_id VARCHAR(64) PRIMARY KEY,
		tenant_id VARCHAR(64) NOT NULL,
		user_uuid VARCHAR(64) NOT NULL,
		draft_application_data JSONB NOT NULL,
		createdby character varying(64) NOT NULL,
		lastmodifiedby character varying(64),
		createdtime bigint NOT NULL,
		lastmodifiedtime bigint
);