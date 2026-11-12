package org.upyog.wms.web.models.mdms;

import java.util.Map;
import java.util.Set;

/**
 * Parsed, active-only master data of one state tenant. Immutable, so it is safe to cache and share
 * between request threads.
 */
public record MdmsData(
        Set<String> financialYears,
        Set<String> purposes,
        Set<String> typesOfWork,
        Set<String> workCategories,
        Set<String> recommendedBy,
        Map<String, Set<String>> schemes,
        Set<String> governmentGrants,
        Set<String> ownSourceRevenues,
        Map<String, Set<String>> componentCategories,
        Map<String, DocumentTypeConfig> documentTypes,
        Set<String> approvingDepartments,
        Set<String> states,
        Map<String, String> districts,
        ActionPlanConfig config) {
}
