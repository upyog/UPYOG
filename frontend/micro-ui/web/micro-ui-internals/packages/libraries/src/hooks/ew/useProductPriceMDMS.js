import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useProductPriceMDMS = (tenantId, moduleCode, type, config = {}) => {
   const useProductPrices = () => {
    return useQuery("PRODUCT_PRICES", () => MdmsServiceV2.getMasterData(tenantId, moduleCode, "ProductName"), config);
  };

  switch (type) {
    case "ProductName":
      return useProductPrices();
    default:
      return null;
  }
};

export default useProductPriceMDMS;
