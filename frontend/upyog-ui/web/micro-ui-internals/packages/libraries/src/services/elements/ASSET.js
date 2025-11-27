import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";


export const ASSETService = {
  create: (details, tenantId) =>
    Request({
      url: Urls.asset.create,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
  assetDisposedCreate: (details, tenantId) =>
    Request({
      url: Urls.asset.assetDisposedCreate,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
  search: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.asset.search,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),
  update: (details, tenantId) =>
    Request({
      url: Urls.asset.update,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
  assign: (details, tenantId) =>
    Request({
      url: Urls.asset.assign,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
  depriciationProcess: (details, tenantId) =>
    Request({
      url: Urls.asset.depriciationProcess,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
  depriciationList: (details, tenantId) =>
    Request({
      url: Urls.asset.depriciationList,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
    maintenanceList: (details, tenantId) =>
      Request({
        url: Urls.asset.maintenanceList,
        data: details,
        useCache: false,
        setTimeParam: false,
        userService: true,
        method: "POST",
        params: {},
        auth: true,
      }),
    maintenance: (details, tenantId) =>
    Request({
      url: Urls.asset.maintenance,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
    edit_maintenance: (details, tenantId) =>
      Request({
        url: Urls.asset.edit_maintenance,
        data: details,
        useCache: false,
        setTimeParam: false,
        userService: true,
        method: "POST",
        params: {},
        auth: true,
      }),
    disposalList: (details, tenantId) =>
      Request({
        url: Urls.asset.disposalList,
        data: details,
        useCache: false,
        setTimeParam: false,
        userService: true,
        method: "POST",
        params: {},
        auth: true,
      }),
      assetAssignable: (details, tenantId) =>
        Request({
          url: Urls.asset.assetAssignable,
          data: details,
          useCache: false,
          setTimeParam: false,
          userService: true,
          method: "POST",
          params: {},
          auth: true,
        }),
  return_asset: (details, tenantId) =>
    Request({
      url: Urls.asset.return_asset,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
  inventoryVendor: (details, tenantId) =>
    Request({
      url: Urls.asset.inventoryVendor,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
  createProcurement: (details, tenantId) =>
    Request({
      url: Urls.asset.createProcurement,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
  inventoryCreation: (details, tenantId) =>
    Request({
      url: Urls.asset.inventoryCreation,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
  assetRegistry: (details, tenantId) =>
    Request({
      url: Urls.asset.assetRegistry,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
    assetVendorList: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.asset.assetVendorList,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      data: {
        RequestInfo: {
          apiId: "Rainmaker"
        },
        Vendor: {
          tenantId: tenantId,
          ...filters
        }
      }
    }),
     updateAssetVendor: (details, tenantId) =>
      Request({
        url: Urls.asset.updateAssetVendor,
        data: details,
        useCache: false,
        setTimeParam: false,
        userService: true,
        method: "POST",
        params: {},
        auth: true,
      }),
  procurementList: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.asset.procurementList,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      data: {
        RequestInfo: {
          apiId: "Rainmaker"
        },
        ProcurementRequest: {
          tenantId: tenantId,
          ...filters
        }
      }
    }),
  updateProcurement: (details, tenantId) =>
      Request({
        url: Urls.asset.updateProcurement,
        data: details,
        useCache: false,
        setTimeParam: false,
        userService: true,
        method: "POST",
        params: {},
        auth: true,
      }),
};