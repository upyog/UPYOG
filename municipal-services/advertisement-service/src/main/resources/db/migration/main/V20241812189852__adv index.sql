CREATE INDEX idx_adv_tenant_id ON eg_adv_booking_detail (tenant_id);
CREATE INDEX idx_adv_booking_id ON eg_adv_booking_detail (booking_id);
CREATE INDEX idx_adv_booking_no ON eg_adv_booking_detail (booking_no);
CREATE INDEX idx_adv_booking_status ON eg_adv_booking_detail (booking_status);
CREATE INDEX idx_appl_applicant_mobile_no ON eg_adv_applicant_detail (applicant_mobile_no);
CREATE INDEX idx_adv_booking_date ON eg_adv_cart_detail (booking_date);

