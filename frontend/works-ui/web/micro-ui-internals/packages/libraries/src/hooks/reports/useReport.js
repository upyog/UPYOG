import { ReportsService } from "../../services/elements/Reports";
import { queryTemplate } from "../../common/queryTemplate";

const useReportMeta = {
  fetchMetaData: (moduleName, reportName, tenantId) =>
    queryTemplate({ queryKey: ["reportMeta", moduleName, reportName], queryFn: () => ReportsService.fetchMeta({ moduleName, reportName, tenantId }) }),
  fetchReportData: (moduleName, reportName, tenantId, searchParams, config = {}) =>
    queryTemplate({ queryKey: ["reportMetaData", moduleName, searchParams], queryFn: () => ReportsService.fetchReportsData({ moduleName, reportName, tenantId, searchParams }), config }),
};

export default useReportMeta;
