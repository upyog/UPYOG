import React from "react";
import { useTranslation } from "react-i18next";
import { TelePhone, DisplayPhotos } from "@nudmcdgnpm/digit-ui-react-components";

 /*The component receives two props: 
    -`data` which contains all the information to be displayed (name, phone number, comments, etc.)
    -`OpenImage` which is a function to handle the opening of full-size images when thumbnails are clicked.
  */
const WFCaption = ({ data,OpenImage }) => {
  const { t } = useTranslation();
  return (
    <div>
      {data.date && <p>{data.date}</p>}
      <p>{data.name}</p>
      {data.mobileNumber && <TelePhone mobile={data.mobileNumber} />}
      {data?.wfComment ? <div>{data?.wfComment?.map( e => 
      <div className="TLComments">
        <h3>{t("WF_COMMON_COMMENTS")}</h3>
        <p style={{overflowX:"scroll"}}>{e}</p>
      </div>
      )}</div> : null}
      {data?.thumbnailsToShow?.thumbs?.length > 0 ? <div className="TLComments">
      <h3>{t("CS_COMMON_ATTACHMENTS")}</h3>
      <DisplayPhotos srcs={data?.thumbnailsToShow.thumbs} onClick={() => {OpenImage(data?.thumbnailsToShow)}} />
    </div> : null}
    </div>
  );
};

export default WFCaption;
