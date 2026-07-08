/**
 * ESTAllotmentAcknowledgement (final)
 * -----------------------------------
 * Uses mutateAsync (plain awaited promise) instead of mutate-callbacks,
 * so a resolved HTTP call can never be silently dropped by react-query.
 * Keeps: ref-held watchdog, crash-proof merge, isolated onSuccess prop call.
 *
 * WHERE TO PUT THIS FILE:
 *   Replace your existing ESTAllotmentAcknowledgement.js
 *   (same folder as before, e.g. .../pageComponents or .../pages/employee/...)
 */

import React, { useEffect, useRef, useState } from "react";
import {
  Banner,
  Card,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
  Loader,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import getESTAllotmentAcknowledgementData from "../../../utils/getESTAllotmentAcknowledgementData";
import { createAllotmentData, estPayloadData } from "../../../utils";

/* ---------------- Styles ---------------- */

const rowContainerStyle = {
  padding: "4px 0px",
  justifyContent: "space-between",
};

const BannerPicker = ({ t, isSuccess, data }) => {
  const applicationNumber = data?.Allotments?.[0]?.assetNo || "";
  return (
    <Banner
      message={isSuccess ? t("EST_ALLOTED_SUCCESSFULL") : t("EST_APPLICATION_FAILED")}
      applicationNumber={applicationNumber}
      info={isSuccess ? t("EST_APPLICATION_NO") : ""}
      successful={isSuccess}
      style={{ width: "100%" }}
    />
  );
};

const ESTAllotmentAcknowledgement = ({ data = {}, onSuccess }) => {
  const { t } = useTranslation();
  const hasRun = useRef(false);
  const payloadRef = useRef(null);
  const watchdogRef = useRef(null);

  const tenantId =
    Digit?.ULBService?.getCitizenCurrentTenant?.(true) ||
    Digit?.ULBService?.getCurrentTenantId?.() ||
    "pg.citya"; // fallback matched to your environment (was pb.amritsar)

  const user = Digit?.UserService?.getUser?.()?.info || {};

  const useAllotmentHook = Digit?.Hooks?.estate?.useESTAssetsAllotment;
  const allotmentMutation = useAllotmentHook ? useAllotmentHook(tenantId) : null;

  const initResponse = Digit?.Hooks?.useStore?.getInitData?.() || {};
  const storeData = initResponse?.data || initResponse;
  const tenants = storeData?.tenants || [];

  const [finalMutation, setFinalMutation] = useState({
    isLoading: true,
    isSuccess: false,
    data: null,
  });

  /* Never let a merge failure strand the loader — fall back to the raw response. */
  const buildMergedResponse = (allotmentRes) => {
    const responseAllotment = allotmentRes?.Allotments?.[0] || {};
    let payloadAllotment = {};
    let assets = [];
    try {
      payloadAllotment = payloadRef.current?.Allotments?.[0] || {};
    } catch (e) {
      console.error("EST_ACK: failed reading payload for merge", e);
    }
    try {
      assets = estPayloadData(data)?.Assets || [];
    } catch (e) {
      console.error("EST_ACK: estPayloadData threw during merge — continuing without Assets", e);
    }
    return {
      Allotments: [
        {
          ...responseAllotment,
          agreementStartDate:
            payloadAllotment.agreementStartDate ?? responseAllotment.agreementStartDate,
          agreementEndDate:
            payloadAllotment.agreementEndDate ?? responseAllotment.agreementEndDate,
          advancePaymentDate:
            payloadAllotment.advancePaymentDate ?? responseAllotment.advancePaymentDate,
        },
      ],
      Assets: assets,
    };
  };

  const settle = (isSuccess, mergedData) => {
    if (watchdogRef.current) {
      clearTimeout(watchdogRef.current);
      watchdogRef.current = null;
    }
    setFinalMutation({ isLoading: false, isSuccess, data: mergedData || null });
    if (isSuccess && mergedData) {
      try {
        onSuccess && onSuccess(mergedData);
      } catch (e) {
        console.error("EST_ACK: onSuccess prop threw", e);
      }
    }
  };

  /* ---------------- API Call ---------------- */

  useEffect(() => {
    if (hasRun.current) return;

    const allotmentFormData =
      data?.Allotments?.Allotments?.[0] ||
      data?.AssignAssetsData?.AllotmentData;

    if (!allotmentFormData) {
      console.warn(
        "EST_ACK: no allotment form data in params — keys present:",
        Object.keys(data || {})
      );
      settle(false);
      return;
    }

    if (!allotmentMutation?.mutateAsync) {
      console.error(
        "EST_ACK: Digit.Hooks.estate.useESTAssetsAllotment missing or has no mutateAsync()"
      );
      settle(false);
      return;
    }

    hasRun.current = true;

    // Watchdog: held in a ref; cleared only inside settle() or true unmount
    watchdogRef.current = setTimeout(() => {
      setFinalMutation((prev) => {
        if (!prev.isLoading) return prev;
        console.error("EST_ACK: allotment API did not respond within 45s — check the Network tab");
        return { isLoading: false, isSuccess: false, data: null };
      });
    }, 45000);

    (async () => {
      try {
        payloadRef.current = createAllotmentData(data);

        const res = await allotmentMutation.mutateAsync(payloadRef.current);

        try {
          settle(true, buildMergedResponse(res));
        } catch (mergeErr) {
          console.error("EST_ACK: merge failed — settling with raw response", mergeErr);
          settle(true, { Allotments: res?.Allotments || [], Assets: [] });
        }
      } catch (err) {
        console.error(
          "EST_ACK: API rejected",
          err?.response?.status,
          err?.response?.data || err
        );
        settle(false);
      }
    })();
    // NOTE: intentionally no cleanup — a [data, tenantId] re-run must not
    // kill the watchdog while the request is in flight.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [data, tenantId]);

  /* Clear the watchdog only on a genuine unmount */
  useEffect(() => {
    return () => {
      if (watchdogRef.current) clearTimeout(watchdogRef.current);
    };
  }, []);

  /* ---------------- PDF Download ---------------- */

  const handleDownloadPdf = async () => {
    try {
      const allotment = finalMutation.data?.Allotments?.[0];
      if (!allotment) return;
      const tenantInfo = tenants.find((tn) => tn.code === allotment.tenantId) || {};
      const pdfData = await getESTAllotmentAcknowledgementData(finalMutation.data, tenantInfo, t);
      Digit.Utils.pdf.generate(pdfData);
    } catch (err) {
      console.error("PDF generation error:", err);
    }
  };

  /* ---------------- UI ---------------- */

  if (finalMutation.isLoading) {
    return <Loader />;
  }

  return (
    <Card>
      <BannerPicker t={t} isSuccess={finalMutation.isSuccess} data={finalMutation.data} />

      <StatusTable>
        <Row rowContainerStyle={rowContainerStyle} last />
      </StatusTable>

      {finalMutation.isSuccess && (
        <SubmitBar label={t("EST_ALLOTMENT_ACKNOWLEDGEMENT")} onSubmit={handleDownloadPdf} />
      )}

      <Link to={user?.type === "CITIZEN" ? "/upyog-ui/citizen" : "/upyog-ui/employee"}>
        <LinkButton label={t("CORE_COMMON_GO_TO_HOME")} />
      </Link>
    </Card>
  );
};

export default ESTAllotmentAcknowledgement;
