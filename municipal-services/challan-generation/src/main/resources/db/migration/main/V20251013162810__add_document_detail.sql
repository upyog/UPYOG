create table  eg_challan_document_detail(
    document_detail_id character varying(64)  NOT NULL,
    challan_id character varying(64)  NOT NULL,
    document_type character varying(64),
    filestore_id character varying(64)  NOT NULL,
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    constraint eg_challan_document_detail_id_pk PRIMARY KEY (document_detail_id),
    constraint eg_challan_document_detail_challan_id_fk
    FOREIGN KEY (challan_id) REFERENCES eg_challan (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_eg_challan_document_detail_challan_id ON eg_challan_document_detail(challan_id);