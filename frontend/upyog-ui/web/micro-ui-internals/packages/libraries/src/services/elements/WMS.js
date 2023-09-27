import { roundToNearestMinutes } from "date-fns/esm";
import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

const WmsService = {
  
  
  SORApplications:{
    search: (tenantId, filters, searchParams) =>
      Request({
        url: Urls.wms.SORApplications.search,
        useCache: false,
        method: "GET",//"POST",
        auth: true,
        userService: true,
        params:{...searchParams},// tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
      }),
    create: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SORApplications.create,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: {  },//TODO:#1 {tenantId}
      }),
    update: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SORApplications.update+"/"+data.sor_id,
        useCache: false,
        method: "PUT",
        auth: true,
        userService: true,
        params: {  },
      }),
    delete: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SORApplications.delete,
        useCache: false,
        method: "DELETE",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    get: (sorId,tenantId) =>
      Request({
        url: Urls.wms.SORApplications.get,
        useCache: false,
        method: "GET",
        auth: true,
        userService: true,
        params: { sor_id:sorId},
      }),
      count: () =>
      Request({
        url: Urls.wms.SORApplications.count,
        useCache: false,
        method:"GET",// "POST",
        auth: true,
        userService: true,
        params: {  },
      }),
},
  SCHApplications:{
    search: (tenantId, filters, searchParams) =>
      Request({
        url: Urls.wms.SCHApplications.search,
        useCache: false,
        method: "GET",//"POST",
        auth: true,
        userService: true,
        params:{...searchParams},// tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
      }),
    create: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SCHApplications.create,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: {  },//TODO:#1 {tenantId}
      }),
    update: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SCHApplications.update+"/"+data.scheme_id,
        useCache: false,
        method: "PUT",
        auth: true,
        userService: true,
        params: {  },
      }),
    delete: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SCHApplications.delete,
        useCache: false,
        method: "DELETE",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    get: (schId,tenantId) =>
      Request({
        url: Urls.wms.SCHApplications.get,
        useCache: false,
        method: "GET",
        auth: true,
        userService: true,
        params: { scheme_id:schId},
      }),
      count: () =>
      Request({
        url: Urls.wms.SCHApplications.count,
        useCache: false,
        method:"GET",// "POST",
        auth: true,
        userService: true,
        params: {  },
      }),
},
PHMApplications:{
    search: (tenantId, filters, searchParams) =>
      Request({
        url: Urls.wms.PHMApplications.search,
        useCache: false,
        method: "GET",//"POST",
        auth: true,
        userService: true,
        params:{...searchParams},// tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
      }),
    create: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.PHMApplications.create,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: {  },//TODO:#1 {tenantId}
      }),
    update: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.PHMApplications.update+"/"+data.phm_id,
        useCache: false,
        method: "PUT",
        auth: true,
        userService: true,
        params: {  },
      }),
    delete: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.PHMApplications.delete,
        useCache: false,
        method: "DELETE",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    get: (phmId,tenantId) =>
      Request({
        url: Urls.wms.PHMApplications.get,
        useCache: false,
        method: "GET",
        auth: true,
        userService: true,
        params: { phmeme_id:phmId},
      }),
      count: () =>
      Request({
        url: Urls.wms.PHMApplications.count,
        useCache: false,
        method:"GET",// "POST",
        auth: true,
        userService: true,
        params: {  },
      }),
},
  ContractorMaster:{
    search: (tenantId, filters, searchParams) =>
      Request({
        url: Urls.wms.ContractorMaster.search,
        useCache: false,
        method: "GET",
        auth: true,
        userService: true,
        params: { tenantId, ...filters, ...searchParams },
      }),
    create: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.create,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    update: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.update,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    delete: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.delete,
        useCache: false,
        method: "DELETE",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    get: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.get,
        useCache: false,
        method: "GET",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
      getList: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.get,
        useCache: false,
        method: "POST",
        // method: "GET",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
      getSingleData: (tenantId) =>
      Request({
        url: `${Urls.wms.ContractorMaster.get}/?vendorId=${tenantId}`,
        // url: Urls.wms.ContractorMaster.get,
        useCache: false,
        method: "POST",
        // method: "GET",
        auth: true,
        userService: true,
        params: {tenantId} ,
      }),
      getDataFilter: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.search+tenantId,
        // url: Urls.wms.ContractorMaster.get+tenantId,
        useCache: false,
        method: "POST",
        // method: "GET",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),

      getMasterData: (endPoint,tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsBank+''+endPoint,
        // url: Urls.wms.ContractorMaster.get+tenantId,
        useCache: false,
        method: "POST",
        // method: "GET",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),

      getMasterDataFakeAPI: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.mdms,
        // url: Urls.wms.ContractorMaster.get+tenantId,
        useCache: false,
        // method: "POST",
        method: "GET",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      getMasterSingleDataFakeAPI: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.mdms+'/'+tenantId,
        useCache: false,
        // method: "POST",
        method: "GET",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      createMasterDataFakeAPI: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdms,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
      
      updateMasterDataFakeAPI: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdms+'/'+tenantId,
        useCache: false,
        method: "PATCH",
        // method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),

      

},
};

export default WmsService;
