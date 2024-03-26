import React, { useEffect } from "react";
import { Card, Banner, CardText, SubmitBar, Loader, LinkButton, ActionBar } from "@egovernments/digit-ui-react-components";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

const GetMessage = (type, action, isSuccess, IsSor, t) => {
  return t(`SOR_RESPONSE_${action ? action : "CREATE"}_${type}${isSuccess ? "" : "_ERROR"}`);
};

const GetActionMessage = (action, isSuccess, IsSor, t) => {
  return GetMessage("ACTION", action, isSuccess, IsSor, t);
};

const GetLabel = (action, isSuccess, IsSor, t) => {
  if (isSuccess) {
    return t("WMS_SOR_ID_LABEL");
  }
  // return GetMessage("LABEL", action, isSuccess, IsSor, t);
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props.action, props.isSuccess, props.IsSor, props.t)}
      applicationNumber={props.isSuccess?props?.data?.Employees?.[0]?.code:''}
      info={GetLabel(props.action, props.isSuccess, props.IsSor, props.t)}
      successful={props.isSuccess}
    />
  );
};

const WmsPrjResponse = (props) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const { state } = props.location;
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("SOR_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("SOR_WMS_MUTATION_SUCCESS_DATA", false);
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("SOR_WMS_ERROR_DATA", false);
  const mutation = state.key === "UPDATE" ? Digit.Hooks.wms.sor.useWmsSorUpdate(tenantId) : Digit.Hooks.wms.sor.useWmsSorCreate(tenantId);

  const onError = (error, variables) => {
    setErrorInfo(error?.response?.data?.Errors[0]?.code || 'ERROR');
    setMutationHappened(true);
  };

  useEffect(() => {
    if (mutation.data) setsuccessData(mutation.data);
  }, [mutation.data]);

  useEffect(() => {
    const onSuccess = () => {
      setMutationHappened(true);
    };
    if (!mutationHappened ) {
      if (state.key === "UPDATE") {
        mutation.mutate(
          {
            Employees: state.Employees,
          },
          {
            onError,
            onSuccess,
          }
        );
      } else {
        mutation.mutate(state, {
          onSuccess,
        });
      }
    }
  }, []);

  const DisplayText = (action, isSuccess, IsSor, t) => {
    if (!isSuccess) {
      return mutation?.error?.response?.data?.Errors[0].code||errorInfo;
    } else {
      Digit.SessionStorage.set("isupdate", Math.floor(100000 + Math.random() * 900000));
      return state.key === "CREATE"?"WMS_CREATE_SOR_INFO" :"";
    }
  };
    if (mutation.isLoading || (mutation.isIdle && !mutationHappened)) {
    return <Loader />;
  }
  return (
    <Card>
      <BannerPicker
        t={t}
        data={mutation?.data|| successData}
        action={state.action}
        isSuccess={!successData ? mutation?.isSuccess : true}
        isLoading={(mutation.isIdle && !mutationHappened) || mutation?.isLoading}
      />
      <CardText>{t(DisplayText(state.action, mutation.isSuccess || !!successData, props.parentRoute.includes("citizen"), t), t)}</CardText>

      <ActionBar>
        <Link to={`${props.parentRoute.includes("citizen") ? "/upyog-ui/citizen" : "/upyog-ui/citizen"}`}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </Card>
  );
};

export default WmsPrjResponse;
