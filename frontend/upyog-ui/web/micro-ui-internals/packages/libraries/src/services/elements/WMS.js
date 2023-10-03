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

      getMasterSubTypeData: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsSubTypeGet,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      getSingleResordsMasterSubTypeData: (id) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsSubTypeGet+'?contractorId='+id,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      }),
      updateMasterSubTypeData: (data) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsSubTypeUpdate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      
      }),
      createMasterSubTypeData: (data) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsSubTypeCreate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      
      }),

      getMasterTypeData: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsTypeGet,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: {tenantId} ,
      }),
      getSingleResordsMasterTypeData: (id) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsTypeGet+'?vendorId='+id,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      }),
      updateMasterTypeData: (data) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsTypeUpdate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      
      }),
      createMasterTypeData: (data) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsTypeCreate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      
      }),


      getMasterData: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsBankGet,
        // url: Urls.wms.ContractorMaster.get+tenantId,
        useCache: false,
        method: "POST",
        // method: "GET",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      getSingleResordsMasterData: (id) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsBankGet+'?bankId='+id,
        useCache: false,
        method: "POST",
        // method: "GET",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      updateMasterData: (data) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsBankUpdate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: { tenantId },
      
      }),
      createMasterData: (data) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsBankCreate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: { tenantId },
      
      }),



//Fake json data getting Vendor Class ###############################
      getMasterClassData: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsVenderClassGet,
        // url: Urls.wms.ContractorMaster.get+tenantId,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),

      getSingleResordsMasterClassData: (tenantId) =>
      Request(
        // console.log("tenantId tenantId ",{tenantId,"ss":Urls.wms.ContractorMaster.mdmsVenderClassGet+'?vendor_id='+tenantId}),
        {
        url: Urls.wms.ContractorMaster.mdmsVenderClassGet+"?vendorClassId="+tenantId,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      createMasterClassData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsVenderClassCreate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
      
      updateMasterClassData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsVenderClassUpdate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),

      //Fake json data getting Account Head ###############################
      getAccountHeadData: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsAccountHeadGet,
        // url: Urls.wms.ContractorMaster.get+tenantId,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),

      getSingleResordsAccountHeadData: (tenantId) =>
      Request(
        // console.log("tenantId tenantId ",{tenantId,"ss":Urls.wms.ContractorMaster.mdmsVenderClassGet+'?vendor_id='+tenantId}),
        {
        url: Urls.wms.ContractorMaster.mdmsAccountHeadGet+'?primaryAccountheadId='+tenantId,    
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      createAccountHeadData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsAccountHeadCreate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
      
      updateAccountHeadData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsAccountHeadUpdate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),

      //Fake json data getting Function App ###############################
      getFunctionAppData: (tenantId) =>
      Request({
        url: Urls.wms.ContractorMaster.mdmsFunctionAppGet,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),

      getSingleResordsFunctionAppData: (tenantId) =>
      Request(
        // console.log("tenantId tenantId ",{tenantId,"ss":Urls.wms.ContractorMaster.mdmsFunctionAppGet+'?functionId='+tenantId}),
        {
        // url: Urls.wms.ContractorMaster.mdmsVenderClassGet+'/'+tenantId,
        url: Urls.wms.ContractorMaster.mdmsFunctionAppGet+'?functionId='+tenantId,    
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      createFunctionAppData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsFunctionAppCreate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
      
      updateFunctionAppData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.ContractorMaster.mdmsFunctionAppUpdate,
        // url: Urls.wms.ContractorMaster.mdmsFunctionAppUpdate+'/'+tenantId,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      })
},

TenderEntry:{
  create: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.Tender_Entry.create,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: { tenantId },
      }),
    update: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.Tender_Entry.update,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    delete: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.Tender_Entry.delete,
        useCache: false,
        method: "DELETE",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    get: (tenantId) =>
      Request({
        url: Urls.wms.Tender_Entry.get,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
      getSingle: (id) =>
      Request({
        url: Urls.wms.Tender_Entry.get+'?tenderId='+id,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      }),
      search: (tenantId, filters, searchParams) =>
      Request({
        url: Urls.wms.Tender_Entry.search,
        useCache: false,
        method: "GET",
        auth: true,
        userService: true,
        params: { tenantId, ...filters, ...searchParams },
      }),
}
};

export default WmsService;
