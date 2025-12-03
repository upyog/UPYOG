CREATE TABLE IF NOT EXISTS public.eg_ss_survey_entity
(
    uuid character varying(128),
    tenantid character varying(128),
    title character varying(60),
    category character varying(128),
    description character varying(140),
    startdate bigint,
    enddate bigint,
    postedby character varying(128),
    active boolean,
    answerscount bigint,
    hasresponded boolean,
    createdTime bigint,
    lastModifiedTime bigint,
    CONSTRAINT eg_ss_survey_entity_pkey PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS public.eg_ss_survey_section
(
    uuid character varying(128),
    surveyuuid character varying(128),
    title character varying(128),
    weightage integer,
    CONSTRAINT eg_ss_survey_section_pkey PRIMARY KEY (uuid),
    CONSTRAINT eg_ss_survey_section_surveyuuid_fkey FOREIGN KEY (surveyuuid) REFERENCES public.eg_ss_survey_entity (uuid)
);

CREATE TABLE IF NOT EXISTS public.eg_ss_question_weightage
(
    questionuuid character varying(128),
    sectionuuid character varying(128),
    weightage integer,
    qorder integer,
    CONSTRAINT eg_ss_question_weightage_pkey PRIMARY KEY (questionuuid, sectionuuid),
    CONSTRAINT eg_ss_question_weightage_sectionuuid_fkey FOREIGN KEY (sectionuuid) REFERENCES public.eg_ss_survey_section (uuid)
);
