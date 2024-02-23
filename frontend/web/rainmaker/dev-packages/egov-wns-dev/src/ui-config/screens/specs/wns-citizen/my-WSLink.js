import { fetchData } from "./myApplicationDetails/myApplicationDetails";
import { getCommonHeader } from "egov-ui-framework/ui-config/screens/specs/utils";

const header = getCommonHeader(
  {
    labelKey: "How to apply & pay WS"
    
  },
  {
    classes: {
      root: "common-header-cont"
    }
  }
  
  
);

const screenConfig = {
  uiFramework: "material-ui",
  name: "my-WSLink",
  beforeInitScreen: (action, state, dispatch) => {
    fetchData(action, state, dispatch);
    return action;
  },
  components: {
    div: {
      uiFramework: "custom-atoms",
      componentPath: "Div",
      props: {
        // className: "common-div-css"
      },
      children: {
        header: header,
        applicationsCard: {
          uiFramework: "custom-molecules-local",
          moduleName: "egov-wns",
          componentPath: "WnsWSmyLink",
        }
      }
    }
  }
};

export default screenConfig;
