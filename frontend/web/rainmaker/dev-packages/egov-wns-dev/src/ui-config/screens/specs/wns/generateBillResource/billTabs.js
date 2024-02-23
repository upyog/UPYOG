import { getCommonContainer } from "egov-ui-framework/ui-config/screens/specs/utils";
import { createBill } from './createBill';
import { searchBill } from './searchBill';

export const showSearches = getCommonContainer({
  showSearchScreens: {
    uiFramework: "custom-containers-local",
    moduleName: "egov-wns",
    componentPath: "CustomTabCointainerGenerateBill",
    props: {
      tabs: [
        {
          tabButton: { labelName: "GENERATE BILLS", label: "GENERATE BILLS" },
          tabContent: { createBill }
         },
         {
          tabButton: { labelName: "SEARCH BILLS", label: "SEARCH BILLS" },
          tabContent: { searchBill }
         }
        
      ],
      tabIndex : 0
    },
    type: "array"
  }
});
