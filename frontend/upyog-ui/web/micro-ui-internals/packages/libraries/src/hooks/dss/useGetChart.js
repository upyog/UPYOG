import { queryTemplate } from "../../common/queryTemplate";
import { DSSService } from "../../services/elements/DSS";

/**
 * Build request payload for DSS chart API
 */
const getRequest = (
  type,
  key,
  requestDate,
  filters = {},
  moduleLevel,
  addlFilter = {},
  indexKeyForEmptyModule = []
) => {
  const aggregationRequestDto = {
    visualizationType: type,
    visualizationCode: key,
    queryType: "",
    filters: filters || {},
    moduleLevel: moduleLevel || "",
    aggregationFactors: addlFilter || {},
    requestDate: requestDate || {},
  };

  // Handle empty module for specific keys
  if (indexKeyForEmptyModule.includes(key)) {
    aggregationRequestDto.moduleLevel = "";
  }

  return { aggregationRequestDto };
};

/**
 * Fetch chart data for DSS dashboard.
 */
const useGetChart = (args) => {
  const {
    key,
    type,
    tenantId,
    requestDate,
    filters,
    moduleLevel,
    addlFilter,
  } = args;

  const indexKeyForEmptyModule = [
    "nssPtCitizenFeedbackScore",
    "nssPtCitizenServiceDeliveryIndex",
    "sdssPtCitizenFeedbackScore",
  ];

  const queryKey = [
    "DSS_CHART",
    key,
    tenantId,
    JSON.stringify(requestDate),
    JSON.stringify(filters),
    moduleLevel,
  ];

  const queryFn = () =>
    DSSService.getCharts({
      ...getRequest(
        type,
        key,
        requestDate,
        filters,
        moduleLevel,
        addlFilter,
        indexKeyForEmptyModule
      ),
      headers: { tenantId },
    });

  const select = (data) => {
    if (data?.responseData?.data) {
      data.responseData.data =
        data.responseData.data.filter(Boolean);

      data.responseData.data.forEach((row) => {
        if (row?.plots) {
          row.plots = row.plots.filter(Boolean);
        }
      });
    }
    return data;
  };

  return queryTemplate({
    queryKey,
    queryFn,
    select,
    config: {
      refetchOnMount: true,
      retry: false,
      refetchOnWindowFocus: false,
    },
  });
};

export default useGetChart;