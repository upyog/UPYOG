
CREATE INDEX idx_eg_ptr_registration_tenant_id ON eg_ptr_registration (tenantid);

CREATE INDEX idx_eg_ptr_registration_status ON eg_ptr_registration (status);

CREATE INDEX idx_eg_ptr_registration_id ON eg_ptr_registration (id);

CREATE INDEX idx_eg_ptr_registration_application_number ON eg_ptr_registration (applicationnumber);

CREATE INDEX idx_eg_ptr_registration_mobile_number ON eg_ptr_registration (mobilenumber);

CREATE INDEX idx_eg_ptr_registration_created_time ON eg_ptr_registration (createdtime);
