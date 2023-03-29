DROP FUNCTION IF EXISTS public.process_eg_birth_details_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_birth_father_information_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_birth_initiator_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_birth_mother_information_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_birth_permanent_address_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_birth_place_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_birth_present_address_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_birth_statitical_information_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_register_birth_details_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_register_birth_father_information_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_register_birth_mother_information_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_register_birth_multiple_details_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_register_birth_permanent_address_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_register_birth_place_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_register_birth_present_address_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_register_birth_statitical_information_audit() CASCADE;
DROP FUNCTION IF EXISTS public.process_eg_birth_certificate_request_audit() CASCADE;
DROP FUNCTION IF EXISTS public.fn_next_birth_id() CASCADE;


--select 'drop table if exists "' || tablename || '" cascade;' from pg_tables where schemaname = 'public';