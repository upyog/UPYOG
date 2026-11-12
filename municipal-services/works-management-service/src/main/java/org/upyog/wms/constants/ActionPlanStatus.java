package org.upyog.wms.constants;

import java.util.Arrays;
import java.util.Optional;

/** Application status. Mirrors the applicationStatus of the WMS_ACTION_PLAN workflow states. */
public enum ActionPlanStatus {
    INITIATED(true, false),
    PENDING_FOR_VERIFICATION(false, false),
    PENDING_FOR_APPROVAL(false, false),
    RETURNED(true, false),
    APPROVED(false, true),
    REJECTED(false, true),
    WITHDRAWN(false, true),
    SUPERSEDED(false, true);

    private final boolean editable;
    private final boolean terminal;

    ActionPlanStatus(boolean editable, boolean terminal) {
        this.editable = editable;
        this.terminal = terminal;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public static Optional<ActionPlanStatus> from(String value) {
        return Arrays.stream(values()).filter(s -> s.name().equalsIgnoreCase(value)).findFirst();
    }

    /** True when the status is a non-terminal (in-progress) one. Unknown statuses are treated as in-progress. */
    public static boolean isInProgress(String value) {
        return from(value).map(s -> !s.isTerminal()).orElse(true);
    }
}
