import React, { useEffect, useState } from "react";
import {
  FormStep,
  TextInput,
  CardLabel,
  RadioButtons,
  LabelFieldPair,
  Dropdown,
  Menu,
  MobileNumber,
  TextArea,
} from "@upyog/digit-ui-react-components";
import { useLocation, useRouteMatch } from "react-router-dom";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/VENDORTimeline";

const VendorDetails = ({ t, config, onSelect, userType, formData, ownerIndex }) => {
  const { pathname: url } = useLocation();

  let index = 0;
  let validation = {};

  //   const user = Digit.UserService.getUser().info;
  //   const [applicantName, setName] = useState(
  //     (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].applicantName) || formData?.ownerKey?.applicantName || ""
  //   );
  // const [emailId, setEmail] = useState(
  //   (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].emailId) || formData?.ownerKey?.emailId || ""
  // );
  //   const [mobileNumber, setMobileNumber] = useState(
  //     (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].mobileNumber) || formData?.ownerKey?.mobileNumber || user?.mobileNumber
  //   );
  //   const [altMobileNumber, setAltMobileNumber] = useState(
  //     (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].altMobileNumber) || formData?.ownerKey?.altmobileNumber || ""
  //   );

  // States for vendor Additionals details

  const [VendorCompany, setVendorCompany] = useState("");
  const [VendorPhone, setVendorPhone] = useState("");
  const [VendorAddress, setVendorAddress] = useState("");
  const [VendorCategory, setVendorCategory] = useState("");
  const [VendorEmail, setVendorEmail] = useState();
  const [VendorId, setVendorId] = useState("");
  const [Bank, setBank] = useState("");
  const [BankbranchName, setBankbranchName] = useState();
  const [IFSC, setIFSC] = useState("");
  const [AccountNo, setAccountNo] = useState("");
  const [PanNo, setPanNo] = useState("");
  const [GstNo, setGstNo] = useState("");
  const [GstState, setGstState] = useState("");
  const [RegistrationNo, setRegistrationNo] = useState("");
  const [EpfNo, setEpfNo] = useState("");
  const [EsiNo, setEsiNo] = useState("");
  const [VendorType, setVendorType] = useState("");
  const [Status, setStatus] = useState("");
  const [micrNo, setmicrNo] = useState("");
  const [PhoneNo, setPhoneNo] = useState("");
  const [ContactPerson, setContactPerson] = useState("");
  const [Company,setCompany] = useState("");

  const [showToast, setShowToast] = useState(null);

  //function for setting the values of the vendor details

  useEffect(() => {
    if (IFSC.length === 11 && !Bank && !BankbranchName && !micrNo) {
      fetch(`https://ifsc.razorpay.com/${IFSC}`)
        .then((response) => response.json())
        .then((data) => {
          if (data && data.BANK && data.BRANCH && data.MICR) {
            setBank(data.BANK);
            setBankbranchName(data.BRANCH);
            setmicrNo(data.MICR);
            setShowToast({ error: false, label: t("VALID_IFSC_CODE") });
          } else {
            setShowToast({ error: true, label: t("INVALID_IFSC_CODE") });
          }
        })
        .catch(() => {
          setShowToast({ error: true, label: t("INVALID_IFSC_CODE") });
        });
    } else {
      if (IFSC.length === 11 && Bank && BankbranchName) {
        setBank(Bank);
        setBankbranchName(BankbranchName);
        setmicrNo(micrNo);
      } else {
        setBank("");
        setBankbranchName("");
        setmicrNo("");
      }
    }
  }, [IFSC]);

  function setvendorcompany(e) {
    setVendorCompany(e.target.value);
  }
  function setvendoraddress(e) {
    setVendorAddress(e.target.value);
  }
  function setvendorcategory(e) {
    setVendorCategory(e.target.value);
  }

  function setvendoremail(e) {
    setVendorEmail(e.target.value);
  }

  function setvendorid(e) {
    setVendorId(e.target.value);
  }
  function setvendorphone(e) {
    setVendorPhone(e.target.value);
  }

  function setvendorbank(e) {
    setBank(e.target.value);
  }

  function setBankbranch(e) {
    setBankbranchName(e.target.value);
  }

  function setvendorifsc(e) {
    const input = e.target.value.replace(/[^a-zA-Z0-9]/g, "");
    if (input.length <= 11) {
      setIFSC(input);
    }
  }

  function setvendoraccountno(e) {
    setAccountNo(e.target.value);
  }

  function setpanno(e) {
    setPanNo(e.target.value);
  }

  function setgstno(e) {
    setGstNo(e.target.value);
  }

  function setgststate(e) {
    setGstState(e.target.value);
  }

  function setregistrationno(e) {
    setRegistrationNo(e.target.value);
  }

  function setepfno(e) {
    setEpfNo(e.target.value);
  }

  function setesino(e) {
    setEsiNo(e.target.value);
  }

  function setvendortype(e) {
    setVendorType(e.target.value);
  }

  function setstatus(e) {
    setStatus(e.target.value);
  }

  function setmicrno(e) {
    setmicrNo(e.target.value);
  }

  function setphoneno(e) {
    setPhoneNo(e.target.value);
  }

  function setcontactperson(e) {
    setContactPerson(e.target.value);
  }

  function setcompanyname(e){
    setCompany(e.target.value);
  }

  // mdms call
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const { control } = useForm();

  // This is responsible for the going into the next step
  const goNext = () => {
    let owner = formData.ownerKey && formData.ownerKey[index];
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, applicantName, mobileNumber, altMobileNumber, emailId };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    } else {
      ownerStep = {
        ...owner,
        VendorCompany,
        VendorPhone,
        VendorAddress,
        VendorCategory,
        VendorEmail,
        VendorId,
        Bank,
        BankbranchName,
        IFSC,
        AccountNo,
        PanNo,
        GstNo,
        GstState,
        RegistrationNo,
        EpfNo,
        EsiNo,
        VendorType,
        Status,
        micrNo,
        PhoneNo,
        ContactPerson,
        Company
      };
      onSelect(config.key, ownerStep, false, index);
    }
  };

  const onSkip = () => onSelect();

  //   useEffect(() => {
  //     if (userType === "citizen") {
  //       goNext();
  //     }
  //   }, [applicantName, mobileNumber, emailId]);

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [
    VendorCompany,
    VendorPhone,
    VendorAddress,
    VendorCategory,
    VendorEmail,
    VendorId,
    Bank,
    BankbranchName,
    IFSC,
    AccountNo,
    PanNo,
    GstNo,
    GstState,
    RegistrationNo,
    EpfNo,
    EsiNo,
    VendorType,
    Status,
    micrNo,
    PhoneNo,
    ContactPerson,
    Company
  ]);

  return (
    <React.Fragment>
      <Timeline currentStep={1} />
      {console.log("kunal in vendor details")}

      <FormStep
        config={config}
        onSelect={goNext}
        // onSkip={onSkip}
        t={t}
        // isDisabled={!applicantName || !mobileNumber || !emailId}
      >
        <div>
          <CardLabel>{`${t("VENDOR_ID")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="VendorId"
            value={VendorId}
            //placeholder={"Enter IFSC Code"}
            onChange={setvendorid}
            style={{ width: "50%" }}
            maxLength={11}
            ValidationRequired={false}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]{1,20}$", // validation for IFSC code
              type: "text",
              title: t("INVALID_VENDOR_ID"),
            })}
          />

          <CardLabel>{`${t("IFSC_CODE")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="IFSC"
            value={IFSC}
            placeholder={"Enter IFSC Code"}
            onChange={setvendorifsc}
            style={{ width: "50%" }}
            maxLength={11}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[A-Z]{4}0[A-Z0-9]{6}$", // validation for IFSC code
              type: "text",
              title: t("INVALID_IFSC_CODE_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>
            {`${t("VENDOR_BANK_NAME")}`} <span className="check-page-link-button">*</span>
          </CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="bankName"
            placeholder={"Bank Name Auto Select"}
            style={{ width: "50%" }}
            value={Bank}
            onChange={setvendorbank}
            disabled={true}
          />

          <CardLabel>
            {`${t("VENDOR_BANK_BRANCH_NAME")}`} <span className="check-page-link-button">*</span>
          </CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="BankbranchName"
            value={BankbranchName}
            style={{ width: "50%" }}
            placeholder={"Bank Branch Name Auto Select"}
            onChange={setBankbranch}
            disabled={false}
          />

          <CardLabel>
            {`${t("VENDOR_MICR_NO")}`} <span className="check-page-link-button">*</span>
          </CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="MicrNo"
            value={micrNo}
            style={{ width: "50%" }}
            placeholder={"MICR No"}
            onChange={setmicrNo}
            disabled={false}
          />

          <CardLabel>{`${t("ACCOUNT_NO")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="AccountNo"
            value={AccountNo}
            onChange={setvendoraccountno}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "[0-9]{9,18}", // validation for account number
              type: "text",
              title: t("INVALID_ACCOUNT_NO_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("PHONE_NO")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="PhoneNo"
            value={PhoneNo}
            onChange={setphoneno}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "[0-9]{9,18}", // validation for account number
              type: "text",
              title: t("INVALID_ACCOUNT_NO_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("CONTACT_PERSON")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="ContactPerson"
            value={ContactPerson}
            onChange={setcontactperson}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]{1,20}$", // validation for account number
              type: "text",
              title: t("INVALID_ACCOUNT_NO_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("COMPANY_NAME")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="Company"
            value={Company}
            onChange={setcompanyname}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]{1,20}$", // validation for account number
              type: "text",
              title: t("INVALID_ACCOUNT_NO_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("PAN_NO")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="PanNo"
            value={PanNo}
            onChange={setpanno}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "[A-Z]{5}[0-9]{4}[A-Z]{1}", // validation for PAN number
              type: "text",
              title: t("INVALID_PAN_NO_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("GST_NO")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="GstNo"
            value={GstNo}
            onChange={setgstno}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z0-9]{1}[Z]{1}[A-Z0-9]{1}$", // validation for GST number
              type: "text",
              title: t("INVALID_GST_NO_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("GST_REGISTERED_STATE/UT")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="GstState"
            value={GstState}
            onChange={setgststate}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]{1,20}$", // validation for GST state
              type: "text",
              title: t("PT_NAME_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("REGISTRATION_NO")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="RegistrationNo"
            value={RegistrationNo}
            onChange={setregistrationno}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]{1,20}$", // validation for registration number
              type: "text",
              title: t("INVALID_REGISTRATION_NO_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("EPF_NO")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="EpfNo"
            value={EpfNo}
            onChange={setepfno}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]{1,15}$", // validation for EPF number
              type: "text",
              title: t("INVALID_EPF_NO_ERROR_MESSAGE"),
            })}
          />

          <CardLabel>{`${t("ESI_NO")}`}</CardLabel>
          <TextInput
            t={t}
            type={"text"}
            isMandatory={false}
            optionKey="i18nKey"
            name="EsiNo"
            value={EsiNo}
            onChange={setesino}
            style={{ width: "50%" }}
            ValidationRequired={true}
            {...(validation = {
              isRequired: true,
              pattern: "^[a-zA-Z0-9/-]{1,20}$", // validation for ESI number
              type: "text",
              title: t("INVALID_ESI_NO_ERROR_MESSAGE"),
            })}
          />

          <div>
            {t("VENDOR_TYPE")}
            <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
              <span
                className="tooltiptext"
                style={{
                  whiteSpace: "pre-wrap",
                  fontSize: "small",
                  wordWrap: "break-word",
                  width: "300px",
                  marginLeft: "15px",
                  marginBottom: "-10px",
                }}
              >
                {`${t(`AST_CLASSIFICATION_ASSET`)}`}
              </span>
            </div>
          </div>
          <Controller
            control={control}
            name={"VendorType"}
            defaultValue={VendorType}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={VendorType}
                select={setVendorType}
                option={[
                  { i18nKey: "Supplier", code: "SUPPLIER" },
                  { i18nKey: "Contractor", code: "CONTRACTOR" },
                ]}
                optionKey="i18nKey"
                placeholder={"Select"}
                t={t}
              />
            )}
          />

          {/* dropdown for vendor category  */}
          <div>
            {t("VENOR_CATEGORY")}
            <div className="tooltip" style={{ width: "12px", height: "5px", marginLeft: "10px", display: "inline-flex", alignItems: "center" }}>
              <span
                className="tooltiptext"
                style={{
                  whiteSpace: "pre-wrap",
                  fontSize: "small",
                  wordWrap: "break-word",
                  width: "300px",
                  marginLeft: "15px",
                  marginBottom: "-10px",
                }}
              >
                {/* {`${t(`AST_SOURCE_OF_FUNDING`)}`} */}
              </span>
            </div>
          </div>
          <Controller
            control={control}
            name={"VendorCategory"}
            defaultValue={VendorCategory}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={VendorCategory}
                select={setVendorCategory}
                //option={sourcefinance}   this loads the vendor category
                option={[
                  { i18nKey: "Firm", code: "FIRM" },
                  { i18nKey: "Individual", code: "Individual" },
                ]}
                optionKey="i18nKey"
                placeholder={"Select"}
                t={t}
              />
            )}
          />

          <div>
            {t("STATUS")}
            <div
              className="tooltip"
              style={{
                width: "12px",
                height: "5px",
                marginLeft: "10px",
                display: "inline-flex",
                alignItems: "center",
              }}
            >
              <span
                className="tooltiptext"
                style={{
                  whiteSpace: "pre-wrap",
                  fontSize: "small",
                  wordWrap: "break-word",
                  width: "300px",
                  marginLeft: "15px",
                  marginBottom: "-10px",
                }}
              >
                {`${t(`AST_CLASSIFICATION_ASSET`)}`}
              </span>
            </div>
          </div>
          <Controller
            control={control}
            name={"Status"}
            defaultValue={Status}
            rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
            render={(props) => (
              <Dropdown
                className="form-field"
                selected={Status}
                select={setStatus}
                option={[
                  { i18nKey: "Active", code: "ACTIVE" },
                  { i18nKey: "Inactive", code: "INACTIVE" },
                ]}
                optionKey="i18nKey"
                placeholder={"Select"}
                t={t}
              />
            )}
          />
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default VendorDetails;
