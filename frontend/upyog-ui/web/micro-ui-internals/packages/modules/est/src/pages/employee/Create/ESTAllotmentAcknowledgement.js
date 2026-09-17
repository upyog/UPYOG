/**
 * ESTAllotmentAcknowledgement
 * Displays allotment result with document thumbnails (click to preview).
 */

import React, { useEffect, useState } from "react";
import {
  Banner,
  Card,
  CardSubHeader,
  LinkButton,
  Loader,
  Row,
  StatusTable,
  SubmitBar,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";
import { downloadESTAcknowledgement } from "../../../utils";
import {
  getEmployeeHomeFromModulePath,
  getCitizenHomeFromModulePath,
  getEmployeePaymentCollectPath,
  getCitizenPaymentPath,
} from "../../../utils/estRoutes";

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = ({ t, isSuccess, data }) => {
  const allotment = data?.Allotments?.[0];
  // Display allotmentNo from create API (never allotmentId UUID).
  const applicationNumber =
    allotment?.allotmentNo ||
    allotment?.additionalDetails?.allotmentNo ||
    allotment?.assetNo ||
    "";
  return (
    <Banner
      message={isSuccess ? t("EST_ALLOTED_SUCCESSFULL") : t("EST_APPLICATION_FAILED")}
      applicationNumber={applicationNumber}
      info={
        isSuccess
          ? t(applicationNumber ? "EST_ALLOTMENT_NUMBER" : "EST_APPLICATION_NO")
          : ""
      }
      successful={isSuccess}
      style={{ width: "100%" }}
    />
  );
};

const readAckState = (locationState) => {
  if (locationState && (locationState.isSuccess != null || locationState.data)) {
    return locationState;
  }
  // Recover when hard navigation dropped router state (non-serializable ack payload).
  try {
    const raw = sessionStorage.getItem("__upyog_nav_state__");
    if (!raw) return locationState || {};
    sessionStorage.removeItem("__upyog_nav_state__");
    return JSON.parse(raw) || {};
  } catch {
    return locationState || {};
  }
};

const ESTAllotmentAcknowledgement = ({ onSuccess }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const { data: ackData, isSuccess = false, error } = readAckState(location?.state);

  const user = Digit?.UserService?.getUser?.()?.info || {};
  const initResponse = Digit?.Hooks?.useStore?.getInitData?.() || {};
  const storeData = initResponse?.data || initResponse;
  const tenants = storeData?.tenants || [];

  useEffect(() => {
    if (isSuccess && typeof onSuccess === "function") {
      onSuccess(ackData);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (error) console.error("EST Allotment Acknowledgement — error:", error);

  const handleDownloadPdf = async () => {
    try {
      await downloadESTAcknowledgement(ackData, tenants, t);
    } catch (err) {
      console.error("PDF generation error:", err);
    }
  };

  const homePath =
    user?.type === "CITIZEN"
      ? getCitizenHomeFromModulePath(modulePath)
      : getEmployeeHomeFromModulePath(modulePath);

  const allotmentNo =
    ackData?.Allotments?.[0]?.allotmentNo ||
    "";

  const paymentPath = allotmentNo
    ? user?.type === "CITIZEN"
      ? getCitizenPaymentPath(allotmentNo)
      : getEmployeePaymentCollectPath(allotmentNo)
    : "";

  return (
    <Card>
      <BannerPicker t={t} isSuccess={isSuccess} data={ackData} />

      <StatusTable>
        <Row rowContainerStyle={rowContainerStyle} last />
      </StatusTable>

      {isSuccess && (
        <SubmitBar label={t("EST_ALLOTMENT_ACKNOWLEDGEMENT")} onSubmit={handleDownloadPdf} />
      )}

      {isSuccess && paymentPath && (
        <Link style={{marginLeft: "10px"}}  to={paymentPath}>
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />
        </Link>
      )}

      <Link to={homePath}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default ESTAllotmentAcknowledgement;
