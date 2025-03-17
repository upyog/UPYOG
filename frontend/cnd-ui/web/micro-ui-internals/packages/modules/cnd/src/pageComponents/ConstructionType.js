import { FormStep, RadioButtons } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { CND_VARIABLES } from "../utils";


/**
 * Component for selecting construction type in a form step
 */
const ConstructionType = ({ t, config, onSelect, userType, formData }) => {
  const [constructionType, setconstructionType] = useState(formData?.constructionType?.constructionType || "");

  /* Fetch construction type master data and filter for active items only */
  const { data: ConstructionType } = Digit.Hooks.useCustomMDMS(
    Digit.ULBService.getStateId(),
    CND_VARIABLES.MDMS_MASTER,
    [{ name: "ConstructionType" }],
    {
      select: (data) => {
        const formattedData = data?.[CND_VARIABLES.MDMS_MASTER]?.["ConstructionType"];
        return formattedData?.filter((item) => item.active === true);
      },
    }
  );

  let common = ConstructionType?.map((construction) => 
  ({ i18nKey: construction.code, code: construction.code, value: construction.code })) 
  || [];

  const goNext = () => {
    let constructionTypeStep = { ...formData.constructionType, constructionType };
    onSelect(config.key, { ...formData[config.key], ...constructionTypeStep }, false);
  };

  return (
    <React.Fragment>
      <FormStep config={config} onSelect={goNext} t={t} isDisabled={!constructionType}>
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
  );
};

export default ConstructionType;
