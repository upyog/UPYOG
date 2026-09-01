WITH next_id AS
(
    SELECT COALESCE(MAX(id), 0) + 1 AS id
    FROM eg_wf_matrix
)
INSERT INTO eg_wf_matrix
(
    id,
    department,
    objecttype,
    currentstate,
    currentstatus,
    pendingactions,
    currentdesignation,
    additionalrule,
    nextstate,
    nextaction,
    nextdesignation,
    nextstatus,
    validactions,
    fromqty,
    toqty,
    fromdate,
    todate,
    version,
    enablefields,
    forwardenabled,
    smsemailenabled,
    nextref,
    rejectenabled
)
SELECT
    id,
    'ANY',
    'RefundApplication',
    'NEW',
    NULL,
    NULL,
    'Accounts Officer',
    NULL,
    'Created',
    'Refund Approval',
    'Accounts Officer',
    'Created',
    'Forward',
    NULL,
    NULL,
    DATE '2026-08-24',
    DATE '2099-04-01',
    0,
    NULL,
    TRUE,
    FALSE,
    NULL,
    FALSE
FROM next_id;



WITH next_id AS
(
    SELECT COALESCE(MAX(id), 0) + 1 AS id
    FROM eg_wf_matrix
)
INSERT INTO eg_wf_matrix
(
    id,
    department,
    objecttype,
    currentstate,
    currentstatus,
    pendingactions,
    currentdesignation,
    additionalrule,
    nextstate,
    nextaction,
    nextdesignation,
    nextstatus,
    validactions,
    fromqty,
    toqty,
    fromdate,
    todate,
    version,
    enablefields,
    forwardenabled,
    smsemailenabled,
    nextref,
    rejectenabled
)
SELECT
    id,
    'ANY',
    'RefundApplication',
    'Created',
    NULL,
    NULL,
    'Accounts Officer',
    NULL,
    'END',
    'END',
    NULL,
    NULL,
    'Approve,Reject',
    NULL,
    NULL,
    DATE '2026-08-24',
    DATE '2099-04-01',
    0,
    NULL,
    FALSE,
    FALSE,
    NULL,
    TRUE
FROM next_id;