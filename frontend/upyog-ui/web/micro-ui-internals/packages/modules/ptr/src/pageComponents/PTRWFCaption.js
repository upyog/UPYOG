/**
 * @file PTRWFCaption.js
 * @description 
 * This component is responsible for rendering the caption information displayed in the workflow timeline. 
 * It shows details such as:
 * - Date and name of the person involved in the workflow.
 * - Mobile number with a clickable telephone component.
 * - Source of the application (e.g., portal, mobile app).
 * - Comments related to the workflow, including system-generated and user-provided comments.
 * - Displaying workflow comments as scrollable text.
 * - Thumbnails and attachments, if available, with an option to open full images in a new tab.
 * 
 * @dependencies 
 * - `React` for component rendering.
 * - `useTranslation` hook from `react-i18next` for handling translations.
 * - `TelePhone` and `DisplayPhotos` components from `@upyog/digit-ui-react-components` for displaying phone numbers and images.
 * 
 * @props
 * - `data`: Object containing workflow details such as date, name, mobile number, comments, and attachments.
 * - `OpenImage`: Function to open the full image in a new tab when a thumbnail is clicked.
 * 
 * @usage 
 * Used within the `PTRWFApplicationTimeline` component to display captions for each checkpoint in the workflow timeline.
 * 
 * @example
 * <PTRWFCaption data={captionData} OpenImage={handleOpenImage} />
 */
import React from "react";
import { useTranslation } from "react-i18next";
import { TelePhone, DisplayPhotos } from "@upyog/digit-ui-react-components";


const PTRWFCaption = ({ data,OpenImage }) => {
  const { t } = useTranslation();
  return (
    <div>
      {data.date && <p>{data.date}</p>}
      <p>{data.name}</p>
      {data.mobileNumber && <TelePhone mobile={data.mobileNumber} />}
      {data.source && <p>{t("ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_" + data.source.toUpperCase())}</p>}
      {data.comment && <Reason otherComment={data?.otherComment} headComment={data?.comment}></Reason>}
      {data?.wfComment ? <div>{data?.wfComment?.map( e => 
      <div className="TLComments">
        <h3>{t("WF_COMMON_COMMENTS")}</h3>
        <p style={{overflowX:"scroll"}}>{e}</p>
      </div>
      )}</div> : null}
      {data?.thumbnailsToShow?.thumbs?.length > 0 ? <div className="TLComments">
      <h3>{t("CS_COMMON_ATTACHMENTS")}</h3>
      <DisplayPhotos srcs={data?.thumbnailsToShow.thumbs} onClick={(src, index) => {OpenImage(src, index,data?.thumbnailsToShow)}} />
    </div> : null}
    </div>
  );
};

export default PTRWFCaption;
