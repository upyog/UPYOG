alter table public.eg_birth_details_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_birth_father_information_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_birth_initiator_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_birth_mother_information_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_birth_permanent_address_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_birth_place_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_birth_present_address_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_birth_statitical_information_audit
DROP COLUMN operation,DROP COLUMN stamp;


alter table public.eg_register_birth_details_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_register_birth_father_information_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_register_birth_mother_information_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_register_birth_multiple_details_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_register_birth_permanent_address_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_register_birth_place_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_register_birth_present_address_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_register_birth_statitical_information_audit
DROP COLUMN operation,DROP COLUMN stamp;
alter table public.eg_register_document_audit
DROP COLUMN operation,DROP COLUMN stamp;