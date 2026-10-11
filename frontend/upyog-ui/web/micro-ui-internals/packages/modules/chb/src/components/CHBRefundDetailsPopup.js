import React from "react";
import { Modal, Card, StatusTable, Row, Loader } from "@nudmcdgnpm/digit-ui-react-components";

const Heading = (props) => {
  return <h1 className="heading-m">{props.label}</h1>;
};

const Close = () => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
    <path d="M0 0h24v24H0V0z" fill="none" />
    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
  </svg>
);

const CloseBtn = (props) => {
  return (
    <div className="icon-bg-secondary" onClick={props.onClick}>
      <Close />
    </div>
  );
};

const extractRefundObject = (res) => {
  if (!res) return null;
  if (res.id || res.refundNo) return res;
  if (res.data) return extractRefundObject(res.data);
  if (Array.isArray(res) && res.length > 0) {
    return extractRefundObject(res[0]);
  }
  if (res.refund) return extractRefundObject(res.refund);
  if (res.Refund) return extractRefundObject(res.Refund);
  if (Array.isArray(res.Refunds) && res.Refunds.length > 0) return extractRefundObject(res.Refunds[0]);
  if (Array.isArray(res.refunds) && res.refunds.length > 0) return extractRefundObject(res.refunds[0]);
  return null;
};

const CHBRefundDetailsPopup = ({ t, closeModal, applicationData, tenantId }) => {
  const consumerCode = applicationData?.bookingNo;
  const targetTenantId = applicationData?.tenantId || tenantId;

  const { data: refundData, isLoading: isRefundLoading } = Digit.Hooks.useCustomAPIHook(
    "/refund-services/refund/v1/_search",
    {},
    {
      data: {
        tenantId: targetTenantId,
        moduleName: "CHB",
        businessService: "CHB.REFUND",
        consumerCode: consumerCode,
      },
    },
    {},
    {
      enabled: !!consumerCode,
    }
  );

  const { data: recieptData, isLoading: isReceiptLoading } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: targetTenantId,
      businessService: "chb-services",
      consumerCodes: consumerCode,
      isEmployee: false,
    },
    { enabled: !!consumerCode }
  );

  const isOnline = recieptData?.Payments?.[0]?.paymentMode === "ONLINE";
  const originalTxnId = recieptData?.Payments?.[0]?.transactionNumber;

  const { data: pgRefundData, isLoading: isPgRefundLoading } = Digit.Hooks.useCustomAPIHook(
    "/pg-service/refund/v1/_search",
    {
      originalTxnId: originalTxnId,
      tenantId: recieptData?.Payments?.[0]?.tenantId || targetTenantId,
    },
    {},
    {},
    {
      enabled: !!(isOnline && originalTxnId),
    }
  );

  const serviceRefund = extractRefundObject(refundData);
  const refund = serviceRefund || extractRefundObject(pgRefundData);

  const refundStatus = refund?.status || refund?.refundStatus;

  const statusUpper = refundStatus?.toUpperCase() || "";
  const isRefundSuccess = Boolean(
    statusUpper === "REFUNDED" ||
    statusUpper === "REFUND_COMPLETED" ||
    statusUpper === "COMPLETED" ||
    statusUpper === "SUCCESS" ||
    statusUpper === "SUCCESSFUL" ||
    statusUpper === "REFUND_APPROVED" ||
    statusUpper === "APPROVED" ||
    statusUpper === "SETTLED" ||
    statusUpper.includes("COMPLETE") ||
    statusUpper.includes("REFUNDED") ||
    statusUpper.includes("SUCCESS")
  );

  const isRefundInProgress =
    !isRefundSuccess &&
    Boolean(
      refundStatus &&
      (statusUpper === "IN_PROGRESS" ||
        statusUpper === "INPROGRESS" ||
        statusUpper === "INITIATED" ||
        statusUpper === "REFUND_INITIATED" ||
        statusUpper.includes("INITIAT") ||
        statusUpper.includes("PROGRESS"))
    );

  const refundBannerStyle = isRefundSuccess
    ? { backgroundColor: "#D4EDDA", border: "1px solid #C3E6CB", color: "#155724" }
    : isRefundInProgress
      ? { backgroundColor: "#FFF3CD", border: "1px solid #FFEBAA", color: "#856404" }
      : { backgroundColor: "#E2E3E5", border: "1px solid #D6D8DB", color: "#383D41" };

  const isLoading = isRefundLoading || (isOnline && isPgRefundLoading);

  return (
    <Modal
      headerBarMain={<Heading label={t("CHB_REFUND_DETAILS") || "Refund Details"} />}
      headerBarEnd={<CloseBtn onClick={closeModal} />}
      // actionCancelLabel={t("CS_COMMON_BACK") || "Back"}
      // actionCancelOnSubmit={closeModal}
      actionSaveLabel={"Close"}
      actionSaveOnSubmit={closeModal}
      popupStyles={{ maxWidth: "650px", width: "100%" }}
    >
      {isLoading ? (
        <Loader />
      ) : refund ? (
        <Card style={{ border: "none", boxShadow: "none", padding: "10px 0", margin: 0 }}>
          <div
            style={{
              padding: "10px 16px",
              borderRadius: "4px",
              marginBottom: "16px",
              fontWeight: "bold",
              fontSize: "16px",
              ...refundBannerStyle,
            }}
          >
            {t("CHB_REFUND_STATUS") || "Refund Status"} &mdash; {t(refundStatus) || t("CS_NA")}
          </div>
          <StatusTable>
            <Row
              label={t("CHB_REFUND_ID") || "Refund ID"}
              text={refund?.refundNo || refund?.refundId || t("CS_NA")}
              textStyle={{ fontWeight: "600" }}
            />
            <Row
              label={t("CHB_REFUND_AMOUNT") || "Refund Amount"}
              text={
                refund?.refundAmount
                  ? `₹${refund.refundAmount}`
                  : refund?.amountPaid
                    ? `₹${refund.amountPaid}`
                    : t("CS_NA")
              }
              textStyle={{ fontWeight: "600" }}
            />
            <Row
              label={t("CHB_REFUND_STATUS") || "Refund Status"}
              text={t(refundStatus) || t("CS_NA")}
              textStyle={{ fontWeight: "600" }}
            />
            {refund?.refundMode && (
              <Row label={t("CHB_REFUND_MODE") || "Refund Mode"} text={refund.refundMode} />
            )}
            {refund?.paymentModeOriginal && (
              <Row
                label={t("CHB_ORIGINAL_PAYMENT_MODE") || "Original Payment Mode"}
                text={refund.paymentModeOriginal}
              />
            )}
            <Row
              label={t("CHB_BOOKING_NO") || "Booking No"}
              text={refund?.consumerCode || applicationData?.bookingNo || t("CS_NA")}
            />
            <Row
              label={t("CHB_APPLICANT_NAME") || "Applicant Name"}
              text={
                refund?.applicantName ||
                applicationData?.applicantDetail?.applicantName ||
                applicationData?.applicantName ||
                t("CS_NA")
              }
            />
            {(refund?.mobileNumber ||
              applicationData?.applicantDetail?.applicantMobileNo ||
              applicationData?.mobileNumber) && (
                <Row
                  label={t("CHB_MOBILE_NUMBER") || "Mobile Number"}
                  text={
                    refund?.mobileNumber ||
                    applicationData?.applicantDetail?.applicantMobileNo ||
                    applicationData?.mobileNumber ||
                    t("CS_NA")
                  }
                />
              )}
            {refund?.refundReason && (
              <Row label={t("CHB_REFUND_REASON") || "Refund Reason"} text={refund.refundReason} />
            )}
          </StatusTable>
        </Card>
      ) : (
        <Card style={{ border: "none", boxShadow: "none", padding: "20px 0", textAlign: "center", margin: 0 }}>
          <p style={{ fontSize: "16px", color: "#666" }}>
            {t("CHB_NO_REFUND_DETAILS_FOUND") || "No refund details found for this booking."}
          </p>
        </Card>
      )}
    </Modal>
  );
};

export default CHBRefundDetailsPopup;
