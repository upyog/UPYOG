import { FSMService } from "../services/elements/FSM";
import { PTService } from "../services/elements/PT";
import { useQuery } from "react-query";
import { MCollectService } from "../services/elements/MCollect";
import { PTRService } from "../services/elements/PTR";
import { CHBServices } from "../services/elements/CHB";
import {ADSServices} from "../services/elements/ADS";
import { SVService } from "../services/elements/SV";
import { WTService } from "../services/elements/WT";
import { MTService } from "../services/elements/MT";
import { TPService } from "../services/elements/TP";

const fsmApplications = async (tenantId, filters) => {
  return (await FSMService.search(tenantId, { ...filters, limit: 10000 })).fsm;
};

const ptrApplications = async (tenantId, filters) => {
  return (await PTRService.search({ tenantId, filters })).PetRegistrationApplications;
};

const ptApplications = async (tenantId, filters) => {
  return (await PTService.search({ tenantId, filters })).Properties;
};

const advtApplications = async (tenantId, filters) => {
  return (await MCollectService.search_bill({ tenantId, filters })).Bills;
};
const tlApplications = async (tenantId, filters) => {
  return (await TLService.search_bill({ tenantId, filters })).Bills;
};

const svApplications = async (tenantId, filters) => {
  return (await SVService.search({ tenantId, filters })).SVDetail;
};

const chbApplications = async (tenantId, filters) => {
  return (await CHBServices.search({ tenantId, filters })).hallsBookingApplication;
};

const adsBookings = async (tenantId, filters) => {
  return (await ADSServices.search({ tenantId, filters })).bookingApplication;
};
const wtBookings = async (tenantId, filters) => {
  return (await WTService.search({ tenantId, filters })).waterTankerBookingDetail;
};

const mtBookings = async (tenantId, filters) => {
  return (await MTService.search({ tenantId, filters })).mobileToiletBookingDetails;
};

const tpBookings = async (tenantId, filters) => {
  return (await TPService.search({ tenantId, filters })).treePruningBookingDetails;
};

const refObj = (tenantId, filters) => {
  let consumerCodes = filters?.consumerCodes;
  // delete filters.consumerCodes;

  return {
    pt: {
      searchFn: () => ptApplications(null, { ...filters, propertyIds: consumerCodes }),
      key: "propertyId",
      label: "PT_UNIQUE_PROPERTY_ID",
    },
    ptr: {
      searchFn: () => ptrApplications(null, { ...filters, applicationNumber: consumerCodes }),
      key: "applicationNumber",
      label: "PTR_UNIQUE_APPLICATION_NUMBER",
    },
    fsm: {
      searchFn: () => fsmApplications(tenantId, filters),
      key: "applicationNo",
      label: "FSM_APPLICATION_NO",
    },
    mcollect: {
      searchFn: () => advtApplications(tenantId, filters),
      key: "consumerCode",
      label: "UC_CHALLAN_NO",
    },
    ws: {
      searchFn: () => advtApplications(tenantId, filters),
      key: "consumerCode",
      label: "WS_MYCONNECTIONS_CONSUMER_NO",
    },
    sw: {
      searchFn: () => advtApplications(tenantId, filters),
      key: "consumerCode",
      label: "WS_MYCONNECTIONS_CONSUMER_NO",
    },
    TL: {
      searchFn: () => tlApplications(tenantId, filters),
      key: "consumerCode",
      label: "REFERENCE_NO",
    },
    BPAREG: {
      searchFn: () => tlApplications(tenantId, filters),
      key: "consumerCode",
      label: "REFERENCE_NO",
    },
    BPA: {
      searchFn: () => tlApplications(tenantId, filters),
      key: "consumerCode",
      label: "REFERENCE_NO",
    },
    street: {
      searchFn: () => svApplications(null, { ...filters, applicationNo: consumerCodes }),
      key: "applicationNo",
      label: "SV_APPLICATION_NO",
    },
    chb: {
      searchFn: () => chbApplications(null, { ...filters, bookingNo: consumerCodes }),
      key: "bookingNo",
      label: "CHB_BOOKING_NO",
    },
    ads: {
      searchFn: () => adsBookings(null, { ...filters, bookingNo: consumerCodes }),
      key: "bookingNo",
      label: "ADS_BOOKING_NO",
    },
    wt: {
      searchFn: () => wtBookings(null, { ...filters, bookingNo: consumerCodes }),
      key: "bookingNo",
      label: "WT_BOOKING_NO",
    },
    mt: {
      searchFn: () => mtBookings(null, { ...filters, bookingNo: consumerCodes }),
      key: "bookingNo",
      label: "MT_BOOKING_NO",
    },
    tp: {
      searchFn: () => tpBookings(null, { ...filters, bookingNo: consumerCodes }),
      key: "bookingNo",
      label: "TP_BOOKING_NO",
    }
  };
};

export const useApplicationsForBusinessServiceSearch = ({ tenantId, businessService, filters }, config = {}) => {
  let _key = businessService?.toLowerCase().split(".")[0];
  if (window.location.href.includes("mcollect")) {
    _key = "mcollect";
  }
  if (window.location.href.includes("TL")) {
    _key = "TL";
  } 
  if (window.location.href.includes("BPAREG")) {
    _key = businessService
  }
  if (window.location.href.includes("BPA.")) {
    _key = "BPA"
  }
  if (window.location.href.includes("pet-services")) {
    _key = "ptr"
  } 
  if (window.location.href.includes("sv-services")) {
    _key = "street"
  } 
  if (window.location.href.includes("chb-services")) {
    _key = "chb"
  } 
  if (window.location.href.includes("adv-services")) {
    _key = "ads"
  } 
  if (window.location.href.includes("request-service.water_tanker")) {
    _key = "wt"
  } 
  if (window.location.href.includes("request-service.mobile_toilet")) {
    _key = "mt"
  } 
  if (window.location.href.includes("request-service.tree_pruning")) {
    _key = "tp"
  }



  /* key from application ie being used as consumer code in bill */
  const { searchFn, key, label } = filters!==undefined ? refObj(tenantId, filters)[_key]:"";
  const applications = useQuery(["applicationsForBillDetails", { tenantId, businessService, filters, searchFn }], searchFn, {
    ...config,
  });

  return { ...applications, key, label };
};
