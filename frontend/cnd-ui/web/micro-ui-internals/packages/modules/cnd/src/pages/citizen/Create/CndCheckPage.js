
import {Card,CardHeader,CardSubHeader,CheckBox,LinkButton,Row,StatusTable,SubmitBar, EditIcon} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA, CNDDocumnetPreview, getOrderDocuments } from "../../../utils";
import ApplicationTable from "../../../components/inbox/ApplicationTable";
import { cndStyles } from "../../../utils/cndStyles";

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
    label={<EditIcon style={cndStyles.editIcon} />}
    className="check-page-link-button" onClick={routeTo} />;
  };


  const CndCheckPage = ({ onSubmit, value = {} }) => {
    const { t } = useTranslation();
    const {owner,address,propertyNature, wasteType, addressDetails} = value;
    const [agree, setAgree] = useState(false);

    const setdeclarationhandler = () => {
      setAgree(!agree);
    };

    const columnName = [
      { Header: t("CND_S_NO"), accessor: "sNo" },
      { Header: t("CND_WASTE_TYPE"), accessor: "wasteType" },
      { Header: t("CND_QUANTITY"), accessor: "quantity" }
    ];

    const operationRows = wasteType?.wasteMaterialType.map((items, index) => ({
      sNo: index + 1,
      wasteType: items?.value || "-",
      quantity: "0",
    }));

    const wasteTypeDocs = [];

    if (wasteType?.siteMediaPhoto) {
      wasteTypeDocs.push({
        documentType: "CND_SITE_MEDIA",
        fileStoreId: wasteType.siteMediaPhoto,
        module: "CND",
      });
    }

    if (wasteType?.siteStack) {
      wasteTypeDocs.push({
        documentType: "CND_SITE_STACK",
        fileStoreId: wasteType.siteStack,
        module: "CND",
      });
    }

    // Step 3: Combine all documents
    const allDocs = [...wasteTypeDocs];

    // Step 4: Fetch PDF details only if documents exist
    const { data: pdfDetails } = Digit.Hooks.useDocumentSearch(allDocs, {
      enabled: allDocs.length > 0
    });

    // Step 5: Extract only CND PDFs
    const applicationDocs = pdfDetails?.pdfFiles?.filter((pdf) => pdf?.module === "CND") || [];

    return (
      <React.Fragment>
      <Card>
        <CardHeader>{t("CND_SUMMARY_PAGE")}</CardHeader>
        <div>
        <CardSubHeader>{t("CND_CONTRUCTION_NATURE_PROPERTY")}</CardSubHeader>
          <StatusTable style={cndStyles.rowStyle}>
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
          <ApplicationTable
              t={t}
              data={operationRows}
              columns={columnName}
              getCellProps={(cellInfo) => ({
                style: cndStyles.citizenApplicationTable,
              })}
              isPaginationRequired={false}
              totalRecords={operationRows.length}
            />
          <StatusTable style={cndStyles.rowStyle}>
          <Row
              label={t("CND_SCHEDULE_PICKUP")}
              text={`${t(checkForNA(wasteType?.pickupDate))}`}
          />
          {(wasteType?.wasteQuantity) ? 
           <Row
              label={t("CND_WASTE_QUANTITY")}
              text={`${t(checkForNA(wasteType?.wasteQuantity + " Tons"))}`}
          />:null}
          </StatusTable>
          <CardSubHeader>{t("COMMON_PERSONAL_DETAILS")}</CardSubHeader>
          <StatusTable style={cndStyles.rowStyle}>
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
          <StatusTable style={cndStyles.rowStyle}>
          <Row
              label={t("HOUSE_NO")}
              text={`${t(checkForNA(address?.houseNo|| addressDetails?.selectedAddressStatement?.houseNumber))}`}
              // actionButton={<ActionButton jumpTo={`/cnd-ui/citizen/cnd/apply/address-details`} />}
              />
          <Row
              label={t("ADDRESS_LINE1")}
              text={`${t(checkForNA(address?.addressLine1 || addressDetails?.selectedAddressStatement?.address ))}`}
              />
              <Row
              label={t("ADDRESS_LINE2")}
              text={`${t(checkForNA(address?.addressLine2|| addressDetails?.selectedAddressStatement?.address2))}`}
              />
              <Row
              label={t("CITY")}
              text={`${t(checkForNA(address?.city?.city?.name|| addressDetails?.selectedAddressStatement?.city ))}`}
              />
              <Row
              label={t("LOCALITY")}
              text={`${t(checkForNA(address?.locality?.i18nKey|| addressDetails?.selectedAddressStatement?.locality ))}`}
              />
              <Row
              label={t("PINCODE")}
              text={`${t(checkForNA(address?.pincode|| addressDetails?.selectedAddressStatement?.pinCode ))}`}
              />
              <Row
              label={t("LANDMARK")}
              text={`${t(checkForNA(address?.landmark|| addressDetails?.selectedAddressStatement?.landmark))}`}
              />
              <Row
              label={t("STREET_NAME")}
              text={`${t(checkForNA(address?.streetName|| addressDetails?.selectedAddressStatement?.streetName))}`}
              />
          </StatusTable>

          {applicationDocs.length > 0 && (
            <div>
              <CardSubHeader>{t("CND_DOC_DETAILS")}</CardSubHeader>
              <CNDDocumnetPreview 
                documents={getOrderDocuments(applicationDocs)} 
                svgStyles={{}} 
                isSendBackFlow={false} 
                titleStyles={{ fontSize: "18px", fontWeight: 700, marginBottom: "10px" }} 
              />
            </div>
          )}

           
         
            
            
          <CheckBox
            label={t("CND_DECALARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={cndStyles.checkBox}
          />
        </div>
        <SubmitBar label={t("COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
     </React.Fragment>
    );
  };
  
  export default CndCheckPage;