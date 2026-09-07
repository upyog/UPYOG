package org.upyog.dashboard.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.exception.ValidationException;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;

/**
 * Cross-module validator that enforces mandatory field rules on every
 * {@link DashboardPayload} before it is handed to the
 * {@link org.upyog.dashboard.loader.Loader}.
 *
 * <p>
 * This validator runs for <em>all</em> modules and checks fields that are
 * common across the entire adapter pipeline (non-null payload, non-empty data
 * dataList, and the presence of each contextual field on the first data
 * record). Module-specific metric validation is handled separately by the
 * relevant {@link ModuleValidator} implementation.
 *
 * <h3>Validation rules (in order)</h3>
 * <ol>
 * <li>The {@code payload} itself must not be {@code null}.</li>
 * <li>{@link DashboardPayload#getData()} must not be {@code null} or
 * empty.</li>
 * <li>The first {@link DashboardData} element must have a non-null and
 * non-empty {@code module} field.</li>
 * <li>The first element must have a non-null and non-empty {@code state}
 * field.</li>
 * <li>The first element must have a non-null {@code metrics} dataMap.</li>
 * <li>The first element must have a non-null and non-empty {@code ward}
 * field.</li>
 * <li>The first element must have a non-null and non-empty {@code region}
 * field.</li>
 * <li>The first element must have a non-null and non-empty {@code ulb}
 * field.</li>
 * </ol>
 *
 * @see ModuleValidator
 * @see org.upyog.dashboard.pt.validation.impl.PTValidator
 * @see ValidationException
 */
/**
 * Class representing the CommonValidator class.
 *
 * <p>
 * Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Component
public class CommonValidator {

    /**
     * Validates common mandatory fields on the first record of {@code payload}.
     *
     * <p>
     * Fails fast: throws on the first validation failure encountered. All
     * checks apply only to the first element of the data dataList; callers that
     * supply multi-record payloads should be aware that records after index 0
     * are not validated here.
     *
     * @param payload the transformed dashboard payload to validate; must not be
     * {@code null}
     * @throws ValidationException if any of the following are true:
     * <ul>
     * <li>{@code payload} is {@code null}</li>
     * <li>{@code payload.getData()} is {@code null} or empty</li>
     * <li>{@code module}, {@code state}, {@code ward}, {@code region}, or
     * {@code ulb} of the first data record is {@code null}</li>
     * <li>{@code metrics} of the first data record is {@code null}</li>
     * </ul>
     */
    public void validate(DashboardPayload payload) {

        if (payload == null) {
            throw new ValidationException("Payload cannot be null");
        }

        if (payload.getData() == null || payload.getData().isEmpty()) {
            throw new ValidationException("Data cannot be empty");
        }

        DashboardData data = payload.getData().get(0);

        if (StringUtils.isBlank(data.getModule())) {
            throw new ValidationException("Module is mandatory");
        }

        if (StringUtils.isBlank(data.getState())) {
            throw new ValidationException("State is mandatory");
        }

        if (data.getMetrics() == null) {
            throw new ValidationException("Metrics cannot be null");
        }

        if (StringUtils.isBlank(data.getWard())) {
            throw new ValidationException("Ward cannot be null");
        }

        if (StringUtils.isBlank(data.getRegion())) {
            throw new ValidationException("Region cannot be null");
        }

        if (StringUtils.isBlank(data.getUlb())) {
            throw new ValidationException("ULB cannot be null");
        }
    }
}
