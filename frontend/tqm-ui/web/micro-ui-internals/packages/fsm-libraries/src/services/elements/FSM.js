import Urls from "@egovernments/digit-ui-libraries/src/services/atoms/urls";
import { Request } from "@egovernments/digit-ui-libraries/src/services/atoms/Utils/Request";

export const FSMService = {
  search: (tenantId, filters = {}) =>
    Request({
      url: Urls.fsm.search,
      useCache: false,
      method: "POST",
      auth: true,
      userService: false,
      params: { tenantId, ...filters },
    }),
  create: (details, tenantId) =>
    Request({
      url: Urls.fsm.create,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  update: (details, tenantId) =>
    Request({
      url: Urls.fsm.update,
      data: details,
      useCache: false,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  vendorSearch: (tenantId, filters) =>
    Request({
      url: Urls.fsm.vendorSearch,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
    }),
  audit: (tenantId, filters) =>
    Request({
      url: Urls.fsm.audit,
      useCache: false,
      userService: true,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
    }),
  vehicleSearch: (tenantId, details) =>
    Request({
      url: Urls.fsm.vehicleTripSearch,
      useCache: false,
      userService: true,
      method: "POST",
      params: { tenantId, ...details },
      auth: true,
    }),
  vehiclesSearch: (tenantId, details) =>
    Request({
      url: Urls.fsm.vehicleSearch,
      useCache: false,
      userService: true,
      method: "POST",
      params: { tenantId, ...details },
      auth: true,
    }),
  billingSlabSearch: (tenantId, filters) =>
    Request({
      url: Urls.fsm.billingSlabSearch,
      useCache: false,
      userService: true,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
    }),
  vehicleUpdate: (details) => {
    return Request({
      url: Urls.fsm.vehilceUpdate,
      data: details,
      useCache: false,
      userService: true,
      method: "POST",
      auth: true,
    });
  },
  vehicleTripCreate: (details) => {
    return Request({
      url: Urls.fsm.vehicleTripCreate,
      data: details,
      useCache: false,
      userService: true,
      method: "POST",
      auth: true,
    });
  },
  createVendor: (details, tenantId) =>
    Request({
      url: Urls.fsm.createVendor,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  updateVendor: (details, tenantId) =>
    Request({
      url: Urls.fsm.updateVendor,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  createVehicle: (details, tenantId) =>
    Request({
      url: Urls.fsm.createVehicle,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  updateVehicle: (details, tenantId) =>
    Request({
      url: Urls.fsm.updateVehicle,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  driverSearch: (tenantId, filters) =>
    Request({
      url: Urls.fsm.driverSearch,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
    }),
  createDriver: (details, tenantId) =>
    Request({
      url: Urls.fsm.createDriver,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  updateDriver: (details, tenantId) =>
    Request({
      url: Urls.fsm.updateDriver,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  advanceBalanceCalculate: (tenantId, details) =>
    Request({
      url: Urls.fsm.advanceBalanceCalculate,
      useCache: false,
      userService: true,
      method: "POST",
      params: { tenantId, ...details },
      auth: true,
    }),
  workerSearch: ({ tenantId, details, params }) =>
    Request({
      url: Urls.fsm.workerSearch,
      data: details,
      useCache: false,
      userService: true,
      method: "POST",
      params: { tenantId, ...params },
      auth: true,
    }),
  createWorker: ({ details, tenantId }) =>
    Request({
      url: Urls.fsm.workerCreate,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  updateWorker: (details, tenantId) =>
    Request({
      url: Urls.fsm.workerUpdate,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  deleteWorker: (details, tenantId) =>
    Request({
      url: Urls.fsm.workerDelete,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  createPlantUser: (details, tenantId) =>
    Request({
      url: Urls.fsm.plantUserCreate,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  updatePlantUser: (details, tenantId) =>
    Request({
      url: Urls.fsm.plantUserUpdate,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  vehicleTripAlertsDetails: (tenantId, details) =>
    Request({
      url: Urls.fsm.vehicleTripAlertsDetails,
      useCache: false,
      userService: true,
      method: "POST",
      params: { tenantId, ...details },
      auth: true,
    }),
};
