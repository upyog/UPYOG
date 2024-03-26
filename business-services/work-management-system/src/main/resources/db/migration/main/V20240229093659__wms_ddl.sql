ALTER TABLE public.contract_agreement DROP COLUMN vendor_type;
ALTER TABLE public.contract_agreement DROP COLUMN vendor_name;
ALTER TABLE public.contract_agreement DROP COLUMN represented_by;
ALTER TABLE public.contract_agreement DROP COLUMN primary_party;

CREATE TABLE public."contractors" (

   
    agreement_no varchar(255) NOT NULL,

    con_id varchar(255) NOT NULL,

    vendor_type varchar(255) NULL,

    vendor_name varchar(255) NULL,

    represented_by varchar(255) NULL,

    primary_party varchar(255) NULL,

    CONSTRAINT cont_fkey FOREIGN KEY (agreement_no) REFERENCES public.contract_agreement(agreement_no),
    CONSTRAINT cont_pkey PRIMARY KEY (con_id)

);