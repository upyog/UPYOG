ALTER TABLE eg_ss_answer
RENAME COLUMN surveyid TO surveyuuid;

ALTER TABLE eg_ss_answer
RENAME COLUMN questionid TO questionuuid;

ALTER TABLE eg_ss_answer
ADD COLUMN sectionUuid character varying(128);

ALTER TABLE eg_ss_answer
ADD COLUMN comments character varying(2048);

ALTER TABLE eg_ss_answer
ADD CONSTRAINT fk_eg_ss_answer_section FOREIGN KEY (sectionuuid) REFERENCES eg_ss_survey_section (uuid);

ALTER TABLE eg_ss_answer
ADD CONSTRAINT fk_eg_ss_answer_survey FOREIGN KEY (surveyuuid) REFERENCES eg_ss_survey_entity (uuid);