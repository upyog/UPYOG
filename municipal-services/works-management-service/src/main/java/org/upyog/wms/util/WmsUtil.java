package org.upyog.wms.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;

/** Small null-safe helpers used across the service. */
public final class WmsUtil {

    private WmsUtil() {
    }

    /** Null-safe list: never returns null. */
    public static <T> List<T> nn(List<T> list) {
        return list == null ? List.of() : list;
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
