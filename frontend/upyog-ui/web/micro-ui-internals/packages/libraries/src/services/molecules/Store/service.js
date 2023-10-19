import { LocalizationService } from "../../elements/Localization/service";
import { MdmsService } from "../../elements/MDMS";
import { Storage } from "../../atoms/Utils/Storage";
import { ApiCacheService } from "../../atoms/ApiCacheService";

const getImgUrl = (url, fallbackUrl) => {
  if (!url && fallbackUrl) {
    return fallbackUrl;
  }
  if (url.includes("s3.ap-south-1.amazonaws.com")) {
    const baseDomain = window?.location?.origin;
    return url.replace("https://s3.ap-south-1.amazonaws.com", baseDomain);
  }
  return url;
};
const addLogo = (id, url, fallbackUrl = "") => {
  const containerDivId = "logo-img-container";
  let containerDiv = document.getElementById(containerDivId);
  if (!containerDiv) {
    containerDiv = document.createElement("div");
    containerDiv.id = containerDivId;
    containerDiv.style = "position: absolute; top: 0; left: -9999px;";
    document.body.appendChild(containerDiv);
  }
  const img = document.createElement("img");
  img.src = getImgUrl(url, fallbackUrl);
  img.id = `logo-${id}`;
  containerDiv.appendChild(img);
};

const renderTenantLogos = (stateInfo, tenants) => {
  addLogo(stateInfo.code, stateInfo.logoUrl);
  tenants.forEach((tenant) => {
    addLogo(tenant.code, tenant.logoId, stateInfo.logoUrl);
  });
};

export const StoreService = {
  getInitData: () => {
    return Storage.get("initData");
  },

  getBoundries: async (tenants) => {
    let allBoundries = [];
    allBoundries = tenants.map((tenant) => {
      return Digit.LocationService.getLocalities(tenant.code);
    });
    return await Promise.all(allBoundries);
  },
  getRevenueBoundries: async (tenants) => {
    let allBoundries = [];
    allBoundries = tenants.map((tenant) => {
      return Digit.LocationService.getRevenueLocalities(tenant.code);
    });
    return await Promise.all(allBoundries);
  },
  digitInitData: async (stateCode, enabledModules) => {
    //const { MdmsRes } = await MdmsService.init(stateCode);
    var MdmsRes  =  
    {
      "ACCESSCONTROL-ACTIONS-TEST": {
          "actions-test": [
              {
                  "id": 2422,
                  "name": "TL_FAQ_S",
                  "url": "digit-ui-card",
                  "displayName": "FAQs",
                  "orderNumber": 7,
                  "parentModule": "TL",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/tl-faq",
                  "leftIcon": "TLIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/tl-home"
              },
              {
                  "id": 2423,
                  "name": "PT_HOW_IT_WORKS",
                  "url": "digit-ui-card",
                  "displayName": "How It Works",
                  "orderNumber": 9,
                  "parentModule": "PT",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pt-how-it-works",
                  "leftIcon": "propertyIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pt-home"
              },
              {
                  "id": 2424,
                  "name": "PT_FAQ_S",
                  "url": "digit-ui-card",
                  "displayName": "FAQs",
                  "orderNumber": 8,
                  "parentModule": "PT",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pt-faq",
                  "leftIcon": "propertyIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pt-home"
              },
              {
                  "id": 2425,
                  "name": "Firenoc Citizen Home",
                  "url": "digit-ui-card",
                  "displayName": "FireNoc Search",
                  "orderNumber": 1,
                  "parentModule": "FireNoc",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/citizen/fire-noc/home",
                  "leftIcon": "FirenocIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/citizen/fire-noc/home"
              },
              {
                  "id": 2426,
                  "name": "Birth Certificate Home",
                  "url": "digit-ui-card",
                  "displayName": "Birth Search",
                  "orderNumber": 1,
                  "parentModule": "Birth",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/citizen/birth-citizen/home",
                  "leftIcon": "BirthIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/citizen/birth-citizen/home"
              },
              {
                  "id": 2427,
                  "name": "Death Certificate Home",
                  "url": "digit-ui-card",
                  "displayName": "Death Search",
                  "orderNumber": 1,
                  "parentModule": "Death",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/citizen/death-citizen/home",
                  "leftIcon": "DeathIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/citizen/death-citizen/home"
              },
              {
                  "id": 2437,
                  "name": "PT_MY_PAYMENTS",
                  "url": "digit-ui-card",
                  "displayName": "My Payments",
                  "orderNumber": 7,
                  "parentModule": "PT",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pt/property/my-payments",
                  "leftIcon": "propertyIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pt-home"
              },
              {
                  "id": 2440,
                  "name": "ACTION_TEST_WNS_MY_BILLS",
                  "url": "digit-ui-card",
                  "displayName": "My Bills",
                  "orderNumber": 2,
                  "parentModule": "WS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/ws/my-bills",
                  "leftIcon": "WSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/ws-home"
              },
              {
                  "id": 2441,
                  "name": "ACTION_TEST_MY_PAYMENTS",
                  "url": "digit-ui-card",
                  "displayName": "My Payments",
                  "orderNumber": 6,
                  "parentModule": "WS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/ws/my-payments",
                  "leftIcon": "WSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/ws-home"
              },
              {
                  "id": 2442,
                  "name": "ACTION_TEST_APPLY_NEW_CONNECTION",
                  "url": "digit-ui-card",
                  "displayName": "Apply for New Connection",
                  "orderNumber": 4,
                  "parentModule": "WS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/ws/create-application/docs-required",
                  "leftIcon": "WSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/ws-home"
              },
              {
                  "id": 2443,
                  "name": "ACTION_TEXT_WS_SEARCH_AND_PAY",
                  "url": "digit-ui-card",
                  "displayName": "Search & Pay",
                  "orderNumber": 1,
                  "parentModule": "WS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/ws/search",
                  "leftIcon": "WSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/ws-home"
              },
              {
                  "id": 2444,
                  "name": "ACTION_TEXT_WS_MY_APPLICATION",
                  "url": "digit-ui-card",
                  "displayName": "My Applications",
                  "orderNumber": 5,
                  "parentModule": "WS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/ws/my-applications",
                  "leftIcon": "WSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/ws-home"
              },
              {
                  "id": 2445,
                  "name": "ACTION_TEXT_WS_MY_CONNECTION",
                  "url": "digit-ui-card",
                  "displayName": "My Connections",
                  "orderNumber": 3,
                  "parentModule": "WS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/ws/my-connections",
                  "leftIcon": "WSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/ws-home"
              },
              {
                  "id": 2446,
                  "name": "UC_SEARCH_AND_PAY",
                  "url": "digit-ui-card",
                  "displayName": "Search & Pay",
                  "orderNumber": 1,
                  "parentModule": "MCollect",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/mcollect/search",
                  "leftIcon": "MCollectIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/mcollect-home"
              },
              {
                  "id": 2447,
                  "name": "UC_MY_CHALLANS",
                  "url": "digit-ui-card",
                  "displayName": "My Challans",
                  "orderNumber": 2,
                  "parentModule": "MCollect",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/mcollect/My-Challans",
                  "leftIcon": "MCollectIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/mcollect-home"
              },
              {
                  "id": 2448,
                  "name": "CS_HOME_APPLY_FOR_DESLUDGING",
                  "url": "digit-ui-card",
                  "displayName": "Apply for Emptying of Septic Tank/Pit",
                  "orderNumber": 1,
                  "parentModule": "FSM",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/fsm/new-application",
                  "leftIcon": "FSMIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/fsm-home"
              },
              {
                  "id": 2449,
                  "name": "CS_HOME_MY_APPLICATIONS",
                  "url": "digit-ui-card",
                  "displayName": "My Applications",
                  "orderNumber": 2,
                  "parentModule": "FSM",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/fsm/my-applications",
                  "leftIcon": "FSMIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/fsm-home"
              },
              {
                  "id": 2450,
                  "name": "CS_COMMON_FILE_A_COMPLAINT",
                  "url": "digit-ui-card",
                  "displayName": "File a Complaint",
                  "orderNumber": 1,
                  "parentModule": "PGR",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pgr/create-complaint/complaint-type",
                  "leftIcon": "PGRIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pgr-home"
              },
              {
                  "id": 2451,
                  "name": "CS_HOME_MY_COMPLAINTS",
                  "url": "digit-ui-card",
                  "displayName": "My Complaints",
                  "orderNumber": 2,
                  "parentModule": "PGR",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pgr/complaints",
                  "leftIcon": "PGRIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pgr-home"
              },
              {
                  "id": 2452,
                  "name": "TL_CREATE_TRADE",
                  "url": "digit-ui-card",
                  "displayName": "Apply for Trade License",
                  "orderNumber": 1,
                  "parentModule": "TL",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/tl/tradelicence/new-application",
                  "leftIcon": "TLIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/tl-home"
              },
              {
                  "id": 2453,
                  "name": "TL_RENEWAL_HEADER",
                  "url": "digit-ui-card",
                  "displayName": "Renew Trade License",
                  "orderNumber": 2,
                  "parentModule": "TL",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/tl/tradelicence/renewal-list",
                  "leftIcon": "TLIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/tl-home"
              },
              {
                  "id": 2454,
                  "name": "TL_MY_APPLICATIONS_HEADER",
                  "url": "digit-ui-card",
                  "displayName": "My Applications",
                  "orderNumber": 3,
                  "parentModule": "TL",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/tl/tradelicence/my-application",
                  "leftIcon": "TLIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/tl-home"
              },
              {
                  "id": 2455,
                  "name": "BPA_CITIZEN_HOME_VIEW_APP_BY_CITIZEN_LABEL",
                  "url": "digit-ui-card",
                  "displayName": "View applications by Citizen",
                  "orderNumber": 1,
                  "parentModule": "OBPS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/obps/my-applications",
                  "leftIcon": "OBPSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/obps-home"
              },
              {
                  "id": 2456,
                  "name": "BPA_CITIZEN_HOME_STAKEHOLDER_LOGIN_LABEL",
                  "url": "digit-ui-card",
                  "displayName": "Register as a Stakeholder",
                  "orderNumber": 2,
                  "parentModule": "OBPS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/obps/stakeholder/apply/stakeholder-docs-required",
                  "leftIcon": "OBPSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/obps-home"
              },
              {
                  "id": 2457,
                  "name": "BPA_CITIZEN_HOME_ARCHITECT_LOGIN_LABEL",
                  "url": "digit-ui-card",
                  "displayName": "Registered Architect Login",
                  "orderNumber": 3,
                  "parentModule": "OBPS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/obps/home",
                  "leftIcon": "OBPSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/obps-home"
              },
              {
                  "id": 2458,
                  "name": "PT_SEARCH_AND_PAY",
                  "url": "digit-ui-card",
                  "displayName": "Search and Pay",
                  "orderNumber": 1,
                  "parentModule": "CommonPT",
                  "enabled": false,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/commonpt/property/citizen-search",
                  "leftIcon": "CommonPTIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/commonpt-home"
              },
              {
                  "id": 2459,
                  "name": "PT_CREATE_PROPERTY",
                  "url": "digit-ui-card",
                  "displayName": "Create Property",
                  "orderNumber": 3,
                  "parentModule": "CommonPT",
                  "enabled": false,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/commonpt/property/new-application",
                  "leftIcon": "CommonPTIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/commonpt-home"
              },
              {
                  "id": 2460,
                  "name": "ABG_SEARCH_BILL_COMMON_HEADER",
                  "url": "digit-ui-card",
                  "displayName": "Search Bills",
                  "orderNumber": 1,
                  "parentModule": "Bills",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/bills/billSearch",
                  "leftIcon": "BillsIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/bills-home"
              },
              {
                  "id": 2466,
                  "name": "PRIVACY_AUDIT_REPORT",
                  "url": "digit-ui-card",
                  "displayName": "Privacy Audit Report",
                  "orderNumber": 7,
                  "parentModule": "WS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/Audit",
                  "leftIcon": "WSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/ws-home"
              },
              {
                  "id": 2468,
                  "name": "MCOLLECT_FAQ_S",
                  "url": "digit-ui-card",
                  "displayName": "FAQs",
                  "orderNumber": 7,
                  "parentModule": "MCollect",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/mcollect-faq",
                  "leftIcon": "MCollectIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/mcollect-home"
              },
              {
                  "id": 2469,
                  "name": "WS_FAQ_S",
                  "url": "digit-ui-card",
                  "displayName": "FAQs",
                  "orderNumber": 8,
                  "parentModule": "WS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/ws-faq",
                  "leftIcon": "WSIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/ws-home"
              },
              {
                  "id": 2470,
                  "name": "PT_MY_APPLICATION",
                  "url": "digit-ui-card",
                  "displayName": "My Applications",
                  "orderNumber": 5,
                  "parentModule": "PT",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pt/property/my-applications",
                  "leftIcon": "propertyIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pt-home"
              },
              {
                  "id": 2471,
                  "name": "PT_MY_PROPERTIES",
                  "url": "digit-ui-card",
                  "displayName": "My Properties",
                  "orderNumber": 4,
                  "parentModule": "PT",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pt/property/my-properties",
                  "leftIcon": "propertyIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pt-home"
              },
              {
                  "id": 2472,
                  "name": "PT_PROPERTY_MUTATION",
                  "url": "digit-ui-card",
                  "displayName": "Transfer Property Ownership/Mutation",
                  "orderNumber": 6,
                  "parentModule": "PT",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pt/property/property-mutation/search-property",
                  "leftIcon": "propertyIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pt-home"
              },
              {
                  "id": 2473,
                  "name": "OBPS_FAQ_S",
                  "url": "digit-ui-card",
                  "displayName": "FAQs",
                  "orderNumber": 8,
                  "parentModule": "OBPS",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/obps-faq",
                  "leftIcon": "TLIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/obps-home"
              },
              {
                  "id": 2474,
                  "name": "CS_TITLE_MY_BILLS",
                  "url": "digit-ui-card",
                  "displayName": "My Bills",
                  "orderNumber": 2,
                  "parentModule": "PT",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/payment/my-bills/PT",
                  "leftIcon": "propertyIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pt-home"
              },
              {
                  "id": 2350,
                  "name": "PT_SEARCH_AND_PAY",
                  "url": "digit-ui-card",
                  "displayName": "Search and Pay",
                  "orderNumber": 1,
                  "parentModule": "PT",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pt/property/citizen-search",
                  "leftIcon": "propertyIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pt-home"
              },
              {
                  "id": 2351,
                  "name": "PT_CREATE_PROPERTY",
                  "url": "digit-ui-card",
                  "displayName": "Register Property",
                  "orderNumber": 3,
                  "parentModule": "PT",
                  "enabled": true,
                  "serviceCode": "",
                  "code": "",
                  "path": "",
                  "navigationURL": "/digit-ui/citizen/pt/property/new-application/info",
                  "leftIcon": "propertyIcon",
                  "rightIcon": "",
                  "queryParams": "",
                  "sidebar": "digit-ui-links",
                  "sidebarURL": "/digit-ui/citizen/pt-home"
              },{
                "id": 3001,
                "name": "SOR_HOME",
                "url": "digit-ui-card",
                "displayName": "SOR HOME",
                "orderNumber": 1,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sor-home",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms-home"
            },
            {
                "id": 3011,
                "name": "SOR_LIST",
                "url": "digit-ui-card",
                "displayName": "SOR LIST",
                "orderNumber": 1,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sor-list",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/sor-home"
            },
            {
                "id": 3012,
                "name": "SOR_CREATE",
                "url": "digit-ui-card",
                "displayName": "SOR CREATE",
                "orderNumber": 2,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sor-create",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/sor-home"
            },
            {
                "id": 3013,
                "name": "SOR_DETAILS",
                "url": "digit-ui-card",
                "displayName": "SOR DETAILS",
                "orderNumber": 3,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sor-details/:sor_id",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/sor-home"
            },
            {
                "id": 3014,
                "name": "SOR_EDIT",
                "url": "digit-ui-card",
                "displayName": "SOR UPDATE",
                "orderNumber": 4,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sor-edit/:sor_id",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/sor-home"
            },
            {
                "id": 3002,
                "name": "PRJ_HOME",
                "url": "digit-ui-card",
                "displayName": "PRJ_HOME",
                "orderNumber": 4,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/prj-home",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms-home"
            },
            {
                "id": 3021,
                "name": "PRJ_LIST",
                "url": "digit-ui-card",
                "displayName": "PRJ_LIST",
                "orderNumber": 1,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/prj-list",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/prj-home"
            },
            {
                "id": 3022,
                "name": "PRJ_CREATE",
                "url": "digit-ui-card",
                "displayName": "PRJ_CREATE",
                "orderNumber": 2,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/prj-create",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/prj-home"
            },
            {
                "id": 3023,
                "name": "PRJ_DETAILS",
                "url": "digit-ui-card",
                "displayName": "PRJ_DETAILS",
                "orderNumber": 3,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/prj-details/:sor_id",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/prj-home"
            },
            {
                "id": 3024,
                "name": "PRJ_EDIT",
                "url": "digit-ui-card",
                "displayName": "PRJ_EDIT",
                "orderNumber": 4,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/prj-edit/:sor_id",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/prj-home"
            },
            {
                "id": 3003,
                "name": "SCH_HOME",
                "url": "digit-ui-card",
                "displayName": "SCH_HOME",
                "orderNumber": 1,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sch-home",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms-home"
            },
            {
                "id": 3031,
                "name": "SCH_LIST",
                "url": "digit-ui-card",
                "displayName": "SCH_LIST",
                "orderNumber": 1,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sch-list",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/sch-home"
            },
            {
                "id": 3032,
                "name": "SCH_CREATE",
                "url": "digit-ui-card",
                "displayName": "SCH_CREATE",
                "orderNumber": 2,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sch-create",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/sch-home"
            },
            {
                "id": 3033,
                "name": "SCH_DETAILS",
                "url": "digit-ui-card",
                "displayName": "SCH_DETAILS",
                "orderNumber": 3,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sch-details/:scheme_id",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/sch-home"
            },
            {
                "id": 3034,
                "name": "SCH_EDIT",
                "url": "digit-ui-card",
                "displayName": "SCH_EDIT",
                "orderNumber": 4,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/sch-edit/:scheme_id",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/sch-home"
            },
            
            {
                "id": 3004,
                "name": "PM_HOME",
                "url": "digit-ui-card",
                "displayName": "Physical Milestone",
                "orderNumber": 3,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/phm-home",
                "leftIcon": "propertyIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/phm-home"
            },
            {
                "id": 3041,
                "name": "PHM_LIST",
                "url": "digit-ui-card",
                "displayName": "PHM_LIST",
                "orderNumber": 1,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/phm-list",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/phm-home"
            },
            {
                "id": 3042,
                "name": "PHM_CREATE",
                "url": "digit-ui-card",
                "displayName": "SCH_CREATE",
                "orderNumber": 2,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/phm-create",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/phm-home"
            },
            {
                "id": 3043,
                "name": "PHM_DETAILS",
                "url": "digit-ui-card",
                "displayName": "PHM_DETAILS",
                "orderNumber": 3,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/phm-details/:phm_id",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/phm-home"
            },
            {
                "id": 3044,
                "name": "PHM_EDIT",
                "url": "digit-ui-card",
                "displayName": "PHM_EDIT",
                "orderNumber": 4,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "CITIZEN_SERVICE_WMS",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/wms/phm-edit/:phm_id",
                "leftIcon": "WMSIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms/phm-home"
            },
            {
                "id": 3009,
                "name": "MB_HOME",
                "url": "digit-ui-card",
                "displayName": "Measurement Book",
                "orderNumber": 3,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/mb-home",
                "leftIcon": "propertyIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/mb-home"
            },
              {
                "id": 3005,
                "name": "Contaractor Master",
                "url": "digit-ui-card",
                "displayName": "Contaractor Master",
                "orderNumber": 6,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/cm-home",
                "leftIcon": "propertyIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms-home"
            },
            {
                "id": 3006,
                "name": "TENDER_ENTRY",
                "url": "digit-ui-card",
                "displayName": "Tender Entry",
                "orderNumber": 6,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/citizen/tender-entry/home",
                "leftIcon": "propertyIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms-home"
            },
            {
                "id": 3007,
                "name": "MASTER_DATA",
                "url": "digit-ui-card",
                "displayName": "Master Data",
                "orderNumber": 6,
                "parentModule": "WMS",
                "enabled": true,
                "serviceCode": "",
                "code": "",
                "path": "",
                "navigationURL": "/digit-ui/master-data",
                "leftIcon": "propertyIcon",
                "rightIcon": "",
                "queryParams": "",
                "sidebar": "digit-ui-links",
                "sidebarURL": "/digit-ui/citizen/wms-home"
            },
          ]
        },
      "common-masters": {
          "Designation": [
              {
                  "code": "DESIG_01",
                  "name": "Superintending Engineer ( B&R)",
                  "description": "Superintending Engineer ( B&R)",
                  "active": true
              },
              {
                  "code": "DESIG_02",
                  "name": "Corporation Engineer (B&R)",
                  "description": "Corporation Engineer (B&R)",
                  "active": true
              },
              {
                  "code": "DESIG_03",
                  "name": "Asst. Engineer ( B&R)",
                  "description": "Asst. Engineer ( B&R)",
                  "active": true
              },
              {
                  "code": "DESIG_04",
                  "name": "Junior Engineer ( B&R)",
                  "description": "Junior Engineer ( B&R)",
                  "active": true
              },
              {
                  "code": "DESIG_05",
                  "name": "Land Scape Officer",
                  "description": "Land Scape Officer",
                  "active": true
              },
              {
                  "code": "DESIG_06",
                  "name": "Superintending Engineer ( O&M)",
                  "description": "Superintending Engineer ( O&M)",
                  "active": true
              },
              {
                  "code": "DESIG_07",
                  "name": "Corporation Engineer (O&M)",
                  "description": "Corporation Engineer (O&M)",
                  "active": true
              },
              {
                  "code": "DESIG_08",
                  "name": "Asst. Engineer ( O&M)",
                  "description": "Asst. Engineer ( O&M)",
                  "active": true
              },
              {
                  "code": "DESIG_09",
                  "name": "Junior Engineer ( O&M)",
                  "description": "Junior Engineer ( O&M)",
                  "active": true
              },
              {
                  "code": "DESIG_10",
                  "name": "Superintending Engineer ( Light)",
                  "description": "Superintending Engineer ( Light)",
                  "active": true
              },
              {
                  "code": "DESIG_11",
                  "name": "Corporation Engineer (Light)",
                  "description": "Corporation Engineer (Light)",
                  "active": true
              },
              {
                  "code": "DESIG_12",
                  "name": "Junior Engineer ( Light)",
                  "description": "Junior Engineer ( Light)",
                  "active": true
              },
              {
                  "code": "DESIG_13",
                  "name": "Health Officer",
                  "description": "Health Officer",
                  "active": true
              },
              {
                  "code": "DESIG_14",
                  "name": "Medical Officer",
                  "description": "Medical Officer",
                  "active": true
              },
              {
                  "code": "DESIG_15",
                  "name": "Chief Sanitary Inspector",
                  "description": "Mechanical Oversear",
                  "active": true
              },
              {
                  "code": "DESIG_16",
                  "name": "Sainitary Inspector",
                  "description": "Clerk",
                  "active": true
              },
              {
                  "code": "DESIG_17",
                  "name": "Sainitary Supervisor",
                  "description": "Accountant",
                  "active": true
              },
              {
                  "code": "DESIG_18",
                  "name": "Senior Town Planner",
                  "description": "Senior Town Planner",
                  "active": true
              },
              {
                  "code": "DESIG_19",
                  "name": "Municipal Town Planner",
                  "description": "Municipal Town Planner",
                  "active": true
              },
              {
                  "code": "DESIG_20",
                  "name": "Asst. Town Planner",
                  "description": "Asst. Town Planner",
                  "active": true
              },
              {
                  "code": "DESIG_21",
                  "name": "Building Inspector",
                  "description": "Building Inspector",
                  "active": true
              },
              {
                  "code": "DESIG_22",
                  "name": "Junior Enginer ( Horticulutre)",
                  "description": "Junior Enginer ( Horticulutre)",
                  "active": true
              },
              {
                  "code": "DESIG_23",
                  "name": "Citizen service representative",
                  "description": "Citizen service representative",
                  "active": true
              },
              {
                  "name": "Deputy Controller Finance and Accounts",
                  "description": "Deputy Controller Finance and Accounts",
                  "code": "DESIG_1001",
                  "active": true
              },
              {
                  "name": "Accountant",
                  "description": "Accountant",
                  "code": "DESIG_58",
                  "active": true
              },
              {
                  "code": "DESIG_24",
                  "name": "Assistant Commissioner",
                  "description": "Assistant Commissioner",
                  "active": true
              },
              {
                  "name": "Superintendent",
                  "description": "Superintendent",
                  "code": "DESIG_47",
                  "active": true
              },
              {
                  "name": "Accounts Officer",
                  "description": "Accounts Officer",
                  "code": "AO",
                  "active": true
              },
              {
                  "name": "Commissioner",
                  "description": "Commissioner",
                  "code": "COMM",
                  "active": true
              }
          ],
          "Department": [
              {
                  "name": "Street Lights",
                  "code": "DEPT_1",
                  "active": true
              },
              {
                  "name": "Building & Roads",
                  "code": "DEPT_2",
                  "active": true
              },
              {
                  "name": "Health & Sanitation",
                  "code": "DEPT_3",
                  "active": true
              },
              {
                  "name": "Operation & Maintenance",
                  "code": "DEPT_4",
                  "active": true
              },
              {
                  "name": "Horticulture",
                  "code": "DEPT_5",
                  "active": true
              },
              {
                  "name": "Building Branch",
                  "code": "DEPT_6",
                  "active": true
              },
              {
                  "name": "Citizen service desk",
                  "code": "DEPT_7",
                  "active": true
              },
              {
                  "name": "Complaint Cell",
                  "code": "DEPT_8",
                  "active": true
              },
              {
                  "name": "Executive Branch",
                  "code": "DEPT_9",
                  "active": true
              },
              {
                  "name": "Others",
                  "code": "DEPT_10",
                  "active": true
              },
              {
                  "name": "Tax Branch",
                  "code": "DEPT_13",
                  "active": true
              },
              {
                  "name": "Accounts Branch",
                  "code": "DEPT_25",
                  "active": true
              },
              {
                  "name": "Works Branch",
                  "code": "DEPT_35",
                  "active": true
              }
          ],          
          "Fund": [
            {
                "name": "Fund One",
                "code": "FUND_1",
                "active": true
            },
            {
              "name": "Fund Two",
              "code": "FUND_2",
              "active": true
          },
          {
            "name": "Fund Three",
            "code": "FUND_3",
            "active": true
          },
          ],
          "Chapter": [
            {
                "name": "Chapter One",
                "code": "CHAPTER_1",
                "active": true
            },
            {
              "name": "Chapter Two",
              "code": "CHAPTER_2",
              "active": true
          },
          {
            "name": "Chapter Three",
            "code": "CHAPTER_3",
            "active": true
        },{
          "name": "Chapter Four",
          "code": "CHAPTER_4",
          "active": true
      },
      {
        "name": "Chapter Five",
        "code": "CHAPTER_5",
        "active": true
    },
          ],
          "Unit": [
            {
                "name": "Unit One",
                "code": "UNIT_1",
                "active": true
            },
            {
              "name": "Unit Two",
              "code": "UNIT_2",
              "active": true
          },
          {
            "name": "Unit Three",
            "code": "UNIT_3",
            "active": true
            },{
            "name": "Unit Four",
            "code": "UNIT_4",
            "active": true
        },
        {
            "name": "Unit Five",
            "code": "UNIT_5",
            "active": true
        },
          ],
          "StateInfo": [
              {
                  "name": "Demo",
                  "code": "pg",
                  "qrCodeURL": "https://lh3.googleusercontent.com/-311gz2-xcHw/X6KRNSQTkWI/AAAAAAAAAKU/JmHSj-6rKPMVFbo6oL5x4JhYTTg8-UHmwCK8BGAsYHg/s0/2020-11-04.png",
                  "bannerUrl": "https://upyog-assets.s3.ap-south-1.amazonaws.com/bannerImage.png",
                  "logoUrl": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/nugp.png",
                  "logoUrlWhite": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/nugp.png",
                  "statelogo": "https://s3.ap-south-1.amazonaws.com/pg-egov-assets/pg.citya/logo.png",
                  "hasLocalisation": true,
                  "defaultUrl": {
                      "citizen": "/user/register",
                      "employee": "/user/login"
                  },
                  "languages": [
                      {
                          "label": "ENGLISH",
                          "value": "en_IN"
                      },
                      {
                          "label": "हिंदी",
                          "value": "hi_IN"
                      }
                  ],
                  "localizationModules": [
                      {
                          "label": "rainmaker-abg",
                          "value": "rainmaker-abg"
                      },
                      {
                          "label": "rainmaker-common",
                          "value": "rainmaker-common"
                      },
                      {
                          "label": "rainmaker-noc",
                          "value": "rainmaker-noc"
                      },
                      {
                          "label": "rainmaker-pt",
                          "value": "rainmaker-pt"
                      },
                      {
                          "label": "rainmaker-uc",
                          "value": "rainmaker-uc"
                      },
                      {
                          "label": "rainmaker-pgr",
                          "value": "rainmaker-pgr"
                      },
                      {
                          "label": "rainmaker-tl",
                          "value": "rainmaker-tl"
                      },
                      {
                          "label": "rainmaker-hr",
                          "value": "rainmaker-hr"
                      },
                      {
                          "label": "rainmaker-test",
                          "value": "rainmaker-test"
                      },
                      {
                          "label": "finance-erp",
                          "value": "finance-erp"
                      },
                      {
                          "label": "rainmaker-receipt",
                          "value": "rainmaker-receipt"
                      },
                      {
                          "label": "rainmaker-dss",
                          "value": "rainmaker-dss"
                      },
                      {
                          "label": "rainmaker-fsm",
                          "value": "rainmaker-fsm"
                      }
                  ]
              }
          ],
          "wfSlaConfig": [
              {
                  "slotPercentage": 33,
                  "positiveSlabColor": "#4CAF50",
                  "negativeSlabColor": "#F44336",
                  "middleSlabColor": "#EEA73A"
              }
          ],
          "uiHomePage": [
              {
                  "appBannerDesktop": {
                      "code": "APP_BANNER_DESKTOP",
                      "name": "App Banner Desktop View",
                      "bannerUrl": "https://uat.digit.org/egov-uat-assets/app-banner-mobile.jpg",
                      "enabled": true
                  },
                  "appBannerMobile": {
                      "code": "APP_BANNER_MOBILE",
                      "name": "App Banner Mobile View",
                      "bannerUrl": "https://uat.digit.org/egov-uat-assets/app-banner-mobile.jpg",
                      "enabled": true
                  },
                  "citizenServicesCard": {
                      "code": "HOME_CITIZEN_SERVICES_CARD",
                      "name": "Home Citizen services Card",
                      "enabled": true,
                      "headerLabel": "DASHBOARD_CITIZEN_SERVICES_LABEL",
                      "sideOption": {
                          "name": "DASHBOARD_VIEW_ALL_LABEL",
                          "enabled": true,
                          "navigationUrl": "/digit-ui/citizen/all-services"
                      },
                      "props": [
                          {
                              "code": "CITIZEN_SERVICE_PGR",
                              "name": "Complaints",
                              "label": "ES_PGR_HEADER_COMPLAINT",
                              "enabled": true,
                              "navigationUrl": "/digit-ui/citizen/pgr-home"
                          },
                          {
                              "code": "CITIZEN_SERVICE_PT",
                              "name": "Property Tax",
                              "label": "MODULE_PT",
                              "enabled": true,
                              "navigationUrl": "/digit-ui/citizen/pt-home"
                          },
                          {
                              "code": "CITIZEN_SERVICE_TL",
                              "name": "Trade Licence",
                              "label": "MODULE_TL",
                              "enabled": true,
                              "navigationUrl": "/digit-ui/citizen/tl-home"
                          },
                          {
                              "code": "CITIZEN_SERVICE_WS",
                              "name": "Water & Sewerage",
                              "label": "ACTION_TEST_WATER_AND_SEWERAGE",
                              "enabled": true,
                              "navigationUrl": "/digit-ui/citizen/ws-home"
                          },
                          {
                              "code": "CITIZEN_SERVICE_WMS",
                              "name": "WMS",
                              "label": "CITIZEN_SERVICE_WMS_LB",
                              "enabled": true,
                              "navigationUrl": "/digit-ui/citizen/wms-home"
                          }
                      ]
                  },
                  "informationAndUpdatesCard": {
                      "code": "HOME_CITIZEN_INFO_UPDATE_CARD",
                      "name": "Home Citizen Information and Updates card",
                      "enabled": true,
                      "headerLabel": "CS_COMMON_DASHBOARD_INFO_UPDATES",
                      "sideOption": {
                          "name": "DASHBOARD_VIEW_ALL_LABEL",
                          "enabled": true,
                          "navigationUrl": ""
                      },
                      "props": [
                          {
                              "code": "CITIZEN_MY_CITY",
                              "name": "My City",
                              "label": "CS_HEADER_MYCITY",
                              "enabled": true,
                              "navigationUrl": ""
                          },
                          {
                              "code": "CITIZEN_EVENTS",
                              "name": "Events",
                              "label": "EVENTS_EVENTS_HEADER",
                              "enabled": true,
                              "navigationUrl": "/digit-ui/citizen/engagement/events"
                          },
                          {
                              "code": "CITIZEN_DOCUMENTS",
                              "name": "Documents",
                              "label": "CS_COMMON_DOCUMENTS",
                              "enabled": true,
                              "navigationUrl": "/digit-ui/citizen/engagement/docs"
                          },
                          {
                              "code": "CITIZEN_SURVEYS",
                              "name": "Surveys",
                              "label": "CS_COMMON_SURVEYS",
                              "enabled": true,
                              "navigationUrl": "/digit-ui/citizen/engagement/surveys/list"
                          }
                      ]
                  },
                  "whatsNewSection": {
                      "code": "WHATSNEW",
                      "name": "What's New",
                      "enabled": true,
                      "headerLabel": "DASHBOARD_WHATS_NEW_LABEL",
                      "sideOption": {
                          "name": "DASHBOARD_VIEW_ALL_LABEL",
                          "enabled": true,
                          "navigationUrl": "/digit-ui/citizen/engagement/whats-new"
                      }
                  },
                  "whatsAppBannerDesktop": {
                      "code": "WHATSAPP_BANNER_DESKTOP",
                      "name": "WhatsApp Banner Desktop View",
                      "bannerUrl": "https://uat.digit.org/egov-uat-assets/whatsapp-web.jpg",
                      "enabled": true,
                      "navigationUrl": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva"
                  },
                  "whatsAppBannerMobile": {
                      "code": "WHATSAPP_BANNER_MOBILE",
                      "name": "WhatsApp Banner Mobile View",
                      "bannerUrl": "https://uat.digit.org/egov-uat-assets/whatsapp-mobile.jpg",
                      "enabled": true,
                      "navigationUrl": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva"
                  }
              }
          ],
          "StaticData": [
          {
              "PT": {
                  "payViaWhatsApp": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva",
                  "helpline": {
                      "contactOne": "01124643284",
                      "contactTwo": "01124617543"
                  },
                  "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
                  "staticDataOne": "7-10",
                  "staticDataTwo": "₹500",
                  "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
              },
              "TL": {
                  "helpline": {
                      "contactOne": "01124643284",
                      "contactTwo": "01124617543"
                  },
                  "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
                  "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
              },
              "MCOLLECT": {
                  "helpline": {
                      "contactOne": "01124643284",
                      "contactTwo": "01124617543"
                  },
                  "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
                  "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
              },
              "PGR": {
                  "helpline": {
                      "contactOne": "01124643284",
                      "contactTwo": "01124617543"
                  },
                  "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
                  "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
              },
              "OBPS": {
                  "helpline": {
                      "contactOne": "01124643284",
                      "contactTwo": "01124617543"
                  },
                  "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
                  "validity": "1",
                  "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
              },
              "WS": {
                  "payViaWhatsApp": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva",
                  "helpline": {
                      "contactOne": "01124643284",
                      "contactTwo": "01124617543"
                  },
                  "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
                  "staticDataOne": "30",
                  "staticDataTwo": "25-30",
                  "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
              },
              "WMS": {
                  "payViaWhatsApp": "https://api.whatsapp.com/send?phone=918744060444&text=mSeva",
                  "helpline": {
                      "contactOne": "01124643284",
                      "contactTwo": "01124617543"
                  },
                  "serviceCenter": "India Habitat Centre, Lodhi Road, New Delhi – 110003",
                  "staticDataOne": "30",
                  "staticDataTwo": "25-30",
                  "viewMapLocation": "https://www.google.com/maps/place/National+Institute+of+Urban+Affairs/@28.5899487,77.2232635,17z/data=!3m2!4b1!5s0x390ce2f1d34a3da5:0x8daa83a9ecfc9da9!4m6!3m5!1s0x390d1d5ebfb73dcf:0xc3b5eadedcbcca50!8m2!3d28.589944!4d77.2258384!16s%2Fm%2F0gx0f1x?entry=ttu"
              }
          }
          ]
          },
      "tenant": {
          "tenants": [
              {
                  "code": "pg.citya",
                  "name": "City A",
                  "description": "City A",
                  "pincode": [
                      143001,
                      143002,
                      143003,
                      143004,
                      143005
                  ],
                  "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
                  "imageId": null,
                  "domainUrl": "https://www.upyog-test.niua.org",
                  "type": "CITY",
                  "twitterUrl": null,
                  "facebookUrl": null,
                  "emailId": "citya@gmail.com",
                  "OfficeTimings": {
                      "Mon - Fri": "9.00 AM - 6.00 PM"
                  },
                  "city": {
                      "name": "City A",
                      "localName": null,
                      "districtCode": "CITYA",
                      "districtName": null,
                      "districtTenantCode": "pg.citya",
                      "regionName": null,
                      "ulbGrade": "Municipal Corporation",
                      "longitude": 75.5761829,
                      "latitude": 31.3260152,
                      "shapeFileLocation": null,
                      "captcha": null,
                      "code": "1013",
                      "ddrName": "DDR A"
                  },
                  "address": "City A Municipal Corporation",
                  "contactNumber": "001-2345876"
              },
              {
                  "code": "pg.cityb",
                  "name": "City B",
                  "description": null,
                  "pincode": [
                      143006,
                      143007,
                      143008,
                      143009,
                      143010
                  ],
                  "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
                  "imageId": null,
                  "domainUrl": "https://www.upyog-test.niua.org",
                  "type": "CITY",
                  "twitterUrl": null,
                  "facebookUrl": null,
                  "emailId": "cityb@gmail.com",
                  "OfficeTimings": {
                      "Mon - Fri": "9.00 AM - 6.00 PM",
                      "Sat": "9.00 AM - 12.00 PM"
                  },
                  "city": {
                      "name": "City B",
                      "localName": null,
                      "districtCode": "CITYB",
                      "districtName": null,
                      "districtTenantCode": "pg.cityb",
                      "regionName": null,
                      "ulbGrade": "Municipal Corporation",
                      "longitude": 74.8722642,
                      "latitude": 31.6339793,
                      "shapeFileLocation": null,
                      "captcha": null,
                      "code": "107",
                      "ddrName": "DDR B"
                  },
                  "address": "City B Municipal Corporation Address",
                  "contactNumber": "0978-7645345",
                  "helpLineNumber": "0654-8734567"
              },
              {
                  "code": "pg.cityc",
                  "name": "City C",
                  "description": null,
                  "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
                  "imageId": null,
                  "domainUrl": "https://www.upyog-test.niua.org",
                  "type": "CITY",
                  "twitterUrl": null,
                  "facebookUrl": null,
                  "emailId": "cityc@gmail.com",
                  "OfficeTimings": {
                      "Mon - Fri": "9.00 AM - 6.00 PM",
                      "Sat": "9.00 AM - 12.00 PM"
                  },
                  "city": {
                      "name": "City C",
                      "localName": null,
                      "districtCode": "CITYC",
                      "districtName": null,
                      "districtTenantCode": "pg.cityc",
                      "regionName": null,
                      "ulbGrade": "Municipal Corporation",
                      "longitude": 73.8722642,
                      "latitude": 31.6339793,
                      "shapeFileLocation": null,
                      "captcha": null,
                      "code": "108",
                      "ddrName": "DDR C"
                  },
                  "address": "City C Municipal Corporation Address",
                  "contactNumber": "0978-7645345",
                  "helpLineNumber": "0654-8734567"
              },
              {
                  "code": "pg.cityd",
                  "name": "City D",
                  "description": null,
                  "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
                  "imageId": null,
                  "domainUrl": "https://www.upyog-test.niua.org",
                  "type": "CITY",
                  "twitterUrl": null,
                  "facebookUrl": null,
                  "emailId": "cityd@gmail.com",
                  "OfficeTimings": {
                      "Mon - Fri": "9.00 AM - 6.00 PM",
                      "Sat": "9.00 AM - 12.00 PM"
                  },
                  "city": {
                      "name": "City D",
                      "localName": null,
                      "districtCode": "CITYD",
                      "districtName": null,
                      "districtTenantCode": "pg.cityd",
                      "regionName": null,
                      "ulbGrade": "Municipal Corporation",
                      "longitude": 75.8722642,
                      "latitude": 35.6339793,
                      "shapeFileLocation": null,
                      "captcha": null,
                      "code": "109",
                      "ddrName": "DDR D"
                  },
                  "address": "City D Municipal Corporation Address",
                  "contactNumber": "0978-7645345",
                  "helpLineNumber": "0654-8734567"
              },
              {
                  "code": "pg.citye",
                  "name": "City E",
                  "description": null,
                  "logoId": "https://in-egov-assets.s3.ap-south-1.amazonaws.com/in.citya/logo.png",
                  "imageId": null,
                  "domainUrl": "https://www.upyog-test.niua.org",
                  "type": "CITY",
                  "twitterUrl": null,
                  "facebookUrl": null,
                  "emailId": "citye@gmail.com",
                  "OfficeTimings": {
                      "Mon - Fri": "9.00 AM - 6.00 PM",
                      "Sat": "9.00 AM - 12.00 PM"
                  },
                  "city": {
                      "name": "City E",
                      "localName": null,
                      "districtCode": "CITYE",
                      "districtName": null,
                      "districtTenantCode": "pg.citye",
                      "regionName": null,
                      "ulbGrade": "Municipal Corporation",
                      "longitude": 76.8722642,
                      "latitude": 36.6339793,
                      "shapeFileLocation": null,
                      "captcha": null,
                      "code": "110",
                      "ddrName": "DDR E"
                  },
                  "address": "City E Municipal Corporation Address",
                  "contactNumber": "0978-7645345",
                  "helpLineNumber": "0654-8734567"
              },
              {
                  "code": "pg",
                  "name": "State",
                  "description": "State",
                  "logoId": "https://s3.ap-south-1.amazonaws.com/pg-egov-assets/pg.citya/logo.png",
                  "imageId": null,
                  "domainUrl": "www.upyog-test.niua.org",
                  "type": "CITY",
                  "twitterUrl": null,
                  "facebookUrl": null,
                  "emailId": "pg.state@gmail.com",
                  "OfficeTimings": {
                      "Mon - Fri": "9.00 AM - 5.00 PM"
                  },
                  "city": {
                      "name": "State",
                      "localName": "Demo State",
                      "districtCode": "0",
                      "districtName": "State",
                      "districtTenantCode": "pg",
                      "regionName": "State",
                      "ulbGrade": "ST",
                      "longitude": 75.3412,
                      "latitude": 31.1471,
                      "shapeFileLocation": null,
                      "captcha": null,
                      "code": "0",
                      "ddrName": null
                  },
                  "address": "State Municipal Corporation",
                  "contactNumber": "0978-7645345"
              }
          ],
          "citymodule": [
              {
                  "module": "PGR",
                  "code": "PGR",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PGR.png",
                  "active": true,
                  "order": 2,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              }, 
              {
              "module": "WMS",
              "code": "WMS",
              "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PGR.png",
              "active": true,
              "order": 2,
              "tenants": [
                  {
                      "code": "pg.citya"
                  },
                  {
                      "code": "pg.cityb"
                  },
                  {
                      "code": "pg.cityc"
                  },
                  {
                      "code": "pg.cityd"
                  },
                  {
                      "code": "pg.citye"
                  }
              ]
              },
              {
                  "module": "PT",
                  "code": "PT",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PT.png",
                  "active": true,
                  "order": 1,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "QuickPayLinks",
                  "code": "QuickPayLinks",
                  "active": true,
                  "order": 1,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      }
                  ]
              },
              {
                  "module": "Finance",
                  "code": "Finance",
                  "active": false,
                  "order": 4,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "TL",
                  "code": "TL",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/TL.png",
                  "active": true,
                  "order": 2,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "FireNoc",
                  "code": "FireNoc",
                  "active": true,
                  "order": 2,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "UC",
                  "code": "UC",
                  "active": false,
                  "order": 2,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "BPAREG",
                  "code": "BPAREG",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/OBPS.png",
                  "active": true,
                  "order": 2,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "BPAAPPLY",
                  "code": "BPAAPPLY",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/OBPS.png",
                  "active": true,
                  "order": 2,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "PGR.WHATSAPP",
                  "code": "PGR.WHATSAPP",
                  "active": false,
                  "order": 4,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      }
                  ]
              },
              {
                  "module": "OBPS",
                  "code": "OBPS",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/OBPS.png",
                  "active": true,
                  "order": 12,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "FSM",
                  "code": "FSM",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/FSM.png",
                  "active": true,
                  "order": 2,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "Payment",
                  "code": "Payment",
                  "active": true,
                  "order": 1,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "Receipts",
                  "code": "Receipts",
                  "active": true,
                  "order": 3,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "NOC",
                  "code": "NOC",
                  "active": true,
                  "order": 2,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "DSS",
                  "code": "DSS",
                  "active": true,
                  "order": 6,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "Engagement",
                  "code": "Engagement",
                  "active": true,
                  "order": 3,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "MCollect",
                  "code": "MCollect",
                  "active": true,
                  "order": 1,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "HRMS",
                  "code": "HRMS",
                  "active": true,
                  "order": 2,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "CommonPT",
                  "code": "CommonPT",
                  "active": true,
                  "order": 3,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "NDSS",
                  "code": "NDSS",
                  "active": true,
                  "order": 5,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "WS",
                  "code": "WS",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/WS.png",
                  "active": true,
                  "order": 1,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      }
                  ]
              },
              {
                  "module": "SW",
                  "code": "SW",
                  "active": true,
                  "order": 1,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      }
                  ]
              },
              {
                  "module": "BillAmendment",
                  "code": "BillAmendment",
                  "active": true,
                  "order": 1,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      }
                  ]
              },
              {
                  "module": "Bills",
                  "code": "Bills",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/Bill.png",
                  "active": true,
                  "order": 3,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      }
                  ]
              },
              {
                  "module": "Birth",
                  "code": "Birth",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PT.png",
                  "active": true,
                  "order": 1,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              },
              {
                  "module": "Death",
                  "code": "Death",
                  "bannerImage": "https://egov-uat-assets.s3.amazonaws.com/PT.png",
                  "active": true,
                  "order": 1,
                  "tenants": [
                      {
                          "code": "pg.citya"
                      },
                      {
                          "code": "pg.cityb"
                      },
                      {
                          "code": "pg.cityc"
                      },
                      {
                          "code": "pg.cityd"
                      },
                      {
                          "code": "pg.citye"
                      }
                  ]
              }
          ]
          },
      "DIGIT-UI": {}
    };
    //const { MdmsRes } = await MdmsService.init(stateCode);
    const stateInfo = MdmsRes["common-masters"]?.StateInfo?.[0]||{};
    const uiHomePage = MdmsRes["common-masters"]?.uiHomePage?.[0]||{};
    const localities = {};
    const revenue_localities = {};
    const initData = {
      languages: stateInfo.hasLocalisation ? stateInfo.languages : [{ label: "ENGLISH", value: "en_IN" }],
      stateInfo: {
        code: stateInfo.code,
        name: stateInfo.name,
        logoUrl: stateInfo.logoUrl,
        statelogo: stateInfo.statelogo,
        logoUrlWhite: stateInfo.logoUrlWhite,
        bannerUrl: stateInfo.bannerUrl,
      },
      localizationModules: stateInfo.localizationModules,
      modules: MdmsRes?.tenant?.citymodule.filter((module) => module?.active).filter((module) => enabledModules?.includes(module?.code))?.sort((x,y)=>x?.order-y?.order),
      uiHomePage: uiHomePage
    };

  
    initData.selectedLanguage = Digit.SessionStorage.get("locale") || initData.languages[0].value;

    ApiCacheService.saveSetting(MdmsRes["DIGIT-UI"]?.ApiCachingSettings);

    const moduleTenants = initData.modules
      .map((module) => module.tenants)
      .flat()
      .reduce((unique, ele) => (unique.find((item) => item.code === ele.code) ? unique : [...unique, ele]), []);
    initData.tenants = MdmsRes?.tenant?.tenants
         .map((tenant) => ({ i18nKey: `TENANT_TENANTS_${tenant.code.replace(".", "_").toUpperCase()}`, ...tenant }));
      // .filter((item) => !!moduleTenants.find((mt) => mt.code === item.code))
      // .map((tenant) => ({ i18nKey: `TENANT_TENANTS_${tenant.code.replace(".", "_").toUpperCase()}`, ...tenant }));

    await LocalizationService.getLocale({
      modules: [
        `rainmaker-common`,
        `rainmaker-${stateCode.toLowerCase()}`,
      ],
      locale: initData.selectedLanguage,
      tenantId: stateCode,
    });
    Storage.set("initData", initData);
    initData.revenue_localities = revenue_localities;
    initData.localities = localities;
    setTimeout(() => {
      renderTenantLogos(stateInfo, initData.tenants);
    }, 0);
    console.log(initData);
    return initData;
  },
  defaultData: async (stateCode, moduleCode, language) => {
    let moduleCodes = [];
    if(typeof moduleCode !== "string") moduleCode.forEach(code => { moduleCodes.push(`rainmaker-${code.toLowerCase()}`) });
    const LocalePromise = LocalizationService.getLocale({
      modules: typeof moduleCode == "string" ? [`rainmaker-${moduleCode.toLowerCase()}`] : moduleCodes,
      locale: language,
      tenantId: stateCode,
    });
    await LocalePromise;
    return {};
  },
};
