import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const mdmsV2Enabled = true;
let mdmsRef = null;

const getMDMSServiceRef = () => {
        if (mdmsV2Enabled) {
            mdmsRef = MdmsServiceV2;
        } else {
            mdmsRef = MdmsService;
        }
        return mdmsRef;
}

const useProductPriceMDMS = (tenantId, moduleCode, type, config = {}) => {
   const useProductPrices = () => {
    return useQuery("PRODUCT_PRICES", () => getMDMSServiceRef().getMasterData(tenantId, moduleCode, "ProductName"), config);
  };

  switch (type) {
    case "ProductName":
      return useProductPrices();
    default:
      return null;
  }
};

export default useProductPriceMDMS;
