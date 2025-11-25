

ALTER TABLE public.eg_noc_owner
ADD COLUMN uuid VARCHAR(256);



UPDATE public.eg_noc_owner lo
SET uuid = u.uuid
FROM public.eg_user u
WHERE u.id::text = lo.id;


ALTER TABLE public.eg_noc_owner
DROP CONSTRAINT pk_eg_noc_owner;



ALTER TABLE public.eg_noc_owner
ADD CONSTRAINT pk_eg_noc_owner PRIMARY KEY (uuid, nocid);


ALTER TABLE public.eg_noc_owner
DROP CONSTRAINT fk_eg_noc_owner;


ALTER TABLE public.eg_noc_owner
ADD CONSTRAINT fk_eg_noc_owner FOREIGN KEY (nocid)
  REFERENCES public.eg_noc (id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
