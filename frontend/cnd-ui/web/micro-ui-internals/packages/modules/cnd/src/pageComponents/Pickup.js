import { CardLabel, FormStep,RadioButtons } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";


const Pickup =({t, config, onSelect, userType, formData}) => {
  console.log("gfcgfc gfcgfcfcfgcgf",formData);
  const [requestType, setRequestType] = useState(formData?.requestType?.requestType || "");

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
                  onSelect={setRequestType}
                  labelKey="i18nKey"
                  isPTFlow={true}
              />
          </div>

          </FormStep>
      </React.Fragment>
    )



}

export default Pickup;