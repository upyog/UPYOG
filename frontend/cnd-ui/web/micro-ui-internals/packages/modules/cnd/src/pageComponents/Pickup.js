import { CardLabel, FormStep,RadioButtons,CitizenInfoLabel } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import LocationPopup from "../components/LocationPopup";


const Pickup =({t, config, onSelect, userType, formData}) => {
  console.log("tefgsvbdfvsgf",formData);
  const [requestType, setRequestType] = useState(formData?.requestType?.requestType || "");
  const [showLocationPopup, setShowLocationPopup] = useState(false);

  const common = [
      {
        code: "REQ_PICK_UP",
        i18nKey: "REQ_PICK_UP",
        value: "Request For Pickup"
      },
      {
        code: "DEP_DIRECT_CENTRE",
        i18nKey: "DEP_DIRECT_CENTRE",
        value: "Deposit Direct To Centre"
      }
    ]


    const goNext = () => {
      let type = formData.requestType;
      let typeStep = { ...type, requestType };
      onSelect(config.key, { ...formData[config.key], ...typeStep }, false);
    };

    // Handle selection change
  const handleSelection = (selected) => {
    setRequestType(selected);
    if (selected.code === "DEP_DIRECT_CENTRE") {
      setShowLocationPopup(true);
    } else {
      setShowLocationPopup(false);
    }
  };



    return(
      <React.Fragment>
          <FormStep
          config={config}
          onSelect={goNext}
          t={t}
          isDisabled={!requestType}
          >
          <div>
              {/* <CardLabel>{`${t("CND_REQUEST_TYPE")}`} <span className="astericColor">*</span></CardLabel> */}
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
        <LocationPopup
          t={t}
          closeModal={() => setShowLocationPopup(false)}
          actionCancelOnSubmit={() => setShowLocationPopup(false)}
        />
      )}
      <CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t(`CND_INFO`)} className={"info-banner-wrap-citizen-override"}/>
      </React.Fragment>
    )



}

export default Pickup;