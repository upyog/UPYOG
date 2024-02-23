import { fetchData } from "./myNOCLink/myNOCLink";
import { getCommonHeader } from "egov-ui-framework/ui-config/screens/specs/utils";

const header = getCommonHeader(
  {
    labelKey: "How to apply & pay Fire NOC"
    
  },
  {
    classes: {
      root: "common-header-cont"
    }
  }
  
  
);

const screenConfig = {
  uiFramework: "material-ui",
  name: "my-NOC",
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
          moduleName: "egov-firenoc",
          componentPath: "FNFirenocApply",
        }
      }
    }
  }
};

export default screenConfig;
