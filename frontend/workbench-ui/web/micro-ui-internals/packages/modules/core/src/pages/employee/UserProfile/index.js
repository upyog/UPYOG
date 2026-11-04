import {
  CameraIcon,
  CardLabel,
  Dropdown,
  LabelFieldPair,
  MobileNumber,
  TextInput,
  Toast,
  CardLabelError,
  BreadCrumb,
  Loader,
  SubmitBar,
} from "@upyog/workbench-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import UploadDrawer from "./UploadDrawer";

const defaultImage =
  "data:image/png;base64,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";

const UserProfile = ({ stateCode, cityDetails }) => {
  const { t } = useTranslation();
  const url = window.location.href;
  const stateId = Digit.ULBService.getStateId();
  const tenant = Digit.ULBService.getCurrentTenantId();
  const userInfo = Digit.UserService.getUser()?.info || {};
  const [userDetails, setUserDetails] = useState(null);
  const [name, setName] = useState(userInfo?.name || "");
  const [email, setEmail] = useState(userInfo?.emailId || "");
  const [gender, setGender] = useState(null);
  const [city, setCity] = useState(userInfo?.permanentCity || cityDetails?.name);
  const [mobileNumber, setMobileNo] = useState(userInfo?.mobileNumber || "");
  const [profilePic, setProfilePic] = useState(null);
  const [profileImg, setProfileImg] = useState("");
  const [openUploadSlide, setOpenUploadSide] = useState(false);
  const [changepassword, setChangepassword] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [toast, setToast] = useState(null);
  const [loading, setLoading] = useState(false);
  const [windowWidth, setWindowWidth] = React.useState(window.innerWidth);
  const [errors, setErrors] = React.useState({});

  const getUserInfo = async () => {
    const uuid = userInfo?.uuid;
    if (uuid) {
      const usersResponse = await Digit.UserService.userSearch(tenant, { uuid: [uuid] }, {});
      usersResponse?.user?.length && setUserDetails(usersResponse.user[0]);
    }
  };

  React.useEffect(() => {
    const handler = () => setWindowWidth(window.innerWidth);
    window.addEventListener("resize", handler);
    return () => window.removeEventListener("resize", handler);
  });

  useEffect(() => {
    setLoading(true);
    getUserInfo();
    setGender({ i18nKey: undefined, code: userDetails?.gender, value: userDetails?.gender });
    const thumbs = userDetails?.photo?.split(",");
    setProfileImg(thumbs?.at(0));
    setLoading(false);
  }, [userDetails !== null]);

  const editScreen = false;
  const onClickAddPic = () => setOpenUploadSide(!openUploadSlide);
  const TogleforPassword = () => setChangepassword(!changepassword);
  const setGenderName = (value) => setGender(value);
  const closeFileUploadDrawer = () => setOpenUploadSide(false);

  const setUserName = (value) => {
    setName(value);
    if (!new RegExp(/^[a-zA-Z ]+$/i).test(value) || value.length === 0 || value.length > 50) {
      setErrors({ ...errors, userName: { type: "pattern", message: "CORE_COMMON_PROFILE_NAME_INVALID" } });
    } else {
      setErrors({ ...errors, userName: null });
    }
  };

  const setUserEmailAddress = (value) => {
    setEmail(value);
    if (value.length && !(value.includes("@") && value.includes("."))) {
      setErrors({ ...errors, emailAddress: { type: "pattern", message: "CORE_COMMON_PROFILE_EMAIL_INVALID" } });
    } else {
      setErrors({ ...errors, emailAddress: null });
    }
  };

  const setUserMobileNumber = (value) => {
    setMobileNo(value);
    if (!new RegExp(/^[6-9]{1}[0-9]{9}$/).test(value)) {
      setErrors({ ...errors, mobileNumber: { type: "pattern", message: "CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID" } });
    } else {
      setErrors({ ...errors, mobileNumber: null });
    }
  };

  const setUserCurrentPassword = (value) => {
    setCurrentPassword(value);
    if (!new RegExp(/^([a-zA-Z0-9@#$%]{8,15})$/i).test(value)) {
      setErrors({ ...errors, currentPassword: { type: "pattern", message: "CORE_COMMON_PROFILE_PASSWORD_INVALID" } });
    } else {
      setErrors({ ...errors, currentPassword: null });
    }
  };

  const setUserNewPassword = (value) => {
    setNewPassword(value);
    if (!new RegExp(/^([a-zA-Z0-9@#$%]{8,15})$/i).test(value)) {
      setErrors({ ...errors, newPassword: { type: "pattern", message: "CORE_COMMON_PROFILE_PASSWORD_INVALID" } });
    } else {
      setErrors({ ...errors, newPassword: null });
    }
  };

  const setUserConfirmPassword = (value) => {
    setConfirmPassword(value);
    if (!new RegExp(/^([a-zA-Z0-9@#$%]{8,15})$/i).test(value)) {
      setErrors({ ...errors, confirmPassword: { type: "pattern", message: "CORE_COMMON_PROFILE_PASSWORD_INVALID" } });
    } else {
      setErrors({ ...errors, confirmPassword: null });
    }
  };

  const removeProfilePic = () => { setProfilePic(null); setProfileImg(null); };

  const showToast = (type, message, duration = 5000) => {
    setToast({ key: type, action: message });
    setTimeout(() => setToast(null), duration);
  };

  const updateProfile = async () => {
    setLoading(true);
    try {
      if (!new RegExp(/^([a-zA-Z ])*$/).test(name) || name === "" || name.length > 50) {
        throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_NAME_INVALID") });
      }
      if (!new RegExp(/^[6-9]{1}[0-9]{9}$/).test(mobileNumber)) {
        throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID") });
      }
      if (email.length && !(email.includes("@") && email.includes("."))) {
        throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_EMAIL_INVALID") });
      }

      const requestData = { ...userInfo, name, gender: gender?.value, emailId: email, photo: profilePic };
      const { responseInfo } = await Digit.UserService.updateUser(requestData, stateCode);

      if (responseInfo?.status === "200") {
        const user = Digit.UserService.getUser();
        if (user) {
          Digit.UserService.setUser({ ...user, info: { ...user.info, name, mobileNumber, emailId: email, permanentCity: city } });
        }
      }

      if (changepassword && currentPassword.length && newPassword.length && confirmPassword.length) {
        if (newPassword !== confirmPassword) {
          throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_PASSWORD_MISMATCH") });
        }
        const pwdRequest = { existingPassword: currentPassword, newPassword, tenantId: tenant, type: "EMPLOYEE", username: userInfo?.userName, confirmPassword };
        const res = await Digit.UserService.changePassword(pwdRequest, tenant);
        if (res?.responseInfo?.status === "200") {
          showToast("success", t("CORE_COMMON_PROFILE_UPDATE_SUCCESS_WITH_PASSWORD"), 5000);
          setTimeout(() => Digit.UserService.logout(), 2000);
        }
      } else if (responseInfo?.status === "200") {
        showToast("success", t("CORE_COMMON_PROFILE_UPDATE_SUCCESS"), 5000);
      }
    } catch (error) {
      const errorObj = JSON.parse(error);
      showToast(errorObj.type, t(errorObj.message), 5000);
    }
    setLoading(false);
  };

  let menu = [];
  const { data: Menu } = Digit.Hooks.useGenderMDMS(stateId, "common-masters", "GenderType");
  Menu?.map((g) => menu.push({ i18nKey: `PT_COMMON_GENDER_${g.code}`, code: g.code, value: g.code }));

  const setFileStoreId = async (fileStoreId) => {
    setProfilePic(fileStoreId);
    const res = fileStoreId ? await Digit.UploadServices.Filefetch([fileStoreId], stateId) : null;
    if (res?.data?.fileStoreIds?.length) setProfileImg(res.data.fileStoreIds[0].url.split(",")[3]);
    closeFileUploadDrawer();
  };

  if (loading) return <Loader />;

  return (
    <div className="user-profile">
      <section style={{ margin: "24px" }}>
        <BreadCrumb
          crumbs={[
            { path: `/${window?.contextPath}/employee`, content: t("ES_COMMON_HOME"), show: true },
            { path: `/${window?.contextPath}/employee/user/profile`, content: t("ES_COMMON_PAGE_1"), show: url.includes("/user/profile") },
          ]}
        />
      </section>
      <div style={{ display: "flex", flex: 1, flexDirection: windowWidth < 768 ? "column" : "row", margin: "16px", gap: "0 24px" }}>
        <section
          style={{
            position: "relative", display: "flex", flex: 2.5, justifyContent: "center", alignItems: "center",
            maxWidth: "100%", height: "376px", borderRadius: "4px", boxShadow: "1px 1px 4px 0px rgba(0,0,0,0.2)",
            border: "24px solid #fff", background: "#EEEEEE", padding: "16px",
          }}
        >
          <div style={{ position: "relative", height: "150px", width: "150px", margin: "16px" }}>
            <img
              style={{ margin: "auto", borderRadius: "300px", justifyContent: "center", height: "100%", width: "100%" }}
              src={!profileImg || profileImg === "" ? defaultImage : profileImg}
            />
            <button style={{ position: "absolute", left: "50%", bottom: "-24px", transform: "translateX(-50%)" }} onClick={onClickAddPic}>
              <CameraIcon />
            </button>
          </div>
        </section>
        <section
          style={{
            display: "flex", flexDirection: "column", flex: 7.5, width: "100%", borderRadius: "4px",
            height: "fit-content", boxShadow: "1px 1px 4px 0px rgba(0,0,0,0.2)", background: "white", padding: "24px", paddingBottom: "20px",
          }}
        >
          <LabelFieldPair style={{ display: "flex" }}>
            <CardLabel className="profile-label-margin" style={{ width: "300px" }}>{`${t("CORE_COMMON_PROFILE_NAME")}`}*</CardLabel>
            <div style={{ width: "100%" }}>
              <TextInput t={t} type="text" isMandatory={false} name="name" value={name} onChange={(e) => setUserName(e.target.value)} placeholder="Enter Your Name" disable={editScreen} />
              {errors?.userName && <CardLabelError style={{ margin: 0, padding: 0 }}>{t(errors.userName.message)}</CardLabelError>}
            </div>
          </LabelFieldPair>

          <LabelFieldPair style={{ display: "flex" }}>
            <CardLabel className="profile-label-margin" style={{ width: "300px" }}>{`${t("CORE_COMMON_PROFILE_GENDER")}`}</CardLabel>
            <Dropdown style={{ width: "100%" }} selected={gender?.length === 1 ? gender[0] : gender} disable={gender?.length === 1 || editScreen} option={menu} select={setGenderName} value={gender} optionKey="code" t={t} name="gender" />
          </LabelFieldPair>

          <LabelFieldPair style={{ display: "flex" }}>
            <CardLabel className="profile-label-margin" style={{ width: "300px" }}>{`${t("CORE_COMMON_PROFILE_CITY")}`}</CardLabel>
            <TextInput t={t} type="text" isMandatory={false} name="city" value={t(Digit.Utils.locale.getTransformedLocale(`TENANT_TENANTS_${tenant}`))} onChange={(e) => setCity(e.target.value)} disable={true} />
          </LabelFieldPair>

          <LabelFieldPair style={{ display: "flex" }}>
            <CardLabel className="profile-label-margin" style={{ width: "300px" }}>{`${t("CORE_COMMON_PROFILE_MOBILE_NUMBER")}*`}</CardLabel>
            <div style={{ width: "100%" }}>
              <MobileNumber value={mobileNumber} style={{ width: "100%" }} name="mobileNumber" onChange={(value) => setUserMobileNumber(value)} disable={true} />
              {errors?.mobileNumber && <CardLabelError style={{ margin: 0, padding: 0 }}>{t(errors.mobileNumber.message)}</CardLabelError>}
            </div>
          </LabelFieldPair>

          <LabelFieldPair style={{ display: "flex" }}>
            <CardLabel className="profile-label-margin" style={{ width: "300px" }}>{`${t("CORE_COMMON_PROFILE_EMAIL")}`}</CardLabel>
            <div style={{ width: "100%" }}>
              <TextInput t={t} type="email" isMandatory={false} name="email" value={email} onChange={(e) => setUserEmailAddress(e.target.value)} disable={editScreen} />
              {errors?.emailAddress && <CardLabelError>{t(errors.emailAddress.message)}</CardLabelError>}
            </div>
          </LabelFieldPair>

          <LabelFieldPair>
            <div>
              <a style={{ color: "orange", cursor: "pointer" }} onClick={TogleforPassword}>{t("CORE_COMMON_CHANGE_PASSWORD")}</a>
              {changepassword && (
                <div style={{ marginTop: "10px" }}>
                  {[
                    { label: "CORE_COMMON_PROFILE_CURRENT_PASSWORD", setter: setUserCurrentPassword, errKey: "currentPassword", val: currentPassword },
                    { label: "CORE_COMMON_PROFILE_NEW_PASSWORD", setter: setUserNewPassword, errKey: "newPassword", val: newPassword },
                    { label: "CORE_COMMON_PROFILE_CONFIRM_PASSWORD", setter: setUserConfirmPassword, errKey: "confirmPassword", val: confirmPassword },
                  ].map(({ label, setter, errKey }) => (
                    <LabelFieldPair key={label} style={{ display: "flex" }}>
                      <CardLabel className="profile-label-margin" style={{ width: "300px" }}>{t(label)}</CardLabel>
                      <div style={{ width: "100%" }}>
                        <TextInput t={t} type="password" isMandatory={false} name={errKey} onChange={(e) => setter(e.target.value)} />
                        {errors?.[errKey] && <CardLabelError>{t(errors[errKey].message)}</CardLabelError>}
                      </div>
                    </LabelFieldPair>
                  ))}
                </div>
              )}
            </div>
          </LabelFieldPair>
        </section>
      </div>

      <div className="action-bar-wrap">
        <SubmitBar t={t} label={t("CORE_COMMON_SAVE")} onSubmit={updateProfile} />
      </div>

      {toast && (
        <Toast error={toast.key === "error"} label={t(toast.key === "success" ? "CORE_COMMON_PROFILE_UPDATE_SUCCESS" : toast.action)} onClose={() => setToast(null)} style={{ maxWidth: "670px" }} />
      )}

      {openUploadSlide && (
        <UploadDrawer setProfilePic={setFileStoreId} closeDrawer={closeFileUploadDrawer} userType="employee" removeProfilePic={removeProfilePic} showToast={showToast} />
      )}
    </div>
  );
};

export default UserProfile;
