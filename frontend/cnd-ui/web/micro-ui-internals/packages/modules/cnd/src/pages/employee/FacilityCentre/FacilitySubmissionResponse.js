import React, { useEffect, useState } from "react";
import { Card, Banner, CardText, SubmitBar, Loader, LinkButton, Toast, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";

/**
 * FacilitySubmissionResponse.js
 * 
 * This component handles the response view after submitting a CND (Construction and Demolition) application.
 * It triggers the API call to submit the application data when the page loads, displays a banner based on 
 * success/failure, and shows a toast message for any error. It also manages session-based mutation state 
 * and renders a loader during the API request. Once the submission is complete, the user is given the 
 * option to return to the home screen.
 * 
 * Key Features:
 * - API call to submit CND application using mutation hook
 * - Banner display based on submission outcome
 * - Toast for error messages
 * - Navigation back to employee home page
 */

const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(isSuccess ? `CND_APPLICATION_COMPLETE_SUCCESSFULL`:`CND_APPLICATION_COMPLETION_FAILED`);
};

const GetActionMessage = (action, isSuccess, isEmployee, t) => {
  return GetMessage("ACTION", action, isSuccess, isEmployee, t);
};



const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props?.data?.cndApplicationDetails?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
      applicationNumber={props?.data?.cndApplicationDetails?.applicationNumber}
      info={(props.data?.cndApplicationDetails?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
      successful={props.isSuccess}
    />
  );
};

const FacilitySubmissionResponse = (props) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const history = useHistory();
  const [error, setError] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const [enableAudit, setEnableAudit] = useState(false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", false);

  const closeToast = () => {
    setShowToast(null);
    setError(null);
  };
  

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { state } = props.location;
  const mutation = Digit.Hooks.cnd.useCndCreateApi(tenantId,false); 

  useEffect(() => {
    const onSuccess = async (successRes) => {
      setMutationHappened(true);
      queryClient.clear();
      if (successRes?.responseInfo?.status === "SUCCESSFUL") {
        setEnableAudit(true);
      }
    };
    const onError = (error, variables) => {
      setShowToast({ key: "error" });
      setError(error?.response?.data?.Errors[0]?.message || null);
    };

    if (!mutationHappened) {
      mutation.mutate(
        {
            cndApplication: state?.cndApplication,
        },
        {
          onError,
          onSuccess,
        }
      );
    }
  }, []);


  if (mutation.isLoading || (mutation.isIdle && !mutationHappened)) {
    return <Loader />;
  }

  return (
    <div>
      <Card>
        <BannerPicker
          t={t}
          data={mutation?.data || successData}
          action={state?.action}
          isSuccess={!Object.keys(successData || {}).length ? mutation?.isSuccess : true}
          isLoading={(mutation.isIdle && !mutationHappened) || mutation?.isLoading}
          isEmployee={props.parentRoute.includes("employee")}
        />
       
      </Card>
      {showToast && <Toast error={showToast.key === "error" ? true : false} label={error} onClose={closeToast} />}
      <ActionBar>
        <Link to={`/cnd-ui/employee`}>
          <SubmitBar label={t("CND_HOME")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default FacilitySubmissionResponse;