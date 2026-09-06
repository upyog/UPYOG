import { Header, MultiLink, ActionBar, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import getChbAcknowledgementData from "../../getChbAcknowledgementData";
import CHBCancelBooking from "../../components/CHBCancelBooking";

/*
    The ApplicationDetails component fetches and displays details of a community hall booking 
    based on a booking number from the URL parameter. It includes functionality for displaying 
    download options like receipt and permission letter, managing workflow details, and handling 
    PDF generation for receipts/letters. The component integrates with hooks for data fetching 
    and mutation, and provides a UI for interacting with the application details.
  */

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

const ApplicationDetails = () => {
  const { t } = useTranslation();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { tenants } = storeData || {};
  const { id: bookingNo } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [appDetailsToShow, setAppDetailsToShow] = useState({});
  const [showOptions, setShowOptions] = useState(false);
  const [enableAudit, setEnableAudit] = useState(false);
  const [businessService, setBusinessService] = useState("booking-refund");
  const [showCancelModal, setShowCancelModal] = useState(false);

  sessionStorage.setItem("chb", bookingNo);
  const { isLoading, isError, data: applicationDetails, error, refetch: refetchApplicationDetails } = Digit.Hooks.chb.useChbApplicationDetail(t, tenantId, bookingNo);

  const bookingData = applicationDetails?.applicationData?.applicationData || appDetailsToShow?.applicationData?.applicationData;
  const bookingStatus = bookingData?.bookingStatus;
  const isCancelled = bookingStatus === "CANCELLED";
  const consumerCode = bookingData?.bookingNo || bookingNo;

  const { data: reciept_data, isLoading: recieptDataLoading, refetch: refetchRecieptData } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "chb-services",
      consumerCodes: consumerCode,
      isEmployee: false,
    },
    { enabled: !!consumerCode }
  );

  const isOnline = reciept_data?.Payments?.[0]?.paymentMode === "ONLINE";
  const instrumentStatus = reciept_data?.Payments?.[0]?.instrumentStatus;
  const isRefunded = instrumentStatus === "REFUNDED";
  const originalTxnId = reciept_data?.Payments?.[0]?.transactionNumber;

  const { data: refundData, revalidate: refetchRefundData } = Digit.Hooks.useCustomAPIHook(
    "/refund-services/refund/v1/_search",
    {},
    {
      data: {
        tenantId: tenantId,
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

  const { data: pgRefundData } = Digit.Hooks.useCustomAPIHook(
    "/pg-service/refund/v1/_search",
    {
      originalTxnId: originalTxnId,
      tenantId: reciept_data?.Payments?.[0]?.tenantId || tenantId,
    },
    {},
    {},
    {
      enabled: !!(isOnline && originalTxnId),
    }
  );

  const refund = extractRefundObject(refundData) || extractRefundObject(pgRefundData);
  const hasRefund = !!(refund && (refund?.refundNo || refund?.id || refund?.status));

  const workflowBusinessId = (hasRefund && refund?.refundNo) ? refund.refundNo : (refund?.refundNo || consumerCode);
  const workflowBusinessService = (hasRefund || isCancelled) ? (refund?.businessService || "CHB.REFUND") : "CHB.REFUND";

  const user = Digit.UserService.getUser();
  const userRoles = user?.info?.roles?.map((e) => e.code) || ["CHB_CEMP"];

  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
    id: workflowBusinessId,
    moduleCode: workflowBusinessService,
    role: userRoles,
    config: {
      enabled: !!(workflowBusinessId),
    },
  });

  const mutation = Digit.Hooks.chb.useChbCreateAPI(tenantId, false);
  const { isLoading: auditDataLoading, isError: isAuditError, data, refetch } = Digit.Hooks.chb.useChbSearch(
    {
      tenantId,
      filters: { bookingNo: bookingNo, audit: true },
    },
    // { enabled: enableAudit, select: (data) => data.PetRegistrationApplications?.filter((e) => e.status === "ACTIVE") }
  );

  const closeToast = () => {
    setShowToast(null);
  };

  useEffect(() => {
    if (applicationDetails) {
      setAppDetailsToShow(_.cloneDeep(applicationDetails));

    }
  }, [applicationDetails]);

  useEffect(() => {
    if (workflowDetails?.data?.applicationBusinessService && !(workflowDetails?.data?.applicationBusinessService === "booking-refund" && businessService === "booking-refund")) {
      setBusinessService(workflowDetails?.data?.applicationBusinessService);
    }
  }, [workflowDetails.data]);

  // Show the exact refund pipeline status from the API (INITIATED, SUCCESS, etc.)
  const refundStatus = refund?.status || refund?.refundStatus;

  const isRefundSuccess = refundStatus?.toUpperCase() === "REFUNDED" ||
    refundStatus?.toUpperCase() === "SUCCESS" ||
    refundStatus?.toUpperCase() === "SUCCESSFUL" ||
    refundStatus?.toUpperCase() === "COMPLETED";
  const isRefundInProgress = !isRefundSuccess && refundStatus && (
    refundStatus.toUpperCase() === "IN_PROGRESS" ||
    refundStatus.toUpperCase() === "INPROGRESS" ||
    refundStatus.toUpperCase() === "INITIATED"
  );
  const refundBannerStyle = isRefundSuccess
    ? { backgroundColor: "#D4EDDA", border: "1px solid #C3E6CB", color: "#155724" }
    : isRefundInProgress
      ? { backgroundColor: "#FFF3CD", border: "1px solid #FFEBAA", color: "#856404" }
      : { backgroundColor: "#E2E3E5", border: "1px solid #D6D8DB", color: "#383D41" };

  useEffect(() => {
    if (refund && appDetailsToShow?.applicationData?.applicationDetails) {
      const refundSection = {
        title: "CHB_REFUND_DETAILS",
        asSectionHeader: true,
        values: [
          { title: t("CHB_REFUND_ID"), value: refund?.refundNo || refund?.refundId || t("CS_NA") },
          { title: t("CHB_REFUND_AMOUNT"), value: refund?.refundAmount ? `₹${refund.refundAmount}` : t("CS_NA") },
          { title: t("CHB_REFUND_STATUS"), value: refund?.status || t("CS_NA") }
        ],
      };

      const hasRefundSection = appDetailsToShow.applicationData.applicationDetails.some(
        (sec) => sec.title === "CHB_REFUND_DETAILS"
      );

      if (!hasRefundSection) {
        setAppDetailsToShow(prev => {
          const updatedDetails = _.cloneDeep(prev);
          updatedDetails.applicationData.applicationDetails.push(refundSection);
          return updatedDetails;
        });
      }
    }
  }, [refund, refundStatus, appDetailsToShow?.applicationData?.applicationDetails, t]);

  async function getRecieptSearch({ tenantId, payments, ...params }) {
    let application = data?.hallsBookingApplication?.[0];
    let fileStoreId = application?.paymentReceiptFilestoreId
    if (!fileStoreId) {
      let response = { filestoreIds: [payments?.fileStoreId] };
      response = await Digit.PaymentService.generatePdf(tenantId, { Payments: [{ ...payments }] }, "chbservice-receipt");
      fileStoreId = response?.filestoreIds[0];
    }
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
    window.open(fileStore[fileStoreId], "_blank");
  };

  async function getPermissionLetter({ tenantId, payments, ...params }) {
    let application = data?.hallsBookingApplication?.[0];
    let fileStoreId = application?.permissionLetterFilestoreId;
    if (!fileStoreId) {
      const response = await Digit.PaymentService.generatePdf(
        tenantId,
        { hallsBookingApplication: [application] },
        "chbpermissionletter"
      );
      fileStoreId = response?.filestoreIds[0];
    }
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
    window.open(fileStore[fileStoreId], "_blank");
  }

  const handleCancelBooking = async (data) => {
    setShowCancelModal(false);
    const bookingDetails = appDetailsToShow?.applicationData?.applicationData;
    let refundFailed = false;
    let refundErrorMessage = "";
    try {
      let paymentDetails = reciept_data?.Payments?.[0];
      if (!paymentDetails && bookingDetails?.bookingNo) {
        try {
          const res = await Digit.PaymentService.recieptSearch(
            bookingDetails?.tenantId || tenantId,
            "chb-services",
            { consumerCodes: bookingDetails?.bookingNo }
          );
          paymentDetails = res?.Payments?.[0];
        } catch (paymentErr) {
          console.error("Failed to fetch payment details for cancellation refund:", paymentErr);
        }
      }

      // =========================================================================
      // [REFUND SERVICE API INTEGRATION: /refund-services/refund/v1/_create]
      // =========================================================================
      const amountPaid = Number(
        paymentDetails?.totalAmountPaid ??
        (paymentDetails?.paymentDetails?.[0]?.totalAmountPaid ??
          (bookingDetails?.totalAmountPaid ??
            (bookingDetails?.totalAmount ?? 0)))
      );

      const refundPayload = {
        refund: {
          tenantId: bookingDetails?.tenantId || paymentDetails?.tenantId || tenantId,
          moduleName: "CHB",
          businessService: "CHB.REFUND",
          consumerCode: bookingDetails?.bookingNo,
          paymentId: paymentDetails?.id || paymentDetails?.paymentId || paymentDetails?.transactionNumber || "",
          applicantName: bookingDetails?.applicantDetail?.applicantName || bookingDetails?.applicantName || paymentDetails?.paidBy || "",
          mobileNumber: bookingDetails?.applicantDetail?.applicantMobileNo || bookingDetails?.applicantDetail?.mobileNumber || bookingDetails?.mobileNumber || paymentDetails?.mobileNumber || "",
          refundCategory: "CANCELLATION",
          refundReason: data?.cancelReason || "Community hall booking cancellation",
          paymentModeOriginal: paymentDetails?.paymentMode || "ONLINE",
          amountPaid: amountPaid,
          refundAmount: amountPaid,
          refundMode: paymentDetails?.paymentMode || "ONLINE",
          fileStoreId: paymentDetails?.fileStoreId || bookingDetails?.paymentReceiptFilestoreId || bookingDetails?.permissionLetterFilestoreId || null
        }
      };

      console.log("REFUND PAYLOAD CREATED:", JSON.stringify(refundPayload, null, 2));
      console.log("Refund Payload:", refundPayload);

      try {
        await Digit.RefundService.create(refundPayload);
      } catch (refundError) {
        refundFailed = true;
        refundErrorMessage = refundError?.response?.data?.Errors?.[0]?.message || refundError?.message || "";
        console.error("Refund API Error:", refundError);
      }

      if (refundFailed) {
        setShowToast({
          key: "error",
          error: {
            message: `${t("CHB_REFUND_CREATION_FAILED") || "Refund initiation failed"}${refundErrorMessage ? `: ${refundErrorMessage}` : ""}`
          }
        });
      } else {
        setShowToast({
          key: "success",
          action: {
            action: "REFUND_INITIATED"
          }
        });
      }
      refetchApplicationDetails?.();
      refetchRefundData?.();
      refetchRecieptData?.();
      workflowDetails?.revalidate?.();
      refetch?.();
    } catch (error) {
      setShowToast({ key: "error", error: { message: error?.response?.data?.Errors?.[0]?.message || error?.message || "Something went wrong" } });
    }
  };

  const handleActionMutate = async (data, callbacks) => {
    try {
      const workflow = data?.hallsBookingApplication?.workflow || data?.workflow || {};
      const action = workflow?.action || sessionStorage.getItem("SELECTED_ACTION");
      const comment = workflow?.comment || "";
      const documents = workflow?.documents || [];
      const assignes = workflow?.assignes || (workflow?.assignee ? [{ uuid: workflow.assignee }] : (data?.hallsBookingApplication?.assignee ? [{ uuid: data.hallsBookingApplication.assignee }] : []));

      let currentRefund = extractRefundObject(refund);
      try {
        const searchRes = await Digit.RefundService.search({
          tenantId: tenantId,
          moduleName: "CHB",
          businessService: "CHB.REFUND",
          consumerCode: consumerCode,
        });
        console.log("searchRessearchRes", searchRes)
        const foundRefund = extractRefundObject(searchRes);
        if (foundRefund) {
          currentRefund = foundRefund;
        }
      } catch (sErr) {
        console.error("Failed to search refund before action mutate:", sErr);
      }

      if (!currentRefund) {
        currentRefund = extractRefundObject(refundData) || extractRefundObject(pgRefundData);
      }

      const refundId = currentRefund?.id || refund?.id;
      const refundNo = currentRefund?.refundNo || refund?.refundNo;
      const paymentDetails = reciept_data?.Payments?.[0];
      const amountPaid = Number(
        currentRefund?.amountPaid ??
        (paymentDetails?.totalAmountPaid ??
          (paymentDetails?.paymentDetails?.[0]?.totalAmountPaid ??
            (bookingData?.totalAmountPaid ??
              (bookingData?.totalAmount ?? 0))))
      );

      const latestProcessInstance = workflowDetails?.data?.processInstances?.[0] || currentRefund?.processInstance;
      const processState = latestProcessInstance?.state || (currentRefund?.status ? { state: currentRefund.status } : undefined);

      const processInstance = {
        id: latestProcessInstance?.id || currentRefund?.processInstance?.id || undefined,
        tenantId: currentRefund?.tenantId || bookingData?.tenantId || tenantId,
        businessService: currentRefund?.businessService || "CHB.REFUND",
        businessId: refundNo || currentRefund?.consumerCode || bookingNo,
        action: action,
        moduleName: currentRefund?.moduleName || "CHB",
        state: processState,
        comment: comment,
        documents: documents,
        assignes: assignes
      };

      const updateRefundPayload = {
        refund: {
          id: refundId,
          refundNo: refundNo,
          tenantId: currentRefund?.tenantId || bookingData?.tenantId || tenantId,
          moduleName: currentRefund?.moduleName || "CHB",
          businessService: currentRefund?.businessService || "CHB.REFUND",
          consumerCode: currentRefund?.consumerCode || bookingData?.bookingNo || bookingNo,
          paymentId: currentRefund?.paymentId || paymentDetails?.id || paymentDetails?.paymentId || paymentDetails?.transactionNumber || "",
          applicantName: currentRefund?.applicantName || bookingData?.applicantDetail?.applicantName || bookingData?.applicantName || paymentDetails?.paidBy || "",
          mobileNumber: currentRefund?.mobileNumber || bookingData?.applicantDetail?.applicantMobileNo || bookingData?.applicantDetail?.mobileNumber || bookingData?.mobileNumber || paymentDetails?.mobileNumber || "",
          refundCategory: currentRefund?.refundCategory || "CANCELLATION",
          refundReason: currentRefund?.refundReason || "Community hall booking cancellation",
          paymentModeOriginal: currentRefund?.paymentModeOriginal || paymentDetails?.paymentMode || "ONLINE",
          amountPaid: amountPaid,
          refundAmount: currentRefund?.refundAmount ?? amountPaid,
          refundMode: currentRefund?.refundMode || paymentDetails?.paymentMode || "ONLINE",
          status: currentRefund?.status || undefined,
          sanctionRef: currentRefund?.sanctionRef ?? null,
          financeApprovalDate: currentRefund?.financeApprovalDate ?? null,
          gatewayRefundId: currentRefund?.gatewayRefundId ?? null,
          beneficiaryDetails: currentRefund?.beneficiaryDetails ?? null,
          additionalDetails: currentRefund?.additionalDetails ?? null,
          auditDetails: currentRefund?.auditDetails || undefined,
          fileStoreId: currentRefund?.fileStoreId || paymentDetails?.fileStoreId || bookingData?.paymentReceiptFilestoreId || bookingData?.permissionLetterFilestoreId || null,
          processInstance: processInstance
        }
      };

      console.log("REFUND UPDATE PAYLOAD:", JSON.stringify(updateRefundPayload, null, 2));
      console.log("Refund Update Payload:", updateRefundPayload);

      const res = await Digit.RefundService.update(updateRefundPayload);
      refetchApplicationDetails?.();
      refetchRefundData?.();
      refetchRecieptData?.();
      workflowDetails?.revalidate?.();
      callbacks?.onSuccess?.(res, data);
    } catch (err) {
      console.error("Refund Update Error:", err);
      callbacks?.onError?.(err, data);
    }
  };

  const dowloadOptions =
    data?.hallsBookingApplication?.[0]?.paymentReceiptFilestoreId || data?.hallsBookingApplication?.[0]?.permissionLetterFilestoreId
      ? [
        data?.hallsBookingApplication?.[0]?.paymentReceiptFilestoreId && {
          label: t("CHB_RECEIPT"),
          onClick: () => getRecieptSearch({ tenantId: tenantId }),
        },
        data?.hallsBookingApplication?.[0]?.permissionLetterFilestoreId && {
          label: t("CHB_PERMISSION_LETTER"),
          onClick: () => getPermissionLetter({ tenantId: tenantId }),
        },
      ].filter(Boolean)
      : [];

  return (
    <div>
      <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
        <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "32px" }}>{t("CHB_BOOKING_DETAILS")}</Header>
        <div style={{ zIndex: "10", display: "flex", flexDirection: "row-reverse", alignItems: "center", marginTop: "-25px" }}>
          {dowloadOptions && dowloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper employee-mulitlink-main-div"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={dowloadOptions}
              downloadBtnClassName={"employee-download-btn-className"}
              optionsClassName={"employee-options-btn-className"}
            // ref={menuRef}
            />
          )}
        </div>
      </div>
      {(isRefundInProgress || refundStatus || isRefunded) && (
        <div style={{ padding: "10px 16px", borderRadius: "4px", marginBottom: "16px", fontWeight: "bold", fontSize: "16px", ...refundBannerStyle }}>
          {t("CHB_REFUND_STATUS") || "Refund Status"} &mdash; {refundStatus || (isRefunded ? "REFUNDED" : "")}
        </div>
      )}
      <ApplicationDetailsTemplate
        applicationDetails={appDetailsToShow?.applicationData}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={appDetailsToShow?.applicationData?.applicationData}
        mutate={handleActionMutate}
        workflowDetails={workflowDetails}
        businessService={workflowBusinessService}
        moduleCode="chb-services"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        timelineStatusPrefix={""}
        forcedActionPrefix={"CHB"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />
      {/* {bookingStatus === "BOOKED" && !hasRefund && (
        <ActionBar>
          <SubmitBar label={t("CHB_CANCEL")} onSubmit={() => setShowCancelModal(true)} />
        </ActionBar>
      )} */}
      {showCancelModal && (
        <CHBCancelBooking
          t={t}
          closeModal={() => setShowCancelModal(false)}
          actionCancelLabel={"BACK"}
          actionCancelOnSubmit={() => setShowCancelModal(false)}
          actionSaveLabel={"CHB_CANCEL"}
          actionSaveOnSubmit={handleCancelBooking}
          onSubmit={handleCancelBooking}
          paymentMode={reciept_data?.Payments?.[0]?.paymentMode}
        />
      )}
    </div>
  );
};

export default React.memo(ApplicationDetails);