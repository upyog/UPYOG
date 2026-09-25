# File Store Service (egov-filestore)

Filestore provides file upload and retrieval capability for all modules in the DIGIT/UPYOG suite.

### DB UML Diagram

N/A

### Service Dependencies

N/A

### Swagger API Contract

- Please refer to the [Swagger API Contract](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/core-services/docs/filestore-service-contract.yml#!/) for Filestore service to understand the structure of APIs and to have visualization of all internal APIs.

## Service Details

File uploader for the UPYOG Portal. The service can be configured to provide upload and download for files. The application uses one of the following filestore backends: AWS S3, Azure Blob, Minio, or local filesystem. At least one backend must be enabled for the application to start successfully (see `LOCALSETUP.md`).

Uploaded files are stored in the configured cloud backend and a metadata record is written to the `eg_filestoremap` table. Each upload receives a unique `fileStoreId` (UUID) that other services use as a reference.

### Storage layout

All uploads (documents, images, audio/video) use the same path pattern:

```
{bucket}/{tenantId}/{module}/{Month}/{day}/{timestamp}{random}.{extension}
```

The `filename` column in `eg_filestoremap` holds this full path. The `filesource` column records the backend used (e.g. `minio`, `AzureBlobStorage`).

## API Details

Base path: `/filestore/v1/files`

### POST /v1/files — Upload files (multipart)

Upload one or more files from the client using `multipart/form-data`. Supports images, PDFs, documents, KML/text, and other formats configured in `allowed.formats.map`.

| Parameter     | Required | Description                                      |
|---------------|----------|--------------------------------------------------|
| `file`        | Yes      | One or more files to upload                      |
| `tenantId`    | Yes      | Tenant identifier (e.g. `pb.amritsar`)           |
| `module`      | Yes      | Owning module (e.g. `PT`, `PGR`, `WS`)           |
| `tag`         | No       | Optional tag for grouping files                  |
| `requestInfo` | No       | DIGIT request info JSON string                   |

**Response:** `201 Created` with a list of `{ fileStoreId, tenantId }` entries.

**Behaviour:**
- Validates file extension and content-type against `allowed.formats.map`.
- Image uploads automatically generate three thumbnail variants (`_small`, `_medium`, `_large`).

**Example:**
```bash
curl -X POST "http://localhost:8083/filestore/v1/files" \
  -F "file=@photo.jpg" \
  -F "tenantId=pb.amritsar" \
  -F "module=PT" \
  -F "tag=property-photo"
```

---

### POST /v1/files/external-media — Upload audio/video (multipart)

Dedicated endpoint for direct audio/video uploads from the client. The file is validated, stored in Minio/S3/Azure using the same storage path as regular uploads, and a `fileStoreId` is returned.

**Content-Type:** `multipart/form-data`

| Parameter     | Required | Description                                      |
|---------------|----------|--------------------------------------------------|
| `file`        | Yes      | Audio or video file to upload                    |
| `tenantId`    | Yes      | Tenant identifier                                |
| `module`      | Yes      | Owning module                                    |
| `tag`         | No       | Optional tag for grouping                        |
| `requestInfo` | No       | DIGIT request info JSON string                   |

**Response:** `201 Created`

```json
{
  "fileStoreId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "tenantId": "pb.amritsar",
  "contentType": "video/mp4",
  "fileSize": "5242880",
  "module": "PGR",
  "tag": "complaint-video"
}
```

**Behaviour:**
1. Validates the file extension is in the allowed audio/video list.
2. Validates declared content-type is `audio/*` or `video/*`.
3. Validates binary content via Apache Tika.
4. Uploads to cloud storage and persists metadata in `eg_filestoremap`.

**Allowed extensions:** `mp4, mp3, wav, ogg, webm, avi, mov, aac, flac, mkv, m4a, m4v`

**Example:**
```bash
curl -X POST "http://localhost:8083/filestore/v1/files/external-media" \
  -F "file=@evidence.mp4" \
  -F "tenantId=pb.amritsar" \
  -F "module=PGR" \
  -F "tag=complaint-video"
```

---

### GET /v1/files/id — Download file by fileStoreId

| Parameter     | Required | Description              |
|---------------|----------|--------------------------|
| `tenantId`    | Yes      | Tenant identifier        |
| `fileStoreId` | Yes      | UUID returned on upload  |

**Response:** File binary stream with appropriate `Content-Type` and `Content-Disposition` headers.

---

### GET /v1/files/metadata — Get file metadata

| Parameter     | Required | Description              |
|---------------|----------|--------------------------|
| `tenantId`    | Yes      | Tenant identifier        |
| `fileStoreId` | Yes      | UUID returned on upload  |

**Response:** `200 OK` with file metadata (content-type, fileName, tenantId) without the binary content.

---

### GET /v1/files/tag — Get files by tag

| Parameter  | Required | Description       |
|------------|----------|-------------------|
| `tenantId` | Yes      | Tenant identifier |
| `tag`      | Yes      | Tag to search     |

**Response:** `200 OK` with a list of matching file records.

---

### GET /v1/files/url — Get presigned URLs by fileStoreId

| Parameter      | Required | Description                              |
|----------------|----------|------------------------------------------|
| `tenantId`     | Yes      | Tenant identifier                        |
| `fileStoreIds` | Yes      | One or more fileStoreId values (repeat param) |

**Response:** `200 OK` with a map of `fileStoreId → presigned URL`.

- For regular files and media: one presigned URL per fileStoreId.
- For images: four comma-separated presigned URLs (original + three thumbnail sizes).

**Example:**
```bash
curl "http://localhost:8083/filestore/v1/files/url?tenantId=pb.amritsar&fileStoreIds=<uuid>"
```

---

## Configuration

Key properties in `application.properties`:

| Property | Description |
|----------|-------------|
| `isS3Enabled` | Enable Minio/S3 backend |
| `isAzureStorageEnabled` | Enable Azure Blob backend |
| `fixed.bucketname` | S3/Minio bucket name |
| `allowed.formats.map` | Extension → MIME type map for multipart uploads |
| `allowed.audio.video.formats` | Allowed extensions for media upload API |
| `media.upload.max.size.bytes` | Max size for direct media uploads (default 100 MB) |
| `spring.servlet.multipart.max-file-size` | Spring multipart limit (set to 100MB for video) |
| `presigned.url.expiry.time.in.secs` | Presigned URL TTL (default 86400 s) |

## Kafka Consumers

N/A

## Kafka Producers

N/A
