import React, { useState, useEffect } from "react";
import { GalleryIcon, RemoveIcon, UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

function UploadDrawer({ setProfilePic, closeDrawer, userType, removeProfilePic ,showToast}) {
  const [uploadedFile, setUploadedFile] = useState(null);
  const [file, setFile] = useState("");
  const [error, setError] = useState(null);
  const { t } = useTranslation();
  const selectfile = (e) => setFile(e.target.files[0]);
  const removeimg = () => {removeProfilePic(); closeDrawer()};
  const onOverlayBodyClick = () => closeDrawer();

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 1000000) {
          showToast("error", t("CORE_COMMON_PROFILE_MAXIMUM_UPLOAD_SIZE_EXCEEDED"))
          setError(t("CORE_COMMON_PROFILE_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage(`${userType}-profile`, file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              const fileStoreId = response?.data?.files[0]?.fileStoreId;
              setUploadedFile(fileStoreId);
              setProfilePic(fileStoreId);
            } else {
              showToast("error", t("CORE_COMMON_PROFILE_FILE_UPLOAD_ERROR"))
              setError(t("CORE_COMMON_PROFILE_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            showToast("error",t("CORE_COMMON_PROFILE_INVALID_FILE_INPUT"))
            // setError(t("PT_FILE_UPLOAD_ERROR"));
          }
        }
      }
    })();
  }, [file]);

  return (
    <React.Fragment>
      <div
        className="core-upload-drawer-fullwidth"
        onClick={onOverlayBodyClick}
      ></div>
      <div className={`upload-drawer-bottom-bar ${userType === "citizen" ? "bottom-citizen" : "bottom-employee"}`}>
        <div
          className="core-upload-drawer-fullwidth-2"
        >
          <label for="file" className="core-upload-drawer-clickable">
            {" "}
            <GalleryIcon />
          </label>
          <label className="core-upload-drawer-clickable"> Gallery</label>
          <input type="file" id="file" accept="image/*, .png, .jpeg, .jpg" onChange={selectfile} className="core-upload-drawer-hidden" />
        </div>

        <div
          className="core-upload-drawer-fullwidth-3"
        >
          <button onClick={removeimg}>
            <RemoveIcon />
          </button>
          <label className="core-upload-drawer-clickable">Remove</label>
        </div>
      </div>
    </React.Fragment>
  );
}

export default UploadDrawer;
