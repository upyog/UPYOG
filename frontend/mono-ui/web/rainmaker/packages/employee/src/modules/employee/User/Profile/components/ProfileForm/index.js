import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Button, TextField } from "components";
import Label from "egov-ui-kit/utils/translationNode";
import { ProfileSection } from "modules/common";
import "./index.css";
import { httpRequestNew } from "egov-ui-kit/utils/api";

const ProfileForm = ({ form, handleFieldChange, onClickAddPic, img, profilePic }) => {
  const fields = form.fields || {};
  const submit = form.submit;
  const [photoUrl, setPhotoUrl] = useState("");

  useEffect(() => {
    const fetchUser = async () => {
      const userInfo = JSON.parse(localStorage.getItem("user-info"))
      try {
        const payload = await httpRequestNew(
          "post",
          `/user/_search?tenantId=${userInfo.tenantId}`,
          "_search",
          {
            tenantId: userInfo.tenantId,
            uuid: [userInfo.uuid],
          }
        );

        if (payload.user.length === 0) {

        } else if (payload.user[0].photo) {
          setPhotoUrl(payload.user[0].photo);
        }

      } catch (error) {
        console.error("Error fetching user:", error);
      }
    };

    fetchUser();

  }, []);
  
  console.log("submitsubmit",submit)
  return (
    <div>
      <div className="profile-card-container">
        <div>
          <div style={{ padding: 0 }} className="col-xs-12 col-sm-4 col-md-4 col-lg-4 profile-profilesection">
            <ProfileSection
              img={
                form &&
                  form.files &&
                  Array.isArray(form.files.photo) &&
                  form.files.photo.length > 0
                  ? form.files.photo[0].imageUri != undefined ? form.files.photo[0].imageUri : photoUrl
                  : photoUrl
              }
              onClickAddPic={onClickAddPic}
            />
          </div>
          <div style={{ padding: "0 8px" }} className="col-xs-12 col-sm-8 col-md-8 col-lg-8 profileFormContainer">
            <TextField {...fields.name} onChange={(e, value) => handleFieldChange("name", value)} />
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
      <Button
  className="responsive-action-button"
  {...submit}
  onClick={(e) => {
    if (submit && typeof submit.onClick === "function") {
      submit.onClick(e); // ✅ call the reload logic passed from parent
    }
  }}
  primary={true}
  fullWidth={true}
/>

      </div>
    </div>
  );
};

export default ProfileForm;