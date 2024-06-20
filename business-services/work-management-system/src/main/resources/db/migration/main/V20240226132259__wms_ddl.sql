CREATE TABLE public."sdpgbgdetails" (

   
    agreement_no varchar(255) NOT NULL,

    sdpg_id varchar(255) NOT NULL,

    deposit_type varchar(255) NULL,

    deposit_amount varchar(255) NULL,

    account_no varchar(255) NULL,

    particulars varchar(255) NULL,

    valid_from_date varchar(255) NULL,

    valid_till_date varchar(255) NULL,

    bank_branch_ifsc_code varchar(255) NULL,

    payment_mode varchar(255) NULL,
    
    CONSTRAINT sdpgbgdetails_fkey FOREIGN KEY (agreement_no) REFERENCES public.contract_agreement(agreement_no),
    CONSTRAINT sdpg_pkey PRIMARY KEY (sdpg_id)

);

ALTER TABLE public.contract_agreement DROP COLUMN deposit_type;
ALTER TABLE public.contract_agreement DROP COLUMN deposit_amount;
ALTER TABLE public.contract_agreement DROP COLUMN account_no;
ALTER TABLE public.contract_agreement DROP COLUMN particulars;
ALTER TABLE public.contract_agreement DROP COLUMN valid_from_date;
ALTER TABLE public.contract_agreement DROP COLUMN valid_till_date;
ALTER TABLE public.contract_agreement DROP COLUMN bank_branch_ifsc_code;
ALTER TABLE public.contract_agreement DROP COLUMN payment_mode;















