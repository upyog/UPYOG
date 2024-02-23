import {
  getBreak,
  getCommonHeader,
  getLabel,
} from "egov-ui-framework/ui-config/screens/specs/utils";
import {
  prepareFinalObject,
  unMountScreen,
} from "egov-ui-framework/ui-redux/screen-configuration/actions";
import {
  getQueryArg,
  getRequiredDocData,
  showHideAdhocPopup,
} from "egov-ui-framework/ui-utils/commons";
import { getTenantId } from "egov-ui-kit/utils/localStorageUtils";
import get from "lodash/get";
import { httpRequest } from "../../../../ui-utils";
import "./index.css";
import { pendingApprovals } from "./searchResource/pendingApprovals";
// import { progressStatus } from "./searchResource/progressStatus";
import { searchResults } from "./searchResource/searchResults";
import { fsApplication } from "./searchResource/FsApplication";

const hasButton = getQueryArg(window.location.href, "hasButton");
let enableButton = true;
enableButton = hasButton && hasButton === "false" ? false : true;
const tenant = getTenantId();
const getMdmsData = async (dispatch) => {
  let mdmsBody = {
    MdmsCriteria: {
      tenantId: getTenantId(),
      moduleDetails: [
        {
          moduleName: "TradeLicense",
          masterDetails: [{ name: "ApplicationType" }],
        },
      ],
    },
  };
  try {
    let payload = null;
    payload = await httpRequest(
      "post",
      "/egov-mdms-service/v1/_search",
      "_search",
      [],
      mdmsBody
    );
    let types = [];
    if (payload && payload.MdmsRes) {
      types = get(payload.MdmsRes, "TradeLicense.ApplicationType").map(
        (item, index) => {
          return {
            code: item.code.split(".")[1],
          };
        }
      );
    }
    dispatch(
      prepareFinalObject(
        "applyScreenMdmsData.searchScreen.applicationType",
        types
      )
    );
  } catch (e) {
    console.log(e);
  }
};

const header = getCommonHeader({
  labelName: "File Store",
  labelKey: "File Store",
});
const tradeLicenseSearchAndResultss = {
  uiFramework: "material-ui",
  name: "searchFs",
  
  components: {
    div: {
      uiFramework: "custom-atoms",
      componentPath: "Form",
      props: {
        className: "common-div-css",
        id: "searchFs",
      },
      children: {
        headerDiv: {
          uiFramework: "custom-atoms",
          componentPath: "Container",

          children: {
            header: {
              gridDefination: {
                xs: 12,
                sm: 6,
              },
              ...header,
            },
            
          },
        },
        pendingApprovals,
        fsApplication,
        breakAfterSearch: getBreak(),
        searchResults,
      },
    },
    adhocDialog: {
      uiFramework: "custom-containers",
      componentPath: "DialogContainer",
      props: {
        open:
          getQueryArg(window.location.href, "action") ===
          "showRequiredDocuments"
            ? true
            : false,
        maxWidth: false,
        screenKey: "searchFs",
        reRouteURL: "/tradelicence/searchFs",
      },
      children: {
        popup: {},
      },
    },
  },
};

export default tradeLicenseSearchAndResultss;
