import { FormStep, RadioButtons, CitizenInfoLabel } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import LocationPopup from "../components/LocationPopup";
import { CND_VARIABLES } from "../utils";

/**
* RequestPickup component that handles waste collection request types.
* Allows users to select a pickup type, shows location popup for direct 
* center deposits, and manages form state within a multi-step form flow.
* Fetches pickup types from MDMS and conditionally disables navigation.
*/

const RequestPickup = ({ t, config, onSelect, userType, formData }) => {
  const [requestType, setRequestType] = useState(formData?.requestType?.requestType || "");
  const [showLocationPopup, setShowLocationPopup] = useState(false);
  const [isNextDisabled, setIsNextDisabled] = useState(false);

  const { data: PickupData } = Digit.Hooks.useEnabledMDMS(Digit.ULBService.getStateId(), CND_VARIABLES.MDMS_MASTER, [{ name: "PickupType" }], {
    select: (data) => {
      const formattedData = data?.[CND_VARIABLES.MDMS_MASTER]?.["PickupType"];
      return formattedData?.filter((item) => item.active === true);
    },
  });
  let common = PickupData?.map((pickup) => ({ i18nKey: pickup.code, code: pickup.code, value: pickup.code })) || [];
  const goNext = () => {
    let typeStep = { ...formData.requestType, requestType };
    onSelect(config.key, { ...formData[config.key], ...typeStep }, false);
  };
  // Handle selection change
  const handleSelection = (selected) => {
    setRequestType(selected);
    if (selected.code === "DEP_DIRECT_CENTRE") {
      setShowLocationPopup(true);
      setIsNextDisabled(true); // Disable the Next button
    } else {
      setShowLocationPopup(false);
      setIsNextDisabled(false); // Enable the Next button
    }
  };

  return (
    <React.Fragment>
      <FormStep config={config} onSelect={goNext} t={t} isDisabled={!requestType || isNextDisabled}>
        <div>
          <RadioButtons
            t={t}
            options={common}
            optionsKey="i18nKey"
            name={requestType}
            value={requestType}
            selectedOption={requestType}
            onSelect={handleSelection}
            labelKey="i18nKey"
            isPTFlow={true}
          />
        </div>
      </FormStep>
      {showLocationPopup && (
        <LocationPopup t={t} closeModal={() => setShowLocationPopup(false)} actionCancelOnSubmit={() => setShowLocationPopup(false)} />
      )}
      <CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t(`CND_INFO`)} className={"info-banner-wrap-citizen-override"} />
    </React.Fragment>
  );
};

export default RequestPickup;
