import { createStore, compose, applyMiddleware } from "redux";
import thunk from "redux-thunk";
import getRootReducer from "./reducer";

/**
 * Redux store configuration:
 * - Creates store with root reducer
 * - Applies thunk middleware for async actions
 */

const middleware = [thunk];
const getStore = (defaultStore) => {
  return createStore(
    getRootReducer(defaultStore),
    compose(applyMiddleware(...middleware))
  );
};
export default getStore;