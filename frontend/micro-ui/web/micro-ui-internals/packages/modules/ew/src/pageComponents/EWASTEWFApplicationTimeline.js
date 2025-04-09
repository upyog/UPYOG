// Importing necessary components and hooks from external libraries and local files
import { ActionLinks, CardSectionHeader, CheckPoint, CloseSvg, ConnectingCheckPoints, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components"; // UI components for timeline, checkpoints, and buttons
import React, { Fragment } from "react"; // React library for building UI components
import { useTranslation } from "react-i18next"; // Hook for translations
import { Link } from "react-router-dom"; // Component for navigation links
import EWASTEWFCaption from "../components/EWASTEWFCaption"; // Component for rendering captions in the timeline

// Main component for displaying the workflow timeline of an E-Waste application
const EWASTEWFApplicationTimeline = (props) => {
  const { t } = useTranslation(); // Translation hook
  const businessService = props?.application?.workflow?.businessService; // Extracting the business service from the application data

  // Fetching workflow details using a custom hook
  const { isLoading, data } = Digit.Hooks.useWorkflowDetails({
    tenantId: props.application?.tenantId, // Tenant ID of the application
    id: props.application?.requestId, // Request ID of the application
    moduleCode: businessService, // Module code for the workflow
  });

  // Function to open an image in a new tab
  function OpenImage(imageSource, index, thumbnailsToShow) {
    window.open(thumbnailsToShow?.fullImage?.[0], "_blank");
  }

  // Function to generate captions for each checkpoint in the timeline
  const getTimelineCaptions = (checkpoint) => {
    if (checkpoint.state === "OPEN") {
      // Caption for an open state
      const caption = {
        date: checkpoint?.auditDetails?.lastModified, // Last modified date
        source: props.application?.channel || "", // Source of the application
      };
      return <EWASTEWFCaption data={caption} />;
    } else if (checkpoint.state) {
      // Caption for a completed state
      const caption = {
        date: checkpoint?.auditDetails?.lastModified, // Last modified date
        name: checkpoint?.assignes?.[0]?.name, // Name of the assignee
        mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber, // Mobile number of the assignee
        comment: t(checkpoint?.comment), // Comment from the workflow
        wfComment: checkpoint.wfComment, // Workflow-specific comment
        thumbnailsToShow: checkpoint?.thumbnailsToShow, // Thumbnails to display
      };
      return <EWASTEWFCaption data={caption} OpenImage={OpenImage} />;
    } else {
      // Caption for other states
      const caption = {
        date: Digit.DateUtils.ConvertTimestampToDate(props.application?.auditDetails.lastModified), // Last modified date
        name: checkpoint?.assigner?.name, // Name of the assigner
        comment: t(checkpoint?.comment), // Comment from the workflow
      };
      return <EWASTEWFCaption data={caption} />;
    }
  };

  // Function to display the next actions available in the workflow
  const showNextActions = (nextActions) => {
    let nextAction = nextActions[0];
    const next = nextActions.map((action) => action.action);
    if (next.includes("PAY") || next.includes("EDIT")) {
      let currentIndex = next.indexOf("EDIT") || next.indexOf("PAY");
      currentIndex = currentIndex !== -1 ? currentIndex : next.indexOf("PAY");
      nextAction = nextActions[currentIndex];
    }
    switch (nextAction?.action) {
      case "PAY":
        // Render a "Make Payment" button for citizens
        return (
          props?.userType === "citizen" ? (
            <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
              <Link
                to={{
                  pathname: `/digit-ui/citizen/payment/my-bills/${businessService}/${props?.application?.applicationNumber}`,
                  state: { tenantId: props.application.tenantId, applicationNumber: props?.application?.applicationNumber },
                }}
              >
                <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />
              </Link>
            </div>
          ) : null
        );

      case "SUBMIT_FEEDBACK":
        // Render a "Rate" button for submitting feedback
        return (
          <div style={{ marginTop: "24px" }}>
            <Link to={`/digit-ui/citizen/fsm/rate/${props.id}`}>
              <SubmitBar label={t("CS_APPLICATION_DETAILS_RATE")} />
            </Link>
          </div>
        );
      default:
        return null;
    }
  };

  // Display a loader while the workflow details are being fetched
  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      {!isLoading && (
        <Fragment>
          {/* Render the timeline header if there are timeline entries */}
          {data?.timeline?.length > 0 && (
            <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>
              {t("CS_APPLICATION_DETAILS_APPLICATION_TIMELINE")}
            </CardSectionHeader>
          )}
          {/* Render the timeline checkpoints */}
          {data?.timeline && data?.timeline?.length === 1 ? (
            <CheckPoint
              isCompleted={true}
              label={t((data?.timeline[0]?.state && `WF_${businessService}_${data.timeline[0].state}`) || "NA")}
              customChild={getTimelineCaptions(data?.timeline[0])}
            />
          ) : (
            <ConnectingCheckPoints>
              {data?.timeline &&
                data?.timeline.map((checkpoint, index) => {
                  return (
                    <React.Fragment key={index}>
                      <CheckPoint
                        keyValue={index}
                        isCompleted={index === 0}
                        label={t(`${data?.processInstances[index].state?.["state"]}`)}
                        customChild={getTimelineCaptions(checkpoint)}
                      />
                    </React.Fragment>
                  );
                })}
            </ConnectingCheckPoints>
          )}
        </Fragment>
      )}
      {/* Render the next actions available in the workflow */}
      {data && showNextActions(data?.nextActions)}
    </React.Fragment>
  );
};

export default EWASTEWFApplicationTimeline; // Exporting the component