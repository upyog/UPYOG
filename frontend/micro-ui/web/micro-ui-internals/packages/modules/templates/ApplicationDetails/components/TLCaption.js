import React,{useState} from "react";
import { useTranslation } from "react-i18next";
import { TelePhone, DisplayPhotos, UnMaskComponent } from "@upyog/digit-ui-react-components";
import Reason from "./Reason";

const TLCaption = ({ data,OpenImage,privacy={}}) => {
  
  const [viewMore, setviewMore] = useState(false);
  const { t } = useTranslation();
  return (
    <div>
      {data.date && <p>{data.date}</p>}
      <p>{data.name}</p>
      {data.mobileNumber && <span style={{ display: "inline-flex", width: "fit-content", marginLeft: "10px" }}>
        <TelePhone mobile={data.mobileNumber}  privacy={privacy} />
        <p>&nbsp;&nbsp;&nbsp;&nbsp;</p>
      </span>}
      {data.source && <p>{t("ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_" + data.source.toUpperCase())}</p>}
      {data.comment && <Reason otherComment={data?.otherComment} headComment={data?.comment}></Reason>}
      {data?.wfComment ? <div>{data?.wfComment?.map( e => 
      <div className="TLComments">
        <h3>{t("WF_COMMON_COMMENTS")}</h3>        
        {!viewMore && (
        <div>
         <p style={{overflow:"hidden", textOverflow:"ellipsis",display:"-webkit-box", WebkitLineClamp:1, WebkitBoxOrient:"vertical"}}>{e}</p>
          {e.length>36?(<button
            type="button"
            onClick={() => {
              setviewMore(true);
            }}
            style={{color:"#800000"}}
          >
            {t("View More")}
          </button>):null}
        </div>
      )}
      {viewMore && (
        <div >
          <p style={{overflow:"visible"}}>{e}</p>
          <button
            type="button"
            onClick={() => {
              setviewMore(false);
            }}
            style={{color:"#800000"}}
          >
            {t("View Less")}
          </button>
        </div>
      )}

      </div>
      )}</div> : null}
      {data?.thumbnailsToShow?.thumbs?.length > 0 ? <div className="TLComments">
      <h3>{t("CS_COMMON_ATTACHMENTS")}</h3>
      <DisplayPhotos srcs={data?.thumbnailsToShow.thumbs} onClick={(src, index) => {OpenImage(src, index,data?.thumbnailsToShow)}} />
    </div> : null}
    </div>
  );
};

export default TLCaption;
