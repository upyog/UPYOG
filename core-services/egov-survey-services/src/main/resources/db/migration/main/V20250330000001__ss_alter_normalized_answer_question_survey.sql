
CREATE TABLE public.eg_ss_question_option (
    uuid VARCHAR(128) NOT NULL,
    questionuuid VARCHAR(1000) ,
    optiontext VARCHAR(1000) ,
    weightage NUMERIC(10, 7) ,
    createdby VARCHAR(1000) ,
    lastmodifiedby VARCHAR(1000),
    createdtime INT8 ,
    lastmodifiedtime INT8,
    CONSTRAINT pk_eg_ss_question_option_uuid PRIMARY KEY (uuid),
    CONSTRAINT fk_eg_ss_question_option_questionuuid FOREIGN KEY (questionuuid) REFERENCES public.eg_ss_question(uuid)
);


CREATE TABLE public.eg_ss_survey_response (
    uuid VARCHAR(128) NOT NULL,
    surveyuuid VARCHAR(1000) NOT NULL,
    citizenid VARCHAR(1000) NOT NULL,
    tenantid VARCHAR(1000),
    locality VARCHAR(1000) ,
    status VARCHAR(50) DEFAULT 'draft',
    createdby VARCHAR(1000) ,
    lastmodifiedby VARCHAR(1000),
    createdtime INT8 ,
    lastmodifiedtime INT8 ,
    CONSTRAINT pk_eg_ss_survey_response_uuid PRIMARY KEY (uuid),
    CONSTRAINT unique_survey_citizen_tenant UNIQUE (surveyuuid, citizenid, tenantid)
);


CREATE TABLE public.eg_ss_answer_detail (
    uuid VARCHAR(128) NOT NULL,
    answeruuid VARCHAR(1000) NOT NULL,
    answertype VARCHAR(50),
    answercontent TEXT ,
    weightage NUMERIC(10, 7),
    comments TEXT,
	createdby VARCHAR(1000) ,
    lastmodifiedby VARCHAR(1000),
    createdtime INT8 ,
    lastmodifiedtime INT8,
    CONSTRAINT pk_eg_ss_answer_detail_uuid PRIMARY KEY (uuid),
    CONSTRAINT fk_eg_ss_answer_detail_answeruuid FOREIGN KEY (answeruuid) REFERENCES public.eg_ss_answer(uuid)
);


ALTER TABLE public.eg_ss_question
DROP COLUMN options;


ALTER TABLE public.eg_ss_answer
ADD COLUMN surveyresponseuuid VARCHAR(1000) NOT NULL,
ADD CONSTRAINT fk_eg_ss_answer_response FOREIGN KEY (surveyresponseuuid) REFERENCES public.eg_ss_survey_response(uuid);

ALTER TABLE public.eg_ss_answer
DROP COLUMN surveyuuid,
DROP COLUMN answer,
DROP COLUMN citizenid,
DROP COLUMN city;

ALTER TABLE public.eg_ss_answer
ADD CONSTRAINT unique_surveyresponse_question UNIQUE (surveyresponseuuid, questionuuid);

