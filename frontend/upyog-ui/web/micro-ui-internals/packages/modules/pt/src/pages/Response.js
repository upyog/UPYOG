import React, { useEffect, useState } from "react";
import { Card, Banner, CardText, SubmitBar, Loader, Toast, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import getPTAcknowledgementData from "../getPTAcknowledgementData";

const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`${isEmployee ? "E" : "C"}S_PT_RESPONSE_${action ? action : "CREATE"}_${type}${isSuccess ? "" : "_ERROR"}`);
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
const BannerPicker = props => {
  return <Banner message={GetActionMessage(props?.data?.Properties?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)} applicationNumber={props?.data?.Properties?.[0]?.acknowldgementNumber} info={GetLabel(props.data?.Properties?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)} successful={props.isSuccess} />;
};

const Response = (props) => {
  const { t } = useTranslation();
  const location = useLocation();
  const { state } = location;

  const { Property, responseData, isSuccess, error: stateError, action, key } = state || {};
  const [error, setError] = useState(stateError || null);
  const [showToast, setShowToast] = useState(stateError ? { key: "error" } : null);
  const [enableAudit, setEnableAudit] = useState(false);

  const closeToast = () => {
    setShowToast(null);
    setError(null);
  };
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const { isLoading: auditDataLoading, isError: isAuditError, data: auditData } = Digit.Hooks.pt.usePropertySearch(
    {
      tenantId,
      filters: { propertyIds: Property?.propertyId, audit: true },
    },
    { enabled: enableAudit, select: (data) => data.Properties?.filter((e) => e.status === "ACTIVE") }
  );

  useEffect(() => {
    if (isSuccess && responseData?.Properties?.[0]?.creationReason === "MUTATION") {
      setEnableAudit(true);
    }
  }, [isSuccess, responseData]);

  const handleDownloadPdf = async () => {
    const { Properties = [] } = responseData || {};
    const propDetails = (Properties && Properties[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === propDetails.tenantId);
    
    let propTenantId = propDetails.tenantId || tenantId;
    const propertyDetails = await Digit.PTService.search({ tenantId: propTenantId, filters: { propertyIds: propDetails?.propertyId, status: "INACTIVE" } });
    propDetails.transferorDetails = propertyDetails?.Properties?.[0] || [];
    propDetails.isTransferor = true;
    propDetails.transferorOwnershipCategory = propertyDetails?.Properties?.[0]?.ownershipCategory
    
    const data = await getPTAcknowledgementData({ ...propDetails, auditData }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  if (!state) {
    return <Loader />;
  }
  return <div>
      <Card>
        <BannerPicker
          t={t}
          data={responseData}
          action={action}
          isSuccess={isSuccess}
          isLoading={false}
          isEmployee={props.parentRoute.includes("employee")}
        />
        <CardText>
          {DisplayText(action, isSuccess, props.parentRoute.includes("employee"), t)}
        </CardText>
        {isSuccess && (
          <SubmitBar className="pt-auto-103" label={t("PT_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />
        )}
      </Card>
      {showToast && <Toast error={showToast.key === "error" ? true : false} label={error} onClose={closeToast} />}
      <ActionBar>
        <Link to={`${props.parentRoute.includes("employee") ? "/upyog-ui/employee" : "/upyog-ui/citizen"}`}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </div>;
};
export default Response;
