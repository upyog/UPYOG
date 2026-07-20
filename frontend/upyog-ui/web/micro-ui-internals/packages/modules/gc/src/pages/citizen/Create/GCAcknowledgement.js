import { Banner, Card, LinkButton, Loader, Row, StatusTable, SubmitBar, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props?.isSuccess) {
    return t("ES_GC_RESPONSE_CREATE_ACTION");
  } else if (props?.isLoading) {
    return t("CS_GC_APPLICATION_PENDING");
  }
  return t("CS_GC_APPLICATION_FAILED");
};

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={props?.data?.garbageAccounts?.[0].grbgApplication?.applicationNo}
      info={props?.isSuccess ? props.t("GC_APPLICATION_NO") : ""}
      successful={props?.isSuccess}
      style={{ width: "100%" }}
    />
  );
};

/**
 * GCAcknowledgement component displays the acknowledgment of a Garbage Collection 
 * registration request. It shows the status of the operation, including 
 * success or failure messages. The component handles the mutation of 
 * GC data and manages loading states effectively.
 */
const GCAcknowledgement = () => {
  const { t } = useTranslation();
  const { state } = useLocation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const navigate = Digit.Hooks.useCustomNavigate();
  const user = Digit.UserService.getUser().info;
  const [showToast, setShowToast] = useState(null);
 
  const handleMakePayment = async () => {
    try {
      const applicationNo = state?.data?.garbageAccounts?.[0].grbgApplication?.applicationNo;
      
      if(user.type === "CITIZEN") {
        navigate(
          `/upyog-ui/citizen/payment/my-bills/${"gc-services"}/${applicationNo}`,
          { state: { tenantId: tenantId, applicationNo: applicationNo } }
        );
      } else if(user.type === "EMPLOYEE") {
        navigate(
          `/upyog-ui/employee/payment/collect/${"gc-services"}/${applicationNo}`,
          { state: { tenantId: tenantId, applicationNo: applicationNo } }
        );
      }
    } catch (error) {
      setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
    }
  };
  
  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 2000); // Close toast after 2 seconds
      return () => clearTimeout(timer); // Clear timer on cleanup
    }
  }, [showToast]);

  const isLoading = !state;
  const isSuccess = state?.isSuccess;

  if (!state) {
    return <Loader />;
  }

  return isLoading ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={state?.data} isSuccess={isSuccess} isLoading={isLoading} />
      <StatusTable>
        {isSuccess && <Row rowContainerStyle={rowContainerStyle} last textStyle={{ whiteSpace: "pre", width: "60%" }} />}
      </StatusTable>
      
      {isSuccess && (
        <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handleMakePayment} />
      )}

      {user?.type === "CITIZEN" ? (
        <Link to={`/upyog-ui/citizen`}>
          <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      ) : (
        <Link to={`/upyog-ui/employee`}>
          <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      )}

      {showToast && (
        <Toast error={showToast.error} warning={showToast.warning} label={t(showToast.label)} onClose={() => setShowToast(null)} />
      )}
    </Card>
  );
};

export default GCAcknowledgement;