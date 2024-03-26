import { useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsTESearch = (tenantId, filters, config) => {
        return useMutation((data) =>{
            console.log("Tender Search Data s data ", data)
            return WMSService.TenderEntry.search(data)
        });
};

export default useWmsTESearch;
