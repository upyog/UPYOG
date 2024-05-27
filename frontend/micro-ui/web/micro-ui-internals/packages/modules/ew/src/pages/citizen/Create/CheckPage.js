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
import Timeline from "../../../components/EWASTETimeline";

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
    pets,
    index = 0,    // set the initial value for tesing ,  need to check why when click on change it will not coming in address page and document page 
    isEditPET,
    isUpdatePET,
    ownerKey,
    ewdet

  } = value;

  // const typeOfApplication = !isEditPET && !isUpdatePET ? `new-application` : `edit-application`;


  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      <Card>
        <CardHeader>{t("EWASTE_CHECK_YOUR_DETAILS")}</CardHeader>
        <div>
          <br></br>



          <CardSubHeader>{t("EWASTE_TITLE_OWNER_DETAILS")}</CardSubHeader>
          <br></br>
          <StatusTable>
            <Row
              label={t("EWASTE_APPLICANT_NAME")}
              text={`${t(checkForNA(ownerKey?.applicantName))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/ew-common/raiseRequest/productdetails/`}`} />}
              // actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/owners/`}${index}`} />}
            />

            <Row
              label={t("EWASTE_MOBILE_NUMBER")}
              text={`${t(checkForNA(ownerKey?.mobileNumber))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/ew-common/raiseRequest/productdetails/`}`} />}
            />

            <Row
              label={t("EWASTE_LOCALITY")}
              text={`${t(checkForNA(ownerKey?.locality?.code))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/ew-common/raiseRequest/productdetails/`}`} />}
            />

            <Row
              label={t("EWASTE_COMPLETE_ADDRESS")}
              text={`${t(checkForNA(ownerKey?.address))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/ew-common/raiseRequest/productdetails/`}`} />}
            />

            <Row
              label={t("EWASTE_VENDOR_NAME")}
              text={`${t(checkForNA(ownerKey?.vendor?.code))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/ew-common/raiseRequest/productdetails/`}`} />}
            />

          </StatusTable>
          <br></br>

          <CardSubHeader>{t("EWASTE_TITLE_PRODUCT_DETAILS")}</CardSubHeader>
          <br></br>
          {/* <p><i>!!Under Construction!!</i></p> */}
          <StatusTable>
        <Row
            label={t("EWASTE_SEARCH_PRODUCT_NAME")}
            text={`${t(checkForNA(ewdet?.productName?.code))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/ew-common/raiseRequest/productdetails/`}`} />}
        />

        <Row
            label={t("EWASTE_SEARCH_QUANTITY")}
            text={`${t(checkForNA(ewdet?.productQuantity))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/ew-common/raiseRequest/productdetails/`}`} />}
        />

        <Row
            label={t("EWASTE_SEARCH_TOTAL_PRICE")}
            text={`${t(checkForNA(ewdet.productPrice))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/ew-common/raiseRequest/productdetails/`}`} />}
        />


        </StatusTable>
          <br></br>
          {/* <CardSubHeader>{t("PTR_LOCATION_DETAILS")}</CardSubHeader>
        <br></br> */}
          {/* <StatusTable>
        <Row
            label={t("PTR_HOUSE_NO")}
            text={`${t(checkForNA(address?.doorNo))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/street/`}${index}`} />}
        />
         <Row
            label={t("PTR_STREET_NAME")}
            text={`${t(checkForNA(address?.street))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/street/`}${index}`} />}
        />
         <Row
            label={t("PTR_ADDRESS_LINE1")}
            text={`${t(checkForNA(address?.addressLine1))}`}
            actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/street/`}${index}`} />}
        />
         
         <Row
            label={t("PTR_ADDRESS_PINCODE")}
            text={`${t(checkForNA(address?.pincode))}`}
            // actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pincode/`}${index}`} />}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/pincode`} />}
        />
         <Row
            label={t("MYCITY_CODE_LABEL")}
            text={`${t(checkForNA(address?.city?.name))}`}
            // actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address/`}${index}`} />}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />
         <Row
            label={t("PTR_LOCALITY")}
            text={`${t(checkForNA(address?.locality?.name))}`}
            // actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address/`}${index}`} />}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/address`} />}
        />

        </StatusTable>
        <br></br> */}


          <CheckBox
          label={t("EWASTE_FINAL_DECLARATION_MESSAGE")}
          onChange={setdeclarationhandler}
          styles={{ height: "auto" }}
          //disabled={!agree}
        />
        </div>
        <SubmitBar label={t("EWASTE_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
    </React.Fragment>
  );
};

export default CheckPage;