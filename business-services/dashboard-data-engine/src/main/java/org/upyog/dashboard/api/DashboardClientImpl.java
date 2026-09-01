package org.upyog.dashboard.api;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.loader.DashboardDataLoader;
import org.upyog.dashboard.model.DashboardRequest;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.registry.TransformerRegistry;
import org.upyog.dashboard.transformer.ModuleTransformer;
import org.upyog.dashboard.validator.CommonValidator;
import org.upyog.dashboard.service.SXSSFExcelGeneratorService;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.model.DashboardData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default Spring-managed implementation of {@link DashboardClient}.
 *
 * <p>
 * This class wires together the four main collaborators of the ingestion
 * pipeline and executes them in sequence for every incoming request:
 *
 * <ol>
 * <li><strong>TransformerRegistry</strong> — looks up the
 * {@link ModuleTransformer} registered for the requested module.</li>
 * <li><strong>ModuleTransformer</strong> — converts module-specific raw data
 * into a normalized {@link DashboardPayload}.</li>
 * <li><strong>CommonValidator</strong> — asserts that mandatory cross-module
 * fields (module, state, ULB, ward, region, metrics) are present.</li>
 * <li><strong>Loader</strong> — pushes the payload to the national dashboard
 * ingest endpoint and returns an {@link IngestionResult}.</li>
 * </ol>
 *
 * <h3>Commented-out code</h3>
 * The {@code ValidatorRegistry} injection and the module-specific
 * {@code validate()} call are temporarily commented out pending completion of
 * per-module validator implementations. They should be re-enabled once all
 * active modules have a corresponding
 * {@link org.upyog.dashboard.validator.ModuleValidator}.
 *
 * <h3>Dependencies</h3>
 * All dependencies are injected via constructor (Lombok
 * {@code @RequiredArgsConstructor}) to keep the bean immutable and easy to
 * test.
 *
 * @see DashboardClient
 * @see TransformerRegistry
 * @see CommonValidator
 * @see Loader
 */
/**
 * Class representing the DashboardClientImpl class.
 *
 * <p>
 * Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardClientImpl implements DashboardClient {

    private final S3UploadClient s3UploadClient;

    /**
     * Registry that maps each
     * {@link org.upyog.dashboard.common.constants.Module} to its concrete
     * {@link ModuleTransformer} implementation. Populated at startup by
     * scanning all {@code ModuleTransformer} beans.
     */
    private final TransformerRegistry registry;

    /**
     * Loader responsible for sending the transformed payload to the downstream
     * endpoint and publishing the audit record to Kafka. The active
     * implementation is
     * {@link org.upyog.dashboard.loader.impl.DashboardDataLoaderImpl}.
     */
    private final DashboardDataLoader loader;

    /**
     * Validator that enforces mandatory cross-module fields on every payload
     * before it is handed to the loader.
     *
     * @see CommonValidator#validate(DashboardPayload)
     */
    private final CommonValidator commonValidator;
    private final DashboardProperties properties;
    private final SXSSFExcelGeneratorService excelGeneratorService;
    // private final ValidatorRegistry validatorRegistry;
    // Un-comment once per-module validators are implemented for all active modules.

    /**
     * Executes the full ingestion pipeline for the given {@code request}.
     *
     * <p>
     * Execution steps:
     * <ol>
     * <li>Looks up the {@link ModuleTransformer} for
     * {@code request.getModule()} via the {@link TransformerRegistry}.</li>
     * <li>Calls {@link ModuleTransformer#transform(Object)} with
     * {@code request.getRawData()} to produce a {@link DashboardPayload}.</li>
     * <li>Calls {@link CommonValidator#validate(DashboardPayload)} to assert
     * that mandatory fields are present and non-empty.</li>
     * <li>Calls {@link Loader#load(DashboardPayload)} and returns the
     * result.</li>
     * </ol>
     *
     * @param request the ingestion request; must not be {@code null}; must have
     * a non-{@code null} {@code module} that has a registered transformer
     * @return the outcome of the loader call; never {@code null}
     * @throws org.upyog.dashboard.exception.ValidationException if
     * {@link CommonValidator#validate} finds a missing or invalid field
     * @throws IllegalArgumentException if no transformer is registered for the
     * requested module
     */
    @Override
    public IngestionResult execute(DashboardRequest request) {

        ModuleTransformer<Object> transformer = registry.get(request.getModule());

        DashboardPayload payload = transformer.transform(request.getRawData());

        commonValidator.validate(payload);

        String mode = properties.getEffectiveDailyUploadMode();
        if ("FILESTORE".equalsIgnoreCase(mode) || "S3".equalsIgnoreCase(mode)) {
            return processViaS3(payload, request.getModule().name());
        }

        return loader.load(payload);

    }

    private IngestionResult processViaS3(DashboardPayload payload, String moduleName) {
        log.info("Executing S3 routing for daily ingestion of module: {}", moduleName);
        File tempFile = null;
        try {
            List<Object> records = new ArrayList<>();
            for (DashboardData data : payload.getData()) {
                records.add(data);
            }

            tempFile = excelGeneratorService.generateExcelFile(moduleName, records);

            String tenantId = properties.getTenantId();
            if (payload.getData() != null && !payload.getData().isEmpty()) {
                String payloadUlb = payload.getData().get(0).getUlb();
                if (payloadUlb != null && payloadUlb.contains(".")) {
                    tenantId = payloadUlb.split("\\.")[0];
                } else if (payloadUlb != null) {
                    tenantId = payloadUlb;
                }
            }

            String fileStoreId = s3UploadClient.uploadFile(tempFile, tenantId, moduleName);

            if (fileStoreId != null) {
                return IngestionResult.builder()
                        .ingestionStatus("SUCCESS")
                        .responseData("{\"fileStoreId\": \"" + fileStoreId + "\"}")
                        .build();
            } else {
                return IngestionResult.builder()
                        .ingestionStatus("FAILURE")
                        .failureReason("Failed to upload Excel file to S3")
                        .build();
            }
        } catch (Exception e) {
            log.error("Failed to generate and upload Excel file for module {}", moduleName, e);
            return IngestionResult.builder()
                    .ingestionStatus("FAILURE")
                    .failureReason("Exception during S3 routing: " + e.getMessage())
                    .build();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

}
