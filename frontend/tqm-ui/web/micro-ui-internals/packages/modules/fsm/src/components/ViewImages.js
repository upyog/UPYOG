import React, { useCallback, useEffect, useState } from "react";
import { DocumentRect } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";

export const ViewImages = (props) => {
  const { t } = useTranslation();
  const [uploadedImagesThumbs, setUploadedImagesThumbs] = useState(null);
  const [uploadedImagesIds, setUploadedImagesIds] = useState(props.fileStoreIds);

  useEffect(() => {
    setUploadedImagesIds(props.fileStoreIds);
  }, [props.fileStoreIds]);

  useEffect(() => {
    (async () => {
      if (uploadedImagesIds !== null) {
        await submit();
      }
    })();
  }, [uploadedImagesIds]);

  function addImageThumbnails(thumbnailsData) {
    var keys = Object.keys(thumbnailsData.data);
    var index = keys.findIndex((key) => key === "fileStoreIds");
    if (index > -1) {
      keys.splice(index, 1);
    }
    var thumbnails = [];

    const newThumbnails = keys.map((key) => {
      return {
        image: thumbnailsData.data[key].split(",")[2],
        key,
        fullImage: Digit.Utils.getFileUrl(thumbnailsData.data[key]),
      };
    });
    setUploadedImagesThumbs([...thumbnails, ...newThumbnails]);
  }

  const submit = useCallback(async () => {
    if (uploadedImagesIds !== null && uploadedImagesIds.length > 0) {
      const res = await Digit.UploadServices.Filefetch(uploadedImagesIds, props.tenantId);
      addImageThumbnails(res);
    }
  }, [uploadedImagesIds]);

  if (props?.docPreview) {
    return uploadedImagesThumbs?.length > 0 ? (
      <div className="multi-upload-wrap">
        {uploadedImagesThumbs.map((doc, index) => (
          <React.Fragment key={index}>
            <a target="_" href={doc.fullImage} style={{ minWidth: "80px", marginRight: "10px", maxWidth: "100px", height: "auto" }} key={index}>
              <DocumentRect />
              {props?.title ? <p className="tqm-document-title">{props?.title}</p> : null}
            </a>
          </React.Fragment>
        ))}
      </div>
    ) : null;
  }
  return (
    <React.Fragment>
      <div className="multi-upload-wrap">
        {uploadedImagesThumbs?.map((thumbnail, index) => {
          return (
            <div key={index}>
              <img src={thumbnail.image} alt="uploaded thumbnail" onClick={() => props.onClick(thumbnail.fullImage, index)} />
            </div>
          );
        })}
      </div>
    </React.Fragment>
  );
};
