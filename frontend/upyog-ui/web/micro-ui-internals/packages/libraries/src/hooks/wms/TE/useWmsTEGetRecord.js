import { useQuery } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsTEGetRecord = (tenantId) => {
    return useQuery(["WMS_TENDER_RECORD",tenantId], () => WMSService.TenderEntry.get());
};

export default useWmsTEGetRecord;
