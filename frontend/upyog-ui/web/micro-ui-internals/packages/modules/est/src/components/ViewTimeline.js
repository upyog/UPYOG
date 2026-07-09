import { CardSectionHeader, CheckPoint, ConnectingCheckPoints, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import Caption from "./Caption";
import { getCitizenPaymentPath } from "../utils/estRoutes";

const ViewTimeline = (props) => {
  const { t } = useTranslation();
  const businessService = props?.application?.workflow?.businessService || "EST";

  const { isLoading, data } = Digit.Hooks.useWorkflowDetails({
    tenantId: props.application?.tenantId,
    id: props.application?.applicationNo || props.id,
    moduleCode: businessService,
    role: props.userType === "employee" ? "EMPLOYEE" : "CITIZEN",
  });

  function OpenImage(thumbnailsToShow) {
    window.open(thumbnailsToShow?.fullImage?.[0], "_blank");
  }

  const getTimelineCaptions = (checkpoint) => {
    const caption = {
      date: checkpoint?.auditDetails?.lastModified,
      name: checkpoint?.assignes?.[0]?.name,
      mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber,
      comment: t(checkpoint?.comment),
      wfComment: checkpoint?.wfComment,
      thumbnailsToShow: checkpoint?.thumbnailsToShow,
      source: props.application?.channel || "CITIZEN",
    };
    return <Caption data={caption} OpenImage={OpenImage} />;
  };

  const showNextActions = (nextActions) => {
    const nextAction = nextActions?.[0];
    switch (nextAction?.action) {
      case "PAY":
        return (
          props?.userType === "citizen" ? (
            <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
              <Link
                to={{
                  pathname: getCitizenPaymentPath(props?.application?.applicationNo),
                  state: {
                    tenantId: props.application.tenantId,
                    applicationNumber: props?.application?.applicationNo,
                  },
                }}
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

  const timeline = data?.timeline || [];

  return (
    <React.Fragment>
      {!isLoading && (
        <Fragment>
          {timeline.length > 0 && (
            <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>
              {t("CS_APPLICATION_DETAILS_APPLICATION_TIMELINE")}
            </CardSectionHeader>
          )}
          {timeline.length === 1 ? (
            <CheckPoint
              isCompleted={true}
              label={t(`WF_${businessService}_${timeline[0]?.state}`) || timeline[0]?.state || "NA"}
              customChild={getTimelineCaptions(timeline[0])}
            />
          ) : (
            <ConnectingCheckPoints>
              {timeline.map((checkpoint, index) => (
                <React.Fragment key={index}>
                  <CheckPoint
                    keyValue={index}
                    isCompleted={index === 0}
                    label={t(`WF_${businessService}_${checkpoint.state}`) || checkpoint.state || "NA"}
                    customChild={getTimelineCaptions(checkpoint)}
                  />
                </React.Fragment>
              ))}
            </ConnectingCheckPoints>
          )}
        </Fragment>
      )}
      {data?.nextActions && showNextActions(data.nextActions)}
    </React.Fragment>
  );
};

export default ViewTimeline;
