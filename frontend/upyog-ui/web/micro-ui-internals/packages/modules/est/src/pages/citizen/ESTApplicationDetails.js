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

/**
 * Application / asset summary — same config-driven layout as the check page (view-only).
 */
const ESTApplicationDetails = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const { assetNo, tenantId: tenantIdParam } = useParams();
  const decodedAssetNo = decodeURIComponent(assetNo || "");

  const passedData = location?.state?.applicationData || null;
  const tenantId =
    tenantIdParam ||
    passedData?.tenantId ||
    Digit.ULBService.getCurrentTenantId();

  const [asset, setAsset] = useState(passedData || null);
  const [allotment, setAllotment] = useState(null);
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
        let nextAsset = passedData;
        if (!nextAsset?.estateNo && decodedAssetNo) {
          const assetRes = await Digit.ESTService.assetSearch({
            tenantId,
            filters: {
              AssetSearchCriteria: {
                tenantId,
                estateNo: decodedAssetNo,
              },
            },
          });
          nextAsset = assetRes?.Assets?.[0] || null;
        }

        let nextAllotment = null;
        if (decodedAssetNo) {
          try {
            const allotRes = await Digit.ESTService.allotmentSearch({
              tenantId,
              filters: {
                tenantId,
                assetNo: decodedAssetNo,
              },
            });
            nextAllotment = allotRes?.Allotments?.[0] || null;
          } catch (err) {
            console.warn("EST application details: allotment search failed", err);
          }
        }

        if (mounted) {
          setAsset(nextAsset);
          setAllotment(nextAllotment);
        }
      } catch (err) {
        console.error("EST application details load failed:", err);
        if (mounted) {
          setAsset(passedData || null);
          setAllotment(null);
        }
      } finally {
        if (mounted) setIsLoading(false);
      }
    };

    load();
    return () => {
      mounted = false;
    };
  }, [decodedAssetNo, tenantId, passedData]);

  const routeConfig = useMemo(() => {
    const steps = Array.isArray(assignAssetMdms) ? assignAssetMdms : [];
    const body = steps[0]?.body || [];
    const mdmsStep =
      body.find((s) => s.key === "Allotments" || s.route === "assign-assets") || {};
    return mergeRouteConfig(mdmsStep, estateAllotmentFormOverrides);
  }, [assignAssetMdms]);

  const flow = EST_CHECK_FLOWS.allotment;

  const sessionValue = useMemo(() => {
    const formValues = buildAllotmentAckFormValues(
      allotment || {},
      asset || {},
      routeConfig
    );
    return {
      Allotments: { Allotments: [formValues] },
      assetData: asset || {},
    };
  }, [allotment, asset, routeConfig]);

  const extraData = useMemo(
    () => buildAllotmentAssetDisplay(asset || {}, allotment || {}, t),
    [asset, allotment, t]
  );

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
