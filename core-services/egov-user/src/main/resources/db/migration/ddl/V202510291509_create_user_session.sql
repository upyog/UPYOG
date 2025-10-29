
CREATE TABLE IF NOT EXISTS public.user_sessions
(
    id uuid NOT NULL,
    user_uuid character varying(100) COLLATE pg_catalog."default" NOT NULL,
    user_id character varying(100) COLLATE pg_catalog."default" NOT NULL,
    username character varying(100) COLLATE pg_catalog."default" NOT NULL,
    login_time timestamp with time zone NOT NULL,
    logout_time timestamp with time zone,
    ip_address character varying(45) COLLATE pg_catalog."default",
    iscurrentlylogin boolean DEFAULT false,
    isautologout boolean DEFAULT false,
    CONSTRAINT user_sessions_pkey PRIMARY KEY (id)
)