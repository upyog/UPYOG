import { CardSectionHeader, CheckPoint, ConnectingCheckPoints, Loader, TelePhone, DisplayPhotos } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";

const GCWFCaption = ({ data, OpenImage }) => {
  const { t } = useTranslation();
  return (
    <div>
      {data.date && <p>{data.date}</p>}
      <p>{data.name}</p>
      {data.mobileNumber && <TelePhone mobile={data.mobileNumber} />}
      {data.source && <p>{t("ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_" + data.source.toUpperCase())}</p>}
      {data.comment && <p>{data.comment}</p>}
      {data?.wfComment ? (
        <div>
          {data.wfComment.map((e, i) => (
            <div key={i} className="TLComments">
              <h3>{t("WF_COMMON_COMMENTS")}</h3>
              <p style={{ overflowX: "scroll" }}>{e}</p>
            </div>
          ))}
        </div>
      ) : null}
      {data?.thumbnailsToShow?.thumbs?.length > 0 ? (
        <div className="TLComments">
          <h3>{t("CS_COMMON_ATTACHMENTS")}</h3>
          <DisplayPhotos srcs={data.thumbnailsToShow.thumbs} onClick={(src, index) => OpenImage(src, index, data.thumbnailsToShow)} />
        </div>
      ) : null}
    </div>
  );
};

const GCWFApplicationTimeline = ({ application }) => {
  const { t } = useTranslation();
  const businessService = application?.businessService || "garbage-service";

  const { isLoading, data } = Digit.Hooks.useWorkflowDetails({
    tenantId: application?.tenantId,
    id: application?.grbgApplication?.applicationNo || application?.grbgApplicationNumber,
    moduleCode: businessService,
    config: {
      enabled: !!(application?.tenantId && (application?.grbgApplication?.applicationNo || application?.grbgApplicationNumber)),
    },
  });

  function OpenImage(imageSource, index, thumbnailsToShow) {
    window.open(thumbnailsToShow?.fullImage?.[0], "_blank");
  }

  const getTimelineCaptions = (checkpoint) => {
    if (checkpoint.state === "OPEN") {
      return <GCWFCaption data={{ date: checkpoint?.auditDetails?.lastModified, source: application?.channel || "" }} />;
    }
    return (
      <GCWFCaption
        data={{
          date: checkpoint?.auditDetails?.lastModified,
          name: checkpoint?.assignes?.[0]?.name,
          mobileNumber: checkpoint?.assignes?.[0]?.mobileNumber,
          comment: t(checkpoint?.comment),
          wfComment: checkpoint.wfComment,
          thumbnailsToShow: checkpoint?.thumbnailsToShow,
        }}
        OpenImage={OpenImage}
      />
    );
  };

  if (isLoading) return <Loader />;

  return (
    <React.Fragment>
      {data?.timeline?.length > 0 && (
        <Fragment>
          <CardSectionHeader style={{ marginBottom: "16px", marginTop: "32px" }}>
            {t("CS_APPLICATION_DETAILS_APPLICATION_TIMELINE")}
          </CardSectionHeader>
          {data.timeline.length === 1 ? (
            <CheckPoint
              isCompleted={true}
              label={t(data.timeline[0]?.state ? `WF_${businessService}_${data.timeline[0].state}` : "NA")}
              customChild={getTimelineCaptions(data.timeline[0])}
            />
          ) : (
            <ConnectingCheckPoints>
              {data.timeline.map((checkpoint, index) => (
                <React.Fragment key={index}>
                  <CheckPoint
                    keyValue={index}
                    isCompleted={index === 0}
                    label={t(data?.processInstances?.[index]?.state?.["state"] || checkpoint.state || "NA")}
                    customChild={getTimelineCaptions(checkpoint)}
                  />
                </React.Fragment>
              ))}
            </ConnectingCheckPoints>
          )}
        </Fragment>
      )}
    </React.Fragment>
  );
};

export default GCWFApplicationTimeline;
