package org.egov.filestore.domain.model;

public class BlobDirectoryCountResponse {

    private long fileCount;
    private long directoryCount;

    public BlobDirectoryCountResponse(long fileCount, long directoryCount) {
        this.fileCount = fileCount;
        this.directoryCount = directoryCount;
    }

    public long getFileCount() {
        return fileCount;
    }

    public long getDirectoryCount() {
        return directoryCount;
    }
}
