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
} from "@upyog/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import ApplicationTable from "../../../components/inbox/ApplicationTable";
import {
  checkForNA,
  getFixedFilename, 
} from "../../../utils";
import Timeline from "../../../components/CHBTimeline";
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
  
  // const columns = [
  //   { Header: "Hall Name", accessor: "hallName" },
  //   { Header: "Hall Code", accessor: "hallCode" },
  //   { Header: "Start Date", accessor: "date1" },
  //   { Header: "End Date", accessor: "date2" },
  //   { Header: "Status", accessor: "status" },
  // ];
  

  

  const {
    bankdetails,
    slots,
    slotlist,
    index,    
    isEditCHB,
    isUpdateCHB,
    ownerss,
    documents
   
  } = value;

//   const productRows = slotlist?.bookingSlotDetails.map((name) => (
//     {
//       hallName:  name.hallName,
//       hallCode: name.hallCode,
//       date1: name.date1,
//       date2: name.date2,
//       status: name.status,
//     }
// )) || [];

  const typeOfApplication = !isEditCHB && !isUpdateCHB ? `bookHall` : `edit-application`;


  const [agree, setAgree] = useState(false);
  const setdeclarationhandler = () => {
    setAgree(!agree);
    console.log(slotlist);
  };
  return (
    <React.Fragment>
     {window.location.href.includes("/citizen") ? <Timeline currentStep={5}/> : null}
    <Card>
      <CardHeader>{t("CHB_CHECK_YOUR_DETAILS")}</CardHeader>
      <div>
        <br></br>
        <CardSubHeader>{slotlist?.bookingSlotDetails.map((slot,index) =>(
    <div key={index}>
      {slot.name}
      {/* ({slot.date1}) */}
    </div>
  ))}</CardSubHeader>
        {/* <ApplicationTable
            t={t}
            data={productRows}
            columns={columns}
            getCellProps={() => ({
              style: {
                minWidth: "150px",
                padding: "10px",
                fontSize: "16px",
                paddingLeft: "20px",
              },
            })}
            isPaginationRequired={true}
            totalRecords={productRows.length}
          /> */}
        <br></br>
        <CardSubHeader>{t("CHB_BOOKING_DETAILS")}</CardSubHeader>
        <br></br>
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
        <br></br>

        <CardSubHeader>{t("SLOT_DETAILS")}</CardSubHeader>
        <br></br>
        <StatusTable>
        <Row
            label={t("CHB_SELECT_SLOT")}
            text={`${t(checkForNA(slots?.selectslot?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/slot-details`} />}

        />

        <Row
            label={t("CHB_RESIDENT_TYPE")}
            text={`${t(checkForNA(slots?.residentType?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/slot-details`} />}
        />

        <Row
            label={t("CHB_SPECIAL_CATEGORY")}
            text={`${t(checkForNA(slots?.specialCategory?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/slot-details`} />}

        />

        <Row
            label={t("CHB_PURPOSE")}
            text={`${t(checkForNA(slots?.purpose?.value))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/slot-details`} />}

        />
        <Row
            label={t("CHB_PURPOSE_DESCRIPTION")}
            text={`${t(checkForNA(slots?.purposeDescription))}`}
            actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/slot-details`} />}

        />

        </StatusTable>
        <br></br>
        <CardSubHeader>{t("CHB_BANK_DETAILS")}</CardSubHeader>
        <br></br>
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
        <StatusTable>
        <br></br>
        <CardSubHeader>{t("CHB_DOCUMENTS_DETAILS")}</CardSubHeader>
        <Card style={{paddingRight:"16px"}}>
        {documents && documents?.documents.map((doc, index) => (
          <div key={`doc-${index}`}>
         {<div><CardSectionHeader>{t("CHB_" +(doc?.documentType?.split('.').slice(0,2).join('_')))}</CardSectionHeader>
          <StatusTable>
          {
           <CHBDocument value={value} Code={doc?.documentType} index={index} /> }
          {documents?.documents.length != index+ 1 ? <hr style={{color:"white",backgroundColor:"white",height:"2px",marginTop:"20px",marginBottom:"20px"}}/> : null}
          </StatusTable>
          </div>}
          </div>
        ))}
      </Card>
        <br></br>
        </StatusTable>
        <br></br>
       
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