CREATE TABLE eg_ptr_applicationdocuments(
    id character varying(64) NOT NULL,
    tenantId character varying(64) NOT NULL,
    documentType character varying(64) NOT NULL,
    filestoreId character varying(64) NOT NULL,
    documentUid CHARACTER VARYING (128),
    petApplicationId character varying(64) NOT NULL,
    active boolean,
    createdBy character varying(64) NOT NULL,
    lastModifiedBy character varying(64),
    createdTime bigint,
    lastModifiedTime bigint,

    CONSTRAINT uk_eg_ptr_applicationdocument PRIMARY KEY (id),
    CONSTRAINT fk_eg_ptr_applicationdocument FOREIGN KEY (petApplicationId) REFERENCES eg_ptr_registration (id)
);