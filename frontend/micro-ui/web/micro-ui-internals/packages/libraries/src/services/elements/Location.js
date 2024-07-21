import Urls from "../atoms/urls";
import { ServiceRequest } from "../atoms/Utils/Request";

export const LocationService = {
  getLocalities: (tenantId) => {
    return ServiceRequest({
      serviceName: "getLocalities",
      url: Urls.location.localities,
      params: { tenantId: tenantId },
      useCache: true,
    });
  },
  getZones: (tenantId) => {
    return ServiceRequest({
      serviceName: "getZones",
      url: Urls.location.zones,
      params: { tenantId: tenantId },
      useCache: true,
    });
  },
  getBlocks: (tenantId) => {
    return ServiceRequest({
      serviceName: "getBlocks",
      url: Urls.location.blocks,
      params: { tenantId: tenantId },
      useCache: true,
    });
  },
  getRevenueLocalities: async (tenantId) => {
    const response = await ServiceRequest({
      serviceName: "getRevenueLocalities",
      url: Urls.location.revenue_localities,
      params: { tenantId: tenantId },
      useCache: true,
    });
    return response;
  },
  getWards: (tenantId) => {
    return ServiceRequest({
      serviceName: "getWards",
      url: Urls.location.wards,
      params: { tenantId: tenantId },
      useCache: true,
    });
  }
};
