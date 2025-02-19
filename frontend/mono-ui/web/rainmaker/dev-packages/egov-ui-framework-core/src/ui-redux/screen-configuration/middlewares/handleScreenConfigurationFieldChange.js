import * as screenActionTypes from "../actionTypes";
import get from "lodash/get";
import {validate} from "../utils";

const handleScreenConfigurationFieldChange = (store) => (next) => (action) => {
  const { type} = action;
  if (type === screenActionTypes.HANDLE_SCREEN_CONFIGURATION_FIELD_CHANGE && action.property==="props.value") {
      const { screenKey,componentJsonpath,value } = action;
      const dispatch = store.dispatch;
      const state = store.getState();
      const componentObject=get(state,`screenConfiguration.screenConfig.${screenKey}.${componentJsonpath}`);
      validate(screenKey,{...componentObject,value},dispatch);
      next(action);
  } else {
    next(action);
  }
};

export default handleScreenConfigurationFieldChange;
