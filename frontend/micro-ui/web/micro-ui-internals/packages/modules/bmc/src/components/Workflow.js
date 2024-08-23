import { ActionBar, Loader, Menu, SubmitBar, Toast } from "@egovernments/digit-ui-react-components";
import React, { useCallback, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import WorkflowPopup from "./Workflowpopup";

const WorkflowActions = ({
    businessService,
    tenantId,
    applicationNo: propApplicationNo,
    forcedActionPrefix,
    ActionBarStyle = {},
    MenuStyle = {},
    applicationDetails,
    url,
    setStateChanged,
    moduleCode,
    editApplicationNumber,
    editCallback,
    callback
}) => {
    const history = useHistory();
    const { estimateNumber } = Digit.Hooks.useQueryParams();
    const { t } = useTranslation();

    const applicationNo = useMemo(() => propApplicationNo || estimateNumber, [propApplicationNo, estimateNumber]);

    const { mutate } = Digit.Hooks.useUpdateCustom(url);
    const [displayMenu, setDisplayMenu] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [selectedAction, setSelectedAction] = useState(null);
    const [isEnableLoader, setIsEnableLoader] = useState(false);
    const [showToast, setShowToast] = useState(null);
    const menuRef = useRef();

    const user = useMemo(() => Digit.UserService.getUser(), []);
    const userRoles = useMemo(() => user?.info?.roles?.map((e) => e.code), [user]);

    const workflowDetails = Digit.Hooks.useWorkflowDetailsV2({
        tenantId,
        id: applicationNo,
        moduleCode: businessService,
        config: { enabled: true, cacheTime: 0 },
    });

    const actions = useMemo(() => (
        workflowDetails?.data?.actionState?.nextActions?.filter((e) => (
            userRoles.some((role) => e.roles?.includes(role)) || !e.roles
        )) || workflowDetails?.data?.nextActions?.filter((e) => (
            userRoles.some((role) => e.roles?.includes(role)) || !e.roles
        ))
    ), [workflowDetails, userRoles]);

    const closeMenu = useCallback(() => setDisplayMenu(false), []);

    const closeToast = useCallback(() => {
        setTimeout(() => setShowToast(null), 5000);
    }, []);

    Digit.Hooks.useClickOutside(menuRef, closeMenu, displayMenu);

    const closeModal = useCallback(() => {
        setSelectedAction(null);
        setShowModal(false);
        setShowToast({ warning: true, label: `WF_ACTION_CANCELLED` });
        closeToast();
    }, [closeToast]);

    const onActionSelect = useCallback((action) => {
        console.log("Action selected:", action); // Debug log
        setDisplayMenu(false);
        setSelectedAction(action);

        const bsEstimate = Digit?.Customizations?.["commonUiConfig"]?.getBusinessService("estimate");
        const bsContract = Digit?.Customizations?.["commonUiConfig"]?.getBusinessService("contract");
        const bsAttendance = Digit?.Customizations?.["commonUiConfig"]?.getBusinessService("muster roll");
        const bsPurchaseBill = Digit?.Customizations?.["commonUiConfig"]?.getBusinessService("works.purchase");

        if (bsEstimate === businessService && action?.action === "RE-SUBMIT") {
            history.push(`/${window?.contextPath}/employee/estimate/create-estimate?tenantId=${tenantId}&projectNumber=${editApplicationNumber}&estimateNumber=${applicationDetails?.estimateNumber}&isEdit=true`);
            return;
        }

        if (bsContract === businessService && action?.action === "EDIT") {
            history.push(`/${window?.contextPath}/employee/contracts/create-contract?tenantId=${tenantId}&workOrderNumber=${applicationNo}`);
            return;
        }
        if (bsAttendance === businessService && action?.action === "RE-SUBMIT") {
            editCallback();
            return;
        }

        if (bsPurchaseBill === businessService && action?.action === "RE-SUBMIT") {
            history.push(`/${window?.contextPath}/employee/expenditure/create-purchase-bill?tenantId=${tenantId}&billNumber=${editApplicationNumber}`);
            return;
        }

        console.log("Setting showModal to true"); // Debug log
        setShowModal(true);
    }, [applicationNo, applicationDetails, businessService, editApplicationNumber, editCallback, history, tenantId]);

    const submitAction = useCallback((data, selectedAction) => {
        setShowModal(false);
        setIsEnableLoader(true);
        const mutateObj = { ...data };

        mutate(mutateObj, {
            onError: (error, variables) => {
                setIsEnableLoader(false);
                setShowToast({ error: true, label: Digit.Utils.locale.getTransformedLocale(`WF_UPDATE_ERROR_${businessService}_${selectedAction.action}`), isDleteBtn: true });
                callback?.onError?.();
            },
            onSuccess: (data, variables) => {
                setIsEnableLoader(false);
                setShowToast({ label: Digit.Utils.locale.getTransformedLocale(`WF_UPDATE_SUCCESS_${businessService}_${selectedAction.action}`) });
                callback?.onSuccess?.();
                workflowDetails.revalidate();
                setStateChanged(`WF_UPDATE_SUCCESS_${selectedAction.action}`); // Notify parent about state change
            },
        });
    }, [businessService, callback, mutate, setStateChanged, workflowDetails]);

    if (isEnableLoader) return <Loader />;

    console.log("Rendering WorkflowActions with showModal:", showModal); // Debug log

    return (
        <React.Fragment>
            {!workflowDetails?.isLoading && actions?.length > 0 && (
                <ActionBar style={{ ...ActionBarStyle }}>
                    {displayMenu ? (
                        <Menu
                            localeKeyPrefix={forcedActionPrefix || Digit.Utils.locale.getTransformedLocale(`WF_${businessService?.toUpperCase()}_ACTION`)}
                            options={actions}
                            optionKey={"action"}
                            t={t}
                            onSelect={onActionSelect}
                            style={MenuStyle}
                        />
                    ) : null}
                    <SubmitBar ref={menuRef} label={t("WORKS_ACTIONS")} onSubmit={() => setDisplayMenu(!displayMenu)} />
                </ActionBar>
            )}
            {showModal && (

                <WorkflowPopup
                    action={selectedAction}
                    tenantId={tenantId}
                    t={t}
                    closeModal={closeModal}
                    submitAction={submitAction}
                    businessService={businessService}
                    moduleCode={moduleCode}
                    applicationDetails={applicationDetails}
                />
            )}
            {showToast && (
                <Toast
                    error={showToast?.error}
                    warning={t(showToast?.warning)}
                    label={t(showToast?.label)}
                    onClose={() => setShowToast(null)}
                    isDleteBtn={showToast?.isDleteBtn}
                />
            )}
        </React.Fragment>
    );
};

export default WorkflowActions;
