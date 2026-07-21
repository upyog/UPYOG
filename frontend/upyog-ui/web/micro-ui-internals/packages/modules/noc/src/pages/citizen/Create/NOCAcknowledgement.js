/* 
 * Fire NOC Application Acknowledgement Page (NOCAcknowledgement.js)
 * Displays application submission status (success banner, application number, and download receipts/options).
 * Clears intermediate form wizard state from session storage upon successful backend payload creation.
 */
import { Banner, Card, CardText, LinkButton, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { convertToFireNOCPayload, getNocAcknowledgementData } from "../../../utils";

const NOCAcknowledgement = ({ data, onSuccess, onUpdateSuccess, clearParams }) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [tenantId] = useState(() => data?.property?.location?.property?.city?.code || data?.location?.city?.code || Digit.ULBService.getCurrentTenantId());

  // Determine if it is a new application vs update flow
  const [isUpdate] = useState(() => !!(data?.id || data?.applicationNumber || data?.fireNOCNumber));

  // Local submission state
  const [submissionState, setSubmissionState] = useState(() => {
    const successApp = Digit.SessionStorage.get("NOC_SUCCESSFUL_APPLICATION");
    if (successApp) {
      return {
        isLoading: false,
        isSuccess: true,
        isError: false,
        application: successApp,
        error: null
      };
    }
    return {
      isLoading: true,
      isSuccess: false,
      isError: false,
      application: null,
      error: null
    };
  });

  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  Digit.Hooks.useCustomBackNavigation({
    redirectPath: `${modulePath}/home`,
    enableConfirmation: false
  });

  const hasSubmitted = useRef(false);
  const isSuccessRef = useRef(false);
  isSuccessRef.current = submissionState.isSuccess;

  useEffect(() => {
    if (submissionState.isSuccess) return;
    if (hasSubmitted.current) return;
    hasSubmitted.current = true;

    const submitApplication = async () => {
      try {
        setSubmissionState({ isLoading: true, isSuccess: false, isError: false, application: null, error: null });

        let finalApp = null;
        if (!isUpdate) {
          // New Application: First Stage (INITIATE)
          const createPayload = convertToFireNOCPayload(data, {
            action: "INITIATE",
            isUpdate: false
          });
          const createRes = await Digit.NOCService.create(createPayload, tenantId);
          const createdApp = createRes?.FireNOCs?.[0];

          // Second Stage: Update (APPLY)
          const updatePayload = convertToFireNOCPayload(data, {
            action: "APPLY",
            isUpdate: true,
            existingApplication: createdApp
          });
          const updateRes = await Digit.NOCService.update(updatePayload, tenantId);
          finalApp = updateRes?.FireNOCs?.[0];
        } else {
          // Existing Application: Direct update to APPLY
          const updatePayload = convertToFireNOCPayload(data, {
            action: "APPLY",
            isUpdate: true,
            existingApplication: data.existingApplication
          });
          const updateRes = await Digit.NOCService.update(updatePayload, tenantId);
          finalApp = updateRes?.FireNOCs?.[0];
        }

        queryClient.invalidateQueries({ queryKey: ["FIRENOC_SEARCH"] });

        setSubmissionState({
          isLoading: false,
          isSuccess: true,
          isError: false,
          application: finalApp || data?.existingApplication,
          error: null
        });

        Digit.SessionStorage.set("NOC_SUCCESSFUL_APPLICATION", finalApp || data?.existingApplication);

        if (isUpdate && onUpdateSuccess) onUpdateSuccess();
        if (!isUpdate && onSuccess) onSuccess();
      } catch (err) {
        console.error("Submission error caught in state handler:", err);

        const errMsg = err?.response?.data?.Errors?.[0]?.message || err?.message || "";
        if (errMsg.includes("Action APPLY not found")) {
          // If already submitted previously (Strict Mode double run), treat as success
          setSubmissionState({
            isLoading: false,
            isSuccess: true,
            isError: false,
            application: data?.existingApplication,
            error: null
          });
          Digit.SessionStorage.set("NOC_SUCCESSFUL_APPLICATION", data?.existingApplication);
          if (isUpdate && onUpdateSuccess) onUpdateSuccess();
          if (!isUpdate && onSuccess) onSuccess();
        } else {
          setSubmissionState({
            isLoading: false,
            isSuccess: false,
            isError: true,
            application: null,
            error: err
          });
        }
      }
    };

    submitApplication();

    return () => {
      if (isSuccessRef.current) {
        Digit.SessionStorage.del("NOC_CREATE_APPLICATION");
        queryClient.invalidateQueries({ queryKey: ["NOC_CREATE_APPLICATION"] });
      }
    };
  }, []);

  const { isLoading, isSuccess, isError, application, error: submissionError } = submissionState;

  useEffect(() => {
    if (isSuccess && clearParams) {
      clearParams();
    }
  }, [isSuccess]);

  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const tenants = storeData?.tenants || [];

  const handleDownloadPdf = async () => {
    try {
      const tenantInfo = tenants.find((t) => t.code === tenantId);
      const dataObj = await getNocAcknowledgementData(application, tenantInfo, t);
      Digit.Utils.pdf.generate(dataObj);
    } catch (err) {
      console.error("Failed to generate PDF download:", err);
    }
  };

  if (isLoading) {
    return <Loader />;
  }

  if (!isSuccess) {
    const errorMsg = submissionError?.response?.data?.Errors?.[0]?.message ||
      submissionError?.message;
    return (
      <Card>
        <Banner
          message={t("NOC_APPLICATION_FAILED_MESSAGE")}
          info=""
          successful={false}
        />
        <CardText>{t("NOC_APPLICATION_FAILED_TEXT")}</CardText>
        {errorMsg && (
          <CardText style={{ color: "red", fontWeight: "bold", marginTop: "10px" }}>
            Error Details: {errorMsg}
          </CardText>
        )}
        <Link to="/upyog-ui/citizen">
          <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </Card>
    );
  }

  return (
    <Card>
      <Banner
        message={isUpdate ? t("NOC_UPDATE_APPLICATION_SUCCESS") : t("NOC_APPLICATION_SUCCESS_MESSAGE")}
        applicationNumber={application?.fireNOCDetails?.applicationNumber || data?.applicationNumber || data?.fireNOCNumber || data?.existingApplication?.fireNOCDetails?.applicationNumber}
        info={t("NOC_APPLICATION_NUMBER_LABEL")}
        successful={true}
      />
      <CardText>
        {isUpdate ? t("NOC_APPLICATION_UPDATED_TEXT") : t("NOC_APPLICATION_SUCCESS_TEXT")}
      </CardText>
      <div style={{ display: "flex", flexDirection: "row", gap: "20px", marginTop: "20px" }}>
        <div style={{ flex: 1 }}>
          <SubmitBar label={t("NOC_DOWNLOAD_ACK")} onSubmit={handleDownloadPdf} style={{ width: "100%" }} />
        </div>
        {application?.fireNOCDetails?.status === "PENDINGPAYMENT" && (
          <Link to={{
            pathname: `/upyog-ui/citizen/payment/collect/FIRENOC/${application?.fireNOCDetails?.applicationNumber || data?.applicationNumber || data?.fireNOCNumber || data?.existingApplication?.fireNOCDetails?.applicationNumber}`,
            state: { tenantId: application?.tenantId || tenantId }
          }} style={{ flex: 1 }}>
            <SubmitBar label={t("COMMON_MAKE_PAYMENT")} style={{ width: "100%" }} />
          </Link>
        )}
      </div>
      <div style={{ display: "flex", flexDirection: "row", justifyContent: "flex-end", marginTop: "16px" }}>
        <Link to="/upyog-ui/citizen">
          <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} onClick={() => {
            Digit.SessionStorage.del("NOC_CREATE_APPLICATION");
            Digit.SessionStorage.del("NOC_SUCCESSFUL_APPLICATION");
          }} style={{ margin: 0 }} />
        </Link>
      </div>
    </Card>
  );
};

export default NOCAcknowledgement;
