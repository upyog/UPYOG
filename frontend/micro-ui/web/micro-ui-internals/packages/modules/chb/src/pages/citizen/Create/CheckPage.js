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
} from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import {
  checkForNA,
  getFixedFilename, 
} from "../../../utils";
import Timeline from "../../../components/CHBTimeline";

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
  

  

  

  const {
    address,
    slots,
    index,    
    isEditCHB,
    isUpdateCHB,
    ownerss,
   
  } = value;



  const typeOfApplication = !isEditCHB && !isUpdateCHB ? `bookHall` : `edit-application`;


  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  return (
    <React.Fragment>
     {window.location.href.includes("/citizen") ? <Timeline currentStep={5}/> : null}
    <Card>
      <CardHeader>{t("CHB_CHECK_YOUR_DETAILS")}</CardHeader>
      <div>
        <br></br>

        <CardSubHeader>{t("BOOKING_DETAILS")}</CardSubHeader>
        <br></br>
        <StatusTable>
        <Row
            label={t("CHB_APPLICANT_NAME")}
            text={`${t(checkForNA(ownerss?.applicantName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />

        <Row
            label={t("CHB_FATHER_HUSBAND_NAME")}
            text={`${t(checkForNA(ownerss?.fatherName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />

        <Row
            label={t("CHB_MOBILE_NUMBER")}
            text={`${t(checkForNA(ownerss?.mobileNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />

        <Row
            label={t("CHB_EMAIL_ID")}
            text={`${t(checkForNA(ownerss?.emailId))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />

        </StatusTable>
        <br></br>

        <CardSubHeader>{t("SLOT_DETAILS")}</CardSubHeader>
        <br></br>
        <StatusTable>
        <Row
            label={t("SELECT_SLOT")}
            text={`${t(checkForNA(slots?.selectslot?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/slot-details`} />}

        />

        <Row
            label={t("RESIDENT_TYPE")}
            text={`${t(checkForNA(slots?.residenttype?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/slot-details`} />}
        />

        <Row
            label={t("SPECIAL_CATEGORY")}
            text={`${t(checkForNA(slots?.specialcategory.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/slot-details`} />}

        />

        <Row
            label={t("PURPOSE")}
            text={`${t(checkForNA(slots?.purpose.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/slot-details`} />}

        />

        </StatusTable>
        <br></br>
        <CardSubHeader>{t("BANK_DETAILS")}</CardSubHeader>
        <br></br>
        <StatusTable>
        <Row
            label={t("ACCOUNT_NUMBER")}
            text={`${t(checkForNA(address?.AccountNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}

        />
         <Row
            label={t("CONFIRM_ACCOUNT_NUMBER")}
            text={`${t(checkForNA(address?.ConfirmAccountNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}

        />
         <Row
            label={t("IFSC_CODE")}
            text={`${t(checkForNA(address?.IFSC))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}

        />
         
         <Row
            label={t("BANK_NAME")}
            text={`${t(checkForNA(address?.BankName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}
        />
         <Row
            label={t("BANK_BRANCH_NAME")}
            text={`${t(checkForNA(address?.BankBranchName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}
        />
         <Row
            label={t("ACCOUNT_HOLDER_NAME")}
            text={`${t(checkForNA(address?.AccountHolderName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}
        />

        </StatusTable>
        <StatusTable>
        <br></br>
        <CardSubHeader>{t("DOCUMENTS_DETAILS")}</CardSubHeader>
        <br></br>
        </StatusTable>
        <br></br>
       
        <CheckBox
          label={t("CHB_FINAL_DECLARATION_MESSAGE")}
          onChange={setdeclarationhandler}
          styles={{ height: "auto" }}
          //disabled={!agree}
        />
      </div>
      <SubmitBar label={t("CHB_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
    </Card>
   </React.Fragment>
  );
};

export default CheckPage;