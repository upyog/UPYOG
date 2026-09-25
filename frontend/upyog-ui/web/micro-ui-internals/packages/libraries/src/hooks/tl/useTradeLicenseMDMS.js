import { MdmsServiceV2 } from "../../services/elements/MDMSV2";
import { queryTemplate } from "../../common/queryTemplate";

const useTradeLicenseMDMS = (tenantId, moduleCode, type, filter, config = {}) => {
  const useTLDocuments = () => {
    return queryTemplate({ queryKey: ["TL_DOCUMENTS"], queryFn: () => MdmsServiceV2.getTLDocumentRequiredScreen(tenantId, moduleCode, type), config });
  };
  const useStructureType = () => {
    return queryTemplate({ queryKey: ["TL_STRUCTURE_TYPE"], queryFn: () => MdmsServiceV2.getTLStructureType(tenantId, moduleCode, type), config });
  };
  const useTradeUnitsData = () => {
    return queryTemplate({ queryKey: ["TL_TRADE_UNITS"], queryFn: () => MdmsServiceV2.getTradeUnitsData(tenantId, moduleCode, type, filter), config });
  };
  const useTradeOwnerShipCategory = () => {
    return queryTemplate({ queryKey: ["TL_TRADE_OWNERSHIP_CATEGORY"], queryFn: () => MdmsServiceV2.GetTradeOwnerShipCategory(tenantId, moduleCode, type), config });
  };
  const useTradeOwnershipSubType = () => {
    return queryTemplate({
      queryKey: ["TL_TRADE_OWNERSHIP_CATEGORY"],
      queryFn: () => MdmsServiceV2.GetTradeOwnerShipCategory(tenantId, moduleCode, type),
      select: (data) => {
        const { "common-masters": { OwnerShipCategory: categoryData } = {} } = data;
        return categoryData.filter((e) => e.code.includes(filter.keyToSearchOwnershipSubtype)).map((e) => ({ ...e, i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_${e.code.replaceAll(".", "_")}` }));
      },
      config,
    });
  };
  const useOwnerTypeWithSubtypes = () => {
    return queryTemplate({
      queryKey: ["TL_TRADE_OWNERSSHIP_TYPE"],
      queryFn: () => MdmsServiceV2.GetTradeOwnerShipCategory(tenantId, moduleCode, type),
      select: (data) => {
        const { "common-masters": { OwnerShipCategory: categoryData } = {} } = data;
        let OwnerShipCategory = {};
        let ownerShipdropDown = [];

        function getDropdwonForProperty(ownerShipdropDown) {
          if (filter?.userType === "employee") {
            const arr = ownerShipdropDown
              ?.filter((e) => e.code.split(".").length <= 2)
              ?.map((ownerShipDetails) => ({
                ...ownerShipDetails,
                i18nKey: `COMMON_MASTERS_OWNERSHIPCATEGORY_INDIVIDUAL_${ownerShipDetails.value.split(".")[1] ? ownerShipDetails.value.split(".")[1] : ownerShipDetails.value.split(".")[0]}`,
              }));
            return arr.filter((data) => data.code.includes("INDIVIDUAL") || data.code.includes("OTHER"));
          }
          const res = ownerShipdropDown?.length
            ? ownerShipdropDown
                ?.map((ownerShipDetails) => ({
                  ...ownerShipDetails,
                  i18nKey: `PT_OWNERSHIP_${ownerShipDetails.value.split(".")[1] ? ownerShipDetails.value.split(".")[1] : ownerShipDetails.value.split(".")[0]}`,
                }))
                .reduce((acc, ownerShipDetails) => {
                  if (ownerShipDetails.code.includes("INDIVIDUAL")) {
                    return [...acc, ownerShipDetails];
                  } else if (ownerShipDetails.code.includes("OTHER")) {
                    const { code, value, ...everythingElse } = ownerShipDetails;
                    return [...acc, { code: code.split(".")[0], value: value.split(".")[0], ...everythingElse }];
                  } else {
                    return acc;
                  }
                }, [])
            : null;
          return res;
        }

        function formDropdown(category) {
          return { label: category.name, value: category.code, code: category.code };
        }

        categoryData.length > 0 ? categoryData?.map((category) => { OwnerShipCategory[category.code] = category; }) : null;

        if (OwnerShipCategory) {
          Object.keys(OwnerShipCategory)?.forEach((category) => {
            ownerShipdropDown.push(formDropdown(OwnerShipCategory[category]));
          });
        }

        return getDropdwonForProperty(ownerShipdropDown);
      },
      config,
    });
  };
  const useTLAccessoriesType = () => {
    return queryTemplate({ queryKey: ["TL_TRADE_ACCESSORY_CATEGORY"], queryFn: () => MdmsServiceV2.getTLAccessoriesType(tenantId, moduleCode, type), config });
  };
  const useTLFinancialYear = () => {
    return queryTemplate({ queryKey: ["TL_TRADE_FINANCIAL_YEAR"], queryFn: () => MdmsServiceV2.getTLFinancialYear(tenantId, moduleCode, type), config });
  };
  const _default = () => {
    return queryTemplate({ queryKey: [tenantId, moduleCode, type], queryFn: () => MdmsServiceV2.getMultipleTypes(tenantId, moduleCode, type), config });
  };

  switch (type) {
    case "TLDocuments":
      return useTLDocuments();
    case "StructureType":
      return useStructureType();
    case "TradeUnits":
      return useTradeUnitsData();
    case "TLOwnerShipCategory":
      return useTradeOwnerShipCategory();
    case "TLOwnerTypeWithSubtypes":
      return useOwnerTypeWithSubtypes();
    case "AccessoryCategory":
      return useTLAccessoriesType();
    case "FinancialYear":
      return useTLFinancialYear();
    case "TradeOwnershipSubType":
      return useTradeOwnershipSubType();
    default:
      return _default();
  }
};

export default useTradeLicenseMDMS;
