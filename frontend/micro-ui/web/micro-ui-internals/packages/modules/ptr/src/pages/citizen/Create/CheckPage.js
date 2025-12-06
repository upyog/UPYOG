/**
 * CheckPage Component
 * 
 * This component displays a summary of the pet registration application details,
 * including owner information, pet details, and location details.
 * It allows users to review their entered information before final submission.
 * 
 * Features:
 * - Displays details in categorized sections using `StatusTable` and `Row` components.
 * - Provides navigation buttons (`ActionButton`) to edit specific sections.
 * - Includes a declaration checkbox that must be checked before submitting.
 * - Uses `useTranslation` for localization and supports multilingual labels.
 * - Implements routing with `useHistory` for navigation between pages.
 */

import {
  Card,
  CardHeader,
  CardSubHeader,
  CheckBox,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar
} from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import {
  checkForNA 
} from "../../../utils";
import Timeline from "../../../components/PTRTimeline";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    history.push(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" onClick={routeTo} />;
};

const CheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const {
    address,
    pets,   
    isEditPET,
    isUpdatePET,
    ownerss,
  } = value;
  const typeOfApplication = !isEditPET && !isUpdatePET ? `new-application` : `edit-application`;


  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  return (
    <React.Fragment>
     {<Timeline currentStep={5}/>}
    <Card>
      <CardHeader>{t("PTR_CHECK_YOUR_DETAILS")}</CardHeader>
      <div>
        <br></br>
        <CardSubHeader>{t("ES_TITILE_OWNER_DETAILS")}</CardSubHeader>
        <br></br>
        <StatusTable>
        <Row
            label={t("PTR_APPLICANT_NAME")}
            text={`${t(checkForNA(ownerss?.applicantName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/owners`} />}
        />
        <Row
            label={t("PTR_FATHER_HUSBAND_NAME")}
            text={`${t(checkForNA(ownerss?.fatherName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/owners`} />}
        />
        <Row
            label={t("PTR_MOBILE_NUMBER")}
            text={`${t(checkForNA(ownerss?.mobileNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/owners`} />}
        />
        <Row
            label={t("PTR_EMAIL_ID")}
            text={`${t(checkForNA(ownerss?.emailId))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/owners`} />}
        />
        </StatusTable>
        <br></br>
        <CardSubHeader>{t("ES_TITILE_PET_DETAILS")}</CardSubHeader>
        <br></br>
        <StatusTable>
        <Row
            label={t("PTR_SEARCH_PET_TYPE")}
            text={`${t(checkForNA(pets?.petType?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />

        <Row
            label={t("PTR_SEARCH_BREED_TYPE")}
            text={`${t(checkForNA(pets?.breedType?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />

        <Row
            label={t("PTR_PET_NAME")}
            text={`${t(checkForNA(pets?.petName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />
        <Row
            label={t("PTR_DOCTOR_NAME")}
            text={`${t(checkForNA(pets?.doctorName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />

        <Row
            label={t("PTR_CLINIC_NAME")}
            text={`${t(checkForNA(pets?.clinicName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />
        <Row
            label={t("PTR_VACCINATED_DATE")}
            text={`${t(checkForNA(pets?.lastVaccineDate))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />
         <Row
            label={t("PTR_PET_AGE")}
            text={`${t(checkForNA(pets?.petAge + " Months"))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />
         <Row
            label={t("PTR_PET_SEX")}
            text={`${t(checkForNA(pets?.petGender?.name))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />
          <Row
            label={t("PTR_IDENTIFICATION_MARK")}
            text={`${t(checkForNA(pets?.identificationMark))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />
          <Row
            label={t("PTR_PET_COLOR")}
            text={`${t(checkForNA(pets?.petColor?.code))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />
          <Row
            label={t("PTR_VACCINATION_NUMBER")}
            text={`${t(checkForNA(pets?.vaccinationNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />
          <Row
            label={t("PTR_SEARCH_BIRTH_ADOPTION")}
            text={`${t(checkForNA(pets?.adoptionDate ? pets?.adoptionDate : pets?.birthDate))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pet-details`} />}
        />

        </StatusTable>
        <br></br>
        <CardSubHeader>{t("PTR_LOCATION_DETAILS")}</CardSubHeader>
        <br></br>
        <StatusTable>
        <Row
            label={t("PTR_HOUSE_NO")}
            text={`${t(checkForNA(address?.houseNo))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />
        {/* If data in houseName, then only it will render */}
        {address?.houseName && <Row
            label={t("PTR_HOUSE_NAME")}
            text={`${t(checkForNA(address?.houseName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />}
         <Row
            label={t("PTR_STREET_NAME")}
            text={`${t(checkForNA(address?.streetName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />
         <Row
            label={t("PTR_ADDRESS_LINE1")}
            text={`${t(checkForNA(address?.addressline1))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}

        />
        {/* If data in addressline 2, then only it will render */}
        {address?.addressline2 && <Row
            label={t("PTR_ADDRESS_LINE2")}
            text={`${t(checkForNA(address?.addressline2))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />}
         <Row
            label={t("PTR_ADDRESS_PINCODE")}
            text={`${t(checkForNA(address?.pincode))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />
         <Row
            label={t("MYCITY_CODE_LABEL")}
            text={`${t(checkForNA(address?.city?.name))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />
         <Row
            label={t("PTR_LOCALITY")}
            text={`${t(checkForNA(address?.locality?.i18nKey))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />
          <Row
            label={t("PTR_LANDMARK")}
            text={`${t(checkForNA(address?.landmark))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />
        </StatusTable>
        <br></br>
       
       
        <CheckBox
          label={t("PTR_FINAL_DECLARATION_MESSAGE")}
          onChange={setdeclarationhandler}
          styles={{ height: "auto" }}
        />
      </div>
      <SubmitBar label={t("PTR_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
    </Card>
   </React.Fragment>
  );
};

export default CheckPage;