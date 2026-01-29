package org.egov.filestore.domain.service;

import org.egov.filestore.domain.model.BlobDirectoryCountResponse;

public interface BlobDirectoryService {

    BlobDirectoryCountResponse countFilesAndDirectories(
            String containerName,
            String path
    );
}
