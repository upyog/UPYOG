import { roundToNearestMinutes } from "date-fns/esm";
import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

const WMSService = {
  Scheme_Master:{
                  search: (tenantId, filters, searchParams) =>
                    Request({
                      url: Urls.wms.Scheme_Master.search,
                      useCache: false,
                      method: "GET",
                      auth: true,
                      userService: true,
                      params: { tenantId, ...filters, ...searchParams },
                    }),
                  create: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.Scheme_Master.create,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  update: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.Scheme_Master.update,
                      useCache: false,
                      method: "PUT",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  delete: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.Scheme_Master.delete,
                      useCache: false,
                      method: "DELETE",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  get: (tenantId) =>
                    Request({
                      url: Urls.wms.Scheme_Master.get,
                      useCache: false,
                      method: "GET",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
  },
  SORApplications:{
                  search: (tenantId, filters, searchParams) =>
                    Request({
                      url: Urls.wms.SORApplications.search,
                      useCache: false,
                      method: "GET",
                      auth: true,
                      userService: true,
                      params: { tenantId, ...filters, ...searchParams },
                    }),
                  create: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.SORApplications.create,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId },
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
                      method: "GET",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
  },
  Project_Master:{
                  search: (tenantId, filters, searchParams) =>
                    Request({
                      url: Urls.wms.Project_Master.search,
                      useCache: false,
                      method: "GET",
                      auth: true,
                      userService: true,
                      params: { tenantId, ...filters, ...searchParams },
                    }),
                  create: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.Project_Master.create,
                      useCache: false,
                      method: "POST",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  update: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.Project_Master.update,
                      useCache: false,
                      method: "PUT",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  delete: (data, tenantId) =>
                    Request({
                      data: data,
                      url: Urls.wms.Project_Master.delete,
                      useCache: false,
                      method: "DELETE",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
                  get: (tenantId) =>
                    Request({
                      url: Urls.wms.Project_Master.get,
                      useCache: false,
                      method: "GET",
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
                      method: "GET",
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
                      method: "GET",
                      auth: true,
                      userService: true,
                      params: { tenantId },
                    }),
  },
    },
};

export default WMSService;
