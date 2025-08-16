import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";

import { Loader } from "@nudmcdgnpm/digit-ui-react-components";

import ActionModal from "./Modal";

import { useHistory, useParams } from "react-router-dom";
import ApplicationDetailsContent from "./components/ApplicationDetailsContent";
import ApplicationDetailsToast from "./components/ApplicationDetailsToast";
import ApplicationDetailsActionBar from "./components/ApplicationDetailsActionBar";

//this page is responsible for showing an action menu for users to select actions (e.g., approve, reject, forward).


const ApplicationDetails = (props) => {
    const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  const history = useHistory();
  let { id: applicationNumber } = useParams();
  const [displayMenu, setDisplayMenu] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [isEnableLoader, setIsEnableLoader] = useState(false);
  const [isWarningPop, setWarningPopUp] = useState(false);

  const {
    applicationDetails,
    showToast,
    setShowToast,
    isLoading,
    isDataLoading,
    applicationData,
    mutate,
    nocMutation,
    workflowDetails,
    id,
    businessService,
    closeToast,
    moduleCode,
    timelineStatusPrefix,
    forcedActionPrefix,
    statusAttribute,
    ActionBarStyle,
    MenuStyle,
    paymentsList,
    showTimeLine = true,
    oldValue,
    isInfoLabel = false,
    clearDataDetails,
    isAction=false
  } = props;
  
  useEffect(() => {
    if (showToast) {
      workflowDetails.revalidate();
    }
  }, [showToast]);

  function onActionSelect(action) {
    sessionStorage.setItem("SELECTED_ACTION", action?.action);
    if (action) {
      if(action?.isToast){
        setShowToast({ key: "error", error: { message: action?.toastMessage } });
        setTimeout(closeToast, 5000);
      }
      else if (action?.isWarningPopUp) {
        setWarningPopUp(true);
      } else if (action?.redirectionUrll) {
        if (action?.redirectionUrll?.action === "ACTIVATE_CONNECTION") {
          // window.location.assign(`${window.location.origin}sv-ui/employee/ws/${action?.redirectionUrll?.pathname}`, { data: action?.redirectionUrll?.state });

          history.push(`${action?.redirectionUrll?.pathname}`, JSON.stringify({ data: action?.redirectionUrll?.state, url: `${location?.pathname}${location.search}` }));
        }
        else if (action?.redirectionUrll?.action === "RE-SUBMIT-APPLICATION"){
          history.push(`${action?.redirectionUrll?.pathname}`, { data: action?.redirectionUrll?.state });
        }
        else {
          window.location.assign(`${window.location.origin}/sv-ui/employee/payment/collect/${action?.redirectionUrll?.pathname}`);
        }
      } else if (!action?.redirectionUrl) {
        setShowModal(true);
      } else {
        history.push({
          pathname: action.redirectionUrl?.pathname,
          state: { ...action.redirectionUrl?.state },
        });
      }
    }
    setSelectedAction(action);
    setDisplayMenu(false);
  }

  const queryClient = useQueryClient();

  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  const closeWarningPopup = () => {
    setWarningPopUp(false);
  };

  const submitAction = async (data) => {
    {
      setIsEnableLoader(true);
      if (typeof data?.customFunctionToExecute === "function") {
        data?.customFunctionToExecute({ ...data });
      }
      if (mutate) {
        setIsEnableLoader(true);
        mutate(data, {
          onError: (error, variables) => {
            setIsEnableLoader(false);
            setShowToast({ key: "error", error });
            setTimeout(closeToast, 5000);
          },
          onSuccess: (data, variables) => {
            setIsEnableLoader(false);
            setShowToast({ key: "success", action: selectedAction });
            clearDataDetails && setTimeout(clearDataDetails, 3000);
            setTimeout(closeToast, 5000);
            queryClient.clear();
            queryClient.refetchQueries("APPLICATION_SEARCH");
  
          },
        });
      }
      closeModal();
    }
  
  };

  if (isLoading || isEnableLoader) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      {!isLoading ? (
        <React.Fragment>
          <ApplicationDetailsContent
            applicationDetails={applicationDetails}
            id={id}
            workflowDetails={workflowDetails}
            isDataLoading={isDataLoading}
            applicationData={applicationData}
            businessService={businessService}
            timelineStatusPrefix={timelineStatusPrefix}
            statusAttribute={statusAttribute}
            paymentsList={paymentsList}
            showTimeLine={showTimeLine}
            oldValue={oldValue}
            isInfoLabel={isInfoLabel}
          />
          {showModal ? (
            <ActionModal
              t={t}
              vending_Zone={props?.vending_Zone || []}
              UserVendingZone={props?.UserVendingZone || ""}
              UserVendingZoneCode={props?.UserVendingZoneCode || ""}
              action={selectedAction}
              tenantId={tenantId}
              state={state}
              id={applicationNumber}
              applicationDetails={applicationDetails}
              applicationData={applicationDetails?.applicationData}
              closeModal={closeModal}
              submitAction={submitAction}
              actionData={workflowDetails?.data?.timeline}
              businessService={businessService}
              workflowDetails={workflowDetails}
              moduleCode={moduleCode}
            />
          ) : null}
          <ApplicationDetailsToast t={t} showToast={showToast} closeToast={closeToast} businessService={businessService} />
          <ApplicationDetailsActionBar
            isAction={isAction} // isAction is added to enable or disable the action bar
            workflowDetails={workflowDetails}
            displayMenu={displayMenu}
            onActionSelect={onActionSelect}
            setDisplayMenu={setDisplayMenu}
            businessService={businessService}
            forcedActionPrefix={forcedActionPrefix}
            ActionBarStyle={ActionBarStyle}
            MenuStyle={MenuStyle}
            applicationDetails={applicationDetails}
          />
        </React.Fragment>
      ) : (
        <Loader />
      )}
    </React.Fragment>
  );
};

export default ApplicationDetails;
