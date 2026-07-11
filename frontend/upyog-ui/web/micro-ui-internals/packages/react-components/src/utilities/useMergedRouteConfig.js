import { useMemo } from "react";
import { mergeRouteConfig, resolveActiveRouteConfig } from "./checkPageUtils";

/**
 * Active wizard step from session (or MDMS) merged with local field overrides.
 */
const useMergedRouteConfig = (mdmsSteps, stepKey, sessionValue = {}, localOverrides = {}) =>
  useMemo(() => {
    const active = resolveActiveRouteConfig(sessionValue, mdmsSteps, stepKey);
    return mergeRouteConfig(active, localOverrides);
  }, [sessionValue, mdmsSteps, stepKey, localOverrides]);

export default useMergedRouteConfig;
