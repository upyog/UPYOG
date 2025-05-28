import { CardSectionHeader, CheckPoint, ConnectingCheckPoints, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment, useCallback, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import PropTypes from 'prop-types';
import Caption from "./Caption";
import { cndStyles } from "../utils/cndStyles";


/**
 * CNDApplicationTimeLine Component
 * 
 * This component displays the timeline of an application within a workflow. 
 * It shows various checkpoints representing the status and actions taken on 
 * the application. The key features include:
 * 
 * 1. **Loading State**: While the workflow details are being fetched, a loader 
 *    is displayed to indicate that data is being loaded.
 * 
 * 2. **Timeline Display**: Once the data is loaded, the timeline of checkpoints 
 *    is rendered. Each checkpoint shows the state of the application, along with 
 *    relevant details such as last modified date, comments, and assigned personnel.
 * 
 * 3. **Next Actions**: Based on the current state of the application, the 
 *    component determines what actions can be taken next (e.g., making a payment 
 *    or editing details) and provides buttons for those actions.
 * 
 * 4. **Image Handling**: If there are any images associated with checkpoints, 
 *    they can be opened in a new tab when clicked.
 * 
 * The component utilizes hooks for managing state and translations for 
 * internationalization. It also organizes the timeline into a visually 
 * appealing format with headers and connecting lines.
 */

const CNDApplicationTimeLine = ({ application, id, userType }) => {
  const { t } = useTranslation();
  const businessService = application?.workflow?.businessService;

  const { isLoading, data } = Digit.Hooks.useWorkflowDetails({
    tenantId: application?.tenantId,
    id,
    moduleCode: businessService,
  });

  const OpenImage = useCallback((thumbnailsToShow) => {
    if (thumbnailsToShow?.fullImage?.[0]) {
      window.open(thumbnailsToShow.fullImage[0], "_blank");
    }
  }, []);

  const getTimelineCaptions = useCallback((checkpoint) => {
    if (!checkpoint) return null;

    if (checkpoint.state === "OPEN") {
      const caption = {
        date: checkpoint?.auditDetails?.lastModified,
        source: application?.channel || "",
      };
      return <Caption data={caption} />;
    }

    if (checkpoint.state) {
      const caption = {
        date: checkpoint?.auditDetails?.lastModified,
        name: checkpoint?.assignes?.[0]?.name,
        mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber,
        comment: t(checkpoint?.comment),
        wfComment: checkpoint.wfComment,
        thumbnailsToShow: checkpoint?.thumbnailsToShow,
      };
      return <Caption data={caption} OpenImage={OpenImage} />;
    }

    return (
      <Caption
        data={{
          date: Digit.DateUtils.ConvertTimestampToDate(application?.auditDetails.lastModified),
          name: checkpoint?.assigner?.name,
          comment: t(checkpoint?.comment),
        }}
      />
    );
  }, [application, t, OpenImage]);

  const showNextActions = useCallback((nextActions) => {
    const nextAction = nextActions?.[0];
    if (!nextAction) return null;

    switch (nextAction.action) {
      case "PAY":
        return userType === 'citizen' ? (
          <div style={cndStyles.payButton}>
            <Link
              to={{
                pathname: `/cnd-ui/citizen/payment/my-bills/cnd-service/${application?.applicationNumber}`,
                state: {
                  tenantId: application.tenantId,
                  applicationNumber: application?.applicationNo
                }
              }}
            >
              <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />
            </Link>
          </div>
        ) : null;
      default:
        return null;
    }
  }, [userType, application, t]);

  const timelineData = useMemo(() => {
    if (!data?.timeline) return null;

    return data.timeline.map((checkpoint, index) => ({
      isCompleted: index === 0,
      label: t(`CND_${data?.processInstances[index].state?.["state"]}`),
      checkpoint
    }));
  }, [data, t]);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      {!isLoading && (
        <Fragment>
          {data?.timeline?.length > 0 && (
            <CardSectionHeader style={cndStyles.timelineHeader}>
              {t("CS_APPLICATION_DETAILS_APPLICATION_TIMELINE")}
            </CardSectionHeader>
          )}
          {data?.timeline?.length === 1 ? (
            <CheckPoint
              isCompleted={true}
              label={t((data.timeline[0]?.state && `WF_${businessService}_${data.timeline[0].state}`) || "NA")}
              customChild={getTimelineCaptions(data.timeline[0])}
            />
          ) : (
            <ConnectingCheckPoints>
              {timelineData?.map(({ isCompleted, label, checkpoint }, index) => (
                <CheckPoint
                  key={index}
                  keyValue={index}
                  isCompleted={isCompleted}
                  label={label}
                  customChild={getTimelineCaptions(checkpoint)}
                />
              ))}
            </ConnectingCheckPoints>
          )}
        </Fragment>
      )}
      {data && showNextActions(data?.nextActions)}
    </React.Fragment>
  );
};

CNDApplicationTimeLine.propTypes = {
  application: PropTypes.shape({
    workflow: PropTypes.shape({
      businessService: PropTypes.string
    }),
    tenantId: PropTypes.string,
    applicationNumber: PropTypes.string,
    applicationNo: PropTypes.string,
    channel: PropTypes.string,
    auditDetails: PropTypes.object
  }),
  id: PropTypes.string.isRequired,
  userType: PropTypes.string
};

export default React.memo(CNDApplicationTimeLine);