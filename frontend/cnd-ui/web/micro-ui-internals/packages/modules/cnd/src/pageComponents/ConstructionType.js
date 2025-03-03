import { CardLabel, FormStep,RadioButtons } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";


const ConstructionType =({t, config, onSelect, userType, formData}) => {
  const [constructionType, setconstructionType] = useState(formData?.constructionType?.constructionType || "");

  const common = [
      {
        code: "RENOVATION",
        i18nKey: "RENOVATION",
        value: "RENOVATION"
      },
      {
        code: "OLD_BUILDING_DEMOLITION",
        i18nKey: "OLD_BUILDING_DEMOLITION",
        value: "Old Building Demolition"
      },
      {
        code: "NEW_CONSTRUCTION",
        i18nKey: "NEW_CONSTRUCTION",
        value: "New Construction"
      }
    ]


    const goNext = () => {
      let type = formData.constructionType;
      let constructionTypeStep = { ...type, constructionType };
      onSelect(config.key, { ...formData[config.key], ...constructionTypeStep }, false);
    };

    return(
      <React.Fragment>
          <FormStep
          config={config}
          onSelect={goNext}
          t={t}
          isDisabled={!constructionType}
          >
          <div>
              <RadioButtons
                  t={t}
                  options={common}
                  optionsKey="i18nKey"
                  name={constructionType}
                  value={constructionType}
                  selectedOption={constructionType}
                  onSelect={setconstructionType}
                  labelKey="i18nKey"
                  isPTFlow={true}
              />
          </div>

          </FormStep>
      </React.Fragment>
    )



}

export default ConstructionType;