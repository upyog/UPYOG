/**
 * Local fallback for Estate.PaymentHistoryConfig (MDMS).
 * Filters + result fields drive the citizen payment-history UI.
 */
const EST_PAYMENT_HISTORY_CONFIG = {
  header: "EST_PAYMENT_HISTORY",
  emptyState: {
    message: "EST_NO_APPLICATION_FOUND_MSG",
  },
  businessService: "est-services",
  resultMode: "cards",
  autoSearch: true,
  filters: [
    {
      order: 1,
      key: "EST_ASSET_NUMBER",
      name: "assetNo",
      type: "text",
      placeholder: "EST_ENTER_ASSET_NUMBER",
    },
    {
      order: 2,
      key: "CS_COMMON_FROM_DATE",
      name: "fromDate",
      type: "date",
    },
    {
      order: 3,
      key: "CS_COMMON_TO_DATE",
      name: "toDate",
      type: "date",
    },
  ],
  // Card / table columns — order + labelKey + accessor (+ optional format).
  resultFields: [
    {
      order: 1,
      key: "CS_PAYMENT_AMOUNT_PAID_WITHOUT_SYMBOL",
      accessor: "amountPaid",
      format: "currency",
      emphasize: true,
    },
    {
      order: 2,
      key: "EST_ESTATE_NUMBER",
      accessor: "assetNo",
    },
    {
      order: 3,
      key: "EST_BUILDING_NAME",
      accessor: "buildingName",
    },
    {
      order: 4,
      key: "EST_BILLING_CYCLE",
      accessor: "billingCycle",
    },
    {
      order: 5,
      key: "PT_RECEIPT_DATE_LABEL",
      accessor: "receiptDateLabel",
    },
    {
      order: 6,
      key: "PT_RECEIPT_NO_LABEL",
      accessor: "receiptNumber",
    },
    {
      order: 7,
      key: "CS_COMMON_PAYMENT_MODE",
      accessor: "paymentMode",
    },
  ],
  actionButton: {
    search: "ES_COMMON_SEARCH",
    clear: "ES_COMMON_CLEAR_ALL",
  },
};

export default EST_PAYMENT_HISTORY_CONFIG;
