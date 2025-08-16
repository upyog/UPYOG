package org.egov.common.entity.edcr;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark fields for inclusion in EDCR report mapping.
 * Fields annotated with this will be extracted and mapped to report details.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ReportScrutinyField {
    String value();
}
