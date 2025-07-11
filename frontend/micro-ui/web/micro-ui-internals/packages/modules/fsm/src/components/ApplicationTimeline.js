import React, { Fragment, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useHistory, useParams } from "react-router-dom";
import {
  Header,
  ActionLinks,
  Card,
  CardSectionHeader,
  ConnectingCheckPoints,
  CheckPoint,
  KeyNote,
  SubmitBar,
  LinkButton,
  Loader,
  Rating,
} from "@upyog/digit-ui-react-components";
import _ from "lodash";
import TLCaption from "./TLCaption";

export const ApplicationTimeline = (props) => {
  const { t } = useTranslation();
  const { isLoading, data } = Digit.Hooks.useWorkflowDetails({
    tenantId: props.application?.tenantId,
    id: props.id,
    moduleCode: "FSM",
  });
  const [showAllTimeline, setShowAllTimeline]=useState(false);
  const getTimelineCaptions = (checkpoint) => {
    const __comment = checkpoint?.comment?.split("~");
    const reason = __comment ? __comment[0] : null;
    const reason_comment = __comment ? __comment[1] : null;
    if (checkpoint.status === "CREATED") {
      const caption = {
        date: checkpoint?.auditDetails?.created,
        source: props.application?.source || "",
      };
      return <TLCaption data={caption} />;
    } else if (
      checkpoint.status === "PENDING_APPL_FEE_PAYMENT" ||
      checkpoint.status === "DSO_REJECTED" ||
      checkpoint.status === "CANCELED" ||
      checkpoint.status === "REJECTED"
    ) {
      const caption = {
        date: checkpoint?.auditDetails?.created,
        name: checkpoint?.assigner,
        comment: reason ? t(`ES_ACTION_REASON_${reason}`) : null,
        otherComment: reason_comment ? reason_comment : null,
      };
      return <TLCaption data={caption} />;
    } else if (checkpoint.status === "CITIZEN_FEEDBACK_PENDING") {
      return (
        <>
          {data?.nextActions.length > 0 && (
            <div>
              <Link to={`/digit-ui/citizen/fsm/rate/${props.id}`}>
                <ActionLinks>{t("CS_FSM_RATE")}</ActionLinks>
              </Link>
            </div>
          )}
        </>
      );
    } else if (checkpoint.status === "DSO_INPROGRESS") {
      const caption = {
        name: checkpoint?.assigner,
        mobileNumber: props.application?.dsoDetails?.mobileNumber,
        date: `${t("CS_FSM_EXPECTED_DATE")} ${Digit.DateUtils.ConvertTimestampToDate(props.application?.possibleServiceDate)}`,
      };
      return <TLCaption data={caption} />;
    } else if (checkpoint.status === "COMPLETED") {
      return (
        <div>
          <Rating withText={true} text={t(`CS_FSM_YOU_RATED`)} currentRating={checkpoint.rating} />
          <Link to={`/digit-ui/citizen/fsm/rate-view/${props.id}`}>
            <ActionLinks>{t("CS_FSM_RATE_VIEW")}</ActionLinks>
          </Link>
        </div>
      );
    } else if (checkpoint.status === "DISPOSAL_IN_PROGRESS") {
      const caption = {
        date: checkpoint?.auditDetails?.created,
        name: checkpoint?.assigner,
        mobileNumber: checkpoint?.assigner?.mobileNumber,
      };
      if (checkpoint?.numberOfTrips) caption.comment = `${t("NUMBER_OF_TRIPS")}: ${checkpoint?.numberOfTrips}`;
      return <TLCaption data={caption} />;
    }
    else if (checkpoint.status === "PENDING_PAYYY") {
      const caption = {
        name: checkpoint?.assigner,
        mobileNumber: checkpoint?.assigner?.mobileNumber,
        date: `${t("CS_FSM_EXPECTED_DATE")} ${Digit.DateUtils.ConvertTimestampToDate(props.application?.possibleServiceDate)}`,
      };
      return <TLCaption data={caption} />;
  };
}

  const showNextActions = (nextAction) => {
    switch (nextAction?.action) {
      case "PAY":
        return (
          <div style={{ marginTop: "24px" }}>
            <Link
              to={{
                pathname: `/digit-ui/citizen/payment/my-bills/FSM.TRIP_CHARGES/${props.id}/?tenantId=${props.application.tenantId}`,
                state: { tenantId: props.application.tenantId },
              }}
            >
              {window.location.href.includes("citizen/fsm/") && <SubmitBar label={t("CS_APPLICATION_DETAILS_MAKE_PAYMENT")} />}
            </Link>
          </div>
        );
      case "SUBMIT_FEEDBACK":
        return (
          <div style={{ marginTop: "24px" }}>
            <Link to={`/digit-ui/citizen/fsm/rate/${props.id}`}>
              <SubmitBar label={t("CS_APPLICATION_DETAILS_RATE")} />
            </Link>
          </div>
        );
    }
  };

  if (isLoading) {
    return <Loader />;
  }
  const toggleTimeline=()=>{
    setShowAllTimeline((prev)=>!prev);
  }

  let deepCopy = _.cloneDeep( data )
let index1 =0
deepCopy?.timeline.map((check,index) => {
  if (check.status == "ASSING_DSO" && index1 ==0)
  {
      let obj= check
      obj.status = "PENDING_PAYYY"
      index1 +=1
      data.timeline[index].status ="ASSING_DSO_PAY"
      data.timeline.splice(index, 0, obj);
  }
})
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
            <CheckPoint isCompleted={true} label={t("CS_COMMON_FSM_" + `${data?.timeline[0]?.performedAction === "UPDATE" ? "UPDATE_" : ""}` + data?.timeline[0]?.status)} customChild={getTimelineCaptions(data?.timeline[0])} />            
          ) : (
            <ConnectingCheckPoints>
              {data?.timeline &&
                data?.timeline.slice(0,showAllTimeline? data.timeline.length:2).map((checkpoint, index, arr) => {
                  return (
                    <React.Fragment key={index}>
                      <CheckPoint
                        keyValue={index}
                        isCompleted={index === 0}
                        label={t("CS_COMMON_" +  `${checkpoint?.performedAction === "UPDATE" ? "UPDATE_" : ""}` + checkpoint.status)}
                        customChild={getTimelineCaptions(checkpoint)}
                      />
                    </React.Fragment>
                  );
                })}
            </ConnectingCheckPoints>
          )}
          {data?.timeline?.length > 2 && (
            <LinkButton label={showAllTimeline? t("COLLAPSE") : t("VIEW_TIMELINE")} onClick={toggleTimeline}>
            </LinkButton>
          )}
        </Fragment>
      )}
      {data && showNextActions(data?.nextActions[0])}
    </React.Fragment>
  );
};