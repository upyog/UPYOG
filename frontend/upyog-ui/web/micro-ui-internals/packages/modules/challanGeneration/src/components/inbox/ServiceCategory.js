import React, { useState, useMemo, useEffect } from "react";
import { Loader, MultiSelectDropdown, RemoveableTag } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import ServiceCategoryCount from "./ServiceCategoryCount";

/**
 * ServiceCategory component:
 * - Multi-select dropdown for offence/service categories
 * - Fetches data from MDMS
 * - Supports selection and removal of categories
 */

const ServiceCategory = ({
  onAssignmentChange,
  searchParams,
  selectedCategory,
  setSearchParams,
  setselectedCategories,
  businessServices,
  clearCheck,
  setclearCheck,
}) => {
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  const [moreStatus, showMoreStatus] = useState(false);
  const { data: Menu, isLoading } = Digit.Hooks.mcollect.useMCollectMDMS(
    stateId,
    "BillingService",
    "BusinessService",
    "[?(@.type=='Adhoc' && @.isActive==true)]"
  );
  const { data: OffenceTypeData, isLoading: OffenceTypeLoading } = Digit.Hooks.useCustomMDMS(tenantId, "Challan", [{ name: "OffenceType" }]);


  let newMenu = [];
  const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
    if (searcher == "") return str;
    while (str.includes(searcher)) {
      str = str.replace(searcher, replaceWith);
    }
    return str;
  };

  OffenceTypeData?.Challan?.OffenceType &&
    OffenceTypeData?.Challan?.OffenceType?.map((ob) => {
      newMenu.push({ ...ob, i18nKey: ob.name });
    });

  const onRemove = (category) => {
    const newBusinessService = searchParams?.businessService?.filter((code) => code !== category.i18nKey);
    const newCategories = selectedCategory?.filter((item) => item.i18nKey !== category.i18nKey);

    setSearchParams({ ...searchParams, businessService: newBusinessService });
    setselectedCategories(newCategories);
  };

  let menuFirst = [];
  let meuSecond = [];
  Menu?.map((option, index) => {
    if (index < 5) menuFirst.push(option);
    else meuSecond.push(option);
  });

  return (
    <div className="status-container">
      <div className="filter-label cg-font-normal">
        {t("CHALLAN_OFFENCE_TYPE")}
      </div>
      <MultiSelectDropdown
        className="form-field"
        isMandatory={true}
        defaultUnit="Selected"
        selected={selectedCategory}
        options={newMenu}
        onSelect={(selectedItems) => {
          const filterParam = selectedItems?.map((item) => item?.[1]?.i18nKey);

          const selectedCategory = selectedItems?.map((item) => ({
            code: item?.[1]?.id,
            i18nKey: item?.[1]?.i18nKey,
          }));

          // Update parent states
          setSearchParams({ ...searchParams, businessService: filterParam });
          setselectedCategories(selectedCategory);
        }}
        optionsKey="i18nKey"
        t={t}
      />
      <div className="tag-container">
        {selectedCategory?.map((value, index) => (
          <div>
            <RemoveableTag key={index} text={`${t(value["i18nKey"])}`} onClick={() => onRemove(value)} />
          </div>
        ))}
      </div>
    </div>
  );
};

export default ServiceCategory;
