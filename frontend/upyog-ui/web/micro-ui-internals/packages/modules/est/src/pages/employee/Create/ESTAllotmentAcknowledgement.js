/**
 * ESTAllotmentAcknowledgement
 * ---------------------------
 * Displays the result of the EST allotment API call.
 * The API call is made in ESTAssignAssetsCheckPage; on success/failure,
 * AssignAssetIndex navigates here with location.state.
 */

import React, { useEffect } from "react";
import {
  Banner,
  Card,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";
import getESTAllotmentAcknowledgementData from "../../../utils/getESTAllotmentAcknowledgementData";
import { getEmployeeHomeFromModulePath, getCitizenHomeFromModulePath } from "../../../utils/estRoutes";

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = ({ t, isSuccess, data }) => {
  const applicationNumber = data?.Allotments?.[0]?.assetNo || "";
  return (
    <Banner
      message={isSuccess ? t("EST_ALLOTED_SUCCESSFULL") : t("EST_APPLICATION_FAILED")}
      applicationNumber={applicationNumber}
      info={isSuccess ? t("EST_APPLICATION_NO") : ""}
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

  useEffect(() => {
    if (isSuccess && typeof onSuccess === "function") {
      onSuccess(ackData);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

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

  return (
    <Card>
      <BannerPicker t={t} isSuccess={isSuccess} data={ackData} />

      <StatusTable>
        <Row rowContainerStyle={rowContainerStyle} last />
      </StatusTable>

      {isSuccess && (
        <SubmitBar label={t("EST_ALLOTMENT_ACKNOWLEDGEMENT")} onSubmit={handleDownloadPdf} />
      )}

      <Link to={homePath}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default ESTAllotmentAcknowledgement;
