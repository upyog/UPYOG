package org.upyog.dashboard.api;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.loader.DashboardDataLoader;
import org.upyog.dashboard.loader.DashboardDataLoaderFactory;
import org.upyog.dashboard.model.DashboardRequest;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.registry.TransformerRegistry;
import org.upyog.dashboard.transformer.ModuleTransformer;
import org.upyog.dashboard.validator.CommonValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Default Spring-managed implementation of {@link DashboardClient}.
 *
 * <p>This class wires together the core stages of the ingestion pipeline:
 * <ol>
 *   <li><b>Transformation:</b> maps the raw module-specific request into a
 *       canonical {@link DashboardPayload} using the appropriate
 *       {@link ModuleTransformer} retrieved from {@link TransformerRegistry}.</li>
 *   <li><b>Validation:</b> enforces system-wide mandatory fields through
 *       {@link CommonValidator}.</li>
 *   <li><b>Delivery:</b> delegates payload delivery to the appropriate {@link DashboardDataLoader}
 *       resolved by {@link DashboardDataLoaderFactory} based on runtime configuration.</li>
 * </ol>
 *
 * <p>Adheres to SOLID principles by delegating transport-specific concerns (HTTP REST vs S3 Excel)
 * to dedicated loader strategy beans.
 *
 * @see DashboardClient
 * @see TransformerRegistry
 * @see CommonValidator
 * @see DashboardDataLoaderFactory
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardClientImpl implements DashboardClient {

    /**
     * Registry that maps each {@link org.upyog.dashboard.common.constants.Module}
     * to its concrete {@link ModuleTransformer} implementation.
     */
    private final TransformerRegistry registry;

    /**
     * Factory responsible for resolving the appropriate {@link DashboardDataLoader} strategy.
     */
    private final DashboardDataLoaderFactory dataLoaderFactory;

    /**
     * Validator that enforces mandatory cross-module fields on every payload
     * before it is handed to the loader.
     *
     * @see CommonValidator#validate(DashboardPayload)
     */
    private final CommonValidator commonValidator;

    /**
     * Executes the full ingestion pipeline for the given {@code request}.
     *
     * <p>Execution steps:
     * <ol>
     *   <li>Looks up the {@link ModuleTransformer} for {@code request.getModule()}
     *       via the {@link TransformerRegistry}.</li>
     *   <li>Calls {@link ModuleTransformer#transform(Object)} with
     *       {@code request.getRawData()} to produce a {@link DashboardPayload}.</li>
     *   <li>Calls {@link CommonValidator#validate(DashboardPayload)} to assert
     *       that mandatory fields are present and non-empty.</li>
     *   <li>Resolves the appropriate {@link DashboardDataLoader} from {@link DashboardDataLoaderFactory}
     *       and delegates delivery.</li>
     * </ol>
     *
     * @param request the ingestion request; must not be {@code null}; must have
     *                a non-{@code null} {@code module} that has a registered transformer
     * @return the outcome of the loader call; never {@code null}
     * @throws org.upyog.dashboard.exception.ValidationException if
     *         {@link CommonValidator#validate} finds a missing or invalid field
     * @throws IllegalArgumentException if no transformer is registered for the
     *         requested module
     */
    @Override
    public IngestionResult execute(DashboardRequest request) {
        log.debug("Executing ingestion pipeline for module: {}", request.getModule());

        ModuleTransformer<Object> transformer = registry.get(request.getModule());
        DashboardPayload payload = transformer.transform(request.getRawData());

        commonValidator.validate(payload);

        DashboardDataLoader loader = dataLoaderFactory.getDailyDataLoader();
        return loader.load(payload);
    }
}
