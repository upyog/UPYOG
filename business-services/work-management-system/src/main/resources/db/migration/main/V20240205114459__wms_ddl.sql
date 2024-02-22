CREATE TABLE IF NOT EXISTS "scheme_master" (
    "scheme_id" SERIAL PRIMARY KEY,
    "source_of_fund" VARCHAR(255),
    "start_date" VARCHAR(255),
    "end_date" VARCHAR(255),
    "scheme_name_en" VARCHAR(255),
    "scheme_name_reg" VARCHAR(255),
    "fund" VARCHAR(255),
    "description_of_the_scheme" TEXT,
    "upload_document" VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS "project_master" (
    "project_id" SERIAL PRIMARY KEY,
    "project_number" VARCHAR(255),
    "project_name_en" VARCHAR(255),
    "project_name_reg" VARCHAR(255),
    "project_description" TEXT,
    "project_timeline" VARCHAR(255),
    "project_start_date" DATE,
    "project_end_date" DATE,
    "scheme_name" VARCHAR(255),
    "approval_number" VARCHAR(255),
    "approval_date" DATE,
    "status" VARCHAR(255)
);

 



CREATE TABLE public."contractor" (

    vendor_id serial4 NOT NULL,

    vendor_type varchar(255) NULL,

    vendor_sub_type varchar(255) NULL,

    vendor_name varchar(255) NULL,

    vendor_status varchar(255) NULL,

    pfms_vendor_code varchar(255) NULL,

    payto varchar(255) NULL,

    mobile_number int8 NULL,

    email varchar(255) NULL,

    uid_number int8 NULL,

    gst_number int8 NULL,

    pan_number varchar(255) NULL,

    bank_branch_ifsc_code varchar(255) NULL,

    bank_account_number int8 NULL,

    function varchar(255) NULL,

    primary_account_head varchar(255) NULL,

    vendor_class varchar(255) NULL,

    address varchar(255) NULL,

    epfo_account_number varchar(255) NULL,

    vat_number varchar(255) NULL,

    allow_direct_payment varchar(255) NULL,
  
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL,

    CONSTRAINT contractor_pkey PRIMARY KEY (vendor_id)

    );

CREATE TABLE public."work_estimation" (

    estimate_id varchar(255) NOT NULL,

    work_estimation_no varchar(255) NULL,

    project_name varchar(255) NULL,

    work_name varchar(255) NULL,

    from_date varchar(255) NULL,

    to_date varchar(255) NULL,

    estimate_type varchar(255) NULL,

    sor_name varchar(255) NULL,

    download_template varchar(255) NULL,

    upload_template varchar(255) NULL,

    chapter varchar(255) NULL,

    item_no varchar(255) NULL,

    description_of_the_item varchar(255) NULL,

    length int4 NULL,

    bw int4 NULL,

    dh int4 NULL,

    nos int4 NULL,

    quantity int4 NULL,

    unit varchar(255) NULL,

    rate int4 NULL,

    estimate_amount int4 NULL,
    
    serial_no int4 NULL,
    
    particulars_of_item varchar(255) NULL,

    calculation_type varchar(255) NULL,

    addition_deduction int4 NULL,

    lf int4 NULL,

    bwf int4 NULL,

    dhf int4 NULL,

    sub_total int4  NULL,

    grand_total int4  NULL,

    estimated_quantity int4 NULL,

    remarks varchar(255) NULL,

    overhead_code varchar(255) NULL,

    overhead_description varchar(255) NULL,
  
    value_type varchar(255) NULL,

    estimated_amount int4 NULL,

    document_description varchar(255) NULL,

    upload_document varchar(255) NULL,

    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL,

    CONSTRAINT work_estimation_pkey PRIMARY KEY (estimate_id)

    );

CREATE TABLE public."tender_entry" (

    tender_id varchar(255) NOT NULL,

    department_name varchar(255) NULL,

    request_category varchar(255) NULL,

    project_name varchar(255) NULL,

    resolution_no int4 NULL,

    resolution_date varchar(100) NULL,

    prebid_meeting_date varchar(100) NULL,

    prebid_meeting_location varchar(255) NULL,

    issue_from_date varchar(100) NULL,

    issue_till_date varchar(100) NULL,

    publish_date varchar(100) NULL,

    technical_bid_open_date varchar(100) NULL,

    financial_bid_open_date varchar(100) NULL,

    validity int4 NULL,

    upload_document varchar(255) NULL,

    work_no varchar(255) NULL,

    work_description varchar(255) NULL,

    estimated_cost varchar(255) NULL,

    tender_type varchar(255) NULL,

    tender_fee int4 NULL,
   
    emd varchar(255) NULL,

    vendor_class varchar(255) NULL,

    work_duration varchar(255) NULL,

    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL,

    CONSTRAINT tender_pkey PRIMARY KEY (tender_id)

    );

CREATE TABLE public."work_award_approval" (

    work_award_id varchar(255) NOT NULL,

    work_no varchar(100) NULL,

    work_name varchar(255) NULL,

    percentage_type varchar(255) NULL,

    quoted_percentage varchar(255) NULL,

    accepted_work_cost varchar(255) NULL,

    contractor_name varchar(255) NULL,

    no_of_days_for_agreement int4 NULL,

    loa_generation varchar(255) NULL,

    award_date varchar(100) NULL,

    document_upload varchar(255) NULL,

    award_status varchar(255) NULL,

    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL,


    CONSTRAINT workaward_pkey PRIMARY KEY (work_award_id)

    );

CREATE TABLE public."physical_financial_milestone" (

    milestone_id varchar(255) NOT NULL,

    project_name varchar(255) NULL,

    work_name varchar(255) NULL,

    milestone_name varchar(255) NULL,

    sr_no int4 NULL,

    activity_description varchar(255) NULL,

    percentage_weightage varchar(255) NULL,

    planned_start_date varchar(100) NULL,

    planned_end_date varchar(255) NULL,

    total_weightage int4 NULL,

    milestone_description varchar(255) NULL,

    actual_start_date varchar(255) NULL,

    actual_end_date varchar(255) NULL,

    progress_update_date varchar(255) NULL,

    completed_percentage varchar(255) NULL,

    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL,

    CONSTRAINT milestone_pkey PRIMARY KEY (milestone_id)

    );

CREATE TABLE public."contract_agreement" (

    agreement_no varchar(255) NOT NULL,

    deposit_type varchar(255) NULL,

    deposit_amount int4 NULL,

    account_no int8 NULL,

    particulars varchar(255) NULL,

    valid_from_date varchar(100) NULL,
   
    valid_till_date varchar(100) NULL,

    bank_branch_ifsc_code varchar(255) NULL,

    payment_mode varchar(255) NULL,

    witness_name_p2 varchar(255) NULL,

    address_p2 varchar(255) NULL,

    uid_p2 varchar(255) NULL,

    vendor_type varchar(255) NULL,

    vendor_name varchar(255) NULL,

    represented_by varchar(255) NULL,

    primary_party varchar(255) NULL,

    sr_no int4 NULL,

    terms_and_conditions varchar(255) NULL,

    document_description varchar(255) NULL,

    upload_document varchar(255) NULL,

    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL,

    CONSTRAINT contract_agreement_pkey PRIMARY KEY (agreement_no),
    

    );

CREATE TABLE public."work_order" (

    work_order_id varchar(255) NOT NULL,

    work_order_date varchar(100) NULL,

    agreement_no varchar(255) NOT NULL,

    contractor_name varchar(255) NULL,

    work_name varchar(255) NULL,

    contract_value varchar(255) NULL,

    stipulated_completion_period varchar(255) NULL,

    tender_number varchar(255) NOT NULL,

    tender_date varchar(100) NULL,

    date_of_commencement varchar(255) NULL,

    work_assignee varchar(255) NULL,

    document_description varchar(255) NULL,

    terms_and_conditions varchar(255) NULL,

    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL,

    CONSTRAINT workorder_pkey PRIMARY KEY (work_order_id),
    
    CONSTRAINT work_order_id_fkey FOREIGN KEY (agreement_no) REFERENCES public.contract_agreement(agreement_no),
    CONSTRAINT workorder_id_fkey FOREIGN KEY (tender_number) REFERENCES public.tender_entry(tender_id)

    );

CREATE TABLE public."measurement_book" (

    measurement_book_id varchar(255) NOT NULL,

    work_order_no varchar(255) NOT NULL,

    contractor_name varchar(255) NULL,

    work_name varchar(255) NULL,

    measurement_book_no varchar(255) NULL,

    status varchar(255) NULL,

    agreement_no varchar(255) NOT NULL,

    project_name varchar(100) NULL,

    work_order_amount int4 NULL,

    work_order_date varchar(100) NULL,

    measurement_date varchar(255) NULL,

    description_of_mb varchar(255) NULL,

    je_name varchar(255) NULL,

    chapter varchar(255) NULL,

    item_no varchar(255) NULL,

    description_of_the_item varchar(255) NULL,

    estimated_quantity int4 NULL,

    cumulative_quantity int4 NULL,

    unit int4 NULL,

    rate int8 NULL,

    consumed_quantity int4 NULL,
   
    amount int4 NULL,

    add_mb varchar(255) NULL,

    item_description varchar(255) NULL,

    nos varchar(255) NULL,

    l varchar(255) NULL,

    bw varchar(255) NULL,

    dh varchar(255) NULL,

    upload_images varchar(255) NULL,

    item_code varchar(255) NULL,

    description varchar(255) NULL,

    commulative_quantity int4 NULL,

    remark varchar(255) NULL,

    overhead_description varchar(255) NULL,

    value_type varchar(255) NULL,

    estimated_amount int4 NULL,

    actual_amount int4 NULL,

    document_description varchar(255) NULL,

    upload_document varchar(255) NULL,

    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL,

    CONSTRAINT mb_pkey PRIMARY KEY (measurement_book_id),
    CONSTRAINT measurement_book_id_fkey FOREIGN KEY (work_order_no) REFERENCES public.work_order(work_order_id),
    CONSTRAINT mb_id_fkey FOREIGN KEY (agreement_no) REFERENCES public.contract_agreement(agreement_no)

    );

CREATE TABLE public."running_account_final_bill" (

    running_account_id varchar(255) NOT NULL,

    project_name varchar(255) NULL,

    work_name varchar(255)NOT NULL,

    mb_no varchar(255) NOT NULL,

    mb_date varchar(255) NULL,

    mb_amount int4 NULL,

    estimated_cost varchar(255) NULL,

    tender_type varchar(255) NULL,

    percentage_type varchar(100) NULL,

    award_amount int4 NULL,

    bill_date varchar(255) NULL,

    bill_no int4 null,

    bill_amount int4 NULL,

    deduction_amount int4 NULL,

    remark varchar(255) NULL,

    sr_no int4 NULL,

    deduction_description varchar(255) NULL,

    addition_deduction varchar(255) NULL,

    calculation_method varchar(255) NULL,

    percentage varchar(255) NULL,

    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL,

    
    CONSTRAINT runningaccount_pkey PRIMARY KEY (running_account_id),
    
    
    CONSTRAINT runningaccount_id_fkey FOREIGN KEY (mb_no) REFERENCES public.measurement_book(measurement_book_id)

    );

CREATE TABLE public."designation" (

    desn_id varchar(100) NOT NULL,

    desn_name varchar(100) NOT NULL Unique,

    desn_status varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT desn_pkey PRIMARY KEY (desn_id)

);

CREATE TABLE public."party1details" (

   
    agreement_no varchar(255) NOT NULL,

    department_name_party1 varchar(255) NULL,

    designation varchar(255) NULL,

    employee_name varchar(255) NULL,

    witness_name_p1 varchar(255) NULL,

    address_p1 varchar(255) NULL,

    uid_p1 varchar(255) NULL,
    
    CONSTRAINT party1details_fkey FOREIGN KEY (agreement_no) REFERENCES public.contract_agreement(agreement_no)
    

);

CREATE TABLE public."agreement" (

   
    agreement_no varchar(255) NOT NULL,
    agreement_name varchar(255) NULL,

    agreement_date varchar(255) NULL,

    department_name_ai varchar(255) NULL,

    loa_no varchar(255) NULL,

    resolution_no int4 NULL,

    resolution_date varchar(100) NULL,

    tender_no varchar(255) NOT NULL,

    tender_date varchar(100) NULL,

    agreement_type varchar(255) NULL,

    defect_liability_period varchar(255) NULL,

    contract_period varchar(255) NULL,

    agreement_amount int4 NULL,

    payment_type varchar(255) NULL,
    
    CONSTRAINT agreement_pkey PRIMARY KEY (agreement_no),
    CONSTRAINT agreement_fkey FOREIGN KEY (tender_no) REFERENCES public.tender_entry(tender_id)

);

CREATE TABLE public."project_register" (

    register_id varchar(100) NOT NULL,

    scheme_name varchar(100) NULL,

    project_name varchar(100) NULL,

    work_name varchar(100) NULL,

    work_type varchar(100) NULL,

    estimated_number int4 NULL,

    estimated_work_cost varchar(100) NULL,

    sanctioned_tender_amount int8 NULL,

    status_name varchar(100) NULL,

    bill_received_till_date varchar(100) NULL,

    payment_received_till_date varchar(100) NULL,

   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT pregister_pkey PRIMARY KEY (register_id)

);

CREATE TABLE public."tender_category" (

    category_id varchar(100) NOT NULL,

    category_name varchar(100) NOT NULL Unique,

    category_status varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT tcategory_pkey PRIMARY KEY (category_id)

);

CREATE TABLE public."department" (

    dept_id varchar(100) NOT NULL,

    dept_name varchar(100) NOT NULL Unique,

    dept_status varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT dept_pkey PRIMARY KEY (dept_id)

);

CREATE TABLE public."function" (

    function_id varchar(100) NOT NULL,

    function_name varchar(100) NOT NULL Unique,

    function_code varchar(100) NOT NULL,

    function_level int4 NOT NULL,

    status varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT function_pkey PRIMARY KEY (function_id)

);

CREATE TABLE public."primary_account_head" (

    primary_accounthead_id varchar(100) NOT NULL,

    primary_accounthead_name varchar(100) NULL,

    primary_accounthead_accountno varchar(100) NULL,

    primary_accounthead_location varchar(100) NULL,

    account_status varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT primary_accounthead_pkey PRIMARY KEY (primary_accounthead_id)

);

CREATE TABLE public."vendor_class" (

    vendor_class_id varchar(100) NOT NULL,

    vendor_class_name varchar(100) NULL,

    vendor_class_status varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT vendorclass_pkey PRIMARY KEY (vendor_class_id)

);

CREATE TABLE public."vendor_type" (

    vendor_id varchar(100) NOT NULL,

    vendor_type_name varchar(100) NULL,

    vendor_type_status varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT vendortype_pkey PRIMARY KEY (vendor_id)

);

CREATE TABLE public."contractor_subtype" (

    contractor_id varchar(100) NOT NULL,

    contractor_stype_name varchar(100) NULL,

    contractor_stype_status varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT csubtype_pkey PRIMARY KEY (contractor_id)

);

CREATE TABLE public."bank_details" (

    bank_id varchar(100) NOT NULL,

    bank_name varchar(100) NULL,

    bank_branch varchar(100) NULL,
   
    bank_ifsc_code varchar(100) NULL UNIQUE,

    bank_branch_ifsc_code varchar(255) NULL,

    status varchar(100) NULL,

    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT bank_pkey PRIMARY KEY (bank_id)

);

CREATE TABLE public."work_status_report" (

    wsr_id varchar(100) NOT NULL,

    project_name varchar(100) NOT NULL Unique,

    work_name varchar(100) NULL,

    activity_name varchar(100) NULL,

    role_name varchar(100) NULL,

    employee_name varchar(100) NULL,

    start_date varchar(100) NULL,

    end_date varchar(100) NULL,

    remarks_content varchar(100) NULL,
   
    createdby varchar(255) NULL,

    lastmodifiedby varchar(255) NULL,

    createdtime int8 NULL,

    lastmodifiedtime int8 NULL, 

CONSTRAINT wsr_pkey PRIMARY KEY (wsr_id)

);




