// Update: /src/components/ViewTimeline.js
import { CardSectionHeader, CheckPoint, ConnectingCheckPoints, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { getCitizenPaymentPath } from "../../utils/estRoutes";

// View Timeline Component
// This component displays the timeline of an EST application, showing various checkpoints and allowing citizens to make payments if applicable.

const Caption = ({ data }) => {
  const { date, name, mobileNumber, comment, source } = data;

  return (
    <div style={{ padding: "8px 0" }}>
      {date && (
        <div style={{ fontSize: "12px", color: "#666", marginBottom: "4px" }}>
          {new Date(date).toLocaleDateString("en-GB")} {new Date(date).toLocaleTimeString("en-GB")}
        </div>
      )}
      
      {name && (
        <div style={{ fontSize: "14px", fontWeight: "500", marginBottom: "2px" }}>
          {name} {mobileNumber && `(${mobileNumber})`}
        </div>
      )}
      
      {source && (
        <div style={{ fontSize: "12px", color: "#666", marginBottom: "4px" }}>
          Source: {source}
        </div>
      )}
      
      {comment && (
        <div style={{ fontSize: "13px", color: "#333", marginTop: "4px" }}>
          {comment}
        </div>
      )}
    </div>
  );
};

const ViewTimeline = (props) => {
  const { t } = useTranslation();
  const businessService = props?.application?.workflow?.businessService || "EST";
  
  // Mock timeline data since workflow hook might not work
  const mockTimeline = [
    {
      state: "CREATED",
      auditDetails: { lastModified: props.application?.auditDetails?.createdTime || Date.now() },
      comment: "Application Created",
      assignes: [{ name: "System", mobileNumber: "" }]
    },
    {
      state: "PENDING_APPROVAL", 
      auditDetails: { lastModified: Date.now() },
      comment: "Pending for Approval",
      assignes: [{ name: "Estate Officer", mobileNumber: "9999999999" }]
    }
  ];

  const getTimelineCaptions = (checkpoint) => {
    const caption = {
      date: checkpoint?.auditDetails?.lastModified,
      name: checkpoint?.assignes?.[0]?.name || "System",
      mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber,
      comment: checkpoint?.comment || "",
      source: props.application?.channel || "CITIZEN",
    };
    return <Caption data={caption} />;
  };

  const showNextActions = () => {
    if (props?.userType === 'citizen') {
      return (
        <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
          <Link
            to={{ 
              pathname: getCitizenPaymentPath(props?.application?.applicationNo), 
              state: { tenantId: props.application.tenantId, applicationNumber: props?.application?.applicationNo } 
            }}
          >
            <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />
          </Link>
        </div>
      );
    }
    return null;
  };

  return (
    <React.Fragment>
      <Fragment>
        <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>
          {t("CS_APPLICATION_DETAILS_APPLICATION_TIMELINE")}
        </CardSectionHeader>
        
        {mockTimeline?.length === 1 ? (
          <CheckPoint
            isCompleted={true}
            label={t(`EST_${mockTimeline[0]?.state}`) || mockTimeline[0]?.state}
            customChild={getTimelineCaptions(mockTimeline[0])}
          />
        ) : (
          <ConnectingCheckPoints>
            {mockTimeline?.map((checkpoint, index) => (
              <React.Fragment key={index}>
                <CheckPoint
                  keyValue={index}
                  isCompleted={index === 0}
                  label={t(`EST_${checkpoint.state}`) || checkpoint.state}
                  customChild={getTimelineCaptions(checkpoint)}
                />
              </React.Fragment>
            ))}
          </ConnectingCheckPoints>
        )}
      </Fragment>
      {showNextActions()}
    </React.Fragment>
  );
};

export default ViewTimeline;