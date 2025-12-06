import { OBPSService } from "../../elements/OBPS"
import { PreApprovedPlanService } from "../../elements/PREAPPROVEDPLAN";

export const Search = {
  scrutinyDetails: async (tenantId, params, data, all = false) => {
    const response = await OBPSService.scrutinyDetails(tenantId, params, data);
    if (window.location.href.includes("bpa/inbox")) {
      return response?.edcrDetail
    } else if(response == "No Record Found" && (window.location.href.includes("/basic-details") || window.location.href.includes("/basic-details"))) {
      return "BPA_NO_RECORD_FOUND"
    } else {
      return response?.edcrDetail?.[0]
    }
    // return window.location.href.includes("bpa/inbox") ? response?.edcrDetail : response?.edcrDetail?.[0];
  },
  preApproveData: async (filters, all = false) => {
    const response = await PreApprovedPlanService.search(filters);
    
      return response?.preapprovedPlan
    
    // return window.location.href.includes("bpa/inbox") ? response?.edcrDetail : response?.edcrDetail?.[0];
  },
  estimateDetails: async (filters, enabled, params, all = false) => {
    const response = await PreApprovedPlanService.estimate(filters, enabled, params);
    console.log("resss",response)
    return response;
  },
  estimateDetailsWithParams: async (filters, params, all=false) => {
    const response = await PreApprovedPlanService.estimate(filters, params); 
    return response;
  },
  NOCDetails: async (tenantId, params) => {
    const response = await OBPSService.NOCSearch(tenantId, params);
    return response?.Noc;
  },
  BPADetails: async (tenantId, params) => {
    const response = await OBPSService.BPASearch(tenantId, params);
    return response?.BPA;
  }
} 
