import { queryTemplate } from "../common/queryTemplate";
import { getLocalities } from "../services/molecules/getLocalities";
import { LocalityService } from "../services/elements/Localities";

const useLocalities = (tenant, boundaryType = "admin", config, t) => {
  boundaryType = boundaryType.toLocaleLowerCase();
  return queryTemplate({
    queryKey: ["BOUNDARY_DATA", tenant, boundaryType],
    queryFn: () => getLocalities[boundaryType](tenant),
    select: (data) => {
      return LocalityService?.get(data).map((key) => {
        return { ...key, i18nkey: t(key.i18nkey) };
      });
    },
    config: { staleTime: Infinity, ...config },
  });
};

export default useLocalities;
