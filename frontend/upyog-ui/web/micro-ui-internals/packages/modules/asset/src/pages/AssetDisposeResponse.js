import React, { useEffect, useState } from "react";
import { Card, Banner, SubmitBar, Loader, Toast, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";

const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`${isEmployee ? "E" : "C"}S_ASSET_RESPONSE_${action ? action : "DISPOSE"}_${type}${isSuccess ? "" : "_ERROR"}`);
};

const GetActionMessage = (action, isSuccess, isEmployee, t) => {
  return GetMessage("ACTION", action, isSuccess, isEmployee, t);
};

const GetLabel = (action, isSuccess, isEmployee, t) => {
  return GetMessage("LABEL", action, isSuccess, isEmployee, t);
};

const BannerPicker = ({ data, action, isSuccess, isEmployee, t }) => {
  return (
    <Banner
      message={GetActionMessage(data?.assetDisposalStatus || action, isSuccess, isEmployee, t)}
      applicationNumber={data?.disposalId}
      info={GetLabel(data?.assetDisposalStatus || action, isSuccess, isEmployee, t)}
      successful={isSuccess}
    />
  );
};

const AssetDisposeResponse = (props) => {
  const location = useLocation();
  const { state } = location;
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [error, setError] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const [enableAudit, setEnableAudit] = useState(false);
  const [successData, setSuccessData] = useState(null);
  const [applicationDetail, setApplicationDetail] = useState(null);

  // Safe check for parentRoute
  const isEmployee = Digit.UserService.getUser()?.info?.type || true;
  
  // Extract data from navigation state or use defaults
  const AssetDisposal = state?.AssetDisposal;
  const applicationNo = state?.applicationNo;
  const isSuccess = state?.isSuccess ?? true;
  const action = state?.action || "DISPOSE";

  useEffect(() => {
    if (AssetDisposal && AssetDisposal.ResponseInfo?.status === "successful") {
      setSuccessData(AssetDisposal.AssetDisposals?.[0] || AssetDisposal);
      setApplicationDetail(applicationNo);
    } else if (AssetDisposal) {
      // Handle case where disposal data exists but might not have ResponseInfo
      setSuccessData(AssetDisposal.AssetDisposals?.[0] || AssetDisposal);
      setApplicationDetail(applicationNo);
    }
  }, [AssetDisposal, applicationNo]);

  const closeToast = () => {
    setShowToast(null);
    setError(null);
  };

  // If no data available (page reload), show appropriate message
  if (!AssetDisposal && !successData) {
    return (
      <div>
        <Card>
          <Banner
            message={t("CS_ASSET_DISPOSE_SUCCESS_DEFAULT")}
            info={t("CS_ASSET_DISPOSE_SUCCESS_LABEL")}
            successful={true}
          />
        </Card>
        <ActionBar>
          <Link to={isEmployee ? "/upyog-ui/employee" : "/upyog-ui/citizen"}>
            <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
          </Link>
        </ActionBar>
      </div>
    );
  }

  return (
    <div>
      <Card>
        {successData ? (
          <BannerPicker
            t={t}
            data={successData}
            action={action}
            isSuccess={isSuccess}
            isEmployee={isEmployee}
          />
        ) : (
          <Loader />
        )}
        
      </Card>
      <ActionBar>
        <Link to={"/upyog-ui/employee"}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default AssetDisposeResponse;
