import { ActionLinks, CardSectionHeader, CheckPoint, CloseSvg, ConnectingCheckPoints, Loader, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import EWASTEWFCaption from "../components/EWASTEWFCaption";

/**
 * Displays a timeline view of an E-Waste application's workflow.
 * Shows checkpoints, status changes, and available actions based on application state.
 *
 * @param {Object} props Component properties
 * @param {Object} props.application Application details containing workflow information
 * @param {string} props.application.tenantId Tenant identifier
 * @param {string} props.application.requestId Unique request identifier
 * @param {Object} props.application.workflow Workflow configuration
 * @param {string} props.userType Type of user viewing the timeline (citizen/employee)
 * @returns {JSX.Element} Application workflow timeline
 */
const EWASTEWFApplicationTimeline = (props) => {
  const { t } = useTranslation();
  const businessService = props?.application?.workflow?.businessService;

  const { isLoading, data } = Digit.Hooks.useWorkflowDetails({
    tenantId: props.application?.tenantId,
    id: props.application?.requestId,
    moduleCode: businessService,
  });

  /**
   * Opens the full-size image in a new tab
   * 
   * @param {string} imageSource Source URL of the image
   * @param {number} index Index of the image in the collection
   * @param {Object} thumbnailsToShow Collection of thumbnail and full-size images
   */
  function OpenImage(imageSource, index, thumbnailsToShow) {
    window.open(thumbnailsToShow?.fullImage?.[0], "_blank");
  }

  /**
   * Generates caption content for timeline checkpoints based on state
   * 
   * @param {Object} checkpoint Workflow checkpoint data
   * @returns {JSX.Element} Caption component with checkpoint details
   */
  const getTimelineCaptions = (checkpoint) => {
    if (checkpoint.state === "OPEN") {
      const caption = {
        date: checkpoint?.auditDetails?.lastModified,
        source: props.application?.channel || "",
      };
      return <EWASTEWFCaption data={caption} />;
    } else if (checkpoint.state) {
      const caption = {
        date: checkpoint?.auditDetails?.lastModified,
        name: checkpoint?.assignes?.[0]?.name,
        mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber,
        comment: t(checkpoint?.comment),
        wfComment: checkpoint.wfComment,
        thumbnailsToShow: checkpoint?.thumbnailsToShow,
      };
      return <EWASTEWFCaption data={caption} OpenImage={OpenImage} />;
    } else {
      const caption = {
        date: Digit.DateUtils.ConvertTimestampToDate(props.application?.auditDetails.lastModified),
        name: checkpoint?.assigner?.name,
        comment: t(checkpoint?.comment),
      };
      return <EWASTEWFCaption data={caption} />;
    }
  };

  /**
   * Determines and renders the next available actions in the workflow
   * 
   * @param {Array} nextActions List of possible next actions
   * @returns {JSX.Element|null} Action buttons or null if no actions available
   */
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
        return (
          props?.userType === "citizen" ? (
            <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
              <Link
                to={{
                  pathname: `/upyog-ui/citizen/payment/my-bills/${businessService}/${props?.application?.applicationNumber}`,
                  state: { tenantId: props.application.tenantId, applicationNumber: props?.application?.applicationNumber },
                }}
              >
                <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />
              </Link>
            </div>
          ) : null
        );

      case "SUBMIT_FEEDBACK":
        return (
          <div style={{ marginTop: "24px" }}>
            <Link to={`/upyog-ui/citizen/fsm/rate/${props.id}`}>
              <SubmitBar label={t("CS_APPLICATION_DETAILS_RATE")} />
            </Link>
          </div>
        );
      default:
        return null;
    }
  };

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      {!isLoading && (
        <Fragment>
          {data?.timeline?.length > 0 && (
            <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>
              {t("CS_APPLICATION_DETAILS_APPLICATION_TIMELINE")}
            </CardSectionHeader>
          )}
          {data?.timeline && data?.timeline?.length === 1 ? (
            <CheckPoint
              isCompleted={true}
              label={t((data?.timeline[0]?.state && `WF_${businessService}_${data.timeline[0].state}`) || "NA")}
              customChild={getTimelineCaptions(data?.timeline[0])}
            />
          ) : (
            <ConnectingCheckPoints>
              {data?.timeline &&
                data?.timeline.map((checkpoint, index) => (
                  <React.Fragment key={index}>
                    <CheckPoint
                      keyValue={index}
                      isCompleted={index === 0}
                      label={t(`${data?.processInstances[index].state?.["state"]}`)}
                      customChild={getTimelineCaptions(checkpoint)}
                    />
                  </React.Fragment>
                ))}
            </ConnectingCheckPoints>
          )}
        </Fragment>
      )}
      {data && showNextActions(data?.nextActions)}
    </React.Fragment>
  );
};

export default EWASTEWFApplicationTimeline;