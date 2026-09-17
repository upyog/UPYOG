import { queryTemplate } from "../common/queryTemplate";
import { getLocalities } from "../services/molecules/getLocalities";
import { LocalityService } from "../services/elements/Localities";

//TODO : as per discusion with team, we will change the default value which is admin from boundaryType from useLocalities hook from pgr and use this hook instead. This is because this hook is generic and can be used in any module. The useLocalities hook in pgr is specific to pgr module and cannot be used in other modules. Hence we are creating a generic useLocalities hook which can be used in any module.

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
