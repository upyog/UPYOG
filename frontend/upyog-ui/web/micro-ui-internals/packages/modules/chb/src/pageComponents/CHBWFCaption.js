import React from "react";
import { useTranslation } from "react-i18next";
import { TelePhone, DisplayPhotos } from "@upyog/digit-ui-react-components";
import CHBWFReason from "./CHBWFReason";


/**
 * CHBWFCaption Component
 * 
 * This component is responsible for rendering the caption details for each checkpoint in the workflow timeline of the CHB module.
 * It displays information such as the date, name, mobile number, source, comments, and attachments for a specific checkpoint.
 * 
 * Props:
 * - `data`: An object containing the details for the caption, including:
 *    - `date`: The date of the checkpoint.
 *    - `name`: The name of the assignee or checkpoint owner.
 *    - `mobileNumber`: The mobile number of the assignee.
 *    - `source`: The source/channel of the application (e.g., online, counter).
 *    - `comment`: The main comment associated with the checkpoint.
 *    - `otherComment`: Additional comments related to the checkpoint.
 *    - `wfComment`: Workflow-specific comments, displayed as a list.
 *    - `thumbnailsToShow`: Object containing thumbnail images and full-size images for attachments.
 * - `OpenImage`: A function to open a full-size image in a new tab when a thumbnail is clicked.
 * 
 * Hooks:
 * - `useTranslation`: Provides the `t` function for internationalization.
 * 
 * Logic:
 * - Displays the date, name, and mobile number if available.
 * - Displays the source/channel of the application using translated text.
 * - Renders the main comment and additional comments using the `CHBWFReason` component.
 * - Displays workflow-specific comments as a list if available.
 * - Renders thumbnails of attachments and allows users to open full-size images by clicking on them.
 * 
 * Returns:
 * - A `div` containing the caption details for the checkpoint, including comments and attachments.
 */
const CHBWFCaption = ({ data,OpenImage }) => {
  const { t } = useTranslation();
  return (
    <div>
      {data.date && <p>{data.date}</p>}
      <p>{data.name}</p>
      {data.mobileNumber && <TelePhone mobile={data.mobileNumber} />}
      {data.source && <p>{t("ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_" + data.source.toUpperCase())}</p>}
      {data.comment && <CHBWFReason otherComment={data?.otherComment} headComment={data?.comment}></CHBWFReason>}
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

export default CHBWFCaption;
