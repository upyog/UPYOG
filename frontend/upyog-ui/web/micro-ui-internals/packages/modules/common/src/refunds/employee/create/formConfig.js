import React from "react";
import {
  RadioButtons,
  DatePicker,
} from "@nudmcdgnpm/digit-ui-react-components";
import { today, money, getApprovedAmount } from "./utils";

export const getRefundFormConfig = ({
  record,
  t,
  mode,
  setMode,
}) => {
  const tr = (key, fallback) => t(key, { defaultValue: fallback });

  const text = (name, label, required = false, validation = {}) => ({
    label,
    isMandatory: required,
    type: "text",
    populators: {
      name,
      defaultValue: "",
      validation: { required, ...validation },
      className: "payment-form-text-input-correction",
    },
  });

  const display = (label, value) => ({
    label,
    populators: (
      <div
        style={{
          paddingTop: "8px",
          paddingBottom: "16px",
          lineHeight: "1.5",
          overflowWrap: "anywhere",
        }}
      >
        {value ?? "—"}
      </div>
    ),
  });

  const radios = (name, label, options, selected, select) => ({
    label,
    type: "custom",
    isMandatory: true,
    populators: {
      name,
      defaultValue: selected,
      customProps: {
        options,
        optionsKey: "label",
        style: { display: "flex", flexWrap: "wrap" },
        innerStyles: { minWidth: "33%" },
      },
      component: (props, customProps) => (
        <RadioButtons
          {...customProps}
          selectedOption={props.value}
          onSelect={(value) => {
            props.onChange(value);
            select(value);
          }}
        />
      ),
    },
  });

  const date = (name, label, required = false, defaultValue = "") => ({
    label,
    type: "custom",
    isMandatory: required,
    populators: {
      name,
      defaultValue,
      customProps: {},
      component: (props) => (
        <DatePicker
          date={props.value}
          isRequired={required}
          onChange={props.onChange}
        />
      ),
    },
  });

  const modes = [
    {
      code: "CASH",
      label: tr("REFUND_MODE_CASH", "Cash"),
    },
    {
      code: "CHEQUE",
      label: tr("REFUND_MODE_CHEQUE", "Cheque"),
    },
    {
      code: "DD",
      label: tr("REFUND_MODE_DD", "Demand Draft (DD)"),
    },
  ];

  // Provisional categories; align with approved configuration.
  const categories = [
    {
      code: "EXCESS_PAYMENT",
      name: tr("REFUND_EXCESS_PAYMENT", "Excess Payment"),
    },
    {
      code: "DUPLICATE_PAYMENT",
      name: tr("REFUND_DUPLICATE_PAYMENT", "Duplicate Payment"),
    },
    {
      code: "CANCELLATION",
      name: tr("REFUND_CANCELLATION", "Cancellation"),
    },
    {
      code: "OTHER",
      name: tr("REFUND_OTHER", "Other"),
    },
  ];

  const recipientNameField = text(
    "recipientName",
    tr("REFUND_RECIPIENT_NAME", "Recipient Name"),
    true,
    { maxLength: 100 }
  );

  recipientNameField.populators.defaultValue = record.applicantName || "";

  const recipientMobileField = text(
    "recipientMobile",
    tr("REFUND_RECIPIENT_MOBILE", "Recipient Mobile Number"),
    true,
    { pattern: /^[6-9]\d{9}$/ }
  );

  recipientMobileField.populators.defaultValue = record.mobileNumber || "";

  const config = [
    {
      head: tr("REFUND_ORIGINAL_PAYMENT_DETAILS", "Original Payment Details"),
      body: [
        display(tr("REFUND_MODULE", "Module"), record.moduleName),
        display(
          tr("REFUND_CONSUMER_CODE", "Application / Consumer Number"),
          record.consumerCode
        ),
        display(
          tr("REFUND_ORIGINAL_MODE", "Original Payment Mode"),
          record.paymentModeOriginal
        ),
        display(
          tr("REFUND_AMOUNT_PAID", "Amount Paid"),
          money(record.amountPaid)
        ),
        display(
          tr("REFUND_ELIGIBLE_AMOUNT", "Eligible Refund Amount"),
          money(getApprovedAmount(record))
        ),
      ],
    },
    {
      head: tr("REFUND_DETAILS", "Refund Details"),
      body: [
        display(
          tr("REFUND_CATEGORY", "Refund Category"),
          record.refundCategory
        ),
        display(
          tr("REFUND_REASON", "Refund Reason"),
          record.refundReason
        ),
        date(
          "refundDate",
          tr("REFUND_DATE", "Refund Date"),
          true,
          today()
        ),
      ],
    },
    {
      head: tr("REFUND_RECIPIENT_DETAILS", "Recipient Details"),
      body: [recipientNameField, recipientMobileField],
    },
    {
      head: tr("REFUND_MODE_HEAD", "Refund Mode"),
      body: [
        radios(
          "refundMode",
          tr("REFUND_MODE", "Mode of Refund"),
          modes,
          modes.find((value) => value.code === mode),
          (value) => setMode(value.code)
        ),
      ],
    },
  ];

  if (mode === "CHEQUE") {
    config.push({
      head: tr("REFUND_CHEQUE_DETAILS", "Cheque Details"),
      body: [
        text(
          "chequeNumber",
          tr("REFUND_CHEQUE_NUMBER", "Cheque Number"),
          true,
          { pattern: /^\d{6}$/ }
        ),
        date("chequeDate", tr("REFUND_CHEQUE_DATE", "Cheque Date"), true),
        text("bankName", tr("REFUND_BANK_NAME", "Bank Name"), true),
      ],
    });
  }
  if (mode === "DD") {
    config.push({
      head: tr("REFUND_DD_DETAILS", "Demand Draft Details"),
      body: [
        text(
          "ddNumber",
          tr("REFUND_DD_NUMBER", "DD Number"),
          true,
          { maxLength: 50 }
        ),
        date(
          "ddDate",
          tr("REFUND_DD_DATE", "DD Date"),
          true
        ),
        text(
          "bankName",
          tr("REFUND_BANK_NAME", "Bank Name"),
          true,
          { maxLength: 100 }
        ),
      ],
    });
  }

  config.push({
    head: tr(
      "REFUND_ADDITIONAL_DETAILS_HEAD",
      "Additional Details (Optional)"
    ),
    body: [
      text(
        "additionalDetailsText",
        tr("REFUND_ADDITIONAL_DETAILS", "Additional Details"),
        false,
        { maxLength: 1000 }
      ),
    ],
  });

  return config;
};