import React from "react";
import { useTranslation } from "react-i18next";
import { TelePhone, DisplayPhotos } from "@upyog/digit-ui-react-components";

/**
 * Caption Component
 * 
 * This component is responsible for displaying detailed information about an application, 
 * including the application date, name, mobile number, source, comments, and any attached 
 * images (thumbnails). It utilizes the `useTranslation` hook for internationalization, 
 * allowing for dynamic translation of certain text based on the provided language.
 * 
 * Props:
 * - data: An object containing the details to be displayed, including:
 *   - date: The date of the application
 *   - name: The name associated with the application
 *   - mobileNumber: The mobile number, which is displayed using the TelePhone component
 *   - source: The channel/source of the application, which is translated using the `t` function
 *   - comment: The main comment related to the application
 *   - otherComment: Any additional comments related to the application
 *   - wfComment: An array of workflow comments to be displayed
 *   - thumbnailsToShow: An object containing an array of thumbnail images to display
 * 
 * - OpenImage: A function that is called when a thumbnail is clicked, allowing 
 *   for the display of the full image.
 * 
 * The component conditionally renders elements based on the presence of data fields, 
 * ensuring that only relevant information is displayed to the user.
 */

const Caption = ({ data,OpenImage }) => {
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

export default Caption;
