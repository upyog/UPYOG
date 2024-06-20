ALTER TABLE public.contract_agreement DROP COLUMN sr_no;
ALTER TABLE public.contract_agreement DROP COLUMN terms_and_conditions;

CREATE TABLE public."termsnconditions" (

   
    agreement_no varchar(255) NOT NULL,

    tnc_id varchar(255) NOT NULL,

    sr_no int8 NULL,

    terms_and_conditions varchar(255) NULL,

    CONSTRAINT tnc_fkey FOREIGN KEY (agreement_no) REFERENCES public.contract_agreement(agreement_no),
    CONSTRAINT tnc_pkey PRIMARY KEY (tnc_id)

);