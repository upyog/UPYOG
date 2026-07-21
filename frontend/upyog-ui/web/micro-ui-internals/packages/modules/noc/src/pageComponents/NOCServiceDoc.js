import { Card, CardHeader, CardSubHeader, CardText, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";

const NOCServiceDoc = ({ t, config, onSelect, userType, formData }) => {
  sessionStorage.removeItem("docReqScreenByBack");

  return (
    <React.Fragment>
      <Card>
        <CardHeader>{t("NOC_REQ_DOCS_HEADER", "Required Documents-Fire NOC")}</CardHeader>
        <div>
          <CardText style={{ color: "red" }}>{t("NOC_PDF_AND_JPG_BOTH_FORMAT_ACCEPTED_IN_DOCUMENT_UPLOAD", "PDF, JPG, PNG formats are accepted")}</CardText>

          {/* a. Proof of Identity */}
          <div>
            <CardSubHeader>{t("NOC_IDENTITY_HEADING", "Proof of Identity (Any 1)")}</CardSubHeader>
            <CardText className="primaryColor">
              {t("NOC_DOC_REQ_SUBTEXT", "One of these documents is needed to apply for this Service")}
            </CardText>
            <CardText className="primaryColor">1. {t("NOC_AADHAR_LABEL", "Aadhar card")}</CardText>
            <CardText className="primaryColor">2. {t("NOC_VOTER_ID_LABEL", "Voter ID Card")}</CardText>
            <CardText className="primaryColor">3. {t("NOC_DRIVING_LICENSE_LABEL", "Driving License")}</CardText>
            <CardText className="primaryColor">4. {t("NOC_PAN_LABEL", "Pan Card")}</CardText>
            <CardText className="primaryColor">5. {t("NOC_PASSPORT_LABEL", "Passport")}</CardText>
            <CardText className="primaryColor">
              {t("NOC_IDENTITY_NOTE", "* In case of multiple/institutional Applicant please provide ID of primary or authorized person")}
            </CardText>
          </div>

          {/* b. Proof of Address */}
          <div>
            <CardSubHeader>{t("NOC_ADDRESS_HEADING", "Proof of Address (Any 1)")}</CardSubHeader>
            <CardText className="primaryColor">
              {t("NOC_DOC_REQ_SUBTEXT", "One of these documents is needed to apply for this Service")}
            </CardText>
            <CardText className="primaryColor">1. {t("NOC_ELECTRICITY_BILL_LABEL", "Electricity Bill")}</CardText>
            <CardText className="primaryColor">2. {t("NOC_DRIVING_LICENSE_LABEL", "Driving License")}</CardText>
            <CardText className="primaryColor">3. {t("NOC_VOTER_ID_LABEL", "Voter ID Card")}</CardText>
            <CardText className="primaryColor">4. {t("NOC_AADHAR_LABEL", "Aadhar Card")}</CardText>
            <CardText className="primaryColor">5. {t("NOC_PAN_LABEL", "Pan Card")}</CardText>
            <CardText className="primaryColor">6. {t("NOC_PASSPORT_LABEL", "Passport")}</CardText>
            <CardText className="primaryColor">
              {t("NOC_ADDRESS_NOTE", "* In case of multiple/institutional Applicant please provide ID of primary or authorized person")}
            </CardText>
          </div>

          {/* c. Building Plan */}
          <div>
            <CardSubHeader>{t("NOC_BUILDING_PLAN_HEADING", "Building Plan")}</CardSubHeader>
            <CardText className="primaryColor">
              {t("NOC_DOC_REQ_SUBTEXT", "One of these documents is needed to apply for this Service")}
            </CardText>
            <CardText className="primaryColor">1. {t("NOC_SITE_PLAN_LABEL", "Site Plan")}</CardText>
            <CardText className="primaryColor">2. {t("NOC_GROUND_FLOOR_PLAN_LABEL", "Ground Floor Plan")}</CardText>
            <CardText className="primaryColor">3. {t("NOC_SECTION_PLAN_LABEL", "Section Plan")}</CardText>
            <CardText className="primaryColor">4. {t("NOC_ELEVATION_PLAN_LABEL", "Elevation Plan")}</CardText>
            <CardText className="primaryColor">5. {t("NOC_BUILT_UP_AREA_STATEMENT_LABEL", "Built-up Area Statement")}</CardText>
            <CardText className="primaryColor">
              {t("NOC_BUILDING_PLAN_NOTE", "* In case of multiple buildings please provide Building plans for all buildings")}
            </CardText>
          </div>

          {/* d. Fire-Fighting Plan */}
          <div>
            <CardSubHeader>{t("NOC_FIRE_FIGHTING_PLAN_HEADING", "Fire-Fighting Plan")}</CardSubHeader>
            <CardText className="primaryColor">
              {t("NOC_DOC_REQ_SUBTEXT", "One of these documents is needed to apply for this Service")}
            </CardText>
            <CardText className="primaryColor">1. {t("NOC_SCHEMATIC_DRAWING_FIRE_FIGHTING_SYSTEM_LABEL", "Schematic drawing of fire fighting system")}</CardText>
            <CardText className="primaryColor">2. {t("NOC_SCHEMATIC_DRAWING_FIRE_DETECTING_SYSTEM_LABEL", "Schematic drawing of fire detecting system")}</CardText>
            <CardText className="primaryColor">
              {t("NOC_FIRE_FIGHTING_PLAN_NOTE", "* In case of multiple buildings please provide Building plans for all buildings")}
            </CardText>
          </div>

          {/* e. Owners Checklist */}
          <div>
            <CardSubHeader>{t("NOC_OWNERS_CHECKLIST_HEADING", "Owners Checklist")}</CardSubHeader>
            <CardText className="primaryColor">
              {t("NOC_DOC_REQ_SUBTEXT", "One of these documents is needed to apply for this Service")}
            </CardText>
            <CardText className="primaryColor">1. {t("NOC_FIRE_AND_LIFE_SAFETY_CHECKLIST_LABEL", "Fire and life safety checklist")}</CardText>
            <CardText className="primaryColor">2. {t("NOC_COPY_OF_PROVISIONAL_FOR_NOC_LABEL", "Copy of Provisional for NoC (if applying for New)")}</CardText>
          </div>
        </div>
        <span>
          <SubmitBar label={t("CS_COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default NOCServiceDoc;
