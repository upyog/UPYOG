import {
  Card,
  CardHeader,
  CardSubHeader,
  CardText,
  CheckBox,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar
} from "@egovernments/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import {
  checkForNA,
  getFixedFilename, 
} from "../../../utils";
import Timeline from "../../../components/TLTimeline";

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
  const history = useHistory();

  //console.log("onSubmit",onSubmit )

  //console.log("checkpage",value)

  const {
    address,
    pets,
    index,
   
   
    
    
    isEditProperty,
    isUpdateProperty,
   
    ownerss,

    
    
    
  } = value;

  

  //console.log("value", value)
  const typeOfApplication = !isEditProperty && !isUpdateProperty ? `new-application` : `edit-application`;


  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  return (
    <React.Fragment>
     {window.location.href.includes("/citizen") ? <Timeline currentStep={5}/> : null}
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
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/owner-details/`}${index}`} />}
        />

        <Row
            label={t("PTR_FATHER_HUSBAND_NAME")}
            text={`${t(checkForNA(ownerss?.fatherName))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/owner-details/`}${index}`} />}
        />

        <Row
            label={t("PTR_MOBILE_NUMBER")}
            text={`${t(checkForNA(ownerss?.mobileNumber))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/owner-details/`}${index}`} />}
        />

        <Row
            label={t("PTR_EMAIL_ID")}
            text={`${t(checkForNA(ownerss?.emailId))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/owner-details/`}${index}`} />}
        />

        </StatusTable>
        <br></br>

        <CardSubHeader>{t("ES_TITILE_PET_DETAILS")}</CardSubHeader>
        <br></br>
        <StatusTable>
        <Row
            label={t("PTR_SEARCH_PET_TYPE")}
            text={`${t(checkForNA(pets?.petType?.value))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/pet-details/`}${index}`} />}
        />

        <Row
            label={t("PTR_SEARCH_BREED_TYPE")}
            text={`${t(checkForNA(pets?.breedType?.value))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/pet-details/`}${index}`} />}
        />

        <Row
            label={t("PTR_PET_NAME")}
            text={`${t(checkForNA(pets?.petName))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/pet-details/`}${index}`} />}
        />

        <Row
            label={t("PTR_DOCTOR_NAME")}
            text={`${t(checkForNA(pets?.doctorName))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/pet-details/`}${index}`} />}
        />

        <Row
            label={t("PTR_CLINIC_NAME")}
            text={`${t(checkForNA(pets?.clinicName))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/pet-details/`}${index}`} />}
        />

        <Row
            label={t("PTR_VACCINATED_DATE")}
            text={`${t(checkForNA(pets?.lastVaccineDate))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/pet-details/`}${index}`} />}
        />

        </StatusTable>
        <br></br>
        <CardSubHeader>{t("PTR_LOCATION_DETAILS")}</CardSubHeader>
        <br></br>
        <StatusTable>
        <Row
            label={t("PTR_HOUSE_NO")}
            text={`${t(checkForNA(address?.doorNo))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/street/`}${index}`} />}
        />
         <Row
            label={t("PTR_STREET_NAME")}
            text={`${t(checkForNA(address?.street))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/street/`}${index}`} />}
        />
         <Row
            label={t("PTR_ADDRESS_LINE1")}
            text={`${t(checkForNA(address?.addressLine1))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/street/`}${index}`} />}
        />
         
         <Row
            label={t("PTR_ADDRESS_PINCODE")}
            text={`${t(checkForNA(address?.pincode))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/pincode/`}${index}`} />}
        />
         <Row
            label={t("MYCITY_CODE_LABEL")}
            text={`${t(checkForNA(address?.city?.name))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/address/`}${index}`} />}
        />
         <Row
            label={t("PTR_LOCALITY")}
            text={`${t(checkForNA(address?.locality?.name))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/pt/property/${typeOfApplication}/address/`}${index}`} />}
        />

        </StatusTable>
        <br></br>
       
       
        <CheckBox
          label={t("PT_FINAL_DECLARATION_MESSAGE")}
          onChange={setdeclarationhandler}
          styles={{ height: "auto" }}
          //disabled={!agree}
        />
      </div>
      <SubmitBar label={t("PT_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
    </Card>
   </React.Fragment>
  );
};

export default CheckPage;