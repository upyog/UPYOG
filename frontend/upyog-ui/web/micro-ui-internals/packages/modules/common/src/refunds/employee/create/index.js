import React, { useEffect, useRef, useState } from "react";
import {
  Card,
  FormComposer,
  Header,
  Loader,
  Toast,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useParams, useSearchParams } from "react-router-dom";

import { getRefundFormConfig } from "./formConfig";
import {
  buildRefundPayload,
  isEligible,
  validateRefund,
} from "./utils";

export const CreateRefund = ({ setLink }) => {
  const { t } = useTranslation();
  const { refundId } = useParams();
  const [searchParams] = useSearchParams();

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const consumerCode = searchParams.get("consumerCode");

  const {
    data,
    isLoading,
    isError,
    revalidate,
  } = Digit.Hooks.useRefundSearch({
    tenantId,
    consumerCode,
    moduleName: "CHB",
    businessService: "CHB.REFUND",
  });

  useEffect(() => {
    if (setLink) {
      setLink(
        t("REFUND_OFFLINE_TITLE", {
          defaultValue: "Offline Refund",
        })
      );
    }
  }, [setLink, t]);

  if (!refundId || !consumerCode || !tenantId) {
    return (
      <Card>
        {t("REFUND_MISSING_DETAILS", {
          defaultValue:
            "Refund details are missing. Open this page from the CHB application.",
        })}
      </Card>
    );
  }

  if (isLoading) {
    return <Loader />;
  }

  if (isError) {
    return (
      <Card>
        {t("REFUND_LOAD_ERROR", {
          defaultValue:
            "Unable to load refund details. Please reopen the page.",
        })}
      </Card>
    );
  }

  const record = data?.refunds?.find(
    (refund) =>
      refund.id === refundId &&
      refund.tenantId === tenantId &&
      refund.consumerCode === consumerCode &&
      refund.moduleName === "CHB" &&
      refund.businessService === "CHB.REFUND"
  );

  if (!record) {
    return (
      <Card>
        {t("REFUND_NOT_FOUND", {
          defaultValue:
            "The selected refund was not found for this application.",
        })}
      </Card>
    );
  }

  return (
    <OfflineRefundForm
      key={`${tenantId}:${record.id}`}
      record={record}
      onCompleted={revalidate}
    />
  );
};

const OfflineRefundForm = ({ record, onCompleted }) => {
  const { t } = useTranslation();

  const [mode, setMode] = useState("CASH");
  const [amountType, setAmountType] = useState("FULL");
  const [formState, setFormState] = useState({});
  const [toast, setToast] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [completed, setCompleted] = useState(false);

  const submitLock = useRef(false);

  const mutation = Digit.Hooks.useCompleteOfflineRefund(record.tenantId);

  const tr = (key, fallback) => t(key, { defaultValue: fallback });

  const onSubmit = async (data) => {
    if (submitLock.current || completed) {
      return;
    }

    const validationError = validateRefund(data, record);

    if (validationError) {
      setToast({
        error: true,
        label: validationError,
      });
      return;
    }

    submitLock.current = true;
    setSubmitting(true);
    setToast(null);

    try {
      const payload = buildRefundPayload(data, record);

      await mutation.mutateAsync(payload);

      setCompleted(true);

      // Refresh the cached refund after successful submission.
      // A refresh failure must not be reported as an update failure.
      if (onCompleted) {
        Promise.resolve()
          .then(() => onCompleted())
          .catch(() => {});
      }
    } catch (error) {
      const apiErrors = error?.response?.data?.Errors;

      const message = apiErrors?.length
        ? apiErrors
            .map((item) => item.message || item.code)
            .join(", ")
        : error?.message ||
          tr(
            "REFUND_UPDATE_ERROR",
            "Could not confirm submission. Check the refund status before retrying."
          );

      setToast({
        error: true,
        label: message,
      });
    } finally {
      submitLock.current = false;
      setSubmitting(false);
    }
  };

  if (completed) {
    return (
      <React.Fragment>
        <Header styles={{ marginLeft: "15px" }}>
          {tr("REFUND_UPDATE_SUBMITTED", "Refund Update Submitted")}
        </Header>

        <Card>
          <p>
            {tr("REFUND_NUMBER", "Refund Number")}: {record.refundNo}
          </p>

          <p>
            {tr(
              "REFUND_UPDATE_ACCEPTED",
              "The offline refund update was submitted successfully. Check the application for its latest status."
            )}
          </p>
        </Card>
      </React.Fragment>
    );
  }

  if (!isEligible(record)) {
    return (
      <React.Fragment>
        <Header styles={{ marginLeft: "15px" }}>
          {tr("REFUND_OFFLINE_TITLE", "Offline Refund")}
        </Header>

        <Card>
          <p>
            {tr("REFUND_NUMBER", "Refund Number")}: {record.refundNo}
          </p>

          <p>
            {tr("REFUND_STATUS", "Status")}: {record.status}
          </p>

          <p>
            {tr("REFUND_CHANNEL", "Refund Channel")}: {record.refundMode}
          </p>

          <p>
            {tr(
              "REFUND_NOT_ELIGIBLE",
              "This refund is not currently eligible for offline completion."
            )}
          </p>
        </Card>
      </React.Fragment>
    );
  }

  return (
    <React.Fragment>
      <Header styles={{ marginLeft: "15px" }}>
        {tr("REFUND_OFFLINE_TITLE", "Offline Refund")}
      </Header>

      <FormComposer
        cardStyle={{ paddingBottom: "100px" }}
        label={
          submitting
            ? tr("REFUND_SUBMITTING", "Submitting...")
            : tr("REFUND_COMPLETE", "Complete Refund")
        }
        config={getRefundFormConfig({
          record,
          t,
          mode,
          amountType,
          setMode,
          setAmountType,
        })}
        formState={formState}
        defaultValues={{
          recipientName: record.applicantName || "",
          recipientMobile: record.mobileNumber || "",
        }}
        onFormValueChange={(_, values) => {
          setFormState((previous) => {
            const sameMode =
              previous.refundMode?.code === values.refundMode?.code;

            const sameAmountType =
              previous.amountType?.code === values.amountType?.code;

            return sameMode && sameAmountType ? previous : values;
          });
        }}
        onSubmit={onSubmit}
        isDisabled={submitting}
      />

      {toast && (
        <Toast
          error={toast.error}
          label={toast.label}
          onClose={() => setToast(null)}
        />
      )}
    </React.Fragment>
  );
};