import { pick } from "./index";
import {
  buildAckDetailsFromRouteConfig,
  buildAllotmentAckExtraData,
  buildAllotmentAckFormValues,
  resolveAllotmentAckContext,
} from "./acknowledgementUtils";

/**
 * Prepares structured acknowledgement data for EST Asset Allotment PDF.
 * Sections and field labels are driven by routeConfig.form (MDMS + local overrides),
 * matching the check page summary.
 */
const getESTAllotmentAcknowledgementData = async (
  application = {},
  tenantInfo = {},
  t = (k) => k
) => {
  const { asset, allotment, routeConfig } = resolveAllotmentAckContext(application);
  const extraData = buildAllotmentAckExtraData(asset, allotment, t);
  const formValues = buildAllotmentAckFormValues(allotment, asset, routeConfig);

  const details = buildAckDetailsFromRouteConfig({
    routeConfig,
    formValues,
    extraData,
    t,
  });

  return {
    t,
    heading: t("EST_ACKNOWLEDGEMENT"),
    applicationNumber: pick(
      allotment.allotmentNo,
      allotment.additionalDetails?.allotmentNo,
      allotment.assetNo,
      asset.estateNo,
      asset.assetNo
    ),

    tenantId: tenantInfo?.code,
    name: tenantInfo?.name || t(tenantInfo?.i18nKey) || tenantInfo?.code || "",
    email: tenantInfo?.emailId || "",
    phoneNumber: tenantInfo?.contactNumber || "",

    // `details` = register/asset rows + allottee/invoice rows from routeConfig.form
    // (same MDMS assignAssetConfig used on the check page). Empty values are omitted.
    details,

    Assets: [asset],
    Allotments: [allotment],
    asset,
    allotment,
    fullApplication: application,
  };
};

export default getESTAllotmentAcknowledgementData;
