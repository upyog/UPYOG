import { Banner, Card, CardText, LinkButton, LinkLabel, Loader, Row, StatusTable, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useRouteMatch } from "react-router-dom";
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
const EWASTEAcknowledgement = ({ data, onSuccess }) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.ew.useEWCreateAPI(data?.address?.city?.code);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};

  useEffect(() => {
    try {
      data.tenantId = tenantId;
      let formdata = EWDataConvert(data);
      mutation.mutate(formdata, {
        onSuccess,
      });
    } catch (err) {
      console.error("Error submitting E-Waste application:", err);
    }
  }, []);

  /**
   * Generates and downloads acknowledgement PDF
   */
  const handleDownloadPdf = async () => {
    const { EwasteApplication = [] } = mutation.data;
    let EW = (EwasteApplication && EwasteApplication[0]) || {};
    const tenantInfo = tenants.find((tenant) => tenant.code === EW.tenantId);
    let tenantId = EW.tenantId || tenantId;

    const data = await getEwAcknowledgementData({ ...EW }, tenantInfo, t);
    Digit.Utils.pdf.generateTable(data);
  };

  return mutation.isLoading || mutation.isIdle ? (
    <Loader />
  ) : (
    <Card>
      <BannerPicker t={t} data={mutation.data} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      <StatusTable>
        {mutation.isSuccess && (
          <Row
            rowContainerStyle={rowContainerStyle}
            last
            textStyle={{ whiteSpace: "pre", width: "60%" }}
          />
        )}
      </StatusTable>
      {mutation.isSuccess && <SubmitBar label={t("EWASTE_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />}
      <Link to={`/upyog-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default EWASTEAcknowledgement;