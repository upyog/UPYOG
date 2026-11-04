// reducers/employeeFormReducer.js
import { UPDATE_Challan_Application_FORMType, SET_Challan_Application_STEPType, RESET_Challan__APPLICATION_FORMType } from "../action/types";

/**
 * Reducer for managing Challan application state:
 * - Handles form data updates
 * - Controls step navigation
 * - Resets application state
 */

const initialState = {
  step: 1,
  isValid: false,
  formData: {},
};

const ChallanApplicationFormReducer = (state = initialState, action) => {
  switch (action.type) {
    case UPDATE_Challan_Application_FORMType:
      return {
        ...state,
        formData: {
          ...state.formData,
          [action.payload.key]: action.payload.value,
        },
      };
    case SET_Challan_Application_STEPType:
      return {
        ...state,
        step: action.payload,
      };
    case RESET_Challan__APPLICATION_FORMType:
      return initialState;
    default:
      return state;
  }
};

export default ChallanApplicationFormReducer;
