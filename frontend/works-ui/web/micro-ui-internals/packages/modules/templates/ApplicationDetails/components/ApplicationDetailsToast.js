import React from "react";
import { Toast } from "@nudmcdgnpm/digit-ui-react-components";

const extractErrorMessages = (error, t) => {
  const apiErrors =
    error?.response?.data?.Errors ||
    error?.Errors ||
    (Array.isArray(error) ? error : null);

  if (Array.isArray(apiErrors) && apiErrors.length > 0) {
    const messages = apiErrors
      .map((err) => err?.message || err?.description || (err?.code ? t(err.code) : null))
      .filter(Boolean);
    if (messages.length > 0) return messages;
  }

  const single = error?.message || (typeof error === "string" ? error : null);
  return single ? [single] : [];
};

function ApplicationDetailsToast({ t, showToast, closeToast, businessService }) {
  if (businessService?.includes("NewTL") || businessService?.includes("TL") || businessService?.includes("EDITRENEWAL")) {
    let label = "";
    switch (showToast?.action?.action) {
      case "SENDBACK":
        label = showToast?.key === "error" ? showToast?.error?.message : t("TL_SENDBACK_CHECKLIST_MESSAGE_HEAD");
        break;
      case "FORWARD":
        label = showToast?.key === "error" ? showToast?.error?.message : t("TL_FORWARD_SUCCESS_MESSAGE_MAIN");
        break;
      case "APPROVE":
        label = showToast?.key === "error" ? showToast?.error?.message : t("TL_APPROVAL_CHECKLIST_MESSAGE_HEAD");
        break;
      case "SENDBACKTOCITIZEN":
        label = showToast?.key === "error" ? showToast?.error?.message : t("TL_SENDBACK_TOCITIZEN_CHECKLIST_MESSAGE_HEAD");
        break;
      case "REJECT":
        label = showToast?.key === "error" ? showToast?.error?.message : t("TL_APPROVAL_REJ_MESSAGE_HEAD");
        break;
      case "RESUBMIT":
        label = showToast?.key === "error" ? showToast?.error?.message : t("TL_APPLICATION_RESUBMIT_SUCCESS_MESSAGE_MAIN");
        break;
      case "CANCEL":
        label = showToast?.key === "error" ? showToast?.error?.message : t("TL_TL_CANCELLED_MESSAGE_HEAD");
        break;
      default:
        label = showToast?.key === "error" ? showToast?.error?.message : t(`ES_${businessService}_${showToast?.action?.action}_UPDATE_SUCCESS`);
    }
    return <React.Fragment>{showToast && <Toast error={showToast.key === "error"} label={label} onClose={closeToast} />}</React.Fragment>;
  } else if (businessService?.includes("BPA") || businessService?.includes("BPA_LOW") || businessService?.includes("BPA_OC")) {
    const getMessage = (messages = []) => {
      let returnValue = messages[0];
      if(messages?.length == 2) returnValue = businessService?.includes("BPA_OC") ? t(messages[1]) : t(messages [0]);
      else returnValue = t(messages[0]);
      return returnValue;
    }
    let label = "";
    switch (showToast?.action?.action) {
      case "REVOCATE":
        label = showToast?.key === "error" ? showToast?.error?.message : getMessage(["BPA_APPROVAL_REVOCATED_MESSAGE_HEAD", "BPA_APPROVAL_OC_REVOCATED_MESSAGE_HEAD"]);
        break;
      case "VERIFY_AND_FORWARD":
        label = showToast?.key === "error" ? showToast?.error?.message : getMessage(["BPA_FORWARD_SUCCESS_MESSAGE_MAIN"]);
        break;
      case "SEND_BACK_TO_CITIZEN":
        label = showToast?.key === "error" ? showToast?.error?.message : getMessage(["BPA_SENDBACK_SUCCESS_MESSAGE_MAIN"]);
        break;
      case "APPROVE":
        label = showToast?.key === "error" ? showToast?.error?.message : getMessage(["BPA_APPROVAL_CHECKLIST_MESSAGE_HEAD"]);
        break;
      case "REJECT":
        label = showToast?.key === "error" ? showToast?.error?.message : getMessage(["BPA_APPROVAL_REJECTED_MESSAGE_HEAD", "BPA_OC_APPROVAL_REJECTED_MESSAGE_HEAD"]);
        break;
      case "FORWARD":
        label = showToast?.key === "error" ? showToast?.error?.message : getMessage(["BPA_FORWARD_SUCCESS_MESSAGE_MAIN"]);
        break;
      case "SEND_BACK_FOR_DOCUMENT_VERIFICATION":
      case "SEND_BACK_FOR_FIELD_INSPECTION":
        label = showToast?.key === "error" ? showToast?.error?.message : getMessage(["BPA_SENDBACK_SUCCESS_MESSAGE_MAIN"]);
        break;
      default:
        label = showToast?.key === "error" ? showToast?.error?.message : t(`ES_${businessService}_${showToast?.action?.action}_UPDATE_SUCCESS`);
    }
    return <React.Fragment>{showToast && <Toast error={showToast.key === "error"} label={label} onClose={closeToast} />}</React.Fragment>;
  } else {
    if (showToast?.key === "error") {
      const errorMessages = extractErrorMessages(showToast?.error, t);
      const errorLabel =
        showToast?.message ||
        (errorMessages.length > 1
          ? errorMessages.map((msg) => `• ${msg}`).join("\n")
          : errorMessages[0] || t("CS_SOMETHING_WENT_WRONG", "Something went wrong"));
      return (
        <React.Fragment>
          {showToast && (
            <Toast
              error={true}
              label={errorLabel}
              onClose={closeToast}
              style={{ whiteSpace: "pre-line", height: "auto", maxWidth: "600px", padding: "12px 16px" }}
            />
          )}
        </React.Fragment>
      );
    }
    const label = showToast?.message || `ES_${businessService}_${showToast?.action?.action}_UPDATE_SUCCESS`;
    return <React.Fragment>{showToast && <Toast error={false} label={t(label)} onClose={closeToast} />}</React.Fragment>;
  }
}

export default ApplicationDetailsToast;
