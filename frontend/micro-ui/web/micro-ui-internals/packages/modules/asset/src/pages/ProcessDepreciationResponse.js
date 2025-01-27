import React, { useEffect, useState } from "react";
import { Card, Banner, SubmitBar, Toast, ActionBar } from "@upyog/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const ProcessDepreciationResponse = (props) => {
  const location = useLocation();
  const { ProcessDepreciation } = location.state || {}; // Getting data from the location state
  const { t } = useTranslation();
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(false);

  // Dynamically update the banner based on the API response
  useEffect(() => {
    if (ProcessDepreciation && ProcessDepreciation.ResponseInfo.status === "successful") {
      setMessage(ProcessDepreciation.Message); // Set success message
    } else {
      setError(true); // If not successful, set error flag
      setMessage(t("CS_SOMETHING_WENT_WRONG")); // Default error message
    }
  }, [ProcessDepreciation, t]);

  const closeToast = () => {
    setMessage(null);
    setError(false);
  };

  // Custom function to get banner message and label dynamically
  const GetBannerMessage = (isSuccess, t) => {
    return isSuccess ? t("CS_DEPRECIATION_SUCCESS_MESSAGE") : t("CS_SOMETHING_WENT_WRONG");
  };

  const GetBannerLabel = (isSuccess, t) => {
    return isSuccess ? t("CS_DEPRECIATION_SUCCESS_LABEL") : t("CS_ERROR_LABEL");
  };

  return (
    <div>
      <Card>
        {/* Show Banner with success or error message */}
        {message && !error ? (
          <Banner
            message={GetBannerMessage(true, t)} // Success message
            info={GetBannerLabel(true, t)} // Success label
            successful={true} // Indicating success status
          />
        ) : (
          // Show loading banner while waiting
          <Banner message="Processing..." successful={false} />
        )}
      </Card>

      {/* Show error message if any */}
      {error && message && (
        <Toast error={true} label={message} onClose={closeToast} />
      )}

      <ActionBar>
        <Link to={`${props.parentRoute.includes("employee") ? "/digit-ui/employee" : "/digit-ui/citizen"}`}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default ProcessDepreciationResponse;
