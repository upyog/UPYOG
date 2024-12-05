DROP Table eg_sv_street_vending_detail cascade;

DROP Table eg_sv_vendor_detail cascade;

DROP Table eg_sv_address_detail cascade;

DROP Table eg_sv_document_detail cascade;

DROP Table eg_sv_bank_detail cascade;

DROP Table eg_sv_operation_time_detail cascade;

DROP Table eg_sv_street_vending_draft_detail cascade;


//Delete query
delete from  eg_sv_street_vending_detail cascade;

delete from eg_sv_vendor_detail cascade;

delete from eg_sv_address_detail cascade;

delete from eg_sv_document_detail cascade;

delete from eg_sv_bank_detail cascade;

delete from eg_sv_operation_time_detail cascade;

delete from eg_sv_street_vending_draft_detail cascade;

-- workflow deletion of sv applications
delete from eg_wf_processinstance_v2 where businessservice='street-vending';

-- audit tables deletion of sv
delete from eg_sv_street_vending_detail_auditdetails;
 
delete from eg_sv_vendor_detail_auditdetails;
---

select  * from  eg_sv_street_vending_detail ;

select  * from eg_sv_vendor_detail ;

select  * from eg_sv_address_detail ;

select  * from eg_sv_document_detail ;

select  * from eg_sv_bank_detail ;

select  * from eg_sv_operation_time_detail ;

select  * from eg_sv_street_vending_draft_detail ;








