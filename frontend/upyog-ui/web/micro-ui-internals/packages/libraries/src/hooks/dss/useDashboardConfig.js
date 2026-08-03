import { queryTemplate } from "../../common/queryTemplate";
import { DSSService } from "../../services/elements/DSS";

/**
 * Fetch dashboard config for a module.
 */
const useDashoardConfig = (moduleCode) => {
  const queryKey = ["DSS_DASHBOARD_CONFIG", moduleCode];

  const queryFn = () => DSSService.getDashboardConfig(moduleCode);

  return queryTemplate({
    queryKey,
    queryFn,
  });
};

export default useDashoardConfig;