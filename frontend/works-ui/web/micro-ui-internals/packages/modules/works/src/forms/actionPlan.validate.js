import { toPaise, fromPaise, todayInIndia, normalizeName } from "../form-builder/formEngine";

// TODO(integration): replace with a real API call that checks whether the Project ID is already registered
async function projectIdExists(projectId) {
    return false;
}

/**
 * values: the cleaned payload (hidden fields already removed).
 * t: translate function with {param} support.
 * Returns { errors, warnings }, each a { "dotted.path": "message" } map.
 */
export async function validateActionPlan(values, { hasFieldErrors, t }) {
    const errors = {};
    const warnings = {};
    const seenIds = new Set();
    let totalComponents = 0;

    (values.projects ?? []).forEach((project, i) => {
        // Duplicate Project ID inside this plan
        const id = (project.projectId ?? "").toUpperCase();
        if (id && seenIds.has(id)) errors[`projects.${i}.projectId`] = t("WORKS_AP_ERR_PROJECT_ID_REPEATED");
        seenIds.add(id);

        // Date ordering
        const { from, to } = project.timeRange ?? {};
        if (from && to && from > to) errors[`projects.${i}.timeRange`] = t("WORKS_AP_ERR_DATE_ORDER");

        // Declared vs actual component count (warning only)
        const declared = Number(project.declaredComponentCount);
        const actual = (project.components ?? []).length;
        if (declared && declared !== actual)
            warnings[`projects.${i}.declaredComponentCount`] = t("WORKS_AP_WARN_COMPONENT_COUNT", { declared, actual });

        const seenNames = new Set();
        (project.components ?? []).forEach((c, j) => {
            const at = `projects.${i}.components.${j}`;
            const entered = c.componentAmount !== undefined && c.componentAmount !== "";
            const amount = toPaise(c.componentAmount) || 0;
            const funded = (toPaise(c.grantAmount) || 0) + (toPaise(c.osrAmount) || 0); // hidden fields are already removed
            totalComponents += amount;

            if (entered && amount === 0) errors[`${at}.componentAmount`] = t("WORKS_AP_ERR_AMOUNT_ZERO");

            // Funding must add up to the component amount
            if (amount && funded !== amount)
                errors[`${at}.componentAmount`] = t("WORKS_AP_ERR_FUNDING_MISMATCH", {
                    funded: fromPaise(funded),
                    amount: fromPaise(amount),
                });

            // Partial funding needs both parts
            if (c.fundSource === "PARTIAL" && (!c.grantAmount || !c.osrAmount))
                errors[`${at}.fundSource`] = t("WORKS_AP_ERR_PARTIAL_BOTH");

            // Similar component name in the same project (flag, never merge)
            const key = normalizeName(c.componentName);
            if (key && seenNames.has(key)) warnings[`${at}.componentName`] = t("WORKS_AP_WARN_SIMILAR_NAME");
            seenNames.add(key);
        });
    });

    // Sum of ALL components of ALL projects must equal the approved amount
    const enteredApproved = values.approvedAmount !== undefined && values.approvedAmount !== "";
    const approved = toPaise(values.approvedAmount) || 0;
    if (enteredApproved && approved === 0) errors.approvedAmount = t("WORKS_AP_ERR_AMOUNT_ZERO");
    else if (approved && totalComponents !== approved)
        errors.approvedAmount = t("WORKS_AP_ERR_TOTAL_MISMATCH", {
            total: fromPaise(totalComponents),
            approved: fromPaise(approved),
            unallocated: fromPaise(approved - totalComponents),
        });

    // Approval date must not be in the future (India time)
    if (values.approvalDate && values.approvalDate > todayInIndia())
        errors.approvalDate = t("WORKS_AP_ERR_APPROVAL_FUTURE");

    // Server check, only when everything local passed
    if (!hasFieldErrors && Object.keys(errors).length === 0) {
        await Promise.all(
            (values.projects ?? []).map(async (p, i) => {
                if (await projectIdExists(p.projectId)) errors[`projects.${i}.projectId`] = t("WORKS_AP_ERR_PROJECT_ID_EXISTS");
            })
        );
    }

    return { errors, warnings };
}