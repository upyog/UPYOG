package org.upyog.wms.constants;

import java.util.Arrays;
import java.util.Optional;

/**
 * Actions accepted by the Action Plan APIs.
 * SAVE_DRAFT is handled inside the service only (no workflow transition); every other action is a
 * transition of the WMS_ACTION_PLAN business service.
 */
public enum ActionPlanAction {
    INITIATE(Kind.CREATE),
    SAVE_DRAFT(Kind.EDIT),
    SUBMIT(Kind.SUBMIT),
    SUBMIT_TO_APPROVER(Kind.SUBMIT),
    RESUBMIT(Kind.SUBMIT),
    RESUBMIT_TO_APPROVER(Kind.SUBMIT),
    VERIFY(Kind.DECISION),
    VERIFY_AND_APPROVE(Kind.DECISION),
    APPROVE(Kind.DECISION),
    RETURN(Kind.DECISION),
    REJECT(Kind.DECISION),
    WITHDRAW(Kind.WITHDRAW);

    public enum Kind { CREATE, EDIT, SUBMIT, DECISION, WITHDRAW }

    private final Kind kind;

    ActionPlanAction(Kind kind) {
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    /** Actions that may change plan data (everything else only moves the status). */
    public boolean modifiesData() {
        return kind == Kind.EDIT || kind == Kind.SUBMIT;
    }

    public boolean isSubmission() {
        return kind == Kind.SUBMIT;
    }

    public boolean isDecision() {
        return kind == Kind.DECISION;
    }

    /** Submission that routes the plan to the Verifier inbox. */
    public boolean routesToVerifier() {
        return this == SUBMIT || this == RESUBMIT;
    }

    /** Actions that end in final approval. */
    public boolean approves() {
        return this == APPROVE || this == VERIFY_AND_APPROVE;
    }

    public static Optional<ActionPlanAction> from(String value) {
        return Arrays.stream(values()).filter(a -> a.name().equalsIgnoreCase(value)).findFirst();
    }
}
