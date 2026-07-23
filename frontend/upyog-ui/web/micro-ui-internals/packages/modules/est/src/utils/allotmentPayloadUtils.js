/**
 * allotmentPayloadUtils.js
 * Thin ESTATE wrapper around generic buildApiPayload — same pattern as assetPayloadUtils.js.
 * Allotment-specific rules (dates, static fields, numeric casts) live in estateAllotmentFormOverrides.
 */
import { buildApiPayload } from "@nudmcdgnpm/digit-ui-react-components";

/** Normalize dropdown/radio values to an uppercase code string. */
const toOptionCode = (val) => {
  if (val === null || val === undefined || val === "") return "";
  if (typeof val === "object" && val.code != null) return String(val.code).trim().toUpperCase();
  return String(val).trim().toUpperCase();
};

export const buildDynamicAllotmentPayload = (routeConfig, flatAllotment = {}, tenantId) => {
  const built = buildApiPayload(routeConfig, flatAllotment, tenantId);
  // Guard: backend stores these as VARCHAR codes (not dropdown option objects).
  const propertyType =
    toOptionCode(built.propertyType) ||
    toOptionCode(built.allotmentType) ||
    "RENT";
  built.propertyType = propertyType === "LEASE" ? "LEASE" : "RENT";
  if (built.allotmentType != null) {
    built.allotmentType = built.propertyType;
  }
  // Default billing cycle to MONTHLY when dropdown was left empty.
  built.billingCycle = toOptionCode(built.billingCycle) || "MONTHLY";
  return built;
};
