alter table public.eg_birth_statitical_information
    alter column weight_of_child drop not null,
    alter column duration_of_pregnancy_in_week drop not null;

alter table public.eg_birth_statitical_information_audit
    alter column weight_of_child drop not null,
    alter column duration_of_pregnancy_in_week drop not null;