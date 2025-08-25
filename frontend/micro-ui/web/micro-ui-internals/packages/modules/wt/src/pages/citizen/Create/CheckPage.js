import {
  Card,
  CardHeader,
  CardSubHeader,
  CheckBox,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar,
  EditIcon,
  GenericFileIcon,
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, Fragment } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { checkForNA } from "../../../utils";
import { convertTo12HourFormat, formatDate, APPLICATION_PATH } from "../../../utils";

/* Custom Component to to show all the form details filled by user. All the details are coming through the value, 
In Parent Component,  we are passing the data as a props coming through params (data in params comes through session storage) into the value.
*/

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const history = useHistory();
  function routeTo() {
    history.push(jumpTo);
  }
  return (
    <LinkButton
      label={<EditIcon style={{ marginTop: "-30px", float: "right", position: "relative", bottom: "32px" }} />}
      className="check-page-link-button"
      onClick={routeTo}
    />
  );
};
// this is file open service
  const openFilePDF = (fileId) => {
    Digit.UploadServices.Filefetch([fileId], Digit.ULBService.getStateId()).then((res) => {
  
      // Extract the concatenated URL string
      const concatenatedUrls = res?.data?.fileStoreIds?.[0]?.url;
  
      if (concatenatedUrls) {
        // Split the string by commas to get individual URLs
        const urlArray = concatenatedUrls.split(',');
  
        // Pick the first URL (or any other logic to decide which URL to open)
        const fileUrl = urlArray[0];
  
        if (fileUrl) {
          window.open(fileUrl, '_blank'); // Open the file in a new tab
        } else {
          console.error('No valid URL found to open!');
        }
      } else {
        console.error('URL is missing in the response!');
      }
    }).catch((error) => {
      console.error('Error fetching file:', error);
    });
  };

const WTCheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const { owner, requestDetails, address, serviceType, toiletRequestDetails, treePruningRequestDetails } = value;
  const [agree, setAgree] = useState(false);
  const immediateRequired = requestDetails?.extraCharge ? "YES" : "NO";

  const setdeclarationhandler = () => {
    setAgree(!agree);
  };


  return (
    <React.Fragment>
      <Card>
        <CardHeader>{t("WT_SUMMARY_PAGE")}</CardHeader>
        <div>
          <CardSubHeader>{t("ES_TITILE_OWNER_DETAILS")}</CardSubHeader>
          <StatusTable style={{ marginTop: "30px", marginBottom: "30px" }}>
            <Row
              label={t("COMMON_APPLICANT_NAME")}
              text={`${t(checkForNA(owner?.applicantName))}`}
              actionButton={<ActionButton jumpTo={`${APPLICATION_PATH}/citizen/wt/request-service/applicant-details`} />}
            />
            <Row label={t("COMMON_MOBILE_NUMBER")} text={`${t(checkForNA(owner?.mobileNumber))}`} />
            <Row label={t("COMMON_ALT_MOBILE_NUMBER")} text={`${t(checkForNA(owner?.alternateNumber))}`} />
            <Row label={t("COMMON_EMAIL_ID")} text={`${t(checkForNA(owner?.emailId))}`} />
          </StatusTable>

          <CardSubHeader>{t("ES_TITLE_ADDRESS_DETAILS")}</CardSubHeader>
          <StatusTable style={{ marginTop: "30px", marginBottom: "30px" }}>
            <Row
              label={t("HOUSE_NO")}
              text={`${t(checkForNA(address?.houseNo))}`}
              actionButton={<ActionButton jumpTo={`${APPLICATION_PATH}/citizen/wt/request-service/address-details`} />}
            />
            <Row label={t("ADDRESS_LINE1")} text={`${t(checkForNA(address?.addressLine1))}`} />
            <Row label={t("ADDRESS_LINE2")} text={`${t(checkForNA(address?.addressLine2))}`} />
            <Row label={t("CITY")} text={`${t(checkForNA(address?.city?.city?.name))}`} />
            <Row label={t("LOCALITY")} text={`${t(checkForNA(address?.locality?.i18nKey))}`} />
            <Row label={t("PINCODE")} text={`${t(checkForNA(address?.pincode))}`} />
            <Row label={t("LANDMARK")} text={`${t(checkForNA(address?.landmark))}`} />
            <Row label={t("STREET_NAME")} text={`${t(checkForNA(address?.streetName))}`} />
          </StatusTable>
          {serviceType?.serviceType?.code === "WT" && (
            <>
              <CardSubHeader>{t("WT_REQUEST_DETAILS")}</CardSubHeader>
              <StatusTable style={{ marginTop: "30px", marginBottom: "30px" }}>
                <Row
                  label={t("WT_TANKER_TYPE")}
                  text={`${t(checkForNA(requestDetails?.tankerType?.value))}`}
                  actionButton={<ActionButton jumpTo={`${APPLICATION_PATH}/citizen/wt/request-service/request-details`} />}
                />
                <Row label={t("WT_WATER_TYPE")} text={`${t(checkForNA(requestDetails?.waterType?.code))}`} />
                <Row label={t("WT_TANKER_QUANTITY")} text={`${t(checkForNA(requestDetails?.tankerQuantity?.code))}`} />
                <Row label={t("WT_WATER_QUANTITY")} text={`${t(checkForNA(requestDetails?.waterQuantity?.value))}`} />
                <Row label={t("WT_DELIVERY_DATE")} text={`${t(checkForNA(formatDate(requestDetails?.deliveryDate)))}`} />
                <Row label={t("WT_DELIVERY_TIME")} text={`${checkForNA(convertTo12HourFormat(requestDetails?.deliveryTime))}`} />
                <Row label={t("WT_DESCRIPTION")} text={`${t(checkForNA(requestDetails?.description))}`} />
                <Row label={t("WT_IMMEDIATE")} text={`${t(checkForNA(immediateRequired))}`} />
              </StatusTable>
            </>
          )}
          {serviceType?.serviceType?.code === "MobileToilet" && (
            <>
              <CardSubHeader>{t("ES_REQUEST_DETAILS")}</CardSubHeader>
              <StatusTable style={{ marginTop: "30px", marginBottom: "30px" }}>
                <Row
                  label={t("MT_NUMBER_OF_MOBILE_TOILETS")}
                  text={`${t(checkForNA(toiletRequestDetails?.mobileToilet?.code))}`}
                  actionButton={<ActionButton jumpTo={`${APPLICATION_PATH}/citizen/wt/request-service/toiletRequest-details`} />}
                />
                <Row label={t("MT_DELIVERY_FROM_DATE")} text={`${t(checkForNA(formatDate(toiletRequestDetails?.deliveryfromDate)))}`} />
                <Row label={t("MT_DELIVERY_TO_DATE")} text={`${t(formatDate(checkForNA(toiletRequestDetails?.deliverytoDate)))}`} />
                <Row label={t("MT_REQUIREMNENT_FROM_TIME")} text={checkForNA(convertTo12HourFormat(toiletRequestDetails?.deliveryfromTime))} />
                <Row label={t("MT_REQUIREMNENT_TO_TIME")} text={checkForNA(convertTo12HourFormat(toiletRequestDetails?.deliverytoTime))} />
                <Row label={t("MT_SPECIAL_REQUEST")} text={`${t(checkForNA(toiletRequestDetails?.specialRequest))}`} />
              </StatusTable>
            </>
          )}

          {serviceType?.serviceType?.code === "TREE_PRUNING" && (
            <>
              <CardSubHeader>{t("TP_REQUEST_DETAILS")}</CardSubHeader>
              <StatusTable style={{ marginTop: "30px", marginBottom: "30px" }}>
                <Row
                  label={t("REASON_FOR_PRUNING")}
                  text={`${t(checkForNA(treePruningRequestDetails?.reasonOfPruning?.code))}`}
                  actionButton={<ActionButton jumpTo={`${APPLICATION_PATH}/citizen/wt/request-service/treePruningRequest-details`} />}
                />

                <Row label={t("LOCATION_GEOTAG")} text={`${t(checkForNA(treePruningRequestDetails?.geoTagLocation))}`} />

                <Row
                  label={t("UPLOAD_THE_SITE_PHOTOGRAPH")}
                  text={
                    <span style={{ display: "flex", alignItems: "center", gap: "8px",cursor: "pointer" }} onClick={() => openFilePDF(treePruningRequestDetails?.supportingDocumentFile)}>
                      <GenericFileIcon />
                      {/* {t(treePruningRequestDetails.supportingDocumentFile)} */}
                    </span>
                  }
                />
              </StatusTable>
              
            </>
          )}

          <CheckBox
            label={t("FINAL_DECLARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto", marginBottom: "30px", marginTop: "10px" }}
          />
        </div>
        <SubmitBar label={t("COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
    </React.Fragment>
  );
};

export default WTCheckPage;
