// Import API URLs and Request utility
import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

/**
 * ChallanGenerationService
 * ------------------------------------------------------
 * This service handles all API calls related to:
 * - Challan creation & update
 * - Search & count
 * - Bill generation & search
 * - Receipt handling
 * - PDF generation & download
 * - File fetching
 */
export const ChallanGenerationService = {

  /**
   * Search Challans
   * Fetch challans based on tenantId and filters
   */
  search: ({ tenantId, filters }) =>
    Request({
      url: Urls.challangeneration.search_new,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: { tenantId, ...filters },
    }),

  /**
   * Create Challan
   * Sends challan details to backend for creation
   */
  create: (details, tenantId) =>
    Request({
      url: Urls.challangeneration.create_new,
      data: details,
      useCache: true,
      method: "POST",
      params: { tenantId },
      auth: true,
      userService: true,
    }),

  /**
   * Generate Bill
   * Creates a bill for a given challan (consumerCode)
   */
  generateBill: (consumerCode, tenantId, businessService, operation) =>
    Request({
      url: Urls.challangeneration.fetch_bill,
      data: {},
      useCache: true,
      method: "POST",
      params: { consumerCode, tenantId, businessService },
      auth: true,
      userService: true,
    }),

  /**
   * Search Bills
   * Uses different endpoints based on business service (PT or others)
   */
  search_bill: (tenantId, filters) =>
    Request({
      url:
        filters?.businesService !== "PT"
          ? Urls.challangeneration.search_bill
          : Urls.challangeneration.search_bill_pt,
      useCache: false,
      method: "POST",
      data: { searchCriteria: { tenantId, ...filters } },
      auth: true,
      userService: false,
      // params can also be used if required
    }),

  /**
   * Receipt Search
   * Fetch receipt details using tenantId and additional params
   */
  recieptSearch: (tenantId, businessService, params) => {

    // Debug log to inspect API params
    console.log("🔍 [challangenerationService.recieptSearch] Params:", {
      tenantId,
      businessService,
      ...params,
    });

    return Request({
      url: Urls.challangeneration.reciept_search,
      urlParams: { businessService },
      method: "POST",
      auth: true,
      params: { tenantId, ...params },
    });
  },

  /**
   * Generate PDF
   * Used for generating challan-related PDFs
   */
  generatePdf: (tenantId, data = {}, key) =>
    Request({
      url: Urls.challangeneration.generate_pdf,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      locale: true,
      params: { tenantId, key },
      data: data,
    }),

  /**
   * Fetch File
   * Retrieves file URLs using fileStoreIds
   */
  file_fetch: (tenantId, fileStoreIds) =>
    Request({
      url: Urls.challangeneration.file_fetch,
      useCache: false,
      method: "GET",
      auth: true,
      userService: true,
      params: { tenantId, fileStoreIds },
    }),

  /**
   * Update Challan
   * Updates existing challan details
   */
  update: (details, tenantId) =>
    Request({
      url: Urls.challangeneration.update_new,
      data: details,
      useCache: true,
      method: "POST",
      // tenantId can be added if required
      auth: true,
      userService: true,
    }),

  /**
   * Download Challan PDF
   * Downloads challan document using challan number
   */
  downloadPdf: (challanNo, tenantId) =>
    Request({
      url: Urls.challangeneration.download_pdf,
      data: {},
      useCache: true,
      method: "POST",
      params: { challanNo, tenantId },
      auth: true,
      locale: true,
      userService: true,
      userDownload: true, // triggers file download
    }),

  /**
   * Download Receipt PDF
   * Downloads receipt using business service and consumer code
   */
  receipt_download: (bussinessService, consumerCode, tenantId) =>
    Request({
      url: Urls.challangeneration.receipt_download,
      data: {},
      useCache: true,
      method: "POST",
      params: { bussinessService, consumerCode, tenantId },
      auth: true,
      locale: true,
      userService: true,
      userDownload: true,
    }),

  /**
   * Get Challan Count
   * Returns total number of challans for a tenant
   */
  count: (tenantId) =>
    Request({
      url: Urls.challangeneration.count,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      params: { tenantId },
    }),

  /**
   * Open Search (Public API)
   * Used for unauthenticated search scenarios
   */
  ChallanGenerationOpenSearch: ({ tenantId, filters }) =>
    Request({
      url: Urls.challangeneration.search,
      useCache: false,
      method: "POST",
      auth: false, // public API
      userService: false,
      params: { tenantId, ...filters },
    }),
};