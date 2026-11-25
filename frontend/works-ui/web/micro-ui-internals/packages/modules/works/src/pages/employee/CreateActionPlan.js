import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import FormBuilder from "../../form-builder/FormBuilder";
import UnsavedPrompt from "../../form-builder/UnsavedPrompt";
import { makeTr, FB_MESSAGES } from "../../form-builder/formEngine";
import { loadOptions } from "../../masters/loadOptions";
import actionPlanForm from "../../forms/actionPlan.form.json";
import messages from "../../forms/actionPlan.messages.json";
import { validateActionPlan } from "../../forms/actionPlan.validate";
import { useCreateActionPlan } from "../../api/useCreateActionPlan";

export default function CreateActionPlan({ parentRoute = "/works-ui/employee/works" }) {
    const { t } = useTranslation();
    const navigate = Digit.Hooks.useCustomNavigate();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const tr = useMemo(() => makeTr(t, { ...FB_MESSAGES, ...messages }), [t]);

    const [dirty, setDirty] = useState(false);
    const [submittedPlan, setSubmittedPlan] = useState(null);
    const { mutateAsync: createPlan } = useCreateActionPlan();

    // Navigate in an effect so the leave-page guard is already disarmed when we leave
    useEffect(() => {
        if (submittedPlan) {
            const appNo = submittedPlan.applicationNumber || "";
            const target = appNo
                ? `${parentRoute}/acknowledgement/${encodeURIComponent(appNo)}`
                : `${parentRoute}/acknowledgement`;
            navigate(target, {
                replace: true,
                state: {
                    actionPlan: submittedPlan,
                    applicationNumber: appNo,
                    id: submittedPlan.id,
                },
            });
        }
    }, [submittedPlan]); // eslint-disable-line react-hooks/exhaustive-deps

    const handleSubmit = async (data) => {
        const plan = await createPlan({ ...data, tenantId }); // calls _create (INITIATE) then _update (SUBMIT)
        setDirty(false);
        setSubmittedPlan(plan);
    };

    const handleBack = () => {
        if (dirty && !window.confirm(tr("FB_UNSAVED"))) return;
        navigate(`${parentRoute}/inbox`);
    };

    const formattedUlb = useMemo(() => {
        if (!tenantId) return "—";
        const code = `TENANT_TENANTS_${tenantId.toUpperCase().replace(/\./g, "_")}`;
        const translated = t(code);
        if (translated && translated !== code) return translated;
        return tenantId.split(".").pop()?.toUpperCase() || tenantId;
    }, [tenantId, t]);

    return (
        <div className="cap-page">
            <UnsavedPrompt when={dirty} message={tr("FB_UNSAVED")} />

            {/* Top Breadcrumb & Context Bar */}
            <div className="cap-topbar">
                <div className="cap-topbar__left">
                    <button
                        type="button"
                        className="cap-back-btn"
                        onClick={handleBack}
                        aria-label={t("CS_COMMON_BACK", "Back")}
                    >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                            <line x1="19" y1="12" x2="5" y2="12" />
                            <polyline points="12 19 5 12 12 5" />
                        </svg>
                        <span>{t("CS_COMMON_BACK", "Back")}</span>
                    </button>

                    <nav className="cap-breadcrumbs" aria-label="Breadcrumb">
                        <Link to="/works-ui/employee" className="cap-breadcrumb-link">
                            {t("ES_COMMON_HOME", "Home")}
                        </Link>
                        <span className="cap-breadcrumb-sep" aria-hidden="true">/</span>
                        <Link to={`${parentRoute}/inbox`} className="cap-breadcrumb-link">
                            {t("ACTION_TEST_WORKS", "Works")}
                        </Link>
                        <span className="cap-breadcrumb-sep" aria-hidden="true">/</span>
                        <span className="cap-breadcrumb-current" aria-current="page">
                            {tr("WORKS_AP_TITLE")}
                        </span>
                    </nav>
                </div>

                <div className="cap-topbar__right">
                    <span className="cap-meta-pill" title={tenantId}>
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                            <path d="M3 21h18" />
                            <path d="M5 21V7l8-4v18" />
                            <path d="M19 21V11l-6-4" />
                        </svg>
                        <span>{tr("WORKS_AP_ULB")}: <strong>{formattedUlb}</strong></span>
                    </span>

                    <span className={`cap-status-pill ${dirty ? "is-dirty" : "is-clean"}`}>
                        <span className="cap-status-dot" aria-hidden="true" />
                        {dirty ? t("WORKS_AP_STATUS_UNSAVED", "Unsaved changes") : t("WORKS_AP_STATUS_NEW", "New Draft")}
                    </span>
                </div>
            </div>

            {/* Full-Width Form Builder */}
            <FormBuilder
                config={actionPlanForm}
                t={t}
                messages={messages}
                initialValues={{ ulb: tenantId }}
                loadOptions={loadOptions}
                validate={validateActionPlan}
                onSubmit={handleSubmit}
                onCancel={handleBack}
                onDirtyChange={setDirty}
            />
        </div>
    );
}
