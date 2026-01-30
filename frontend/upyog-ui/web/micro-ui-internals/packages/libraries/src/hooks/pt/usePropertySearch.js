import { useQuery, useQueryClient } from "react-query";

const usePropertySearch = (
  { tenantId, filters, auth, searchedFrom = "" },
  config = {}
) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if (!data?.Properties?.length) return data;
  
    return {
      ...data,
      Properties: data.Properties.map((property) => ({
        ...property,
        units: Array.isArray(property.units)
          ? property.units.filter((unit) => unit?.active !== false)
          : [],
        owners:
          searchedFrom === "myPropertyCitizen"
            ? (property.owners || []).filter(
                (owner) =>
                  owner.status ===
                  (property.creationReason === "MUTATION" ? "INACTIVE" : "ACTIVE")
              )
            : property.owners || []
      }))
    };
  };
  

  const { isLoading, error, data, isSuccess } = useQuery(
    ["propertySearchList", tenantId, filters, auth, searchedFrom],
    () => Digit.PTService.search(args),
    {
      select: defaultSelect,
      ...config,
    }
  );

  return {
    isLoading,
    error,
    data,
    isSuccess,
    revalidate: () =>
      client.invalidateQueries([
        "propertySearchList",
        tenantId,
        filters,
        auth,
        searchedFrom,
      ]),
  };
};

export default usePropertySearch;
