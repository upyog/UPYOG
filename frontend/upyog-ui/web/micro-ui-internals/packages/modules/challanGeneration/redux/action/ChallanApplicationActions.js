import { UPDATE_Challan_Application_FORMType, SET_Challan_Application_STEPType, RESET_Challan__APPLICATION_FORMType } from "./types";

/**
 * Redux actions for managing Challan application form:
 * - Update form fields
 * - Manage step flow
 * - Reset form state
 */

export const UPDATE_ChallanApplication_FORM = (key, value) => ({
  type: UPDATE_Challan_Application_FORMType,
  payload: { key, value },
});

export const SET_ChallanApplication_STEP = (step) => ({
  type: SET_Challan_Application_STEPType,
  payload: step,
});

export const RESET_ChallanAPPLICATION_FORM = () => ({
  type: RESET_Challan__APPLICATION_FORMType,
});
