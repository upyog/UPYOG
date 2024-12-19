CREATE INDEX idx_eg_ew_requests_tenantId ON eg_ew_requests(tenantid);
CREATE INDEX idx_eg_ew_requests_requestId ON eg_ew_requests(requestid);
CREATE INDEX idx_eg_ew_requests_requestStatus ON eg_ew_requests(requeststatus);
CREATE INDEX idx_eg_ew_requests_mobilenumber ON eg_ew_requests(mobilenumber);
CREATE INDEX idx_eg_ew_requests_createdTime ON eg_ew_requests(createdTime);