import React, { useEffect, useState } from "react";
import { Card, Banner, SubmitBar, Toast, ActionBar, Loader } from "@upyog/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const ProcessDepreciationResponse = (props) => {
  const location = useLocation();
  const { ProcessDepreciation, applicationNo } = location.state || {}; // Getting data from the location state
  const { t } = useTranslation();
  const [message, setMessage] = useState(null);
  const [applicationDetail, setApplicationDetail] = useState(null);
  const [error, setError] = useState(false);
  const [loading, setLoading] = useState(true); // Loader state



  useEffect(() => {
    if (ProcessDepreciation) {
      setTimeout(() => { // Simulating a delay
        setLoading(false); // Stop loader after delay
        if (ProcessDepreciation.ResponseInfo.status === "successful") {
          setMessage(ProcessDepreciation.Message);
          setApplicationDetail(applicationNo);
        } else {
          setError(true);
          setMessage(t("CS_SOMETHING_WENT_WRONG"));
        }
      }, 2000); // 3 seconds delay
    }
  }, [ProcessDepreciation, t]);
  

  const closeToast = () => {
    setMessage(null);
    setError(false);
  };

  const GetBannerMessage = (isSuccess, t) => {
    return isSuccess ? t("CS_DEPRECIATION_SUCCESS_MESSAGE") : t("CS_SOMETHING_WENT_WRONG");
  };

  const GetBannerLabel = (isSuccess, t) => {
    return isSuccess ? t("CS_DEPRECIATION_SUCCESS_LABEL") : t("CS_ERROR_LABEL");
  };

  return (
    <div>
      <Card>
        {/* Show Loader while waiting for response */}
        {loading ? (
          <Loader />
        ) : message && !error ? (
          <div> 
            <Banner
              message={GetBannerMessage(true, t)}
              applicationNumber={applicationDetail}
              info={GetBannerLabel(true, t)}
              successful={true}
            />
            <div style={{ padding: "10px", paddingBottom: "10px", display: "flex", justifyContent: "center", alignItems: "center" }}>
              <Link to={`${props.parentRoute}/assetservice/applicationsearch/application-details/${applicationDetail}`} >
                <SubmitBar label={t("AST_DEPRECIATION_LIST")} />
              </Link>
            </div>
          </div>
        ) : (
          <Banner message="Processing..." successful={false} />
        )}
      </Card>

      {error && message && (
        <Toast error={true} label={message} onClose={closeToast} />
      )}

      <ActionBar>
        <Link to={`${props.parentRoute.includes("employee") ? "/upyog-ui/employee" : "/upyog-ui/citizen"}`}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default ProcessDepreciationResponse;
