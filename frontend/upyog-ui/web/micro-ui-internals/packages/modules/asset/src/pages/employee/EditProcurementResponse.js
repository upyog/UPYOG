import React, { useEffect, useState } from "react";
import { Card, Banner, CardText, SubmitBar, Loader, LinkButton, Toast, ActionBar } from "@upyog/digit-ui-react-components";
import { Link, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";

const GetProcurementMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`${isEmployee ? "E" : "C"}S_PROCUREMENT_RESPONSE_${action ? action : "EDIT_SUCCESS"}_${type}${isSuccess ? "" : "_ERROR"}`);
};

const GetProcurementActionMessage = (action, isSuccess, isEmployee, t) => {
  return GetProcurementMessage("ACTION", action, isSuccess, isEmployee, t);
};

const GetProcurementLabel = (action, isSuccess, isEmployee, t) => {
  return GetProcurementMessage("LABEL", action, isSuccess, isEmployee, t);
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetProcurementActionMessage(props.action, props.isSuccess, props.isEmployee, props.t)}
      applicationNumber={props?.data?.ProcurementRequests?.[0]?.requestId}
      info={GetProcurementLabel(props.action, props.isSuccess, props.isEmployee, props.t)}
      successful={props.isSuccess}
    />
  );
};

const EditProcurementResponse = (props) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const history = useHistory();
  const [error, setError] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const [enableAudit, setEnableAudit] = useState(false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_PROCUREMENT_SUCCESS_DATA", false);

  const closeToast = () => {
    setShowToast(null);
    setError(null);
  };

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { state } = props.location;
  const mutation = Digit.Hooks.asset.useProcerementCreateAPI(tenantId, false);

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

    mutation.mutate(
      {
        ProcurementRequest: state?.ProcurementRequest || state?.ProcurementDetail,
      },
      {
        onError,
        onSuccess,
      }
    );
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
        <Link to={`${props.parentRoute.includes("employee") ? `/upyog-ui/employee/asset/assetservice/procurement-search` : false}`}>
          <SubmitBar label={t("PROCUREMENT_APPLICATIONS")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default EditProcurementResponse;
