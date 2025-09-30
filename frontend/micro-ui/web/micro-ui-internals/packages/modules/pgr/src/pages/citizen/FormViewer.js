import React, { useMemo,useState } from "react";
import {
  BreakLine,
  Card,
  CardLabel,
  CardSubHeader,
  CardSectionHeader,
  LabelFieldPair,
  DisplayPhotos,
  ImageViewer
} from "@demodigit/digit-ui-react-components";
import { useTranslation } from "react-i18next";

export const FormViewer = ({ heading, config, apiData }) => {
  const { t } = useTranslation();
  const [imageShownBelowComplaintDetails, setImageToShowBelowComplaintDetails] = useState({});

  const [imageZoom, setImageZoom] = useState(null);
  const renderFieldValue = (field) => {
    const key = field.populators?.name || field.key;
    let value = apiData?.[key];
  
    if (!value && field.value) value = field.value; // fallback to default
    if (!value) return "-";
  
    // Handle custom type for images
    if (field.type === "custom") {
      const images = value || { thumbs: [], fullImage: [] }; // safe fallback
      if (!images.thumbs || !images.thumbs.length) return "-";
  
      return (
        <React.Fragment>
          <DisplayPhotos
            srcs={images.thumbs}
            onClick={(src, idx) => zoomImage(images.fullImage[idx] || src)}
          />
          {imageZoom && <ImageViewer imageSrc={imageZoom} onClose={onCloseImageZoom} />}
        </React.Fragment>
      );
    }
  
    // handle array values (like multi-select)
    if (Array.isArray(value)) return value.join(", ");
  
    return value;
  };
  
  function zoomImage(imageSource, index) {
    setImageZoom(imageSource);
  }
  function zoomImageWrapper(imageSource, index) {
    zoomImage(imageShownBelowComplaintDetails?.fullImage[index]);
  }

  function onCloseImageZoom() {
    setImageZoom(null);
  }
  const formFields = useMemo(
    () =>
      config?.map((section, index, array) => (
        <React.Fragment key={index}>
          <div className="section-header-wrapper">
            <CardSectionHeader>{section.head}</CardSectionHeader>
            <div className="section-header-line" />
          </div>

          <div className="form-section-grid">
            {section.body.map((field, idx) => {
              const shouldSpanFull = field.type === "textarea" || field.populators?.fullWidth;
              const wrapperClass = shouldSpanFull ? "form-field-wrapper textarea-span" : "form-field-wrapper";

              return (
                <div className={wrapperClass} key={idx}>
                  <LabelFieldPair>
                    <CardLabel>
                      {field.label}
                      {field.isMandatory ? " * " : null}
                    </CardLabel>
                    <div
                      className="field"
                      style={{
                        padding: "8px 0",
                        whiteSpace: "pre-wrap",
                        minHeight: field.type === "textarea" ? "80px" : "auto",
                      }}
                    >
                      {renderFieldValue(field)}
                    </div>
                  </LabelFieldPair>
                </div>
              );
            })}
          </div>

          {array.length - 1 === index ? null : <BreakLine />}
        </React.Fragment>
      )),
    [config, apiData, imageZoom]
  );


  return (
    <Card className="styled-form">
      {/* <CardSubHeader className="form-header">{heading}</CardSubHeader> */}
      <div className="form-sections">{formFields}</div>

      <style>
        {`
          .styled-form { background: #fff; border-radius: 14px; padding: 22px; box-shadow: 0 6px 20px rgba(0,0,0,0.06); }
          .form-header { font-size: 20px; font-weight: 700; color: #0056ad; margin-bottom: 18px; }
          .form-header::before {  margin-right: 8px; }
          .form-section-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 18px; margin-bottom: 28px; align-items: start; }
          @media (max-width: 992px) { .form-section-grid { grid-template-columns: repeat(2, 1fr); } }
          @media (max-width: 600px) { .form-section-grid { grid-template-columns: 1fr; } }
          .form-field-wrapper.textarea-span { grid-column: 1 / -1; }
          .section-header-wrapper { margin-bottom: 12px; }
          .section-header-line { height: 2px; background: #eef2f6; border-radius: 2px; margin-top: 8px; }
        `}
      </style>
      <style>
{`
.map-popup-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.map-popup {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  max-width: 600px;
  width: 90%;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.map-popup h3 {
  margin-bottom: 12px;
}

.map-popup .close-btn {
  margin-top: 12px;
  background: #0056ad;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
}

/* ---------- Overall Form ---------- */
.styled-form {
  background: #fff;
  border-radius: 16px;
  padding: 24px 32px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.08);
  max-width: 1200px;
  margin: 0 auto;
}

/* ---------- Page Heading ---------- */
.form-header {
  font-size: 24px;
  font-weight: 700;
  color: #222;
  margin-bottom: 28px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.form-header::before {
  content: "📋";
  font-size: 24px;
}

/* ---------- Section Header ---------- */
.section-header {
  font-size: 18px;
  font-weight: 600;
  color: #444;
  margin-bottom: 12px;
}
.section-divider {
  border: none;
  border-top: 1px solid #eee;
  margin: 0 0 24px 0;
}

/* ---------- Grid Layout ---------- */
.form-section-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 👈 3 columns */
  gap: 20px;
  margin-bottom: 32px;
}
.form-field-wrapper {
  display: flex;
  flex-direction: column;
}
.form-field-wrapper label {
  font-weight: 500;
  margin-bottom: 6px;
  color: #555;
}
.card .card-label, .card-emp .card-label {
  font-size: 16px;
  line-height: 23px;
  font-weight: 600;
  --text-opacity:1;
  color: #0456ad !important;
  
  margin-bottom: 8px; }
.card-section-header{
  color : #0056ad;
}
.card-sub-header form-header{
  color : #0056ad;
}

/* ---------- Error ---------- */
.card-label-error {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}

/* ---------- Sticky Action Bar ---------- */
.sticky-action-bar {
  position: sticky;
  bottom: 0;
  background: #fff;
  border-top: 1px solid #eee;
  padding: 16px 24px;
  display: flex;
  justify-content: flex-end;
  z-index: 10;
  border-radius: 0 0 16px 16px;
}
html, body {
  scroll-behavior: smooth;
}
.styled-form {
  scroll-behavior: smooth;
}
.form-field-full {
  grid-column: 1 / -1; /* 👈 spans all columns */
}
.form-field-full textarea {
  width: 100%;
  min-height: 120px; /* adjust height */
  resize: vertical;  /* user can resize */
}
.styled-form {
  -webkit-overflow-scrolling: touch;
  overflow-y: auto; /* only if height is fixed */
}
.select-wrap .select-active
{
  padding: 0px !important;
}
/* ---------- Responsive ---------- */
@media (max-width: 992px) {
  .form-section-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 600px) {
  .form-section-grid {
    grid-template-columns: 1fr;
  }
}
@media (min-width: 780px) {
  .bill-citizen, .bills-citizen-wrapper, .citizen-all-services-wrapper, .citizen-obps-wrapper, .engagement-citizen-wrapper, .fsm-citizen-wrapper, .mcollect-citizen, .payer-bills-citizen-wrapper, .pgr-citizen-wrapper, .pt-citizen, .selection-card-wrapper, .tl-citizen, .ws-citizen-wrapper {
    width: 100%;
    padding-right: 16px;
    margin-top: 2rem; } }
`}
</style>
    </Card>
  );
};
