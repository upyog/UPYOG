import { Banner, Card, SubmitBar, Toast, ActionBar, LinkButton } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, Link } from "react-router-dom";
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

/**
 * Employee NDC Response Component
 * 
 * Displays the status of an NDC application upon submission for employee workflows.
 * Enables downloading of the application acknowledgement PDF, fee receipt, and navigation.
 */
const Response = (props) => {
  const location = useLocation();
  const state = props?.location?.state || location?.state || {};
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = window.localStorage.getItem("Employee.tenant-id") || Digit.ULBService.getCurrentTenantId();
  const [showToast, setShowToast] = useState(null);
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { tenants = [] } = storeData || {};

  const pathname = window.location.pathname || "";
  const ndcCode = pathname.split("/").pop();

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
      isEmployee: true,
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

  const onSubmit = () => {
    navigate(`/upyog-ui/employee`);
  };

  const onGoToNDC = () => {
    navigate(`/upyog-ui/employee/ndc/inbox`);
  };

  const handlePayment = () => {
    navigate(
      `/upyog-ui/employee/payment/collect/NDC/${ndcCode}/${tenantId}?tenantId=${tenantId}`
    );
  };

  return (
    <div>
      <Card>
        <Banner
          message={GetActionMessage({ isSuccess, isLoading, t })}
          applicationNumber={ndcCode}
          info={isSuccess ? t(`NDC_APPROVAL_NUMBER`) : ""}
          successful={isSuccess}
          style={{ padding: "10px" }}
          headerStyles={{ fontSize: "32px", wordBreak: "break-word" }}
        />

        {isSuccess && (
          <SubmitBar
            label={t("CS_COMMON_DOWNLOAD_ACKNOWLEDGEMENT")}
            onSubmit={handleDownloadPdf}
            style={{ marginBottom: "16px" }}
          />
        )}

        {reciept_data && reciept_data?.Payments?.length > 0 && (
          <SubmitBar
            label={t("NDC_FEE_RECIEPT")}
            onSubmit={handleDownloadReceipt}
            style={{ marginBottom: "16px" }}
          />
        )}

        <ActionBar className="challan-emp-acknowledgement">
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} onSubmit={onSubmit} />
          <SubmitBar label={t("CORE_COMMON_GO_TO_NDC")} onSubmit={onGoToNDC} />
          <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} onSubmit={handlePayment} />
        </ActionBar>

        {showToast && (
          <Toast error={showToast.error} warning={showToast.warning} label={t(showToast.label)} onClose={() => setShowToast(null)} />
        )}
      </Card>
    </div>
  );
};

export default Response;
