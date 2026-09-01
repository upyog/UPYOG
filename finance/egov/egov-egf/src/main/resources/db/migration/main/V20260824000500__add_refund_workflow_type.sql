INSERT INTO eg_wf_types
(
    id,
    module,
    type,
    link,
    createdby,
    createddate,
    lastmodifiedby,
    lastmodifieddate,
    enabled,
    grouped,
    typefqn,
    displayname,
    version
)
SELECT
    nextval('seq_eg_wf_types'),
    module.id,
    'RefundApplication',
    '/services/EGF/refund/refundApplication-view.action?id=:ID',
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    true,
    false,
    'org.egov.model.refund.RefundApplication',
    'Refund Approval',
    0
FROM eg_module module
WHERE module.name = 'EGF'
  AND NOT EXISTS
  (
      SELECT 1
      FROM eg_wf_types
      WHERE type = 'RefundApplication'
  );