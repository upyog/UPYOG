import React, { useEffect, useState,Fragment } from "react";
import {
  Card,
  CardHeader,
  CardSubHeader,
  StatusTable,
  Row,
  LinkButton,
  SubmitBar,
  CheckBox,
  EditIcon,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { checkForNA, ESTDocumnetPreview, formatEpochDate } from "../../../utils"; // ensure path is correct

/* =========================================================
   Action Button Component
   ========================================================= */

/**
 * ActionButton
 * ---------------------------------------------------------
 * Displays an edit icon button that navigates
 * the user back to a specific form step
 */
const ActionButton = ({ jumpTo }) => {
  const navigate = Digit.Hooks.useCustomNavigate();
  return <LinkButton label={<EditIcon />} onClick={() => navigate(jumpTo)} />;
};

/* =========================================================
   Main Check Page Component
   ========================================================= */

/**
 * ESTAssignAssetsCheckPage
 * ---------------------------------------------------------
 * This component:
 * - Displays a complete summary of asset allotment details
 * - Shows uploaded document previews
 * - Collects final declaration
 * - Submits data for final processing
 */

const ESTAssignAssetsCheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const [agree, setAgree] = useState(false);
  const [previewDocs, setPreviewDocs] = useState([]);
  const [loadingDocs, setLoadingDocs] = useState(false);

  const AssignAssetsData = value?.AssignAssetsData?.AllotmentData || {};

  //  helper to show Rent / Lease nicely
  const getAllotmentTypeLabel = () => {
    const code = AssignAssetsData?.allotmentType;
    if (!code) return t("NA");

    if (code === "RENT")  return t("EST_ALLOTMENT_TYPE_RENT")  || "Rent";
    if (code === "LEASE") return t("EST_ALLOTMENT_TYPE_LEASE") || "Lease";

    // fallback – if some other code comes in future
    return code;
  };

   /* =========================================================
     Single File Open Handler (Unused but retained)
     ========================================================= */

  /**
   * Opens a document in a new browser tab
   */

  const handleFileOpen = (fileId) => {
    if (!fileId) return;
    Digit.UploadServices.Filefetch([fileId], Digit.ULBService.getStateId())
      .then((res) => {
        const fileStoreIdsArray = res?.data?.fileStoreIds;
        if (Array.isArray(fileStoreIdsArray) && fileStoreIdsArray.length > 0) {
          const url = fileStoreIdsArray[0]?.url?.split(",")[0];
          url ? window.open(url, "_blank") : console.error("No valid URL found!");
        }
      })
      .catch((err) => console.error("Error fetching file:", err));
  };

  useEffect(() => {
    let mounted = true;

    const docFields = [
      { key: "citizenLetter", label: t("EST_CITIZEN_REQUEST_LETTER") },
      { key: "allotmentLetter", label: t("EST_ALLOTMENT_LETTER") },
      { key: "signedDeed", label: t("EST_SIGNED_DEED") },
    ];

    const filestoreIds = [];
    const idToLabelMap = {};

    docFields.forEach((field) => {
      const obj = AssignAssetsData?.[field.key];
      if (obj?.filestoreId) {
        filestoreIds.push(obj.filestoreId);
        idToLabelMap[obj.filestoreId] = field.label;
      }
    });

    if (filestoreIds.length === 0) {
      if (mounted) setPreviewDocs([]);
      return () => {
        mounted = false;
      };
    }

    setLoadingDocs(true);

    Digit.UploadServices.Filefetch(filestoreIds, Digit.ULBService.getStateId())
      .then((res) => {
        if (!mounted) return;

        const arr = res?.data?.fileStoreIds;
        const values = [];

        if (Array.isArray(arr)) {
          arr.forEach((fsObj) => {
            const fsid = fsObj?.fileStoreId || fsObj?.id;
            const url = fsObj?.url?.split(",")[0];
            if (url) {
              values.push({
                url,
                title: idToLabelMap[fsid] || t("DOCUMENT"),
                documentType: fsid,
              });
            }
          });
        }

        const ordered = filestoreIds
          .map((id) => values.find((v) => v.documentType === id))
          .filter(Boolean);

        if (mounted) setPreviewDocs(ordered.length ? [{ values: ordered }] : []);
      })
      .catch((err) => {
        console.error("Error fetching files for preview:", err);
        if (mounted) setPreviewDocs([]);
      })
      .finally(() => mounted && setLoadingDocs(false));

    return () => {
      mounted = false;
    };
  }, [AssignAssetsData, t]);

  return (
    <Card>
      <CardHeader>{t("EST_ASSIGN_ASSETS_SUMMARY")}</CardHeader>

      {/* ----------------- ASSET DETAILS ----------------- */}
      <CardSubHeader>{t("EST_ASSET_DETAILS")}</CardSubHeader>
      <StatusTable>
        <Row
          label={t("EST_ASSET_NUMBER")}
          text={checkForNA(value?.assetData?.estateNo)}
          actionButton={
            <ActionButton jumpTo={`/upyog-ui/employee/est/assignassets/assign-assets`} />
          }
        />
        <Row
          label={t("EST_BUILDING_NAME")}
          text={checkForNA(value?.assetData?.buildingName)}
        />
        <Row label={t("EST_LOCALITY")} text={checkForNA(value?.assetData?.locality)} />
        <Row label={t("EST_TOTAL_AREA")} text={checkForNA(value?.assetData?.totalFloorArea)} />
        <Row label={t("EST_FLOOR")} text={checkForNA(value?.assetData?.floor)} />
        <Row label={t("EST_RATE")} text={checkForNA(value?.assetData?.rate)} />
      </StatusTable>

      {/* ----------------- PERSONAL DETAILS ----------------- */}
      <CardSubHeader>{t("EST_PERSONAL_DETAILS_OF_ALLOTTEE")}</CardSubHeader>
      <StatusTable> 
        <Row
          label={t("EST_ALLOTMENT_TYPE")}
          text={checkForNA(getAllotmentTypeLabel())}
        />
        <Row label={t("EST_ALLOTTEE_NAME")} text={checkForNA(AssignAssetsData?.allotteeName)} />
        <Row label={t("EST_PHONE_NUMBER")} text={checkForNA(AssignAssetsData?.phoneNumber)} />
        <Row
          label={t("EST_ALTERNATE_PHONE_NUMBER")}
          text={checkForNA(AssignAssetsData?.altPhoneNumber)}
        />
        <Row label={t("EST_EMAIL_ID")} text={checkForNA(AssignAssetsData?.email)} />
      </StatusTable>

      {/* ----------------- AGREEMENT DETAILS ----------------- */}
      <CardSubHeader>{t("EST_AGREEMENT_DETAILS")}</CardSubHeader>
      <StatusTable>
        <Row
          label={t("EST_AGREEMENT_START_DATE")}
          text={formatEpochDate(AssignAssetsData?.startDate)}
        />
        <Row
          label={t("EST_AGREEMENT_END_DATE")}
          text={formatEpochDate(AssignAssetsData?.endDate)}
        />
        <Row
          label={t("EST_DURATION_IN_YEARS")}
          text={checkForNA(AssignAssetsData?.duration)}
        />
        <Row
          label={t("EST_RATE_PER_SQFT")}
          text={checkForNA(AssignAssetsData?.rate)}
        />
        <Row
          label={t("EST_MONTHLY_RENT_IN_INR")}
          text={checkForNA(AssignAssetsData?.monthlyRent)}
        />
        <Row
          label={t("EST_ADVANCE_PAYMENT_IN_INR")}
          text={checkForNA(AssignAssetsData?.advancePayment)}
        />
        <Row
          label={t("EST_ADVANCE_PAYMENT_DATE")}
          text={formatEpochDate(AssignAssetsData?.advancePaymentDate)}
        />
        <Row
          label={t("EST_EOFFICE_FILE_NO")}
          text={checkForNA(AssignAssetsData?.eOfficeFileNo)}
        />
      </StatusTable>

      {/* ----------------- DOCUMENT PREVIEW ----------------- */}
      <CardSubHeader>{t("EST_DOCUMENT_PREVIEW")}</CardSubHeader>

      {loadingDocs ? (
        <div style={{ padding: "12px 16px" }}>{t("CS_LOADING")}</div>
      ) : previewDocs.length > 0 ? (
        <div style={{ paddingTop: 8 }}>
          <ESTDocumnetPreview
            documents={previewDocs}
            titleStyles={{ fontSize: "14px" }}
            pdfSize={48}
            labelWidth={220}
          />
        </div>
      ) : (
        <div style={{ padding: "8px 16px", color: "#666" }}>
          {t("EST_NO_DOCUMENTS_UPLOADED_LABEL")}
        </div>
      )}

      {/* ----------------- DECLARATION + SUBMIT ----------------- */}
      <div style={{ marginTop: 16 }}>
        <CheckBox
          label={t("EST_FINAL_DECLARATION_MESSAGE")}
          onChange={() => setAgree(!agree)}
          value={agree}
        />
      </div>

      <div style={{ marginTop: 12 }}>
        <SubmitBar
          label={t("EST_COMMON_SUBMIT")}
          onSubmit={onSubmit}
          disabled={!agree}
        />
      </div>
    </Card>
  );
};

export default ESTAssignAssetsCheckPage;