//HAVE TO CHANGE THI
import { ApiCacheService } from "../atoms/ApiCacheService";
import Urls from "../atoms/urls";
import { Request, ServiceRequest } from "../atoms/Utils/Request";
import { PersistantStorage } from "../atoms/Utils/Storage";


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
        masterDetails: [{ name: "Department" }, { name: "Designation" }, { name: "StateInfo" }, { name: "wfSlaConfig" }, { name: "uiHomePage" }],
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

const getMasterDataCategory = (tenantId, moduleCode, masterName, type) => ({
  type,
  details: {
    tenantId: tenantId,
    moduleDetails: [
      {
        moduleName: moduleCode,
        masterDetails: [
          {
            name: masterName,
          },
        ],
      },
    ],
  },
});

const getDataWithi18nkey = (MdmsRes, moduleName, masterName, i18nKeyString) => {
  return MdmsRes[moduleName][masterName].filter((row) => row.active).map((item) => {
    return {
      ...item,
      i18nKey: `${i18nKeyString + item.name}`,
    };
  });
};

const getDataWithi18nkeyandCode = (MdmsRes, moduleName, masterName, i18nKeyString) => {
  return MdmsRes[moduleName][masterName].filter((row) => row.active).map((item) => {
    return {
      ...item,
      i18nKey: `${i18nKeyString + item.name}`,
      code: item.code
    };
  });
};

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

const getGenderType = (MdmsRes) => {
  return MdmsRes["common-masters"].GenderType.filter((GenderType) => GenderType.active).map((genderDetails) => {
    return {
      ...genderDetails,
      i18nKey: `PT_COMMON_GENDER_${genderDetails.code}`,
    };
  });
};


const GetRoleStatusMapping = (MdmsRes) => MdmsRes["DIGIT-UI"].RoleStatusMapping;


const transformResponse = (type, MdmsRes, moduleCode, moduleName, tenantId, masterName, i18nKeyString) => {
  switch (type) {
    case "citymodule":
      return GetCitiesWithi18nKeys(MdmsRes, moduleCode);
    case "egovLocation":
      return GetEgovLocations(MdmsRes);
    
    case "Reason":
      return GetReasonType(MdmsRes, type, moduleCode);
    case "RoleStatusMapping":
      return GetRoleStatusMapping(MdmsRes);
    case "GenderType":
      return getGenderType(MdmsRes);
    case "i18nKey":
      return getDataWithi18nkey(MdmsRes, moduleName, masterName, i18nKeyString);
    case "i18nkey&code":
      return getDataWithi18nkeyandCode(MdmsRes, moduleName, masterName, i18nKeyString);
    default:
      return MdmsRes;
  }
};

const getCacheSetting = (moduleName) => {
  return ApiCacheService.getSettingByServiceUrl(Urls.MDMSV2, moduleName);
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

export const MdmsServiceV2 = {
  init: (stateCode) =>
    ServiceRequest({
      serviceName: "mdmsInit",
      url: Urls.MDMSV2,
      data: initRequestBody(stateCode),
      useCache: true,
      params: { tenantId: stateCode },
    }),
  call: (tenantId, details) => {
    return new Promise((resolve, reject) =>
      debouncedCall(
        {
          serviceName: "mdmsCall",
          url: Urls.MDMSV2,
          data: getCriteria(tenantId, details),
          useCache: true,
          params: { tenantId },
        },
        resolve,
        reject
      )
    );
  },
  getDataByCriteria: async (tenantId, mdmsDetails, moduleCode, masterName, i18nKeyString) => {
    const moduleName = moduleCode; // moduleName is used here to pass unchanged modulecode
    const key = `MDMS.${tenantId}.${moduleCode}.${mdmsDetails.type}.${JSON.stringify(mdmsDetails.details)}`;
    const inStoreValue = PersistantStorage.get(key);
    if (inStoreValue) {
      return inStoreValue;
    }
    const { MdmsRes } = await MdmsServiceV2.call(tenantId, mdmsDetails.details);
    const responseValue = transformResponse(mdmsDetails.type, MdmsRes, moduleCode.toUpperCase(), moduleName, tenantId, masterName, i18nKeyString);
    const cacheSetting = getCacheSetting(mdmsDetails.details.moduleDetails[0].moduleName);
    PersistantStorage.set(key, responseValue, cacheSetting.cacheTimeInSecs);
    return responseValue;
  },
  getServiceDefs: (tenantId, moduleCode) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getModuleServiceDefsCriteria(tenantId, moduleCode), moduleCode);
  },
  

  getCustomizationConfig: (tenantId, moduleCode) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getConfig(tenantId, moduleCode), moduleCode);
  },
  getReason: (tenantId, moduleCode, type, payload) =>
    MdmsServiceV2.getDataByCriteria(tenantId, getReasonCriteria(tenantId, moduleCode, type, payload), moduleCode),

  getRoleStatus: (tenantId, moduleCode, type) =>
    MdmsServiceV2.getDataByCriteria(tenantId, getRoleStatusCriteria(tenantId, moduleCode, type), moduleCode),

  getCommonFieldsConfig: (tenantId, moduleCode, type, payload) =>
    MdmsServiceV2.getDataByCriteria(tenantId, getCommonFieldsCriteria(tenantId, moduleCode, type, payload), moduleCode),

  getPreFieldsConfig: (tenantId, moduleCode, type, payload) =>
    MdmsServiceV2.getDataByCriteria(tenantId, getPreFieldsCriteria(tenantId, moduleCode, type, payload), moduleCode),

  getPostFieldsConfig: (tenantId, moduleCode, type, payload) =>
    MdmsServiceV2.getDataByCriteria(tenantId, getPostFieldsCriteria(tenantId, moduleCode, type, payload), moduleCode),

  
  getHrmsEmployeeRolesandDesignation: (tenantId) => {
    return MdmsServiceV2.call(tenantId, getHrmsEmployeeRolesandDesignations());
  },
  getHrmsEmployeeTypes: (tenantId, moduleCode, type, filter) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getGeneralCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getHrmsEmployeeReason: (tenantId, moduleCode, type) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getGeneralCriteria(tenantId, moduleCode, type), moduleCode);
  },
  getMultipleTypes: (tenantId, moduleCode, types) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getMultipleTypes(tenantId, moduleCode, types), moduleCode);
  },
  getMultipleTypesWithFilter: (tenantId, moduleCode, types) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getMultipleTypesWithFilter(moduleCode, types), moduleCode);
  },
  getCancelReceiptReason: (tenantId, moduleCode) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getCancelReceiptReason(tenantId, moduleCode), moduleCode);
  },
  getReceiptStatus: (tenantId, moduleCode) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getReceiptStatus(tenantId, moduleCode), moduleCode);
  },
  getCancelReceiptReasonAndStatus: (tenantId, moduleCode) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getCancelReceiptReasonAndStatus(tenantId, moduleCode), moduleCode);
  },

  getGenderType: (tenantId, moduleCode, type) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getGenderTypeList(tenantId, moduleCode, type), moduleCode);
  },

  getHowItWorksJSONData: (tenantId) => {
    return MdmsServiceV2.call(tenantId, getHowItWorksJSON(tenantId));
  },
  getFAQsJSONData: (tenantId) => {
    return MdmsServiceV2.call(tenantId, getFAQsJSON(tenantId));
  },
  getStaticDataJSON: (tenantId) => {
    return MdmsServiceV2.call(tenantId, getStaticData());
  },

  /**
   * getMasterData - Fetches master data based on the provided criteria.
   * 
   * @param {string} tenantId - The ID of the tenant for which the data is being fetched.
   * @param {string} moduleCode - The module code associated with the master data.
   * @param {string} masterName - The name of the master data to be fetched.
   * @param {string} type - The type to be passed in switch case for fetching filtered data.
   * 
   * @description
   * This function retrieves master data by calling the `MdmsServiceV2.getDataByCriteria` method.
   * It constructs the criteria for fetching the data using the `getMasterDataCategory` function,
   * which is passed the tenantId, moduleCode, masterName, and type as parameters.
   */
  getMasterData: (tenantId, moduleCode, masterName, i18nKeyString = "", type) => {
    return MdmsServiceV2.getDataByCriteria(tenantId, getMasterDataCategory(tenantId, moduleCode, masterName, type), moduleCode, masterName, i18nKeyString);
  },
};
