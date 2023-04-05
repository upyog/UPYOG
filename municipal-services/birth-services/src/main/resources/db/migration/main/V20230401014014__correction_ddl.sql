ALTER TABLE public.eg_birth_correction_child
    ADD COLUMN correction_id character varying(64) COLLATE pg_catalog."default"  NOT NULL,
    ADD COLUMN local_column character varying(64) COLLATE pg_catalog."default" NOT NULL,
    ADD CONSTRAINT eg_birth_correction_child_fkey FOREIGN KEY (correction_id)
    REFERENCES public.eg_birth_correction (id) MATCH SIMPLE;

ALTER TABLE public.eg_register_birth_correction_child
    ADD COLUMN correction_id character varying(64) COLLATE pg_catalog."default"  NOT NULL,
    ADD COLUMN local_column character varying(64) COLLATE pg_catalog."default" NOT NULL,
    ADD CONSTRAINT eg_register_birth_correction_child_fkey FOREIGN KEY (correction_id)
    REFERENCES public.eg_register_birth_correction (id) MATCH SIMPLE;

ALTER TABLE public.eg_birth_correction_document
    ADD COLUMN correction_id character varying(64) COLLATE pg_catalog."default"  NOT NULL,
    ADD CONSTRAINT eg_birth_correction_document_fkey FOREIGN KEY (correction_id)
    REFERENCES public.eg_birth_correction (id) MATCH SIMPLE;

ALTER TABLE public.eg_register_birth_correction_document
    ADD COLUMN correction_id character varying(64) COLLATE pg_catalog."default"  NOT NULL,
    ADD CONSTRAINT eg_register_birth_correction_document_fkey FOREIGN KEY (correction_id)
    REFERENCES public.eg_register_birth_correction (id) MATCH SIMPLE;
