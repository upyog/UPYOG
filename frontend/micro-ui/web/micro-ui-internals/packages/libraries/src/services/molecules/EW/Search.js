// Importing necessary utilities and services
import { EwService } from "../../elements/EW"; // E-Waste service for API calls

// Object containing methods for E-Waste search functionality
export const EWSearch = {
  // Method to fetch all E-Waste applications based on tenant ID and filters
  all: async (tenantId, filters = {}) => {
    console.log("filterrsrs", filters); // Debugging log for filters
    const response = await EwService.search({ tenantId, filters }); // Call the search API
    return response; // Return the response
  },

  // Method to fetch a single E-Waste application based on tenant ID and filters
  application: async (tenantId, filters = {}) => {
    const response = await EwService.search({ tenantId, filters }); // Call the search API
    return response.EwasteApplication[0]; // Return the first application from the response
  },

  // Method to transform registration details for an E-Waste application
  RegistrationDetails: ({ EwasteApplication: response, t }) => {
    // Map product details into rows for a table
    const productRows = response?.ewasteDetails?.map((product) => [
      product?.productName, // Product name
      product?.quantity, // Product quantity
      product?.price / product?.quantity, // Unit price
      product?.price, // Total price
    ]) || [];

    // Prepare transaction details
    const transactionDetails = [
      response?.calculatedAmount ? { title: "EWASTE_NET_PRICE", value: "₹ " + response?.calculatedAmount } : null,
      response?.transactionId ? { title: "ES_EW_ACTION_TRANSACTION_ID", value: response?.transactionId } : null,
      response?.finalAmount ? { title: "ES_EW_ACTION_FINALAMOUNT", value: "₹ " + response?.finalAmount } : null,
      response?.pickUpDate ? { title: "EW_PICKUP_DATE", value: response?.pickUpDate } : null,
    ].filter(detail => detail !== null && detail.value !== null); // Filter out null values

    // Return structured application details
    return [
      {
        title: "EW_APPLICANT_DETAILS", // Section title
        asSectionHeader: true, // Mark as a section header
        values: [
          { title: "EW_APPLICANT_NAME", value: response?.applicant?.applicantName }, // Applicant name
          { title: "EW_EMAIL", value: response?.applicant?.emailId }, // Email ID
          { title: "EW_REQUEST_ID", value: response?.requestId }, // Request ID
          { title: "EW_MOBILE_NUMBER", value: response?.applicant?.mobileNumber }, // Mobile number
          { title: "EW_ALT_NUMBER", value: response?.applicant?.altMobileNumber }, // Alternate mobile number
        ],
      },
      {
        title: "EW_ADDRESS_DETAILS", // Section title
        asSectionHeader: true, // Mark as a section header
        values: [
          { title: "EW_PINCODE", value: response?.address?.pincode }, // Pincode
          { title: "EW_CITY", value: response?.address?.city }, // City
          { title: "EW_DOOR_NO", value: response?.address?.doorNo }, // Door number
          { title: "EW_STREET", value: response?.address?.street }, // Street
          { title: "EW_ADDRESS_LINE_1", value: response?.address?.addressLine1 }, // Address line 1
          { title: "EW_ADDRESS_LINE_2", value: response?.address?.addressLine2 }, // Address line 2
          { title: "EW_BUILDING_NAME", value: response?.address?.buildingName }, // Building name
        ],
      },
      {
        title: "EW_PRODUCT_DETAILS", // Section title
        asSectionHeader: true, // Mark as a section header
        isTable: true, // Mark as a table
        headers: ["PRODUCT_NAME", "PRODUCT_QUANTITY", "UNIT_PRICE", "TOTAL_PRODUCT_PRICE"], // Table headers
        tableRows: productRows, // Table rows
      },
      {
        title: "EWASTE_TITLE_TRANSACTION_DETAILS", // Section title
        asSectionHeader: true, // Mark as a section header
        values: transactionDetails, // Transaction details
      },
      {
        title: "EWASTE_DOCUMENT_DETAILS", // Section title
        additionalDetails: {
          documents: [
            {
              values: response?.documents?.map((document) => {
                return {
                  title: `${document?.documentType?.replace(".", "_")}`, // Document type
                  documentType: document?.documentType, // Document type
                  documentUid: document?.fileStoreId, // Document UID
                  fileStoreId: document?.filestoreId, // File store ID
                  status: document.status, // Document status
                };
              }),
            },
          ],
        },
      },
    ];
  },

  // Method to fetch detailed application data
  applicationDetails: async (t, tenantId, requestId, userType, args) => {
    const filter = { requestId, ...args }; // Prepare filters
    const response = await EWSearch.application(tenantId, filter); // Fetch application data
    return {
      tenantId: response.tenantId, // Tenant ID
      applicationDetails: EWSearch.RegistrationDetails({ EwasteApplication: response, t }), // Transformed application details
      applicationData: response, // Raw application data
      transformToAppDetailsForEmployee: EWSearch.RegistrationDetails, // Transformation function
    };
  },
};
