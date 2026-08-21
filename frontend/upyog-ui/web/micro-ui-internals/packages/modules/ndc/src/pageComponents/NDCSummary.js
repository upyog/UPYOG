import "../../css/ndc.css";
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

  /*
   * =========================================================
   * SUMMARY CARD
   * =========================================================
   */

  /*
   * =========================================================
   * TITLE
   * =========================================================
   */

  /*
   * =========================================================
   * SECTION STYLES
   * =========================================================
   */

  /*
   * =========================================================
   * ROW STYLES
   * =========================================================
   */

  /*
   * =========================================================
   * DOCUMENT STYLES
   * =========================================================
   */

  /*
   * =========================================================
   * DECLARATION
   * =========================================================
   */

  /*
   * =========================================================
   * SUBMIT
   * =========================================================
   */

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
    <div className="ndc-css-fcfffca2" >
      <div className="ndc-css-f8a36f7d" >{label}</div>

      <div className="ndc-css-500ff7b4" >{renderValue(value)}</div>
    </div>
  );

  const renderSectionHeader = (title, onEdit) => (
    <div className="ndc-css-0913a7ec" >
      <div className="ndc-css-70fcc8ba" >{title}</div>

      <button className="ndc-css-76f24cb8" type="button"  title={t("Edit")} onClick={onEdit}>
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
    <div className="ndc-css-1e54a5cc" >
      {/* ===================================================
          SUMMARY CARD
          Back button intentionally removed
      ==================================================== */}

      <div className="ndc-css-1afa1bd6" >
        {/* =================================================
            HEADER
        ================================================== */}

        <h2 className="ndc-css-d5535c12" >{t("Summary")}</h2>

        <div className="ndc-css-446ce61a" >{t("Check Your Details")}</div>

        {/* =================================================
            APPLICANT DETAILS
        ================================================== */}

        <div className="ndc-css-d175ed94" >
          {renderSectionHeader(t("Applicant Details"), handleEdit)}

          {renderLabel(t("Full Name"), fullName)}

          {renderLabel(t("Mobile Number"), mobileNumber)}

          {renderLabel(t("Email ID"), email)}
        </div>

        {/* =================================================
            APPLICATION DETAILS
        ================================================== */}

        <div className="ndc-css-d175ed94" >
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

        <div className="ndc-css-d175ed94" >
          {renderSectionHeader(t("Documents"), () => {})}

          {docs?.length > 0 ? (
            <div className="ndc-css-f9744310" >
              {docs.map((doc, index) => (
                <div className="ndc-css-0ec02b21" key={`${doc?.documentType || "document"}-${index}`} >
                  <NDCDocument value={docs} Code={doc?.documentType} index={index} formData={formData} />
                </div>
              ))}
            </div>
          ) : (
            <div className="ndc-css-53470f08"
              
            >
              {t("TL_NO_DOCUMENTS_MSG")}
            </div>
          )}
        </div>

        {/* =================================================
            DECLARATION
        ================================================== */}

        <div className="ndc-css-dea39455" >
          <input className="ndc-css-3870986f" type="checkbox"  />

          <div className="ndc-css-c1ad7f1a" >
            {t(
              "I hereby declare and affirm that the above-furnished information is true and correct and nothing has been concealed therefrom. I am also aware of the fact that in case this information is found false/incorrect, the authorities are at liberty to initiate recovery of amount/interest/penalty/fine as provided in UPYOG Municipal Act 1911 or UPYOG Municipal Corporation Act 1976.",
            )}
          </div>
        </div>

        {/* =================================================
            SUBMIT BUTTON
        ================================================== */}

        <div className="ndc-css-b2739e19" >
          <SubmitBar label={t("Submit")} onSubmit={handleSubmit} />
        </div>
      </div>
    </div>
  );
};

export default NDCSummary;
