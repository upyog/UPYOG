import { CardLabel, LabelFieldPair, Modal, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { Link, useHistory, useLocation } from "react-router-dom";
import Timeline from "../components/bmcTimeline";
import Title from "../components/title";

import {
  EditIcon
} from "@egovernments/digit-ui-react-components";

const defaultImage =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAO4AAADUCAMAAACs0e/bAAAAM1BMVEXK0eL" +
  "/" +
  "/" +
  "/" +
  "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";

export const Profile = ({ info, stateName, t }) => {
  const [profilePic, setProfilePic] = useState(null);
  useEffect(async () => {
    const tenant = Digit.ULBService.getCurrentTenantId();
    const uuid = info?.uuid;
    if (uuid) {
      const usersResponse = await Digit.UserService.userSearch(tenant, { uuid: [uuid] }, {});

      if (usersResponse && usersResponse.user && usersResponse.user.length) {
        const userDetails = usersResponse.user[0];
        const thumbs = userDetails?.photo?.split(",");
        setProfilePic(thumbs?.at(0));
      }
    }
  }, [profilePic !== null]);
  return (
    <div className="profile-section" style={{ padding: "0" }}>
      <div className="imageloader imageloader-loaded">
        <img
          className="img-responsive img-circle img-Profile"
          src={profilePic ? profilePic : defaultImage}
          style={{ objectFit: "cover", objectPosition: "center", borderRadius: "0", width: "14rem", height: "14rem", margin: "0" }}
        />
      </div>
      <div id="profile-name" className="label-container name-Profile">
        <div className="label-text"> {info?.name} </div>
      </div>
      <div id="profile-location" className="label-container loc-Profile">
        <div className="label-text"> {info?.mobileNumber} </div>
      </div>
      {info?.emailId && (
        <div id="profile-emailid" className="label-container loc-Profile">
          <div className="label-text"> {info.emailId} </div>
        </div>
      )}
    </div>
  );
};

const createOwnerDetail = () => ({
  applicationNumber: "",
  scheme: "Scheme Name",
  machineName: "Sewing Machine",
  name: "Bal Krishana Yadav",
  fatherName: "Ram Krishana Yadav",
  gender: "Male",
  address: "127/14 Secotr 3 Gomati Nagar",
  city: "Lucknow",
  district: "Lucknow",
  state: "Uttar Pradesh",
  pincode: "226022",
  subDistrict: "Lucknow",
  dob: "20/11/1990",
  religion: "Hindu",
  wardName: "A",
  subWardName: "B",
  caste: "Caste Name",
  rationCardType: "BPL",
  bankName: "SBI",
  branchName: "Gomati Nagar",
  ifscCode: "89990",
  accountNumber: "12345678",
  micrCode: "89909",
  profession: "Profession",
  docimile: "Yes",
  income: "Yes",
  voterId: "Yes",
  pan: "Yes",
  bankPassBook: "Yes",
});

const BMCReviewPage = ({ config, onSelect, formData, setError, formState, clearErrors }) => {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const [owners, setOwners] = useState(formData?.owners || [createOwnerDetail()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const commonProps = {
    focusIndex,
    allOwners: owners,
    setFocusIndex,
    formData,
    formState,
    setOwners,
    t,
    setError,
    clearErrors,
    config,
  };

  return (
    <React.Fragment>
      {owners.map((owner, index) => (
        <ReviewDetailForm key={owner.key} index={index} owner={owner} {...commonProps} />
      ))}
    </React.Fragment>
  );
};

const ReviewDetailForm = (_props) => {
  const {
    owner,
    userType,
    index,
    onSelect,
    focusIndex,
    allOwners,
    setFocusIndex,
    formData,
    formState,
    setOwners,
    t,
    setError,
    clearErrors,
    config,
  } = _props;

  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm();
  const history = useHistory();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const location = useLocation();
  const { aadhaarInfo, selectedScheme, selectedRadio } = location.state || {};
  const [isSecondModalOpen, setIsSecondModalOpen] = useState(false);
  const [isEdit, setIsEdit] = useState({});

  const handleEdit = (value) => {
    setIsEdit((prev) => ({ ...prev, [value]: !prev[value] }));
  };

  const openSecondModal = () => {
    setIsModalOpen(false);
    setIsSecondModalOpen(true);
  };

  console.log(owner);

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setIsSecondModalOpen(false);
  };

  const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#FFFFFF">
      <path d="M0 0h24v24H0V0z" fill="none" />
      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
    </svg>
  );

  const CloseBtn = (props) => {
    return (
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        {window.location.href.includes("/citizen") ? <Timeline currentStep={5} /> : null}
        <Title text={"Review Page"} />
        <div className="bmc-row-card-header">
          <div className="bmc-title" style={{ paddingBottom: "0" }}>
            Scheme Details
          </div>
          <div className="bmc-card-row">
            <div className="bmc-col1-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Application_Number:"}</CardLabel>
                <Controller
                  control={control}
                  name={"applicationNumber"}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "applicationNumber"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "applicationNumber" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col1-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Scheme_Name:"}</CardLabel>
                <Controller
                  control={control}
                  name={"scheme"}
                  defaultValue={owner?.scheme}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === selectedScheme?.key && focusIndex.type === "scheme"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: selectedScheme.key, type: "scheme" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col1-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Machine_Name:"}</CardLabel>
                <Controller
                  control={control}
                  name={"machineName"}
                  defaultValue={owner?.machineName}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "machineName"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "machineName" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
          </div>
        </div>
        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-title" style={{ paddingBottom: "0" }}>
              Personal Details
            </div>
            <div className="bmc-col-large-header">
              <div className="bmc-card-row">
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Name:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"name"}
                      defaultValue={aadhaarInfo?.name}
                      render={(props) => (
                        <TextInput
                          // readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === aadhaarInfo?.key && focusIndex.type === "name"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: aadhaarInfo.key, type: "name" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Father_Name:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"fatherName"}
                      defaultValue={owner?.fatherName}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "fatherName"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "fatherName" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Gender:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"gender"}
                      defaultValue={owner?.gender}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "gender"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "gender" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
              </div>
              <div className="bmc-card-row">
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_DOB:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"dob"}
                      defaultValue={owner?.dob}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "dob"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "dob" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Address:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"address"}
                      defaultValue={owner?.address}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "address"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "address" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Pincode:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"pincode"}
                      defaultValue={owner?.pincode}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "pincode"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "pincode" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
              </div>
              <div className="bmc-card-row">
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_District:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"district"}
                      defaultValue={owner?.district}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "district"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "district" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_State:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"state"}
                      defaultValue={owner?.state}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "state"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "state" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                  </LabelFieldPair>
                </div>
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Religion:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"religion"}
                      defaultValue={owner?.religion}
                      render={(props) => (
                        <TextInput
                          readOnly={!isEdit.religion}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "religion"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "religion" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                    <EditIcon className="fill-path-primary-main" onChange={() => handleEdit("religion")} />
                  </LabelFieldPair>
                </div>
              </div>
              <div className="bmc-card-row">
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Caste:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"caste"}
                      defaultValue={owner?.caste}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "caste"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "caste" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                    <EditIcon className="fill-path-primary-main" />
                  </LabelFieldPair>
                </div>
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Ward_Name:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"wardName"}
                      defaultValue={owner?.wardName}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "wardName"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "wardName" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                    <EditIcon className="fill-path-primary-main" />
                  </LabelFieldPair>
                </div>
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Sub_Ward_Name:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"subWardName"}
                      defaultValue={owner?.subWardName}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "subWardName"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "subWardName" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                    <EditIcon className="fill-path-primary-main" />
                  </LabelFieldPair>
                </div>
              </div>
              <div className="bmc-card-row">
                <div className="bmc-col1-card">
                  <LabelFieldPair
                    style={{
                      display: "flex",
                      flexDirection: "row",
                      alignItems: "baseline",
                    }}
                  >
                    <CardLabel className="bmc-label">{"BMC_Ration_Card_Type:"}</CardLabel>
                    <Controller
                      control={control}
                      name={"rationCardType"}
                      defaultValue={owner?.rationCardType}
                      render={(props) => (
                        <TextInput
                          readOnly={props.disable}
                          style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                          // disabled
                          value={props.value}
                          autoFocus={focusIndex.index === owner?.key && focusIndex.type === "rationCardType"}
                          onChange={(e) => {
                            // props.onChange(e.target.value);
                            setFocusIndex({ index: owner.key, type: "rationCardType" });
                          }}
                          onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                          }}
                        />
                      )}
                    />
                    <EditIcon className="fill-path-primary-main" />
                  </LabelFieldPair>
                </div>
              </div>
            </div>
            <div className="bmc-col-small-header">
              <Profile />

              {/* <img src="" style={{ height: "250px", width: "250px", backgroundColor: "#F0EFEF" }} /> */}
            </div>
          </div>

          <div className="bmc-card-row">
            <hr />
            <div className="bmc-title" style={{ paddingBottom: "0", paddingTop: "1rem" }}>
              Bank Details
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Bank_Name:"}</CardLabel>
                <Controller
                  control={control}
                  name={"bankName"}
                  defaultValue={owner?.bankName}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "bankName"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "bankName" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
                <EditIcon className="fill-path-primary-main" />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Branch_Name:"}</CardLabel>
                <Controller
                  control={control}
                  name={"branchName"}
                  defaultValue={owner?.branchName}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "branchName"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "branchName" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
                <EditIcon className="fill-path-primary-main" />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Account_Number:"}</CardLabel>
                <Controller
                  control={control}
                  name={"accountNumber"}
                  defaultValue={owner?.accountNumber}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "accountNumber"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "accountNumber" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
                <EditIcon className="fill-path-primary-main" />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_IFSC_Code:"}</CardLabel>
                <Controller
                  control={control}
                  name={"ifscCode"}
                  defaultValue={owner?.ifscCode}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "ifscCode"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "ifscCode" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
                <EditIcon className="fill-path-primary-main" />
              </LabelFieldPair>
            </div>
          </div>
          <div className="bmc-card-row">
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_MICR_Code:"}</CardLabel>
                <Controller
                  control={control}
                  name={"micrCode"}
                  defaultValue={owner?.micrCode}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "micrCode"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "micrCode" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
                <EditIcon className="fill-path-primary-main" />
              </LabelFieldPair>
            </div>
          </div>

          <div className="bmc-card-row">
            <hr />
            <div className="bmc-title" style={{ paddingBottom: "0", paddingTop: "1rem" }}>
              Professional Details
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Profession:"}</CardLabel>
                <Controller
                  control={control}
                  name={"profession"}
                  defaultValue={owner?.profession}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "profession"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "profession" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Docimile:"}</CardLabel>
                <Controller
                  control={control}
                  name={"docimile"}
                  defaultValue={owner?.docimile}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "docimile"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "docimile" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Income:"}</CardLabel>
                <Controller
                  control={control}
                  name={"income"}
                  defaultValue={owner?.income}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "income"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "income" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_Voter_Id:"}</CardLabel>
                <Controller
                  control={control}
                  name={"voterId"}
                  defaultValue={owner?.voterId}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "voterId"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "voterId" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
          </div>
          <div className="bmc-card-row">
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_PAN_NUMBER:"}</CardLabel>
                <Controller
                  control={control}
                  name={"pan"}
                  defaultValue={owner?.pan}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "pan"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "pan" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair
                style={{
                  display: "flex",
                  flexDirection: "row",
                  alignItems: "baseline",
                }}
              >
                <CardLabel className="bmc-label">{"BMC_BANK_PASSBOOK:"}</CardLabel>
                <Controller
                  control={control}
                  name={"bankPassBook"}
                  defaultValue={owner?.bankPassBook}
                  render={(props) => (
                    <TextInput
                      readOnly={props.disable}
                      style={{ border: "none", fontSize: "17px", color: "#626161", fontWeight: "600" }}
                      // disabled
                      value={props.value}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "bankPassBook"}
                      onChange={(e) => {
                        // props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "bankPassBook" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
          </div>
        </div>

        {window.location.href.includes("/citizen") ? (
          <React.Fragment>
            <div style={{ float: "right", paddingBottom: "1rem", textAlign: "end" }}>
              <button type="submit" className="bmc-card-button" style={{ marginRight: "1rem", borderBottom: "3px solid black" }} onClick={openModal}>
                {t("BMC_Submit")}
              </button>
              <button type="button" className="bmc-card-button-cancel" style={{ borderBottom: "3px solid black" }} onClick={() => history.goBack()}>
                {t("BMC_Cancel")}
              </button>
            </div>
          </React.Fragment>
        ) : (
          <React.Fragment>
            <div style={{ float: "right", paddingBottom: "1rem", textAlign: "end" }}>
              <button type="submit" className="bmc-card-button" style={{ marginRight: "1rem", borderBottom: "3px solid black" }} onClick={openModal}>
                {t("BMC_Verify / Approve")}
              </button>
              <button type="button" className="bmc-card-button-cancel" style={{ borderBottom: "3px solid black" }} onClick={() => history.goBack()}>
                {t("BMC_Reject")}
              </button>
            </div>
          </React.Fragment>
        )}
        {isModalOpen && (
          <div className="bmc-modal">
            <Modal
              onClose={closeModal}
              fullScreen
              hideSubmit={true}
              headerBarEnd={<CloseBtn onClick={closeModal} />}
              headerBarMain={
                <h1 className="bmc-title" style={{ textAlign: "center", padding: "1rem" }}>
                  Confirmation
                </h1>
              }
            >
              <hr></hr>
              <p style={{ fontSize: "15px", padding: "2rem" }}>Are you sure you want to submit your application?</p>
              <div style={{ textAlign: "center" }}>
                <button
                  className="bmc-card-button"
                  style={{
                    borderBottom: "3px solid black",
                    outline: "none",
                    marginRight: "1rem",
                    width: "100px",
                    height: "40px",
                  }}
                  onClick={openSecondModal}
                >
                  Agree
                </button>
                <button
                  onClick={closeModal}
                  className="bmc-card-button-cancel"
                  style={{
                    borderBottom: "3px solid black",
                    outline: "none",
                    width: "100px",
                    height: "40px",
                  }}
                >
                  Cancel
                </button>
              </div>
            </Modal>
          </div>
        )}
        {isSecondModalOpen && (
          <div className="bmc-modal">
            <Modal
              onClose={closeModal}
              fullScreen
              hideSubmit={true}
              headerBarEnd={<CloseBtn onClick={closeModal} />}
              headerBarMain={
                <h1 className="bmc-title" style={{ textAlign: "center", padding: "1rem" }}>
                  Notification
                </h1>
              }
            >
              <hr></hr>
              <p style={{ fontSize: "15px", padding: "2rem", textAlign: "justify" }}>
                Your Application has been submitted successfully. your Application number is
                <span style={{ fontWeight: "bold" }}> 1234567890</span>. Please Note this Apllication Number for future Reference.
              </p>
              <div style={{ textAlign: "center" }}>
                <Link to="/citizen" style={{ textDecoration: "none" }}>
                  <button
                    className="bmc-card-button"
                    style={{
                      borderBottom: "3px solid black",
                      outline: "none",
                      marginRight: "1rem",
                      width: "100px",
                      height: "40px",
                    }}
                  >
                    OK
                  </button>
                </Link>
              </div>
            </Modal>
          </div>
        )}
      </div>
    </React.Fragment>
  );
};

export default BMCReviewPage;
