import { CardLabel, CardLabelDesc, Dropdown, FormStep, UploadFile } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { stringReplaceAll } from "../utils";
import Timeline from "../components/TLTimeline";

const UsageCategoryVacantLand = ({ t, config, onSelect, userType, formData }) => {
  //let index = window.location.href.charAt(window.location.href.length - 1);
  const { pathname: url } = useLocation();
  const isMutation = url.includes("property-mutation");

  let index = window.location.href.split("/").pop();
  
  const [error, setError] = useState(null);
  const cityDetails = Digit.ULBService.getCurrentUlb();
  const isUpdateProperty = formData?.isUpdateProperty || false;
  const isEditProperty = formData?.isEditProperty || false;
  const [UsageCategory, setUsageCategory] = useState(
    formData?.usageCategory ? {i18nKey: `PROPERTYTAX_${formData.usageCategory?.code}`,code: formData.usageCategory.code} : ''
  );

  const { data: mdmsData, isLoading } = Digit.Hooks.useCommonMDMS(
    Digit.ULBService.getStateId(),
    "PropertyTax",
    [ "UsageCategory","VacantLandUsageCategory"],
    {
      select: (data) => {
        // let usageCategory = data?.PropertyTax?.UsageCategory?.map((category) => getUsageCategory(category.code))
        //   .filter(
        //     (category) => category.usageCategoryDetail === false && category.usageCategorySubMinor === false && category.usageCategoryMinor !== false
        //   )
        //   .map((category) => ({ code: category.usageCategoryMinor, i18nKey: `PROPERTYTAX_BILLING_SLAB_${category.usageCategoryMinor}` }));
        // let subCategory = Digit.Utils.getUnique(
        //   data?.PropertyTax?.UsageCategory.map((e) => getUsageCategory(e.code))
        //     .filter((e) => e.usageCategoryDetail)
        //     .map((e) => ({
        //       code: e.usageCategoryDetail,
        //       i18nKey: `PROPERTYTAX_BILLING_SLAB_${e.usageCategoryDetail}`,
        //       usageCategorySubMinor: e.usageCategorySubMinor,
        //       usageCategoryMinor: e.usageCategoryMinor,
        //     }))
        // );

        return {
          UsageCategory: data?.PropertyTax?.UsageCategory?.filter((category) => category.active)?.map((category) => ({
            i18nKey: `PROPERTYTAX_${category.code}`,
            code: category.code,
          })),
          VacantLandUsageCategory: data?.PropertyTax?.VacantLandUsageCategory?.filter((category) => category.active)?.map((category) => ({
            i18nKey: `PROPERTYTAX_${category.code}`,
            code: category.code,
          })),
          usageDetails: data?.PropertyTax?.UsageCategory,
        };
      },
      retry: false,
      enable: false,
    }
  );
  


  const handleSubmit = () => {
    sessionStorage.setItem("UsageCategory", UsageCategory?.i18nKey);
    onSelect(config.key, UsageCategory);
  };
  const onSkip = () => onSelect();

  
  function selectUsageCategory(value) {
    setUsageCategory(value);
  }

  return (
    <React.Fragment>
      {window.location.href.includes("/citizen") ? <Timeline currentStep={1} /> : null}
      <FormStep
        config={config}
        onSelect={handleSubmit}
        onSkip={onSkip}
        t={t}
        isDisabled={ !UsageCategory}
      >
        <CardLabel>{`${t("PT_FORM2_USAGE_TYPE")}*`}</CardLabel>
        <Dropdown
        t={t}
        optionKey="i18nKey"
        isMandatory={config.isMandatory}
        option={[
            ...(formData?.PropertyType?.i18nKey !== "COMMON_PROPTYPE_VACANT" ? (mdmsData?.UsageCategory ? mdmsData?.UsageCategory : []) : mdmsData?.VacantLandUsageCategory ? mdmsData?.VacantLandUsageCategory : []),
        ]}
        selected={UsageCategory}
        select={selectUsageCategory}
        />
      </FormStep>
    </React.Fragment>
  );
};

export default UsageCategoryVacantLand;
