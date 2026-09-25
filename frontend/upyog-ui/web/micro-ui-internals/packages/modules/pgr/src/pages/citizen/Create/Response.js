import React, { useEffect, useMemo, useState } from "react";
import { Card, Banner, CardText, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { useTranslation } from "react-i18next";
import getPGRcknowledgementData from "../../../utils/getPGRcknowledgementData";

const PGR_COMPLAINT_RESPONSE = "PGR_COMPLAINT_RESPONSE";

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const GetActionMessage = ({ action }) => {
  const { t } = useTranslation();
  switch (action) {
    case "REOPEN":
      return t("CS_COMMON_COMPLAINT_REOPENED");
    case "RATE":
      return t("CS_COMMON_THANK_YOU");
    default:
      return t("CS_COMMON_COMPLAINT_SUBMITTED");
  }
};

const BannerPicker = ({ apiResponse }) => {
  const { t } = useTranslation();

  if (apiResponse?.responseInfo) {
    return (
      <Banner
        message={<GetActionMessage action={apiResponse.ServiceWrappers?.[0]?.workflow?.action} />}
        complaintNumber={apiResponse.ServiceWrappers?.[0]?.service?.serviceRequestId}
        successful={true}
      />
    );
  }
  return <Banner message={t("CS_COMMON_COMPLAINT_NOT_SUBMITTED")} successful={false} />;
};

// ---------------------------------------------------------------------------
// Main component — pure presentation, no API calls
// ---------------------------------------------------------------------------

const Response = (props) => {
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants } = storeData || {};
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [enable, setEnable] = useState(false);

  // ── 1. Try sessionStorage first (survives refresh) ──────────────────────
  const persistedResponse = useMemo(() => {
    try {
      const raw = sessionStorage.getItem(PGR_COMPLAINT_RESPONSE);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }, []);

  // ── 2. Fall back to Redux store (available on first render, gone on refresh) ─
  const reduxResponse = useSelector((state) => state?.pgr?.complaints?.response);

  // The canonical response for this page — session takes priority
  const apiResponse = persistedResponse ?? reduxResponse ?? null;

  // ── 3. Derive the service request ID for PDF download ───────────────────
  const serviceRequestId = apiResponse?.ServiceWrappers?.[0]?.service?.serviceRequestId;

  const { data: complaintDetails } = Digit.Hooks.pgr.useComplaintDetails(
    { tenantId: "pg.citya", id: serviceRequestId },
    { enabled: enable && !!serviceRequestId }
  );

  // ── 4. Redirect to home if there is no response data at all ─────────────
  useEffect(() => {
    if (!apiResponse) {
      navigate("/upyog-ui/citizen", { replace: true });
    }
  }, [apiResponse, navigate]);

  // ── 5. PDF download ──────────────────────────────────────────────────────
  const handleDownloadPdf = async (e) => {
    e.preventDefault();
    setEnable(true);
    const tenantInfo = tenants?.find((tenant) => tenant.code === "pg.citya");
    const data = await getPGRcknowledgementData({ ...complaintDetails }, tenantInfo, t);
    Digit.Utils.pdf.generate(data);
  };

  // ── 6. Clear persisted data and navigate home ────────────────────────────
  const handleGoHome = () => {
    sessionStorage.removeItem(PGR_COMPLAINT_RESPONSE);
  };

  if (!apiResponse) return null; // briefly render nothing while redirect fires

  const workflowAction = apiResponse?.ServiceWrappers?.[0]?.workflow?.action;

  return (
    <Card>
      <BannerPicker apiResponse={apiResponse} />
      <CardText>{t("CS_COMMON_TRACK_COMPLAINT_TEXT")}</CardText>
      {workflowAction !== "RATE" && (
        <div style={{ marginBottom: "10px" }}>
          <SubmitBar label={t("PT_DOWNLOAD_ACK_FORM")} onSubmit={handleDownloadPdf} />
        </div>
      )}
      <Link to="/upyog-ui/citizen" onClick={handleGoHome}>
        <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default Response;
