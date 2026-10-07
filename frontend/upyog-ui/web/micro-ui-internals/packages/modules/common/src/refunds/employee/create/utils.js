export const today = () => {
  const date = new Date();

  return [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, "0"),
    String(date.getDate()).padStart(2, "0"),
  ].join("-");
};

export const money = (amount) =>
  `₹ ${Number(amount || 0).toFixed(2)}`;

export const validDate = (value) => {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value || "")) {
    return false;
  }

  const date = new Date(`${value}T00:00:00Z`);

  return (
    !Number.isNaN(date.getTime()) &&
    date.toISOString().slice(0, 10) === value &&
    value <= today()
  );
};

// The record must be the existing refund returned by the backend.
// Its refundAmount is the approved amount available for completion.
export const getApprovedAmount = (record) =>
  Number(record?.refundAmount);

  export const isEligible = (record) => {
    const approvedAmount = getApprovedAmount(record);
  
    return (
      Boolean(record?.id && record?.tenantId) &&
      record.refundMode === "OFFLINE" &&
      record.status === "REFUND_INITIATED" &&
      Number.isFinite(approvedAmount) &&
      approvedAmount > 0
    );
  };

export const getEnteredAmount = (_data, record) =>
  getApprovedAmount(record);

export const validateRefund = (data, record) => {
  if (!isEligible(record)) {
    return "A valid existing offline refund is required.";
  }
  const amount = getEnteredAmount(data, record);
  const approvedAmount = getApprovedAmount(record);

  if (
    !Number.isFinite(amount) ||
    amount <= 0 ||
    amount > approvedAmount ||
    Math.abs(amount * 100 - Math.round(amount * 100)) > 0.000001
  ) {
    return "Enter an amount greater than zero and within the approved refund amount, with up to two decimal places.";
  }

  if (!data.recipientName?.trim()) {
    return "Enter the payee name.";
  }

  if (!/^[6-9]\d{9}$/.test(data.recipientMobile || "")) {
    return "Enter a valid 10-digit payee mobile number.";
  }

  if (!validDate(data.refundDate)) {
    return "Enter a valid refund date that is not in the future.";
  }

  const mode = data.refundMode?.code;

  if (!["CASH", "CHEQUE", "DD"].includes(mode)) {
    return "Select Cash, Cheque or DD.";
  }

  if (mode === "CHEQUE") {
    if (!/^\d{6}$/.test(data.chequeNumber || "")) {
      return "Enter a valid 6-digit cheque number.";
    }

    if (!validDate(data.chequeDate)) {
      return "Enter a valid cheque date that is not in the future.";
    }

    if (!data.bankName?.trim()) {
      return "Enter the bank name.";
    }
  }
  if (mode === "DD") {
    const ddNumber = (data.ddNumber || "").trim();
  
    if (!ddNumber || ddNumber.length > 50) {
      return "Enter a DD number of up to 50 characters.";
    }
  
    if (!validDate(data.ddDate)) {
      return "Enter a valid DD date that is not in the future.";
    }
  
    if (!data.bankName?.trim()) {
      return "Enter the bank name.";
    }
  }

  if ((data.additionalDetailsText || "").length > 1000) {
    return "Additional Details cannot exceed 1000 characters.";
  }

  return null;
};

export const buildRefundPayload = (data, record) => {
  const validationError = validateRefund(data, record);

  if (validationError) {
    throw new Error(validationError);
  }

  const mode = data.refundMode.code;
  const amount = getEnteredAmount(data, record);
  const additionalText = (data.additionalDetailsText || "").trim();

  const beneficiaryDetails = {
    mode,
    payeeName: data.recipientName.trim(),
    mobileNumber: data.recipientMobile,
    amount,
    refundDate: data.refundDate,
  };

  // Hidden cheque fields are not sent when Cash is selected.
  if (mode === "CHEQUE") {
    beneficiaryDetails.chequeNumber = data.chequeNumber;
    beneficiaryDetails.chequeDate = data.chequeDate;
    beneficiaryDetails.bankName = data.bankName.trim();
  }
  if (mode === "DD") {
    beneficiaryDetails.ddNumber = data.ddNumber.trim();
    beneficiaryDetails.ddDate = data.ddDate;
    beneficiaryDetails.bankName = data.bankName.trim();
  }

  const existingAdditionalDetails =
    record.additionalDetails &&
    typeof record.additionalDetails === "object" &&
    !Array.isArray(record.additionalDetails)
      ? record.additionalDetails
      : {};

  return {
    refund: {
      ...record,

      // Preserve the channel; Cash/Cheque goes inside beneficiaryDetails.
      refundMode: "OFFLINE",
      refundAmount: amount,

      beneficiaryDetails,

      additionalDetails: {
        ...existingAdditionalDetails,
        offlineRefundRemarks: additionalText,
      },

      processInstance: {
        action: "COMPLETE_REFUND",
        comment: additionalText || "Offline refund completed",
        documents: [],
        assignes: [],
      },
    },
  };
};