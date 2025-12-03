ALTER TABLE public.eg_ss_question
ADD COLUMN IF NOT EXISTS categoryid VARCHAR(128) REFERENCES eg_ss_category(id);

ALTER TABLE public.eg_ss_question ADD COLUMN IF NOT EXISTS tenantid varchar(128) ;

CREATE INDEX IF NOT EXISTS idx_eg_ss_question_uuid ON eg_ss_question(uuid);
CREATE INDEX IF NOT EXISTS idx_eg_ss_question_questionstatement ON eg_ss_question(questionstatement);
CREATE INDEX IF NOT EXISTS idx_eg_ss_question_tenantid ON eg_ss_question(tenantid);
CREATE INDEX IF NOT EXISTS idx_eg_ss_question_status ON eg_ss_question(status);
CREATE INDEX IF NOT EXISTS idx_eg_ss_question_type ON eg_ss_question(type);
CREATE INDEX IF NOT EXISTS idx_eg_ss_question_tenantid_categoryid ON eg_ss_question(tenantid,categoryid);
CREATE INDEX IF NOT EXISTS idx_eg_ss_question_label_tenantid_categoryid_quesst ON eg_ss_question(tenantid,categoryid,questionstatement);
CREATE INDEX IF NOT EXISTS idx_eg_ss_question_label_tenantid_categoryid_type ON eg_ss_question(tenantid,categoryid,type);