import React, { useState, useEffect } from "react";
import { GalleryIcon, RemoveIcon } from "@upyog/workbench-ui-react-components";
import { useTranslation } from "react-i18next";

function UploadDrawer({ setProfilePic, closeDrawer, userType, removeProfilePic, showToast }) {
  const [file, setFile] = useState("");
  const { t } = useTranslation();
  const selectfile = (e) => setFile(e.target.files[0]);
  const removeimg = () => { removeProfilePic(); closeDrawer(); };
  const onOverlayBodyClick = () => closeDrawer();

  useEffect(() => {
    (async () => {
      if (file) {
        if (file.size >= 1000000) {
          showToast("error", t("CORE_COMMON_PROFILE_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            const response = await Digit.UploadServices.Filestorage(`${userType}-profile`, file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setProfilePic(response.data.files[0].fileStoreId);
            } else {
              showToast("error", t("CORE_COMMON_PROFILE_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            showToast("error", t("CORE_COMMON_PROFILE_INVALID_FILE_INPUT"));
          }
        }
      }
    })();
  }, [file]);

  return (
    <React.Fragment>
      <div
        style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, width: "100%", height: "100vh", backgroundColor: "rgba(0,0,0,.5)" }}
        onClick={onOverlayBodyClick}
      />
      <div
        style={{
          width: "100%", justifyContent: "space-between", display: "flex", backgroundColor: "white",
          alignItems: "center", position: "fixed", left: 0, right: 0, height: "20%", bottom: 0, zIndex: 1000,
        }}
      >
        <div style={{ display: "flex", flex: 1, flexDirection: "column", width: "100%", justifyContent: "center", alignItems: "center", gap: "8px 0" }}>
          <label htmlFor="file" style={{ cursor: "pointer" }}><GalleryIcon /></label>
          <label style={{ cursor: "pointer" }}>Gallery</label>
          <input type="file" id="file" accept="image/*, .png, .jpeg, .jpg" onChange={selectfile} style={{ display: "none" }} />
        </div>
        <div style={{ display: "flex", flex: 1, width: "100%", justifyContent: "center", alignItems: "center", flexDirection: "column", gap: "8px 0" }}>
          <button onClick={removeimg}><RemoveIcon /></button>
          <label style={{ cursor: "pointer" }}>Remove</label>
        </div>
      </div>
    </React.Fragment>
  );
}

export default UploadDrawer;
