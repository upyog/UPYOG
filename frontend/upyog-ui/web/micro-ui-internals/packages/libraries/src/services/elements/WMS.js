import { roundToNearestMinutes } from "date-fns/esm";
import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

const WMSService = {
  SchemeMaster:{
                  search: (tenantId, filters, searchParams) =>
                    Request({
                      url: Urls.wms.SchemeMaster.search,
                      useCache: false,
                      method: "GET",
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
  ProjectMaster:{
                  search: (tenantId, filters, searchParams) =>
                    Request({
                      url: Urls.wms.ProjectMaster.search,
                      useCache: false,
                      method: "GET",
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
  }
};

export default WMSService;
