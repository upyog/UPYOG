import { combineReducers } from "redux";
import ChallanApplicationFormReducer from "./ChallanApplicationFormReducer";

/**
 * Root reducer for the application:
 * Combines all feature reducers into a single state tree.
 */

const getRootReducer = () =>
  combineReducers({
    ChallanApplicationFormReducer,
  });

export default getRootReducer;
