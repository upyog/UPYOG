import {
    Card,
    CardHeader,
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
    
    const { application } = value;
  
    console.log("application darta ::", application)
   
  
    const [agree, setAgree] = useState(false);
    const setdeclarationhandler = () => {
      setAgree(!agree);
    };
    
    return (
      <React.Fragment>
       {/* {window.location.href.includes("/citizen") ? <Timeline currentStep={6}/> : null} */}
      <Card>
        <CardHeader>{t("FN_CHECK_YOUR_DETAILS")}</CardHeader>
        <div>
        <CardText>{t("FN_CHECK_CHECK_YOUR_ANSWERS_TEXT")}</CardText>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("FN_NOC_DETAILS")}</CardSubHeader>
          <StatusTable>
          { <Row
              label={t("NOC_TYPE")}
            //   text={`${t(("applicantName"))}`}
            //   text={application.applicantName? application.applicantName : application.applicantNames}

            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}
             />
          }
           { <Row
              label={t("Provisional fire NoC number")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/applicant-details`} />}
             />
          }
          </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("PROPERTY_DETAILS")}</CardSubHeader>
          <StatusTable>
          <Row
              label={t("PROPERTY TYPE")}
              text={application.special.code}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("NAME OF BUILDING")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
         <Row
            label={t("Building Usage Type as per NBC")} // Label for the row, presumably fetched from translations
            text={`${t(("applicantName"))}`}
            // actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />} // Action button component
          />
          <Row
              label={t("Height of the Building from Ground level (in meters)")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("No. Of Floors (Excluding Basement, Including Ground Floor)")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("No. Of Basements")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("Property Location Details")}</CardSubHeader>
          <StatusTable>
          <Row
              label={t("Property ID")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
  
          <Row
              label={t("City")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("Door/House No")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("Building/Colony Name")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("Street Name")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("Mohalla")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("Pincode")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("Locate on Map")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
          <Row
              label={t("Applicable Fire Station")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
  
          </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("Applicant Details")}</CardSubHeader>
          <StatusTable>
          <Row
              label={t("Mobile No.")}
              text={application.mobileNumber}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
            
          />
           <Row
              label={t("Name")}
              text={application.applicantName? application.applicantName : application.applicantNames}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
           <Row
              label={t("Gender")}
              text={application.genderr.code}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
           <Row
              label={t("Father/Husband's Name")}
              text={application.genderr.code}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
           <Row
              label={t("Relationship")}
              text={application.relation.code}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
           <Row
              label={t("Date Of Birth")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
           <Row
              label={t("Email")}
              text={application.emailId}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
        />
          <Row
              label={t("Pan no")}
              text={application.pan}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
            <Row
              label={t("Correspondence Address")}
              text={application.landmark}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/searchHall`} />}
  
          />
  
          </StatusTable>
          <CardSubHeader style={{ fontSize: "24px" }}>{t("Documents")}</CardSubHeader>
          <StatusTable>
         
           <Row
              label={t("Identity proof")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/address-details`} />}
          />
           <Row
              label={t("Address proof")}
              text={`${t(("applicantName"))}`}
            //   actionButton={<ActionButton jumpTo={`/digit-ui/citizen/chb/${typeOfApplication}/address-details`} />}
          />
          </StatusTable>
             
          <CheckBox
            label={t("FN_FINAL_DECLARATION_MESSAGE")}
            onChange={setdeclarationhandler}
            styles={{ height: "auto" }}
            //disabled={!agree}
          />
        </div>
        <SubmitBar label={t("FN_COMMON_BUTTON_SUBMIT")} onSubmit={onSubmit} disabled={!agree} />
      </Card>
     </React.Fragment>
    );
  };
  
  export default CheckPage;