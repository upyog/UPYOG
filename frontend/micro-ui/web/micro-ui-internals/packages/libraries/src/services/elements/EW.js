// Importing the URLs configuration for API endpoints
import Urls from "../atoms/urls";
// Importing the Request utility for making API calls
import { Request } from "../atoms/Utils/Request";

// Service object for handling E-Waste-related API calls
export const EwService = {
  
  // Method to create a new E-Waste application
  create: (details) =>
    Request({
      url: Urls.ew.create, // API endpoint for creating an application
      data: details, // Payload for the API request
      useCache: false, // Disable caching for this request
      setTimeParam: false, // Do not append time parameters to the URL
      userService: true, // Use user service for authentication
      method: "POST", // HTTP method
      params: {}, // Additional query parameters (none in this case)
      auth: true, // Enable authentication for this request
    }),

  // Method to search for E-Waste applications
  search: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.ew.search, // API endpoint for searching applications
      useCache: false, // Disable caching for this request
      method: "POST", // HTTP method
      auth: auth === false ? auth : true, // Enable or disable authentication based on the `auth` parameter
      userService: auth === false ? auth : true, // Use user service based on the `auth` parameter
      params: { tenantId, ...filters }, // Query parameters including tenant ID and filters
    }),

  // Method to update an existing E-Waste application
  update: (details) =>
    Request({
      url: Urls.ew.update, // API endpoint for updating an application
      data: details, // Payload for the API request
      useCache: false, // Disable caching for this request
      setTimeParam: false, // Do not append time parameters to the URL
      userService: true, // Use user service for authentication
      method: "POST", // HTTP method
      params: {}, // Additional query parameters (none in this case)
      auth: true, // Enable authentication for this request
    }),
};



