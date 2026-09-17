import "../../../css/ndc.css";
import { Banner, Card, LinkButton, Loader, Row, StatusTable, SubmitBar, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";
import getNdcAcknowledgementData from "../../getNdcAcknowledgementData";
import { downloadNDCReceipt } from "../../utils";

const GetActionMessage = (props) => {
  const { t } = useTranslation();
  if (props?.isSuccess) {
    return t("ES_NDC_RESPONSE_CREATE_ACTION") !== "ES_NDC_RESPONSE_CREATE_ACTION"
      ? t("ES_NDC_RESPONSE_CREATE_ACTION")
      : "NDC Application Submitted Successfully";
  } else if (props?.isLoading) {
    return t("CS_NDC_APPLICATION_PENDING") !== "CS_NDC_APPLICATION_PENDING"
      ? t("CS_NDC_APPLICATION_PENDING")
      : "NDC Application Pending";
  }
  return t("CS_NDC_APPLICATION_FAILED") !== "CS_NDC_APPLICATION_FAILED"
    ? t("CS_NDC_APPLICATION_FAILED")
    : "NDC Application Failed";
};

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = (props) => {
  const applicationNo =
    props?.data?.Applications?.[0]?.applicationNo ||
    props?.data?.applicationNo ||
    props?.applicationNo;

  return (
    <Banner
      message={GetActionMessage(props)}
      applicationNumber={applicationNo}
      info={props?.isSuccess ? props.t("NDC_APPLICATION_NO") : ""}
      successful={props?.isSuccess}
      style={{ width: "100%" }}
    />
  );
};

/**
 * NDCResponseCitizen Component
 * 
 * Displays the acknowledgment screen after an NDC application is submitted.
 * Provides a success banner with application number and download buttons
 * for the application acknowledgement PDF and payment fee receipt.
 */
const NDCResponseCitizen = () => {
  const { t } = useTranslation();
  const { state } = useLocation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const [showToast, setShowToast] = useState(null);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants = [] } = storeData || {};

  const ndcCode = location.pathname.split("/").pop();

  const { data: fetchedData, isLoading: isSearchLoading } = Digit.Hooks.ndc.useSearchEmployeeApplication(
    { applicationNo: ndcCode },
    tenantId,
    { enabled: !state?.data && !!ndcCode }
  );

  const { data: reciept_data } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "NDC",
      consumerCodes: ndcCode,
      isEmployee: false,
    },
    { enabled: !!ndcCode }
  );

  const applicationData = state?.data || fetchedData;
  const isSuccess = state?.isSuccess !== undefined ? state?.isSuccess : (applicationData?.Applications?.length > 0 || !!applicationData);
  const isLoading = !state?.data && isSearchLoading;

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => {
        setShowToast(null);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  const handleDownloadPdf = async () => {
    try {
      const application = applicationData?.Applications?.[0] || applicationData;
      const tenantInfo = tenants.find((tenant) => tenant.code === (application?.tenantId || tenantId));
      const acknowledgementData = await getNdcAcknowledgementData(application, tenantInfo, t);
      Digit.Utils.pdf.generate(acknowledgementData);
    } catch (error) {
      console.error("Error downloading NDC acknowledgement:", error);
      setShowToast({ error: true, label: "CS_SOMETHING_WENT_WRONG" });
    }
  };

  const handleDownloadReceipt = async () => {
    try {
      const application = applicationData?.Applications?.[0] || applicationData;
      const payment = reciept_data?.Payments?.[0];
      if (payment) {
        await downloadNDCReceipt(payment.tenantId || tenantId, payment, application);
      } else {
        setShowToast({ error: true, label: "CS_RECEIPT_NOT_FOUND" });
      }
    } catch (error) {
      console.error("Error downloading NDC receipt:", error);
      setShowToast({ error: true, label: "CS_SOMETHING_WENT_WRONG" });
    }
  };

  const onGoToNDC = () => {
    navigate(`/upyog-ui/citizen/ndc/my-application`);
  };

  const handlePayment = () => {
    navigate(`/upyog-ui/citizen/payment/collect/NDC/${ndcCode}/${tenantId}?tenantId=${tenantId}`);
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <Card>
      <BannerPicker t={t} data={applicationData} applicationNo={ndcCode} isSuccess={isSuccess} isLoading={isLoading} />
      <StatusTable>
        {isSuccess && <Row rowContainerStyle={rowContainerStyle} last textStyle={{ whiteSpace: "pre", width: "60%" }} />}
      </StatusTable>

      {isSuccess && <SubmitBar label={t("CS_COMMON_DOWNLOAD_ACKNOWLEDGEMENT")} onSubmit={handleDownloadPdf} />}

      {reciept_data && reciept_data?.Payments?.length > 0 && (
        <SubmitBar label={t("NDC_FEE_RECIEPT")} onSubmit={handleDownloadReceipt} style={{ marginTop: "10px" }} />
      )}

      {isSuccess && (
        <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handlePayment} style={{ marginTop: "10px" }} />
      )}

      <SubmitBar label={t("CORE_COMMON_GO_TO_NDC")} onSubmit={onGoToNDC} style={{ marginTop: "10px" }} />

      <Link to={`/upyog-ui/citizen`}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>

      {showToast && (
        <Toast error={showToast.error} warning={showToast.warning} label={t(showToast.label)} onClose={() => setShowToast(null)} />
      )}
    </Card>
  );
};

export default NDCResponseCitizen;
