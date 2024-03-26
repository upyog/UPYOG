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
        auth: false,
        userService: false,
        params:{...searchParams},// tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
      }),
    create: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SORApplications.create,
        useCache: false,
        method: "POST",
        auth: true,
        userService: false,
        params: {  },//TODO:#1 {tenantId}
      }),
    update: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SORApplications.update+"/"+data.sor_id,
        useCache: false,
        method: "PUT",
        auth: true,
        userService: false,
        params: {  },
      }),
    delete: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SORApplications.delete,
        useCache: false,
        method: "DELETE",
        auth: true,
        userService: false,
        params: { tenantId },
      }),
    get: (sor_id) =>
      Request({
        url: Urls.wms.SORApplications.get+"/"+ sor_id,
        useCache: false,
        method: "GET",
        auth: false,
        userService: false,
        params:{},
      }),
      count: () =>
      Request({
        url: Urls.wms.SORApplications.count,
        useCache: false,
        method:"GET",// "POST",
        auth: false,
        userService: false,
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
        method: "POST",//"POST",
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
        method: "POST",
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
        method: "POST",
        auth: true,
        userService: true,
        params: { phmeme_id:phmId},
      }),
      count: () =>
      Request({
        url: Urls.wms.PHMApplications.count,
        useCache: false,
        method:"POST",// "POST",
        auth: true,
        userService: true,
        params: {  },
      }),
},

MBApplications:{
  search: (tenantId, filters, searchParams) =>
    Request({
      url: Urls.wms.MBApplications.search,
      useCache: false,
      method: "GET",//"POST",
      auth: true,
      userService: true,
      params:{...searchParams},// tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
    }),
  create: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.MBApplications.create,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {  },//TODO:#1 {tenantId}
    }),
  update: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.MBApplications.update+"/"+data.phm_id,
      useCache: false,
      method: "PUT",
      auth: true,
      userService: true,
      params: {  },
    }),
  delete: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.MBApplications.delete,
      useCache: false,
      method: "DELETE",
      auth: true,
      userService: true,
      params: { tenantId },
    }),
  get: (phmId,tenantId) =>
    Request({
      url: Urls.wms.MBApplications.get,
      useCache: false,
      method: "GET",
      auth: true,
      userService: true,
      params: { phmeme_id:phmId},
    }),
    count: () =>
    Request({
      url: Urls.wms.MBApplications.count,
      useCache: false,
      method:"GET",// "POST",
      auth: true,
      userService: true,
      params: {  },
    }),
},

PMAApplications:{
  search: (tenantId, filters, searchParams) =>
    Request({
      url: Urls.wms.PMAApplications.search,
      useCache: false,
      method: "POST",//"POST",
      auth: true,
      userService: true,
      params:{...searchParams},// tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
    }),
  create: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.PMAApplications.create,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {  },//TODO:#1 {tenantId}
    }),
  update: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.PMAApplications.update+"/"+data.pma_id,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {  },
    }),
  delete: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.PMAApplications.delete,
      useCache: false,
      method: "DELETE",
      auth: true,
      userService: true,
      params: { tenantId },
    }),
  get: (pmaId,tenantId) =>
    Request({
      url: Urls.wms.PMAApplications.get,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: { pmaeme_id:pmaId},
    }),
    count: () =>
    Request({
      url: Urls.wms.PMAApplications.count,
      useCache: false,
      method:"POST",// "POST",
      auth: true,
      userService: true,
      params: {  },
    }),
},
DRApplications:{
  search: (tenantId, filters, searchParams) =>
    Request({
      url: Urls.wms.DRApplications.search,
      useCache: false,
      method: "GET",//"POST",
      auth: true,
      userService: true,
      params:{...searchParams},// tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
    }),
  create: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.DRApplications.create,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {  },//TODO:#1 {tenantId}
    }),
  update: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.DRApplications.update+"/"+data.dr_id,
      useCache: false,
      method: "PUT",
      auth: true,
      userService: true,
      params: {  },
    }),
  delete: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.DRApplications.delete,
      useCache: false,
      method: "DELETE",
      auth: true,
      userService: true,
      params: { tenantId },
    }),
  get: (phmId,tenantId) =>
    Request({
      url: Urls.wms.DRApplications.get,
      useCache: false,
      method: "GET",
      auth: true,
      userService: true,
      params: { phmeme_id:drId},
    }),
    count: () =>
    Request({
      url: Urls.wms.DRApplications.count,
      useCache: false,
      method:"GET",// "POST",
      auth: true,
      userService: true,
      params: {  },
    }),
},

PRApplications:{
  search: (tenantId, filters, searchParams) =>
    Request({
      url: Urls.wms.PRApplications.search,
      useCache: false,
      method: "POST",//"POST",
      auth: true,
      userService: true,
      params:{...searchParams},// tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
    }),
  create: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.PRApplications.create,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {  },//TODO:#1 {tenantId}
    }),
  update: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.PRApplications.update+"/"+data.pr_id,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {  },
    }),
  delete: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.PRApplications.delete,
      useCache: false,
      method: "DELETE",
      auth: true,
      userService: true,
      params: { tenantId },
    }),
  get: (prId,tenantId) =>
    Request({
      url: Urls.wms.PRApplications.get,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: { phmeme_id:prId},
    }),
    count: () =>
    Request({
      url: Urls.wms.PRApplications.count,
      useCache: false,
      method:"POST",// "POST",
      auth: true,
      userService: true,
      params: {  },
    }),
},

WSRApplications:{
  search: (tenantId, filters, searchParams) =>
    Request({
      url: Urls.wms.WSRApplications.search,
      useCache: false,
      method: "POST",//"POST",
      auth: true,
      userService: true,
      params:{...searchParams},// tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
    }),
  create: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.WSRApplications.create,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {  },//TODO:#1 {tenantId}
    }),
  update: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.WSRApplications.update+"/"+data.wsr_id,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {  },
    }),
  delete: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.WSRApplications.delete,
      useCache: false,
      method: "DELETE",
      auth: true,
      userService: true,
      params: { tenantId },
    }),
  get: (wsrId,tenantId) =>
    Request({
      url: Urls.wms.WSRApplications.get,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: { phmeme_id:wsrId},
    }),
    count: () =>
    Request({
      url: Urls.wms.WSRApplications.count,
      useCache: false,
      method:"POST",// "POST",
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



// data getting Vendor Class ###############################
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

      // data getting Account Head ###############################
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

      // data getting Function App ###############################
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
      search: (data) =>
      // console.log("Search tender List ", {tenantId, filters, searchParams}),
       Request({
        url: Urls.wms.Tender_Entry.search+data,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: { tenantId, filters, searchParams },
      }),


      // data getting Department ###############################
      getDepartmentMasterData: (tenantId) =>
      Request({
        url: Urls.wms.Tender_Entry.mdmsdepartmentGet,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),

      getDepartmentSingleResordsMasterData: (tenantId) =>
      Request(
        {
        url: Urls.wms.Tender_Entry.mdmsdepartmentGet+'?deptId='+tenantId,    
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      updateDepartmentMasterData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.Tender_Entry.mdmsdepartmentUpdate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
      
      createDepartmentMasterData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.Tender_Entry.mdmsdepartmentCreate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),

      // data getting Tender Category ###############################
      getTenderCategoryMasterData: (tenantId) =>
      Request({
        url: Urls.wms.Tender_Entry.mdmsTCategoryGet,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),

      getTenderCategorySingleResordsMasterData: (tenantId) =>
      Request(
        {
        url: Urls.wms.Tender_Entry.mdmsTCategoryGet+'?categoryId='+tenantId,    
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),
      updateTenderCategoryMasterData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.Tender_Entry.mdmsTCategoryUpdate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
      
      createTenderCategoryMasterData: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.Tender_Entry.mdmsTCategoryCreate,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),

      getProjectName: (tenantId) =>
      Request({
        url: Urls.wms.Tender_Entry.ProjectName,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        // params: {tenantId} ,
      }),   
  },
  ContractorAgreement:{
    create: (data, tenantId) =>
        Request({
          data: data,
          url: Urls.wms.Contractor_Agreement.create,
          useCache: false,
          method: "POST",
          auth: true,
          userService: true,
          // params: { tenantId },
        }),
      update: (data, tenantId) =>
        Request({
          data: data,
          url: Urls.wms.Contractor_Agreement.update,
          useCache: false,
          method: "POST",
          auth: true,
          userService: true,
          params: { tenantId },
        }),
      delete: (data, tenantId) =>
        Request({
          data: data,
          url: Urls.wms.Contractor_Agreement.delete,
          useCache: false,
          method: "DELETE",
          auth: true,
          userService: true,
          params: { tenantId },
        }),
      getList: (tenantId) =>
        Request({
          url: Urls.wms.Contractor_Agreement.get,
          useCache: false,
          method: "POST",
          auth: true,
          userService: true,
          params: { tenantId },
        }),
        getSingle: (id) =>
        Request({
          url: Urls.wms.Contractor_Agreement.get+'?agreementNo='+id,
          useCache: false,
          method: "POST",
          auth: true,
          userService: true,
        }),
        search: (data) =>
        // console.log("Search tender List ", {tenantId, filters, searchParams}),
         Request({
          url: Urls.wms.Contractor_Agreement.search+data,
          useCache: false,
          method: "POST",
          auth: true,
          userService: true,
          // params: { tenantId, filters, searchParams },
        }),



        createFake: (data, tenantId) =>
        Request({
          data: data,
          url: Urls.wms.Contractor_Agreement.createFake,
          useCache: false,
          method: "POST",
          auth: true,
          userService: true,
          // params: { tenantId },
        }),

        getSingleFake: (tenantId) =>
          // console.log("bankList sttepr single data WMS tenantId "),
         Request({
          // url: Urls.wms.Contractor_Agreement.createFake/tenantId,
          url: Urls.wms.Contractor_Agreement.createFake+'/'+tenantId,
          useCache: false,
          method: "GET",
          auth: true,
          userService: true,
          // params: { tenantId },
        }),
        
        
        getListFake: (tenantId) =>
          // console.log("bankList sttepr single data WMS tenantId "),
         Request({
          // url: Urls.wms.Contractor_Agreement.createFake/tenantId,
          url: Urls.wms.Contractor_Agreement.createFake,
          useCache: false,
          method: "GET",
          auth: true,
          userService: true,
          // params: { tenantId },
        }),

        updateFake: (data,id,tenantId) =>
        // console.log('bankList sttepr single data update WMS data ',data,id)
        Request({
          data: data,
          url: Urls.wms.Contractor_Agreement.createFake+'/'+id,
          useCache: false,
          method: "PUT",
          auth: true,
          userService: true,
          // params: { tenantId },
        }),
        
        //  Request({
        //   // url: Urls.wms.Contractor_Agreement.createFake/tenantId,
        //   url: Urls.wms.Contractor_Agreement.createFake+'/'+tenantId,
        //   useCache: false,
        //   method: "PUT",
        //   auth: true,
        //   userService: true,
        //   // params: { tenantId },

          
        // }),

      //   getTenderCategorySingleResordsMasterData: (tenantId) =>
      // Request(
      //   {
      //   url: Urls.wms.Tender_Entry.mdmsTCategoryGet+'?categoryId='+tenantId,    
      //   useCache: false,
      //   method: "POST",
      //   auth: true,
      //   userService: true,
      //   // params: {tenantId} ,
      // }),
      
    },

    RunningAccountFinalBill:{
      getPreviousBill: (tenantId) =>
          // console.log("bankList sttepr single data WMS tenantId "),
         Request({
          // url: Urls.wms.Contractor_Agreement.createFake/tenantId,
          url: Urls.wms.Running_Account_Final_Bill.getPreviousBill,
          useCache: false,
          method: "GET",
          auth: true,
          userService: true,
          // params: { tenantId },
        }),
        createPreviousBill: (data, tenantId) =>
        // console.log("true data useWmsRAFBCreate WMS ",data),
        Request({
          data: data,
          url: Urls.wms.Running_Account_Final_Bill.createPreviousBill,
          useCache: false,
          method: "POST",
          auth: true,
          userService: true,
          // params: { tenantId },
        }),
    }
};

export default WmsService;
