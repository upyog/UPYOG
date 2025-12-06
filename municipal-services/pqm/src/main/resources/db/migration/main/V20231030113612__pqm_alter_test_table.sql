ALTER TABLE eg_pqm_tests RENAME COLUMN id TO testId;

ALTER TABLE eg_pqm_tests RENAME COLUMN testType TO sourceType;

ALTER TABLE eg_pqm_tests_auditlog RENAME COLUMN id TO testId;

ALTER TABLE eg_pqm_tests_auditlog RENAME COLUMN testType TO sourceType;

