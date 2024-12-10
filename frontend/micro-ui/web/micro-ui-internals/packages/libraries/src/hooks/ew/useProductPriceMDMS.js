import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";

const useProductPriceMDMS = (tenantId, moduleCode, type, config = {}) => {
   const useProductPrices = () => {
    return useQuery("PRODUCT_PRICES", () => MdmsService.EWProductPrice(tenantId, moduleCode), config);
  };

  switch (type) {
    case "ProductName":
      return useProductPrices();
    default:
      return null;
  }
};

export default useProductPriceMDMS;
