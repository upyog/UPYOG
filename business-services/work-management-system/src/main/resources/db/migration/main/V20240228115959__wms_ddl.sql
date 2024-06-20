ALTER TABLE public.contract_agreement DROP COLUMN document_description;
ALTER TABLE public.contract_agreement DROP COLUMN upload_document;
ALTER TABLE public.contract_agreement DROP COLUMN witness_name_p2;
ALTER TABLE public.contract_agreement DROP COLUMN address_p2;
ALTER TABLE public.contract_agreement DROP COLUMN uid_p2;

CREATE TABLE public."agreementdocuments" (

   
    agreement_no varchar(255) NOT NULL,

    ad_id varchar(255) NOT NULL,

    document_description varchar(255) NULL,

    upload_document varchar(255) NULL,

    CONSTRAINT ad_fkey FOREIGN KEY (agreement_no) REFERENCES public.contract_agreement(agreement_no),
    CONSTRAINT ad_pkey PRIMARY KEY (ad_id)

);

CREATE TABLE public."party2witness" (

   
    agreement_no varchar(255) NOT NULL,

    pw_id varchar(255) NOT NULL,

    witness_name_p2 varchar(255) NULL,

    address_p2 varchar(255) NULL,

    uid_p2 varchar(255) NULL,

    CONSTRAINT p2w_fkey FOREIGN KEY (agreement_no) REFERENCES public.contract_agreement(agreement_no),
    CONSTRAINT p2w_pkey PRIMARY KEY (pw_id)

);