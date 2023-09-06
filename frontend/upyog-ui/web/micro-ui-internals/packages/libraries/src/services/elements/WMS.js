import { roundToNearestMinutes } from "date-fns/esm";
import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

const WmsService = {
  
  SORApplications1:{
                  search: (data,tenantId, filters, searchParams) =>
                    Request({
                      data: data,
                      url: Urls.wms.SORApplications.search,
                      useCache: false,
                      method: "POST",//"POST",
                      auth: true,
                      userService: true,
                      params:{sorId:searchParams.sor_name}//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
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
                      url: Urls.wms.SORApplications.update,
                      useCache: false,
                      method: "PUT",
                      auth: true,
                      userService: true,
                      params: { tenantId },
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
                  get: (tenantId) =>
                    Request({
                      url: Urls.wms.SORApplications.get,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId },
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
  SORApplications:{
    search: (tenantId, filters, searchParams) =>
      Request({
        url: Urls.wms.SORApplications.search,
        useCache: false,
        method: "GET",//"POST",
        auth: true,
        userService: true,
        params:{ tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
      }),
    create: (data, tenantId) =>
      Request({
        data: data.ScheduleOfRateApplication[0],
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
        url: Urls.wms.SORApplications.update,
        useCache: false,
        method: "PUT",
        auth: true,
        userService: true,
        params: { tenantId },
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
    get: (tenantId,sor_id) =>
      Request({
        url: Urls.wms.SORApplications.get,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { sor_id:sor_id },
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
PMApplications:{
  search: (tenantId, filters, searchParams) =>
    Request({
      url: Urls.wms.PMApplications.search,
      useCache: false,
      method: "GET",//"POST",
      auth: true,
      userService: true,
      params:{ tenantId, ...filters, ...searchParams }//TODO:#1 Actual API needs to attach  { tenantId, ...filters, ...searchParams },
    }),
  create: (data, tenantId) =>
    Request({
      data: data.PhysicalMilestone[0],
      url: Urls.wms.PMApplications.create,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: {  },//TODO:#1 {tenantId}
    }),
  update: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.PMApplications.update,
      useCache: false,
      method: "PUT",
      auth: true,
      userService: true,
      params: { tenantId },
    }),
  delete: (data, tenantId) =>
    Request({
      data: data,
      url: Urls.wms.PMApplications.delete,
      useCache: false,
      method: "DELETE",
      auth: true,
      userService: true,
      params: { tenantId },
    }),
  get: (tenantId,sor_id) =>
    Request({
      url: Urls.wms.PMApplications.get,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: { sor_id:sor_id },
    }),
    count: () =>
    Request({
      url: Urls.wms.PMApplications.count,
      useCache: false,
      method:"GET",// "POST",
      auth: true,
      userService: true,
      params: {  },
    }),
},
  SchemeMaster:{
    search: (tenantId, filters, searchParams) =>
      Request({
        url: Urls.wms.SchemeMaster.search,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId, ...filters, ...searchParams },
      }),
    create: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SchemeMaster.create,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    update: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SchemeMaster.update,
        useCache: false,
        method: "PUT",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    delete: (data, tenantId) =>
      Request({
        data: data,
        url: Urls.wms.SchemeMaster.delete,
        useCache: false,
        method: "DELETE",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
    get: (tenantId) =>
      Request({
        url: Urls.wms.SchemeMaster.get,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
      }),
},
  ProjectMaster:{
                  search: (tenantId, filters, searchParams) =>
                    Request({
                      url: Urls.wms.ProjectMaster.search,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId, ...filters, ...searchParams },
                    }),
                  create: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.ProjectMaster.create,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  update: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.ProjectMaster.update,
                      useCache: false,
                      method: "PUT",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  delete: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.ProjectMaster.delete,
                      useCache: false,
                      method: "DELETE",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  get: (tenantId) =>
                    Request({
                      url: Urls.wms.ProjectMaster.get,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
  },
  Work:{
                  search: (tenantId, filters, searchParams) =>
                    Request({
                      url: Urls.wms.Work.search,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId, ...filters, ...searchParams },
                    }),
                  create: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.Work.create,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  update: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.Work.update,
                      useCache: false,
                      method: "PUT",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  delete: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.Work.delete,
                      useCache: false,
                      method: "DELETE",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  get: (tenantId) =>
                    Request({
                      url: Urls.wms.Work.get,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
  },
};

export default WmsService;
