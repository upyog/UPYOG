/**
 * allotmentPayloadUtils.js
 * Thin ESTATE wrapper around generic buildApiPayload — same pattern as assetPayloadUtils.js.
 * Allotment-specific rules (dates, static fields, numeric casts) live in estateAllotmentFormOverrides.
 */
import { buildApiPayload, optionCode } from "@nudmcdgnpm/digit-ui-react-components";

export const buildDynamicAllotmentPayload = (routeConfig, flatAllotment = {}, tenantId) => {
  const built = buildApiPayload(routeConfig, flatAllotment, tenantId);
  // Guard: backend stores these as VARCHAR codes (not dropdown option objects).
  const propertyType =
    optionCode(built.propertyType) ||
    optionCode(built.allotmentType) ||
    "RENT";
  built.propertyType = propertyType === "LEASE" ? "LEASE" : "RENT";
  if (built.allotmentType != null) {
    built.allotmentType = built.propertyType;
  }
  // Default billing cycle to MONTHLY when dropdown was left empty.
  built.billingCycle = optionCode(built.billingCycle) || "MONTHLY";
  return built;
};
