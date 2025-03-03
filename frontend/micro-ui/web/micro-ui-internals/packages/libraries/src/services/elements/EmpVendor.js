import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

export const VendorService = {
  vendorSearch: (tenantId, filters) =>
    Request({
      url: Urls.vendor.search,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
    }),
  createVendor: (details, tenantId) =>
    Request({
      url: Urls.vendor.create,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
    vendorcommonSearch: ({ tenantId, filters, auth }) =>
      Request({
        url:Urls.vendor.vendorcommonSearch,
        useCache: false,
        method: "POST",
        auth: auth === false ? auth : true,
        userService: auth === false ? auth : true,
        params: { tenantId, ...filters },
      }),
    createVendorAdditionaldetails: (details, tenantId) => 
      Request({
        url: Urls.vendor.additionaldetailsCreate,
        data: details,
        useCache: true,
        userService: true,
        method: "POST",
        params: { tenantId },
        auth: true,
      })
};
