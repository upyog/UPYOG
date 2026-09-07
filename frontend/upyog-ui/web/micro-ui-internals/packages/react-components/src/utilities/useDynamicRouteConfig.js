/**
 * useDynamicRouteConfig.js
 *
 * Resolves routeConfig for check / submit pages.
 * Prefers the snapshot stored in wizard session when the user completed the
 * form step (via attachRouteConfigToStepData + mergeSessionStepWithRouteConfig);
 * falls back to the MDMS wizard step definition when no snapshot exists yet.
 *
 * @param {object|array} mdmsSteps     MDMS wizard steps (array or { body }).
 * @param {string}       stepKey       Step key to resolve (e.g. "Allotments").
 * @param {object}       [sessionValue] Wizard session form data.
 * @returns {object} Active routeConfig for the step.
 *
 * @see resolveActiveRouteConfig
 * @see useMergedRouteConfig
 */

import { useMemo } from "react";
import { resolveActiveRouteConfig } from "./checkPageUtils";

/**
 * Memoized active routeConfig for a check/submit page.
 *
 * @param {object|array} mdmsSteps
 * @param {string}       stepKey
 * @param {object}       [sessionValue]
 * @returns {object}
 */
const useDynamicRouteConfig = (mdmsSteps, stepKey, sessionValue = {}) =>
  useMemo(
    () => resolveActiveRouteConfig(sessionValue, mdmsSteps, stepKey),
    [sessionValue, mdmsSteps, stepKey]
  );

export default useDynamicRouteConfig;
