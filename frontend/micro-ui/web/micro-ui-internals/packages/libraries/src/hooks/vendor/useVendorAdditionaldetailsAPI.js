import { useQuery, useMutation } from "react-query";
import { VendorService } from "../../services/elements/EmpVendor";



const useVendorAdditionaldetailsAPI = (tenantId) => {
  console.log("data in api hook cakll :: ", tenantId);
  // return useQuery(["EMP_VENDOR_ADDITIONAL_DETAILS_CREATE", filters], () => VendorService.createVendorAdditionaldetails(tenantId, filters), config);

  return useMutation((data) => VendorService.createVendorAdditionaldetails(data, tenantId));


};

export default useVendorAdditionaldetailsAPI;