import { FSMService } from "../services/elements/FSM";
import { PTService } from "../services/elements/PT";
import { useQuery } from "react-query";
import { MCollectService } from "../services/elements/MCollect";
import { PTRService } from "../services/elements/PTR";
import { CHBServices } from "../services/elements/CHB";
import {ADSServices} from "../services/elements/ADS"

const fsmApplications = async (tenantId, filters) => {
  return (await FSMService.search(tenantId, { ...filters, limit: 10000 })).fsm;
};

const ptrApplications = async (tenantId, filters) => {
  return (await PTRService.search({ tenantId, filters })).PetRegistrationApplications;
};


const chbApplications = async (tenantId, filters) => {
  return (await CHBServices.search({ tenantId, filters })).hallsBookingApplication;
};

const adsBookings = async (tenantId, filters) => {
  return (await ADSServices.search({ tenantId, filters })).bookingApplication;
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
  if (window.location.href.includes("chb-services")) {
    _key = "chb"
  } 
  if (window.location.href.includes("ads-services")) {
    _key = "ads"
  } 

  /* key from application ie being used as consumer code in bill */
  const { searchFn, key, label } = refObj(tenantId, filters)[_key];
  const applications = useQuery(["applicationsForBillDetails", { tenantId, businessService, filters, searchFn }], searchFn, {
    ...config,
  });

  return { ...applications, key, label };
};
