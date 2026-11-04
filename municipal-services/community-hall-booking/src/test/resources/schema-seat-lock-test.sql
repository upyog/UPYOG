-- Minimal schema for seat-lock IT (Flyway disabled in tests)
CREATE TABLE IF NOT EXISTS seat_locks (
    seat_id            VARCHAR(256) PRIMARY KEY,
    user_id            VARCHAR(256) NOT NULL,
    lock_expiry_time   TIMESTAMPTZ  NOT NULL,
    version            BIGINT       NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_seat_locks_expiry ON seat_locks (lock_expiry_time);

CREATE TABLE IF NOT EXISTS seat_lock_idempotency (
    idempotency_key VARCHAR(128) PRIMARY KEY,
    seat_id         VARCHAR(256) NOT NULL,
    user_id         VARCHAR(256) NOT NULL,
    outcome         VARCHAR(64)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
