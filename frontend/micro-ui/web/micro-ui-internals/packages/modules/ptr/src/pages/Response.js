import React, { useEffect, useState } from "react";
import { Card, Banner, CardText, SubmitBar, Loader, LinkButton, Toast, ActionBar } from "@egovernments/digit-ui-react-components";
import { Link, useHistory } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";
import getPTAcknowledgementData from "../getPTAcknowledgementData";

const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`${isEmployee ? "E" : "C"}S_PTR_RESPONSE_${action ? action : "CREATE"}_${type}${isSuccess ? "" : "_ERROR"}`);
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
  //console.log("propssss", props)
  return (
    <Banner
      message={GetActionMessage(props?.data?.PetRegistrationApplications?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
      applicationNumber={props?.data?.PetRegistrationApplications?.[0]?.applicationNumber}
      info={GetLabel(props.data?.PetRegistrationApplications?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
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

  const mutation = Digit.Hooks.ptr.usePTRCreateAPI(tenantId, state.key !== "UPDATE");
  const mutation1 = Digit.Hooks.ptr.usePTRCreateAPI(tenantId, false);

  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const { isLoading: auditDataLoading, isError: isAuditError, data: auditData } = Digit.Hooks.ptr.usePTRSearch(
    {
      tenantId,
      filters: { applicationNumber: state.PetRegistrationApplications.applicationNumber, audit: true },
    },
    { enabled: enableAudit, select: (data) => data.PetRegistrationApplications?.filter((e) => e.status === "ACTIVE") }
  );

  // useEffect(() => {
  //   if (mutation1.data && mutation1.isSuccess) setsuccessData(mutation1.data);
  // }, [mutation.data]);

  useEffect(() => {
    if (mutation1.data && mutation1.isSuccess) setsuccessData(mutation1.data);
  }, [mutation1.data]);

  /* 
  This useEffect is created in a logic that once you successfully submitted
  It clears the query cache to force a data refresh.  handling the response.

  */
  useEffect(() => {
    const onSuccess = async () => {queryClient.clear(); };


    const onError = (error, variables) => {
      setShowToast({ key: "error" });
      setError(error?.response?.data?.Errors[0]?.message || null);
    };

    if (!mutationHappened) {
      mutation.mutate(
        {
          PetRegistrationApplications: state?.PetRegistrationApplications,
        },
        {
          onError,
          onSuccess,
        }
      );
    }
  }, []);

  // TODO: will need to add a specific module for pdf to download the pdf 

  // const handleDownloadPdf = async () => {
  //   //const { PetRegistrationApplication = [] } = mutation.data || successData;
  //   const PetRegistrationApplications = PetRegistrationApplications?.[0] || {};
  //   const tenantInfo = tenants.find((tenant) => tenant.code === PetRegistrationApplications.tenantId);
    
  //   let tenantId = PetRegistrationApplications.tenantId || tenantId;
  //   // const propertyDetails = await Digit.PTService.search({ tenantId, filters: { applicationNumber: PetRegistrationApplications?.propertyId, status: "INACTIVE" } }); 
  //   const petDetails = await Digit.PTRService.search({ tenantId, filters: { applicationNumber: PetRegistrationApplications?.applicationNumber, status: "INACTIVE" } });   
  
  //   PetRegistrationApplications.transferorDetails = petDetails?.PetRegistrationApplications?.[0] || [];
  //   PetRegistrationApplications.isTransferor = true;
  //   PetRegistrationApplications.transferorOwnershipCategory = petDetails?.PetRegistrationApplications?.[0]?.ownershipCategory
    
  //   const data = await getPTAcknowledgementData({ ...PetRegistrationApplications, auditData }, tenantInfo, t);
  //   Digit.Utils.pdf.generate(data);
  // };

  const handleDownloadPdf = async () => {
    const PetRegistrationApplications = props?.data?.PetRegistrationApplications?.[0] ;
    const tenantInfo  = "pg.citya"
    // tenants.find((tenant) => tenant.code === PetRegistrationApplications.tenantId);

    const data = await getPTAcknowledgementData(PetRegistrationApplications, tenantInfo, t);
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
          <SubmitBar style={{ overflow: "hidden" }} label={t("PTR_PET_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />
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