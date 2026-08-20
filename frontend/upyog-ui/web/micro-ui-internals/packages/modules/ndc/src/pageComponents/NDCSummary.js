import React from "react";
import { SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import NDCDocument from "../components/NDCDocument";

const NDCSummary = ({ formData, goNext, onGoBack }) => {
  const { t } = useTranslation();

  /*
   * =========================================================
   * FORM DATA
   * =========================================================
   */

  const propertyDetails = formData?.NDCDetails?.PropertyDetails || {};

  const ndcReasonData = formData?.NDCDetails?.NDCReason;

  const docs = formData?.DocummentDetails?.documents?.documents || [];

  /*
   * =========================================================
   * APPLICANT DETAILS
   * =========================================================
   */

  const fullName = propertyDetails?.firstName || "NA";

  const mobileNumber = propertyDetails?.mobileNumber || "NA";

  const email = propertyDetails?.email || "NA";

  /*
   * =========================================================
   * APPLICATION DETAILS
   * =========================================================
   */

  const address = propertyDetails?.address || "NA";

  const remarks = propertyDetails?.remarks || "NA";

  const propertyId = formData?.NDCDetails?.cpt?.id || formData?.NDCDetails?.cpt?.details?.propertyId || "NA";

  const waterConnection = propertyDetails?.waterConnection || [];

  const sewerageConnection = propertyDetails?.sewerageConnection || [];

  /*
   * =========================================================
   * NDC REASON
   * =========================================================
   */

  const ndcReason = ndcReasonData?.i18nKey === "OTHERS" ? t(ndcReasonData?.reason || "") : ndcReasonData?.i18nKey ? t(ndcReasonData.i18nKey) : "NA";

  /*
   * =========================================================
   * CONNECTION DISPLAY
   * =========================================================
   */

  const waterConnectionValue =
    waterConnection?.length > 0
      ? waterConnection
          .map((item) => item?.connectionNo)
          .filter(Boolean)
          .join(", ") || "NA"
      : "NA";

  const sewerageConnectionValue =
    sewerageConnection?.length > 0
      ? sewerageConnection
          .map((item) => item?.connectionNo)
          .filter(Boolean)
          .join(", ") || "NA"
      : "NA";

  /*
   * =========================================================
   * PAGE STYLES
   * =========================================================
   */

  const pageStyle = {
    width: "100%",
    minHeight: "100%",
    boxSizing: "border-box",
    padding: "10px 0 30px 0",
  };

  /*
   * =========================================================
   * SUMMARY CARD
   * =========================================================
   */

  const summaryCardStyle = {
    backgroundColor: "#ffffff",
    width: "620px",
    maxWidth: "100%",
    margin: "0",
    padding: "18px",
    boxSizing: "border-box",
    borderRadius: "2px",
  };

  /*
   * =========================================================
   * TITLE
   * =========================================================
   */

  const titleStyle = {
    fontSize: "28px",
    lineHeight: "1.2",
    fontWeight: "700",
    color: "#111111",
    margin: "0 0 4px 0",
  };

  const subtitleStyle = {
    fontSize: "13px",
    lineHeight: "1.4",
    color: "#555555",
    marginBottom: "18px",
  };

  /*
   * =========================================================
   * SECTION STYLES
   * =========================================================
   */

  const sectionStyle = {
    width: "100%",
    marginBottom: "18px",
  };

  const sectionHeaderStyle = {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    width: "100%",
    minHeight: "32px",
    borderBottom: "1px solid #cfcfcf",
    boxSizing: "border-box",
  };

  const sectionTitleStyle = {
    fontSize: "16px",
    lineHeight: "1.4",
    fontWeight: "700",
    color: "#111111",
    margin: "0",
    padding: "5px 0",
  };

  const editButtonStyle = {
    border: "none",
    background: "transparent",
    padding: "0",
    margin: "0",
    color: "#b21f2d",
    fontSize: "19px",
    fontWeight: "700",
    lineHeight: "1",
    cursor: "pointer",
  };

  /*
   * =========================================================
   * ROW STYLES
   * =========================================================
   */

  const rowStyle = {
    display: "grid",
    gridTemplateColumns: "42% 58%",
    width: "100%",
    minHeight: "30px",
    borderBottom: "1px solid #d5d5d5",
    boxSizing: "border-box",
  };

  const labelStyle = {
    fontSize: "13px",
    lineHeight: "1.4",
    fontWeight: "700",
    color: "#111111",
    padding: "6px 8px 6px 0",
    boxSizing: "border-box",
  };

  const valueStyle = {
    fontSize: "13px",
    lineHeight: "1.4",
    color: "#333333",
    padding: "6px 0 6px 8px",
    wordBreak: "break-word",
    overflowWrap: "anywhere",
    boxSizing: "border-box",
  };

  /*
   * =========================================================
   * DOCUMENT STYLES
   * =========================================================
   */

  const documentsContainerStyle = {
    display: "flex",
    flexDirection: "row",
    flexWrap: "wrap",
    gap: "10px",
    width: "100%",
    padding: "10px 0",
    boxSizing: "border-box",
  };

  const documentCardStyle = {
    flex: "0 0 120px",
    width: "120px",
    minHeight: "105px",
    backgroundColor: "#ffffff",
    border: "1px solid #d5d5d5",
    borderRadius: "2px",
    padding: "7px",
    boxSizing: "border-box",
  };

  /*
   * =========================================================
   * DECLARATION
   * =========================================================
   */

  const declarationContainerStyle = {
    display: "flex",
    alignItems: "flex-start",
    width: "100%",
    marginTop: "8px",
    padding: "10px 0 0 0",
    borderTop: "1px solid #d0d0d0",
    boxSizing: "border-box",
  };

  const checkboxStyle = {
    width: "15px",
    height: "15px",
    margin: "2px 8px 0 0",
    flexShrink: 0,
  };

  const declarationTextStyle = {
    fontSize: "10px",
    lineHeight: "1.5",
    color: "#333333",
  };

  /*
   * =========================================================
   * SUBMIT
   * =========================================================
   */

  const submitContainerStyle = {
    display: "flex",
    justifyContent: "flex-end",
    width: "100%",
    marginTop: "16px",
    boxSizing: "border-box",
  };

  /*
   * =========================================================
   * HELPERS
   * =========================================================
   */

  const renderValue = (value) => {
    if (value === undefined || value === null || value === "") {
      return "NA";
    }

    return value;
  };

  const renderLabel = (label, value) => (
    <div style={rowStyle}>
      <div style={labelStyle}>{label}</div>

      <div style={valueStyle}>{renderValue(value)}</div>
    </div>
  );

  const renderSectionHeader = (title, onEdit) => (
    <div style={sectionHeaderStyle}>
      <div style={sectionTitleStyle}>{title}</div>

      <button type="button" style={editButtonStyle} title={t("Edit")} onClick={onEdit}>
        ✎
      </button>
    </div>
  );

  /*
   * =========================================================
   * EDIT
   * =========================================================
   */

  const handleEdit = () => {
    if (typeof onGoBack === "function") {
      onGoBack();
    }
  };

  /*
   * =========================================================
   * SUBMIT
   * =========================================================
   */

  const handleSubmit = () => {
    if (typeof goNext === "function") {
      goNext();
    }
  };

  /*
   * =========================================================
   * RENDER
   * =========================================================
   */

  return (
    <div style={pageStyle}>
      {/* ===================================================
          SUMMARY CARD
          Back button intentionally removed
      ==================================================== */}

      <div style={summaryCardStyle}>
        {/* =================================================
            HEADER
        ================================================== */}

        <h2 style={titleStyle}>{t("Summary")}</h2>

        <div style={subtitleStyle}>{t("Check Your Details")}</div>

        {/* =================================================
            APPLICANT DETAILS
        ================================================== */}

        <div style={sectionStyle}>
          {renderSectionHeader(t("Applicant Details"), handleEdit)}

          {renderLabel(t("Full Name"), fullName)}

          {renderLabel(t("Mobile Number"), mobileNumber)}

          {renderLabel(t("Email ID"), email)}
        </div>

        {/* =================================================
            APPLICATION DETAILS
        ================================================== */}

        <div style={sectionStyle}>
          {renderSectionHeader(t("Application Details"), handleEdit)}

          {renderLabel(t("Address"), address)}

          {renderLabel(t("NDC Reason"), ndcReason)}

          {renderLabel(t("Remarks"), remarks)}

          {renderLabel(t("Water Connection"), waterConnectionValue)}

          {renderLabel(t("Sewerage Connection"), sewerageConnectionValue)}

          {renderLabel(t("Property ID"), propertyId)}
        </div>

        {/* =================================================
            DOCUMENTS
        ================================================== */}

        <div style={sectionStyle}>
          {renderSectionHeader(t("Documents"), () => {})}

          {docs?.length > 0 ? (
            <div style={documentsContainerStyle}>
              {docs.map((doc, index) => (
                <div key={`${doc?.documentType || "document"}-${index}`} style={documentCardStyle}>
                  <NDCDocument value={docs} Code={doc?.documentType} index={index} formData={formData} />
                </div>
              ))}
            </div>
          ) : (
            <div
              style={{
                fontSize: "13px",
                color: "#555555",
                padding: "10px 0",
              }}
            >
              {t("TL_NO_DOCUMENTS_MSG")}
            </div>
          )}
        </div>

        {/* =================================================
            DECLARATION
        ================================================== */}

        <div style={declarationContainerStyle}>
          <input type="checkbox" style={checkboxStyle} />

          <div style={declarationTextStyle}>
            {t(
              "I hereby declare and affirm that the above-furnished information is true and correct and nothing has been concealed therefrom. I am also aware of the fact that in case this information is found false/incorrect, the authorities are at liberty to initiate recovery of amount/interest/penalty/fine as provided in UPYOG Municipal Act 1911 or UPYOG Municipal Corporation Act 1976.",
            )}
          </div>
        </div>

        {/* =================================================
            SUBMIT BUTTON
        ================================================== */}

        <div style={submitContainerStyle}>
          <SubmitBar label={t("Submit")} onSubmit={handleSubmit} />
        </div>
      </div>
    </div>
  );
};

export default NDCSummary;
