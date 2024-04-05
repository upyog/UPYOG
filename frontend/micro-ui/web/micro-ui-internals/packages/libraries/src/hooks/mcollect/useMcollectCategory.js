import { useQuery } from "react-query";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useMCollectCategory = (tenantId, filter, config = {}) => {
  const { data } = useQuery("MCOLLECT_CATEGORY_SERVICE", () => MdmsServiceV2.getPaymentRules(tenantId, filter), config);
  let Categories = [];
  data?.MdmsRes?.BillingService?.BusinessService.map((ob) => {
    let found = Categories.length > 0 ? Categories?.some((el) => el?.code.split(".")[0] === ob.code.split(".")[0]) : false;
    if (!found) Categories.push({ ...ob, i18nkey: `BILLINGSERVICE_BUSINESSSERVICE_${ob.code.split(".")[0].toUpperCase()}` });
  });

  return { Categories, data };
};

export default useMCollectCategory;
