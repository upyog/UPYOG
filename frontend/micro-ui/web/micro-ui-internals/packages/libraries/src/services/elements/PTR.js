// Importing necessary modules and utilities
import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";


 // Exporting the PTRService object which contains various methods for interacting with the PTR API
export const PTRService = {
  
  // Method to create a new PTR entry
  create: (details, tenantId) =>
    Request({
      url: Urls.ptr.create,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),

  // Method to search for PTR entries based on filters
    search: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.ptr.search,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),

// Method to update an existing PTR entry
    update: (details, tenantId) =>
    Request({
      url: Urls.ptr.update,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
  }),
  // Method to search for payment-related information
  paymentsearch: ({ tenantId, filters, auth }) =>
  Request({
    url: Urls.ptr.payment_search,
    useCache: false,
    method: "POST",
    auth: auth === false ? auth : true,
    userService: auth === false ? auth : true,
    params: { tenantId, ...filters },
  }),

 
 
  // Method to fetch payment details for specific consumer codes
  fetchPaymentDetails: ({ tenantId, consumerCodes ,auth=true}) =>
    Request({
      url: Urls.ptr.fetch_payment_details,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, consumerCode: consumerCodes, businessService: "pet-services" },
    }),
    
};




