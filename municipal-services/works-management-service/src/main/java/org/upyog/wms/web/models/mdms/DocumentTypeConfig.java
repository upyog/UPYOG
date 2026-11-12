package org.upyog.wms.web.models.mdms;

/**
 * MDMS WorksManagement.DocumentType entry.
 * owner: ACTION_PLAN | RECOMMENDATION | COMPONENT. supporting = counts toward the plan document limit.
 */
public record DocumentTypeConfig(String code, String owner, boolean required, boolean supporting) {
}
