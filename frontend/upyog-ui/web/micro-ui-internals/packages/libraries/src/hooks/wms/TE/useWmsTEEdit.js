import { useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsTEEdit = () => {
        return useMutation((data) =>{
            console.log("Edit ",data)
            return WMSService.TenderEntry.update(data)
        });
};

export default useWmsTEEdit;
