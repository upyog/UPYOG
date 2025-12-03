-- =====================================================================
-- Migration: Community Hall Booking Schema Update (Fully Idempotent)
-- =====================================================================
-- Includes:
-- 1. CREATE TABLE eg_chb_owner (if not exists)
-- 2. ADD COLUMN community_hall_name to booking detail tables
-- 3. DROP NOT NULL constraints from redundant fields
-- 4. ADD COLUMN booking_end_date to slot detail tables
-- =====================================================================


-- ==========================================================
-- 1️⃣ CREATE TABLE eg_chb_owner (if not exists)
-- ==========================================================
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.tables
            WHERE table_name = 'eg_chb_owner'
        ) THEN
            CREATE TABLE eg_chb_owner (
                                          uuid                 VARCHAR(256) NOT NULL,
                                          tenantid             VARCHAR(256),
                                          booking_id           VARCHAR(256) NOT NULL,
                                          status               VARCHAR(128),
                                          isprimaryowner       BOOLEAN DEFAULT FALSE,
                                          ownertype            VARCHAR(256),
                                          ownershippercentage  VARCHAR(128),
                                          institutionid        VARCHAR(128),
                                          relationship         VARCHAR(128),
                                          createdby            VARCHAR(128),
                                          createdtime          BIGINT,
                                          lastmodifiedby       VARCHAR(128),
                                          lastmodifiedtime     BIGINT,
                                          additionaldetails    JSONB,
                                          CONSTRAINT pk_eg_chb_owner PRIMARY KEY (uuid, booking_id),
                                          CONSTRAINT fk_eg_chb_owner FOREIGN KEY (booking_id)
                                              REFERENCES eg_chb_booking_detail(booking_id)
                                              ON DELETE CASCADE
                                              ON UPDATE CASCADE
            );
        END IF;
    END $$;

-- Create indexes safely
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_indexes WHERE indexname = 'idx_eg_chb_owner_booking_id'
        ) THEN
            CREATE INDEX idx_eg_chb_owner_booking_id ON eg_chb_owner (booking_id);
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM pg_indexes WHERE indexname = 'idx_eg_chb_owner_tenantid'
        ) THEN
            CREATE INDEX idx_eg_chb_owner_tenantid ON eg_chb_owner (tenantid);
        END IF;
    END $$;


-- ==========================================================
-- 2️⃣ ADD COLUMN community_hall_name (if not exists)
-- ==========================================================
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'eg_chb_booking_detail'
              AND column_name = 'community_hall_name'
        ) THEN
            ALTER TABLE eg_chb_booking_detail
                ADD COLUMN community_hall_name VARCHAR(64);
        END IF;

        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'eg_chb_booking_detail_audit'
              AND column_name = 'community_hall_name'
        ) THEN
            ALTER TABLE eg_chb_booking_detail_audit
                ADD COLUMN community_hall_name VARCHAR(64);
        END IF;

        -- Uncomment if needed later
        /*
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'eg_chb_booking_detail_init'
              AND column_name = 'community_hall_name'
        ) THEN
            ALTER TABLE eg_chb_booking_detail_init
                ADD COLUMN community_hall_name VARCHAR(64);
        END IF;
        */
    END $$;


-- ==========================================================
-- 3️⃣ DROP NOT NULL Constraints (if currently applied)
-- ==========================================================
DO $$
    BEGIN
        -- eg_chb_applicant_detail
        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_applicant_detail'
          AND column_name = 'account_no'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_applicant_detail ALTER COLUMN account_no DROP NOT NULL;
        END IF;

        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_applicant_detail'
          AND column_name = 'ifsc_code'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_applicant_detail ALTER COLUMN ifsc_code DROP NOT NULL;
        END IF;

        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_applicant_detail'
          AND column_name = 'bank_name'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_applicant_detail ALTER COLUMN bank_name DROP NOT NULL;
        END IF;

        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_applicant_detail'
          AND column_name = 'bank_branch_name'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_applicant_detail ALTER COLUMN bank_branch_name DROP NOT NULL;
        END IF;

        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_applicant_detail'
          AND column_name = 'account_holder_name'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_applicant_detail ALTER COLUMN account_holder_name DROP NOT NULL;
        END IF;


        -- eg_chb_address_detail
        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_address_detail'
          AND column_name = 'city'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_address_detail ALTER COLUMN city DROP NOT NULL;
        END IF;

        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_address_detail'
          AND column_name = 'city_code'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_address_detail ALTER COLUMN city_code DROP NOT NULL;
        END IF;

        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_address_detail'
          AND column_name = 'locality'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_address_detail ALTER COLUMN locality DROP NOT NULL;
        END IF;

        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_address_detail'
          AND column_name = 'locality_code'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_address_detail ALTER COLUMN locality_code DROP NOT NULL;
        END IF;

        PERFORM 1 FROM information_schema.columns
        WHERE table_name = 'eg_chb_address_detail'
          AND column_name = 'pincode'
          AND is_nullable = 'NO';
        IF FOUND THEN
            ALTER TABLE eg_chb_address_detail ALTER COLUMN pincode DROP NOT NULL;
        END IF;
    END $$;


-- ==========================================================
-- 4️⃣ ADD COLUMN booking_end_date (if not exists)
-- ==========================================================
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'eg_chb_slot_detail'
              AND column_name = 'booking_end_date'
        ) THEN
            ALTER TABLE eg_chb_slot_detail
                ADD COLUMN booking_end_date DATE;
        END IF;

        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'eg_chb_slot_detail_audit'
              AND column_name = 'booking_end_date'
        ) THEN
            ALTER TABLE eg_chb_slot_detail_audit
                ADD COLUMN booking_end_date DATE;
        END IF;
    END $$;

-- =====================================================================
-- ✅ End of Migration
-- =====================================================================
