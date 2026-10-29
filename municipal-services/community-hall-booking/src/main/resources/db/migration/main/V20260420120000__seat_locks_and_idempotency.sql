-- Timer-based seat locks (DB backend)
CREATE TABLE IF NOT EXISTS seat_locks (
    seat_id            VARCHAR(256) PRIMARY KEY,
    user_id            VARCHAR(256) NOT NULL,
    lock_expiry_time   TIMESTAMPTZ  NOT NULL,
    version            BIGINT       NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_seat_locks_expiry ON seat_locks (lock_expiry_time);

-- Idempotency for confirm / payment callbacks (avoids duplicate booking on retries)
CREATE TABLE IF NOT EXISTS seat_lock_idempotency (
    idempotency_key VARCHAR(128) PRIMARY KEY,
    seat_id         VARCHAR(256) NOT NULL,
    user_id         VARCHAR(256) NOT NULL,
    outcome         VARCHAR(64)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_seat_lock_idempotency_seat ON seat_lock_idempotency (seat_id, user_id);
