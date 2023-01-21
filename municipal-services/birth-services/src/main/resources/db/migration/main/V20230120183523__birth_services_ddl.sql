ALTER TABLE public.eg_birth_present_address
ADD COLUMN taluk_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN village_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ward_code  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN doorno  int,
ADD COLUMN sub_no  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN ot_zipcode  character varying(10) COLLATE pg_catalog."default",
Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";


ALTER TABLE public.eg_birth_present_address_audit
ADD COLUMN taluk_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN village_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ward_code  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN doorno  int,
ADD COLUMN subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN ot_zipcode  character varying(10) COLLATE pg_catalog."default",
Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";

ALTER TABLE public.eg_birth_permanent_address
ADD COLUMN taluk_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN village_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ward_code  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN doorno  int,
ADD COLUMN subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN ot_zipcode  character varying(10) COLLATE pg_catalog."default",
Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";

ALTER TABLE public.eg_birth_permanent_address_audit
ADD COLUMN taluk_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN village_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ward_code  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN doorno  int,
ADD COLUMN subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN ot_zipcode  character varying(10) COLLATE pg_catalog."default",
Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";

ALTER TABLE public.eg_birth_place
    ADD COLUMN ho_doorno  int,
    ADD COLUMN ho_subno  character varying(10) COLLATE pg_catalog."default",
    ADD COLUMN vehicle_haltplace  character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN vehicle_hospitalid  character varying(64) COLLATE pg_catalog."default",
    ADD COLUMN informant_addressline2  character varying(1000) COLLATE pg_catalog."default",
Add Column ho_res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN ho_main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";

ALTER TABLE public.eg_birth_place_audit
ADD COLUMN ho_doorno  int,
ADD COLUMN ho_subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN vehicle_haltplace  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN vehicle_hospitalid  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN informant_addressline2  character varying(1000) COLLATE pg_catalog."default",
Add Column ho_res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN ho_main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";


ALTER TABLE public.eg_register_birth_permanent_address
ADD COLUMN taluk_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN village_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ward_code  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN doorno  int,
ADD COLUMN subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN ot_zipcode  character varying(10) COLLATE pg_catalog."default",
Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";


ALTER TABLE public.eg_register_birth_permanent_address_audit
ADD COLUMN taluk_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN village_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ward_code  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN doorno  int,
ADD COLUMN subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN ot_zipcode  character varying(10) COLLATE pg_catalog."default",
Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";


ALTER TABLE public.eg_register_birth_present_address
ADD COLUMN taluk_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN village_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ward_code  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN doorno  int,
ADD COLUMN subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN ot_zipcode  character varying(10) COLLATE pg_catalog."default",
Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";


ALTER TABLE public.eg_register_birth_present_address_audit
ADD COLUMN taluk_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN village_name  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ward_code  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN doorno  int,
ADD COLUMN subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN ot_zipcode  character varying(10) COLLATE pg_catalog."default",
Add Column res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";



ALTER TABLE public.eg_register_birth_place
ADD COLUMN ho_doorno  int,
ADD COLUMN ho_subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN vehicle_haltplace  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN vehicle_hospitalid  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN informant_addressline2  character varying(1000) COLLATE pg_catalog."default",
Add Column ho_res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN ho_main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";

ALTER TABLE public.eg_register_birth_place_audit
ADD COLUMN ho_doorno  int,
ADD COLUMN ho_subno  character varying(10) COLLATE pg_catalog."default",
ADD COLUMN vehicle_haltplace  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN vehicle_hospitalid  character varying(64) COLLATE pg_catalog."default",
ADD COLUMN informant_addressline2  character varying(1000) COLLATE pg_catalog."default",
Add Column ho_res_asso_no_ml character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_main_place_en  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_street_locality_area_en  character varying(2000) COLLATE pg_catalog."default",
ADD COLUMN ho_main_place_ml  character varying(1000) COLLATE pg_catalog."default",
ADD COLUMN ho_street_locality_area_ml  character varying(2000) COLLATE pg_catalog."default";






