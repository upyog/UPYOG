import React, { useEffect, useState } from "react";
import { Card, Banner, SubmitBar, Loader, Toast, ActionBar } from "@upyog/digit-ui-react-components";
import { Link, useHistory, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "react-query";

const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`${isEmployee ? "E" : "C"}S_ASSET_RESPONSE_${action ? action : "ASSIGN"}_${type}${isSuccess ? "" : "_ERROR"}`);
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
  const { AssetDisposal, applicationNo } = location.state || {};
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const history = useHistory();
  const [error, setError] = useState(null);
  const [showToast, setShowToast] = useState(null);
  const [enableAudit, setEnableAudit] = useState(false);
  const [successData, setSuccessData] = useState(null);
  const [applicationDetail, setApplicationDetail] = useState(null);

  useEffect(() => {
    if (AssetDisposal && AssetDisposal.ResponseInfo.status === "successful") {
      setSuccessData(AssetDisposal.AssetDisposals[0]);
      setApplicationDetail(applicationNo);

    }
  }, [AssetDisposal, applicationDetail]);

  const closeToast = () => {
    setShowToast(null);
    setError(null);
  };

  return (
    <div>
      <Card>
        {successData ? (
          <BannerPicker
            t={t}
            data={successData}
            action={props.action}
            isSuccess={true}
            isEmployee={props.parentRoute.includes("employee")}
          />
        ) : (
          <Loader />
        )}
        <div style={{ padding: "10px", paddingBottom: "10px", display: "flex", justifyContent: "center", alignItems: "center" }}>
          <Link to={`${props.parentRoute}/assetservice/applicationsearch/application-details/${applicationDetail}`} >
            <SubmitBar label={t("AST_DISPOSEL_LIST")} />
          </Link>
        </div>
      </Card>
      {showToast && <Toast error={showToast.key === "error"} label={error} onClose={closeToast} />}
      <ActionBar>
        <Link to={`${props.parentRoute.includes("employee") ? "/upyog-ui/employee" : "/upyog-ui/citizen"}`}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default AssetDisposeResponse;
