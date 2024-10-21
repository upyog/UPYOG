/* Custom Component to to show all the form details filled by user. All the details are coming through the value, 
In Parent Component,  we are passing the data as a props coming through params (data in params comes through session storage) into the value.
*/
import {Card,CardHeader,CardSubHeader,CheckBox,LinkButton,Row,StatusTable,SubmitBar, EditIcon, RadioButtons,CardLabel} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA, getOrderDocuments } from "../../../utils";
import ApplicationTable from "../../../components/ApplicationTable";
import DocumentsPreview from "../../../../../templates/ApplicationDetails/components/DocumentsPreview";
import Timeline from "../../../components/Timeline";


  //function for edit button with edit icon and functioanality of redirecting to differnt URL's
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


  const SVCheckPage = ({ onSubmit, value = {} }) => {
    const { t } = useTranslation();
    const {owner,businessDetails,address,bankdetails,documents} = value;
    const [disabilityStatus, setdisabilityStatus]=useState(null);
    const [beneficiary, setbeneficiary] =useState(null)
    const common = [
      { code: "YES", value:"Yes", i18nKey:"YES" },
      { code: "NO", value:"NO", i18nKey:"No" },
    ];
    const [agree, setAgree] = useState(false);
    const setdeclarationhandler = () => {
      setAgree(!agree);
    };
    function setDisabilityStatus(value) {
      setdisabilityStatus(value);
    }
    function setBeneficiary(value) {
      setbeneficiary(value);
    }

    const columnName = [
        { Header: t("SV_WEEK_DAYS"), accessor: "name" },
        { Header: t("SV_START_TIME"), accessor: "startTime" },
        { Header: t("SV_END_TIME"), accessor: "endTime" }
      ];
      
      /*filtering the daysOfOperation array to show and then mapping those filetered data to to get only those whose "isSelected" is true,
      once get , i am formatting the data in accessor like name, Start time and End time, used ParseInt to convert the string in number so that 
      i can check whether the number is greater or less than 12
      */
      const coloumnRows = businessDetails?.daysOfOperation?.filter((day_time) => day_time.isSelected).map((day_time, index) => ({
        name: day_time?.name,
        startTime: parseInt(day_time?.startTime)>=12?day_time?.startTime+" "+"PM":day_time?.startTime+" "+"AM",
        endTime: parseInt(day_time?.endTime)>=12?day_time?.endTime+" "+"PM":day_time?.endTime+" "+"AM"
      })) || [];



     /* Initialize an empty array improvedDoc to hold modified documents.
        Iterate over existing documents, adding a "module" property with the value "StreetVending" to each document.
        Use the modified documents to fetch PDF details only if there are documents present.
        If the fetched PDF details contain files, filter them to include only those related to the "StreetVending" module and store them in applicationDocs array.*/ 
    let improvedDoc = [];
    documents?.documents?.map(appDoc => { improvedDoc.push({...appDoc, module: "StreetVending"}) });
    const { data: pdfDetails, isLoading:pdfLoading, error } = Digit.Hooks.useDocumentSearch( improvedDoc, { enabled: improvedDoc?.length > 0 ? true : false});

    let applicationDocs = []
    if (pdfDetails?.pdfFiles?.length > 0) {  
      pdfDetails?.pdfFiles?.map(pdfAppDoc => {
        if (pdfAppDoc?.module == "StreetVending") applicationDocs.push(pdfAppDoc);
      });
    }
  
    /**
     *  This React component renders the Street Vendor Application Summary Page. 
     * It includes a timeline indicating the current step in the application process, followed by sections for 
     * Vendor Personal Details,
     * Business Details, 
     * Bank Details, 
     * Address Details, 
     * and Document Preview. 
     * Each section displays relevant information using the Row component, and includes action buttons for navigation to edit details. 
     * The page also features radio buttons for selecting disability status and beneficiary schemes, along with a checkbox for the final declaration.
     */
    
    return (
      <React.Fragment>
       {<Timeline currentStep={6}/>}
      <Card>
        <CardHeader>{t("SV_SUMMARY_PAGE")}</CardHeader>
        <div>
          <CardSubHeader>{t("SV_VENDOR_PERSONAL_DETAILS")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
          <Row
              label={t("SV_VENDOR_NAME")}
              text={`${t(checkForNA(owner?.units?.[0]?.vendorName))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/citizen/sv/apply/applicant-details`} />}
          />
  
          <Row
              label={t("SV_REGISTERED_MOB_NUMBER")}
              text={`${t(checkForNA(owner?.units?.[0]?.mobileNumber))}`}
          />
  
          <Row
              label={t("SV_DATE_OF_BIRTH")}
              text={`${t(checkForNA(owner?.units?.[0]?.vendorDateOfBirth))}`}
          />
  
          <Row
              label={t("SV_GENDER")}
              text={`${t(checkForNA(owner?.units?.[0]?.gender?.code))}`}
          />
          <Row
              label={t("SV_FATHER_NAME")}
              text={`${t(checkForNA(owner?.units?.[0]?.fatherName))}`}
          />
          {
            owner?.units?.[0]?.email?
            <Row
              label={t("SV_EMAIL")}
              text={`${t(checkForNA(owner?.units?.[0]?.email))}`}
          />:null
          }
          {owner?.units?.[0]?.spouseName && (
            <Row
                label={t("SV_SPOUSE_NAME")}
                text={
                    <span>
                        {t(checkForNA(owner?.units?.[0]?.spouseName))}
                        {" "}
                        <span className="astericColor">
                            ({t(owner?.spouseDependentChecked ? "INVOLVED" : "NOT INVOLVED")})
                        </span>
                    </span>
                }
            />
        )}
          {
            owner?.units?.[0]?.spouseDateBirth?
            <Row
              label={t("SV_SPOUSE_DATE_OF_BIRTH")}
              text={`${t(checkForNA(owner?.units?.[0]?.spouseDateBirth))}`}
          />:null
          }
          {
            owner?.units?.[0]?.dependentName?
            <Row
              label={t("SV_DEPENDENT_NAME")}
              text={`${t(checkForNA(owner?.units?.[0]?.dependentName))}`}
          />:null
          }
          {owner?.units?.[0]?.dependentName && (
            <Row
                label={t("SV_DEPENDENT_NAME")}
                text={
                    <span>
                        {t(checkForNA(owner?.units?.[0]?.dependentName))}
                        {" "}
                        <span className="astericColor">
                            ({t(owner?.dependentNameChecked ? "INVOLVED" : "NOT INVOLVED")})
                        </span>
                    </span>
                }
            />
        )}
          {
            owner?.units?.[0]?.dependentDateBirth?
            <Row
              label={t("SV_DEPENDENT_DATE_OF_BIRTH")}
              text={`${t(checkForNA(owner?.units?.[0]?.dependentDateBirth))}`}
          />:null
          }
          {
            owner?.units?.[0]?.dependentGender?
            <Row
              label={t("SV_DEPENDENT_GENDER")}
              text={`${t(checkForNA(owner?.units?.[0]?.dependentGender))}`}
          />:null
          }
          {
            owner?.units?.[0]?.dependentGender?
            <Row
              label={t("SV_TRADE_NUMBER")}
              text={`${t(checkForNA(owner?.units?.[0]?.tradeNumber))}`}
          />:null
          }
          </StatusTable>
          <CardSubHeader>{t("SV_VENDOR_BUSINESS_DETAILS")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
          <Row
              label={t("SV_VENDING_TYPE")}
              text={`${t(checkForNA(businessDetails?.vendingType?.code))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/citizen/sv/apply/business-details`} />}
          />
          <Row
              label={t("SV_VENDING_ZONES")}
              text={`${t(checkForNA(businessDetails?.vendingZones?.value))}`}
          />
          <Row
              label={t("SV_AREA_REQUIRED")}
              text={`${t(checkForNA(businessDetails?.areaRequired))}`}
          />
          <Row
              label={t("SV_LOCAL_AUTHORITY_NAME")}
              text={`${t(checkForNA(businessDetails?.nameOfAuthority))}`}
          />
          {
            businessDetails?.vendingLiscence?
            <Row
              label={t("SV_VENDING_LISCENCE")}
              text={`${t(checkForNA(businessDetails?.vendingLiscence))}`}
          />:null
          }
          
          <span style={{marginTop:"5px", fontSize:"18px", fontWeight:"bold" }}>{t("SV_DAY_HOUR_OPERATION")}</span>
          <ApplicationTable
              t={t}
              data={coloumnRows}
              columns={columnName}
              getCellProps={(cellInfo) => ({
                style: {
                  minWidth: "150px",
                  padding: "10px",
                  fontSize: "16px",
                  paddingLeft: "20px",
                },
              })}
              isPaginationRequired={false}
              totalRecords={coloumnRows.length}
            />
          </StatusTable>
          {bankdetails?.accountNumber&&
          bankdetails?.ifscCode&&
          bankdetails?.bankName&&
          bankdetails?.bankBranchName&&
          bankdetails?.accountHolderName&&(
            <React.Fragment>
              <CardSubHeader>{t("SV_BANK_DETAILS")}</CardSubHeader>
              <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
              <Row
              label={t("SV_ACCOUNT_NUMBER")}
              text={`${t(checkForNA(bankdetails?.accountNumber))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/citizen/sv/apply/bank-details`} />}
              />
              <Row
              label={t("SV_IFSC_CODE")}
              text={`${t(checkForNA(bankdetails?.ifscCode))}`}
              />
              <Row
              label={t("SV_BANK_NAME")}
              text={`${t(checkForNA(bankdetails?.bankName))}`}
              />
              <Row
              label={t("SV_BANK_BRANCH_NAME")}
              text={`${t(checkForNA(bankdetails?.bankBranchName))}`}
              />
              <Row
              label={t("SV_ACCOUNT_HOLDER_NAME")}
              text={`${t(checkForNA(bankdetails?.accountHolderName))}`}
              />
              </StatusTable>
            </React.Fragment>
          )}
          <CardSubHeader>{t("SV_ADDRESS_DETAILS")}</CardSubHeader>
          <StatusTable style={{marginTop:"30px",marginBottom:"30px"}}>
          <Row
              label={t("SV_ADDRESS_LINE1")}
              text={`${t(checkForNA(address?.addressline1))}`}
              actionButton={<ActionButton jumpTo={`/digit-ui/citizen/sv/apply/address-details`} />}
              />
              <Row
              label={t("SV_ADDRESS_LINE2")}
              text={`${t(checkForNA(address?.addressline2))}`}
              />
              <Row
              label={t("SV_CITY")}
              text={`${t(checkForNA(address?.city?.city?.name))}`}
              />
              <Row
              label={t("SV_LOCALITY")}
              text={`${t(checkForNA(address?.locality?.i18nKey))}`}
              />
              {address?.pincode?
              <Row
              label={t("SV_ADDRESS_PINCODE")}
              text={`${t(checkForNA(address?.pincode))}`}
              />:null
              }
              {address?.landmark?
              <Row
              label={t("SV_LANDMARK")}
              text={`${t(checkForNA(address?.landmark))}`}
              />:null
              }
          </StatusTable>
          
          <CardSubHeader>{t("SV_DOCUMENT_DETAILS_LABEL")}</CardSubHeader>
          {<DocumentsPreview documents={getOrderDocuments(applicationDocs)} svgStyles = {{}} isSendBackFlow = {false} titleStyles ={{fontSize: "18px", "fontWeight": 700, marginBottom: "10px"}}/>}
          <br></br>

          <CardLabel>{t("SV_DISABILITY_STATUS")}</CardLabel>
          <RadioButtons
              selectedOption={disabilityStatus}
              onSelect={setDisabilityStatus}
              style={{ display: "flex", flexWrap: "wrap", gap:"40px"  }}
              options={common}
              optionsKey="i18nKey"
          />
          <CardLabel>{t("SV_BENEFICIARY_SCHEMES")}</CardLabel>
          <RadioButtons
              selectedOption={beneficiary}
              onSelect={setBeneficiary}
              style={{ display: "flex", flexWrap: "wrap", gap:"40px"  }}
              options={common}
              optionsKey="i18nKey"
          />
          <CheckBox
            label={t("SV_FINAL_DECLARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto", marginBottom:"30px", marginTop:"10px" }}
          />
        </div>
        <SubmitBar label={t("SV_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
     </React.Fragment>
    );
  };
  
  export default SVCheckPage;