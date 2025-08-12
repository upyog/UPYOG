/* Custom Component to to show all the form details filled by user. All the details are coming through the value, 
In Parent Component,  we are passing the data as a props coming through params (data in params comes through session storage) into the value.
*/
import { Card, CardHeader, CardSubHeader, CheckBox, LinkButton, Row, StatusTable, SubmitBar, EditIcon } from "@upyog/digit-ui-react-components";
import React, { useState, useMemo, Fragment } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, Link } from "react-router-dom";
import { checkForNA, getOrderDocuments } from "../../../utils";
import ApplicationTable from "../../../components/inbox/ApplicationTable";
import { SVDocumnetPreview } from "../../../utils";
import Timeline from "../../../components/Timeline";
import { formatTime } from "../../../utils";
import { UPYOG_CONSTANTS } from "../../../utils";


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

const prepareDocuments = (editdata, documents, storedData) => {
  const improvedDoc = [];

  // Process editdata documents
  if (editdata?.documentDetails?.length) {
    editdata.documentDetails.forEach(data => {
      improvedDoc.push({ ...data, module: UPYOG_CONSTANTS.MODULE_NAME });
    });
  }

  // Process documents if editdata is empty
  if (_.isEmpty(editdata)) {
    // Add documents from documents?.documents
    documents?.documents?.forEach(appDoc => {
      const isDuplicate = improvedDoc.some(doc => doc.documentType === appDoc.documentType);
      if (!isDuplicate) {
        improvedDoc.push({ ...appDoc, module: UPYOG_CONSTANTS.MODULE_NAME });
      }
    });

    // Add documents from storedData
    const parsedStoredData = JSON.parse(sessionStorage.getItem(UPYOG_CONSTANTS.DOCUMENT)) || [];
    parsedStoredData.forEach(data => {
      const isDuplicate = improvedDoc.some(doc => doc.documentType === data.documentType);
      if (!isDuplicate) {
        improvedDoc.push({ ...data, module: UPYOG_CONSTANTS.MODULE_NAME });
      }
    });
  }

  return improvedDoc;
};


const SVCheckPage = ({ onSubmit, editdata, value = {}, renewalData }) => {
  const { t } = useTranslation();
  const { owner, businessDetails, address, bankdetails, documents, specialCategoryData } = value;
  const [agree, setAgree] = useState(false);
  const isRenew = window.location.href.includes("renew") ? true : false; // creating common variable so that i dont have to write this much long condition every where
  const isMakePayment = window.location.href.includes("makePayment") ? true : false;

  const setdeclarationhandler = () => {
    setAgree(!agree);
  };

  const columnName = useMemo(() => ([
    { Header: t("SV_WEEK_DAYS"), accessor: "name" },
    { Header: t("SV_START_TIME"), accessor: "startTime" },
    { Header: t("SV_END_TIME"), accessor: "endTime" }
  ]), []);

  const operationRows = useMemo(() => {
    if (isRenew) {
      return renewalData?.vendingOperationTimeDetails?.map(renew_data => ({
        name: renew_data?.dayOfWeek,
        startTime: formatTime(renew_data?.fromTime),
        endTime: formatTime(renew_data?.toTime)
      })) || [];
    }

    return businessDetails?.daysOfOperation
      ?.filter(day_time => day_time.isSelected)
      ?.map(day_time => ({
        name: day_time?.name,
        startTime: formatTime(day_time?.startTime),
        endTime: formatTime(day_time?.endTime)
      })) || [];
  }, [isRenew, renewalData, businessDetails]);

  // Process documents
  const improvedDoc = useMemo(() =>
    prepareDocuments(editdata, documents, JSON.parse(sessionStorage.getItem(UPYOG_CONSTANTS.DOCUMENT))),
    [editdata, documents]
  );

  const { data: pdfDetails, isLoading: pdfLoading } = Digit.Hooks.useDocumentSearch(
    improvedDoc,
    { enabled: improvedDoc?.length > 0 }
  );

  const applicationDocs = useMemo(() =>
    pdfDetails?.pdfFiles?.filter(doc => doc?.module === UPYOG_CONSTANTS.MODULE_NAME) || [],
    [pdfDetails]
  );

  // Determine the gender value for display based on renewalData.
  // If gender is "M", set as "Male"; if "F", set as "Female"; otherwise, set as "Transgender".
  let gender;
  if (renewalData?.vendorDetail[0]?.gender) {
    gender = renewalData?.vendorDetail[0]?.gender === "M" ? "Male" : renewalData?.vendorDetail[0]?.gender === "F" ? "Female" : "Transgender";
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
      {!isRenew ? <Timeline currentStep={7} /> : null}
      <Card>
        <CardHeader>{(isRenew) ? t("SV_RENEWAL_DETAILS") : t("SV_SUMMARY_PAGE")}</CardHeader>
        <div>
          <CardSubHeader>{t("SV_VENDOR_PERSONAL_DETAILS")}</CardSubHeader>
          <StatusTable style={{ marginTop: "30px", marginBottom: "30px" }}>
            <Row
              label={t("SV_VENDOR_NAME")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendorDetail?.[0]?.name : owner?.units?.[0]?.vendorName))}`}
              actionButton={(isRenew) ? null : <ActionButton jumpTo={`/digit-ui/citizen/sv/apply/applicant-details`} />}
            />
            <Row
              label={t("SV_REGISTERED_MOB_NUMBER")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendorDetail?.[0]?.mobileNo : owner?.units?.[0]?.mobileNumber))}`}
            />

            <Row
              label={t("SV_DATE_OF_BIRTH")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendorDetail?.[0]?.dob : owner?.units?.[0]?.vendorDateOfBirth))}`}
            />

            <Row
              label={t("SV_GENDER")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? gender : owner?.units?.[0]?.gender?.code))}`}
            />
            <Row
              label={t("SV_FATHER_NAME")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendorDetail?.[0]?.fatherName : owner?.units?.[0]?.fatherName))}`}
            />
            {
              (renewalData?.vendorDetail?.[0]?.emailId || owner?.units?.[0]?.email) ?
                <Row
                  label={t("SV_EMAIL")}
                  text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendorDetail?.[0]?.emailId : owner?.units?.[0]?.email))}`}
                /> : null
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
              owner?.units?.[0]?.spouseDateBirth ?
                <Row
                  label={t("SV_SPOUSE_DATE_OF_BIRTH")}
                  text={`${t(checkForNA(owner?.units?.[0]?.spouseDateBirth))}`}
                /> : null
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
              owner?.units?.[0]?.dependentDateBirth ?
                <Row
                  label={t("SV_DEPENDENT_DATE_OF_BIRTH")}
                  text={`${t(checkForNA(owner?.units?.[0]?.dependentDateBirth))}`}
                /> : null
            }
            {
              owner?.units?.[0]?.dependentGender ?
                <Row
                  label={t("SV_DEPENDENT_GENDER")}
                  text={`${t(checkForNA(owner?.units?.[0]?.dependentGender?.code))}`}
                /> : null
            }
            {
              owner?.units?.[0]?.dependentGender ?
                <Row
                  label={t("SV_TRADE_NUMBER")}
                  text={`${t(checkForNA(owner?.units?.[0]?.tradeNumber))}`}
                /> : null
            }
          </StatusTable>
          <CardSubHeader>{t("SV_VENDOR_BUSINESS_DETAILS")}</CardSubHeader>
          <StatusTable style={{ marginTop: "30px", marginBottom: "30px" }}>
            <Row
              label={t("SV_VENDING_TYPE")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendingActivity : businessDetails?.vendingType?.code))}`}
              actionButton={(isRenew) ? null : <ActionButton jumpTo={`/digit-ui/citizen/sv/apply/business-details`} />}
            />
            <Row
              label={t("SV_VENDING_LOCALITY")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.localityValue : businessDetails?.vendorLocality?.name))}`}
            />
            <Row
              label={t("SV_VENDING_ZONES")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendingZoneValue : businessDetails?.vendingZones?.i18nKey))}`}
            />
            <Row
              label={t("SV_VENDING_PAYMENT")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendorPaymentFrequency : businessDetails?.vendingPayment?.value))}`}
            />
            <Row
              label={t("SV_AREA_REQUIRED")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendingArea : businessDetails?.areaRequired))}`}
            />
            <Row
              label={t("SV_LOCAL_AUTHORITY_NAME")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.localAuthorityName : businessDetails?.nameOfAuthority))}`}
            />
            {
              (renewalData?.vendingLicenseId || businessDetails?.vendingLiscence) ?
                <Row
                  label={t("SV_VENDING_LISCENCE")}
                  text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.vendingLicenseId : businessDetails?.vendingLiscence))}`}
                /> : null
            }

            <span style={{ marginTop: "5px", fontSize: "18px", fontWeight: "bold" }}>{t("SV_DAY_HOUR_OPERATION")}</span>
            <ApplicationTable
              t={t}
              data={operationRows}
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
              totalRecords={operationRows.length}
            />
          </StatusTable>
          {bankdetails?.accountNumber &&
            bankdetails?.ifscCode &&
            bankdetails?.bankName &&
            bankdetails?.bankBranchName &&
            bankdetails?.accountHolderName && (
              <React.Fragment>
                <CardSubHeader>{t("SV_BANK_DETAILS")}</CardSubHeader>
                <StatusTable style={{ marginTop: "30px", marginBottom: "30px" }}>
                  <Row
                    label={t("SV_ACCOUNT_NUMBER")}
                    text={`${t(checkForNA(bankdetails?.accountNumber))}`}
                    actionButton={(isRenew) ? null : <ActionButton jumpTo={`/digit-ui/citizen/sv/apply/bank-details`} />}
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
          <StatusTable style={{ marginTop: "30px", marginBottom: "30px" }}>
            <Row
              label={t("SV_ADDRESS_LINE1")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.addressDetails?.[0]?.addressLine1 : address?.addressline1))}`}
              actionButton={(isRenew) ? null : <ActionButton jumpTo={`/digit-ui/citizen/sv/apply/address-details`} />}
            />
            <Row
              label={t("SV_ADDRESS_LINE2")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.addressDetails?.[0]?.addressLine2 : address?.addressline2))}`}
            />
            <Row
              label={t("SV_CITY")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.addressDetails?.[0]?.city : address?.city?.city?.name))}`}
            />
            <Row
              label={t("SV_LOCALITY")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.addressDetails?.[0]?.locality : address?.locality?.i18nKey))}`}
            />
            <Row
              label={t("SV_ADDRESS_PINCODE")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.addressDetails?.[0]?.pincode : address?.pincode))}`}
            />
            {(renewalData?.addressDetails?.[0]?.landmark || address?.landmark) ?
              <Row
                label={t("SV_LANDMARK")}
                text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.addressDetails?.[0]?.landmark : address?.landmark))}`}
              /> : null
            }
          </StatusTable>

          <CardSubHeader>{t("SV_ADDITIONAL_DETAILS")}</CardSubHeader>
          <StatusTable style={{ marginBottom: "30px" }}>
            <Row
              label={t("SV_CATEGORY")}
              text={`${t(checkForNA((isRenew && isMakePayment) ? renewalData?.disabilityStatus : specialCategoryData?.ownerCategory?.value))}`}
              actionButton={(isRenew) ? null : <ActionButton jumpTo={`/digit-ui/citizen/sv/apply/special-category`} />}
            />
            {/* Handles rendering of beneficiaryList based on renew data or the data from normal flow of code */}
            {specialCategoryData?.beneficiaryList[0]?.schemeName ?
              specialCategoryData?.beneficiaryList.map((item) => (
                <>
                  <Row
                    label={t("SV_BENEFICIARY_SCHEMES")}
                    text={`${t(checkForNA(item?.schemeName))}`}
                  />

                  < Row
                    label={t("SV_ENROLLMENT_APPLICATION_NUMBER")}
                    text={`${t(checkForNA(item?.enrollmentId))}`}
                  />
                </>
              ))
              : renewalData?.benificiaryOfSocialSchemes?.map((item) => (
                <>
                  <Row
                    label={t("SV_BENEFICIARY_SCHEMES")}
                    text={`${t(checkForNA(item?.schemeName))}`}
                  />

                  < Row
                    label={t("SV_ENROLLMENT_APPLICATION_NUMBER")}
                    text={`${t(checkForNA(item?.enrollmentId))}`}
                  />
                </>
              )
              )
            }
          </StatusTable>

          <CardSubHeader>{t("SV_DOCUMENT_DETAILS_LABEL")}</CardSubHeader>
          {<SVDocumnetPreview documents={getOrderDocuments(applicationDocs)} svgStyles={{}} isSendBackFlow={false} titleStyles={{ fontSize: "18px", "fontWeight": 700, marginBottom: "10px" }} />}
          <br></br>

          <CheckBox
            label={t("SV_FINAL_DECLARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto", marginBottom: "30px", marginTop: "10px" }}
          />
        </div>

        {/* If Make Payment application, renders Make payment button otherwise renders the submit button */}
        {
          !isMakePayment ?
            <SubmitBar label={t("SV_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
            :
            <Link to={{ pathname: `/digit-ui/citizen/payment/my-bills/sv-services/${renewalData?.applicationNo}`, state: { tenantId: renewalData?.tenantId, applicationNumber: renewalData?.applicationNo } }}>
              <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} disabled={!agree} />
            </Link>
        }
      </Card>
    </React.Fragment>
  );
};

export default React.memo(SVCheckPage);
