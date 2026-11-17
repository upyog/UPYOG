import { queryTemplate } from "../../common/queryTemplate";

const usePropertySearch = (
  { tenantId, filters, auth, searchedFrom = "" },
  config = {}
) => {
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  return queryTemplate({
    queryKey: ["propertySearchList", tenantId, filters, auth, searchedFrom],

    queryFn: () => Digit.PTService.search(args),

    select: (data) => {
      if (!data?.Properties?.length) return data;

      return {
        ...data,
        Properties: data.Properties.map((p) => ({
          ...p,
          units: Array.isArray(p.units)
            ? p.units.filter((u) => u?.active !== false)
            : [],
          owners:
            searchedFrom === "myPropertyCitizen"
              ? (p.owners || []).filter(
                  (o) =>
                    o.status ===
                    (p.creationReason === "MUTATION"
                      ? "INACTIVE"
                      : "ACTIVE")
                )
              : p.owners || [],
        })),
      };
    },

    config,
  });
};

export default usePropertySearch;