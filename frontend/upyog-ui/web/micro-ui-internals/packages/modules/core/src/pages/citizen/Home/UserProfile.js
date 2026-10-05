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
  BackButton,
  Loader,
  DatePicker,
  Card,
  StatusTable,
  Row,
  EditIcon,
  LinkButton
} from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

import UploadDrawer from "./ImageUpload/UploadDrawer";
import { subYears, format, differenceInYears } from "date-fns";
import Address from "./AddressDetails";

const defaultImage =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAO4AAADUCAMAAACs0e/bAAAAM1BMVEXK0eL" +
  "/" +
  "/" +
  "/" +
  "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";

const UserProfile = ({ stateCode, userType, cityDetails }) => {
  const navigate = Digit.Hooks.useCustomNavigate();
  const { t } = useTranslation();
  const url = window.location.href;
  const stateId = Digit.ULBService.getStateId();
  const tenant = Digit.ULBService.getCurrentTenantId();
  const userInfo = Digit.UserService.getUser()?.info || {};
  const [userDetails, setUserDetails] = useState(null);
  const [userAddresses, setUserAddresses] = useState([]); // Separate state for addresses
  const [name, setName] = useState(userInfo?.name ? userInfo.name : "");
  const dateOfBirth= userDetails?.dob
  const formattedDob=(dateOfBirth!==undefined) ?format(new Date(dateOfBirth), 'MM/dd/yyyy') : ""
  //const dateOfBirth1= (dateOfBirth!==undefined) ?dateOfBirth.split("-").reverse().join("-") : ""
  const [dob, setDob] = useState(dateOfBirth);
  const [email, setEmail] = useState(userInfo?.emailId ? userInfo.emailId : "");
  const [gender, setGender] = useState(userDetails?.gender);
  const [city, setCity] = useState(userInfo?.permanentCity ? userInfo.permanentCity : cityDetails?.name);
  const [mobileNumber, setMobileNo] = useState(userInfo?.mobileNumber ? userInfo.mobileNumber : "");
  const [altMobileNumber, setAltMobileNo] = useState(userInfo?.altContactNumber ? userInfo.altContactNumber : "");
  const [profilePic, setProfilePic] = useState(userDetails?.photo ? userDetails?.photo : "");
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
  const isMobile = window.Digit.Utils.browser.isMobile();
  const [profilePhoto, setProfilePhoto] = useState(null);
  const [activeTab, setActiveTab] = useState("profile");
  const [showModal, setShowModal] = useState(false);
  const [isEdit, setisEdit] = useState(false);
  const [selectedAddress, setSelectedAddress] = useState(null);
  /*
 * Fetches the user's address details using the `Digit.UserService.userSearchNewV2` API.
 * - Retrieves the user's UUID from `userInfo`.
 * - Calls the API with the tenant ID and UUID to fetch user data.
 * - Updates the `userAddresses` state with the fetched address list if available.
 */
  const userSearchNewV2 = async () => {
    const uuid = userInfo?.uuid;
    if (uuid) {
      const usersResponse = await Digit.UserService.userSearchNewV2(tenant, { uuid: [uuid] }, {});
      if (usersResponse && usersResponse.user && usersResponse.user.length) {
        setUserAddresses(usersResponse.user[0]?.addresses || []); // Set addresses separately
      }
    }
  };

  const getUserInfo = async () => {
    const uuid = userInfo?.uuid;
    if (uuid) {
      const usersResponse = await Digit.UserService.userSearch(tenant, { uuid: [uuid] }, {});
      if (usersResponse?.user?.[0]?.photo) {
        try {
          const file = await Digit.UploadServices.Filefetch([usersResponse?.user?.[0]?.photo], "pg");
          if (file?.data?.fileStoreIds?.[0]?.url) {
            setProfilePhoto(file?.data?.fileStoreIds?.[0]?.url.split(",")[0]);
          }
        } catch (err) {
          console.error("Error fetching profile photo:", err);
        }
      }
      usersResponse && usersResponse.user && usersResponse.user.length && setUserDetails(usersResponse.user[0]);
    }
  };

  React.useEffect(() => {
    window.addEventListener("resize", () => setWindowWidth(window.innerWidth));
    return () => {
      window.removeEventListener("resize", () => setWindowWidth(window.innerWidth));
    };
  });

  useEffect(() => {
    setLoading(true);
    userSearchNewV2();
    getUserInfo();

    setGender({
      i18nKey: undefined,
      code: userDetails?.gender,
      value: userDetails?.gender,
    });

    //const thumbs = userDetails?.photo?.split(",");
    setProfileImg(profilePhoto);

    setLoading(false);
  }, [userDetails !== null]);

  let validation = {};
  const editScreen = false; // To-do: Deubug and make me dynamic or remove if not needed
  const onClickAddPic = () => setOpenUploadSide(!openUploadSlide);
  const TogleforPassword = () => setChangepassword(!changepassword);
  const setGenderName = (value) => setGender(value);

  const setUserDOB =(value)=> {
      setDob(value);
  }
  const closeFileUploadDrawer = () => setOpenUploadSide(false);

  const setUserName = (value) => {
    setName(value);

    if(!new RegExp(/^[a-zA-Z ]+$/i).test(value) || value.length === 0 || value.length > 50){
      setErrors({...errors, userName : {type: "pattern", message: t("CORE_COMMON_PROFILE_NAME_INVALID")}});
    }else{
      setErrors({...errors, userName : null})
    }
  }
  
  const setUserEmailAddress = (value) => {
    setEmail(value);
    const emailPattern=/^[a-zA-Z0-9._%+-]+@[a-z.-]+\.(com|org|in)$/
    if(value.length && !emailPattern.test(value)){
      setErrors({...errors, emailAddress: {type: "pattern", message: t("CORE_COMMON_PROFILE_EMAIL_INVALID")}})
    }else{
      setEmail(value);
      setErrors({ ...errors, emailAddress: null });
    }
  };
  const SetActiveTab = (tab) => {
    setActiveTab(tab);
  };

  const setUserMobileNumber = (value) => {
    setMobileNo(value);

    if (userType === "employee" && !new RegExp(/^[6-9]{1}[0-9]{9}$/).test(value)) {
      setErrors({...errors, mobileNumber: {type: 'pattern', message: t("CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID")}})
    }else{
      setErrors({...errors, mobileNumber: null});
    }
  };

  const setUserAltMobileNumber = (value) => {
    setAltMobileNo(value);

    if (!new RegExp(/^[6-9]{1}[0-9]{9}$/).test(value)) {
      setErrors({ ...errors, altMobileNumber: { type: "pattern", message: t("CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID") } });
    } else {
      setErrors({ ...errors, altMobileNumber: null });
    }
  };

  const setUserCurrentPassword = (value) => {
    setCurrentPassword(value);

    if (!new RegExp(/^([a-zA-Z0-9@#$%]{8,15})$/i).test(value)) {
      setErrors({...errors, currentPassword: {type: "pattern", message: t("CORE_COMMON_PROFILE_PASSWORD_INVALID")}})
    }else{
      setErrors({...errors, currentPassword: null});
    }
  }

  const setUserNewPassword = (value) => {
    setNewPassword(value);

    if (!new RegExp(/^([a-zA-Z0-9@#$%]{8,15})$/i).test(value)) {
      setErrors({...errors, newPassword: {type: "pattern", message: t("CORE_COMMON_PROFILE_PASSWORD_INVALID")}})
    }else{
      setErrors({...errors, newPassword: null});
    }
  }

  const setUserConfirmPassword = (value) => {
    setConfirmPassword(value);

    if (!new RegExp(/^([a-zA-Z0-9@#$%]{8,15})$/i).test(value)) {
      setErrors({...errors, confirmPassword: {type: "pattern", message: t("CORE_COMMON_PROFILE_PASSWORD_INVALID")}})
    }else{
      setErrors({...errors, confirmPassword: null});
    }
  }

  const removeProfilePic = () => {
    setProfilePic(null);
    setProfileImg(null);
  };

  const showToast = (type, message, duration = 5000) => {
    setToast({ key: type, action: message });
    setTimeout(() => {
      setToast(null);
    }, duration);
  };

  const updateProfile = async () => {
    setLoading(true);
    try {
      const requestData = {
        ...userInfo,
        name,
        dob: dob!== undefined ? dob.split("-").reverse().join("/") : "",
        gender: gender?.value,
        emailId: email,
        altContactNumber: altMobileNumber,
        photo: profilePic,
      };

      if (!new RegExp(/^([a-zA-Z ])*$/).test(name) || name === "" || name.length > 50 || name.length < 1) {
        throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_NAME_INVALID") });
      }

      if (userType === "employee" && (!requestData.gender || requestData.gender.trim() === "")) {
        throw JSON.stringify({ type: "error", message: "CORE_COMMON_PROFILE_GENDER_REQUIRED" });
      }

      if (userType === "employee" && !new RegExp(/^[6-9]{1}[0-9]{9}$/).test(mobileNumber)) {
        throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID") });
      }

      if (!new RegExp(/^[6-9]{1}[0-9]{9}$/).test(altMobileNumber)) {
        throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID") });
      }

      if (email.length && !/^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(email)) {
        throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_EMAIL_INVALID") });
      }     

      if (currentPassword.length || newPassword.length || confirmPassword.length) {
        if (newPassword !== confirmPassword) {
          throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_PASSWORD_MISMATCH") });
        }

        if (!(currentPassword.length && newPassword.length && confirmPassword.length)) {
          throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_PASSWORD_INVALID") });
        }

        if (!new RegExp(/^([a-zA-Z0-9@#$%]{8,15})$/i).test(newPassword) && !new RegExp(/^([a-zA-Z0-9@#$%]{8,15})$/i).test(confirmPassword)) {
          throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_PASSWORD_INVALID") });
        }
      }
      requestData["locale"]=Digit.StoreData.getCurrentLanguage();
      const { responseInfo, user } = await Digit.UserService.updateUser(requestData, stateCode);

      if (responseInfo && responseInfo.status === "200") {
        const user = Digit.UserService.getUser();

        if (user) {
          Digit.UserService.setUser({
            ...user,
            info: {
              ...user.info,
              name,
              //DOB,
              mobileNumber,
              altContactNumber: altMobileNumber,
              emailId: email,
              permanentCity: city,
              photo: profileImg
            },
          });
        }
      }

      if (currentPassword.length && newPassword.length && confirmPassword.length) {
        const requestData = {
          existingPassword: currentPassword,
          newPassword: newPassword,
          tenantId: tenant,
          type: "EMPLOYEE",
          username: userInfo?.userName,
          confirmPassword: confirmPassword,
        };

        if (newPassword === confirmPassword) {
          try {
            const res = await Digit.UserService.changePassword(requestData, tenant);

            const { responseInfo: changePasswordResponseInfo } = res;
            if (changePasswordResponseInfo?.status && changePasswordResponseInfo.status === "200") {
              showToast("success", t("CORE_COMMON_PROFILE_UPDATE_SUCCESS_WITH_PASSWORD"), 5000);
            } else {
              throw "";
            }
          } catch (error) {
            throw JSON.stringify({
              type: "error",
              message: error.Errors?.at(0)?.description ? error.Errors.at(0).description : t("CORE_COMMON_PROFILE_UPDATE_ERROR_WITH_PASSWORD"),
            });
          }
        } else {
          throw JSON.stringify({ type: "error", message: t("CORE_COMMON_PROFILE_ERROR_PASSWORD_NOT_MATCH") });
        }
      } else if (responseInfo?.status && responseInfo.status === "200") {
        showToast("success", t("CORE_COMMON_PROFILE_UPDATE_SUCCESS"), 5000);
      }
    } catch (error) {
      const errorObj = JSON.parse(error);
      showToast(errorObj.type, t(errorObj.message), 5000);
    }

    setLoading(false);
  };

  let menu = [];
  const { data: Menu } = Digit.Hooks.pt.useGenderMDMS(stateId, "common-masters", "GenderType");
  Menu &&
    Menu.map((genderDetails) => {
      menu.push({ i18nKey: `PT_COMMON_GENDER_${genderDetails.code}`, code: `${genderDetails.code}`, value: `${genderDetails.code}` });
    });

  const setFileStoreId = async (fileStoreId) => {
    setProfilePic(fileStoreId);

    const thumbnails = fileStoreId ? await getThumbnails([fileStoreId], stateId) : null;

    setProfileImg(thumbnails?.thumbs[0]);

    closeFileUploadDrawer();
  };

  const getThumbnails = async (ids, tenantId) => {
    const res = await Digit.UploadServices.Filefetch(ids, tenantId);
    if (res.data.fileStoreIds && res.data.fileStoreIds.length !== 0) {
      return {
        thumbs: res.data.fileStoreIds.map((o) => o.url.split(",")[3]),
        images: res.data.fileStoreIds.map((o) => Digit.Utils.getFileUrl(o.url)),
      };
    } else {
      return null;
    }
  };

  if (loading) return <Loader></Loader>;

  //function for edit button with edit icon and functioanality of redirecting to differnt URL's
  const ActionButton = ({ onClick }) => {
    return <LinkButton 
    label={<EditIcon className="profile-edit-icon" />}
    className="check-page-link-button" onClick={onClick} />;
  };

  if (userType === "employee") {
    return (
      <div className="employee-profile-wrapper">
        <div className="employee-profile-breadcrumb-section">
          <BreadCrumb
            crumbs={[
              {
                path: "/upyog-ui/employee",
                content: t("ES_COMMON_HOME") || "Home",
                show: true,
              },
              {
                path: "/upyog-ui/employee/user/profile",
                content: !t("CORE_COMMON_PROFILE") || t("CORE_COMMON_PROFILE") === "CORE_COMMON_PROFILE" ? "Edit Profile" : t("CORE_COMMON_PROFILE"),
                show: true,
              },
            ]}
          />
        </div>

        <div className="employee-profile-grid">
          {/* Left Summary & Avatar Card */}
          <div className="employee-profile-sidebar-card">
            <div className="employee-profile-avatar-box">
              <img
                className="employee-profile-avatar-img"
                src={!profileImg || profileImg === "" ? defaultImage : profileImg}
                alt="Profile Avatar"
              />
              <button
                type="button"
                className="employee-profile-camera-btn"
                title="Update Profile Photo"
                onClick={onClickAddPic}
              >
                <CameraIcon />
              </button>
            </div>

            <h2 className="employee-profile-name">{name || userInfo?.name || "Employee"}</h2>
            <div className="employee-profile-role-badge">
              {userInfo?.roles?.[0]?.name || t("CORE_EMPLOYEE_ROLE") || "Official User"}
            </div>

            <div className="employee-profile-quick-info">
              <div className="quick-info-row">
                <span className="quick-info-label">{t("CORE_COMMON_PROFILE_CITY") || "City / ULB"}</span>
                <span className="quick-info-val">{t(city) || "—"}</span>
              </div>
              <div className="quick-info-row">
                <span className="quick-info-label">{t("CORE_COMMON_PROFILE_MOBILE_NUMBER") || "Mobile No."}</span>
                <span className="quick-info-val">{mobileNumber || "—"}</span>
              </div>
              {email && (
                <div className="quick-info-row">
                  <span className="quick-info-label">{t("CORE_COMMON_PROFILE_EMAIL") || "Email"}</span>
                  <span className="quick-info-val">{email}</span>
                </div>
              )}
            </div>

            <div className="employee-profile-note-box">
              <span className="note-icon">💡</span>
              <p className="note-text">
                {t("CORE_PROFILE_INFO_TIP") || "Keep your profile up-to-date for official communications and workflow assignments."}
              </p>
            </div>
          </div>

          {/* Right Main Form Card */}
          <div className="employee-profile-main-card">
            <div className="employee-profile-card-header">
              <div className="header-title-wrap">
                <h3 className="card-main-title">{t("CORE_COMMON_PROFILE_DETAILS") || "Personal Information"}</h3>
                <p className="card-sub-title">
                  {t("CORE_COMMON_PROFILE_SUBTITLE") || "Manage and update your personal details and account credentials."}
                </p>
              </div>
            </div>

            <div className="employee-profile-form-grid">
              {/* Full Name */}
              <div className="employee-profile-form-item">
                <label className="employee-profile-form-label">
                  {t("CORE_COMMON_PROFILE_NAME") || "Full Name"} <span className="req-star">*</span>
                </label>
                <TextInput
                  t={t}
                  type={"text"}
                  name="name"
                  value={name}
                  onChange={(e) => setUserName(e.target.value)}
                  placeholder="Enter full name"
                  disable={editScreen}
                />
                {errors?.userName && (
                  <CardLabelError className="profile-field-error">{errors?.userName?.message}</CardLabelError>
                )}
              </div>

              {/* Gender */}
              <div className="employee-profile-form-item">
                <label className="employee-profile-form-label">{t("CORE_COMMON_PROFILE_GENDER") || "Gender"}</label>
                <Dropdown
                  selected={gender?.length === 1 ? gender[0] : gender}
                  disable={gender?.length === 1 || editScreen}
                  option={menu}
                  select={setGenderName}
                  value={gender}
                  optionKey="code"
                  t={t}
                  name="gender"
                />
              </div>

              {/* City */}
              <div className="employee-profile-form-item">
                <label className="employee-profile-form-label">
                  {t("CORE_COMMON_PROFILE_CITY") || "City / Tenant"} <span className="locked-pill">🔒 {t("READ_ONLY") || "Read-only"}</span>
                </label>
                <TextInput
                  t={t}
                  type={"text"}
                  name="city"
                  value={t(city)}
                  onChange={(e) => setCity(e.target.value)}
                  placeholder="City"
                  disable={true}
                />
              </div>

              {/* Mobile Number */}
              <div className="employee-profile-form-item">
                <label className="employee-profile-form-label">
                  {t("CORE_COMMON_PROFILE_MOBILE_NUMBER") || "Mobile Number"} <span className="locked-pill">🔒 {t("READ_ONLY") || "Read-only"}</span>
                </label>
                <MobileNumber
                  value={mobileNumber}
                  name="mobileNumber"
                  placeholder="Enter mobile number"
                  onChange={(value) => setUserMobileNumber(value)}
                  disable={true}
                />
                {errors?.mobileNumber && (
                  <CardLabelError className="profile-field-error">{errors?.mobileNumber?.message}</CardLabelError>
                )}
              </div>

              {/* Alt Mobile Number */}
              <div className="employee-profile-form-item">
                <label className="employee-profile-form-label">{t("CORE_COMMON_PROFILE_ALT_MOBILE_NUMBER") || "Alternate Mobile Number"}</label>
                <MobileNumber
                  value={altMobileNumber}
                  name="altMobileNumber"
                  placeholder="Enter alternate mobile number"
                  onChange={(value) => setUserAltMobileNumber(value)}
                />
                {errors?.altMobileNumber && (
                  <CardLabelError className="profile-field-error">{errors?.altMobileNumber?.message}</CardLabelError>
                )}
              </div>

              {/* Email */}
              <div className="employee-profile-form-item">
                <label className="employee-profile-form-label">{t("CORE_COMMON_PROFILE_EMAIL") || "Email Address"}</label>
                <TextInput
                  t={t}
                  type={"email"}
                  placeholder="Enter email address"
                  name="email"
                  value={email}
                  onChange={(e) => setUserEmailAddress(e.target.value)}
                  disable={editScreen}
                />
                {errors?.emailAddress && (
                  <CardLabelError className="profile-field-error">{errors?.emailAddress?.message}</CardLabelError>
                )}
              </div>

              {/* Date of Birth */}
              <div className="employee-profile-form-item">
                <label className="employee-profile-form-label">{t("CORE_COMMON_PROFILE_DOB") || "Date of Birth"}</label>
                <DatePicker date={dob || dateOfBirth} onChange={setUserDOB} disable={true} />
              </div>
            </div>

            {/* Change Password Accordion Card */}
            <div className="employee-profile-security-box">
              <div className="security-box-header" onClick={TogleforPassword}>
                <div className="security-box-left">
                  <span className="security-box-icon">🔐</span>
                  <div>
                    <h4 className="security-box-title">{t("CORE_COMMON_CHANGE_PASSWORD") || "Change Password"}</h4>
                    <p className="security-box-desc">
                      {changepassword ? "Enter current and new password below" : "Click to expand and update your login password"}
                    </p>
                  </div>
                </div>
                <button type="button" className={`security-box-toggle-btn ${changepassword ? "expanded" : ""}`}>
                  {changepassword ? "Close" : "Update Password"}
                </button>
              </div>

              {changepassword && (
                <div className="security-box-content">
                  <div className="employee-profile-form-grid">
                    <div className="employee-profile-form-item">
                      <label className="employee-profile-form-label">{t("CORE_COMMON_PROFILE_CURRENT_PASSWORD") || "Current Password"}</label>
                      <TextInput
                        t={t}
                        type={"password"}
                        name="currentPassword"
                        placeholder="Enter current password"
                        onChange={(e) => setUserCurrentPassword(e.target.value)}
                        disable={editScreen}
                      />
                      {errors?.currentPassword && (
                        <CardLabelError className="profile-field-error">{errors?.currentPassword?.message}</CardLabelError>
                      )}
                    </div>

                    <div className="employee-profile-form-item">
                      <label className="employee-profile-form-label">{t("CORE_COMMON_PROFILE_NEW_PASSWORD") || "New Password"}</label>
                      <TextInput
                        t={t}
                        type={"password"}
                        name="newPassword"
                        placeholder="8-15 alphanumeric chars (@#$%)"
                        onChange={(e) => setUserNewPassword(e.target.value)}
                        disable={editScreen}
                      />
                      {errors?.newPassword && (
                        <CardLabelError className="profile-field-error">{errors?.newPassword?.message}</CardLabelError>
                      )}
                    </div>

                    <div className="employee-profile-form-item">
                      <label className="employee-profile-form-label">{t("CORE_COMMON_PROFILE_CONFIRM_PASSWORD") || "Confirm New Password"}</label>
                      <TextInput
                        t={t}
                        type={"password"}
                        name="confirmPassword"
                        placeholder="Confirm new password"
                        onChange={(e) => setUserConfirmPassword(e.target.value)}
                        disable={editScreen}
                      />
                      {errors?.confirmPassword && (
                        <CardLabelError className="profile-field-error">{errors?.confirmPassword?.message}</CardLabelError>
                      )}
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Actions Bar */}
            <div className="employee-profile-actions-bar">
              <button
                type="button"
                className="employee-profile-save-btn"
                onClick={updateProfile}
              >
                {t("CORE_COMMON_SAVE") || "Save Changes"}
              </button>
            </div>
          </div>
        </div>

        {toast && (
          <div className="profile-toast-container">
            <Toast
              error={toast.key === "error"}
              label={t(toast.key === "success" ? `CORE_COMMON_PROFILE_UPDATE_SUCCESS` : toast.action)}
              onClose={() => setToast(null)}
            />
          </div>
        )}

        {openUploadSlide === true && (
          <UploadDrawer
            setProfilePic={setFileStoreId}
            closeDrawer={closeFileUploadDrawer}
            userType={userType}
            removeProfilePic={removeProfilePic}
            showToast={showToast}
          />
        )}
      </div>
    );
  }

  return (
    <div>
      <section className="user-profile-header-section">
        <React.Fragment>
          <BackButton />
          <div className="user-profile-tabs-wrapper">
            <button
              onClick={() => SetActiveTab("profile")}
              className={`user-profile-tab-button ${isMobile ? "is-mobile" : ""} ${activeTab === "profile" ? "active" : ""}`}
            >
              {t("PROFILE")}
            </button>

            <button
              onClick={() => SetActiveTab("address")}
              className={`user-profile-tab-button ${isMobile ? "is-mobile" : ""} ${activeTab === "address" ? "active" : ""}`}
            >
              {t("ADDRESS")}
            </button>
          </div>
        </React.Fragment>
      </section>
      <div
        className={`user-profile-main-card ${windowWidth < 768 ? "is-stacked" : ""} ${activeTab === "address" ? "is-address-tab" : ""}`}
      >
        {activeTab !== "address" ? (
          <section className="user-profile-avatar-section">
            <div className="user-profile-avatar-inner">
              <img
                className="user-profile-avatar-img"
                src={!profileImg || profileImg === "" ? defaultImage : profileImg}
                alt="Profile"
              />
              <button className="user-profile-camera-btn" onClick={onClickAddPic}>
                <CameraIcon />
              </button>
            </div>
          </section>
        ) : null}
        <section
          className={`user-profile-form-section ${activeTab === "address" ? "is-address-tab" : ""}`}
        >
          {activeTab === "profile" ? (
            <React.Fragment>
              <LabelFieldPair>
                <CardLabel className={editScreen ? "profile-label-disabled" : ""}>{`${t("CORE_COMMON_PROFILE_NAME")}`}*</CardLabel>
                <div className="user-profile-input-wrap">
                  <TextInput
                    t={t}
                    className="w-full"
                    type={"text"}
                    isMandatory={false}
                    name="name"
                    value={name}
                    onChange={(e) => setUserName(e.target.value)}
                    {...(validation = {
                      isRequired: true,
                      pattern: "^[a-zA-Z ]*$",
                      type: "tel",
                      title: t("CORE_COMMON_PROFILE_NAME_ERROR_MESSAGE"),
                    })}
                    disable={editScreen}
                  />
                  {errors?.userName && <CardLabelError> {errors?.userName?.message} </CardLabelError>}
                </div>
              </LabelFieldPair>

              <LabelFieldPair>
                <CardLabel className={editScreen ? "profile-label-disabled" : ""}>{`${t("CORE_COMMON_PROFILE_GENDER")}`}</CardLabel>
                <Dropdown
                  className="form-field w-full"
                  selected={gender?.length === 1 ? gender[0] : gender}
                  disable={gender?.length === 1 || editScreen}
                  option={menu}
                  select={setGenderName}
                  value={gender}
                  optionKey="code"
                  t={t}
                  name="gender"
                />
              </LabelFieldPair>
              <LabelFieldPair>
                <CardLabel className={editScreen ? "profile-label-disabled" : ""}>{`${t("CORE_COMMON_PROFILE_DOB")}`}*</CardLabel>
                <div className="user-profile-input-wrap">
                  <DatePicker date={dob || dateOfBirth} onChange={setUserDOB} disable={true} />
                  {errors?.userName && <CardLabelError> {errors?.userName?.message} </CardLabelError>}
                </div>
              </LabelFieldPair>
              <LabelFieldPair>
                <CardLabel className={editScreen ? "profile-label-disabled" : ""}>{`${t("CORE_COMMON_PROFILE_MOBILE_NUMBER")}*`}</CardLabel>
                <div className="w-full">
                  <MobileNumber
                    value={mobileNumber}
                    className="w-full"
                    name="mobileNumber"
                    placeholder="Enter a valid Mobile No."
                    onChange={(value) => setUserMobileNumber(value)}
                    disable={true}
                    {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID") }}
                  />
                  {errors?.mobileNumber && <CardLabelError className="profile-field-error-flush"> {errors?.mobileNumber?.message} </CardLabelError>}
                </div>
              </LabelFieldPair>
              <LabelFieldPair>
                <CardLabel className={editScreen ? "profile-label-disabled" : ""}>{`${t("CORE_COMMON_PROFILE_ALT_MOBILE_NUMBER")}*`}</CardLabel>
                <div className="w-full">
                  <MobileNumber
                    value={altMobileNumber}
                    className="w-full"
                    name="altMobileNumber"
                    placeholder="Enter a valid Mobile No."
                    onChange={(value) => setUserAltMobileNumber(value)}
                    {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_PROFILE_MOBILE_NUMBER_INVALID") }}
                  />
                  {errors?.altMobileNumber && (
                    <CardLabelError className="profile-field-error-flush"> {errors?.altMobileNumber?.message} </CardLabelError>
                  )}
                </div>
              </LabelFieldPair>
              <LabelFieldPair>
                <CardLabel className={editScreen ? "profile-label-disabled" : ""}>{`${t("CORE_COMMON_PROFILE_EMAIL")}`}</CardLabel>
                <div className="w-full">
                  <TextInput
                    t={t}
                    className="w-full"
                    type={"email"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="email"
                    value={email}
                    onChange={(e) => setUserEmailAddress(e.target.value)}
                    disable={editScreen}
                  />
                  {errors?.emailAddress && <CardLabelError> {errors?.emailAddress?.message} </CardLabelError>}
                </div>
              </LabelFieldPair>

              <button
                onClick={updateProfile}
                className="citizen-profile-update-btn"
              >
                {t("CORE_COMMON_SAVE")}
              </button>
            </React.Fragment>
          ) : activeTab === "address" ? (
            <React.Fragment>
              <div className="citizen-address-btn-row">
                <button
                  onClick={() => setShowModal(true)}
                  className="citizen-add-address-btn"
                >
                  {t("ADD_NEW_ADDRESS")}
                </button>
              </div>

              {showModal && (
                <Address
                  refreshAddresses={userSearchNewV2}
                  actionCancelOnSubmit={() => setShowModal(false)}
                />
              )}
            </React.Fragment>
          ) : null}
        </section>
      </div>

      {activeTab === "address" && (
        <React.Fragment>
          {userAddresses.length > 0 ? (
            <React.Fragment>
              {userAddresses.map((address, index) => (
                <Card key={index}>
                  <StatusTable>
                    <React.Fragment>
                      <Row
                        className="border-none"
                        label={t(`${address.addressType}`)}
                        text=""
                        actionButton={
                          <ActionButton
                            onClick={() => {
                              setSelectedAddress(address);
                              setShowModal(true);
                              setisEdit(true);
                            }}
                          />
                        }
                      />
                      <Row className="border-none" label={t("COMMON_HOUSE_NO")} text={address.houseNumber || t("CS_NA")} />
                      <Row className="border-none" label={t("COMMON_STREET_NAME")} text={address.streetName || t("CS_NA")} />
                      <Row className="border-none" label={t("COMMON_ADDRESS_LINE1")} text={address.address || t("CS_NA")} />
                      <Row className="border-none" label={t("COMMON_ADDRESS_LINE2")} text={address.address2 || t("CS_NA")} />
                      <Row className="border-none" label={t("COMMON_LANDMARK")} text={address.landmark || t("CS_NA")} />
                      <Row className="border-none" label={t("COMMON_CITY")} text={address.city || t("CS_NA")} />
                      <Row className="border-none" label={t("COMMON_LOCALITY")} text={address.locality || t("CS_NA")} />
                      <Row className="border-none" label={t("COMMON_ADDRESS_PINCODE")} text={address.pinCode || t("CS_NA")} />
                    </React.Fragment>
                  </StatusTable>
                </Card>
              ))}

              {showModal && selectedAddress && (
                <Address
                  isEdit={isEdit}
                  address={selectedAddress}
                  refreshAddresses={userSearchNewV2}
                  actionCancelOnSubmit={() => {
                    setShowModal(false);
                    setSelectedAddress(null);
                    setisEdit(false);
                  }}
                />
              )}
            </React.Fragment>
          ) : (
            <Card>
              <p>{t("CS_NO_ADDRESS_AVAILABLE")}</p>
            </Card>
          )}
        </React.Fragment>
      )}

      {toast && (
        <div className="profile-toast-container">
          <Toast
            error={toast.key === "error"}
            label={t(toast.key === "success" ? `CORE_COMMON_PROFILE_UPDATE_SUCCESS` : toast.action)}
            onClose={() => setToast(null)}
          />
        </div>
      )}

      {openUploadSlide === true ? (
        <UploadDrawer
          setProfilePic={setFileStoreId}
          closeDrawer={closeFileUploadDrawer}
          userType={userType}
          removeProfilePic={removeProfilePic}
          showToast={showToast}
        />
      ) : (
        ""
      )}
    </div>
  );
};

export default UserProfile;