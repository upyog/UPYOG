import { CardSectionHeader, CardSubHeader, CheckPoint, ConnectingCheckPoints, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import WFCaption from "./WFCaption";
import { useTranslation } from "react-i18next";

/*
  This file defines the `WFApplicationTimeline` component, which is used to render the workflow timeline of an application.
  - It fetches workflow details using Digit's custom hooks (`useWorkflowDetails`) based on the application ID and tenant ID.
  - The component displays checkpoints with relevant information such as dates, assignees, comments, and images.
  - It includes utility functions:
    - `OpenImage`: Opens a full image in a new browser tab.
    - `getTimelineCaptions`: Generates captions for workflow checkpoints using data from the workflow or application.
  - The component uses:
    - React for rendering.
    - `i18next` for internationalization.
    - UI components from the `@nudmcdgnpm/digit-ui-react-components` library for displaying headers, checkpoints, and loaders.
*/
const WFApplicationTimeline = (props) => {
  const { application, tenantId: propTenantId } = props; 
  const { t } = useTranslation();
  const businessService = "pgr-ai-services";
  
  const tenantId = propTenantId || application?.tenantId;
  const applicationId = application?.serviceRequestId || props.id;

  const { isLoading, data, isError } = Digit.Hooks.useWorkflowDetails({
    tenantId,
    id: applicationId,
    moduleCode: businessService,
    businessService,
    processInstanceSearchCriteria: {
      tenantId,
      businessIds: [applicationId],
      businessService
    }
  });

  const OpenImage = (thumbnailsToShow) => {
    if (thumbnailsToShow?.fullImage?.[0]) {
      window.open(thumbnailsToShow.fullImage[0], "_blank");
    }
  };

  const getTimelineCaptions = (checkpoint) => {
    if (!checkpoint) return null;

    const baseCaption = {
      date: checkpoint?.auditDetails?.lastModifiedTime || 
            checkpoint?.auditDetails?.createdTime || 
            application?.auditDetails?.lastModifiedTime,
      name: checkpoint?.assignes?.[0]?.name || 
            checkpoint?.assigner?.name || 
            application?.citizen?.name,
      mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber,
      comment: checkpoint?.comment || checkpoint?.wfComment || '',
      wfComment: checkpoint?.wfComment,
      thumbnailsToShow: checkpoint?.thumbnailsToShow,
      source: application?.source || "web"
    };

    return <WFCaption data={baseCaption} OpenImage={OpenImage} />;
  };

  if (isLoading) return <Loader />;
  if (isError) return <div className="error">{t("CS_COMMON_TIMELINE_FETCH_ERROR")}</div>;

  const timelineData = data?.timeline?.length > 0 
    ? data.timeline 
    : application?.workflow 
      ? [{
          state: application.workflow.action,
          auditDetails: application.auditDetails,
          assigner: { name: application.citizen?.name }
        }]
      : [];

  return (
    <Fragment>
      <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>
        <CardSubHeader style={{ fontSize: "24px" }}>{t("PGR_AI_GRIEVANCE_TIMELINE")}</CardSubHeader>
      </CardSectionHeader>

      {timelineData.length === 1 ? (
        <CheckPoint
          isCompleted={true}
          label={timelineData[0]?.state || "NA"}
          customChild={getTimelineCaptions(timelineData[0])}
        />
      ) : (
        <ConnectingCheckPoints>
          {timelineData.map((checkpoint, index) => (
            <CheckPoint
              key={index}
              isCompleted={index === 0}
              label={checkpoint?.state || "NA"}
              customChild={getTimelineCaptions(checkpoint)}
            />
          ))}
        </ConnectingCheckPoints>
      )}
    </Fragment>
  );
};

export default WFApplicationTimeline;