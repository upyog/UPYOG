import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";

import { Loader,SubmitBar } from "@upyog/digit-ui-react-components";

import ActionModal from "./Modal";

import { useHistory, useParams } from "react-router-dom";
import ApplicationDetailsContent from "./components/ApplicationDetailsContent";
import ApplicationDetailsToast from "./components/ApplicationDetailsToast";
import ApplicationDetailsActionBar from "./components/ApplicationDetailsActionBar";
import ApplicationDetailsWarningPopup from "./components/ApplicationDetailsWarningPopup";

const ApplicationDetails = (props) => {
  let isEditApplication=window.location.href.includes("editApplication") && window.location.href.includes("bpa") ;
    const tenantId = Digit.ULBService.getCurrentTenantId();
  const state = Digit.ULBService.getStateId();
  const { isLoadingg, data: blockReason } = Digit.Hooks.obps.useMDMS(state, "BPA", ["BlockReason"]);
  const { t } = useTranslation();
  const history = useHistory();
  let { id: applicationNumber } = useParams();
  const [displayMenu, setDisplayMenu] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [isEnableLoader, setIsEnableLoader] = useState(false);
  const [isWarningPop, setWarningPopUp] = useState(false);
  const { isMdmsLoading, data: mdmsData } = Digit.Hooks.obps.useMDMS(state, "BPA", ["RiskTypeComputation"]);
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
    clearDataDetails
  } = props;
  
  useEffect(() => {
    if (showToast) {
      workflowDetails.revalidate();
    }
  }, [showToast]);

  function onActionSelect(action) {
    if (action) {
      if(action?.action=="EDIT PAY 2" && window.location.href.includes("bpa")){
        window.location.assign(window.location.href.split("bpa")[0]+"editApplication/bpa"+window.location.href.split("bpa")[1]);
      }
      if(action?.isToast){
        setShowToast({ key: "error", error: { message: action?.toastMessage } });
        setTimeout(closeToast, 5000);
      }
      else if (action?.isWarningPopUp) {
        setWarningPopUp(true);
      } else if (action?.redirectionUrll) {
        if (action?.redirectionUrll?.action === "ACTIVATE_CONNECTION") {
          // window.location.assign(`${window.location.origin}digit-ui/employee/ws/${action?.redirectionUrll?.pathname}`, { data: action?.redirectionUrll?.state });

          history.push(`${action?.redirectionUrll?.pathname}`, JSON.stringify({ data: action?.redirectionUrll?.state, url: `${location?.pathname}${location.search}` }));
        }
        else if (action?.redirectionUrll?.action === "RE-SUBMIT-APPLICATION"){
          history.push(`${action?.redirectionUrll?.pathname}`, { data: action?.redirectionUrll?.state });
        }
        else {
          window.location.assign(`${window.location.origin}/digit-ui/employee/payment/collect/${action?.redirectionUrll?.pathname}`);
        }
      } else if (!action?.redirectionUrl && action?.action!="EDIT PAY 2") {
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

  const submitAction = async (data, nocData = false, isOBPS = {}) => {
    if(data?.Property?.workflow?.comment?.length == 0 || data?.Licenses?.[0]?.comment?.length == 0 || data?.WaterConnection?.comment?.length == 0 || data?.SewerageConnection?.comment?.length == 0 || data?.BPA?.comment?.length == 0)
    {
     alert(t("Please fill in the comments before submitting"))
    }
    else if( data?.BPA?.businessService=="BPA" && !data?.BPA?.additionalDetails?.blockingReason && data?.BPA?.workflow?.action=="BLOCK"){
      alert(t("Please select Blocking reason"))
    }
    else{
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
              data.selectedAction = selectedAction;
              history.replace(`/digit-ui/employee/obps/response`, { data: data });
            }
            if (isOBPS?.isStakeholder) {
              data.selectedAction = selectedAction;
              history.push(`/digit-ui/employee/obps/stakeholder-response`, { data: data });
            }
            if (isOBPS?.isNoc) {
              history.push(`/digit-ui/employee/noc/response`, { data: data });
            }
            if (data?.Amendments?.length > 0) {
              //RAIN-6981 instead just show a toast here with appropriate message
              //show toast here and return 
              //history.push("/digit-ui/employee/ws/response-bill-amend", { status: true, state: data?.Amendments?.[0] })
  
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
    }
  
  };

  if (isLoading || isEnableLoader) {
    return <Loader />;
  }
  const onSubmit =async(data)=> {
    const bpaApplicationDetails = await Digit.OBPSService.BPASearch(tenantId, {applicationNo: applicationData?.applicationNo});
    const riskType = Digit.Utils.obps.calculateRiskType(
      mdmsData?.BPA?.RiskTypeComputation,
      applicationDetails?.edcrDetails?.planDetail?.plot?.area,
      applicationDetails?.edcrDetails?.planDetail?.blocks
    );
    const bpaDetails={
      BPA:bpaApplicationDetails.BPA[0]
    }
    bpaDetails.BPA.riskType=riskType
    bpaDetails.BPA.workflow={
                "action": "EDIT PAY 2",
                "assignes": [],
                "comments": null,
                "varificationDocuments": null
            }
    bpaDetails.BPA.additionalDetails.selfCertificationCharges.BPA_DEVELOPMENT_CHARGES=sessionStorage.getItem("development") || "0";
    bpaDetails.BPA.additionalDetails.selfCertificationCharges.BPA_OTHER_CHARGES=sessionStorage.getItem("otherCharges") ||"0";
    bpaDetails.BPA.additionalDetails.selfCertificationCharges.BPA_LESS_ADJUSMENT_PLOT=sessionStorage.getItem("lessAdjusment")|| "0";
    bpaDetails.BPA.additionalDetails.otherFeesDiscription=sessionStorage.getItem("otherChargesDisc"|| "NA");
    bpaDetails.BPA.additionalDetails.lessAdjustmentFeeFiles=JSON.parse(sessionStorage.getItem("uploadedFileLess"));
   
    if(parseInt(sessionStorage.getItem("lessAdjusment"))>(parseInt(sessionStorage.getItem("development"))+parseInt(sessionStorage.getItem("otherCharges"))+parseInt(bpaDetails?.BPA?.additionalDetails?.selfCertificationCharges?.BPA_MALBA_CHARGES)+parseInt(bpaDetails?.BPA?.additionalDetails?.selfCertificationCharges?.BPA_LABOUR_CESS)+parseInt(bpaDetails?.BPA?.additionalDetails?.selfCertificationCharges?.BPA_WATER_CHARGES)+parseInt(bpaDetails?.BPA?.additionalDetails?.selfCertificationCharges?.BPA_GAUSHALA_CHARGES_CESS))){
      alert(t("Enterd Less Adjustment amount is invalid"));
    }
    else{
        const response = await Digit.OBPSService.update(bpaDetails, tenantId); 
        window.location.assign(window.location.href.split("/editApplication")[0]+window.location.href.split("editApplication")[1]);
          }    
  };

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
              blockReason={blockReason?.BPA?.BlockReason}
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
          {!isEditApplication?  (
            <ApplicationDetailsActionBar
            workflowDetails={workflowDetails}
            displayMenu={displayMenu}
            onActionSelect={onActionSelect}
            setDisplayMenu={setDisplayMenu}
            businessService={businessService}
            forcedActionPrefix={forcedActionPrefix}
            ActionBarStyle={ActionBarStyle}
            MenuStyle={MenuStyle}
          />
          ):(<div >
            <SubmitBar style={{ marginRight:20}} label={t("BPA_EDIT_UPDATE")} onSubmit={onSubmit}  id/>
            </div>)}          
        </React.Fragment>
      ) : (
        <Loader />
      )}
    </React.Fragment>
  );
};

export default ApplicationDetails;
