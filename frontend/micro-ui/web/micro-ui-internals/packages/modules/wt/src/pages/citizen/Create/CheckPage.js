
import {Card,CardHeader,CardSubHeader,CheckBox,LinkButton,Row,StatusTable,SubmitBar, EditIcon} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA } from "../../../utils";

  const ActionButton = ({ jumpTo }) => {
    const { t } = useTranslation();
    const history = useHistory();
    function routeTo() {
      history.push(jumpTo);
    }
    return <LinkButton 
    label={<EditIcon style={{ marginTop: "-30px", float: "right", position: "relative", bottom: "32px" }} />}
    className="check-page-link-button" onClick={routeTo} />;
  };


  const WTCheckPage = ({ onSubmit, value = {} }) => {
    const { t } = useTranslation();
    const {owner,requestDetails,address} = value;
    const [agree, setAgree] = useState(false);



    const setdeclarationhandler = () => {
      setAgree(!agree);
    };
    

    return (
      <React.Fragment>
      <Card>
        <CardHeader>{t("WT_SUMMARY_PAGE")}</CardHeader>
        <div>
          <CardSubHeader>{t("ES_TITILE_OWNER_DETAILS")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
          <Row
              label={t("COMMON_APPLICANT_NAME")}
              text={`${t(checkForNA(owner?.applicantName))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/citizen/wt/request-service/applicant-details`} />}
          />
          <Row
              label={t("COMMON_MOBILE_NUMBER")}
              text={`${t(checkForNA(owner?.mobileNumber))}`}
          />
          <Row
              label={t("COMMON_BIRTH_DATE")}
              text={`${t(checkForNA(owner?.dateOfBirth))}`}
          />
          <Row
              label={t("SV_GENDER")}
              text={`${t(checkForNA(owner?.gender?.code))}`}
          />
          <Row
              label={t("COMMON_EMAIL_ID")}
              text={`${t(checkForNA(owner?.emailId))}`}
          />
          <Row
              label={t("COMMON_GUARDIAN")}
              text={`${t(checkForNA(owner?.guardianName))}`}
          />
          <Row
              label={t("COMMON_RELATIONTYPE")}
              text={`${t(checkForNA(owner?.relationShipType?.name))}`}
          />
        </StatusTable>
         
          <CardSubHeader>{t("ES_TITLE_ADDRESS_DETAILS")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
          <Row
              label={t("HOUSE_NO")}
              text={`${t(checkForNA(address?.houseNo))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/citizen/wt/request-service/address-details`} />}
              />
          <Row
              label={t("ADDRESS_LINE1")}
              text={`${t(checkForNA(address?.addressLine1))}`}
              />
              <Row
              label={t("ADDRESS_LINE2")}
              text={`${t(checkForNA(address?.addressLine2))}`}
              />
              <Row
              label={t("CITY")}
              text={`${t(checkForNA(address?.city?.city?.name))}`}
              />
              <Row
              label={t("LOCALITY")}
              text={`${t(checkForNA(address?.locality?.i18nKey))}`}
              />
              <Row
              label={t("PINCODE")}
              text={`${t(checkForNA(address?.pincode))}`}
              />
              <Row
              label={t("LANDMARK")}
              text={`${t(checkForNA(address?.landmark))}`}
              />
              <Row
              label={t("STREET_NAME")}
              text={`${t(checkForNA(address?.streetName))}`}
              />
          </StatusTable>


          <CardSubHeader>{t("ES_REQUEST_DETAILS")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
          <Row
              label={t("WT_TANKER_TYPE")}
              text={`${t(checkForNA(requestDetails?.tankerType?.value))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/citizen/wt/request-service/request-details`} />}
              />
              <Row
              label={t("WT_TANKER_QUANTITY")}
              text={`${t(checkForNA(requestDetails?.tankerQuantity))}`}
              />
              <Row
              label={t("WT_WATER_QUANTITY")}
              text={`${t(checkForNA(requestDetails?.waterQuantity))}`}
              />
              <Row
              label={t("WT_DELIVERY_DATE")}
              text={`${t(checkForNA(requestDetails?.deliveryDate))}`}
              />
              <Row
              label={t("WT_DESCRIPTION")}
              text={`${t(checkForNA(requestDetails?.description))}`}
              />
          </StatusTable>
    

          <CheckBox
            label={t("WT_FINAL_DECLARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto", marginBottom:"30px", marginTop:"10px" }}
          />
        </div>
        <SubmitBar label={t("COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
     </React.Fragment>
    );
  };
  
  export default WTCheckPage;