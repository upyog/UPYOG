package org.egov.filestore.domain.service.impl;


import org.egov.filestore.domain.model.BlobDirectoryCountResponse;
import org.egov.filestore.domain.service.BlobDirectoryService;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.ListBlobsOptions;

@Service
public class BlobDirectoryServiceImpl implements BlobDirectoryService {

    private final BlobServiceClient blobServiceClient;

    public BlobDirectoryServiceImpl(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    @Override
    public BlobDirectoryCountResponse countFilesAndDirectories(String containerName, String path) {

        try {
            BlobContainerClient containerClient =
                    blobServiceClient.getBlobContainerClient(containerName);

            if (!containerClient.exists()) {
                throw new CustomException("Container does not exist: " + containerName, null);
            }

            String prefix = normalizePath(path);

            ListBlobsOptions options = new ListBlobsOptions()
                    .setPrefix(prefix);

            long fileCount = 0;
            long directoryCount = 0;

            PagedIterable<BlobItem> items =
                    containerClient.listBlobsByHierarchy("/", options, null);

            for (BlobItem item : items) {
                if (item.isPrefix()) {
                    directoryCount++;
                } else {
                    fileCount++;
                }
            }

            return new BlobDirectoryCountResponse(fileCount, directoryCount);

    
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return new BlobDirectoryCountResponse(0, 0);
    }

    private String normalizePath(String path) {
        if (path == null || path=="") {
            return "";
        }
        return path.endsWith("/") ? path : path + "/";
    }
}
