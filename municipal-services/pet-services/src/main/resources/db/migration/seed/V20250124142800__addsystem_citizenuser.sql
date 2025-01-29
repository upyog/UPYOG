DO $$
DECLARE
    v_user_count INT;
	v_userid BIGINT;
BEGIN
    
    SELECT COUNT(*) INTO v_user_count FROM eg_user WHERE username = '8888888888';

    IF v_user_count = 0 THEN
        
        INSERT INTO eg_user (
            title, salutation, dob, locale, username, password, pwdexpirydate, mobilenumber,
            altcontactnumber, emailid, createddate, lastmodifieddate, createdby, lastmodifiedby, active,
            name, gender, pan, aadhaarnumber, type, version, guardian, guardianrelation, signature,
            accountlocked, bloodgroup, photo, identificationmark, tenantid, id, uuid, accountlockeddate, alternatemobilenumber
        )
        VALUES (
            NULL, NULL, NULL, 'en_IN',
            '8888888888',
            '$2a$10$uheIOutTnD33x7CDqac1zOL8DMiuz7mWplToPgcf7oxAI9OzRKxmK',
            (CURRENT_DATE + INTERVAL '20 years'),
            '8888888888', NULL, NULL,
            CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
            1, 1, TRUE,
            'system_citizen', NULL, NULL, NULL, 'CITIZEN',
            0, NULL, NULL, NULL,
            FALSE, 'B_POSITIVE', NULL, 'None',
            'default', nextval('seq_eg_user'), NULL, NULL, NULL
        )
        RETURNING id INTO v_userid;

        INSERT INTO eg_userrole_v1 (
            role_code, role_tenantid, user_id, user_tenantid, lastmodifieddate
        )
        VALUES (
            'CITIZEN', 'default',
            v_userid,
            'default',
            CURRENT_TIMESTAMP
        );
    END IF;
END $$;