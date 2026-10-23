import { combineReducers } from "redux";
import NDCFormReducer from "./NDCFormReducer";

const getRootReducer = () =>
  combineReducers({
    NDCForm: NDCFormReducer,
  });

export default getRootReducer;
