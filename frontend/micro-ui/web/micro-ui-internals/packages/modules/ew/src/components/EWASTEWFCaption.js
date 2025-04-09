// Importing necessary components and hooks from external libraries and local files
import React from "react";
import { useTranslation } from "react-i18next"; // Hook for translations
import { TelePhone, DisplayPhotos } from "@nudmcdgnpm/digit-ui-react-components"; // Components for displaying telephone numbers and photos
import EWASTEWFReason from "./EWASTEWFReason"; // Component for rendering workflow reasons

// Component to render captions for workflow checkpoints in the E-Waste module
const EWASTEWFCaption = ({ data, OpenImage }) => {
  const { t } = useTranslation(); // Translation hook

  return (
    <div>
      {/* Display the date if available */}
      {data.date && <p>{data.date}</p>}

      {/* Display the name if available */}
      <p>{data.name}</p>

      {/* Display the mobile number as a clickable telephone link if available */}
      {data.mobileNumber && <TelePhone mobile={data.mobileNumber} />}

      {/* Display the application source if available */}
      {data.source && <p>{t("ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_" + data.source.toUpperCase())}</p>}

      {/* Display the workflow comment if available */}
      {data.comment && <EWASTEWFReason otherComment={data?.otherComment} headComment={data?.comment}></EWASTEWFReason>}

      {/* Display additional workflow comments if available */}
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

      {/* Display thumbnails if available */}
      {data?.thumbnailsToShow?.thumbs?.length > 0 ? (
        <div className="TLComments">
          <h3>{t("CS_COMMON_ATTACHMENTS")}</h3>
          <DisplayPhotos
            srcs={data?.thumbnailsToShow.thumbs} // Array of thumbnail sources
            onClick={(src, index) => {
              OpenImage(src, index, data?.thumbnailsToShow); // Open the image in a new tab when clicked
            }}
          />
        </div>
      ) : null}
    </div>
  );
};

export default EWASTEWFCaption; // Exporting the component