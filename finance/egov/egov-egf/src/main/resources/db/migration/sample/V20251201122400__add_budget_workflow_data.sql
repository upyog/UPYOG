INSERT INTO eg_module
(id, name, enabled, contextroot, parentmodule, displayname, ordernumber, rootmodule)
VALUES(nextval('SEQ_EG_MODULE'), 'BudgetRegister', true, 'egf', NULL, 'Budget', NULL, NULL);

INSERT INTO eg_wf_matrix
(id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate, "version", enablefields, forwardenabled, smsemailenabled, nextref, rejectenabled)
VALUES(NEXTVAL('SEQ_EG_WF_MATRIX'), 'ANY', 'BudgetRegister', 'New', '', NULL, 'Executive Officer', NULL, 'PENDING_EO_APPROVAL', 'Forward, Reject or Revert', '', 'INPROGRESS', 'Forward,Can', NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO eg_wf_matrix
(id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate, "version", enablefields, forwardenabled, smsemailenabled, nextref, rejectenabled)
VALUES(NEXTVAL('SEQ_EG_WF_MATRIX'), 'ANY', 'BudgetRegister', 'PENDING_EO_APPROVAL', NULL, NULL, 'DMA,Financial Management Officer,Accounts Officer', NULL, 'FORWARDED_TO_DMA', 'DMA approval pending', 'DMA', NULL, 'Approve,Reject', NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO eg_wf_matrix
(id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate, "version", enablefields, forwardenabled, smsemailenabled, nextref, rejectenabled)
VALUES(NEXTVAL('SEQ_EG_WF_MATRIX'), 'ANY', 'BudgetRegister', 'SUBMITTED_TO_EO', NULL, NULL, 'Executive Officer', NULL, 'FORWARDED_TO_DMA', 'APPROVE,REJECT', 'DMA', NULL, 'FORWARD,REJECT,REVERT', NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL);

SELECT setval('seq_eg_wf_types', (SELECT COALESCE(MAX(id), 0) FROM eg_wf_types));

INSERT INTO eg_wf_types
(id, module, type, link, createdby, createddate, lastmodifiedby, lastmodifieddate, enabled, grouped, typefqn, displayname, version)
VALUES(nextval('seq_eg_wf_types'), (select id from eg_module where name='BudgetRegister'), 'BudgetRegister', '/services/EGF/budget/register/workflow/form/:ID', 1, NULL, NULL, NULL, true, false, 'org.egov.model.budget.BudgetRegister', 'Budget Input', 0);

INSERT INTO egw_status
(id, moduletype, description, lastmodifieddate, code, order_id)
VALUES(nextval('SEQ_EGW_STATUS'), 'BudgetRegister', 'New', '2025-11-11 16:13:06.324', 'New', NULL);
INSERT INTO egw_status
(id, moduletype, description, lastmodifieddate, code, order_id)
VALUES(nextval('SEQ_EGW_STATUS'), 'BudgetRegister', 'Created', '2025-11-11 16:14:12.642', 'Created', NULL);
INSERT INTO egw_status
(id, moduletype, description, lastmodifieddate, code, order_id)
VALUES(nextval('SEQ_EGW_STATUS'), 'BudgetRegister', 'Cancelled', '2025-11-11 16:14:34.575', 'Cancelled', NULL);
INSERT INTO egw_status
(id, moduletype, description, lastmodifieddate, code, order_id)
VALUES(nextval('SEQ_EGW_STATUS'), 'BudgetRegister', 'Approved', '2025-11-11 16:14:55.589', 'Approved', NULL);
INSERT INTO egw_status
(id, moduletype, description, lastmodifieddate, code, order_id)
VALUES(nextval('SEQ_EGW_STATUS'), 'BudgetRegister', 'Rejected', '2025-11-11 16:15:30.384', 'Rejected', NULL);
INSERT INTO egw_status
(id, moduletype, description, lastmodifieddate, code, order_id)
VALUES(nextval('SEQ_EGW_STATUS'), 'BudgetRegister', 'FMO FORWARDED', '2025-11-28 20:47:25.365', 'FMO_FORWARDED', NULL);