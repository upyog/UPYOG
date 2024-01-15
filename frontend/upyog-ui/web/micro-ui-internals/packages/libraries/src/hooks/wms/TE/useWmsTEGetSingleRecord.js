import { useQuery } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsTEGetSingleRecord = (tenantId, id) => {
    console.log("useWmsTEGetSingleRecord ",id)
    return useQuery(["WMS_TENDER_SINGLE_RECORD",id], () => WMSService.TenderEntry.getSingle(id));
};

export default useWmsTEGetSingleRecord;
