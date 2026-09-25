import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState, useCallback } from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";
import getEwAcknowledgementData from "../../../utils/getEwAcknowledgementData";
import { EWDataConvert } from "../../../utils";

/**
 * Determines the appropriate action message based on application status
 *
 * @param {Object} props Component properties
 * @param {boolean} props.isSuccess Whether the application submission was successful
 * @param {boolean} props.isLoading Whether the application is being processed
 * @returns {string} Translated message for the current status
 */
const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("ES_EWASTE_RESPONSE_CREATE_ACTION") : t("CS_EWASTE_UPDATE_APPLICATION_SUCCESS");
  } else if (props.isLoading) {
    return !window.location.href.includes("edit-application") ? t("CS_EWASTE_APPLICATION_PENDING") : t("CS_EWASTE_UPDATE_APPLICATION_PENDING");
  } else if (!props.isSuccess) {
    return !window.location.href.includes("edit-application") ? t("CS_EWASTE_APPLICATION_FAILED") : t("CS_EWASTE_UPDATE_APPLICATION_FAILED");
  }
};

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

/**
 * Renders a status banner with application details and status message
 *
 * @param {Object} props Component properties
 * @param {Object} props.data Application data containing request details
 * @param {Function} props.t Translation function
 * @param {boolean} props.isSuccess Success status indicator
 * @returns {JSX.Element} Banner component with status information
 */
const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={props.data?.EwasteApplication[0].requestId}
      info={props.isSuccess ? props.t("EWASTE_APPLICATION_NO") : ""}
      successful={props.isSuccess}
      style={{ width: "100%" }}
    />
  );
};

/**
 * Acknowledgement page for E-Waste application submission.
 * Handles application creation, status display, and acknowledgement download.
 *
 * @param {Object} props Component properties
 * @param {Object} props.data Application data to be submitted
 * @param {Function} props.onSuccess Callback function for successful submission
 * @returns {JSX.Element} Acknowledgement page with status and actions
 */
const EWASTEAcknowledgement = () => {
  const { t } = useTranslation();
  const {state} = useLocation();
  const [errorToast, setErrorToast] = useState(null);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  const application = state?.data;

  /**
   * Generates and downloads acknowledgement PDF
   */
  const handleDownloadPdf = async () => {
    const { EwasteApplication = [] } = state.data || {};
    let EW = (EwasteApplication && EwasteApplication[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === EW.tenantId);
    let tenantId = EW.tenantId || tenantId;

    const data = await getEwAcknowledgementData({ ...EW }, tenantInfo, t);
    Digit.Utils.pdf.generateTable(data);
  };

   const isLoading = !state;
   const isSuccess = state?.isSuccess;
 
   if (!state) {
     return <Loader />;
   }

  return isLoading ? (
    <Loader />
  ) : 
  (
    <Card>
      <BannerPicker t={t} data={application} isSuccess={isSuccess}  isLoading={isLoading}/>
      <StatusTable>
        {isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last       
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>
      {isSuccess && <SubmitBar label={t("EWASTE_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />}
      {errorToast && <Toast error label={errorToast} onClose={() => setErrorToast(null)} />}
      <Link to={`/upyog-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default EWASTEAcknowledgement;