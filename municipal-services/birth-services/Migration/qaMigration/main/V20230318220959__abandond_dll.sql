ALTER TABLE   eg_birth_mother_information  ADD addressofmother character varying(2500);
ALTER TABLE   eg_birth_mother_information_audit  ADD addressofmother character varying(2500);
ALTER TABLE   eg_register_birth_mother_information  ADD addressofmother character varying(2500);
ALTER TABLE   eg_register_birth_mother_information_audit  ADD addressofmother character varying(2500);

CREATE TABLE eg_application_document(
                                        id character varying(128),
                                        tenantid character varying(128),
                                        document_name character varying(128),
                                        document_type character varying(128),
                                        document_description character varying(140),
                                        filestoreid character varying(1024),
                                        document_link character varying(1024),
                                        file_type character varying(20),
                                        file_size bigint,
                                        document_uuid character varying(128),
                                        active boolean,
                                        createdby character varying(64) COLLATE pg_catalog."default",
                                        createdtime bigint,
                                        lastmodifiedby character varying(64) COLLATE pg_catalog."default",
                                        lastmodifiedtime bigint,
                                        CONSTRAINT eg_application_document_pkey UNIQUE (id),
                                        CONSTRAINT eg_application_document_fkey PRIMARY KEY (tenantid,document_type,document_name,active)
);

CREATE TABLE eg_application_document_audit(
                                              operation character(1) COLLATE pg_catalog."default" NOT NULL,
                                              stamp timestamp without time zone NOT NULL,
                                              id character varying(128),
                                              tenantid character varying(128),
                                              document_name character varying(128),
                                              document_type character varying(128),
                                              document_description character varying(140),
                                              filestoreid character varying(1024),
                                              document_link character varying(1024),
                                              file_type character varying(20),
                                              file_size bigint,
                                              document_uuid character varying(128),
                                              active boolean,
                                              createdby character varying(64) COLLATE pg_catalog."default",
                                              createdtime bigint,
                                              lastmodifiedby character varying(64) COLLATE pg_catalog."default",
                                              lastmodifiedtime bigint
);
CREATE TABLE eg_register_document(
                                     id character varying(128),
                                     tenantid character varying(128),
                                     document_name character varying(128),
                                     document_type character varying(128),
                                     document_description character varying(140),
                                     filestoreid character varying(1024),
                                     document_link character varying(1024),
                                     file_type character varying(20),
                                     file_size bigint,
                                     document_uuid character varying(128),
                                     active boolean,
                                     createdby character varying(64) COLLATE pg_catalog."default",
                                     createdtime bigint,
                                     lastmodifiedby character varying(64) COLLATE pg_catalog."default",
                                     lastmodifiedtime bigint,
                                     CONSTRAINT eg_register_document_pkey UNIQUE (id),
                                     CONSTRAINT eg_register_document_fkey PRIMARY KEY (tenantid,document_type,document_name,active)
);

CREATE TABLE eg_register_document_audit(
                                           operation character(1) COLLATE pg_catalog."default" NOT NULL,
                                           stamp timestamp without time zone NOT NULL,
                                           id character varying(128),
                                           tenantid character varying(128),
                                           document_name character varying(128),
                                           document_type character varying(128),
                                           document_description character varying(140),
                                           filestoreid character varying(1024),
                                           document_link character varying(1024),
                                           file_type character varying(20),
                                           file_size bigint,
                                           document_uuid character varying(128),
                                           active boolean,
                                           createdby character varying(64) COLLATE pg_catalog."default",
                                           createdtime bigint,
                                           lastmodifiedby character varying(64) COLLATE pg_catalog."default",
                                           lastmodifiedtime bigint
);