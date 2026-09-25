/**
 * useMergedRouteConfig.js
 *
 * Resolves the active wizard step config from session (or MDMS), then merges
 * module-local overrides (computedFields, crossFieldValidations, form field
 * overlays, etc.) via mergeRouteConfig.
 *
 * Typical use: DynamicFormStep / check pages that need MDMS structure plus
 * local compute / validation behavior.
 *
 * @param {object|array} mdmsSteps       MDMS wizard steps (array or { body }).
 * @param {string}       stepKey         Step key to resolve.
 * @param {object}       [sessionValue]  Wizard session form data.
 * @param {object}       [localOverrides] Module-local route/form overrides.
 * @returns {object} Merged routeConfig.
 *
 * @see resolveActiveRouteConfig
 * @see mergeRouteConfig
 * @see useDynamicRouteConfig
 */

import { useMemo } from "react";
import { mergeRouteConfig, resolveActiveRouteConfig } from "./checkPageUtils";

/**
 * Active wizard step from session/MDMS, merged with local field/behavior overrides.
 *
 * @param {object|array} mdmsSteps
 * @param {string}       stepKey
 * @param {object}       [sessionValue]
 * @param {object}       [localOverrides]
 * @returns {object}
 */
const useMergedRouteConfig = (mdmsSteps, stepKey, sessionValue = {}, localOverrides = {}) =>
  useMemo(() => {
    const active = resolveActiveRouteConfig(sessionValue, mdmsSteps, stepKey);
    return mergeRouteConfig(active, localOverrides);
  }, [sessionValue, mdmsSteps, stepKey, localOverrides]);

export default useMergedRouteConfig;
