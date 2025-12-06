import React, { Fragment, useEffect, useState } from "react";
import { ViewComposer, Toast, Loader } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useLocation, useHistory } from "react-router-dom";
import TestWFActions from "./TestWFActions";
import { useQueryClient } from "react-query";
import { UICustomizations } from "../../../configs/UICustomizations";

function TestDetails() {
  const { t } = useTranslation();
  const location = useLocation();
  const history = useHistory();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const searchParams = new URLSearchParams(location.search);
  const id = searchParams.get("id");
  const businessService = "PQM";
  const [status, setStatus] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const [workflowDetails, setWorkflowDetails] = useState(null);
  const [nextAction, setNextAction] = useState(null);
  const queryClient = useQueryClient();

  const { isLoading, data: testData, revalidate, isFetching, refetch } = Digit.Hooks.tqm.useViewTestResults({
    t,
    id: id,
    tenantId: tenantId,
    config: {
      select: (data) => {
        if(data?.wfStatus==="PENDINGRESULTS" || data?.wfStatus==="DRAFTED"){
          return {
            data: {
              cards: [
                {
                  sections: [
                    {
                      type: "DATA",
                      cardHeader: { value: t("ES_TQM_TEST_DETAILS_HEADING"), inlineStyles: { marginTop: 0 } },
                      values: data.details,
                    },
                    // {
                    //   type: "COMPONENT",
                    //   component: "TqmCardReading",
                    //   props: {
                    //     parameterData: data?.testResponse?.testCriteria || null,
                    //   },
                    // },
                  ],
                },
              ],
            },
            response: data.testResponse,
          }
        }else {
          return {
            data: {
              cards: [
                {
                  sections: [
                    {
                      type: "DATA",
                      cardHeader: { value: t("ES_TQM_TEST_DETAILS_HEADING"), inlineStyles: { marginTop: 0 } },
                      values: data.details,
                    },
                    {
                      type: "COMPONENT",
                      component: "TqmCardReading",
                      props: {
                        parameterData: data?.testResponse?.testCriteria || null,
                      },
                    },
                  ],
                },
              ],
            },
            response: data.testResponse,
          }
        }
        
    },
      staleTime: 0,
      cacheTime: 0
    },
  });

  let { isLoading: isWFLoading, isError: isWFError, data: WFData } = Digit.Hooks.useWorkflowDetailsFSM({
    tenantId: tenantId,
    id: id,
    moduleCode: businessService,
    config: {
      enabled: true,
      cacheTime: 0,
    },
  });

  const { mutate } = Digit.Hooks.tqm.useTestUpdate(tenantId);

  const closeToast = () => {
    setShowToast(null);
  };

  useEffect(() => {
    if (WFData && !isWFLoading) {
      setWorkflowDetails(WFData);
      WFData?.actionState?.applicationStatus === UICustomizations?.workflowStatusMap?.submit ? setNextAction(null) : setNextAction(WFData?.actionState?.applicationStatus);
    }
  }, [WFData, isWFLoading]);

  const submitAction = (data) => {
    if(data?.updateError === true){
      setShowToast({ key: "error", action: "ES_TQM_FILL_MANDATORY_FIELD" });
      setTimeout(closeToast, 5000);
      return;
    }
    if(data?.draftError === true){
      setShowToast({ key: "error", action: "ES_TQM_ONE_FIELD_MANDATORY" });
      setTimeout(closeToast, 5000);
      return;
    }
    mutate(data, {
      onError: (error, variables) => {
        //here if the response error code is "" then show "This test is no longer valid as Process/Stage/Output of the test has been removed"  MDMS_INVALID_ERROR_UPDATE_TEST
        if(error?.message?.includes("code is not present in mdms")){
          setShowToast({ key: "error", action: "MDMS_INVALID_ERROR_UPDATE_TEST" });
          setTimeout(closeToast, 5000);
        }else{
          setShowToast({ key: "error", action: nextAction === "SCHEDULED" ? "ES_TQM_SCHEDULE_UPDATED_FAILED" : "ES_TQM_TEST_UPDATED_FAILED" });
          setTimeout(closeToast, 5000);
        }
      },
      onSuccess: (data, variables) => {
        setShowToast({ key: "success", action: data?.tests?.[0]?.workflow?.action === "SAVE_AS_DRAFT" ? "ES_TQM_DRAFT_UPDATED_SUCCESSFULLY" :"ES_TQM_STATUS_UPDATE_SUCCESS" });
        setTimeout(closeToast, 5000);
        queryClient.invalidateQueries("TQM_ADMIN_TEST_RESULTS");
        queryClient.invalidateQueries("workFlowDetailsWorks");
        refetch();
        // Remove scroll value when action taken 
        localStorage.removeItem("/tqm-ui/employee/tqm/inbox")
        if (data?.tests?.[0]?.workflow?.action === UICustomizations?.workflowActionMap?.update) {
          return history.push(`/${window.contextPath}/employee/tqm/response?testId=${id}&isSuccess=${true}`, {
            message: "ES_TQM_TEST_UPDATE_SUCCESS_RESPONSE",
            text: "ES_TQM_TEST_UPDATE_SUCCESS_RESPONSE_TEXT",
          });
        }
      },
    });
  };

  if (isLoading || isWFLoading) {
    return <Loader />;
  }
  return (
    <>
      {!isLoading && <ViewComposer data={testData?.data} isLoading={isLoading} />}
      {testData && !isLoading && workflowDetails && !isWFLoading && nextAction && (
        <TestWFActions
          id={id}
          t={t}
          WFData={workflowDetails}
          actionData={workflowDetails?.nextActions}
          actionState={workflowDetails?.actionState?.applicationStatus}
          submitAction={submitAction}
          testDetailsData={testData?.response}
          isDataLoading={isLoading}
        />
      )}
      {showToast && (
        <Toast error={showToast.key === "error" ? true : false} label={t(showToast.key === "success" ? showToast.action : showToast.action)} onClose={closeToast} isDleteBtn={true} />
      )}
    </>
  );
}

export default TestDetails;