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

  const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.chb.useChbApplicationAction(tenantId);
  let workflowDetails = Digit.Hooks.useWorkflowDetails({
    tenantId: applicationDetails?.applicationData?.tenantId || tenantId,
    id: applicationDetails?.applicationData?.applicationData?.bookingNo,
    moduleCode: businessService,
    role: ["CHB_CEMP"],
    config: {
      enabled: !!(applicationDetails?.applicationData?.applicationData?.bookingNo),
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


  const { data: reciept_data, isLoading: recieptDataLoading, refetch: refetchRecieptData } = Digit.Hooks.useRecieptSearch(
    {
      tenantId: tenantId,
      businessService: "chb-services",
      consumerCodes: appDetailsToShow?.applicationData?.applicationData?.bookingNo,
      isEmployee: false,
    },
    { enabled: appDetailsToShow?.applicationData?.applicationData?.bookingNo ? true : false }
  );

  const isCancelled = appDetailsToShow?.applicationData?.applicationData?.bookingStatus === "CANCELLED";
  const isOnline = reciept_data?.Payments?.[0]?.paymentMode === "ONLINE";
  // instrumentStatus is the authoritative final state from the payment gateway
  const instrumentStatus = reciept_data?.Payments?.[0]?.instrumentStatus;
  const isRefunded = instrumentStatus === "REFUNDED";
  const originalTxnId = reciept_data?.Payments?.[0]?.transactionNumber;

  const { data: refundData } = Digit.Hooks.useCustomAPIHook(
    "/pg-service/refund/v1/_search",
    {
      originalTxnId: originalTxnId,
      tenantId: reciept_data?.Payments?.[0]?.tenantId || tenantId,
    },
    {},
    {},
    {
      enabled: !!(isCancelled && isOnline && originalTxnId),
    }
  );

  const refund = refundData?.Refund?.[0] || refundData?.Refunds?.[0] || refundData?.[0];
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
          { title: t("CHB_REFUND_ID"), value: refund?.refundId || t("CS_NA") },
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
      const updatedApplication = {
        ...application,
        paymentReceiptFilestoreId: response?.filestoreIds[0]
      };
      await mutation.mutateAsync({
        hallsBookingApplication: updatedApplication
      });
      fileStoreId = response?.filestoreIds[0];
      refetch();
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
      const updatedApplication = {
        ...application,
        permissionLetterFilestoreId: response?.filestoreIds[0]
      };
      await mutation.mutateAsync({
        hallsBookingApplication: updatedApplication
      });
      fileStoreId = response?.filestoreIds[0];
      refetch();
    }
    const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: fileStoreId });
    window.open(fileStore[fileStoreId], "_blank");
  }

  const handleCancelBooking = async (data) => {
    setShowCancelModal(false);
    const bookingDetails = appDetailsToShow?.applicationData?.applicationData;
    const updatedApplication = {
      ...bookingDetails,
      bookingStatus: "CANCELLED",
      additionalDetails: {
        ...bookingDetails?.additionalDetails,
        cancellationReason: data?.cancelReason || ""
      }
    };
    let refundFailed = false;
    let refundErrorMessage = "";
    try {
      const paymentDetails = reciept_data?.Payments?.[0];
      if (paymentDetails && paymentDetails.paymentMode === "ONLINE") {
        try {
          const refundPayload = {
            PaymentWorkflows: [
              {
                paymentId: paymentDetails.id,
                action: "REFUND",
                tenantId: paymentDetails.tenantId || tenantId,
                reason: data?.cancelReason || "Customer requested refund"
              }
            ]
          };
          await Digit.ReceiptsService.update(refundPayload, paymentDetails.tenantId || tenantId, "CHB");
        } catch (refundError) {
          refundFailed = true;
          refundErrorMessage = refundError?.response?.data?.Errors?.[0]?.message || refundError?.message || "";
        }
      }

      await mutation.mutateAsync({
        hallsBookingApplication: updatedApplication
      });
      if (refundFailed) {
        setShowToast({
          key: "warning",
          error: {
            message: `${t("CHB_CANCELLATION_SUCCESS_BUT_REFUND_FAILED") || "Booking cancelled, but refund initiation failed"}${refundErrorMessage ? `: ${refundErrorMessage}` : ""}`
          }
        });
      }
    } catch (error) {
      setShowToast({ key: "error", error: { message: error?.response?.data?.Errors?.[0]?.message || error?.message || "Something went wrong" } });
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
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService={businessService}
        moduleCode="chb-services"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        timelineStatusPrefix={""}
        forcedActionPrefix={"CHB"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
      />
      {appDetailsToShow?.applicationData?.applicationData?.bookingStatus === "BOOKED" && (
        <ActionBar>
          <SubmitBar label={t("CHB_CANCEL")} onSubmit={() => setShowCancelModal(true)} />
        </ActionBar>
      )}
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