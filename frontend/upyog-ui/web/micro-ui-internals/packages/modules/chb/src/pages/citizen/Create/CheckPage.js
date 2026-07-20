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

import {
  checkForNA,
  getFixedFilename, 
} from "../../../utils";
import Timeline from "../../../components/CHBTimeline";
import ApplicationTable from "../../../components/inbox/ApplicationTable";
import CHBDocument from "../../../pageComponents/CHBDocument";
import { TimerValues } from "../../../components/TimerValues";

const ActionButton = ({ jumpTo }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  function routeTo() {
    navigate(jumpTo);
  }

  return <LinkButton label={t("CS_COMMON_CHANGE")} className="check-page-link-button" onClick={routeTo} />;
};

/**
 * CheckPage Component
 * 
 * This component is responsible for rendering the final review page (Check Page) in the CHB module.
 * It allows users to review their entered details, such as bank details, slots, documents, and address, before submitting the application.
 * 
 * Subcomponents:
 * - `ActionButton`: A reusable button component that navigates the user to a specific page for editing details.
 *    - Props:
 *      - `jumpTo`: The route to navigate to when the button is clicked.
 *    - Functions:
 *      - `routeTo`: Navigates to the specified route using the `useHistory` hook.
 * 
 * Props:
 * - `onSubmit`: Callback function triggered when the user submits the application.
 * - `value`: Object containing the application details to be reviewed, including:
 *    - `bankdetails`: Bank details entered by the user.
 *    - `slots`: Slot details selected by the user.
 *    - `slotlist`: List of available slots.
 *    - `index`: Index of the current step in the application process.
 *    - `isEditCHB`: Boolean indicating whether the application is being edited.
 *    - `isUpdateCHB`: Boolean indicating whether the application is being updated.
 *    - `ownerss`: Owner details entered by the user.
 *    - `documents`: List of uploaded documents.
 *    - `address`: Address details entered by the user.
 * 
 * Hooks:
 * - `useTranslation`: Provides the `t` function for internationalization.
 * - `useHistory`: Provides navigation functionality within the application.
 * 
 * Variables:
 * - `typeOfApplication`: Determines the type of application based on whether it is a new booking, an edit, or an update.
 *    - `bookHall`: Indicates a new booking.
 *    - `editbookHall`: Indicates an edit or update to an existing booking.
 * 
 * Logic:
 * - Extracts and organizes the application details from the `value` prop for display on the review page.
 * - Provides an action button for users to navigate back and edit specific sections of the application.
 * 
 * Returns:
 * - A review page displaying the application details, with an action button for editing and a submit button for final submission.
 */
const CheckPage = ({ onSubmit, value = {} }) => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  
  const {
    bankdetails,
    slots,
    slotlist,
    index,    
    isEditCHB,
    isUpdateCHB,
    ownerss,
    documents,
    address
   
  } = value;

  const typeOfApplication = !isEditCHB && !isUpdateCHB ? `bookHall` : `editbookHall`;
  const columns = [
    { Header: `${t("CHB_HALL_NAME")}` + "/" + `${t("CHB_PARK")}`, accessor: "name" },
    { Header: `${t("CHB_ADDRESS")}`, accessor: "address" },
    { Header: `${t("CHB_HALL_CODE")}`, accessor: "hallCode" },
    { Header: `${t("CHB_BOOKING_DATE")}`, accessor: "bookingDate" },
    { Header: `${t("CHB_BOOKING_TIME")}`, accessor: "time" }
  ];
  const slotlistRows = slotlist?.bookingSlotDetails?.map((slot) => (
    {
      name: slot.name,
      address:slot.address,
      hallCode:slot.venueCode,
      bookingDate:slot.bookingDate,
      time:slotlist.searchData.fromTime+" - "+slotlist.searchData.toTime
    }
  )) || [];

  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
  };
 
  return (
    <React.Fragment>
     {window.location.href.includes("/citizen") ? <Timeline currentStep={6}/> : null}
    <Card>
      <div style={{ display: "flex", justifyContent: "space-between", width: "100%" }}>
        <CardHeader>{t("CHB_CHECK_YOUR_DETAILS")}</CardHeader>
        <CardSubHeader>
          <TimerValues timerValues={slotlist?.existingDataSet?.timervalue?.timervalue} SlotSearchData={slotlist?.searchData} draftId={slotlist?.existingDataSet?.draftId} />
        </CardSubHeader>
      </div>
      <div>
      <CardText>{t("CHB_CHECK_CHECK_YOUR_ANSWERS_TEXT")}</CardText>
        <CardSubHeader className="chb-subheader-lg">{t("CHB_APPLICANT_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_APPLICANT_NAME")}
            text={`${t(checkForNA(ownerss?.applicantName))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />

        <Row
            label={t("CHB_MOBILE_NUMBER")}
            text={`${t(checkForNA(ownerss?.mobileNumber))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />
        <Row
            label={t("CHB_ALT_MOBILE_NUMBER")}
            text={`${t(checkForNA(ownerss?.alternateNumber))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />

        <Row
            label={t("CHB_EMAIL_ID")}
            text={`${t(checkForNA(ownerss?.emailId))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}

        />
        </StatusTable>
        {/* <CardSubHeader style={{ fontSize: "24px" }}>{t("SLOT_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_COMMUNITY_HALL_NAME")}
            text={`${t(checkForNA(slotlist?.bookingSlotDetails[0]?.name))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/searchHall`} />}

        />
        <Row
            label={t("CHB_BOOKING_DATE")}
            text={`${t(checkForNA(getBookingDateRange(slotlist?.bookingSlotDetails)))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/searchHall`} />}

        />
       <Row
          label={t("CHB_BOOKING_TIME")} // Label for the row, presumably fetched from translations
          text={(checkForNA(getBookingTimeRange(slotlist?.bookingSlotDetails)))} // Text to display, likely the formatted time range
          actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/searchHall`} />} // Action button component
        />
        </StatusTable> */}
        <CardSubHeader className="chb-subheader-lg">{t("CHB_EVENT_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_SPECIAL_CATEGORY")}
            text={`${t(checkForNA(slots?.specialCategory?.value))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/event-details`} />}

        />

        <Row
            label={t("CHB_PURPOSE")}
            text={`${t(checkForNA(slots?.purpose?.value))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/event-details`} />}

        />
        <Row
            label={t("CHB_PURPOSE_DESCRIPTION")}
            text={`${t(checkForNA(slots?.purposeDescription))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/event-details`} />}

        />

        </StatusTable>
        <CardSubHeader className="chb-subheader-lg">{t("CHB_BANK_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_ACCOUNT_NUMBER")}
            text={`${t(checkForNA(bankdetails?.accountNumber))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/bank-details`} />}

        />
         <Row
            label={t("CHB_CONFIRM_ACCOUNT_NUMBER")}
            text={`${t(checkForNA(bankdetails?.confirmAccountNumber))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/bank-details`} />}

        />
         <Row
            label={t("CHB_IFSC_CODE")}
            text={`${t(checkForNA(bankdetails?.ifscCode))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/bank-details`} />}

        />
         
         <Row
            label={t("CHB_BANK_NAME")}
            text={`${t(checkForNA(bankdetails?.bankName))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/bank-details`} />}
        />
         <Row
            label={t("CHB_BANK_BRANCH_NAME")}
            text={`${t(checkForNA(bankdetails?.bankBranchName))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/bank-details`} />}
        />
         <Row
            label={t("CHB_ACCOUNT_HOLDER_NAME")}
            text={`${t(checkForNA(bankdetails?.accountHolderName))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/bank-details`} />}
        />

        </StatusTable>
        <CardSubHeader className="chb-subheader-lg">{t("CHB_ADDRESS_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Row
            label={t("CHB_PINCODE")}
            text={`${t(checkForNA(address?.pincode))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/address-details`} />}

        />
         <Row
            label={t("CHB_CITY")}
            text={`${t(checkForNA(address?.city?.city?.name))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/address-details`} />}

        />
         <Row
            label={t("CHB_LOCALITY")}
            text={`${t(checkForNA(address?.locality?.name))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/address-details`} />}

        />
         
         <Row
            label={t("CHB_STREET_NAME")}
            text={`${t(checkForNA(address?.streetName))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/address-details`} />}
        />
         <Row
            label={t("CHB_HOUSE_NO")}
            text={`${t(checkForNA(address?.houseNo))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/address-details`} />}
        />
         <Row
            label={t("CHB_LANDMARK")}
            text={`${t(checkForNA(address?.landmark))}`}
            actionButton={<ActionButton jumpTo={`/upyog-ui/citizen/chb/${typeOfApplication}/address-details`} />}
        />
        </StatusTable>
        <CardSubHeader className="chb-subheader-lg">{t("SLOT_DETAILS")}</CardSubHeader>
        <ApplicationTable
              t={t}
              data={slotlistRows}
              columns={columns}
              getCellProps={(cellInfo) => ({
                className: "chb-table-cell",
              })}
              isPaginationRequired={false}
              totalRecords={slotlistRows.length}
            />
        <CardSubHeader className="chb-subheader-lg">{t("CHB_DOCUMENTS_DETAILS")}</CardSubHeader>
        <StatusTable>
        <Card className="chb-doc-card">
          {documents && documents?.documents.map((doc, index) => (
            <div key={`doc-${index}`} className="chb-doc-item">
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
          className="chb-checkbox-auto-height"
          //disabled={!agree}
        />
      </div>
      <SubmitBar label={t("CHB_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
    </Card>
   </React.Fragment>
  );
};

export default CheckPage;