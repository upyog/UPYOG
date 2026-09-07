# Encryption Service (egov-enc-service)

Encryption Service is a centralized Spring Boot microservice used to secure sensitive data across the UPYOG/eGov platform. Other services call it over HTTP to encrypt, decrypt, sign, and verify data. It supports **both symmetric and asymmetric encryption**, digital signatures, and per-tenant key management.

### DB UML Diagram

- To Do

### Service Dependencies

- MDMS Service (egov-mdms-service) — used to fetch tenant IDs for key provisioning

### Swagger API Contract

https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/core-services/docs/enc-service-contract.yml#!/

---

## Service Details

Encryption Service offers the following features:

- **Encrypt** — Encrypts data based on input parameters (`tenantId`, `type`) and the value to encrypt. Output is always a string in the format `{keyId}|{base64Ciphertext}`.
- **Decrypt** — Decryption happens solely based on the encrypted input. The ciphertext embeds the key ID used at encryption time; no extra parameters are required.
- **Sign** — Hashes and signs data using RSA, producing a searchable unique identifier for a value (e.g. for lookup without storing plaintext).
- **Verify** — Verifies whether a given signature is valid for the provided claim/value.
- **Rotate Key** — Supports key rotation. Old keys remain in the database (deactivated) for decrypting historical data; new data is encrypted with the new active key.

---

## Encryption Architecture

### Overview

The service uses a **layered (envelope) encryption** design:

```
┌─────────────────────────────────────────────────┐
│  Layer 1: Master Key (never stored in DB)       │
│  - Software: PBKDF2 → AES-256                   │
│  - Cloud: AWS KMS                                 │
└─────────────────────────────────────────────────┘
                    ↓ encrypts/decrypts
┌─────────────────────────────────────────────────┐
│  Layer 2: Per-tenant keys (stored in PostgreSQL)  │
│  - Symmetric: AES key + IV (encrypted at rest)    │
│  - Asymmetric: RSA public + private (encrypted)   │
└─────────────────────────────────────────────────┘
                    ↓ encrypts/decrypts
┌─────────────────────────────────────────────────┐
│  Layer 3: Application data (plaintext in API)     │
└─────────────────────────────────────────────────┘
```

All cryptographic operations use the **Bouncy Castle** provider (`bcprov-jdk15on`) via Java JCA.

### Cryptographic Techniques

| Technique | Algorithm | Purpose |
|-----------|-----------|---------|
| **Symmetric** | `AES/GCM/NoPadding` (256-bit key, 12-byte IV, 128-bit auth tag) | Default data encryption (`type: "Normal"`) |
| **Asymmetric** | `RSA/NONE/OAEPWithSHA3-256AndMGF1Padding` (1024-bit) | High-sensitivity data (`type: "Imp"`) |
| **Digital Signature** | `SHA256withRSA` | Sign/verify for searchable hashes |
| **Key Wrapping** | PBKDF2 + AES-GCM (software) or AWS KMS (cloud) | Protect tenant keys at rest in the database |

---

## Symmetric Encryption (AES-GCM)

**When used:** Request `type` = `"Normal"` (mapped to `SYM` internally).

**Algorithm:** AES-256 in **GCM mode** (authenticated encryption — provides both confidentiality and integrity).

**Flow:**
1. Look up the tenant's **active symmetric key** from the in-memory `KeyStore`.
2. Decrypt the stored secret key and initialization vector (IV) using the master key provider.
3. Encrypt the plaintext (UTF-8) with AES-GCM.
4. Return Base64-encoded ciphertext prefixed with the key ID: `{keyId}|{base64Ciphertext}`.

**Implementation:** `SymmetricEncryptionUtil` → `SymmetricEncryptionService`

**Why GCM:** GCM provides authenticated encryption in a single step, making it a strong choice for field-level data encryption.

---

## Asymmetric Encryption (RSA-OAEP)

**When used:** Request `type` = `"Imp"` (mapped to `ASY` internally).

**Algorithm:** RSA 1024-bit with **OAEP padding** and **SHA3-256** hash.

**Flow:**
1. Fetch the tenant's active RSA key pair from `KeyStore`.
2. Encrypt with the **public key**.
3. Decrypt with the **private key** (resolved via the key ID embedded in the ciphertext).

**Implementation:** `AsymmetricEncryptionUtil` → `AsymmetricEncryptionService`

**Type mapping** (configured in `application.properties`):

```properties
type.to.method.map = {"Normal":"SYM","Imp":"ASY"}
```

---

## Digital Signatures

Sign and verify operations use **asymmetric cryptography for integrity and authenticity**, not confidentiality.

**Algorithm:** `SHA256withRSA` — the data is hashed, then signed with the RSA private key.

**Use case:** Generate deterministic, searchable signatures of values (e.g. phone numbers) without storing plaintext. The signature can be used for lookup; verification confirms the original value.

**Implementation:** `SignatureUtil` → `SignatureService`

---

## Key Management

### Per-Tenant Keys

- Each **tenant** (fetched from MDMS) gets its own symmetric and asymmetric key pair.
- Keys are stored in PostgreSQL tables: `eg_enc_symmetric_keys` and `eg_enc_asymmetric_keys`.
- Only **one active key per tenant** per key type is enforced via unique partial indexes.
- Parent tenant IDs also receive keys (e.g. for tenant `pg.amritsar`, keys are created for both `pg` and `pg.amritsar`).

### Key Generation

On startup (and when new tenants are discovered), `KeyGenerator` creates:

| Key Type | Generation Method | Size |
|----------|-------------------|------|
| Symmetric | `SecureRandom` → AES secret key + IV | 256-bit key, 12-byte IV |
| Asymmetric | `KeyPairGenerator` → RSA key pair | 1024-bit |

Generated keys are **encrypted with the master key** before being persisted to the database.

### In-Memory KeyStore

On startup and refresh, `KeyStore`:

1. Loads all keys from the database.
2. Decrypts them using the configured master key provider.
3. Indexes keys by `keyId` and maintains active key mappings per `tenantId`.

Keys are held in memory for fast encrypt/decrypt operations during runtime.

### Master Key Providers

Two master key providers are supported (selected via `master.password.provider`):

#### Software-based (default)

Master key is derived from configuration using **PBKDF2WithHmacSHA256** (65536 iterations, 256-bit output), then used with AES-GCM to wrap/unwrap tenant keys.

| Property | Description |
|----------|-------------|
| `master.password` | Master password for key derivation |
| `master.salt` | 8-byte salt for PBKDF2 |
| `master.initialvector` | 12-byte IV for AES-GCM key wrapping |

#### AWS KMS

When `master.password.provider=awskms`, tenant keys are encrypted/decrypted using AWS KMS instead of a local password.

| Property | Description |
|----------|-------------|
| `aws.kms.access.key` | AWS access key |
| `aws.kms.secret.key` | AWS secret key |
| `aws.kms.region` | AWS region |
| `aws.kms.master.password.key.id` | KMS key ID for wrapping |

---

## Encrypt / Decrypt Flow

### Encrypt (`POST /crypto/v1/_encrypt`)

**Request example:**

```json
{
  "encryptionRequests": [{
    "tenantId": "pg.amritsar",
    "type": "Normal",
    "value": "9876543210"
  }]
}
```

**Steps:**
1. Validate tenant exists (auto-generate keys if new tenant).
2. Map `type` → `SYM` or `ASY` using `type.to.method.map`.
3. `ProcessJSONUtil` recursively walks JSON objects/arrays and encrypts **values only** (JSON keys remain unchanged).
4. Return encrypted string: `"123456|aGVsbG8gd29ybGQ..."` (`{keyId}|{base64Ciphertext}`).

### Decrypt (`POST /crypto/v1/_decrypt`)

**Request example:**

```json
{
  "value": "123456|aGVsbG8gd29ybGQ..."
}
```

**Steps:**
1. Parse `keyId` from the `{keyId}|{ciphertext}` format.
2. Resolve key type (symmetric or asymmetric) from `KeyStore` — no `type` parameter needed.
3. Decrypt using the matching key and return original plaintext.

---

## Key Rotation

| Endpoint | Behavior |
|----------|----------|
| `POST /crypto/v1/_rotatekey` | Deactivates keys for a given tenant and generates new ones |
| `POST /crypto/v1/_rotateallkeys` | Rotates keys for all tenants |

Old keys remain in the database with `active=false`, so historical ciphertext can still be decrypted. All new encryption uses the newly generated active key.

---

## Configurations

Following are the configurable properties in `application.properties`:

| Property | Default Value | Remarks |
|----------|---------------|---------|
| `method.symmetric` | `AES/GCM/NoPadding` | Symmetric encryption algorithm (Bouncy Castle compliant) |
| `method.asymmetric` | `RSA/NONE/OAEPWithSHA3-256AndMGF1Padding` | Asymmetric encryption algorithm |
| `method.signature` | `SHA256withRSA` | Digital signature algorithm |
| `size.key.symmetric` | `256` | Symmetric key size in bits (AES: 128/192/256) |
| `size.key.asymmetric` | `1024` | Asymmetric key size in bits (RSA) |
| `size.initialvector` | `12` | IV size in bytes (12 bytes for AES-GCM) |
| `length.keyid` | `6` | Length of generated key IDs |
| `master.password` | — | Master password for software-based key wrapping |
| `master.salt` | — | 8-byte salt for PBKDF2 key derivation |
| `master.initialvector` | — | 12-byte IV for master key AES-GCM operations |
| `master.password.provider` | `software` | Master key provider: `software` or `awskms` |
| `type.to.method.map` | `{"Normal":"SYM","Imp":"ASY"}` | Maps request `type` to encryption method |

Refer to [Bouncy Castle specifications](https://www.bouncycastle.org/specifications.html) for supported algorithm names.

---

## API Details

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/crypto/v1/_encrypt` | Encrypts the given input value(s) or values within a JSON object |
| POST | `/crypto/v1/_decrypt` | Decrypts the given input value(s) or values within a JSON object |
| POST | `/crypto/v1/_sign` | Generates a hash-and-sign signature for a given value |
| POST | `/crypto/v1/_verify` | Verifies whether a signature is correct for the provided value |
| POST | `/crypto/v1/_rotatekey` | Deactivates keys for the given tenant and generates new keys (both symmetric and asymmetric) |
| POST | `/crypto/v1/_rotateallkeys` | Deactivates and regenerates keys for all tenants |

### Ciphertext Format

All encrypted values are returned as strings in this format:

```
{keyId}|{base64EncodedCiphertext}
```

- `keyId` — 6-digit identifier of the key used for encryption (enables decryption without extra metadata).
- `base64EncodedCiphertext` — Base64-encoded encrypted bytes.

---

## Database Schema

| Table | Purpose |
|-------|---------|
| `eg_enc_symmetric_keys` | Stores per-tenant AES keys (encrypted secret key + IV) |
| `eg_enc_asymmetric_keys` | Stores per-tenant RSA key pairs (encrypted public + private keys) |

Both tables track `key_id`, `tenant_id`, and an `active` flag for key rotation support.

---

## Summary

| Question | Answer |
|----------|--------|
| Symmetric or asymmetric? | **Both.** AES-GCM for normal data; RSA-OAEP for important data. |
| Default encryption | AES-256-GCM (symmetric) |
| When is RSA used? | `type: "Imp"` for encryption; always for sign/verify |
| Key storage | Encrypted in PostgreSQL; master key in config or AWS KMS |
| Multi-tenancy | Separate key pairs per tenant |
| Crypto library | Bouncy Castle + Java JCA |
| Output format | `{keyId}\|{base64-ciphertext}` |

---

### Kafka Consumers

NA

### Kafka Producers

NA
