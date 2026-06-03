/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.filestore.service.impl;

import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import static java.io.File.separator;
import static java.util.UUID.randomUUID;
import static org.apache.commons.io.FileUtils.getUserDirectoryPath;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.egov.infra.config.core.ApplicationThreadLocals.getCityCode;
import static org.egov.infra.utils.StringUtils.normalizeString;
import static org.slf4j.LoggerFactory.getLogger;

@Component("localDiskFileStoreService")
public class LocalDiskFileStoreService implements FileStoreService {

    private static final Logger LOG = getLogger(LocalDiskFileStoreService.class);

    private String fileStoreBaseDir;

    @Autowired
    public LocalDiskFileStoreService(@Value("${filestore.base.dir}") String fileStoreBaseDir) {
        if (fileStoreBaseDir.isEmpty())
            this.fileStoreBaseDir = getUserDirectoryPath() + separator + "egovfilestore";
        else
            this.fileStoreBaseDir = fileStoreBaseDir;
    }

    @Override
    public FileStoreMapper store(File sourceFile, String fileName, String mimeType, String moduleName) {
        return store(sourceFile, fileName, mimeType, moduleName, true);
    }
    
    @Override
    public FileStoreMapper store(File sourceFile, String fileName, String mimeType, String moduleName, String tenantId) {
        return store(sourceFile, fileName, mimeType, moduleName, true, tenantId);
    }

    @Override
    public FileStoreMapper store(InputStream sourceFileStream, String fileName, String mimeType, String moduleName) {
        return store(sourceFileStream, fileName, mimeType, moduleName, true);
    }

//    @Override
//    public FileStoreMapper store(File file, String fileName, String mimeType, String moduleName, boolean deleteFile) {
//        try {
//            fileName = normalizeString(fileName);
//            moduleName = normalizeString(moduleName);
//            FileStoreMapper fileMapper = new FileStoreMapper(randomUUID().toString(),
//                    defaultString(fileName, file.getName()));
//            Path newFilePath = this.createNewFilePath(fileMapper, moduleName);
//            Files.copy(file.toPath(), newFilePath);
//            fileMapper.setContentType(mimeType);
//            fileMapper.setTenantId(ApplicationThreadLocals.getFilestoreTenantID());
//            if (deleteFile && file.delete())
//                LOG.info("File store source file deleted");
//            return fileMapper;
//        } catch (IOException e) {
//            LOG.error(String.format("Error occurred while storing files at %s/%s/%s", this.fileStoreBaseDir, getCityCode(),
//                    moduleName), e);
//        }
//        return null;
//    }
    @Override
    public FileStoreMapper store(File file, String fileName, String mimeType, String moduleName, boolean deleteFile) {
        long startTime = System.currentTimeMillis();
        fileName = normalizeString(fileName);
        moduleName = normalizeString(moduleName);

        FileStoreMapper fileMapper = new FileStoreMapper(randomUUID().toString(),
                defaultString(fileName, file.getName()));
        Path newFilePath = null;

        try {
            newFilePath = this.createNewFilePath(fileMapper, moduleName);

            try (InputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()), 8192);
                 OutputStream out = new BufferedOutputStream(Files.newOutputStream(newFilePath), 8192)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            fileMapper.setContentType(mimeType);
//            fileMapper.setTenantId(ApplicationThreadLocals.getFilestoreTenantID());
            fileMapper.setTenantId(ApplicationThreadLocals.getFullTenantID());

            if (deleteFile) {
                boolean deleted = safeDeleteWithRetry(file.toPath(), 3, 200);
                if (deleted)
                    LOG.debug("✅ Deleted source file '{}' after storing.", file.getAbsolutePath());
                else
                    LOG.warn("⚠️ Could not delete source file '{}' after retries.", file.getAbsolutePath());
            }

            long elapsed = System.currentTimeMillis() - startTime;
            LOG.info("✅ Stored file '{}' in '{}' ({} bytes, {} ms)", fileName, newFilePath, file.length(), elapsed);

            return fileMapper;

        } catch (IOException e) {
            LOG.error("I/O error storing file '{}': {}", fileName, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            LOG.error("Unexpected error storing file '{}': {}", fileName, e.getMessage(), e);
            return null;
        }
    }
    
    
    public FileStoreMapper store(File file, String fileName, String mimeType, String moduleName, boolean deleteFile,
    		String tenantId) {
        long startTime = System.currentTimeMillis();
        fileName = normalizeString(fileName);
        moduleName = normalizeString(moduleName);

        FileStoreMapper fileMapper = new FileStoreMapper(randomUUID().toString(),
                defaultString(fileName, file.getName()));
        Path newFilePath = null;

        try {
            newFilePath = this.createNewFilePath(fileMapper, moduleName);

            try (InputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()), 8192);
                 OutputStream out = new BufferedOutputStream(Files.newOutputStream(newFilePath), 8192)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            fileMapper.setContentType(mimeType);
            fileMapper.setTenantId(tenantId);

            if (deleteFile) {
                boolean deleted = safeDeleteWithRetry(file.toPath(), 3, 200);
                if (deleted)
                    LOG.debug("✅ Deleted source file '{}' after storing.", file.getAbsolutePath());
                else
                    LOG.warn("⚠️ Could not delete source file '{}' after retries.", file.getAbsolutePath());
            }

            long elapsed = System.currentTimeMillis() - startTime;
            LOG.info("✅ Stored file '{}' in '{}' ({} bytes, {} ms)", fileName, newFilePath, file.length(), elapsed);

            return fileMapper;

        } catch (IOException e) {
            LOG.error("I/O error storing file '{}': {}", fileName, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            LOG.error("Unexpected error storing file '{}': {}", fileName, e.getMessage(), e);
            return null;
        }
    }


    /**
     * Attempts to delete a file with retries (for transient file locks, e.g. on Windows).
     */
    private boolean safeDeleteWithRetry(Path path, int maxRetries, long sleepMillis) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (Files.deleteIfExists(path)) {
                    return true;
                }
            } catch (FileSystemException fse) {
                // Windows-specific: file in use
                LOG.debug("Attempt {} to delete '{}' failed - file may be in use: {}", attempt, path, fse.getMessage());
            } catch (Exception e) {
                LOG.debug("Attempt {} to delete '{}' failed: {}", attempt, path, e.getMessage());
            }

            // Wait before retrying (to allow OS to release handle)
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return false;
    }




    @Override
    public FileStoreMapper store(InputStream fileStream, String fileName, String mimeType, String moduleName,
            boolean closeStream) {
        return storeCommon(fileStream, fileName, mimeType, moduleName, closeStream);
    }

	private FileStoreMapper storeCommon(InputStream fileStream, String fileName, String mimeType, String moduleName,
			boolean closeStream) {
		try {
            fileName = normalizeString(fileName);
            moduleName = normalizeString(moduleName);
            FileStoreMapper fileMapper = new FileStoreMapper(randomUUID().toString(), fileName);
            Path newFilePath = this.createNewFilePath(fileMapper, moduleName);
            Files.copy(fileStream, newFilePath);
            fileMapper.setContentType(mimeType);
            fileMapper.setTenantId(ApplicationThreadLocals.getFilestoreTenantID());
            if (closeStream)
                fileStream.close();
            return fileMapper;
        } catch (IOException e) {
            LOG.error(String.format("Error occurred while storing files at %s/%s/%s", this.fileStoreBaseDir, getCityCode(), moduleName), e);
        }
        return null;
	}

    @Override
    public File fetch(FileStoreMapper fileMapper, String moduleName) {
        return this.fetch(fileMapper.getFileStoreId(), moduleName);
    }

    @Override
    public Set<File> fetchAll(Set<FileStoreMapper> fileMappers, String moduleName) {
        return fileMappers.stream().map(fileMapper -> this.fetch(fileMapper.getFileStoreId(), moduleName))
                .collect(Collectors.toSet());
    }

    @Override
    public File fetch(String fileStoreId, String moduleName) {
        return fetchAsPath(fileStoreId, moduleName).toFile();
    }

    @Override
    public Path fetchAsPath(String fileStoreId, String moduleName) {
        fileStoreId = normalizeString(fileStoreId);
        moduleName = normalizeString(moduleName);
        Path fileDirPath = this.getFileDirectoryPath(moduleName);
        if (!fileDirPath.toFile().exists())
            throw new ApplicationRuntimeException(String.format("File Store does not exist at Path : %s/%s/%s",
                    this.fileStoreBaseDir, getCityCode(), moduleName));
        return this.getFilePath(fileDirPath, fileStoreId);
    }

    @Override
    public void delete(String fileStoreId, String moduleName) {
        fileStoreId = normalizeString(fileStoreId);
        moduleName = normalizeString(moduleName);
        Path fileDirPath = this.getFileDirectoryPath(moduleName);
        if (!fileDirPath.toFile().exists()) {
            Path filePath = this.getFilePath(fileDirPath, fileStoreId);
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new ApplicationRuntimeException(String.format("Could not remove document %s", filePath.getFileName()), e);
            }
        }
    }

    private Path createNewFilePath(FileStoreMapper fileMapper, String moduleName) throws IOException {
        Path fileDirPath = this.getFileDirectoryPath(moduleName);
        if (!fileDirPath.toFile().exists()) {
            LOG.info("File Store Directory {}/{}/{} not found, creating one", this.fileStoreBaseDir, getCityCode(),
                    moduleName);
            Files.createDirectories(fileDirPath);
            LOG.info("Created File Store Directory {}/{}/{}", this.fileStoreBaseDir, getCityCode(), moduleName);
        }
        return this.getFilePath(fileDirPath, fileMapper.getFileStoreId());
    }

    private Path getFileDirectoryPath(String moduleName) {
        return Paths.get(new StringBuilder().append(this.fileStoreBaseDir).append(separator).append(getCityCode())
                .append(separator).append(moduleName).toString());
    }

    private Path getFilePath(Path fileDirPath, String fileStoreId) {
        return Paths.get(fileDirPath + separator + fileStoreId);
    }

	@Override
	public FileStoreMapper store(InputStream fileStream, String fileName, String mimeType, String moduleName,
			String tenantId) {
		return null;
	}

	@Override
	public FileStoreMapper store(InputStream fileStream, String fileName, String mimeType, String moduleName,
			String tenantId, boolean closeStream) {
		return storeCommon(fileStream, fileName, mimeType, moduleName, closeStream);
	}

	@Override
	public File fetch(String fileStoreId, String moduleName, String tenantId) {
		return fetchAsPath(fileStoreId, moduleName).toFile();
	}
}