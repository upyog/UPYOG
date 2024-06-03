import {
  Card,
  CardHeader,
  CardSubHeader,
  CardText,
  CheckBox,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
} from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA, getFixedFilename } from "../../../utils";
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
    index = 0, // set the initial value for tesing ,  need to check why when click on change it will not coming in address page and document page
    isEditPET,
    isUpdatePET,
    ownerKey,
    vendorKey,
    ewdet,
  } = value;

  // const typeOfApplication = !isEditPET && !isUpdatePET ? `new-application` : `edit-application`;

  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={5} /> : null}
      <Card>
        <CardHeader>{t("EWASTE_CHECK_YOUR_DETAILS")}</CardHeader>
        <div>
          <br></br>

          <CardSubHeader>{t("EWASTE_TITLE_PRODUCT_DETAILS")}</CardSubHeader>
          <br></br>
          <p><i>!!Under Construction!!</i></p>
          {/* <StatusTable>
            <Row
              label={t("EWASTE_SEARCH_PRODUCT_NAME")}
              text={`${t(checkForNA(ewdet?.productName?.code))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/productdetails/`}`} />}
            />

            <Row
              label={t("EWASTE_SEARCH_QUANTITY")}
              text={`${t(checkForNA(ewdet?.productQuantity))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/productdetails/`}`} />}
            />

            <Row
              label={t("EWASTE_SEARCH_TOTAL_PRICE")}
              text={`${t(checkForNA(ewdet.productPrice))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/productdetails/`}`} />}
            />
          </StatusTable> */}
          <br></br>

          <CardSubHeader>{t("EWASTE_TITLE_OWNER_DETAILS")}</CardSubHeader>
          <br></br>
          <StatusTable>
            <Row
              label={t("EWASTE_APPLICANT_NAME")}
              text={`${t(checkForNA(ownerKey?.applicantName))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/owner-details`}`} />}
              // actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ptr/petservice/${typeOfApplication}/owners/`}${index}`} />}
            />

            <Row
              label={t("EWASTE_MOBILE_NUMBER")}
              text={`${t(checkForNA(ownerKey?.mobileNumber))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/owner-details`}`} />}
            />

            <Row
              label={t("EWASTE_EMAIL")}
              text={`${t(checkForNA(ownerKey?.emailId))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/owner-details`}`} />}
            />
          </StatusTable>
          <br></br>

          <CardSubHeader>{t("EWASTE_TITLE_VENDOR_DETAILS")}</CardSubHeader>
          <br></br>
          <StatusTable>
            <Row
              label={t("EWASTE_VENDOR_NAME")}
              text={`${t(checkForNA(vendorKey?.vendor?.code))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/vendor-details`}`} />}
            />
          </StatusTable>
          <br></br>

          <CardSubHeader>{t("EWASTE_TITLE_ADDRESS_DETAILS")}</CardSubHeader>
          <br></br>
          <StatusTable>
            <Row
              label={t("EWASTE_SEARCH_PINCODE")}
              text={`${t(checkForNA(address?.pincode))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/pincode`}`} />}
            />

            <Row
              label={t("EWASTE_SEARCH_CITY")}
              text={`${t(checkForNA(address?.city?.name))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/address`}`} />}
            />

            <Row
              label={t("EWASTE_SEARCH_STREET_NAME")}
              text={`${t(checkForNA(address?.street))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/street`}`} />}
            />

            <Row
              label={t("EWASTE_SEARCH_HOUSE_NO")}
              text={`${t(checkForNA(address?.doorNo))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/street`}`} />}
            />

            <Row
              label={t("EWASTE_SEARCH_HOUSE_NAME")}
              text={`${t(checkForNA(ewdet?.productPrice))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/street`}`} />}
            />

            <Row
              label={t("EWASTE_SEARCH_ADDRESS_LINE_1")}
              text={`${t(checkForNA(ewdet?.productPrice))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/street`}`} />}
            />

            <Row
              label={t("EWASTE_SEARCH_ADDRESS_LINE_2")}
              text={`${t(checkForNA(ewdet?.productPrice))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/street`}`} />}
            />

            <Row
              label={t("EWASTE_SEARCH_LANDMARK")}
              text={`${t(checkForNA(ewdet?.productPrice))}`}
              actionButton={<ActionButton jumpTo={`${`/digit-ui/citizen/ew/raiseRequest/street`}`} />}
            />
          </StatusTable>
          <br></br>

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
