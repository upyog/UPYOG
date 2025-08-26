import { CardSectionHeader, CheckPoint, ConnectingCheckPoints, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import WFCaption from "./WFCaption";
import { APPLICATION_PATH } from "../utils";

// This component WFApplicationTimeline displays the timeline of an application process.
const WFApplicationTimeline = (props) => {
  let bookingCode=props?.application?.bookingNo.split("-")[0]; // for selecting the Module code from booking number
  const { t } = useTranslation();
  const businessService = bookingCode=="WT" 
        ? "watertanker"
          : bookingCode === "MT"
          ? "mobileToilet"
          : bookingCode === "TP"
          ? "treePruning"
          : "unknown"; // for selecting the Module code from booking number

  // const moduleName = bookingCode=="WT" ? "request-service.water_tanker" : "request-service.mobile_toilet"; // for selecting the Module code from booking number
  const moduleName =
  bookingCode === "WT"
    ? "request-service.water_tanker"
    : bookingCode === "MT"
    ? "request-service.mobile_toilet"
    : bookingCode === "TP"
    ? "request-service.tree_pruning"
    : "request-service.unknown";
  

  const { isLoading, data } = Digit.Hooks.useWorkflowDetails({
    tenantId: props.application?.tenantId,
    id: props.application?.bookingNo,
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
      return <WFCaption data={caption} />;
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
      return <WFCaption data={caption} OpenImage={OpenImage} />;
    } 
    
   
    else {
      const caption = {
        date: Digit.DateUtils.ConvertTimestampToDate(props.application?.auditDetails.lastModified),
        name: checkpoint?.assigner?.name,
        comment: t(checkpoint?.comment),
      };
      return <WFCaption data={caption} />;
    }
  };

  const showNextActions = (nextActions) => {
    let nextAction = nextActions[0];
    switch (nextAction?.action) {
      case "PAY":
        return (
          props?.userType === 'citizen'
          ? (
          <div style={{ marginTop: "1em", bottom: "0px", width: "100%", marginBottom: "1.2em" }}>
            <Link
              to={{ pathname: `${APPLICATION_PATH}/citizen/payment/my-bills/${moduleName}/${props?.application?.bookingNo}`, state: { tenantId: props.application.tenantId, bookingNo : props?.application?.bookingNo } }}
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
                label={t((data?.timeline[0]?.state && `${data.timeline[0].state}`) || "NA")}
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
                          label={t(
                            `${data?.processInstances[index].state?.["state"]}`
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
export default WFApplicationTimeline;