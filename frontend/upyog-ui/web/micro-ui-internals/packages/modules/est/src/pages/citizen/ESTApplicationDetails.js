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
import {
  buildAllotmentAssetDisplay,
  resolveAllotmentAsset,
} from "../../utils/estMdmsUtils";

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
    Digit.ULBService.getCitizenCurrentTenant?.(true) ||
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
        const isAllotmentNo = /^EST-AL-/i.test(decodedId);
        const passedMatches =
          passedAllotment &&
          (getAllotmentNo(passedAllotment) === decodedId ||
            !getAllotmentNo(passedAllotment))
            ? passedAllotment
            : null;

        // Citizen View Summary: always search by allotmentNo
        // POST /estate-management/estate/allotment/v1/_search
        // body: { AllotmentSearchCriteria: { tenantId, allotmentNo } }
        if (decodedId && isAllotmentNo) {
          try {
            const allotRes = await Digit.ESTService.allotmentSearch({
              tenantId,
              filters: {
                tenantId,
                allotmentNo: decodedId,
              },
            });
            const list = allotRes?.Allotments || allotRes?.allotments || [];
            const fromApi =
              list.find((item) => getAllotmentNo(item) === decodedId) ||
              (passedMatches
                ? list.find(
                    (item) =>
                      (item?.allotmentId &&
                        item.allotmentId === passedMatches.allotmentId) ||
                      (item?.assetNo &&
                        item.assetNo ===
                          (passedMatches.assetNo || passedMatches.estateNo))
                  )
                : null) ||
              (list.length === 1 ? list[0] : null);

            // Prefer API row; keep list-card fields as fallback (e.g. allotmentNo).
            if (fromApi) {
              nextAllotment = {
                ...(passedMatches || {}),
                ...fromApi,
                allotmentNo:
                  getAllotmentNo(fromApi) ||
                  getAllotmentNo(passedMatches) ||
                  decodedId,
              };
              // Allotment _search embeds asset — never call asset/_search here.
              nextAsset =
                fromApi.asset ||
                fromApi.Asset ||
                passedMatches?.asset ||
                passedMatches?.Asset ||
                null;
            }
          } catch (err) {
            console.warn("EST application details: allotment search failed", err);
          }

          if (!nextAllotment && passedMatches) {
            nextAllotment = {
              ...passedMatches,
              allotmentNo: getAllotmentNo(passedMatches) || decodedId,
            };
            nextAsset =
              passedMatches.asset || passedMatches.Asset || null;
          }
        } else if (decodedId) {
          // Legacy employee flow: URL id is estateNo / assetNo.
          const estateNo =
            passedAsset?.estateNo ||
            passedAsset?.assetNo ||
            decodedId ||
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

          // Prefer asset/_search; fall back to navigation state. Keep refAssetNo.
          if (!nextAsset && passedAsset) {
            nextAsset = passedAsset;
          } else if (
            nextAsset &&
            !nextAsset.refAssetNo &&
            (passedAsset?.refAssetNo || passedAsset?.assetRef)
          ) {
            nextAsset = {
              ...nextAsset,
              refAssetNo: passedAsset.refAssetNo || passedAsset.assetRef,
            };
          }
        }

        if (nextAllotment && !getAllotmentNo(nextAllotment) && isAllotmentNo) {
          nextAllotment = { ...nextAllotment, allotmentNo: decodedId };
        }

        // Allotment _search embeds asset on the row — use it (no asset/_search).
        if (!nextAsset && nextAllotment) {
          nextAsset =
            nextAllotment.asset ||
            nextAllotment.Asset ||
            passedMatches?.asset ||
            null;
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
    const resolvedAsset = resolveAllotmentAsset(asset, allotment || {});
    const formValues = buildAllotmentAckFormValues(
      allotment || {},
      resolvedAsset,
      routeConfig
    );
    if (!formValues.allotmentNo && /^EST-AL-/i.test(decodedId)) {
      formValues.allotmentNo = decodedId;
    }
    return {
      Allotments: { Allotments: [formValues] },
      assetData: resolvedAsset,
    };
  }, [allotment, asset, routeConfig, decodedId]);

  const extraData = useMemo(() => {
    const resolvedAsset = resolveAllotmentAsset(asset, allotment || {});
    const display = buildAllotmentAssetDisplay(resolvedAsset, allotment || {}, t);
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
