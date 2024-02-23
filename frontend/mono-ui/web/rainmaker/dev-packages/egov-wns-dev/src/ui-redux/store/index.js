import rootReducer from "./reducer";
import { createStore, applyMiddleware,combineReducers,compose } from "redux";
import thunk from "redux-thunk";
import screenConfigurationMiddleware from "egov-ui-framework/ui-redux/screen-configuration/middlewares";
import authMiddleware from "egov-ui-framework/ui-redux/auth/middlewares";

let middlewares = [];

middlewares = middlewares.concat(authMiddleware);
middlewares = middlewares.concat(screenConfigurationMiddleware);
middlewares = middlewares.concat(thunk);

if (process.env.NODE_ENV === "development") {
  const { logger } = require("redux-logger");
  middlewares = middlewares.concat(logger);
}

const store = createStore(combineReducers({
  ...rootReducer
}),compose(
   applyMiddleware(...middlewares),
   window.devToolsExtension ? window.devToolsExtension() : f => f
 ))
 export const prepareFinalObject = (jsonPath, value) => {
  return {
    type: screenActionTypes.PREPARE_FINAL_OBJECT,
    jsonPath,
    value
  };
};
export default store
