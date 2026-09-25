import { ReportsService } from "../../services/elements/Reports";
import { useQuery } from "@tanstack/react-query";

const useReportMeta = {
  fetchMetaData: (moduleName, reportName, tenantId) =>
    useQuery({
      queryKey: ["reportMeta", moduleName, reportName],
      queryFn: () =>
        ReportsService.fetchMeta({
          moduleName,
          reportName,
          tenantId,
        }),
    }),

  fetchReportData: (
    moduleName,
    reportName,
    tenantId,
    searchParams,
    config = {}
  ) =>
    useQuery({
      queryKey: ["reportMetaData", moduleName, searchParams],
      queryFn: () =>
        ReportsService.fetchReportsData({
          moduleName,
          reportName,
          tenantId,
          searchParams,
        }),
      ...config,
    }),
};

export default useReportMeta;