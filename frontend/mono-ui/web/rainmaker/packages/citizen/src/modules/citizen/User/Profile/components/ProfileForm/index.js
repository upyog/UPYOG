import React, { useEffect, useState } from "react";
import Field from "egov-ui-kit/utils/field";
import { Button } from "components";
import { CityPicker } from "modules/common";
import { ProfileSection } from "modules/common";
import { httpRequest } from "../../../../../../ui-utils/api.js";

const ProfileForm = ({
  form,
  handleFieldChange,
  onClickAddPic,
  img,
  profilePic,
  localProfilePreview,
  userInfo,
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

        const photoId = userPayload.user[0].photo;
        if (!photoId) return;

        const fileResponse = await httpRequest(
          "get",
          "/filestore/v1/files/url",
          "",
          [
            { key: "tenantId", value: tenantId },
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
  const submit = form.submit;

  return (
    <div>
      <div className="profile-card-container">
        <div>
          <div style={{ padding: 0 }} className="col-xs-12 col-sm-4 col-md-4 col-lg-4 profile-profilesection">
            <ProfileSection img={localProfilePreview || profilePhotoUrl || profilePic || img} onClickAddPic={onClickAddPic} />
          </div>
          <div className="col-xs-12 col-sm-8 col-md-8 col-lg-8 profileFormContainer">
            <Field fieldKey="name" field={fields.name} handleFieldChange={handleFieldChange} />
            <CityPicker onChange={handleFieldChange} fieldKey="city" field={fields.city} />
            <Field fieldKey="email" field={fields.email} handleFieldChange={handleFieldChange} />
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
