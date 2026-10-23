import { queryTemplate } from "../common/queryTemplate"; // queryTemplate use karo
import { getLocalities } from "../services/molecules/getLocalities";
import { LocalityService } from "../services/elements/Localities";

const useLocalities = (tenant, boundaryType = "admin", config, t) => {
  return queryTemplate({
    queryKey: ["BOUNDARY_DATA", tenant, boundaryType],
    queryFn: () => getLocalities[boundaryType.toLowerCase()](tenant),
    config: {
      select: (data) => {
        return LocalityService.get(data).map((key) => {
          return { ...key, i18nkey: t(key.i18nkey) };
        });
      },
      staleTime: Infinity,
      ...config,
    },
  });
};

export default useLocalities;