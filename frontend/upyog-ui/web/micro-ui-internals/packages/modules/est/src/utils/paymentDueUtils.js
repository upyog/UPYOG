/**
 * paymentDueUtils.js
 * Next payment due date for citizen My Applications / payment flows.
 * Unpaid → current due (bill expiry / schedule, today or later).
 * Paid (amount 0) → next billing-cycle date after today.
 */
import { resolveBillingCycleMultiplier } from "@nudmcdgnpm/digit-ui-react-components";

const startOfLocalDay = (date) => {
  const d = new Date(date);
  d.setHours(0, 0, 0, 0);
  return d;
};

const parseDueDate = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (value instanceof Date) {
    return isNaN(value.getTime()) ? null : startOfLocalDay(value);
  }
  if (typeof value === "number" || /^\d+$/.test(String(value).trim())) {
    let num = Number(value);
    if (String(Math.trunc(num)).length === 10) num *= 1000;
    const d = new Date(num);
    return isNaN(d.getTime()) ? null : startOfLocalDay(d);
  }
  const dmy = String(value).match(/^(\d{2})[/-](\d{2})[/-](\d{4})$/);
  if (dmy) return startOfLocalDay(new Date(+dmy[3], +dmy[2] - 1, +dmy[1]));
  const ymd = String(value).match(/^(\d{4})-(\d{2})-(\d{2})/);
  if (ymd) return startOfLocalDay(new Date(+ymd[1], +ymd[2] - 1, +ymd[3]));
  const d = new Date(value);
  return isNaN(d.getTime()) ? null : startOfLocalDay(d);
};

const addMonthsLocal = (date, months) => {
  const d = new Date(date);
  const day = d.getDate();
  d.setMonth(d.getMonth() + months);
  // Clamp month overflow (e.g. Jan 31 + 1 month).
  if (d.getDate() < day) d.setDate(0);
  return startOfLocalDay(d);
};

const resolveCycleMonths = (allotment, billingOptions = []) => {
  const source = allotment && typeof allotment === "object" ? allotment : {};
  const months = resolveBillingCycleMultiplier(
    source.billingCycle || "MONTHLY",
    billingOptions
  );
  return Number.isFinite(months) && months > 0 ? months : 1;
};

export const getBillAmountDue = (billData) => {
  const bill = billData?.Bill?.[0];
  if (!bill) return 0;
  const total = Number(bill.totalAmount);
  if (Number.isFinite(total)) return total;
  const details = bill.billDetails || [];
  return details.reduce((sum, d) => sum + (Number(d?.amount) || 0), 0);
};

/** Latest bill detail (by fromPeriod), then expiryDate. */
export const getBillExpiryDate = (billData) => {
  const details = billData?.Bill?.[0]?.billDetails;
  if (!Array.isArray(details) || details.length === 0) return null;
  const sorted = [...details].sort(
    (a, b) => Number(b?.fromPeriod || 0) - Number(a?.fromPeriod || 0)
  );
  return parseDueDate(sorted[0]?.expiryDate ?? sorted[0]?.currentExpiryDate);
};

/**
 * Walk allotment schedule by billing-cycle months.
 * @param {object} options
 * @param {boolean} options.strictlyAfterToday - when true (already paid), skip today
 *   and return the next cycle date; otherwise land on today if that is the due day.
 */
export const getScheduledNextDueDate = (
  allotment,
  billingOptions = [],
  { strictlyAfterToday = false } = {}
) => {
  const source = allotment && typeof allotment === "object" ? allotment : {};
  const anchor = parseDueDate(
    source.advancePaymentDate || source.agreementStartDate
  );
  if (!anchor) return null;

  const step = resolveCycleMonths(source, billingOptions);
  const today = startOfLocalDay(new Date());

  let due = anchor;
  let guard = 0;
  const isPastOrEqualToday = (d) =>
    strictlyAfterToday ? d <= today : d < today;

  while (isPastOrEqualToday(due) && guard < 600) {
    due = addMonthsLocal(due, step);
    guard += 1;
  }
  return due;
};

/**
 * Prefer open-bill expiry while amount is due.
 * Once paid (amount 0), advance to the next billing-cycle date (e.g. next month).
 */
export const getNextPaymentDueDate = ({
  billData,
  allotment,
  billingOptions = [],
  amountDue,
} = {}) => {
  const dueAmount =
    amountDue != null ? Number(amountDue) : getBillAmountDue(billData);
  const hasOpenDemand = Number.isFinite(dueAmount) && dueAmount > 0;

  if (hasOpenDemand) {
    return (
      getBillExpiryDate(billData) ||
      getScheduledNextDueDate(allotment || {}, billingOptions, {
        strictlyAfterToday: false,
      })
    );
  }

  // Paid / no demand → show the upcoming cycle, not today's (already paid) date.
  const billExpiry = getBillExpiryDate(billData);
  if (billExpiry) {
    const step = resolveCycleMonths(allotment || {}, billingOptions);
    const today = startOfLocalDay(new Date());
    let due = billExpiry;
    let guard = 0;
    while (due <= today && guard < 600) {
      due = addMonthsLocal(due, step);
      guard += 1;
    }
    return due;
  }

  return getScheduledNextDueDate(allotment || {}, billingOptions, {
    strictlyAfterToday: true,
  });
};

/** True when today is on/after the due date (local calendar day). */
export const isPaymentDueTodayOrPast = (dueDate) => {
  const due = parseDueDate(dueDate);
  if (!due) return false;
  return startOfLocalDay(new Date()) >= due;
};

export const formatPaymentDueDate = (dueDate) => {
  const d = parseDueDate(dueDate);
  if (!d) return "";
  const dd = String(d.getDate()).padStart(2, "0");
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  return `${dd}/${mm}/${d.getFullYear()}`;
};
