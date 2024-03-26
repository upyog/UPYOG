ALTER TABLE public.running_account_final_bill DROP COLUMN bill_date;
ALTER TABLE public.running_account_final_bill DROP COLUMN bill_no;
ALTER TABLE public.running_account_final_bill DROP COLUMN bill_amount;
ALTER TABLE public.running_account_final_bill ALTER COLUMN work_name DROP NOT NULL;
ALTER TABLE public.running_account_final_bill ALTER COLUMN mb_no DROP NOT NULL;
CREATE TABLE public."previousrunningbillinfo" (

   
    running_account_id varchar(255) NOT NULL,

    prbi_id varchar(255) NOT NULL,

    running_account_bill_date varchar(255) NULL,

    running_account_bill_no varchar(255) NULL,

    running_account_bill_amount varchar(255) NULL,

    tax_Amount varchar(255) NULL,

    remark varchar(255) NULL,

    CONSTRAINT prbi_fkey FOREIGN KEY (running_account_id) REFERENCES public.running_account_final_bill(running_account_id),
    CONSTRAINT prbi_pkey PRIMARY KEY (prbi_id)

);