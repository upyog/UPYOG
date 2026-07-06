/**
 * assetPayloadUtils.js
 * --------------------
 * Thin ESTATE-specific wrapper around the generic payloadUtils. Kept so
 * existing imports in NewRegistration.js / ESTRegCheckPage.js don't need
 * to change, while the actual mapping logic lives in payloadUtils.js
 * (generic) + estateFormConfig.js (estate-specific static/computed fields).
 *
 * If you're building a NEW module, do not copy this file — import
 * buildApiPayload / toDropdownOption / resolveOption / getRequestInfo
 * directly from payloadUtils.js and supply your own <module>FormConfig.js.
 */
// estate package: utils/assetPayloadUtils.js
import {
  buildApiPayload,
  toDropdownOption,
  resolveOption,
  getRequestInfo,
} from "@nudmcdgnpm/digit-ui-react-components";
import  estateFormConfig  from "../config/estateFormConfig";

export { toDropdownOption, resolveOption };

export const getEstateRequestInfo = (extra = {}) =>
  getRequestInfo(estateFormConfig.apiId, extra);

export const buildDynamicAssetPayload = (routeConfig, flatAsset, tenantId) =>
  buildApiPayload(routeConfig, flatAsset, tenantId);