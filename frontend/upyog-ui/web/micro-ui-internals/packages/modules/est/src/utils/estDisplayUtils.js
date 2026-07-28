/**
 * Shared display helpers for citizen EST cards (My Applications / Payment History).
 */
import { optionCode } from "@nudmcdgnpm/digit-ui-react-components";

export const translateOrCode = (t, prefix, code) => {
  if (!code) return "N/A";
  const key = `${prefix}_${code}`;
  const translated = t(key);
  return translated && translated !== key ? translated : code;
};

export const toBillingCycleLabel = (value, t) => {
  const code = optionCode(value);
  return code ? translateOrCode(t, "EST_BILLING_CYCLE", code) : "N/A";
};

/** Backend dueDate from allotment search (Allotments[].dueDate). */
export const getAllotmentDueDate = (application, allotment) =>
  allotment?.dueDate ??
  application?.dueDate ??
  allotment?.additionalDetails?.dueDate ??
  application?.additionalDetails?.dueDate ??
  null;

/** Parse backend dueDate (dd-MM-yyyy, ISO, epoch, or Jackson [y,m,d] array). */
export const parseEstDueDate = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (value instanceof Date) {
    return Number.isNaN(value.getTime()) ? null : value;
  }
  if (Array.isArray(value) && value.length >= 3) {
    const [y, m, d] = value.map(Number);
    if (![y, m, d].every(Number.isFinite)) return null;
    const date = new Date(y, m - 1, d);
    return Number.isNaN(date.getTime()) ? null : date;
  }
  if (typeof value === "object") {
    const y = Number(value.year ?? value.Year);
    const m = Number(value.monthValue ?? value.month ?? value.Month);
    const d = Number(value.dayOfMonth ?? value.day ?? value.Day);
    if ([y, m, d].every(Number.isFinite)) {
      const date = new Date(y, m - 1, d);
      return Number.isNaN(date.getTime()) ? null : date;
    }
  }
  if (typeof value === "number" || /^\d+$/.test(String(value).trim())) {
    let num = Number(value);
    if (String(Math.trunc(num)).length === 10) num *= 1000;
    const date = new Date(num);
    return Number.isNaN(date.getTime()) ? null : date;
  }
  const raw = String(value).trim();
  const dmy = raw.match(/^(\d{1,2})[/-](\d{1,2})[/-](\d{4})$/);
  if (dmy) return new Date(+dmy[3], +dmy[2] - 1, +dmy[1]);
  const ymd = raw.match(/^(\d{4})-(\d{1,2})-(\d{1,2})/);
  if (ymd) return new Date(+ymd[1], +ymd[2] - 1, +ymd[3]);
  const date = new Date(raw);
  return Number.isNaN(date.getTime()) ? null : date;
};

export const formatEstDueDate = (value) => {
  if (value === null || value === undefined || value === "") return "";
  if (typeof value === "string") {
    const trimmed = value.trim();
    if (/^\d{1,2}[/-]\d{1,2}[/-]\d{4}$/.test(trimmed)) {
      return trimmed.replace(/-/g, "/");
    }
  }
  const date = parseEstDueDate(value);
  if (!date) return typeof value === "string" ? value.trim() : "";
  const dd = String(date.getDate()).padStart(2, "0");
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  return `${dd}/${mm}/${date.getFullYear()}`;
};

export const formatReceiptDate = (value) => {
  if (!value && value !== 0) return "N/A";
  const date = parseEstDueDate(value) || new Date(value);
  if (Number.isNaN(date.getTime())) return "N/A";
  return date.toLocaleDateString("en-GB");
};

export const getBillAmountDue = (billData) => {
  const bill = billData?.Bill?.[0];
  if (!bill) return 0;
  const total = Number(bill.totalAmount);
  if (Number.isFinite(total)) return total;
  const details = bill.billDetails || [];
  return details.reduce((sum, d) => sum + (Number(d?.amount) || 0), 0);
};

export const isNoDemandError = (err) => {
  const code =
    err?.response?.data?.Errors?.[0]?.code ||
    err?.Errors?.[0]?.code ||
    "";
  return (
    code === "EG_BS_BILL_NO_DEMANDS_FOUND" ||
    code === "EMPTY_DEMANDS" ||
    String(code).includes("NO_DEMAND")
  );
};

/**
 * Payment status from allotment _search (`Allotments[]`).
 * Primary field is `status` (PAID | PENDING_FOR_PAYMENT); also accept aliases.
 */
export const getAllotmentPaymentStatus = (allotment, application) => {
  const candidates = [
    allotment?.status,
    allotment?.paymentStatus,
    allotment?.rentPaymentDetails?.paymentStatus,
    allotment?.rentPaymentDetails?.status,
    allotment?.additionalDetails?.paymentStatus,
    allotment?.additionalDetails?.status,
    application?.status,
    application?.paymentStatus,
    application?.rentPaymentDetails?.paymentStatus,
    application?.rentPaymentDetails?.status,
    application?.additionalDetails?.paymentStatus,
    application?.additionalDetails?.status,
  ];
  const paymentCodes = new Set(["PAID", "PENDING_FOR_PAYMENT", "DEPOSITED"]);
  let fallback = "";
  for (const candidate of candidates) {
    const code = optionCode(candidate);
    if (!code) continue;
    if (paymentCodes.has(code)) return code;
    if (!fallback) fallback = code;
  }
  return fallback;
};

/**
 * Normalize API payment codes to citizen filter codes:
 * PAID → PAID; anything else / missing → PENDING_FOR_PAYMENT.
 */
export const normalizeCitizenPaymentStatus = (status) => {
  const code = String(status || "")
    .toUpperCase()
    .replace(/\s+/g, "_");
  if (code === "PAID" || code === "DEPOSITED") return "PAID";
  return "PENDING_FOR_PAYMENT";
};

/** True when Allotments[].status is PAID — Make Payment must be hidden. */
export const isAllotmentPaymentPaid = (allotment, application) =>
  normalizeCitizenPaymentStatus(getAllotmentPaymentStatus(allotment, application)) ===
  "PAID";
