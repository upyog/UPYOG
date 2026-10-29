import { queryTemplate } from "../common/queryTemplate";
import { TLService } from "./../services/elements/TL";
import { MCollectService } from "./../services/elements/MCollect";
import { PGRService } from "../services/elements/PGR";
import { WSService } from "../services/elements/WS";
import { PTService } from "../services/elements/PT";
import { OBPSService } from "../services/elements/OBPS";
import { format, subMonths } from "date-fns";

const useDynamicData = ({moduleCode ,tenantId, filters, t }) => {
    const useTLDynamicData = () => {
        const { isLoading, error, data, isSuccess } =  queryTemplate({ queryKey: ['TL_OPEN_SEARCH', tenantId, filters], queryFn: async () => await TLService.TLOpensearch({ tenantId, filters }), config: {select: (data) => {
            const tlData = {
                dynamicDataOne : data?.applicationsIssued === 0 || data?.applicationsIssued === null ? null : data?.applicationsIssued + " " + t("APPLICATION_ISSUED_IN_LAST_12_MONTHS"),
                dynamicDataTwo : data?.applicationsRenewed === 0 || data?.applicationsRenewed === null ? null : data?.applicationsRenewed + " " + t("APPLICATION_RENEWED_IN_LAST_12_MONTHS"),
                staticData : data?.applicationValidity === 0 || data?.applicationValidity === null ? null :  data?.applicationValidity + " " + (data?.applicationValidity === 1 ? t("COMMON_YEAR") : t("COMMON_YEARS"))
            }
            return tlData;
        }}});
        return { isLoading, error, data, isSuccess };
    }

    const useMCOLLECTDynamicData = () => {
        const { isLoading, error, data, isSuccess } =  queryTemplate({ queryKey: ['MCOLLECT_OPEN_SEARCH', tenantId, filters], queryFn: async () => await MCollectService.MCollectOpenSearch({ tenantId, filters }), config: {select: (data) => {
            const mCollectData = {
                dynamicDataOne : data?.countOfServices === 0 || data?.countOfServices === null ? null : data?.countOfServices + " "+ t("SERVICE_CATEGORIES_OF_CHALLANS_PROCESSED_IN") + " " + t(tenantId),
                dynamicDataTwo : data?.totalAmountCollected === 0 || data?.totalAmountCollected === null ? null : `₹ ${data?.totalAmountCollected}` + " " +  t("COLLECTED_IN_FORM_OF_CHALLANS_IN_LAST_12_MONTHS"),
                staticData : data?.challanValidity === 0 || data?.challanValidity === null ? null :  data?.challanValidity + " " + (data?.challanValidity === 1 ? t("COMMON_DAY") : t("COMMON_DAYS"))
            }
            return mCollectData;
        }}});
        return { isLoading, error, data, isSuccess };
    }

    const usePGRDynamicData = () => {
        const { isLoading, error, data, isSuccess } =  queryTemplate({ queryKey: ['PGR_OPEN_SEARCH', tenantId, filters], queryFn: async () => await PGRService.PGROpensearch({ tenantId, filters }), config: {select: (data) => {
            const pgrData = {
                dynamicDataOne : data?.complaintsResolved === 0 || data?.complaintsResolved === null ? null : data?.complaintsResolved + " " + t("COMPLAINTS_RESOLVED_IN_LAST_30_DAYS"),
                dynamicDataTwo : data?.averageResolutionTime === 0 || data?.averageResolutionTime === null ? null : data?.averageResolutionTime + " " + (data?.averageResolutionTime === 1 ? t("COMMON_DAY") : t("COMMON_DAYS")) + " " + t("IS_AVG_COMPLAINT_RESOLUTION_TIME"),
                staticData : data?.complaintTypes === 0 || data?.complaintTypes === null ? null : data?.complaintTypes
            }
            return pgrData;
        }}});
        return { isLoading, error, data, isSuccess };
    }
    const usePTDynamicData = () => {
        const fromDate = format(subMonths(new Date(), 12), 'yyyy-MM-dd').toString();
        const toDate = format(new Date(),'yyyy-MM-dd').toString();
        let filter1 = {
            fromDate: Digit.Utils.pt.convertDateToEpoch(fromDate),
            toDate: Digit.Utils.pt.convertDateToEpoch(toDate),
            isRequestForCount : true
        }

        let filter2 = {...filter1, status: "ACTIVE"}
        const { isLoading : isPTPropLoading, error: ptPropError, data: ptPropData, isSuccess : ptPropSuccess  } =  queryTemplate({ queryKey: ['PT_OPEN_SEARCH_REG_PROP', tenantId, filter1], queryFn: async () => await PTService.PTOpenSearch({ tenantId, filters: filter1 }), config: {select: (data) => {
            const ptDataOne = {
                dynamicDataOne : data?.count === 0 || data?.count === null ? null : data?.count + " " + t("PROPERTIES_REGISTERED_IN_LAST_12_MONTHS"),
            }
            return ptDataOne;
        }}});

        const { isLoading : isPTAppLoading, error: ptAppError, data: ptAppData, isSuccess : ptAppSuccess  } =  queryTemplate({ queryKey: ['PT_OPEN_SEARCH_APP_PROCESS', tenantId, filter2], queryFn: async () => await PTService.PTOpenSearch({ tenantId, filters: filter2 }), config: {select: (data) => {
            const ptDataTwo = {
                dynamicDataTwo : data?.count === 0 || data?.count === null ? null : data?.count + " " + t("APPLICATION_PROCESSED_IN_LAST_12_MONTHS"),
            }
            return ptDataTwo;
        }}});
        return { isLoading : isPTPropLoading || isPTAppLoading, error : ptAppError || ptPropError, data : {...ptPropData, ...ptAppData}, isSuccess: ptPropSuccess && ptAppSuccess };
    }

    const useWSDynamicData = () => {
        const { isLoading, error, data, isSuccess } =  queryTemplate({ queryKey: ['WS_OPEN_SEARCH_DSS', tenantId], queryFn: async () => await WSService.WSOpensearch({
            module: "WS",
            tenantId: tenantId
        }), config: {select: (data) => {
            const wsData = {
                dynamicDataOne : data?.wstotalCollection === 0 || data?.wstotalCollection === null ? null : `₹ ${data?.wstotalCollection}` + " " +  t("PAID_IN_LAST_12_MONTHS_TOWARDS_WS_CHARGES"),
                dynamicDataTwo : data?.wstotalConnection === 0 || data?.wstotalConnection === null ? null : data?.wstotalConnection + " " + t("ACTIVE_CONNECTIONS_PRESENT_IN") + " " + t(tenantId),
            }
            return wsData;
        }}});
        return { isLoading, error, data, isSuccess };
    }

    const useBPADynamicData = () => {
        const { isLoading, error, data, isSuccess } =  queryTemplate({ queryKey: ['BPA_OPEN_SEARCH_DSS', tenantId], queryFn: async () => await OBPSService.BPAOpensearch({
            module: "BPA",
            tenantId: tenantId
        }), config: {select: (data) => {
            const obpsData = {
                dynamicDataOne : data?.bpaTotalPermitsIssued === 0 || data?.bpaTotalPermitsIssued === null ? null : ` ${data?.bpaTotalPermitsIssued}` + " " +  t("PERMITS_ISSUED_IN_LAST_12_MONTHS"),
                dynamicDataTwo : data?.bpaTotalPlansScrutinized === 0 || data?.bpaTotalPlansScrutinized === null ? null : data?.bpaTotalPlansScrutinized + " " + t("BUILING_PLANS_SCRUTINISED_IN_LAST_12_MONTHS"),
            }
            return obpsData;
        }}});
        return { isLoading, error, data, isSuccess };
    }

    switch(moduleCode){
        case 'TL':
            return useTLDynamicData();
        case 'MCOLLECT':
            return useMCOLLECTDynamicData();
        case 'PGR':
            return usePGRDynamicData();
        case 'WS':                                         
            return useWSDynamicData();
        case 'PT':
            return usePTDynamicData();
        case 'OBPS':                                         
            return useBPADynamicData();
        default:
            return {isLoading: false, error: false, data: null, isSuccess: false};
    }
    
  };

export default useDynamicData;