import React, { useEffect, useState } from "react";
import { Card, Banner, CardText, SubmitBar, Loader, LinkButton, Toast, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";



const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`${isEmployee ? "E" : "C"}S_ASSET_RESPONSE_${action ? action : "EDIT"}_${type}${isSuccess ? "" : "_ERROR"}`);
};

const GetActionMessage = (action, isSuccess, isEmployee, t) => {
  return GetMessage("ACTION", action, isSuccess, isEmployee, t);
};

const GetLabel = (action, isSuccess, isEmployee, t) => {
  return GetMessage("LABEL", action, isSuccess, isEmployee, t);
};

const DisplayText = (action, isSuccess, isEmployee, t) => {
  return GetMessage("DISPLAY", action, isSuccess, isEmployee, t);
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props?.data?.Asset?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
      applicationNumber={props?.data?.Assets?.[0]?.applicationNo}
      info={GetLabel(props.data?.Assets?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
      successful={props.isSuccess}
    />
  );
};

const EditResponse = (props) => {
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

  const mutation = Digit.Hooks.asset.useEditUpdateAPI(tenantId, state.key !== "UPDATE");
  const mutation1 = Digit.Hooks.asset.useEditUpdateAPI(tenantId, false);

  

  useEffect(() => {
    if (mutation1.data && mutation1.isSuccess) setsuccessData(mutation1.data);
  }, [mutation.data]);
  useEffect(() => {
    if (mutation1.data && mutation1.isSuccess) setsuccessData(mutation1.data);
  }, [mutation1.data]);


  useEffect(() => {
    const onSuccess = async (successRes) => {
      setMutationHappened(true);
      queryClient.clear();
      if (successRes?.ResponseInfo?.status === "successful") {
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
          Asset: state?.Assets,
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
        <Link to={`${props.parentRoute.includes("employee") ? `/digit-ui/employee/asset/assetservice/applicationsearch/application-details/${mutation?.data?.Assets?.[0]?.applicationNo}` : false}`}>
          <SubmitBar label={t("MY_APPLICATIONS")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default EditResponse;