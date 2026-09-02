package org.upyog.dashboard.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import org.upyog.dashboard.config.DashboardProperties;

import jakarta.annotation.PostConstruct;
import java.io.File;
import org.upyog.dashboard.util.CommonUtils;

@Slf4j
@Component
public class S3UploadClient {

    private final DashboardProperties properties;
    private S3Client s3Client;

    public S3UploadClient(DashboardProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        if (properties.getAwsS3AccessKey() == null || properties.getAwsS3SecretKey() == null || properties.getAwsS3Region() == null) {
            log.warn("S3 credentials or region not fully configured. S3UploadClient will not be initialized.");
            return;
        }
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(properties.getAwsS3AccessKey(), properties.getAwsS3SecretKey());
            Region region = Region.of(properties.getAwsS3Region());
            this.s3Client = S3Client.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
            log.info("S3Client initialized successfully for region: {}", properties.getAwsS3Region());
        } catch (Exception e) {
            log.error("Failed to initialize S3Client", e);
        }
    }

    /**
     * Generates a standard S3 key using {@link CommonUtils#buildS3Key(String, String, String, String)}.
     */
    public String generateS3Key(String tenantId, String moduleName, String fileName) {
        return CommonUtils.buildS3Key(properties.getAwsS3Folder(), tenantId, moduleName, fileName);
    }

    public String uploadFile(File file, String tenantId, String moduleName) {
        if (s3Client == null) {
            log.error("S3Client is not initialized.");
            return null;
        }

        try {
            String key = generateS3Key(tenantId, moduleName, file.getName());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(properties.getAwsS3Bucket())
                    .key(key)
                    .build();

            log.info("Uploading file to S3. Bucket: {}, Key: {}", properties.getAwsS3Bucket(), key);
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
            log.info("File uploaded successfully to S3: {}", key);
            
            return key;
        } catch (S3Exception e) {
            log.error("Error uploading file to S3: {}", e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error uploading file to S3", e);
        }
        return null;
    }
}
