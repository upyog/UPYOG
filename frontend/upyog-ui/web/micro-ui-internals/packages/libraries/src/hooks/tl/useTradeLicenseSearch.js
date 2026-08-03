import { useEffect } from "react";
import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const useTradeLicenseSearch = ({ tenantId, filters, auth }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };
  const { isLoading, error, data } = queryTemplate({ queryKey: ["tradeSearchList", tenantId, filters], queryFn: () => Digit.TLService.TLsearch(args), config });

  useEffect(() => {
    if (config?.filters?.tenantId)
      Digit.LocalizationService.getLocale({ modules: [`rainmaker-${config.filters?.tenantId}`], locale: Digit.StoreData.getCurrentLanguage(), tenantId: `${config?.filters?.tenantId}` });
  }, [config?.filters?.tenantId]);

  return { isLoading, error, data, revalidate: () => client.invalidateQueries({ queryKey: ["tradeSearchList", tenantId, filters] }) };
};

export default useTradeLicenseSearch;
