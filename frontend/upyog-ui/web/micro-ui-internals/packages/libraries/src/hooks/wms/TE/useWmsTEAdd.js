import { useMutation } from "react-query";
import WMSService from "../../../services/elements/WMS";

const useWmsTEAdd = () => {
        return useMutation((data) =>{
            console.log("Tender entry add ", data)
            return WMSService.TenderEntry.create(data)
        });
};

export default useWmsTEAdd;
