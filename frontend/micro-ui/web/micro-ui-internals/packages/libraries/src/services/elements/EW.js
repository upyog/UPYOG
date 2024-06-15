import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";



export const EwService = {

  create: (details, tenantId) =>
    Request({
      url: Urls.ew.create,
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
      url: Urls.ew.search,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),

  update: (details, tenantId) =>
    Request({
      url: Urls.ew.update,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),



  //   paymentsearch: ({ tenantId, filters, auth }) =>
  //   Request({
  //     url: Urls.ew.payment_search,
  //     useCache: false,
  //     method: "POST",
  //     auth: auth === false ? auth : true,
  //     userService: auth === false ? auth : true,
  //     params: { tenantId, ...filters },
  //   }),

  //   fetchPaymentDetails: ({ tenantId, consumerCodes ,auth=true}) =>
  //     Request({
  //       url: Urls.ew.fetch_payment_details,
  //       useCache: false,
  //       method: "POST",
  //       auth: auth === false ? auth : true,
  //       userService: auth === false ? auth : true,
  //       params: { tenantId, consumerCode: consumerCodes, businessService: "pet-services" },
  //     }),

};



