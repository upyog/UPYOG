import {Card,CardHeader,CardSubHeader,CardText,CheckBox,LinkButton,Row,StatusTable,SubmitBar, EditIcon} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState,useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA, getOrderDocuments } from "../../../utils";
import ApplicationTable from "../../../components/ApplicationTable";
import DocumentsPreview from "../../../../../templates/ApplicationDetails/components/DocumentsPreview";
import Timeline from "../../../components/Timeline";
  
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
    const history = useHistory();
    const {owner,businessDetails,address,bankdetails,documents} = value;

    const columnName = [
        { Header: t("SV_WEEK_DAYS"), accessor: "name" },
        { Header: t("SV_START_TIME"), accessor: "startTime" },
        { Header: t("SV_END_TIME"), accessor: "endTime" }
      ];

      const coloumnRows = businessDetails?.daysOfOperation?.filter((day_time) => day_time.isSelected).map((day_time, index) => (
        console.log("day_timeday_time",day_time),
    {
      name: day_time?.name,
      startTime: day_time?.startTime + " " + day_time?.startTimeAmPm,
      endTime: day_time?.endTime+ " " + day_time?.endTimeAmPm
    })) || [];

    let improvedDoc = [];
      documents?.documents?.map(appDoc => { improvedDoc.push({...appDoc, module: "StreetVending"}) });

    const { data: pdfDetails, isLoading:pdfLoading, error } = Digit.Hooks.useDocumentSearch( improvedDoc, { enabled: improvedDoc?.length > 0 ? true : false});

    let applicationDocs = []
    if (pdfDetails?.pdfFiles?.length > 0) {  
      pdfDetails?.pdfFiles?.map(pdfAppDoc => {
        if (pdfAppDoc?.module == "StreetVending") applicationDocs.push(pdfAppDoc);
      });
    }
  
    const [agree, setAgree] = useState(false);
    const setdeclarationhandler = () => {
      setAgree(!agree);
    };
    return (
      <React.Fragment>
       {window.location.href.includes("/citizen") ? <Timeline currentStep={6}/> : null}
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
          bankdetails?.accountHolderName&&
          (
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
          
          <StatusTable>
          <CardSubHeader>{t("SV_DOCUMENT_DETAILS_LABEL")}</CardSubHeader>
          {<DocumentsPreview documents={getOrderDocuments(applicationDocs)} svgStyles = {{}} isSendBackFlow = {false} isHrLine = {true} titleStyles ={{fontSize: "18px", lineHeight: "24px", "fontWeight": 700, marginBottom: "10px"}}/>}
          </StatusTable>
      

         
          <CheckBox
            label={t("SV_FINAL_DECLARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto", marginBottom:"30px" }}
          />
        </div>
        <SubmitBar label={t("SV_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
     </React.Fragment>
    );
  };
  
  export default SVCheckPage;