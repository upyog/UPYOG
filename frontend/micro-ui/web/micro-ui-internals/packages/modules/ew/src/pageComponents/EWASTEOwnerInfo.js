import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, RadioButtons, LabelFieldPair, Dropdown, Menu, MobileNumber } from "@egovernments/digit-ui-react-components";
import { cardBodyStyle } from "../utils";
import { useLocation, useRouteMatch } from "react-router-dom";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/EWASTETimeline";

const EWOwnerDetails
  = ({ t, config, onSelect, userType, formData, ownerIndex }) => {
    const { pathname: url } = useLocation();

    let index = 0
    // window.location.href.charAt(window.location.href.length - 1);
    // console.log("index in detail page ",  index)

    let validation = {};

    const [applicantName, setName] = useState((formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].applicantName) || formData?.ownerKey?.applicantName || "");
    const [mobileNumber, setMobileNumber] = useState(
      (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].mobileNumber) || formData?.ownerKey?.mobileNumber || ""
    );

    const [locality, setLocality] = useState(
      (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].locality) || formData?.ownerKey?.locality || ""
    );
    const [address, setAddress] = useState(
      (formData.ownerKey && formData.ownerKey[index] && formData.ownerKey[index].address) || formData?.ownerKey?.address || ""
    );

    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
    const { control } = useForm();


    function setOwnerName(e) {
      setName(e.target.value);
    }


    function setMobileNo(e) {
      setMobileNumber(e.target.value);
    }


    function setaddress(e){
      setAddress(e.target.value);
    }

    const roughlocality = [
      {
        code: "city a",
        i18nKey: "city a"
      },
      {
        code: "city b",
        i18nKey: "city b"
      },
      {
        code: "city c",
        i18nKey: "city c"
      },
      {
        code: "city d",
        i18nKey: "city d"
      },
    ]

    // const roughvendor = [
    //   {
    //     code: "vendor a",
    //     i18nKey: "vendor a"
    //   },
    //   {
    //     code: "vendor b",
    //     i18nKey: "vendor b"
    //   },
    //   {
    //     code: "vendor c",
    //     i18nKey: "vendor c"
    //   },
    //   {
    //     code: "vendor d",
    //     i18nKey: "vendor d"
    //   }
    // ]

    // const goNext = () => {
    //   let owner = formData.ownerKey && formData.ownerKey[index];
    //   let ownerStep;
    //   if (userType === "citizen") {
    //     ownerStep = { ...owner, applicantName, mobileNumber, alternateNumber, fatherName, emailId };
    //     onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
    //   } else {

    //     ownerStep = { ...owner, applicantName, mobileNumber, alternateNumber, fatherName, emailId };
    //     onSelect(config.key, ownerStep, false, index);
    //   }
    // };

    const goNext = () => {
      let owner = formData.ownerKey && formData.ownerKey[index];
      let ownerStep;
      if (userType === "citizen") {
        ownerStep = { ...owner, applicantName, mobileNumber, locality, address};
        onSelect(config.key, { ...formData[config.key], ...ownerStep }, false, index);
      } else {

        ownerStep = { ...owner, applicantName, mobileNumber, locality, address};
        onSelect(config.key, ownerStep, false, index);
      }
    };

    const onSkip = () => onSelect();




    useEffect(() => {
      if (userType === "citizen") {
        goNext();
      }
    }, [applicantName, mobileNumber, locality, address]);



    return (
      <React.Fragment>
        {
          window.location.href.includes("/citizen") ?
            <Timeline currentStep={2} />
            : null
        }

        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
        // isDisabled={!applicantName || !mobileNumber || !fatherName || !emailId}
        >
          <div>
            <CardLabel>{`${t("EWASTE_APPLICANT_NAME")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="applicantName"
              value={applicantName}
              onChange={setOwnerName}
              ValidationRequired={true}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("EWASTE_NAME_ERROR_MESSAGE"),
              })}
            />

            <CardLabel>{`${t("EWASTE_MOBILE_NUMBER")}`}</CardLabel>
            <MobileNumber
              value={mobileNumber}
              name="mobileNumber"
              onChange={(value) => setMobileNo({ target: { value } })}
              {...{ required: true, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
            />

            {/* <CardLabel>{`${t("EWASTE_ALT_MOBILE_NUMBER")}`}</CardLabel>
            <MobileNumber
              value={alternateNumber}
              name="alternateNumber"
              onChange={(value) => setAltMobileNo({ target: { value } })}
              {...{ required: false, pattern: "[6-9]{1}[0-9]{9}", type: "tel", title: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") }}
            /> */}


            <CardLabel>{`${t("EWASTE_LOCALITY")}`}</CardLabel>
            <Controller
              control={control}
              name={"locality"}
              defaultValue={locality}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={locality}
                  select={setLocality}
                  option={roughlocality}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            />

            <CardLabel>{`${t("EWASTE_COMPLETE_ADDRESS")}`}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="address"
              value={address}
              onChange={setaddress}
              style={{ width: "86%" }}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z-.`' ]*$",
                type: "text",
                title: t("EWASTE_ADDRESS_ERROR_MESSAGE"),
              })}
            />

            {/* <CardLabel>{`${t("EWASTE_VENDOR_NAME")}`}</CardLabel>
            <Controller
              control={control}
              name={"vendor"}
              defaultValue={vendor}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={vendor}
                  select={setVendor}
                  option={roughvendor}
                  optionKey="i18nKey"
                  t={t}
                />
              )}
            /> */}


          </div>

        </FormStep>
      </React.Fragment>
    );
  };

export default EWOwnerDetails;