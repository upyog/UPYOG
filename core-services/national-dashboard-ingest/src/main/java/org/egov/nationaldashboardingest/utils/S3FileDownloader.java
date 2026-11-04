package org.egov.nationaldashboardingest.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;

/**
 * Storage component for securely transferring dataset files from Amazon Simple Storage Service (S3) to local storage.
 * <p>
 * Key characteristics:
 * <ul>
 *   <li>Dynamically authenticates with AWS using static IAM credentials from {@link ApplicationProperties}.</li>
 *   <li>Streams S3 objects directly to local temporary files ({@code /tmp/legacy_file_<uuid>.xlsx}),
 *       ensuring minimal memory consumption during large binary downloads.</li>
 *   <li>Converts AWS SDK client exceptions into standardized eGov {@link CustomException} instances.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
public class S3FileDownloader {

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Initializes and returns an authenticated Amazon S3 client instance.
     * <p>
     * Builds standard {@link AmazonS3} client configured with AWS region and static credentials
     * sourced from {@link ApplicationProperties}.
     * </p>
     *
     * @return initialized {@link AmazonS3} client instance
     */
    private AmazonS3 getS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(
                applicationProperties.getAwsS3AccessKey(),
                applicationProperties.getAwsS3SecretKey()
        );
        return AmazonS3ClientBuilder.standard()
                .withRegion(applicationProperties.getAwsS3Region())
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    /**
     * Downloads an object from S3 to a unique temporary file on the local file system.
     * <p>
     * Generates a unique UUID-based filename in {@code /tmp}, fetches the S3 object stream,
     * writes it to the local file, and logs file size statistics.
     * </p>
     *
     * @param s3Key the full object key / storage path within the configured S3 bucket
     * @return {@link File} reference pointing to the downloaded local temporary file
     * @throws CustomException with code {@code EG_NDI_S3_DOWNLOAD_ERR} if S3 retrieval fails
     */
    public File downloadFileFromS3(String s3Key) {
        try {
            String bucketName = applicationProperties.getAwsS3Bucket();
            log.info("Downloading file from S3 bucket: {}, key: {}", bucketName, s3Key);

            AmazonS3 s3Client = getS3Client();

            String tempFileName = "data_file_" + UUID.randomUUID().toString() + ".xlsx";
            String tempDir = applicationProperties.getBulkIngestTempDir();
            File tempFile = new File(tempDir, tempFileName);

            s3Client.getObject(new GetObjectRequest(bucketName, s3Key), tempFile);

            log.info("Successfully downloaded S3 object to local temp file: {}, size: {} bytes",
                    tempFile.getAbsolutePath(), tempFile.length());

            return tempFile;
        } catch (Exception e) {
            log.error("Failed to download file from S3 for key: {}", s3Key, e);
            throw new CustomException("EG_NDI_S3_DOWNLOAD_ERR", "Failed to download file from S3: " + e.getMessage());
        }
    }
}
