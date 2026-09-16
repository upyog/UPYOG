-- Register the module used by refund Payment Vouchers.
-- Use the tenant schema selected by the Finance migration runner.

LOCK TABLE eg_modules IN EXCLUSIVE MODE;

DO $$
BEGIN
    -- Support environments where the entry was already added manually.
    IF EXISTS (
        SELECT 1
        FROM eg_modules
        WHERE name = 'Refund'
    ) THEN
        RETURN;
    END IF;

    -- Fail clearly rather than overwrite another module or silently skip.
    IF EXISTS (
        SELECT 1
        FROM eg_modules
        WHERE id = 11
    ) THEN
        RAISE EXCEPTION
            'Cannot register Refund module: eg_modules ID 11 is already in use';
    END IF;

    INSERT INTO eg_modules (id, name, description)
    VALUES (11, 'Refund', 'Refund Finance Integration');
END;
$$;