import { useMemo } from "react";
import { mergeRouteConfig, resolveRouteConfigFromSteps } from "./checkPageUtils";

/**
 * Resolves the active wizard step config from MDMS steps + local form overrides.
 * Use in module check pages that pair DynamicForm with DynamicCheckPage.
 */
const useDynamicRouteConfig = (config, stepKey, localFormConfig = {}) => {
  const rawRouteConfig = useMemo(
    () => resolveRouteConfigFromSteps(config, stepKey),
    [config, stepKey]
  );

  const routeConfig = useMemo(
    () => mergeRouteConfig(rawRouteConfig, localFormConfig),
    [rawRouteConfig, localFormConfig]
  );

  return routeConfig;
};

export default useDynamicRouteConfig;
