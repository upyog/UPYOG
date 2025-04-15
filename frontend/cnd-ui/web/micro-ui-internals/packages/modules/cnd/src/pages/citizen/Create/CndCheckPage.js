
import {Card,CardHeader,CardSubHeader,CheckBox,LinkButton,Row,StatusTable,SubmitBar, EditIcon} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA, CNDDocumnetPreview,getOrderDocuments } from "../../../utils";

/* Custom Component to to show all the form details filled by user. All the details are coming through the value, 
In Parent Component,  we are passing the data as a props coming through params (data in params comes through session storage) into the value.
*/

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


  const CndCheckPage = ({ onSubmit, value = {} }) => {
    const { t } = useTranslation();
    const {owner,address,constructionType,propertyNature, wasteType} = value;
    console.log("data",wasteType);
    const [agree, setAgree] = useState(false);

    const setdeclarationhandler = () => {
      setAgree(!agree);
    };

    
    
    return (
      <React.Fragment>
      <Card>
        <CardHeader>{t("CND_SUMMARY_PAGE")}</CardHeader>
        <div>
        <CardSubHeader>{t("CND_CONTRUCTION_NATURE_PROPERTY")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
          <Row
              label={t("CND_TYPE_CONSTRUCTION")}
              text={`${t(checkForNA(propertyNature?.constructionType?.code))}`}
              actionButton={<ActionButton jumpTo={`/cnd-ui/citizen/cnd/apply/property`} />}
          />
          <Row
              label={t("CND_AREA_HOUSE")}
              text={`${t(checkForNA(propertyNature?.houseArea))}`}
          />
           <Row
              label={t("CND_PROPERTY_USAGE")}
              text={`${t(checkForNA(propertyNature?.propertyUsage?.code))}`}
          />
          </StatusTable>
          <CardSubHeader>{t("CND_WASTE_TYPE")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
           {wasteType.wasteMaterialType.map((material, index) => (
            <Row
              key={`waste-material-${index}`}
              label={`${t("CND_WASTE_TYPE")} ${index + 1}`}
              text={
                // Display just the value or use the i18nKey for translation
                material.value ? 
                  t(checkForNA(material.value)) : 
                  material.i18nKey ? 
                    t(checkForNA(material.i18nKey)) : 
                    t(checkForNA(material.code))
              }
            />
          ))}
          <Row
              label={t("CND_SCHEDULE_PICKUP")}
              text={`${t(checkForNA(wasteType?.pickupDate))}`}
          />
          {(wasteType?.wasteQuantity) ? 
           <Row
              label={t("CND_WASTE_QUANTITY")}
              text={`${t(checkForNA(wasteType?.wasteQuantity))}`}
          />:null}
          </StatusTable>
          <CardSubHeader>{t("COMMON_PERSONAL_DETAILS")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
          <Row
              label={t("COMMON_APPLICANT_NAME")}
              text={`${t(checkForNA(owner?.applicantName))}`}
              actionButton={<ActionButton jumpTo={`/cnd-ui/citizen/cnd/apply/applicant-details`} />}
          />
          <Row
              label={t("COMMON_MOBILE_NUMBER")}
              text={`${t(checkForNA(owner?.mobileNumber))}`}
          />
           <Row
              label={t("COMMON_ALT_MOBILE_NUMBER")}
              text={`${t(checkForNA(owner?.alternateNumber))}`}
          />
          <Row
              label={t("COMMON_EMAIL_ID")}
              text={`${t(checkForNA(owner?.emailId))}`}
          />
          </StatusTable>
         
          <CardSubHeader>{t("ADDRESS_DEATILS")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
          <Row
              label={t("HOUSE_NO")}
              text={`${t(checkForNA(address?.houseNo))}`}
              actionButton={<ActionButton jumpTo={`/cnd-ui/citizen/cnd/apply/address-details`} />}
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
         
            
            
          <CheckBox
            label={t("CND_DECALARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto", marginBottom:"30px", marginTop:"10px" }}
          />
        </div>
        <SubmitBar label={t("COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
     </React.Fragment>
    );
  };
  
  export default CndCheckPage;