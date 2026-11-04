import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";

import { Loader } from "@nudmcdgnpm/digit-ui-react-components";

import ActionModal from "./Modal";

import { useParams,  } from "react-router-dom";
import ApplicationDetailsContent from "./components/ApplicationDetailsContent";
import ApplicationDetailsToast from "./components/ApplicationDetailsToast";
import ApplicationDetailsActionBar from "./components/ApplicationDetailsActionBar";
import ApplicationDetailsWarningPopup from "./components/ApplicationDetailsWarningPopup";

const ApplicationDetails = (props) => {
    const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
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
          navigate(
            action?.redirectionUrll?.pathname,
            {
              state: {
                data: action?.redirectionUrll?.state,
                url: `${location?.pathname}${location.search}`,
              },
            }
          );
        }
        else if (action?.redirectionUrll?.action === "RE-SUBMIT-APPLICATION"){
          navigate(
            action?.redirectionUrll?.pathname,
            {
              state: action?.redirectionUrll?.state,
            }
          );
        }
        else {
          window.location.assign(`${window.location.origin}/upyog-ui/employee/payment/collect/${action?.redirectionUrll?.pathname}`);
        }
      } else if (!action?.redirectionUrl) {
        setShowModal(true);
      } else {
        navigate(action.redirectionUrl?.pathname, {
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

  const submitAction = async (data, nocData = false, isOBPS = {}) => {
    // Check if comment field exists in workflow and validate it
    // const workflow = data?.Property?.workflow || data?.Licenses?.[0]?.workflow || data?.WaterConnection?.workflow || data?.SewerageConnection?.workflow || data?.BPA?.workflow;
    
    // console.log('submitAction data:', data);
    // console.log('workflow:', workflow);
    // console.log('workflow.comment:', workflow?.comment);
    // console.log('hasOwnProperty comment:', workflow?.hasOwnProperty('comment'));
    
    // if(workflow && workflow.hasOwnProperty('comment') && (!workflow.comment || workflow.comment?.trim()?.length === 0))
    // {
    //  alert("Please fill in the comments before submitting")
    //  return;
    // }
    
    setIsEnableLoader(true);
    if (typeof data?.customFunctionToExecute === "function") {
      data?.customFunctionToExecute({ ...data });
    }
    if (nocData !== false && nocMutation) {
      const nocPrmomises = nocData?.map((noc) => {
        return nocMutation?.mutateAsync(noc);
      });
      try {
        setIsEnableLoader(true);
        const values = await Promise.all(nocPrmomises);
        values &&
          values.map((ob) => {
            Digit.SessionStorage.del(ob?.Noc?.[0]?.nocType);
          });
      } catch (err) {
        setIsEnableLoader(false);
        let errorValue = err?.response?.data?.Errors?.[0]?.code
          ? t(err?.response?.data?.Errors?.[0]?.code)
          : err?.response?.data?.Errors?.[0]?.message || err;
        closeModal();
        setShowToast({ key: "error", error: { message: errorValue } });
        setTimeout(closeToast, 5000);
        return;
      }
    }
    sessionStorage.setItem("updateData",JSON.stringify(data))
    if (mutate) {
      setIsEnableLoader(true);
      mutate(data, {
        onError: (error, variables) => {
          setIsEnableLoader(false);
          setShowToast({ key: "error", error });
          setTimeout(closeToast, 5000);
        },
        onSuccess: (data, variables) => {
          sessionStorage.removeItem("WS_SESSION_APPLICATION_DETAILS");
          setIsEnableLoader(false);
          if (isOBPS?.bpa) {
           // data.selectedAction = selectedAction;
            navigate(`/upyog-ui/employee/obps/response`, { replace: true, state: { data: data } });
          }
          if (isOBPS?.isStakeholder) {
           // data.selectedAction = selectedAction;
            navigate(`/upyog-ui/employee/obps/stakeholder-response`, { state: { data: data } });
          }
          if (isOBPS?.isNoc) {
            navigate(`/upyog-ui/employee/noc/response`, { state: { data: data } });
          }
          if (data?.Amendments?.length > 0) {
            //RAIN-6981 instead just show a toast here with appropriate message
            //show toast here and return 
            //navigate("/upyog-ui/employee/ws/response-bill-amend", { status: true, state: data?.Amendments?.[0] })

            if (variables?.AmendmentUpdate?.workflow?.action.includes("SEND_BACK")) {
              setShowToast({ key: "success", label: t("ES_MODIFYSWCONNECTION_SEND_BACK_UPDATE_SUCCESS") })
            } else if (variables?.AmendmentUpdate?.workflow?.action.includes("RE-SUBMIT")) {
              setShowToast({ key: "success", label: t("ES_MODIFYSWCONNECTION_RE_SUBMIT_UPDATE_SUCCESS") })
            } else if (variables?.AmendmentUpdate?.workflow?.action.includes("APPROVE")) {
              setShowToast({ key: "success", label: t("ES_MODIFYSWCONNECTION_APPROVE_UPDATE_SUCCESS") })
            }
            else if (variables?.AmendmentUpdate?.workflow?.action.includes("REJECT")) {
              setShowToast({ key: "success", label: t("ES_MODIFYWSCONNECTION_REJECT_UPDATE_SUCCESS") })
            }
            return
          }
          setShowToast({ key: "success", action: selectedAction });
          clearDataDetails && setTimeout(clearDataDetails, 3000);
          setTimeout(closeToast, 5000);
          queryClient.clear();
          queryClient.refetchQueries("APPLICATION_SEARCH");
          //push false status when reject

        },
      });
    }
    closeModal();
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
          {isWarningPop ? (
            <ApplicationDetailsWarningPopup
              action={selectedAction}
              workflowDetails={workflowDetails}
              businessService={businessService}
              isWarningPop={isWarningPop}
              closeWarningPopup={closeWarningPopup}
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
