/**
 * PTRWFApplicationTimeline Component
 * 
 * Description:
 * This component displays the workflow timeline for a Pet Service application. 
 * It fetches and renders the application timeline using Digit's workflow hooks and 
 * displays checkpoints with captions, images, and next actions.
 * 
 * Key Functionalities:
 * - Fetches workflow details based on `tenantId`, `applicationNumber`, and `moduleCode`.
 * - Displays the application timeline with checkpoints and captions.
 * - Shows checkpoint details including:
 *    - Date
 *    - Assigner/Assignee name
 *    - Mobile number
 *    - Comments and workflow comments
 *    - Thumbnails (images) with clickable preview functionality.
 * - Renders a "Make Payment" button for citizen users when applicable.
 * - Displays a loader while fetching data.
 * 
 * Props:
 * - `application`: Contains the application details including:
 *    - `tenantId`: ID of the current tenant.
 *    - `applicationNumber`: Unique ID of the application.
 *    - `workflow`: Contains business service information.
 *    - `channel`: Channel through which the application was submitted.
 * - `userType`: Defines whether the user is a citizen or employee.
 * 
 * Dependencies:
 * - `Digit.Hooks.useWorkflowDetails`: Hook to fetch workflow details.
 * - `Digit.DateUtils.ConvertTimestampToDate`: Utility for date conversion.
 * - `PTRWFCaption`: Custom component for rendering captions.
 * - `Loader`, `CardSectionHeader`, `CheckPoint`, `ConnectingCheckPoints`, `SubmitBar`: UI components from `@nudmcdgnpm/digit-ui-react-components`.
 * 
 */

import { CardSectionHeader, CheckPoint, ConnectingCheckPoints, Loader, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import PTRWFCaption from "./PTRWFCaption";


const PTRWFApplicationTimeline = (props) => {
  
  const { t } = useTranslation();
  const businessService = props?.application?.workflow?.businessService;
  

  const { isLoading, data } = Digit.Hooks.useWorkflowDetails({
    tenantId: props.application?.tenantId,
    id: props.application?.applicationNumber,
    moduleCode: businessService,
  });
  

  function OpenImage(thumbnailsToShow) {
    window.open(thumbnailsToShow?.fullImage?.[0], "_blank");
  }

  const getTimelineCaptions = (checkpoint) => {
    
    if (checkpoint.state === "OPEN")
    {
      const caption = {
        date: checkpoint?.auditDetails?.lastModified,
        source: props.application?.channel || "",
      };
      return <PTRWFCaption data={caption} />;
    }
    else if (checkpoint.state) {
      const caption = {
        date: checkpoint?.auditDetails?.lastModified,
        name: checkpoint?.assignes?.[0]?.name,
        mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber,
        comment: t(checkpoint?.comment),
        wfComment: checkpoint.wfComment,
        thumbnailsToShow: checkpoint?.thumbnailsToShow,
      };
      return <PTRWFCaption data={caption} OpenImage={OpenImage} />;
    } 
    
   
    else {
      const caption = {
        date: Digit.DateUtils.ConvertTimestampToDate(props.application?.auditDetails.lastModified),
        name: checkpoint?.assigner?.name,
        comment: t(checkpoint?.comment),
      };
      return <PTRWFCaption data={caption} />;
    }
  };

  const showNextActions = (nextActions) => {
    let nextAction = nextActions[0];
    const next = nextActions.map((action) => action.action);
    if (next.includes("PAY") || next.includes("EDIT")) {
      let currentIndex = next.indexOf("EDIT") || next.indexOf("PAY");
      currentIndex = currentIndex != -1 ? currentIndex : next.indexOf("PAY");
      nextAction = nextActions[currentIndex];
    }
    switch (nextAction?.action) {
      case "PAY":
        return (
          props?.userType === 'citizen'
          ? (
          <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
            <Link
              to={{ pathname: `/digit-ui/citizen/payment/my-bills/${businessService}/${props?.application?.applicationNumber}`, state: { tenantId: props.application.tenantId, applicationNumber : props?.application?.applicationNumber } }}
            >
              <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />
            </Link>
          </div>
          ) : null
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
                data?.timeline.map((checkpoint, index, arr) => {
                  
                  let timelineStatusPostfix = "";
                  return (
                    <React.Fragment key={index}>
                      <CheckPoint
                        keyValue={index}
                        isCompleted={index === 0}
                       //label={checkpoint.state ? t(`WF_${businessService}_${checkpoint.state}`) : "NA"}
                       label={t(
                        `ES_PTR_COMMON_STATUS_${data?.processInstances[index].state?.["state"]
                        }${timelineStatusPostfix}`
                      )}
                        customChild={getTimelineCaptions(checkpoint)}
                      />
                    </React.Fragment>
                  );
                })}
            </ConnectingCheckPoints>
          )}
        </Fragment>
      )}
      {data && showNextActions(data?.nextActions)}
    </React.Fragment>
  );
};

export default PTRWFApplicationTimeline;