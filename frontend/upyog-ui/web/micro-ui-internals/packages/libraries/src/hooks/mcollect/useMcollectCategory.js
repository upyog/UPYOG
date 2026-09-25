import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useMCollectCategory = (tenantId, filter, config = {}) => {
  const queryKey = [
    "MCOLLECT_CATEGORY",
    tenantId,
    JSON.stringify(filter),
  ];

  const select = (data) => {
    const Categories = [];

    data?.MdmsRes?.BillingService?.BusinessService?.forEach((ob) => {
      const exists = Categories.some(
        (el) => el?.code.split(".")[0] === ob.code.split(".")[0]
      );

      if (!exists) {
        Categories.push({
          ...ob,
          i18nkey: `BILLINGSERVICE_BUSINESSSERVICE_${ob.code
            .split(".")[0]
            .toUpperCase()}`,
        });
      }
    });

    return {
      Categories,
      data,
    };
  };

  return queryTemplate({
    queryKey,
    queryFn: () => MdmsService.getPaymentRules(tenantId, filter),
    select,
    config,
  });
};

export default useMCollectCategory;