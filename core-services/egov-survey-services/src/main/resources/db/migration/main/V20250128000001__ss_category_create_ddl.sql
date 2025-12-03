CREATE TABLE IF NOT EXISTS public.eg_ss_category (
	id varchar(128) NOT NULL,
	"label" varchar(128) NOT NULL,
	tenantid varchar(128) NOT NULL,
	isactive bool NULL,
	createdby varchar(64) NOT NULL,
	lastmodifiedby varchar(64) NOT NULL,
	createdtime bigint NOT NULL,
	lastmodifiedtime bigint NOT NULL,
	CONSTRAINT pk_eg_ss_category_id PRIMARY KEY (id),
    CONSTRAINT uk_eg_ss_category_label_tenantid UNIQUE ("label", tenantid)
    );
);

CREATE INDEX IF NOT EXISTS idx_eg_ss_category_id ON eg_ss_category(id);
CREATE INDEX IF NOT EXISTS idx_eg_ss_category_label ON eg_ss_category("label");
CREATE INDEX IF NOT EXISTS idx_eg_ss_category_tenantid ON eg_ss_category(tenantid);
CREATE INDEX IF NOT EXISTS idx_eg_ss_category_isactive ON eg_ss_category(isactive);
CREATE INDEX IF NOT EXISTS idx_eg_ss_category_label_tenantid ON eg_ss_category("label",tenantid);
CREATE INDEX IF NOT EXISTS idx_eg_ss_category_label_tenantid_isactive ON eg_ss_category("label",tenantid,isactive);