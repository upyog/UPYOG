INSERT INTO eg_appconfig
(
    id,
    key_name,
    description,
    module
)
VALUES
(
    nextval('seq_eg_appconfig'),
    'REFUND_FINANCE_APPROVER_USER_ID',
    'User ID of the Finance approver for refund applications',
    (
        SELECT id
        FROM eg_module
        WHERE name = 'EGF'
    )
);

INSERT INTO eg_appconfig_values
(
    id,
    key_id,
    effective_from,
    value
)
VALUES
(
    nextval('seq_eg_appconfig_values'),
    (
        SELECT id
        FROM eg_appconfig
        WHERE key_name = 'REFUND_FINANCE_APPROVER_USER_ID'
          AND module = (
              SELECT id
              FROM eg_module
              WHERE name = 'EGF'
          )
    ),
    CURRENT_DATE,
    '9579'
);