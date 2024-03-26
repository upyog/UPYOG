ALTER TABLE public.agreement ADD agr_id varchar(255) NOT NULL;
alter table public.agreement alter column agr_id set not null;
ALTER TABLE public.agreement ADD CONSTRAINT agreement_id_pkey1 PRIMARY KEY (agr_id);


ALTER TABLE public.party1details ADD party1_id varchar(255) NOT NULL;
alter table public.party1details alter column party1_id set not null;
ALTER TABLE public.party1details ADD CONSTRAINT party1_id_pkey PRIMARY KEY (party1_id);




