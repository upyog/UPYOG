import { useMemo } from "react";
import { resolveActiveRouteConfig } from "./checkPageUtils";

/**
 * Resolves routeConfig for check/submit pages.
 * Prefers the snapshot stored when the user completed the form step;
 * falls back to the MDMS wizard step definition.
 */
const useDynamicRouteConfig = (mdmsSteps, stepKey, sessionValue = {}) =>
  useMemo(
    () => resolveActiveRouteConfig(sessionValue, mdmsSteps, stepKey),
    [sessionValue, mdmsSteps, stepKey]
  );

export default useDynamicRouteConfig;
