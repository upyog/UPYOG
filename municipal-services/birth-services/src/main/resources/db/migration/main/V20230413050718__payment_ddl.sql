alter table eg_birth_details
    add column  has_payment boolean,
    add column  is_payment_success boolean,
    add column  amount decimal;

alter table eg_birth_details_audit
    add column  has_payment boolean,
    add column  is_payment_success boolean,
    add column  amount decimal;