import React from "react";
import { useTranslation } from "react-i18next";
import { TelePhone, DisplayPhotos } from "@upyog/digit-ui-react-components";
import EWASTEWFReason from "./EWASTEWFReason";

/**
 * Renders captions and details for workflow checkpoints in the E-Waste module.
 * Displays various information including dates, contact details, comments, and attachments.
 *
 * @param {Object} props - Component properties
 * @param {Object} props.data - Workflow data containing checkpoint details
 * @param {string} props.data.date - Timestamp of the workflow checkpoint
 * @param {string} props.data.name - Name associated with the checkpoint
 * @param {string} props.data.mobileNumber - Contact number for the checkpoint
 * @param {string} props.data.source - Source of the application
 * @param {string} props.data.comment - Primary comment for the workflow
 * @param {string} props.data.otherComment - Additional comments
 * @param {Array} props.data.wfComment - Array of workflow-specific comments
 * @param {Object} props.data.thumbnailsToShow - Object containing thumbnail images
 * @param {Array} props.data.thumbnailsToShow.thumbs - Array of thumbnail URLs
 * @param {Function} props.OpenImage - Handler for opening full-size images
 * @returns {JSX.Element} Workflow caption component
 */
const EWASTEWFCaption = ({ data, OpenImage }) => {
  const { t } = useTranslation();

  return (
    <div>
      {data.date && <p>{data.date}</p>}

      <p>{data.name}</p>

      {data.mobileNumber && <TelePhone mobile={data.mobileNumber} />}

      {data.source && <p>{t("ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_" + data.source.toUpperCase())}</p>}

      {data.comment && <EWASTEWFReason otherComment={data?.otherComment} headComment={data?.comment}></EWASTEWFReason>}

      {data?.wfComment ? (
        <div>
          {data?.wfComment?.map((e, index) => (
            <div className="TLComments" key={index}>
              <h3>{t("WF_COMMON_COMMENTS")}</h3>
              <p style={{ overflowX: "scroll" }}>{e}</p>
            </div>
          ))}
        </div>
      ) : null}

      {data?.thumbnailsToShow?.thumbs?.length > 0 ? (
        <div className="TLComments">
          <h3>{t("CS_COMMON_ATTACHMENTS")}</h3>
          <DisplayPhotos
            srcs={data?.thumbnailsToShow.thumbs}
            onClick={(src, index) => {
              OpenImage(src, index, data?.thumbnailsToShow);
            }}
          />
        </div>
      ) : null}
    </div>
  );
};

export default EWASTEWFCaption;