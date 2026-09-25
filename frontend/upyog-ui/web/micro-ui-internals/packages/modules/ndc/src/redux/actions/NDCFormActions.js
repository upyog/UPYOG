import { UPDATE_NDC_FORM, SET_NDC_STEP, RESET_NDC_FORM } from "./types";

export const updateNDCForm = (key, value) => ({
  type: UPDATE_NDC_FORM,
  payload: { key, value },
});

export const setNDCStep = (step) => ({
  type: SET_NDC_STEP,
  payload: step,
});

export const resetNDCForm = () => ({
  type: RESET_NDC_FORM,
});
