CREATE INDEX IF NOT EXISTS idx_eg_ew_requests_tenantId ON eg_ew_requests (tenantid);
CREATE INDEX IF NOT EXISTS idx_eg_ew_requests_requestId ON eg_ew_requests (requestid);
CREATE INDEX IF NOT EXISTS idx_eg_ew_requests_requestStatus ON eg_ew_requests (requeststatus);
CREATE INDEX IF NOT EXISTS idx_eg_ew_requests_mobilenumber ON eg_ew_applicantdetails (mobilenumber);
CREATE INDEX IF NOT EXISTS idx_eg_ew_requests_createdTime ON eg_ew_requests (createdTime);