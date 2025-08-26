import { EwService } from "../../elements/EW";

/**
 * Service module for E-Waste application search operations.
 * Provides methods for searching, transforming, and structuring application data.
 */
export const EWSearch = {
  /**
   * Fetches all E-Waste applications matching the given criteria
   * 
   * @param {string} tenantId Tenant/city identifier
   * @param {Object} filters Search criteria and filters
   * @returns {Promise<Object>} Search results with application data
   */
  all: async (tenantId, filters = {}) => {
    const response = await EwService.search({ tenantId, filters });
    return response;
  },

  /**
   * Fetches a single E-Waste application by its identifiers
   * 
   * @param {string} tenantId Tenant/city identifier
   * @param {Object} filters Search criteria including requestId
   * @returns {Promise<Object>} Single application details
   */
  application: async (tenantId, filters = {}) => {
    const response = await EwService.search({ tenantId, filters });
    return response.EwasteApplication[0];
  },

  /**
   * Transforms application data into structured format for display
   * Organizes details into sections: applicant, address, products, and transactions
   * 
   * @param {Object} params Transform parameters
   * @param {Object} params.EwasteApplication Application data to transform
   * @param {Function} params.t Translation function
   * @returns {Array<Object>} Structured sections with application details
   */
  RegistrationDetails: ({ EwasteApplication: response, t }) => {
    const productRows = response?.ewasteDetails?.map((product) => [
      product?.productName,
      product?.quantity,
      product?.price / product?.quantity,
      product?.price,
    ]) || [];

    const transactionDetails = [
      response?.calculatedAmount ? { title: "EWASTE_NET_PRICE", value: "₹ " + response?.calculatedAmount } : null,
      response?.transactionId ? { title: "ES_EW_ACTION_TRANSACTION_ID", value: response?.transactionId } : null,
      response?.finalAmount ? { title: "ES_EW_ACTION_FINALAMOUNT", value: "₹ " + response?.finalAmount } : null,
      response?.pickUpDate ? { title: "EW_PICKUP_DATE", value: response?.pickUpDate } : null,
    ].filter(detail => detail !== null && detail.value !== null);

    return [
      {
        title: "EW_APPLICANT_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "EW_APPLICANT_NAME", value: response?.applicant?.applicantName },
          { title: "EW_EMAIL", value: response?.applicant?.emailId },
          { title: "EW_REQUEST_ID", value: response?.requestId },
          { title: "EW_MOBILE_NUMBER", value: response?.applicant?.mobileNumber },
          { title: "EW_ALT_NUMBER", value: response?.applicant?.altMobileNumber },
        ],
      },
      {
        title: "EW_ADDRESS_DETAILS",
        asSectionHeader: true,
        values: [
          { title: "EW_PINCODE", value: response?.address?.pincode },
          { title: "EW_CITY", value: response?.address?.city },
          { title: "EW_DOOR_NO", value: response?.address?.doorNo },
          { title: "EW_STREET", value: response?.address?.street },
          { title: "EW_ADDRESS_LINE_1", value: response?.address?.addressLine1 },
          { title: "EW_ADDRESS_LINE_2", value: response?.address?.addressLine2 },
          { title: "EW_BUILDING_NAME", value: response?.address?.buildingName },
        ],
      },
      {
        title: "EW_PRODUCT_DETAILS",
        asSectionHeader: true,
        isTable: true,
        headers: ["PRODUCT_NAME", "PRODUCT_QUANTITY", "UNIT_PRICE", "TOTAL_PRODUCT_PRICE"],
        tableRows: productRows,
      },
      {
        title: "EWASTE_TITLE_TRANSACTION_DETAILS",
        asSectionHeader: true,
        values: transactionDetails,
      },
      {
        title: "EWASTE_DOCUMENT_DETAILS",
        additionalDetails: {
          documents: [
            {
              values: response?.documents?.map((document) => ({
                title: `${document?.documentType?.replace(".", "_")}`,
                documentType: document?.documentType,
                documentUid: document?.fileStoreId,
                fileStoreId: document?.filestoreId,
                status: document.status,
              })),
            },
          ],
        },
      },
    ];
  },

  /**
   * Fetches and transforms complete application details for display
   * 
   * @param {Function} t Translation function
   * @param {string} tenantId Tenant/city identifier
   * @param {string} requestId Application request ID
   * @param {string} userType Type of user accessing details
   * @param {Object} args Additional search parameters
   * @returns {Promise<Object>} Complete application details with transformed data
   */
  applicationDetails: async (t, tenantId, requestId, userType, args) => {
    const filter = { requestId, ...args };
    const response = await EWSearch.application(tenantId, filter);
    return {
      tenantId: response.tenantId,
      applicationDetails: EWSearch.RegistrationDetails({ EwasteApplication: response, t }),
      applicationData: response,
      transformToAppDetailsForEmployee: EWSearch.RegistrationDetails,
    };
  },
};