/**
 * allotmentPayloadUtils.js
 * Thin ESTATE wrapper around generic buildApiPayload — same pattern as assetPayloadUtils.js.
 * Allotment-specific rules (dates, static fields, numeric casts) live in estateAllotmentFormConfig.
 */
import { buildApiPayload } from "@nudmcdgnpm/digit-ui-react-components";

export const buildDynamicAllotmentPayload = (routeConfig, flatAllotment = {}, tenantId) =>
  buildApiPayload(routeConfig, flatAllotment, tenantId);
