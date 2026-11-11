package org.upyog.wms.constants;

import java.util.Arrays;
import java.util.Optional;

/** Fund source of a plan / component and fund type of a funding row. PARTIAL is only valid as a source. */
public enum FundType {
    GOVT_GRANT,
    OSR,
    PARTIAL;

    public static Optional<FundType> from(String value) {
        return Arrays.stream(values()).filter(f -> f.name().equalsIgnoreCase(value)).findFirst();
    }

    /** Is a funding row of type {@code rowType} allowed under this fund source? */
    public boolean allowsRow(FundType rowType) {
        return switch (this) {
            case GOVT_GRANT -> rowType == GOVT_GRANT;
            case OSR -> rowType == OSR;
            case PARTIAL -> rowType == GOVT_GRANT || rowType == OSR;
        };
    }
}
