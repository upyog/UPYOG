import {
  Card,
  CardHeader,
  CardSectionHeader,
  CardSubHeader,
  CardText,
  CheckBox,
  LinkButton,
  Row,
  StatusTable,
  SubmitBar
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import {
  checkForNA,
  getFixedFilename, 
} from "../../../utils";
import Timeline from "../../../components/CHBTimeline";
import ApplicationTable from "../../../components/inbox/ApplicationTable";
import CHBDocument from "../../../pageComponents/CHBDocument";

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
    bankdetails,
    slots,
    slotlist,
    index,    
    isEditCHB,
    isUpdateCHB,
    ownerss,
    documents,
    address,
   
  } = value;

  const typeOfApplication = !isEditCHB && !isUpdateCHB ? `bookHall` : `editbookHall`;
  const columns = [
    { Header: `${t("CHB_HALL_NAME")}` + "/" + `${t("CHB_PARK")}`, accessor: "name" },
    { Header: `${t("CHB_ADDRESS")}`, accessor: "address" },
    { Header: `${t("CHB_HALL_CODE")}`, accessor: "hallCode" },
    { Header: `${t("CHB_BOOKING_DATE")}`, accessor: "bookingDate" }
  ];
  const slotlistRows = slotlist?.bookingSlotDetails?.map((slot) => (
    {
      name: slot.name,
      address:slot.address,
      hallCode:slot.hallCode,
      bookingDate:slot.bookingDate,
    }
  )) || [];

  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
  // const getBookingDateRange = (bookingSlotDetails) => {
  //   if (!bookingSlotDetails || bookingSlotDetails.length === 0) {
  //     return t("CS_NA");
  //   }
  //   const startDate = bookingSlotDetails[0]?.bookingDate;
  //   const endDate = bookingSlotDetails[bookingSlotDetails.length - 1]?.bookingDate;
  //   if (startDate === endDate) {
  //     return startDate; // Return only the start date
  //   } else {
  //     // Format date range as needed, for example: "startDate - endDate"
  //     return startDate && endDate ? `${startDate} - ${endDate}` : t("CS_NA");
  //   }
  // };
  // const getBookingTimeRange = (bookingSlotDetails) => {
  //   if (!bookingSlotDetails || bookingSlotDetails.length === 0) {
  //     return "10:00 - 11:59"; 
  //   }
  //   const startTime = "10:00"; 
    
  //   const length = bookingSlotDetails.length;
  
  //   let defaultEndTime = "11:59"; 
  //   if (length === 2) {
  //     defaultEndTime = "23:59"; 
  //   } else if (length === 3) {
  //     defaultEndTime = "71:59"; 
  //   }
  
  //   // Return formatted time range
  //   return `${startTime} - ${defaultEndTime}`;
  // };
  return (
    <React.Fragment>
     {window.location.href.includes("/citizen") ? <Timeline currentStep={6}/> : null}
    <Card>
      <CardHeader>{t("CHB_CHECK_YOUR_DETAILS")}</CardHeader>
      <div>
      <CardText>{t("CHB_CHECK_CHECK_YOUR_ANSWERS_TEXT")}</CardText>
        <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_APPLICANT_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_APPLICANT_NAME")}
            text={`${t(checkForNA(ownerss?.applicantName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />

        <Row
            label={t("CHB_MOBILE_NUMBER")}
            text={`${t(checkForNA(ownerss?.mobileNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />
        <Row
            label={t("CHB_ALT_MOBILE_NUMBER")}
            text={`${t(checkForNA(ownerss?.alternateNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />

        <Row
            label={t("CHB_EMAIL_ID")}
            text={`${t(checkForNA(ownerss?.emailId))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />
        </StatusTable>
        {/* <CardSubHeader style={{ fontSize: "24px" }}>{t("SLOT_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_COMMUNITY_HALL_NAME")}
            text={`${t(checkForNA(slotlist?.bookingSlotDetails[0]?.name))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}

        />
        <Row
            label={t("CHB_BOOKING_DATE")}
            text={`${t(checkForNA(getBookingDateRange(slotlist?.bookingSlotDetails)))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}

        />
       <Row
          label={t("CHB_BOOKING_TIME")} // Label for the row, presumably fetched from translations
          text={(checkForNA(getBookingTimeRange(slotlist?.bookingSlotDetails)))} // Text to display, likely the formatted time range
          actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />} // Action button component
        />
        </StatusTable> */}
        <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_EVENT_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_SPECIAL_CATEGORY")}
            text={`${t(checkForNA(slots?.specialCategory?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/event-details`} />}

        />

        <Row
            label={t("CHB_PURPOSE")}
            text={`${t(checkForNA(slots?.purpose?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/event-details`} />}

        />
        <Row
            label={t("CHB_PURPOSE_DESCRIPTION")}
            text={`${t(checkForNA(slots?.purposeDescription))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/event-details`} />}

        />

        </StatusTable>
        <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_BANK_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_ACCOUNT_NUMBER")}
            text={`${t(checkForNA(bankdetails?.accountNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}

        />
         <Row
            label={t("CHB_CONFIRM_ACCOUNT_NUMBER")}
            text={`${t(checkForNA(bankdetails?.confirmAccountNumber))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}

        />
         <Row
            label={t("CHB_IFSC_CODE")}
            text={`${t(checkForNA(bankdetails?.ifscCode))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}

        />
         
         <Row
            label={t("CHB_BANK_NAME")}
            text={`${t(checkForNA(bankdetails?.bankName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}
        />
         <Row
            label={t("CHB_BANK_BRANCH_NAME")}
            text={`${t(checkForNA(bankdetails?.bankBranchName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}
        />
         <Row
            label={t("CHB_ACCOUNT_HOLDER_NAME")}
            text={`${t(checkForNA(bankdetails?.accountHolderName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/bank-details`} />}
        />

        </StatusTable>
        <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_ADDRESS_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_PINCODE")}
            text={`${t(checkForNA(address?.pincode))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/address-details`} />}

        />
         <Row
            label={t("CHB_CITY")}
            text={`${t(checkForNA(address?.city?.city?.name))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/address-details`} />}

        />
         <Row
            label={t("CHB_LOCALITY")}
            text={`${t(checkForNA(address?.locality?.name))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/address-details`} />}

        />
         
         <Row
            label={t("CHB_STREET_NAME")}
            text={`${t(checkForNA(address?.streetName))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/address-details`} />}
        />
         <Row
            label={t("CHB_HOUSE_NO")}
            text={`${t(checkForNA(address?.houseNo))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/address-details`} />}
        />
         <Row
            label={t("CHB_LANDMARK")}
            text={`${t(checkForNA(address?.landmark))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/address-details`} />}
        />
        </StatusTable>
        <CardSubHeader style={{ fontSize: "24px" }}>{t("SLOT_DETAILS")}</CardSubHeader>
        <ApplicationTable
              t={t}
              data={slotlistRows}
              columns={columns}
              getCellProps={(cellInfo) => ({
                style: {
                  minWidth: "150px",
                  padding: "10px",
                  fontSize: "16px",
                  paddingLeft: "20px",
                },
              })}
              isPaginationRequired={false}
              totalRecords={slotlistRows.length}
            />
        <CardSubHeader style={{ fontSize: "24px" }}>{t("CHB_DOCUMENTS_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Card style={{display: "flex", flexDirection: "row" }}>
          {documents && documents?.documents.map((doc, index) => (
            <div key={`doc-${index}`} style={{ marginRight: "25px"}}>
              <div>
                <CardSectionHeader>{t("CHB_" + (doc?.documentType?.split('.').slice(0,2).join('_')))}</CardSectionHeader>
                <CHBDocument value={value} Code={doc?.documentType} index={index} />
              </div>
            </div>
          ))}
        </Card>
        </StatusTable>
       
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