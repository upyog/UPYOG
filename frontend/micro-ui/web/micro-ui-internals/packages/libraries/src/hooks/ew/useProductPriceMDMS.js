import { useQuery } from "react-query";

const useProductPriceMDMS = (tenantId, moduleCode, type, config = {}) => {
   const useProductPrices = () => {
    return useQuery("PRODUCT_PRICES", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "ProductName"), config);
  };

  switch (type) {
    case "ProductName":
      return useProductPrices();
    default:
      return null;
  }
};

export default useProductPriceMDMS;
