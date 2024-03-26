//HAVE TO CHANGE THI
import { stringReplaceAll } from "@egovernments/digit-ui-module-pt/src/utils";
import { ApiCacheService } from "../atoms/ApiCacheService";
import Urls from "../atoms/urls";
import { Request, ServiceRequest } from "../atoms/Utils/Request";
import { PersistantStorage } from "../atoms/Utils/Storage";

// export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
//   if (searcher == "") return str;
//   while (str.includes(searcher)) {
//     str = str.replace(searcher, replaceWith);
//   }
//   return str;
// };

const SortByName = (na, nb) => {
  if (na < nb) {
    return -1;
  }
  if (na > nb) {
    return 1;
  }
  return 0;
};

const GetCitiesWithi18nKeys = (MdmsRes, moduleCode) => {
  const cityList = (MdmsRes.tenant.citymodule && MdmsRes.tenant.citymodule.find((module) => module.code === moduleCode).tenants) || [];
  const citiesMap = cityList.map((city) => city.code);
  const cities = MdmsRes.tenant.tenants
    .filter((city) => citiesMap.includes(city.code))
    .map(({ code, name, logoId, emailId, address, contactNumber }) => ({
      code,
      name,
      logoId,
      emailId,
      address,
      contactNumber,
      i18nKey: "TENANT_TENANTS_" + code.replace(".", "_").toUpperCase(),
    }))
    .sort((cityA, cityB) => {
      const na = cityA.name.toLowerCase(),
        nb = cityB.name.toLowerCase();
      return SortByName(na, nb);
    });
  return cities;
};

const initRequestBody = (tenantId) => ({
  MdmsCriteria: {
    tenantId,
    moduleDetails: [
      {
        moduleName: "common-masters",
        masterDetails: [{ name: "Department" }, { name: "Fund" }, { name: "Chapter" }, { name: "Designation" }, { name: "StateInfo" }, { name: "wfSlaConfig" }, { name: "uiHomePage" }],
      },
      {
        moduleName: "tenant",
        masterDetails: [{ name: "tenants" }, { name: "citymodule" }],
      },
      {
        moduleName: "DIGIT-UI",
        masterDetails: [{ name: "ApiCachingSettings" }],
      },
    ],
  },
});

const getCriteria = (tenantId, moduleDetails) => {
  return {
    MdmsCriteria: {
      tenantId,
      ...moduleDetails,
    },
  };
};

export const getGeneralCriteria = (tenantId, moduleCode, type) => ({
  details: {
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: type,
          },
        ],
      },
    ],
  },
});

export const getMultipleTypes = (tenantId, moduleCode, types) => ({
  details: {
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: types.map((type) => ({ name: type })),
      },
    ],
  },
});
export const getMultipleTypesWithFilter = (moduleCode, masterDetails) => ({
  details: {
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: masterDetails,
      },
    ],
  },
});

const getReceiptKey = (tenantId, moduleCode) => ({
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "uiCommonPay",
          },
        ],
      },
    ],
  },
});

const getBillsGenieKey = (tenantId, moduleCode) => ({
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "BusinessService",
          },
        ],
      },
      {
        moduleName: "tenant",
        masterDetails: [{ name: "tenants" }, { name: "citymodule" }],
      },
      {
      moduleName: "common-masters",
      masterDetails: [{name: "uiCommonPay"}]
      }
    ],
  },
});

const getModuleServiceDefsCriteria = (tenantId, moduleCode) => ({
  type: "serviceDefs",
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: `RAINMAKER-${moduleCode}`,
        masterDetails: [
          {
            name: "ServiceDefs",
          },
        ],
      },
    ],
  },
});

const getSanitationTypeCriteria = (tenantId, moduleCode) => ({
  type: "SanitationType",
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "SanitationType",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getPitTypeCriteria = (tenantId, moduleCode) => ({
  type: "PitType",
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "PitType",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getApplicationChannelCriteria = (tenantId, moduleCode) => ({
  type: "ApplicationChannel",
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "ApplicationChannel",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getPropertyTypeCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "PropertyType",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getPropertyUsageCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "PropertyType",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getCommonFieldsCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "CommonFieldsConfig",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getPreFieldsCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "PreFieldsConfig",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getPostFieldsCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "PostFieldsConfig",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getConfig = (tenantId, moduleCode) => ({
  type: "Config",
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "Config",
          },
        ],
      },
    ],
  },
});

const getVehicleTypeCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "VehicleMakeModel",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getChecklistCriteria = (tenantId, moduleCode) => ({
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "CheckList",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getSlumLocalityCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "Slum",
          },
        ],
      },
    ],
  },
});
const getPropertyOwnerTypeCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "OwnerType" }],
      },
    ],
  },
});

const getSubPropertyOwnerShipCategoryCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "SubOwnerShipCategory" }],
      },
    ],
  },
});
const getPropertyOwnerShipCategoryCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "OwnerShipCategory" }],
      },
    ],
  },
});

const getTradeOwnerShipCategoryCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "OwnerShipCategory" }],
      },
    ],
  },
});

const getDocumentRequiredScreenCategory = (tenantId, moduleCode) => ({
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "Documents",
          },
        ],
      },
    ],
  },
});

const getDefaultMapConfig = (tenantId, moduleCode) => ({
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "MapConfig",
          },
        ],
      },
    ],
  },
});

const getUsageCategoryList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "UsageCategory" }],
      },
    ],
  },
});

const getPTPropertyTypeList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "PropertyType" }],
      },
    ],
  },
});

const getTLStructureTypeList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "StructureType" }],
      },
    ],
  },
});

const getTLAccessoriesTypeList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "AccessoriesCategory" }],
      },
    ],
  },
});

const getTLFinancialYearList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "FinancialYear", filter: `[?(@.module == "TL")]` }],
      },
    ],
  },
});

const getPTFloorList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "Floor" }],
      },
    ],
  },
});

const getReasonCriteria = (tenantId, moduleCode, type, payload) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: payload.map((mdmsLoad) => ({
          name: mdmsLoad,
        })),
      },
    ],
  },
});

const getBillingServiceForBusinessServiceCriteria = (filter) => ({
  moduleDetails: [
    {
      moduleName: "BillingService",
      masterDetails: [
        { name: "BusinessService", filter },
        {
          name: "TaxHeadMaster",
        },
        {
          name: "TaxPeriod",
        },
      ],
    },
  ],
});

const getRoleStatusCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "RoleStatusMapping",
            filter: null,
          },
        ],
      },
    ],
  },
});
const getRentalDetailsCategoryCriteria = (tenantId, moduleCode) => ({
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "RentalDetails",
          },
        ],
      },
    ],
  },
});

const getChargeSlabsCategoryCriteria = (tenantId, moduleCode) => ({
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "ChargeSlabs",
          },
        ],
      },
    ],
  },
});

const getGenderTypeList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "GenderType",
          },
        ],
      },
    ],
  },
});
const getDepartmentList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "Department",
          },
        ],
      },
    ],
  },
});
const getFundList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "Fund",
          },
        ],
      },
    ],
  },
});
const getChapterList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "Chapter",
          },
        ],
      },
    ],
  },
});
const getUnitList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "Unit",
          },
        ],
      },
    ],
  },
});
const getProjectList = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "Project",
          },
        ],
      },
    ],
  },
});
const getMeterStatusTypeList = (tenantId) => ({
    moduleDetails: [
      {
        moduleName: "ws-services-calculation",
        masterDetails: [
          {
            name: "MeterStatus",
            filter: `$.*.name`
          },
        ],
      },
    ],

});

const getBillingPeriodValidation = (tenantId) => ({
    moduleDetails: [
      {
        moduleName: "ws-services-masters",
        masterDetails: [
          {
            name: "billingPeriod",
            filter: "*"
          },
        ],
      },
    ],
});

const getDssDashboardCriteria = (tenantId, moduleCode) => ({
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "dashboard-config",
          },
        ],
      },
    ],
  },
});

const getMCollectBillingServiceCriteria = (tenantId, moduleCode, type, filter) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "BusinessService", filter: filter }],
      },
    ],
  },
});

const getTradeUnitsDataList = (tenantId, moduleCode, type, filter) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "TradeType", filter: filter }],
      },
    ],
  },
});


const getMCollectApplicationStatusCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "applicationStatus" }],
      },
    ],
  },
});

const getHrmsEmployeeRolesandDesignations = () => ({
  moduleDetails: [
    {
      moduleName: "common-masters",
      masterDetails: [
        { name: "Department", filter: "[?(@.active == true)]" },
        { name: "Designation", filter: "[?(@.active == true)]" },
      ],
    },
    {
      moduleName: "tenant",
      masterDetails: [{ name: "tenants" }],
    },
    {
      moduleName: "ACCESSCONTROL-ROLES",
      masterDetails: [{ name: "roles", filter: "$.[?(@.code!='CITIZEN')]" }],
    },
    { moduleName: "egov-location", masterDetails: [{ name: "TenantBoundary" }] },
  ],
});
const getFSTPPlantCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "FSTPPlantInfo" }],
      },
    ],
  },
});
const getCancelReceiptReason = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "CancelReceiptReason" }],
      },
    ],
  },
});
const getReceiptStatus = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "ReceiptStatus" }],
      },
    ],
  },
});
const getCancelReceiptReasonAndStatus = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [{ name: "ReceiptStatus" }, { name: "uiCommonPay" }],
      },
    ],
  },
});

const getDocumentTypesCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "DocTypeMapping",
          },
        ],
      },
    ],
  },
});

const getTradeTypeRoleCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "TradeTypetoRoleMapping",
          },
        ],
      },
    ],
  },
});

const getFSTPORejectionReasonCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "FSTPORejectionReason",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getFSMPaymentTypeCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "PaymentType",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getFSMTripNumberCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "TripNumber",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getFSMReceivedPaymentTypeCriteria = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: "ReceivedPaymentType",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getWSTaxHeadMasterCritera = (tenantId, moduleCode, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: "BillingService",
        masterDetails: [
          {
            name: "TaxHeadMaster",
            filter: null,
          },
        ],
      },
    ],
  },
});

const getHowItWorksJSON = (tenantId) => ({
      moduleDetails: [
      {
        moduleName: "common-masters",
        masterDetails: [
          {
            name: "howItWorks",
          },
        ],
      },
    ],
});

const getFAQsJSON = (tenantId) => ({
  moduleDetails: [
  {
    moduleName: "common-masters",
    masterDetails: [
      {
        name: "faqs",
      },
    ],
  },
],
});
const getDSSFAQsJSON = (tenantId) => ({
  moduleDetails: [
  {
    moduleName: "dss-dashboard",
    masterDetails: [
      {
        name: "FAQs",
      },
    ],
  },
],
});
const getDSSAboutJSON = (tenantId) => ({
  moduleDetails: [
  {
    moduleName: "dss-dashboard",
    masterDetails: [
      {
        name: "About",
      },
    ],
  },
],
});

const getStaticData = () => ({
  moduleDetails: [
    {
      moduleName: "common-masters",
      masterDetails: [
        {
          name: "StaticData",
        },
      ],
    },
  ],
});

const GetEgovLocations = (MdmsRes) => {
  return MdmsRes["egov-location"].TenantBoundary[0].boundary.children.map((obj) => ({
    name: obj.localname,
    i18nKey: obj.localname,
  }));
};

const GetServiceDefs = (MdmsRes, moduleCode) => MdmsRes[`RAINMAKER-${moduleCode}`].ServiceDefs.filter((def) => def.active);

const GetSanitationType = (MdmsRes) => MdmsRes["FSM"].SanitationType.filter((type) => type.active);

const GetPitType = (MdmsRes) =>
  MdmsRes["FSM"].PitType.filter((item) => item.active).map((type) => ({ ...type, i18nKey: `PITTYPE_MASTERS_${type.code}` }));

const GetApplicationChannel = (MdmsRes) =>
  MdmsRes["FSM"].ApplicationChannel.filter((type) => type.active).map((channel) => ({
    ...channel,
    i18nKey: `ES_APPLICATION_DETAILS_APPLICATION_CHANNEL_${channel.code}`,
  }));

const GetPropertyType = (MdmsRes) =>
  MdmsRes["FSM"].PropertyType.filter((property) => property.active && !property.propertyType).map((item) => ({
    ...item,
    i18nKey: `PROPERTYTYPE_MASTERS_${item.code}`,
    code: item.code,
  }));

const GetPropertySubtype = (MdmsRes) =>
  MdmsRes["FSM"].PropertyType.filter((property) => property.active && property.propertyType).map((item) => ({
    ...item,
    i18nKey: `PROPERTYTYPE_MASTERS_${item.code}`,
    code: item.code,
  }));

const GetVehicleType = (MdmsRes) =>
  MdmsRes["Vehicle"].VehicleMakeModel.filter((vehicle) => vehicle.active)
    .filter((vehicle) => vehicle.make)
    .map((vehicleDetails) => {
      return {
        ...vehicleDetails,
        i18nKey: `COMMON_MASTER_VEHICLE_${vehicleDetails.code}`,
      };
    });

const GetVehicleMakeModel = (MdmsRes) =>
  MdmsRes["Vehicle"].VehicleMakeModel.filter((vehicle) => vehicle.active)
    .map((vehicleDetails) => {
      return {
        ...vehicleDetails,
        i18nKey: `COMMON_MASTER_VEHICLE_${vehicleDetails.code}`,
      };
  });

const GetSlumLocalityMapping = (MdmsRes, tenantId) =>
  MdmsRes["FSM"].Slum.filter((type) => type.active).reduce((prev, curr) => {
    return prev[curr.locality]
      ? {
          ...prev,
          [curr.locality]: [
            ...prev[curr.locality],
            {
              ...curr,
              i18nKey: `${tenantId.toUpperCase().replace(".", "_")}_${curr.locality}_${curr.code}`,
            },
          ],
        }
      : {
          ...prev,
          [curr.locality]: [
            {
              ...curr,
              i18nKey: `${tenantId.toUpperCase().replace(".", "_")}_${curr.locality}_${curr.code}`,
            },
          ],
        };
  }, {});

const GetPropertyOwnerShipCategory = (MdmsRes) =>
  MdmsRes["PropertyTax"].OwnerShipCategory.filter((ownerShip) => ownerShip.active).map((ownerShipDetails) => {
    return {
      ...ownerShipDetails,
      i18nKey: `COMMON_MASTER_OWNER_TYPE_${ownerShipDetails.code}`,
    };
  });

const GetTradeOwnerShipCategory = (MdmsRes) =>
  MdmsRes["common-masters"].OwnerShipCategory.filter((ownerShip) => ownerShip.active).map((ownerShipDetails) => {
    return {
      ...ownerShipDetails,
      i18nKey: `COMMON_MASTER_OWNER_TYPE_${ownerShipDetails.code}`,
    };
  });

const GetPropertyOwnerType = (MdmsRes) =>
  MdmsRes["PropertyTax"].OwnerType.filter((owner) => owner.active).map((ownerDetails) => {
    return {
      ...ownerDetails,
      i18nKey: `PROPERTYTAX_OWNERTYPE_${ownerDetails.code}`,
    };
  });

const getSubPropertyOwnerShipCategory = (MdmsRes) => {
  MdmsRes["PropertyTax"].SubOwnerShipCategory.filter((category) => category.active).map((subOwnerShipDetails) => {
    return {
      ...subOwnerShipDetails,
      i18nKey: `PROPERTYTAX_BILLING_SLAB_${subOwnerShipDetails.code}`,
    };
  });
  sessionStorage.setItem("getSubPropertyOwnerShipCategory", JSON.stringify(MdmsRes));
};

const getDocumentRequiredScreen = (MdmsRes) => {
  MdmsRes["PropertyTax"].Documents.filter((Documents) => Documents.active).map((dropdownData) => {
    return {
      ...Documents,
      i18nKey: `${dropdownData.code}`,
    };
  });
};

const getTLDocumentRequiredScreen = (MdmsRes) => {
  MdmsRes["TradeLicense"].Documents.filter((Documents) => Documents.active).map((dropdownData) => {
    return {
      ...Documents,
      i18nKey: `${dropdownData.code}`,
    };
  });
};

const getMapConfig = (MdmsRes) => {
  MdmsRes["PropertyTax"].MapConfig.filter((MapConfig) => MapConfig).map((MapData) => {
    return {
      ...MapConfig,
      defaultconfig: MapData.defaultConfig,
    };
  });
};

const getUsageCategory = (MdmsRes) =>
  MdmsRes["PropertyTax"].UsageCategory.filter((UsageCategory) => UsageCategory.active).map((UsageCategorylist) => {
    return {
      ...UsageCategorylist,
      i18nKey: `PROPERTYTAX_BILLING_SLAB_${UsageCategorylist.code}`,
    };
  });

const getPTPropertyType = (MdmsRes) =>
  MdmsRes["PropertyTax"].UsageCategory.filter((PropertyType) => PropertyType.active).map((PTPropertyTypelist) => {
    return {
      ...UsageCategorylist,
      i18nKey: `COMMON_PROPTYPE_${stringReplaceAll(PTPropertyTypelist.code, ".", "_")}`,
    };
  });

const getTLStructureType = (MdmsRes) =>
  MdmsRes["common-masters"].StructureType.filter((StructureType) => StructureType.active).map((TLStructureTypeList) => {
    return {
      ...TLStructureTypeList,
      i18nKey: `COMMON_MASTERS_STRUCTURETYPE_${stringReplaceAll(TLStructureTypeList.code, ".", "_")}`,
    };
  });

const getTLAccessoriesType = (MdmsRes) =>
  MdmsRes["TradeLicense"].AccessoriesCategory.filter((AccessoriesCategory) => AccessoriesCategory.active).map((TLAccessoryTypeList) => {
    return {
      ...TLAccessoryTypeList,
      i18nKey: `TRADELICENSE_ACCESSORIESCATEGORY_${stringReplaceAll(TLAccessoryTypeList.code, ".", "_")}`,
    };
  });

const getTLFinancialYear = (MdmsRes) =>
  MdmsRes["egf-master"].FinancialYear.filter((FinancialYear) => FinancialYear.active && FinancialYear.module === "TL").map((FinancialYearList) => {
    return {
      ...FinancialYearList,
      //i18nKey: `TRADELICENSE_ACCESSORIESCATEGORY_${stringReplaceAll(TLAccessoryTypeList.code, ".", "_")}`,
    };
  });
const getFloorList = (MdmsRes) =>
  MdmsRes["PropertyTax"].Floor.filter((PTFloor) => PTFloor.active).map((PTFloorlist) => {
    return {
      ...PTFloorlist,
      i18nKey: `PROPERTYTAX_FLOOR_${PTFloorlist.code}`,
    };
  });

const GetReasonType = (MdmsRes, type, moduleCode) =>
  Object.assign(
    {},
    ...Object.keys(MdmsRes[moduleCode]).map((collection) => ({
      [collection]: MdmsRes[moduleCode][collection]
        .filter((reason) => reason.active)
        .map((reason) => ({
          ...reason,
          i18nKey: `ES_ACTION_REASON_${reason.code}`,
        })),
    }))
  );

const getRentalDetailsCategory = (MdmsRes) => {
  MdmsRes["PropertyTax"].RentalDetails.filter((category) => category.active).map((RentalDetailsInfo) => {
    return {
      ...RentalDetailsInfo,
      i18nKey: `PROPERTYTAX_BILLING_SLAB_${RentalDetailsInfo.code}`,
    };
  });
};

const getChargeSlabsCategory = (MdmsRes) => {
  MdmsRes["PropertyTax"].ChargeSlabs.filter((category) => category.active).map((ChargeSlabsInfo) => {
    return {
      ...ChargeSlabsInfo,
    };
  });
};

const getGenderType = (MdmsRes) => {
  return MdmsRes["common-masters"].GenderType.filter((GenderType) => GenderType.active).map((genderDetails) => {
    return {
      ...genderDetails,
      i18nKey: `PT_COMMON_GENDER_${genderDetails.code}`,
    };
  });
  //return MdmsRes;
};

const TLGenderType = (MdmsRes) => {
  MdmsRes["common-masters"].GenderType.filter((GenderType) => GenderType.active).map((genders) => {
    return {
      ...genders,
      i18nKey: `TL_GENDER_${genders.code}`,
    };
  });
};

const PTGenderType = (MdmsRes) => {
  MdmsRes["common-masters"].GenderType.filter((GenderType) => GenderType.active).map((formGender) => {
    return {
      ...formGender,
      i18nKey: `PT_FORM3_${formGender.code}`,
    };
  });
};

const HRGenderType = (MdmsRes) => {
  MdmsRes["common-masters"].GenderType.filter((GenderType) => GenderType.active).map((comGender) => {
    return {
      ...comGender,
      i18nKey: `COMMON_GENDER_${comGender.code}`,
    };
  });
};
const WMSDepartment = (MdmsRes) => {
  MdmsRes["common-masters"].Department.filter((Department) => Department.active).map((comDepartment) => {
    return {
      ...comDepartment,
      i18nKey: `COMMON_DEPARTMENT_${comDepartment.code}`,
    };
  });
};
const WMSFund = (MdmsRes) => {
  MdmsRes["common-masters"].Fund.filter((Fund) => Fund.active).map((comFund) => {
  return {
    ...comFund,
    i18nKey: `COMMON_FUND_${comFund.code}`,
  };
});
};
const WMSChapter = (MdmsRes) => {
  MdmsRes["common-masters"].Chapter.filter((Chapter) => Chapter.active).map((comChapter) => {
  return {
    ...comChapter,
    i18nKey: `COMMON_CHAPTER_${comChapter.code}`,
  };
});
};
const WMSUnit = (MdmsRes) => {
  MdmsRes["common-masters"].Unit.filter((Unit) => Unit.active).map((comUnit) => {
  return {
    ...comUnit,
    i18nKey: `COMMON_UNIT_${com.Unit.code}`,
  };
});
};
const WMSProject = (MdmsRes) => {
  MdmsRes["common-masters"].Project.filter((Project) => Project.active).map((comProject) => {
    console.log(comProject);
  return {
    ...comProject,
    i18nKey: `COMMON_PROJECT_${comProject.code}`,
  };
});
};
const GetMCollectBusinessService = (MdmsRes) =>
  MdmsRes["BillingService"].BusinessService.map((businesServiceDetails) => {
    return {
      ...businesServiceDetails,
      i18nKey: `BILLINGSERVICE_BUSINESSSERVICE_${businesServiceDetails.code}`,
    };
  });

const GetMCollectApplicationStatus = (MdmsRes) =>
  MdmsRes["mCollect"].applcationStatus.map((appStatusDetails) => {
    return {
      ...appStatusDetails,
      i18nKey: `BILLINGSERVICE_BUSINESSSERVICE_${appStatusDetails.code}`,
    };
  });

const getFSMGenderType = (MdmsRes) => {
  return MdmsRes["common-masters"].GenderType.map((genderDetails) => {
    return {
      ...genderDetails,
      i18nKey: `COMMON_GENDER_${genderDetails.code}`,
    };
  });
};

const GetFSTPORejectionReason = (MdmsRes) => {
  return MdmsRes["Vehicle"].FSTPORejectionReason.filter((reason) => reason.active).map((reasonDetails) => {
    return {
      ...reasonDetails,
      i18nKey: `ES_ACTION_REASON_${reasonDetails.code}`,
    };
  });
};

const GetPaymentType = (MdmsRes) => {
  return MdmsRes["FSM"].PaymentType.filter((option) => option.active).map((reasonDetails) => {
    return {
      ...reasonDetails,
      i18nKey: `ES_ACTION_${reasonDetails.code}`,
    };
  });
};

const GetTripNumber = (MdmsRes) => {
  return MdmsRes["FSM"].TripNumber.filter((option) => option.active).map((reasonDetails) => {
    return {
      ...reasonDetails,
      i18nKey: `ES_ACTION_TRIP_${reasonDetails.code}`,
    };
  });
};

const GetReceivedPaymentType = (MdmsRes) => {
  return MdmsRes["FSM"].ReceivedPaymentType.filter((option) => option.active).map((reasonDetails) => {
    return {
      ...reasonDetails,
      i18nKey: `ES_ACTION_${reasonDetails.code}`,
    };
  });
};

const getDssDashboard = (MdmsRes) => MdmsRes["dss-dashboard"]["dashboard-config"];

const GetRoleStatusMapping = (MdmsRes) => MdmsRes["DIGIT-UI"].RoleStatusMapping;
const GetCommonFields = (MdmsRes, moduleCode) =>
  moduleCode.toUpperCase() === "PROPERTYTAX" ? MdmsRes["PropertyTax"].CommonFieldsConfig : MdmsRes["FSM"].CommonFieldsConfig;

const GetPreFields = (MdmsRes) => MdmsRes["FSM"].PreFieldsConfig;

const GetPostFields = (MdmsRes) => MdmsRes["FSM"].PostFieldsConfig;

const GetFSTPPlantInfo = (MdmsRes) => MdmsRes["FSM"].FSTPPlantInfo;

const GetDocumentsTypes = (MdmsRes) => MdmsRes["BPA"].DocTypeMapping;

const GetChecklist = (MdmsRes) => MdmsRes["BPA"].CheckList;

const transformResponse = (type, MdmsRes, moduleCode, tenantId) => {
  switch (type) {
    case "citymodule":
      return GetCitiesWithi18nKeys(MdmsRes, moduleCode);
    case "egovLocation":
      return GetEgovLocations(MdmsRes);
    case "serviceDefs":
      return GetServiceDefs(MdmsRes, moduleCode);
    case "ApplicationChannel":
      return GetApplicationChannel(MdmsRes);
    case "SanitationType":
      return GetSanitationType(MdmsRes);
    case "PropertyType":
      return GetPropertyType(MdmsRes);
    case "PropertySubtype":
      return GetPropertySubtype(MdmsRes);
    case "PitType":
      return GetPitType(MdmsRes);
    case "VehicleType":
      return GetVehicleType(MdmsRes);
    case "VehicleMakeModel":
      return GetVehicleMakeModel(MdmsRes);
    case "Slum":
      return GetSlumLocalityMapping(MdmsRes, tenantId);
    case "OwnerShipCategory":
      return GetPropertyOwnerShipCategory(MdmsRes);
    case "TLOwnerShipCategory":
      return GetTradeOwnerShipCategory(MdmsRes);
    case "OwnerType":
      return GetPropertyOwnerType(MdmsRes);
    case "SubOwnerShipCategory":
      return getSubPropertyOwnerShipCategory(MdmsRes);
    case "Documents":
      return getDocumentRequiredScreen(MdmsRes);
    case "TLDocuments":
      return getTLDocumentRequiredScreen(MdmsRes);
    case "MapConfig":
      return getMapConfig(MdmsRes);
    case "UsageCategory":
      return getUsageCategory(MdmsRes);
    case "PTPropertyType":
      return getPTPropertyType(MdmsRes);
    case "StructureType":
      return getTLStructureType(MdmsRes);
    case "AccessoryCategory":
      return getTLAccessoriesType(MdmsRes);
    case "FinancialYear":
      return getTLFinancialYear(MdmsRes);
    case "Floor":
      return getFloorList(MdmsRes);
    case "Reason":
      return GetReasonType(MdmsRes, type, moduleCode);
    case "RoleStatusMapping":
      return GetRoleStatusMapping(MdmsRes);
    case "CommonFieldsConfig":
      return GetCommonFields(MdmsRes, moduleCode);
    case "PreFieldsConfig":
      return GetPreFields(MdmsRes);
    case "PostFieldsConfig":
      return GetPostFields(MdmsRes);
    case "RentalDeatils":
      return getRentalDetailsCategory(MdmsRes);
    case "ChargeSlabs":
      return getChargeSlabsCategory(MdmsRes);
    case "DssDashboard":
      return getDssDashboard(MdmsRes);
    case "BusinessService":
      return GetMCollectBusinessService(MdmsRes);
    case "applcatonStatus":
      return GetMCollectApplicationStatus(MdmsRes);
    case "FSTPPlantInfo":
      return GetFSTPPlantInfo(MdmsRes);
    case "GenderType":
      return getGenderType(MdmsRes);
    case "TLGendertype":
      return TLGenderType(MdmsRes);
    case "PTGenderType":
      return PTGenderType(MdmsRes);
    case "HRGenderType":
      return HRGenderType(MdmsRes);
    case "WMSDepartment":
      return WMSDepartment(MdmsRes);
    case "WMSChapter":
      return WMSChapter(MdmsRes);      
      case "WMSUnit":
        return WMSUnit(MdmsRes);
    case "WMSProject":
      return WMSProject(MdmsRes);
    case "WMSFund":
      return WMSFund(MdmsRes);
    case "DocumentTypes":
      return GetDocumentsTypes(MdmsRes);
    case "CheckList":
      return GetChecklist(MdmsRes);
    case "FSMGenderType":
      return getFSMGenderType(MdmsRes);
    case "FSTPORejectionReason":
      return GetFSTPORejectionReason(MdmsRes);
    case "PaymentType":
      return GetPaymentType(MdmsRes);
    case "TripNumber":
      return GetTripNumber(MdmsRes);
    case "ReceivedPaymentType":
      return GetReceivedPaymentType(MdmsRes);
    default:
      return MdmsRes;
  }
};

const getCacheSetting = (moduleName) => {
  return ApiCacheService.getSettingByServiceUrl(Urls.MDMS, moduleName);
};

const mergedData = {};
const mergedPromises = {};
const callAllPromises = (success, promises = [], resData) => {
  promises.forEach((promise) => {
    if (success) {
      promise.resolve(resData);
    } else {
      promise.reject(resData);
    }
  });
};
const mergeMDMSData = (data, tenantId) => {
  if (!mergedData[tenantId] || Object.keys(mergedData[tenantId]).length === 0) {
    mergedData[tenantId] = data;
  } else {
    data.MdmsCriteria.moduleDetails.forEach((dataModuleDetails) => {
      const moduleName = dataModuleDetails.moduleName;
      const masterDetails = dataModuleDetails.masterDetails;
      let found = false;
      mergedData[tenantId].MdmsCriteria.moduleDetails.forEach((moduleDetail) => {
        if (moduleDetail.moduleName === moduleName) {
          found = true;
          moduleDetail.masterDetails = [...moduleDetail.masterDetails, ...masterDetails];
        }
      });
      if (!found) {
        mergedData[tenantId].MdmsCriteria.moduleDetails.push(dataModuleDetails);
      }
    });
  }
};
const debouncedCall = ({ serviceName, url, data, useCache, params }, resolve, reject) => {
  if (!mergedPromises[params.tenantId] || mergedPromises[params.tenantId].length === 0) {
    const cacheSetting = getCacheSetting();
    setTimeout(() => {
      let callData = JSON.parse(JSON.stringify(mergedData[params.tenantId]));
      mergedData[params.tenantId] = {};
      let callPromises = [...mergedPromises[params.tenantId]];
      mergedPromises[params.tenantId] = [];
      ServiceRequest({
        serviceName,
        url,
        data: callData,
        useCache,
        params,
      })
        .then((data) => {
          callAllPromises(true, callPromises, data);
        })
        .catch((err) => {
          callAllPromises(false, callPromises, err);
        });
    }, cacheSetting.debounceTimeInMS || 500);
  }
  mergeMDMSData(data, params.tenantId);
  if (!mergedPromises[params.tenantId]) {
    mergedPromises[params.tenantId] = [];
  }
  mergedPromises[params.tenantId].push({ resolve, reject });
};

export const MdmsService = {
  init: (stateCode) =>
    ServiceRequest({
      serviceName: "mdmsInit",
      url: Urls.MDMS,
      data: initRequestBody(stateCode),
      useCache: true,
      params: { tenantId: stateCode },
    }),
  call: (tenantId, details) => {
    console.log("call tenantId ",{tenantId, details})
    return new Promise((resolve, reject) =>
      debouncedCall(
        {
          serviceName: "mdmsCall",
          url: Urls.MDMS,
          data: getCriteria(tenantId, details),
          useCache: true,
          params: { tenantId },
        },
        resolve,
        reject
      )
    );
  },
  getDataByCriteria: async (tenantId, mdmsDetails, moduleCode) => {
    const key = `MDMS.${tenantId}.${moduleCode}.${mdmsDetails.type}.${JSON.stringify(mdmsDetails.details)}`;
    const inStoreValue = PersistantStorage.get(key);
    console.log("PersistantStorage inStoreValue ",{PersistantStorage,inStoreValue})

    if (inStoreValue) {
      return inStoreValue;
    }
    const { MdmsRes } = await MdmsService.call(tenantId, mdmsDetails.details);
    console.log("return data MdmsRes ",MdmsRes)
//     var MdmsRes  =  
//     {
//       "ACCESSCONTROL-ACTIONS-TEST": {
//           "actions-test": [
//               {
//                   "id": 2422,
//                   "name": "TL_FAQ_S",
//                   "url": "digit-ui-card",
//                   "displayName": "FAQs",
//                   "orderNumber": 7,
//                   "parentModule": "TL",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/tl-faq",
//                   "leftIcon": "TLIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/tl-home"
//               },
//               {
//                   "id": 2423,
//                   "name": "PT_HOW_IT_WORKS",
//                   "url": "digit-ui-card",
//                   "displayName": "How It Works",
//                   "orderNumber": 9,
//                   "parentModule": "PT",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pt-how-it-works",
//                   "leftIcon": "propertyIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pt-home"
//               },
//               {
//                   "id": 2424,
//                   "name": "PT_FAQ_S",
//                   "url": "digit-ui-card",
//                   "displayName": "FAQs",
//                   "orderNumber": 8,
//                   "parentModule": "PT",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pt-faq",
//                   "leftIcon": "propertyIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pt-home"
//               },
//               {
//                   "id": 2425,
//                   "name": "Firenoc Citizen Home",
//                   "url": "digit-ui-card",
//                   "displayName": "FireNoc Search",
//                   "orderNumber": 1,
//                   "parentModule": "FireNoc",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/citizen/fire-noc/home",
//                   "leftIcon": "FirenocIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/citizen/fire-noc/home"
//               },
//               {
//                   "id": 2426,
//                   "name": "Birth Certificate Home",
//                   "url": "digit-ui-card",
//                   "displayName": "Birth Search",
//                   "orderNumber": 1,
//                   "parentModule": "Birth",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/citizen/birth-citizen/home",
//                   "leftIcon": "BirthIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/citizen/birth-citizen/home"
//               },
//               {
//                   "id": 2427,
//                   "name": "Death Certificate Home",
//                   "url": "digit-ui-card",
//                   "displayName": "Death Search",
//                   "orderNumber": 1,
//                   "parentModule": "Death",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/citizen/death-citizen/home",
//                   "leftIcon": "DeathIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/citizen/death-citizen/home"
//               },
//               {
//                   "id": 2437,
//                   "name": "PT_MY_PAYMENTS",
//                   "url": "digit-ui-card",
//                   "displayName": "My Payments",
//                   "orderNumber": 7,
//                   "parentModule": "PT",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pt/property/my-payments",
//                   "leftIcon": "propertyIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pt-home"
//               },
//               {
//                   "id": 2440,
//                   "name": "ACTION_TEST_WNS_MY_BILLS",
//                   "url": "digit-ui-card",
//                   "displayName": "My Bills",
//                   "orderNumber": 2,
//                   "parentModule": "WS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/ws/my-bills",
//                   "leftIcon": "WSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/ws-home"
//               },
//               {
//                   "id": 2441,
//                   "name": "ACTION_TEST_MY_PAYMENTS",
//                   "url": "digit-ui-card",
//                   "displayName": "My Payments",
//                   "orderNumber": 6,
//                   "parentModule": "WS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/ws/my-payments",
//                   "leftIcon": "WSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/ws-home"
//               },
//               {
//                   "id": 2442,
//                   "name": "ACTION_TEST_APPLY_NEW_CONNECTION",
//                   "url": "digit-ui-card",
//                   "displayName": "Apply for New Connection",
//                   "orderNumber": 4,
//                   "parentModule": "WS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/ws/create-application/docs-required",
//                   "leftIcon": "WSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/ws-home"
//               },
//               {
//                   "id": 2443,
//                   "name": "ACTION_TEXT_WS_SEARCH_AND_PAY",
//                   "url": "digit-ui-card",
//                   "displayName": "Search & Pay",
//                   "orderNumber": 1,
//                   "parentModule": "WS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/ws/search",
//                   "leftIcon": "WSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/ws-home"
//               },
//               {
//                   "id": 2444,
//                   "name": "ACTION_TEXT_WS_MY_APPLICATION",
//                   "url": "digit-ui-card",
//                   "displayName": "My Applications",
//                   "orderNumber": 5,
//                   "parentModule": "WS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/ws/my-applications",
//                   "leftIcon": "WSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/ws-home"
//               },
//               {
//                   "id": 2445,
//                   "name": "ACTION_TEXT_WS_MY_CONNECTION",
//                   "url": "digit-ui-card",
//                   "displayName": "My Connections",
//                   "orderNumber": 3,
//                   "parentModule": "WS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/ws/my-connections",
//                   "leftIcon": "WSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/ws-home"
//               },
//               {
//                   "id": 2446,
//                   "name": "UC_SEARCH_AND_PAY",
//                   "url": "digit-ui-card",
//                   "displayName": "Search & Pay",
//                   "orderNumber": 1,
//                   "parentModule": "MCollect",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/mcollect/search",
//                   "leftIcon": "MCollectIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/mcollect-home"
//               },
//               {
//                   "id": 2447,
//                   "name": "UC_MY_CHALLANS",
//                   "url": "digit-ui-card",
//                   "displayName": "My Challans",
//                   "orderNumber": 2,
//                   "parentModule": "MCollect",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/mcollect/My-Challans",
//                   "leftIcon": "MCollectIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/mcollect-home"
//               },
//               {
//                   "id": 2448,
//                   "name": "CS_HOME_APPLY_FOR_DESLUDGING",
//                   "url": "digit-ui-card",
//                   "displayName": "Apply for Emptying of Septic Tank/Pit",
//                   "orderNumber": 1,
//                   "parentModule": "FSM",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/fsm/new-application",
//                   "leftIcon": "FSMIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/fsm-home"
//               },
//               {
//                   "id": 2449,
//                   "name": "CS_HOME_MY_APPLICATIONS",
//                   "url": "digit-ui-card",
//                   "displayName": "My Applications",
//                   "orderNumber": 2,
//                   "parentModule": "FSM",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/fsm/my-applications",
//                   "leftIcon": "FSMIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/fsm-home"
//               },
//               {
//                   "id": 2450,
//                   "name": "CS_COMMON_FILE_A_COMPLAINT",
//                   "url": "digit-ui-card",
//                   "displayName": "File a Complaint",
//                   "orderNumber": 1,
//                   "parentModule": "PGR",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pgr/create-complaint/complaint-type",
//                   "leftIcon": "PGRIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pgr-home"
//               },
//               {
//                   "id": 2451,
//                   "name": "CS_HOME_MY_COMPLAINTS",
//                   "url": "digit-ui-card",
//                   "displayName": "My Complaints",
//                   "orderNumber": 2,
//                   "parentModule": "PGR",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pgr/complaints",
//                   "leftIcon": "PGRIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pgr-home"
//               },
//               {
//                   "id": 2452,
//                   "name": "TL_CREATE_TRADE",
//                   "url": "digit-ui-card",
//                   "displayName": "Apply for Trade License",
//                   "orderNumber": 1,
//                   "parentModule": "TL",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/tl/tradelicence/new-application",
//                   "leftIcon": "TLIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/tl-home"
//               },
//               {
//                   "id": 2453,
//                   "name": "TL_RENEWAL_HEADER",
//                   "url": "digit-ui-card",
//                   "displayName": "Renew Trade License",
//                   "orderNumber": 2,
//                   "parentModule": "TL",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/tl/tradelicence/renewal-list",
//                   "leftIcon": "TLIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/tl-home"
//               },
//               {
//                   "id": 2454,
//                   "name": "TL_MY_APPLICATIONS_HEADER",
//                   "url": "digit-ui-card",
//                   "displayName": "My Applications",
//                   "orderNumber": 3,
//                   "parentModule": "TL",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/tl/tradelicence/my-application",
//                   "leftIcon": "TLIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/tl-home"
//               },
//               {
//                   "id": 2455,
//                   "name": "BPA_CITIZEN_HOME_VIEW_APP_BY_CITIZEN_LABEL",
//                   "url": "digit-ui-card",
//                   "displayName": "View applications by Citizen",
//                   "orderNumber": 1,
//                   "parentModule": "OBPS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/obps/my-applications",
//                   "leftIcon": "OBPSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/obps-home"
//               },
//               {
//                   "id": 2456,
//                   "name": "BPA_CITIZEN_HOME_STAKEHOLDER_LOGIN_LABEL",
//                   "url": "digit-ui-card",
//                   "displayName": "Register as a Stakeholder",
//                   "orderNumber": 2,
//                   "parentModule": "OBPS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/obps/stakeholder/apply/stakeholder-docs-required",
//                   "leftIcon": "OBPSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/obps-home"
//               },
//               {
//                   "id": 2457,
//                   "name": "BPA_CITIZEN_HOME_ARCHITECT_LOGIN_LABEL",
//                   "url": "digit-ui-card",
//                   "displayName": "Registered Architect Login",
//                   "orderNumber": 3,
//                   "parentModule": "OBPS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/obps/home",
//                   "leftIcon": "OBPSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/obps-home"
//               },
//               {
//                   "id": 2458,
//                   "name": "PT_SEARCH_AND_PAY",
//                   "url": "digit-ui-card",
//                   "displayName": "Search and Pay",
//                   "orderNumber": 1,
//                   "parentModule": "CommonPT",
//                   "enabled": false,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/commonpt/property/citizen-search",
//                   "leftIcon": "CommonPTIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/commonpt-home"
//               },
//               {
//                   "id": 2459,
//                   "name": "PT_CREATE_PROPERTY",
//                   "url": "digit-ui-card",
//                   "displayName": "Create Property",
//                   "orderNumber": 3,
//                   "parentModule": "CommonPT",
//                   "enabled": false,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/commonpt/property/new-application",
//                   "leftIcon": "CommonPTIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/commonpt-home"
//               },
//              /*  {
//                   "id": 2460,
//                   "name": "ABG_SEARCH_BILL_COMMON_HEADER",
//                   "url": "digit-ui-card",
//                   "displayName": "Search Bills",
//                   "orderNumber": 1,
//                   "parentModule": "Bills",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/bills/billSearch",
//                   "leftIcon": "BillsIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/bills-home"
//               }, */
//               {
//                   "id": 2466,
//                   "name": "PRIVACY_AUDIT_REPORT",
//                   "url": "digit-ui-card",
//                   "displayName": "Privacy Audit Report",
//                   "orderNumber": 7,
//                   "parentModule": "WS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/Audit",
//                   "leftIcon": "WSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/ws-home"
//               },
//               {
//                   "id": 2468,
//                   "name": "MCOLLECT_FAQ_S",
//                   "url": "digit-ui-card",
//                   "displayName": "FAQs",
//                   "orderNumber": 7,
//                   "parentModule": "MCollect",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/mcollect-faq",
//                   "leftIcon": "MCollectIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/mcollect-home"
//               },
//               {
//                   "id": 2469,
//                   "name": "WS_FAQ_S",
//                   "url": "digit-ui-card",
//                   "displayName": "FAQs",
//                   "orderNumber": 8,
//                   "parentModule": "WS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/ws-faq",
//                   "leftIcon": "WSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/ws-home"
//               },
//               {
//                   "id": 2470,
//                   "name": "PT_MY_APPLICATION",
//                   "url": "digit-ui-card",
//                   "displayName": "My Applications",
//                   "orderNumber": 5,
//                   "parentModule": "PT",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pt/property/my-applications",
//                   "leftIcon": "propertyIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pt-home"
//               },
//               {
//                   "id": 2471,
//                   "name": "PT_MY_PROPERTIES",
//                   "url": "digit-ui-card",
//                   "displayName": "My Properties",
//                   "orderNumber": 4,
//                   "parentModule": "PT",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pt/property/my-properties",
//                   "leftIcon": "propertyIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pt-home"
//               },
//               {
//                   "id": 2472,
//                   "name": "PT_PROPERTY_MUTATION",
//                   "url": "digit-ui-card",
//                   "displayName": "Transfer Property Ownership/Mutation",
//                   "orderNumber": 6,
//                   "parentModule": "PT",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pt/property/property-mutation/search-property",
//                   "leftIcon": "propertyIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pt-home"
//               },
//               {
//                   "id": 2473,
//                   "name": "OBPS_FAQ_S",
//                   "url": "digit-ui-card",
//                   "displayName": "FAQs",
//                   "orderNumber": 8,
//                   "parentModule": "OBPS",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/obps-faq",
//                   "leftIcon": "TLIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/obps-home"
//               },
//               {
//                   "id": 2474,
//                   "name": "CS_TITLE_MY_BILLS",
//                   "url": "digit-ui-card",
//                   "displayName": "My Bills",
//                   "orderNumber": 2,
//                   "parentModule": "PT",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/payment/my-bills/PT",
//                   "leftIcon": "propertyIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pt-home"
//               },
//               {
//                   "id": 2350,
//                   "name": "PT_SEARCH_AND_PAY",
//                   "url": "digit-ui-card",
//                   "displayName": "Search and Pay",
//                   "orderNumber": 1,
//                   "parentModule": "PT",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pt/property/citizen-search",
//                   "leftIcon": "propertyIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pt-home"
//               },
//               {
//                   "id": 2351,
//                   "name": "PT_CREATE_PROPERTY",
//                   "url": "digit-ui-card",
//                   "displayName": "Register Property",
//                   "orderNumber": 3,
//                   "parentModule": "PT",
//                   "enabled": true,
//                   "serviceCode": "",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/pt/property/new-application/info",
//                   "leftIcon": "propertyIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/pt-home"
//               },
//               {
//                   "id": 3001,
//                   "name": "SOR_HOME",
//                   "url": "digit-ui-card",
//                   "displayName": "SOR HOME",
//                   "orderNumber": 1,
//                   "parentModule": "WMS",
//                   "enabled": true,
//                   "serviceCode": "CITIZEN_SERVICE_WMS",
//                   "code": "",
//                   "path": "",
//                   "navigationURL": "/digit-ui/citizen/wms/sor-home",
//                   "leftIcon": "WMSIcon",
//                   "rightIcon": "",
//                   "queryParams": "",
//                   "sidebar": "digit-ui-links",
//                   "sidebarURL": "/digit-ui/citizen/wms-home"
//               },
//               {
//                   "id": 3002,
//                   "name": "PRJ_HOME",
//                   "url": "digit-ui-card",
//                   "displayName": "Project Master Home",
//                   "orderNumber": 2,
//                   "queryParams": "",
//                   "parentModule": "WMS",
//                   "enabled": true,
//                   "serviceCode": "CITIZEN_SERVICE_WMS",
//                   "tenantId": "pg",
//                   "createdDate": null,
//                   "createdBy": null,
//                   "lastModifiedDate": null,
//                   "lastModifiedBy": null,
//                   "path": "/digit-ui/citizen/wms/prj-home",
//                   "navigationURL": "/digit-ui/citizen/wms/prj-home",
//                   "leftIcon": "WMSIcon",
//                   "rightIcon": ""
//               },
//               {
//                 "id": 3003,
//                 "name": "SCH_HOME",
//                 "url": "digit-ui-card",
//                 "displayName": "Scheme Master Home",
//                 "orderNumber": 3,
//                 "queryParams": "",
//                 "parentModule": "WMS",
//                 "enabled": true,
//                 "serviceCode": "CITIZEN_SERVICE_WMS",
//                 "tenantId": "pg",
//                 "createdDate": null,
//                 "createdBy": null,
//                 "lastModifiedDate": null,
//                 "lastModifiedBy": null,
//                 "path": "/digit-ui/citizen/wms/sch-home",
//                 "navigationURL": "/digit-ui/citizen/wms/sch-home",
//                 "leftIcon": "WMSIcon",
//                 "rightIcon": ""
//             },
//             {
//               "id": 3004,
//               "name": "Physical Milestone Home ",
//               "url": "digit-ui-card",
//               "displayName": "Physical Milestone",
//               "orderNumber": 4,
//               "parentModule": "WMS",
//               "enabled": true,
//               "serviceCode": "CITIZEN_SERVICE_WMS",
//               "code": "",
//               "path": "",
//               "navigationURL": "/digit-ui/citizen/wms/phm-home",
//               "leftIcon": "WMSIcon",
//               "rightIcon": "",
//               "queryParams": "",
//               "sidebar": "digit-ui-links",
//               "sidebarURL": "/digit-ui/citizen/wms-home"
//           },
//           {
//             "id": 3005,
//             "name": "Measurement Book Home",
//             "url": "digit-ui-card",
//             "displayName": "Measurement Book",
//             "orderNumber": 5,
//             "parentModule": "WMS",
//             "enabled": true,
//             "serviceCode": "CITIZEN_SERVICE_WMS",
//             "code": "",
//             "path": "",
//             "navigationURL": "/digit-ui/citizen/wms/mb-home",
//             "leftIcon": "WMSIcon",
//             "rightIcon": "",
//             "queryParams": "",
//             "sidebar": "digit-ui-links",
//             "sidebarURL": "/digit-ui/citizen/wms-home"
//         },
//           {
//             "id": 3006,
//             "name": "Contaractor Master",
//             "url": "digit-ui-card",
//             "displayName": "Contaractor Master",
//             "orderNumber": 6,
//             "parentModule": "WMS",
//             "enabled": true,
//             "serviceCode": "CITIZEN_SERVICE_WMS",
//             "code": "",
//             "path": "",
//             "navigationURL": "/digit-ui/citizen/wms/cm-home",
//             "leftIcon": "WMSIcon",
//             "rightIcon": "",
//             "queryParams": "",
//             "sidebar": "digit-ui-links",
//             "sidebarURL": "/digit-ui/citizen/wms-home"
//         },
//         {
//           "id": 3007,
//           "name": "Tender Entry",
//           "url": "digit-ui-card",
//           "displayName": "Tender Entry",
//           "orderNumber": 7,
//           "parentModule": "WMS",
//           "enabled": true,
//           "serviceCode": "CITIZEN_SERVICE_WMS",
//           "code": "",
//           "path": "",
//           "navigationURL": "/digit-ui/citizen/wms/tender-entry/home",
//           "leftIcon": "WMSIcon",
//           "rightIcon": "",
//           "queryParams": "",
//           "sidebar": "digit-ui-links",
//           "sidebarURL": "/digit-ui/citizen/wms-home"
//       },
      
//     {
//       "id": 3008,
//       "name": "Contract Agreement",
//       "url": "digit-ui-card",
//       "displayName": "Contract Agreement",
//       "orderNumber": 8,
//       "parentModule": "WMS",
//       "enabled": true,
//       "serviceCode": "CITIZEN_SERVICE_WMS",
//       "code": "",
//       "path": "",
//       "navigationURL": "/digit-ui/citizen/wms/contract-agreement/list",
//       "leftIcon": "WMSIcon",
//       "rightIcon": "",
//       "queryParams": "",
//       "sidebar": "digit-ui-links",
//       "sidebarURL": "/digit-ui/citizen/wms/contract-agreement/list"
//   },
 
// {
//   "id": 3009,
//   "name": "Running Account / Final Bill",
//   "url": "digit-ui-card",
//   "displayName": "Running Account / Final Bill",
//   "orderNumber": 9,
//   "parentModule": "WMS",
//   "enabled": true,
//   "serviceCode": "CITIZEN_SERVICE_WMS",
//   "code": "",
//   "path": "",
//   "navigationURL": "/digit-ui/citizen/wms/running-account/list",
//   "leftIcon": "WMSIcon",
//   "rightIcon": "",
//   "queryParams": "",
//   "sidebar": "digit-ui-links",
//   "sidebarURL": "/digit-ui/citizen/wms/running-account/list"
// },
// {
//   "id": 3010,
//   "name": "Master Data",
//   "url": "digit-ui-card",
//   "displayName": "Master Data",
//   "orderNumber": 10,
//   "parentModule": "WMS",
//   "enabled": true,
//   "serviceCode": "CITIZEN_SERVICE_WMS",
//   "code": "",
//   "path": "",
//   "navigationURL": "/digit-ui/citizen/wms/master-data",
//   "leftIcon": "WMSIcon",
//   "rightIcon": "",
//   "queryParams": "",
//   "sidebar": "digit-ui-links",
//   "sidebarURL": "/digit-ui/citizen/wms/master-data"
// },
// {
//   "id": 3011,
//   "name": "Deduction Register ",
//   "url": "digit-ui-card",
//   "displayName": "Deduction Register",
//   "orderNumber": 4,
//   "parentModule": "WMS",
//   "enabled": true,
//   "serviceCode": "CITIZEN_SERVICE_WMS",
//   "code": "",
//   "path": "",
//   "navigationURL": "/digit-ui/citizen/wms/dr-home",
//   "leftIcon": "WMSIcon",
//   "rightIcon": "",
//   "queryParams": "",
//   "sidebar": "digit-ui-links",
//   "sidebarURL": "/digit-ui/citizen/dr-home"
// },
// {
//   "id": 3012,
//   "name": "Project Register ",
//   "url": "digit-ui-card",
//   "displayName": "Project Register",
//   "orderNumber": 4,
//   "parentModule": "WMS",
//   "enabled": true,
//   "serviceCode": "CITIZEN_SERVICE_WMS",
//   "code": "",
//   "path": "",
//   "navigationURL": "/digit-ui/citizen/wms/pr-home",
//   "leftIcon": "WMSIcon",
//   "rightIcon": "",
//   "queryParams": "",
//   "sidebar": "digit-ui-links",
//   "sidebarURL": "/digit-ui/citizen/pr-home"
// },
// {
//   "id": 3033,
//   "name": "Work Status Report",
//   "url": "digit-ui-card",
//   "displayName": "Work Status Report",
//   "orderNumber": 4,
//   "parentModule": "WMS",
//   "enabled": true,
//   "serviceCode": "CITIZEN_SERVICE_WMS",
//   "code": "",
//   "path": "",
//   "navigationURL": "/digit-ui/citizen/wms/wsr-home",
//   "leftIcon": "WMSIcon",
//   "rightIcon": "",
//   "queryParams": "",
//   "sidebar": "digit-ui-links",
//   "sidebarURL": "/digit-ui/citizen/wsr-home"
// },
//   ]
//         },
//       "common-masters": {
//           "Designation": [
//               {
//                   "code": "DESIG_01",
//                   "name": "Superintending Engineer ( B&R)",
//                   "description": "Superintending Engineer ( B&R)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_02",
//                   "name": "Corporation Engineer (B&R)",
//                   "description": "Corporation Engineer (B&R)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_03",
//                   "name": "Asst. Engineer ( B&R)",
//                   "description": "Asst. Engineer ( B&R)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_04",
//                   "name": "Junior Engineer ( B&R)",
//                   "description": "Junior Engineer ( B&R)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_05",
//                   "name": "Land Scape Officer",
//                   "description": "Land Scape Officer",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_06",
//                   "name": "Superintending Engineer ( O&M)",
//                   "description": "Superintending Engineer ( O&M)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_07",
//                   "name": "Corporation Engineer (O&M)",
//                   "description": "Corporation Engineer (O&M)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_08",
//                   "name": "Asst. Engineer ( O&M)",
//                   "description": "Asst. Engineer ( O&M)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_09",
//                   "name": "Junior Engineer ( O&M)",
//                   "description": "Junior Engineer ( O&M)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_10",
//                   "name": "Superintending Engineer ( Light)",
//                   "description": "Superintending Engineer ( Light)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_11",
//                   "name": "Corporation Engineer (Light)",
//                   "description": "Corporation Engineer (Light)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_12",
//                   "name": "Junior Engineer ( Light)",
//                   "description": "Junior Engineer ( Light)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_13",
//                   "name": "Health Officer",
//                   "description": "Health Officer",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_14",
//                   "name": "Medical Officer",
//                   "description": "Medical Officer",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_15",
//                   "name": "Chief Sanitary Inspector",
//                   "description": "Mechanical Oversear",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_16",
//                   "name": "Sainitary Inspector",
//                   "description": "Clerk",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_17",
//                   "name": "Sainitary Supervisor",
//                   "description": "Accountant",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_18",
//                   "name": "Senior Town Planner",
//                   "description": "Senior Town Planner",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_19",
//                   "name": "Municipal Town Planner",
//                   "description": "Municipal Town Planner",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_20",
//                   "name": "Asst. Town Planner",
//                   "description": "Asst. Town Planner",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_21",
//                   "name": "Building Inspector",
//                   "description": "Building Inspector",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_22",
//                   "name": "Junior Enginer ( Horticulutre)",
//                   "description": "Junior Enginer ( Horticulutre)",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_23",
//                   "name": "Citizen service representative",
//                   "description": "Citizen service representative",
//                   "active": true
//               },
//               {
//                   "name": "Deputy Controller Finance and Accounts",
//                   "description": "Deputy Controller Finance and Accounts",
//                   "code": "DESIG_1001",
//                   "active": true
//               },
//               {
//                   "name": "Accountant",
//                   "description": "Accountant",
//                   "code": "DESIG_58",
//                   "active": true
//               },
//               {
//                   "code": "DESIG_24",
//                   "name": "Assistant Commissioner",
//                   "description": "Assistant Commissioner",
//                   "active": true
//               },
//               {
//                   "name": "Superintendent",
//                   "description": "Superintendent",
//                   "code": "DESIG_47",
//                   "active": true
//               },
//               {
//                   "name": "Accounts Officer",
//                   "description": "Accounts Officer",
//                   "code": "AO",
//                   "active": true
//               },
//               {
//                   "name": "Commissioner",
//                   "description": "Commissioner",
//                   "code": "COMM",
//                   "active": true
//               }
//           ],
//           "Department": [
//               {
//                   "name": "Street Lights",
//                   "code": "DEPT_1",
//                   "active": true
//               },
//               {
//                   "name": "Building & Roads",
//                   "code": "DEPT_2",
//                   "active": true
//               },
//               {
//                   "name": "Health & Sanitation",
//                   "code": "DEPT_3",
//                   "active": true
//               },
//               {
//                   "name": "Operation & Maintenance",
//                   "code": "DEPT_4",
//                   "active": true
//               },
//               {
//                   "name": "Horticulture",
//                   "code": "DEPT_5",
//                   "active": true
//               },
//               {
//                   "name": "Building Branch",
//                   "code": "DEPT_6",
//                   "active": true
//               },
//               {
//                   "name": "Citizen service desk",
//                   "code": "DEPT_7",
//                   "active": true
//               },
//               {
//                   "name": "Complaint Cell",
//                   "code": "DEPT_8",
//                   "active": true
//               },
//               {
//                   "name": "Executive Branch",
//                   "code": "DEPT_9",
//                   "active": true
//               },
//               {
//                   "name": "Others",
//                   "code": "DEPT_10",
//                   "active": true
//               },
//               {
//                   "name": "Tax Branch",
//                   "code": "DEPT_13",
//                   "active": true
//               },
//               {
//                   "name": "Accounts Branch",
//                   "code": "DEPT_25",
//                   "active": true
//               },
//               {
//                   "name": "Works Branch",
//                   "code": "DEPT_35",
//                   "active": true
//               }
//           ],
//           "Fund": [
//             {
//                 "name": "Fund One",
//                 "code": "FUND_1",
//                 "active": true
//             },
//             {
//               "name": "Fund Two",
//               "code": "FUND_2",
//               "active": true
//           },
//           {
//             "name": "Fund Three",
//             "code": "FUND_3",
//             "active": true
//           },
//           ],
//           "Chapter": [
//             {
//                 "name": "Chapter One",
//                 "code": "CHAPTER_1",
//                 "active": true
//             },
//             {
//               "name": "Chapter Two",
//               "code": "CHAPTER_2",
//               "active": true
//           },
//           {
//             "name": "Chapter Three",
//             "code": "CHAPTER_3",
//             "active": true
//         },{
//           "name": "Chapter Four",
//           "code": "CHAPTER_4",
//           "active": true
//       },
//       {
//         "name": "Chapter Five",
//         "code": "CHAPTER_5",
//         "active": true
//     },
//   ],
//   "Project": [
//     {
//         "name": "Project One",
//         "code": "PROJECT_1",
//         "active": true
//     },
//     {
//       "name": "Project Two",
//       "code": "PROJECT_2",
//       "active": true
//   },
//   {
//     "name": "Project Three",
//     "code": "PROJECT_3",
//     "active": true
// },{
//   "name": "Project Four",
//   "code": "PROJECT_4",
//   "active": true
// },
// {
// "name": "Project Five",
// "code": "PROJECT_5",
// "active": true
//     },
//           ],
//           "Unit": [
//             {
//                 "name": "Unit One",
//                 "code": "UNIT_1",
//                 "active": true
//             },
//             {
//               "name": "Unit Two",
//               "code": "UNIT_2",
//               "active": true
//           },
//           {
//             "name": "Unit Three",
//             "code": "UNIT_3",
//             "active": true
//         },{
//           "name": "Unit Four",
//           "code": "UNIT_4",
//           "active": true
//       },
//       {
//         "name": "Unit Five",
//         "code": "UNIT_5",
//         "active": true
// },
//           ],
//           "StateInfo": [
//               {
//                   "name": "Demo",
//                   "code": "pg",
//                   "qrCodeURL": "https://lh3.googleusercontent.com/-311gz2-xcHw/X6KRNSQTkWI/AAAAAAAAAKU/JmHSj-6rKPMVFbo6oL5x4JhYTTg8-UHmwCK8BGAsYHg/s0/2020-11-04.png",
//                   "bannerUrl": "https://upyog-assets.s3.ap-south-1.amazonaws.com/bannerImage.png",
//                   "logoUrl": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/nugp.png",
//                   "logoUrlWhite": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/nugp.png",
//                   "statelogo": "https://s3.ap-south-1.amazonaws.com/pg-egov-assets/pg.citya/logo.png",
//                   "hasLocalisation": true,
//                   "defaultUrl": {
//                       "citizen": "/user/register",
//                       "employee": "/user/login"
//                   },
//                   "languages": [
//                       {
//                           "label": "ENGLISH",
//                           "value": "en_IN"
//                       },
//                       {
//                           "label": "हिंदी",
//                           "value": "hi_IN"
//                       }
//                   ],
//                   "localizationModules": [
//                       {
//                           "label": "rainmaker-abg",
//                           "value": "rainmaker-abg"
//                       },
//                       {
//                           "label": "rainmaker-common",
//                           "value": "rainmaker-common"
//                       },
//                       {
//                           "label": "rainmaker-noc",
//                           "value": "rainmaker-noc"
//                       },
//                       {
//                           "label": "rainmaker-pt",
//                           "value": "rainmaker-pt"
//                       },
//                       {
//                           "label": "rainmaker-uc",
//                           "value": "rainmaker-uc"
//                       },
//                       {
//                           "label": "rainmaker-pgr",
//                           "value": "rainmaker-pgr"
//                       },
//                       {
//                           "label": "rainmaker-tl",
//                           "value": "rainmaker-tl"
//                       },
//                       {
//                           "label": "rainmaker-hr",
//                           "value": "rainmaker-hr"
//                       },
//                       {
//                           "label": "rainmaker-test",
//                           "value": "rainmaker-test"
//                       },
//                       {
//                           "label": "finance-erp",
//                           "value": "finance-erp"
//                       },
//                       {
//                           "label": "rainmaker-receipt",
//                           "value": "rainmaker-receipt"
//                       },
//                       {
//                           "label": "rainmaker-dss",
//                           "value": "rainmaker-dss"
//                       },
//                       {
//                           "label": "rainmaker-fsm",
//                           "value": "rainmaker-fsm"
//                       },
//                       {
//                           "label": "rainmaker-wms",
//                           "value": "rainmaker-wms"
//                       }
//                   ]
//               }
//           ],
//           "wfSlaConfig": [
//               {
//                   "slotPercentage": 33,
//                   "positiveSlabColor": "#4CAF50",
//                   "negativeSlabColor": "#F44336",
//                   "middleSlabColor": "#EEA73A"
//               }
//           ],
//           "uiHomePage": [
//               {
//                   "appBannerDesktop": {
//                       "code": "APP_BANNER_DESKTOP",
//                       "name": "App Banner Desktop View",
//                       "bannerUrl": "https://uat.digit.org/egov-uat-assets/app-banner-mobile.jpg",
//                       "enabled": true
//                   },
//                   "appBannerMobile": {
//                       "code": "APP_BANNER_MOBILE",
//                       "name": "App Banner Mobile View",
//                       "bannerUrl": "https://uat.digit.org/egov-uat-assets/app-banner-mobile.jpg",
//                       "enabled": true
//                   },
//                   "citizenServicesCard": {
//                       "code": "HOME_CITIZEN_SERVICES_CARD",
//                       "name": "Home Citizen services Card",
//                       "enabled": true,
//                       "headerLabel": "DASHBOARD_CITIZEN_SERVICES_LABEL",
//                       "sideOption": {
//                           "name": "DASHBOARD_VIEW_ALL_LABEL",
//                           "enabled": true,
//                           "navigationUrl": "/digit-ui/citizen/all-services"
//                       },
//                       "props": [
//                           {
//                               "code": "CITIZEN_SERVICE_PGR",
//                               "name": "Complaints",
//                               "label": "ES_PGR_HEADER_COMPLAINT",
//                               "enabled": true,
//                               "navigationUrl": "/digit-ui/citizen/pgr-home"
//                           },
//                           {
//                               "code": "CITIZEN_SERVICE_PT",
//                               "name": "Property Tax",
//                               "label": "MODULE_PT",
//                               "enabled": true,
//                               "navigationUrl": "/digit-ui/citizen/pt-home"
//                           },
//                           {
//                               "code": "CITIZEN_SERVICE_TL",
//                               "name": "Trade Licence",
//                               "label": "MODULE_TL",
//                               "enabled": true,
//                               "navigationUrl": "/digit-ui/citizen/tl-home"
//                           },
//                           {
//                               "code": "CITIZEN_SERVICE_WS",
//                               "name": "Water & Sewerage",
//                               "label": "ACTION_TEST_WATER_AND_SEWERAGE",
//                               "enabled": true,
//                               "navigationUrl": "/digit-ui/citizen/ws-home"
//                           },
//                           {
//                               "code": "CITIZEN_SERVICE_WMS",
//                               "name": "WMS",
//                               "label": "CITIZEN_SERVICE_WMS",
//                               "enabled": true,
//                               "navigationUrl": "/digit-ui/citizen/wms-home"
//                           }
//                       ]
//                   },
//                   "informationAndUpdatesCard": {
//                       "code": "HOME_CITIZEN_INFO_UPDATE_CARD",
//                       "name": "Home Citizen Information and Updates card",
//                       "enabled": true,
//                       "headerLabel": "CS_COMMON_DASHBOARD_INFO_UPDATES",
//                       "sideOption": {
//                           "name": "DASHBOARD_VIEW_ALL_LABEL",
//                           "enabled": true,
//                           "navigationUrl": ""
//                       },
//                       "props": [
//                           {
//                               "code": "CITIZEN_MY_CITY",
//                               "name": "My City",
//                               "label": "CS_HEADER_MYCITY",
//                               "enabled": true,
//                               "navigationUrl": ""
//                           },
//                           {
//                               "code": "CITIZEN_EVENTS",
//                               "name": "Events",
//                               "label": "EVENTS_EVENTS_HEADER",
//                               "enabled": true,
//                               "navigationUrl": "/digit-ui/citizen/engagement/events"
//                           },
//                           {
//                               "code": "CITIZEN_DOCUMENTS",
//                               "name": "Documents",
//                               "label": "CS_COMMON_DOCUMENTS",
//                               "enabled": true,
//                               "navigationUrl": "/digit-ui/citizen/engagement/docs"
//                           },
//                           {
//                               "code": "CITIZEN_SURVEYS",
//                               "name": "Surveys",
//                               "label": "CS_COMMON_SURVEYS",
//                               "enabled": true,
//                               "navigationUrl": "/digit-ui/citizen/engagement/surveys/list"
//                           }
//                       ]
//                   },
//                   "whatsNewSection": {
//                       "code": "WHATSNEW",
//                       "name": "What's New",
//                       "enabled": true,
//                       "headerLabel": "DASHBOARD_WHATS_NEW_LABEL",
//                       "sideOption": {
//                           "name": "DASHBOARD_VIEW_ALL_LABEL",
//                           "enabled": true,
//                           "navigationUrl": "/digit-ui/citizen/engagement/whats-new"
//                       }
//                   },
//                   "whatsAppBannerDesktop": {
//                       "code": "WHATSAPP_BANNER_DESKTOP",
//                       "name": "WhatsApp Banner Desktop View",
//                       "bannerUrl": "https://uat.digit.org/egov-uat-assets/whatsapp-web.jpg",
//                       "enabled": true,
//                       "navigationUrl": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva"
//                   },
//                   "whatsAppBannerMobile": {
//                       "code": "WHATSAPP_BANNER_MOBILE",
//                       "name": "WhatsApp Banner Mobile View",
//                       "bannerUrl": "https://uat.digit.org/egov-uat-assets/whatsapp-mobile.jpg",
//                       "enabled": true,
//                       "navigationUrl": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva"
//                   }
//               }
//           ],
//           "StaticData": [
//           {
//               "PT": {
//                   "payViaWhatsApp": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva",
//                   "helpline": {
//                       "contactOne": "01124643284",
//                       "contactTwo": "01124617543"
//                   },
//                   "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
//                   "staticDataOne": "7-10",
//                   "staticDataTwo": "₹500",
//                   "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
//               },
//               "TL": {
//                   "helpline": {
//                       "contactOne": "01124643284",
//                       "contactTwo": "01124617543"
//                   },
//                   "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
//                   "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
//               },
//               "MCOLLECT": {
//                   "helpline": {
//                       "contactOne": "01124643284",
//                       "contactTwo": "01124617543"
//                   },
//                   "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
//                   "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
//               },
//               "PGR": {
//                   "helpline": {
//                       "contactOne": "01124643284",
//                       "contactTwo": "01124617543"
//                   },
//                   "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
//                   "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
//               },
//               "OBPS": {
//                   "helpline": {
//                       "contactOne": "01124643284",
//                       "contactTwo": "01124617543"
//                   },
//                   "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
//                   "validity": "1",
//                   "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
//               },
//               "WS": {
//                   "payViaWhatsApp": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva",
//                   "helpline": {
//                       "contactOne": "01124643284",
//                       "contactTwo": "01124617543"
//                   },
//                   "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
//                   "staticDataOne": "30",
//                   "staticDataTwo": "25-30",
//                   "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
//               },
//               "WMS": {
//                   "payViaWhatsApp": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva",
//                   "helpline": {
//                       "contactOne": "01124643284",
//                       "contactTwo": "01124617543"
//                   },
//                   "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
//                   "staticDataOne": "30",
//                   "staticDataTwo": "25-30",
//                   "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
//               }
//           }
//           ]
//           },
//       "tenant": {
//           "tenants": [
//               {
//                   "code": "pg.citya",
//                   "name": "City A",
//                   "description": "City A",
//                   "pincode": [
//                       143001,
//                       143002,
//                       143003,
//                       143004,
//                       143005
//                   ],
//                   "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                   "imageId": null,
//                   "domainUrl": "https://www.upyog-test.niua.org",
//                   "type": "CITY",
//                   "twitterUrl": null,
//                   "facebookUrl": null,
//                   "emailId": "citya@gmail.com",
//                   "OfficeTimings": {
//                       "Mon - Fri": "9.00 AM - 6.00 PM"
//                   },
//                   "city": {
//                       "name": "City A",
//                       "localName": null,
//                       "districtCode": "CITYA",
//                       "districtName": null,
//                       "districtTenantCode": "pg.citya",
//                       "regionName": null,
//                       "ulbGrade": "Municipal Corporation",
//                       "longitude": 75.5761829,
//                       "latitude": 31.3260152,
//                       "shapeFileLocation": null,
//                       "captcha": null,
//                       "code": "1013",
//                       "ddrName": "DDR A"
//                   },
//                   "address": "City A Municipal Corporation",
//                   "contactNumber": "001-2345876"
//               },
//               {
//                   "code": "pg.cityb",
//                   "name": "City B",
//                   "description": null,
//                   "pincode": [
//                       143006,
//                       143007,
//                       143008,
//                       143009,
//                       143010
//                   ],
//                   "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                   "imageId": null,
//                   "domainUrl": "https://www.upyog-test.niua.org",
//                   "type": "CITY",
//                   "twitterUrl": null,
//                   "facebookUrl": null,
//                   "emailId": "cityb@gmail.com",
//                   "OfficeTimings": {
//                       "Mon - Fri": "9.00 AM - 6.00 PM",
//                       "Sat": "9.00 AM - 12.00 PM"
//                   },
//                   "city": {
//                       "name": "City B",
//                       "localName": null,
//                       "districtCode": "CITYB",
//                       "districtName": null,
//                       "districtTenantCode": "pg.cityb",
//                       "regionName": null,
//                       "ulbGrade": "Municipal Corporation",
//                       "longitude": 74.8722642,
//                       "latitude": 31.6339793,
//                       "shapeFileLocation": null,
//                       "captcha": null,
//                       "code": "107",
//                       "ddrName": "DDR B"
//                   },
//                   "address": "City B Municipal Corporation Address",
//                   "contactNumber": "0978-7645345",
//                   "helpLineNumber": "0654-8734567"
//               },
//               {
//                   "code": "pg.cityc",
//                   "name": "City C",
//                   "description": null,
//                   "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                   "imageId": null,
//                   "domainUrl": "https://www.upyog-test.niua.org",
//                   "type": "CITY",
//                   "twitterUrl": null,
//                   "facebookUrl": null,
//                   "emailId": "cityc@gmail.com",
//                   "OfficeTimings": {
//                       "Mon - Fri": "9.00 AM - 6.00 PM",
//                       "Sat": "9.00 AM - 12.00 PM"
//                   },
//                   "city": {
//                       "name": "City C",
//                       "localName": null,
//                       "districtCode": "CITYC",
//                       "districtName": null,
//                       "districtTenantCode": "pg.cityc",
//                       "regionName": null,
//                       "ulbGrade": "Municipal Corporation",
//                       "longitude": 73.8722642,
//                       "latitude": 31.6339793,
//                       "shapeFileLocation": null,
//                       "captcha": null,
//                       "code": "108",
//                       "ddrName": "DDR C"
//                   },
//                   "address": "City C Municipal Corporation Address",
//                   "contactNumber": "0978-7645345",
//                   "helpLineNumber": "0654-8734567"
//               },
//               {
//                   "code": "pg.cityd",
//                   "name": "City D",
//                   "description": null,
//                   "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                   "imageId": null,
//                   "domainUrl": "https://www.upyog-test.niua.org",
//                   "type": "CITY",
//                   "twitterUrl": null,
//                   "facebookUrl": null,
//                   "emailId": "cityd@gmail.com",
//                   "OfficeTimings": {
//                       "Mon - Fri": "9.00 AM - 6.00 PM",
//                       "Sat": "9.00 AM - 12.00 PM"
//                   },
//                   "city": {
//                       "name": "City D",
//                       "localName": null,
//                       "districtCode": "CITYD",
//                       "districtName": null,
//                       "districtTenantCode": "pg.cityd",
//                       "regionName": null,
//                       "ulbGrade": "Municipal Corporation",
//                       "longitude": 75.8722642,
//                       "latitude": 35.6339793,
//                       "shapeFileLocation": null,
//                       "captcha": null,
//                       "code": "109",
//                       "ddrName": "DDR D"
//                   },
//                   "address": "City D Municipal Corporation Address",
//                   "contactNumber": "0978-7645345",
//                   "helpLineNumber": "0654-8734567"
//               },
//               {
//                   "code": "pg.citye",
//                   "name": "City E",
//                   "description": null,
//                   "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
//                   "imageId": null,
//                   "domainUrl": "https://www.upyog-test.niua.org",
//                   "type": "CITY",
//                   "twitterUrl": null,
//                   "facebookUrl": null,
//                   "emailId": "citye@gmail.com",
//                   "OfficeTimings": {
//                       "Mon - Fri": "9.00 AM - 6.00 PM",
//                       "Sat": "9.00 AM - 12.00 PM"
//                   },
//                   "city": {
//                       "name": "City E",
//                       "localName": null,
//                       "districtCode": "CITYE",
//                       "districtName": null,
//                       "districtTenantCode": "pg.citye",
//                       "regionName": null,
//                       "ulbGrade": "Municipal Corporation",
//                       "longitude": 76.8722642,
//                       "latitude": 36.6339793,
//                       "shapeFileLocation": null,
//                       "captcha": null,
//                       "code": "110",
//                       "ddrName": "DDR E"
//                   },
//                   "address": "City E Municipal Corporation Address",
//                   "contactNumber": "0978-7645345",
//                   "helpLineNumber": "0654-8734567"
//               },
//               {
//                   "code": "pg",
//                   "name": "State",
//                   "description": "State",
//                   "logoId": "https://s3.ap-south-1.amazonaws.com/pg-egov-assets/pg.citya/logo.png",
//                   "imageId": null,
//                   "domainUrl": "www.upyog-test.niua.org",
//                   "type": "CITY",
//                   "twitterUrl": null,
//                   "facebookUrl": null,
//                   "emailId": "pg.state@gmail.com",
//                   "OfficeTimings": {
//                       "Mon - Fri": "9.00 AM - 5.00 PM"
//                   },
//                   "city": {
//                       "name": "State",
//                       "localName": "Demo State",
//                       "districtCode": "0",
//                       "districtName": "State",
//                       "districtTenantCode": "pg",
//                       "regionName": "State",
//                       "ulbGrade": "ST",
//                       "longitude": 75.3412,
//                       "latitude": 31.1471,
//                       "shapeFileLocation": null,
//                       "captcha": null,
//                       "code": "0",
//                       "ddrName": null
//                   },
//                   "address": "State Municipal Corporation",
//                   "contactNumber": "0978-7645345"
//               }
//           ],
//           "citymodule": [
//               {
//                   "module": "PGR",
//                   "code": "PGR",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PGR.png",
//                   "active": true,
//                   "order": 2,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               }, 
//               {
//               "module": "WMS",
//               "code": "WMS",
//               "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PGR.png",
//               "active": true,
//               "order": 2,
//              /*  "common-masters":{
//                 "Fund": [
//                   {
//                       "name": "Fund One",
//                       "code": "FUND_1",
//                       "active": true
//                   },
//                   {
//                     "name": "Fund Two",
//                     "code": "FUND_2",
//                     "active": true
//                 },
//                 {
//                   "name": "Fund Three",
//                   "code": "FUND_3",
//                   "active": true
//                 },
//                 ],
//                 "Chapter": [
//                   {
//                       "name": "Chapter One",
//                       "code": "CHAPTER_1",
//                       "active": true
//                   },
//                   {
//                     "name": "Chapter Two",
//                     "code": "CHAPTER_2",
//                     "active": true
//                 },
//                 {
//                   "name": "Chapter Three",
//                   "code": "CHAPTER_3",
//                   "active": true
//               },{
//                 "name": "Chapter Four",
//                 "code": "CHAPTER_4",
//                 "active": true
//             },
//             {
//               "name": "Chapter Five",
//               "code": "CHAPTER_5",
//               "active": true
//           },
//                 ],
//               }, */
//               "tenants": [
//                   {
//                       "code": "pg.citya"
//                   },
//                   {
//                       "code": "pg.cityb"
//                   },
//                   {
//                       "code": "pg.cityc"
//                   },
//                   {
//                       "code": "pg.cityd"
//                   },
//                   {
//                       "code": "pg.citye"
//                   }
//               ]
//               },
//               {
//                   "module": "PT",
//                   "code": "PT",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PT.png",
//                   "active": true,
//                   "order": 1,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "QuickPayLinks",
//                   "code": "QuickPayLinks",
//                   "active": true,
//                   "order": 1,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       }
//                   ]
//               },
//               {
//                   "module": "Finance",
//                   "code": "Finance",
//                   "active": false,
//                   "order": 4,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "TL",
//                   "code": "TL",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/TL.png",
//                   "active": true,
//                   "order": 2,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "FireNoc",
//                   "code": "FireNoc",
//                   "active": true,
//                   "order": 2,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "UC",
//                   "code": "UC",
//                   "active": false,
//                   "order": 2,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "BPAREG",
//                   "code": "BPAREG",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/OBPS.png",
//                   "active": true,
//                   "order": 2,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "BPAAPPLY",
//                   "code": "BPAAPPLY",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/OBPS.png",
//                   "active": true,
//                   "order": 2,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "PGR.WHATSAPP",
//                   "code": "PGR.WHATSAPP",
//                   "active": false,
//                   "order": 4,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       }
//                   ]
//               },
//               {
//                   "module": "OBPS",
//                   "code": "OBPS",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/OBPS.png",
//                   "active": true,
//                   "order": 12,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "FSM",
//                   "code": "FSM",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/FSM.png",
//                   "active": true,
//                   "order": 2,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "Payment",
//                   "code": "Payment",
//                   "active": true,
//                   "order": 1,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "Receipts",
//                   "code": "Receipts",
//                   "active": true,
//                   "order": 3,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               }/* ,
//               {
//                   "module": "NOC",
//                   "code": "NOC",
//                   "active": true,
//                   "order": 2,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "DSS",
//                   "code": "DSS",
//                   "active": true,
//                   "order": 6,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "Engagement",
//                   "code": "Engagement",
//                   "active": true,
//                   "order": 3,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "MCollect",
//                   "code": "MCollect",
//                   "active": true,
//                   "order": 1,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "HRMS",
//                   "code": "HRMS",
//                   "active": true,
//                   "order": 2,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               } */,
//               {
//                   "module": "CommonPT",
//                   "code": "CommonPT",
//                   "active": true,
//                   "order": 3,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "NDSS",
//                   "code": "NDSS",
//                   "active": true,
//                   "order": 5,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "WS",
//                   "code": "WS",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/WS.png",
//                   "active": true,
//                   "order": 1,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       }
//                   ]
//               },
//               {
//                   "module": "SW",
//                   "code": "SW",
//                   "active": true,
//                   "order": 1,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       }
//                   ]
//               }/* ,
//               {
//                   "module": "BillAmendment",
//                   "code": "BillAmendment",
//                   "active": true,
//                   "order": 1,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       }
//                   ]
//               },
//               {
//                   "module": "Bills",
//                   "code": "Bills",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/Bill.png",
//                   "active": true,
//                   "order": 3,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       }
//                   ]
//               },
//               {
//                   "module": "Birth",
//                   "code": "Birth",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PT.png",
//                   "active": true,
//                   "order": 1,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               },
//               {
//                   "module": "Death",
//                   "code": "Death",
//                   "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PT.png",
//                   "active": true,
//                   "order": 1,
//                   "tenants": [
//                       {
//                           "code": "pg.citya"
//                       },
//                       {
//                           "code": "pg.cityb"
//                       },
//                       {
//                           "code": "pg.cityc"
//                       },
//                       {
//                           "code": "pg.cityd"
//                       },
//                       {
//                           "code": "pg.citye"
//                       }
//                   ]
//               } */
//           ]
//           },
//       "DIGIT-UI": {}
//     };
    // const { MdmsRes } = await MdmsService.call(tenantId, mdmsDetails.details);
    const responseValue = transformResponse(mdmsDetails.type, MdmsRes, moduleCode.toUpperCase(), tenantId);
    const cacheSetting = getCacheSetting(mdmsDetails.details.moduleDetails[0].moduleName);
    PersistantStorage.set(key, responseValue, cacheSetting.cacheTimeInSecs);
    console.log("return data responseValue ",responseValue);
    return responseValue;
  },
  getServiceDefs: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getModuleServiceDefsCriteria(tenantId, moduleCode), moduleCode);
  },
  getSanitationType: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getSanitationTypeCriteria(tenantId, moduleCode), moduleCode);
  },
  getApplicationChannel: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getApplicationChannelCriteria(tenantId, moduleCode), moduleCode);
  },
  getPropertyType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getPropertyTypeCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getPropertyUsage: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getPropertyUsageCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getPropertySubtype: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getPropertyTypeCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getPitType: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getPitTypeCriteria(tenantId, moduleCode), moduleCode);
  },
  getVehicleType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getVehicleTypeCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getChecklist: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getChecklistCriteria(tenantId, moduleCode), moduleCode);
  },
  getPaymentRules: (tenantId, filter) => {
    return MdmsService.call(tenantId, getBillingServiceForBusinessServiceCriteria(filter));
  },

  getCustomizationConfig: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getConfig(tenantId, moduleCode), moduleCode);
  },
  getSlumLocalityMapping: (tenantId, moduleCode, type) =>
    MdmsService.getDataByCriteria(tenantId, getSlumLocalityCriteria(tenantId, moduleCode, type), moduleCode),

  getReason: (tenantId, moduleCode, type, payload) =>
    MdmsService.getDataByCriteria(tenantId, getReasonCriteria(tenantId, moduleCode, type, payload), moduleCode),

  getRoleStatus: (tenantId, moduleCode, type) =>
    MdmsService.getDataByCriteria(tenantId, getRoleStatusCriteria(tenantId, moduleCode, type), moduleCode),

  getCommonFieldsConfig: (tenantId, moduleCode, type, payload) =>
    MdmsService.getDataByCriteria(tenantId, getCommonFieldsCriteria(tenantId, moduleCode, type, payload), moduleCode),

  getPreFieldsConfig: (tenantId, moduleCode, type, payload) =>
    MdmsService.getDataByCriteria(tenantId, getPreFieldsCriteria(tenantId, moduleCode, type, payload), moduleCode),

  getPostFieldsConfig: (tenantId, moduleCode, type, payload) =>
    MdmsService.getDataByCriteria(tenantId, getPostFieldsCriteria(tenantId, moduleCode, type, payload), moduleCode),

  getPropertyOwnerShipCategory: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getPropertyOwnerShipCategoryCriteria(tenantId, moduleCode, type), moduleCode);
  },

  GetTradeOwnerShipCategory: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getTradeOwnerShipCategoryCriteria(tenantId, moduleCode, type), moduleCode);
  },

  getPropertyOwnerType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getPropertyOwnerTypeCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getPropertySubOwnerShipCategory: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getSubPropertyOwnerShipCategoryCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getDocumentRequiredScreen: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getDocumentRequiredScreenCategory(tenantId, moduleCode), moduleCode);
  },
  getTLDocumentRequiredScreen: (tenantId, moduleCode) => {
      return MdmsService.getDataByCriteria(tenantId, getDocumentRequiredScreenCategory(tenantId, moduleCode), moduleCode);
  },
  getTradeUnitsData: (tenantId, moduleCode, type, filter) => {
    return MdmsService.getDataByCriteria(tenantId, getTradeUnitsDataList(tenantId, moduleCode, type, filter), moduleCode);
  },
  getMapConfig: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getDefaultMapConfig(tenantId, moduleCode), moduleCode);
  },
  getUsageCategory: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getUsageCategoryList(tenantId, moduleCode), moduleCode);
  },
  getPTPropertyType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getPTPropertyTypeList(tenantId, moduleCode), moduleCode);
  },
  getTLStructureType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getTLStructureTypeList(tenantId, moduleCode), moduleCode);
  },
  getTLAccessoriesType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getTLAccessoriesTypeList(tenantId, moduleCode), moduleCode);
  },
  getTLFinancialYear: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getTLFinancialYearList(tenantId, moduleCode), moduleCode);
  },
  getFloorList: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getPTFloorList(tenantId, moduleCode, type), moduleCode);
  },
  getRentalDetails: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getRentalDetailsCategoryCriteria(tenantId, moduleCode), moduleCode);
  },
  getChargeSlabs: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getChargeSlabsCategoryCriteria(tenantId, moduleCode), moduleCode);
  },
  getDssDashboard: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getDssDashboardCriteria(tenantId, moduleCode), moduleCode);
  },
  getPaymentGateway: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getGeneralCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getReceiptKey: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getReceiptKey(tenantId, moduleCode), moduleCode);
  },
  getHelpText: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getGeneralCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getMCollectBillingService: (tenantId, moduleCode, type, filter) => {
    return MdmsService.getDataByCriteria(tenantId, getMCollectBillingServiceCriteria(tenantId, moduleCode, type, filter), moduleCode);
  },
  getMCollectApplcationStatus: (tenantId, moduleCode, type, filter) => {
    return MdmsService.getDataByCriteria(tenantId, getMCollectApplicationStatusCriteria(tenantId, moduleCode, type, filter), moduleCode);
  },
  getHrmsEmployeeRolesandDesignation: (tenantId) => {
    return MdmsService.call(tenantId, getHrmsEmployeeRolesandDesignations());
  },
  getHrmsEmployeeTypes: (tenantId, moduleCode, type, filter) => {
    return MdmsService.getDataByCriteria(tenantId, getGeneralCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getHrmsEmployeeReason: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getGeneralCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getMultipleTypes: (tenantId, moduleCode, types) => {
    return MdmsService.getDataByCriteria(tenantId, getMultipleTypes(tenantId, moduleCode, types), moduleCode);
  },
  getMultipleTypesWithFilter: (tenantId, moduleCode, types) => {
    return MdmsService.getDataByCriteria(tenantId, getMultipleTypesWithFilter(moduleCode, types), moduleCode);
  },
  getFSTPPlantInfo: (tenantId, moduleCode, types) => {
    return MdmsService.getDataByCriteria(tenantId, getFSTPPlantCriteria(tenantId, moduleCode, types), moduleCode);
  },
  getCancelReceiptReason: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getCancelReceiptReason(tenantId, moduleCode), moduleCode);
  },
  getReceiptStatus: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getReceiptStatus(tenantId, moduleCode), moduleCode);
  },
  getCancelReceiptReasonAndStatus: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getCancelReceiptReasonAndStatus(tenantId, moduleCode), moduleCode);
  },

  getGenderType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getGenderTypeList(tenantId, moduleCode, type), moduleCode);
  },

  TLGenderType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getGenderTypeList(tenantId, moduleCode, type), moduleCode);
  },

  PTGenderType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getGenderTypeList(tenantId, moduleCode, type), moduleCode);
  },

  HRGenderType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getGenderTypeList(tenantId, moduleCode, type), moduleCode);
  },
  WMSDepartment: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getDepartmentList(tenantId, moduleCode, type), moduleCode);
  },
  WMSFund: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getFundList(tenantId, moduleCode, type), moduleCode);
  },
  WMSChapter: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId,getChapterList(tenantId, moduleCode, type) , moduleCode);
  },
  WMSUnit: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId,getUnitList(tenantId, moduleCode, type) , moduleCode);
  },
  WMSProject: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId,getProjectList(tenantId, moduleCode, type) , moduleCode);
  },
  getDocumentTypes: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getDocumentTypesCriteria(tenantId, moduleCode, type), moduleCode);
  },

  getTradeTypeRoleTypes: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getTradeTypeRoleCriteria(tenantId, moduleCode, type), moduleCode);
  },

  getFSMGenderType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getGenderTypeList(tenantId, moduleCode, type), moduleCode);
  },

  getFSTPORejectionReason: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getFSTPORejectionReasonCriteria(tenantId, moduleCode, type), moduleCode);
  },

  getFSMPaymentType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getFSMPaymentTypeCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getBillsGenieKey: (tenantId, moduleCode) => {
    return MdmsService.getDataByCriteria(tenantId, getBillsGenieKey(tenantId, moduleCode), moduleCode);
  },

  getFSMTripNumber: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getFSMTripNumberCriteria(tenantId, moduleCode, type), moduleCode);
  },

  getFSMReceivedPaymentType: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getFSMReceivedPaymentTypeCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getWSTaxHeadMaster: (tenantId, moduleCode, type) => {
    return MdmsService.getDataByCriteria(tenantId, getWSTaxHeadMasterCritera(tenantId, moduleCode, type), moduleCode);
  },

  getMeterStatusType: (tenantId) => {
    return MdmsService.call(tenantId, getMeterStatusTypeList(tenantId));
  },

  getBillingPeriod: (tenantId) => {
    return MdmsService.call(tenantId, getBillingPeriodValidation(tenantId));
  },
  getHowItWorksJSONData: (tenantId) => {
    return MdmsService.call(tenantId, getHowItWorksJSON(tenantId));
  },
  getFAQsJSONData: (tenantId) => {
    return MdmsService.call(tenantId, getFAQsJSON(tenantId));
  },
  getDSSFAQsJSONData: (tenantId) => {
    return MdmsService.call(tenantId, getDSSFAQsJSON(tenantId));
  },
  
  getDSSAboutJSONData: (tenantId) => {
    return MdmsService.call(tenantId, getDSSAboutJSON(tenantId));
  },
  getStaticDataJSON: (tenantId) => {
    return MdmsService.call(tenantId, getStaticData());
  }
};
