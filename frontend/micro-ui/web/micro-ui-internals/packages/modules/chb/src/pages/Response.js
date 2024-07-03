  import React, { useEffect, useState } from "react";
  import { Card, Banner, CardText, SubmitBar, Loader, LinkButton, Toast, ActionBar } from "@upyog/digit-ui-react-components";
  import { Link, useHistory } from "react-router-dom";
  import { useTranslation } from "react-i18next";
  import { useQueryClient } from "react-query";
  import getChbAcknowledgementData from "../getChbAcknowledgementData";

  const GetMessage = (type, action, isSuccess, isEmployee, t) => {
    return t(`${isEmployee ? "E" : "C"}S_CHB_RESPONSE_${action ? action : "CREATE"}_${type}${isSuccess ? "" : "_ERROR"}`);
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
        message={GetActionMessage(props?.data?.hallsBookingApplication?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
        applicationNumber={props?.data?.hallsBookingApplication?.[0]?.applicationNumber}
        info={GetLabel(props.data?.hallsBookingApplication?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
        successful={props.isSuccess}
      />
    );
  };

  const Response = (props) => {
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

    const mutation = Digit.Hooks.chb.useChbCreateAPI(tenantId, state.key !== "UPDATE");
    const mutation1 = Digit.Hooks.chb.useChbCreateAPI(tenantId, false);

    const { data: storeData } = Digit.Hooks.useStore.getInitData();
    const { tenants } = storeData || {};

    const { isLoading: auditDataLoading, isError: isAuditError, data: auditData } = Digit.Hooks.chb.useChbSearch(
      {
        tenantId,
        filters: { applicationNumber: state.hallsBookingApplication.bookingNo, audit: true },
      },
      { enabled: enableAudit, select: (data) => data.hallsBookingApplication?.filter((e) => e.status === "ACTIVE") }
    );

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
        if (successRes?.hallsBookingApplication[0]?.creationReason === "CREATE") {
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
            hallsBookingApplication: state?.hallsBookingApplication,
          },
          {
            onError,
            onSuccess,
          }
        );
      }
    }, []);

    const handleDownloadPdf = async () => {
      const { hallsBookingApplication = [] } = mutation.data || successData;
      const Pet = (hallsBookingApplication && hallsBookingApplication[0]) || {};
      const tenantInfo = tenants.find((tenant) => tenant.code === Pet.tenantId);

      let tenantId = Pet.tenantId || tenantId;
      

      const data = await getChbAcknowledgementData({ ...Pet, auditData }, tenantInfo, t);
      Digit.Utils.pdf.generate(data);
    };

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
          <CardText>
            {DisplayText(state.action, (mutation.isSuccess || !!successData) && !mutation.isError, props.parentRoute.includes("employee"), t)}
          </CardText>
          {(mutation.isSuccess || !!successData) && !mutation.isError && (
            <SubmitBar style={{ overflow: "hidden" }} label={t("CHB_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />
          )}
        </Card>
        {showToast && <Toast error={showToast.key === "error" ? true : false} label={error} onClose={closeToast} />}
        <ActionBar>
          <Link to={`${props.parentRoute.includes("employee") ? "/digit-ui/employee" : "/digit-ui/citizen"}`}>
            <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
          </Link>
        </ActionBar>
      </div>
    );
  };

  export default Response;