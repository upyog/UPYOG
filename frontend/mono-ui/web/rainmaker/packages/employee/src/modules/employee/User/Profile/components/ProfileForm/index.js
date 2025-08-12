import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button, TextField,SelectField } from "components";
import Label from "egov-ui-kit/utils/translationNode";
import { ProfileSection } from "modules/common";
import { httpRequest } from "../../../../../../ui-utils/api.js";
import "./index.css";
const ProfileForm = ({ form, handleFieldChange, onClickAddPic, img, profilePic, localProfilePreview, userInfo,
}) => {
  const [profilePhotoUrl, setProfilePhotoUrl] = useState(null);
  useEffect(() => {
    const fetchPhoto = async () => {
      try {
        const tenantId = userInfo.tenantId;
        const uuid = userInfo.uuid;
        if (!uuid || !tenantId) return;

        const userPayload = await httpRequest(
          "post",
          "/user/_search",
          "_search",
          [],
          {
            tenantId,
            uuid: [uuid],
          }
        );

        const userObj = userPayload.user && userPayload.user[0];        
        if (userObj.gender) {
          handleFieldChange("gender", userObj.gender);
        }

        const photoId = userPayload.user[0].photo;
        if (!photoId) return;

        const fileResponse = await httpRequest(
          "get",
          "/filestore/v1/files/url",
          "",
          [
            { key: "tenantId", value: "pg" },
            { key: "fileStoreIds", value: photoId },
          ]
        );

        const url = fileResponse.fileStoreIds[0].url;
        if (url) {
          setProfilePhotoUrl(url);
        }
      } catch (error) {
        console.error("Error fetching user photo:", error);
      }
    };

    fetchPhoto();
  }, [userInfo]);

  const fields = form.fields || {};
  console.log("filedss",fields)
  const submit = form.submit;
  return (
    <div>
      <div className="profile-card-container">
        <div>
          <div style={{ padding: 0 }} className="col-xs-12 col-sm-4 col-md-4 col-lg-4 profile-profilesection">
            <ProfileSection img={localProfilePreview || profilePhotoUrl || profilePic || img} onClickAddPic={onClickAddPic} />
          </div>
          <div style={{ padding: "0 8px" }} className="col-xs-12 col-sm-8 col-md-8 col-lg-8 profileFormContainer">
            <TextField {...fields.name} onChange={(e, value) => handleFieldChange("name", value)} />
            <SelectField
              {...(fields.gender || {})}
              dropDownData={(fields.gender && fields.gender.dropDownData) || []}
              onChange={(e, index, selectedValue) => handleFieldChange("gender", selectedValue)}
            />
            <TextField {...fields.phonenumber} />
            <TextField {...fields.email} onChange={(e, value) => handleFieldChange("email", value)} />
            <Link to="/user/change-password">
              <div style={{ marginTop: "24px", marginBottom: "24px" }}>
                <Label className="change-password-label-style" label={"CORE_COMMON_CHANGE_PASSWORD"} color="#f89a3f" />
              </div>
            </Link>
          </div>
        </div>
      </div>

      <div className="responsive-action-button-cont">
        <Button className="responsive-action-button" {...submit} primary={true} fullWidth={true} />
      </div>
    </div>
  );
};

export default ProfileForm;



