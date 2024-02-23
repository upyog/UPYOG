import { getCommonHeader, getBreak, getLabel } from "egov-ui-framework/ui-config/screens/specs/utils";
import { showSearches } from "./generateBillResource/billTabs";
import { viewCreateBill } from "./generateBillResource/viewCreateBills";
import { createBill } from "./generateBillResource/createBill";
import { getTenantIdCommon } from "egov-ui-kit/utils/localStorageUtils";
import { prepareFinalObject,unMountScreen } from "egov-ui-framework/ui-redux/screen-configuration/actions";
import "./index.css";
import { getRequiredDocData, showHideAdhocPopup } from "egov-ui-framework/ui-utils/commons";
import { getTenantId } from "egov-ui-kit/utils/localStorageUtils";
import { httpRequest } from "../../../../ui-utils/api";
import commonConfig from "config/common.js";

const getMDMSData = (action, dispatch) => {
  const moduleDetails = [
    {
      moduleName: "ws-services-masters",
      masterDetails: [
        { name: "Documents" }
      ] 
    }
  ]
  try {
    getRequiredDocData(action, dispatch, moduleDetails)
  } catch (e) {
    console.log(e);
  }
};

const getMhollaData = async(dispatch)=>{
  const queryObject = [
    { key: "tenantId", value: localStorage.getItem('tenant-id') },
     
  ];
  let response = await httpRequest(
    "post",
    "/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=REVENUE&boundaryType=Locality",
    "_search",
    queryObject,
    {}
  );
  let mohallaDataArray = [];
  let mohallaDataRow=null;
  let name;
  response.TenantBoundary[0].boundary.map((element,index) => {
    name = element.name + "( "+element.code+" )";
   // code=element.code;
    mohallaDataRow={"code":name};
   mohallaDataArray.push(mohallaDataRow);
  
 });
 
 dispatch(prepareFinalObject("mohallaData", mohallaDataArray));
}

const getMDMSAppType =async (dispatch) => {
  // getMDMS data for ApplicationType
    let mdmsBody = {
      MdmsCriteria: {
        tenantId: commonConfig.tenantId,
        moduleDetails: [
         {
            moduleName: "ws-services-masters", masterDetails: [
              { name: "ApplicationType" }
            ]
          }
        ]
      }
    };
    try {
      let applicationType = [];
      let payload = null;
       payload = await httpRequest("post", "/egov-mdms-service/v1/_search", "_search", [], mdmsBody);       
        if(payload && payload.MdmsRes['ws-services-masters'] && payload.MdmsRes['ws-services-masters'].ApplicationType !== undefined){
          payload.MdmsRes['ws-services-masters'].ApplicationType.forEach(obj => applicationType.push({ code: obj.code.replace(/_/g,' '), name: obj.name, businessService:obj.businessService}));          
          applicationType.forEach(type=>getBusinessService(type.businessService,dispatch))
          dispatch(prepareFinalObject("applyScreenMdmsData.searchScreen.applicationType", applicationType));
        }
    } catch (e) { console.log(e); }
  }

const header = getCommonHeader({
  labelKey: "WS_GENERATE_BILL_SUB_HEADER"
});


export const getMdmsTenantsData = async (dispatch) => {
  let mdmsBody = {
      MdmsCriteria: {
          tenantId: commonConfig.tenantId,
          moduleDetails: [
              {
                  moduleName: "tenant",
                  masterDetails: [
                      {
                          name: "tenants"
                      },
                      { 
                        name: "citymodule" 
                      }
                  ]
              },
          ]
      }
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
      payload.MdmsRes.tenant.tenants = payload.MdmsRes.tenant.citymodule[1].tenants;
      dispatch(prepareFinalObject("applyScreenMdmsData.tenant", payload.MdmsRes.tenant));

  } catch (e) {
      console.log(e);
  }
};




const generateBill = {
  uiFramework: "material-ui",
  name: "generateBill",
  beforeInitScreen: (action, state, dispatch) => {
    getMDMSData(action, dispatch);
    getMDMSAppType(dispatch);
    getMdmsTenantsData(dispatch);
    getMhollaData(dispatch);
    dispatch(prepareFinalObject("searchConnection.tenantId", getTenantIdCommon()));
    dispatch(prepareFinalObject('generateBillScreen',{}))
    dispatch(prepareFinalObject('searchBillScreen',{}))
   // resetFieldsForGenerateBills(state, dispatch);
   // resetFieldsForSearchBills(state, dispatch);
    dispatch(prepareFinalObject("currentTab", "CREATE_BILL"));


    return action;
  },
  components: {
    div: {
      uiFramework: "custom-atoms",
      componentPath: "Form",
      props: {
        className: "common-div-css",
        id: "generateBill"
      },
      children: {
        headerDiv: {
          uiFramework: "custom-atoms",
          componentPath: "Container",

          children: {
            header: {
              gridDefination: {
                xs: 12,
                sm: 6
              },
              ...header
            },
            
          },
          children: {
            header1: {
              gridDefination: {
                xs: 12,
                sm: 6
              },
              ...header
            },
            
          }

        },
        



     
        showSearches,
        breakAfterSearch: getBreak(),
        createBill,
        viewCreateBill,
   //     searchBill,
      }
    },
    adhocDialog: {
      uiFramework: "custom-containers",
      componentPath: "DialogContainer",
      props: {
        open: false,
        maxWidth: false,
        screenKey: "search"
      },
      children: {
        popup: {}
      }
    }
  }
};

export default generateBill;
