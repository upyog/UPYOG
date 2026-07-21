import React, { useState } from "react";
import { FormStep, RadioButtons, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import Timeline from "../components/NocTimeline";

const NocOwnerShipDetails = ({ t, config, onSelect, userType, formData }) => {
  const stateId = Digit.ULBService.getStateId();

  const [ownershipCategory, setOwnershipCategory] = useState(formData?.ownershipCategory);

  const { data: ownershipCategories, isLoading } = Digit.Hooks.useCustomMDMS(
    stateId,
    "common-masters",
    [{ name: "OwnerShipCategory" }],
    {
      select: (data) => {
        const categoryData = data?.["common-masters"]?.["OwnerShipCategory"] || [];
        return categoryData
          .filter((cat) => cat.active && cat.code.split(".").length === 2)
          .map((cat) => ({
            code: cat.code,
            i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_${cat.code.replaceAll(".", "_")}`,
          }));
      },
    }
  );

  const onSkip = () => onSelect();

  function goNext() {
    onSelect(config.key, ownershipCategory);
  }

  if (isLoading) {
    return <Loader />;
  }

  return (
    <React.Fragment>
      <Timeline currentStep={2} />
      <FormStep
        t={t}
        config={config}
        onSelect={goNext}
        onSkip={onSkip}
        isDisabled={!ownershipCategory}
      >
        <RadioButtons
          options={ownershipCategories || []}
          selectedOption={ownershipCategory}
          optionsKey="i18nKey"
          onSelect={setOwnershipCategory}
        />
      </FormStep>
    </React.Fragment>
  );
};

export default NocOwnerShipDetails;
