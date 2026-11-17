package org.upyog.wms.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.upyog.wms.web.models.ActionPlan;
import org.upyog.wms.web.models.Component;
import org.upyog.wms.web.models.ProjectDetail;

/** Small null-safe helpers used across the service. */
public final class WmsUtil {

    private WmsUtil() {
    }

    /** Null-safe list: never returns null. */
    public static <T> List<T> nn(List<T> list) {
        return list == null ? List.of() : list;
    }

    /** Components belong to the plan's single project-detail section. */
    public static List<Component> projectComponents(ActionPlan plan) {
        return nn(plan == null ? null : plan.getProjectDetails()).stream()
                .filter(Objects::nonNull)
                .map(ProjectDetail::getComponents)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(List::of);
    }

    /** The first active project-detail row; submission rules enforce that exactly one exists. */
    public static Optional<ProjectDetail> projectDetail(ActionPlan plan) {
        return nn(plan == null ? null : plan.getProjectDetails()).stream()
                .filter(Objects::nonNull)
                .filter(detail -> isLive(detail.getIsActive()))
                .findFirst();
    }

    public static String projectId(ActionPlan plan) {
        return projectDetail(plan).map(ProjectDetail::getProjectId).orElse(null);
    }

    public static String projectName(ActionPlan plan) {
        return projectDetail(plan).map(ProjectDetail::getProjectName).orElse(null);
    }

    /** Replaces the components of the plan's single project-detail section. */
    public static void setProjectComponents(ActionPlan plan, List<Component> components) {
        nn(plan == null ? null : plan.getProjectDetails()).stream()
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(project -> project.setComponents(components));
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isTrue(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    /** Row is live unless explicitly marked inactive. */
    public static boolean isLive(Boolean isActive) {
        return !Boolean.FALSE.equals(isActive);
    }

    public static <T> BigDecimal sum(Collection<T> items, Function<T, BigDecimal> amount) {
        return items.stream().map(amount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** "pg.citya" -> "pg"; "pg" -> "pg". */
    public static String stateLevelTenant(String tenantId) {
        if (tenantId == null) {
            return null;
        }
        int dot = tenantId.indexOf('.');
        return dot < 0 ? tenantId : tenantId.substring(0, dot);
    }

    public static String userUuid(RequestInfo requestInfo) {
        User user = requestInfo == null ? null : requestInfo.getUserInfo();
        return user == null ? null : user.getUuid();
    }
}
