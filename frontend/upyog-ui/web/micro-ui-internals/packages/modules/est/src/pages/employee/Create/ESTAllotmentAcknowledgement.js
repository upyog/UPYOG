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
import getESTAllotmentAcknowledgementData from "../../../utils/getESTAllotmentAcknowledgementData";
import { ESTDocumnetPreview } from "../../../utils";
import { fetchAllotmentDocumentPreviews } from "../../../utils/allotmentDocumentUtils";
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
  const applicationNumber = allotment?.allotmentId || allotment?.assetNo || "";
  return (
    <Banner
      message={isSuccess ? t("EST_ALLOTED_SUCCESSFULL") : t("EST_APPLICATION_FAILED")}
      applicationNumber={applicationNumber}
      info={isSuccess ? t(allotment?.allotmentId ? "EST_ALLOTMENT_ID" : "EST_APPLICATION_NO") : ""}
      successful={isSuccess}
      style={{ width: "100%" }}
    />
  );
};

const ESTAllotmentAcknowledgement = ({ onSuccess }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const { data: ackData, isSuccess = false, error } = location?.state || {};

  const user = Digit?.UserService?.getUser?.()?.info || {};
  const initResponse = Digit?.Hooks?.useStore?.getInitData?.() || {};
  const storeData = initResponse?.data || initResponse;
  const tenants = storeData?.tenants || [];

  const [previewDocs, setPreviewDocs] = useState([]);
  const [loadingDocs, setLoadingDocs] = useState(false);

  useEffect(() => {
    if (isSuccess && typeof onSuccess === "function") {
      onSuccess(ackData);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!isSuccess || !ackData?.Allotments?.[0]) {
      setPreviewDocs([]);
      return;
    }

    let mounted = true;
    setLoadingDocs(true);

    fetchAllotmentDocumentPreviews(ackData.Allotments[0], t)
      .then((docs) => {
        if (mounted) setPreviewDocs(docs);
      })
      .catch((err) => {
        console.error("EST ack document preview failed:", err);
        if (mounted) setPreviewDocs([]);
      })
      .finally(() => {
        if (mounted) setLoadingDocs(false);
      });

    return () => {
      mounted = false;
    };
  }, [ackData, isSuccess, t]);

  if (error) console.error("EST Allotment Acknowledgement — error:", error);

  const handleDownloadPdf = async () => {
    try {
      const allotment = ackData?.Allotments?.[0];
      if (!allotment) return;
      const tenantInfo = tenants.find((tn) => tn.code === allotment.tenantId) || {};
      const pdfData = await getESTAllotmentAcknowledgementData(ackData, tenantInfo, t);
      Digit.Utils.pdf.generate(pdfData);
    } catch (err) {
      console.error("PDF generation error:", err);
    }
  };

  const homePath =
    user?.type === "CITIZEN"
      ? getCitizenHomeFromModulePath(modulePath)
      : getEmployeeHomeFromModulePath(modulePath);

  const assetNo =
    ackData?.Allotments?.[0]?.assetNo ||
    ackData?.Assets?.[0]?.estateNo ||
    ackData?.Assets?.[0]?.assetNo ||
    "";

  const paymentPath = assetNo
    ? user?.type === "CITIZEN"
      ? getCitizenPaymentPath(assetNo)
      : getEmployeePaymentCollectPath(assetNo)
    : "";

  return (
    <Card>
      <BannerPicker t={t} isSuccess={isSuccess} data={ackData} />

      <StatusTable>
        <Row rowContainerStyle={rowContainerStyle} last />
      </StatusTable>

      {isSuccess && (
        <>
          <CardSubHeader>{t("EST_DOCUMENT_PREVIEW")}</CardSubHeader>
          {loadingDocs ? (
            <div style={{ padding: "12px 16px" }}>
              <Loader />
            </div>
          ) : (
            <ESTDocumnetPreview documents={previewDocs} useThumbnails thumbSize={80} />
          )}
        </>
      )}

      {isSuccess && (
        <SubmitBar label={t("EST_ALLOTMENT_ACKNOWLEDGEMENT")} onSubmit={handleDownloadPdf} />
      )}

      {isSuccess && paymentPath && (
        <Link to={paymentPath}>
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
