import { CardLabel, FormStep, LabelFieldPair, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import Timeline from "../components/EWASTETimeline";

const EWASTECitizenAddress = ({ t, config, onSelect, userType, formData, formState, setError, clearErrors }) => {
  const onSkip = () => onSelect();
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger } = useForm();
  const formValue = watch();

  const [street, setStreet] = useState(formData?.address?.street || "");
  const [addressLine1, setAddressLine1] = useState(formData?.address?.addressLine1 || "");
  const [addressLine2, setAddressLine2] = useState(formData?.address?.addressLine2 || "");
  const [landmark, setLandmark] = useState(formData?.address?.landmark || "");
  const [buildingName, setBuildingName] = useState(formData?.address?.latitude || "");
  const [doorNo, setDoorNo] = useState(formData?.address?.doorNo || "");

  useEffect(() => {
    trigger();
  }, []);

  useEffect(() => {
    const keys = Object.keys(formValue);
    const part = {};
    keys.forEach((key) => (part[key] = formData[config.key]?.[key]));

    if (!_.isEqual(formValue, part)) {
      onSelect(config.key, { ...formData[config.key], ...formValue });
      trigger();
    }
  }, [formValue]);

  const selectStreet = (e) => setStreet(e.target.value);
  const selectDoorNo = (e) => setDoorNo(e.target.value);
  const selectBuilding = (e) => setBuildingName(e.target.value);
  const selectLandmark = (e) => setLandmark(e.target.value);
  const selectAddressLine1 = (e) => setAddressLine1(e.target.value);
  const selectAddressLine2 = (e) => setAddressLine2(e.target.value);


  const goNext = () => {
    let owner = formData.address;
    let ownerStep;
    if (userType === "citizen") {
      ownerStep = { ...owner, street, addressLine1, addressLine2, landmark, buildingName, doorNo };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
    } else {
      ownerStep = { ...owner, street, addressLine1, addressLine2, landmark, buildingName, doorNo };
      onSelect(config.key, ownerStep, false);
    }
  };

  useEffect(() => {
    if (userType === "citizen") {
      goNext();
    }
  }, [street, addressLine1, addressLine2, landmark, buildingName, doorNo]);

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
      <FormStep
        config={{ ...config }}
        onSelect={goNext}
        onSkip={onSkip}
        isDisabled={addressLine1 == "" || doorNo == ""}
        t={t}
      >
        <CardLabel>{`${t("EWASTE_STREET_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="street"
          onChange={selectStreet}
          value={street}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}
        />
        <CardLabel>{`${t("EWASTE_HOUSE_NO")}`}<span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="doorNo"
          onChange={selectDoorNo}
          value={doorNo}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}

        />
        <CardLabel>{`${t("EWASTE_HOUSE_NAME")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="buildingName"
          onChange={selectBuilding}
          value={buildingName}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}

        />
        <CardLabel>{`${t("EWASTE_ADDRESS_LINE1")}`}<span style={{ color: 'red' }}>*</span></CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="addressLine1"
          onChange={selectAddressLine1}
          value={addressLine1}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}
        />
        <CardLabel>{`${t("EWASTE_ADDRESS_LINE2")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="addressLine2"
          onChange={selectAddressLine2}
          value={addressLine2}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}

        />
        <CardLabel>{`${t("EWASTE_landmark")}`}</CardLabel>
        <TextInput
          t={t}
          type={"text"}
          optionKey="i18nKey"
          name="landmark"
          onChange={selectLandmark}
          value={landmark}
          errorStyle={false}
          autoFocus={focusIndex?.index == 1}
        />
      </FormStep>
    </React.Fragment>
  );
};

export default EWASTECitizenAddress;
