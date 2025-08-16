package org.egov.edcr.service;

import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.ReportScrutinyField;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for handling feature-related operations in EDCR (Electronic Development Control Regulations) system.
 * Provides methods for mapping report details and adding scrutiny details to plans.
 */
@Component
public class FeatureUtil {
    private static final Logger log = LoggerFactory.getLogger(FeatureUtil.class);

    /**
     * Maps object fields annotated with @ReportScrutinyField to a key-value map.
     * Uses reflection to extract field values and their corresponding annotation values.
     *
     * @param obj The object whose fields need to be mapped
     * @return Map<String, String> containing annotation values as keys and field values as values
     */
    public static Map<String, String> mapReportDetails(Object obj) {
        Map<String, String> result = new LinkedHashMap<>();
        log.info("Mapping Report Details...");

        if (obj == null) return result;

        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                ReportScrutinyField annotation = field.getAnnotation(ReportScrutinyField.class);
                Object value = field.get(obj);
                if (annotation != null && value != null) {
                    result.put(annotation.value(), String.valueOf(value));
                }
            } catch (IllegalAccessException error) {
                log.error("Key or Value not found for Mapping: ", error);
            }
        }
        return result;
    }

    /**
     * Adds scrutiny details to a plan's report output.
     * Appends the provided details map to the scrutiny detail and adds the scrutiny detail to the plan.
     *
     * @param scrutinyDetail The scrutiny detail object to be updated
     * @param plan The plan object where scrutiny details will be added
     * @param details Map containing the details to be added to scrutiny
     */
    public static void addScrutinyDetailtoPlan(ScrutinyDetail scrutinyDetail, Plan plan, Map<String, String> details){
        log.info("Adding Scrutiny Details to Plan...");
        scrutinyDetail.getDetail().add(details);
        plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

}
