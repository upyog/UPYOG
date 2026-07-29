import React, { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useParams } from "react-router-dom";
import {
  DynamicCheckPage,
  Loader,
  formatCheckPageDate,
  mergeRouteConfig,
} from "@nudmcdgnpm/digit-ui-react-components";
import estateAllotmentFormOverrides from "../../config/Create/estateAllotmentFormOverrides";
import { EST_CHECK_FLOWS } from "../../config/estCheckPageConfig";
import { checkForNA, ESTDocumnetPreview } from "../../utils";
import { buildAllotmentAckFormValues } from "../../utils/acknowledgementUtils";
import { buildAllotmentAssetDisplay } from "../../utils/estMdmsUtils";

const getAllotmentNo = (item = {}) =>
  String(
    item?.allotmentNo ?? item?.additionalDetails?.allotmentNo ?? ""
  ).trim();

const ALLOTMENT_NUMBER_FIELD = {
  order: -1,
  key: "EST_ALLOTMENT_NUMBER",
  field: {
    code: "EST_ALLOTMENT_NUMBER",
    name: "allotmentNo",
    type: "text",
  },
  validation: {
    required: false,
    disabled: true,
    readOnly: true,
  },
  excludeFromPayload: true,
};

/**
 * Application / allotment summary — same config-driven layout as the check page (view-only).
 * Citizen View Summary uses allotmentNo in the URL:
 *   /upyog-ui/citizen/est/application-details/EST-AL-1013-000008
 * Employee links may still pass estateNo / assetNo.
 */
const ESTApplicationDetails = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const { assetNo, tenantId: tenantIdParam } = useParams();
  const decodedId = decodeURIComponent(assetNo || "");

  const passedAllotment =
    location?.state?.allotmentData ||
    (location?.state?.applicationData?.allotmentNo
      ? location.state.applicationData
      : null);
  const passedAsset = location?.state?.applicationData || null;

  const tenantId =
    tenantIdParam ||
    passedAllotment?.tenantId ||
    passedAsset?.tenantId ||
    Digit.ULBService.getCurrentTenantId();

  const [asset, setAsset] = useState(null);
  const [allotment, setAllotment] = useState(passedAllotment || null);
  const [isLoading, setIsLoading] = useState(true);

  const { data: assignAssetMdms, isLoading: mdmsLoading } = Digit.Hooks.useEnabledMDMS(
    Digit.ULBService.getStateId(),
    "Estate",
    [{ name: "assignAssetConfig" }],
    {
      select: (data) => data?.Estate?.assignAssetConfig,
    }
  );

  useEffect(() => {
    let mounted = true;

    const load = async () => {
      setIsLoading(true);
      try {
        let nextAllotment = null;
        let nextAsset = null;

        // 1) Prefer allotment matched by allotmentNo (citizen View Summary URL).
        if (decodedId) {
          const passedMatches =
            passedAllotment && getAllotmentNo(passedAllotment) === decodedId
              ? passedAllotment
              : null;

          if (passedMatches) {
            nextAllotment = passedMatches;
          } else {
            try {
              const allotRes = await Digit.ESTService.allotmentSearch({
                tenantId,
                filters: { tenantId },
              });
              const list = allotRes?.Allotments || allotRes?.allotments || [];
              nextAllotment =
                list.find((item) => getAllotmentNo(item) === decodedId) || null;
            } catch (err) {
              console.warn("EST application details: allotment search failed", err);
            }
          }
        }

        // 2) Legacy: treat URL id as estateNo / assetNo when no allotment matched.
        const estateNo =
          nextAllotment?.assetNo ||
          nextAllotment?.estateNo ||
          (!nextAllotment ? decodedId : "") ||
          passedAsset?.estateNo ||
          passedAsset?.assetNo ||
          "";

        if (estateNo) {
          try {
            const assetRes = await Digit.ESTService.assetSearch({
              tenantId,
              filters: {
                AssetSearchCriteria: {
                  tenantId,
                  estateNo,
                },
              },
            });
            nextAsset = assetRes?.Assets?.[0] || null;
          } catch (err) {
            console.warn("EST application details: asset search failed", err);
          }
        }

        // If we only had estateNo in the URL, also try allotment by assetNo.
        if (!nextAllotment && estateNo) {
          try {
            const allotRes = await Digit.ESTService.allotmentSearch({
              tenantId,
              filters: {
                tenantId,
                assetNo: estateNo,
              },
            });
            nextAllotment = allotRes?.Allotments?.[0] || null;
          } catch (err) {
            console.warn("EST application details: allotment-by-asset search failed", err);
          }
        }

        if (!nextAsset && passedAsset?.estateNo) {
          nextAsset = passedAsset;
        }

        // Ensure allotmentNo is set from URL when API row is missing it.
        if (
          nextAllotment &&
          !getAllotmentNo(nextAllotment) &&
          /^EST-AL-/i.test(decodedId)
        ) {
          nextAllotment = { ...nextAllotment, allotmentNo: decodedId };
        }

        if (mounted) {
          setAllotment(nextAllotment);
          setAsset(nextAsset);
        }
      } catch (err) {
        console.error("EST application details load failed:", err);
        if (mounted) {
          setAllotment(passedAllotment || null);
          setAsset(passedAsset || null);
        }
      } finally {
        if (mounted) setIsLoading(false);
      }
    };

    load();
    return () => {
      mounted = false;
    };
  }, [decodedId, tenantId, passedAllotment, passedAsset]);

  const routeConfig = useMemo(() => {
    const steps = Array.isArray(assignAssetMdms) ? assignAssetMdms : [];
    const body = steps[0]?.body || [];
    const mdmsStep =
      body.find((s) => s.key === "Allotments" || s.route === "assign-assets") || {};
    const merged = mergeRouteConfig(mdmsStep, estateAllotmentFormOverrides);
    const form = Array.isArray(merged.form) ? [...merged.form] : [];
    const hasAllotmentNo = form.some(
      (f) => f.key === "EST_ALLOTMENT_NUMBER" || f.field?.name === "allotmentNo"
    );
    if (!hasAllotmentNo) {
      form.unshift(ALLOTMENT_NUMBER_FIELD);
    }
    return { ...merged, form };
  }, [assignAssetMdms]);

  const flow = EST_CHECK_FLOWS.allotment;

  const sessionValue = useMemo(() => {
    const formValues = buildAllotmentAckFormValues(
      allotment || {},
      asset || {},
      routeConfig
    );
    if (!formValues.allotmentNo && /^EST-AL-/i.test(decodedId)) {
      formValues.allotmentNo = decodedId;
    }
    return {
      Allotments: { Allotments: [formValues] },
      assetData: asset || {},
    };
  }, [allotment, asset, routeConfig, decodedId]);

  const extraData = useMemo(() => {
    const display = buildAllotmentAssetDisplay(asset || {}, allotment || {}, t);
    if (!display.allotmentNo && /^EST-AL-/i.test(decodedId)) {
      display.allotmentNo = decodedId;
    }
    return display;
  }, [asset, allotment, t, decodedId]);

  if (isLoading || mdmsLoading) return <Loader />;

  if (!asset && !allotment) {
    return <div>{t("EST_APPLICATION_NOT_FOUND")}</div>;
  }

  if (!routeConfig?.form?.length) {
    return <div>{t("EST_APPLICATION_NOT_FOUND")}</div>;
  }

  return (
    <DynamicCheckPage
      routeConfig={routeConfig}
      config={{ key: flow.stepKey }}
      value={sessionValue}
      extraData={extraData}
      summaryHeaderCode="EST_APPLICATION_DETAILS"
      defaultSectionHeaderCode={flow.defaultSectionHeaderCode || "EST_ASSET_DETAILS"}
      t={t}
      formatDate={formatCheckPageDate}
      checkNA={checkForNA}
      DocumentPreview={ESTDocumnetPreview}
      viewOnly
    />
  );
};

export default ESTApplicationDetails;
