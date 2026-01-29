package org.egov.filestore.web.controller;

import org.egov.filestore.domain.model.BlobDirectoryCountResponse;
import org.egov.filestore.domain.service.BlobDirectoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlobDirectoryController {

    private final BlobDirectoryService blobDirectoryService;

    public BlobDirectoryController(BlobDirectoryService blobDirectoryService) {
        this.blobDirectoryService = blobDirectoryService;
    }

    @GetMapping("/azure/blob/count")
    public BlobDirectoryCountResponse count(
            @RequestParam String container,
            @RequestParam(required = false) String path
    ) {
        return blobDirectoryService.countFilesAndDirectories(container, path);
    }
}
