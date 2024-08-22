import React from "react";
import { Loader } from "../atoms/Loader";
import {Dropdown} from "@egovernments/digit-ui-react-components";;
import { useTranslation } from "react-i18next";

const PropertyType = ({ selectLocality, keepNull, selected, optionCardStyles, style, disable, disableLoader }) => {
  const { t } = useTranslation();

  if (isLoading && !disableLoader) {
    return <Loader />;
  }
  const stateId = Digit.ULBService.getStateId();
  const { data: usageMenu = {}, isLoading } = Digit.Hooks.pt.usePropertyMDMS(stateId, "PropertyTax", [
    "UsageCategory",
    "OccupancyType",
    "Floor",
    "OwnerType",
    "OwnerShipCategory",
    "Documents",
    "SubOwnerShipCategory",
    "OwnerShipCategory",
  ]);
  let usagecat = [];
  usagecat = usageMenu?.PropertyTax?.UsageCategory?.filter((e) => e?.code !== "MIXED") || [];
  const usageCategoryMajorMenu = (usagecat) => {
    const catMenu = usagecat
      ?.filter((e) => e?.code.split(".").length <= 2 && e?.code !== "NONRESIDENTIAL")
      ?.map((item) => {
        const arr = item?.code.split(".");
        if (arr.length == 2) return {code: item?.code, name:item?.code, i18nKey: "PROPERTYTAX_BILLING_SLAB_" + arr[1],label:"PropertyType"};
        else return {  code: item?.code , name:item?.code, i18nKey: "PROPERTYTAX_BILLING_SLAB_" + item?.code,label:"PropertyType" };
      });
      console.log("catMenu", catMenu)
    return catMenu;
  };

  return (
    // <Dropdown
    //   option={usageCategoryMajorMenu(usagecat)}
    //   keepNull={keepNull === false ? false : true}
    //   selected={selected}
    //   select={selectLocality}
    //   optionCardStyles={optionCardStyles}
    //   optionKey="i18nkey"
    //   style={style}
    //   disable={false}
      
    // />
    <Dropdown
    selected={selected}
    disable={false}
    optionCardStyles={optionCardStyles}
    option={usageCategoryMajorMenu(usagecat)}
    select={selectLocality}
   
    style={style}
    optionKey="i18nKey"
    t={t}
  />
    
  );
  //  <h1>ABCD</h1>
};

export default PropertyType;