import { useQuery } from "@tanstack/react-query";
import { useQueryClient } from "../../common/queryClientTemplate";

const usePropertySearchWithDue = ({ tenantId, filters, auth = true, configs }) => {
  const client = useQueryClient();

  const getOwnerNames = (propertyData) => {
    const getActiveOwners = propertyData?.owners?.filter(owner => owner?.active);
    const getOwnersList = getActiveOwners?.map(activeOwner => activeOwner?.name)?.join(",");
    return getOwnersList;
  };
  /**
   * Transforms the property search response into a UI-friendly format.
   *
   * This select function runs after the API response is received and before
   * the data is stored in React Query cache. It prepares additional data
   * required by the UI:
   * - Extracts active property IDs as consumer codes for fetching bills.
   * - Filters inactive units and owners.
   * - Creates a formatted property map for faster lookup.
   *
   * Keeping this transformation inside select avoids repeating data processing
   * logic in multiple components and keeps the cached data consistent.
   */
  const defaultSelect = (data) => {
    if (!data || !Array.isArray(data.Properties)) {
      return { Properties: [], ConsumerCodes: [], FormattedData: {} };
    }
    let consumerCodes = [];
    let formattedData = {};
    data.Properties.forEach((property) => {
      if (!property) return;
      // Only active properties should be used to fetch payment dues.
      property.status == "ACTIVE" && consumerCodes.push(property.propertyId);
      // Keep only active units because inactive units should not be shown.
      property.units = property?.units?.filter((unit) => unit?.active) || [];
      property.owners = property?.owners?.filter((owner) =>
        (owner?.status === property?.status) === "INWORKFLOW" && property?.creationReason === "MUTATION" ? "INACTIVE" : "ACTIVE"
      ) || [];
      // Store property details in a map using propertyId as key.
      // This allows quick access while updating dues after bill API response.
      formattedData[property.propertyId] = {
        propertyId: property?.propertyId,
        name: property?.owners?.[0]?.name,
        status: property?.status,
        due: false,
        locality: property?.tenantId && property?.address?.locality?.code
          ? `${property.tenantId.replace(".", "_").toUpperCase()}_REVENUE_${property.address.locality.code}`
          : "",
        owners: property?.owners || [],
        documents: property?.documents || [],
        ownerNames: getOwnerNames(property)
      };
    });
    data["ConsumerCodes"] = consumerCodes;
    data["FormattedData"] = formattedData;
    return data;
  };
  /**
   * First query:
   * Searches properties based on provided filters.
   *
   * The result is transformed using defaultSelect to prepare consumer codes
   * required for the next bill details query.
   */
  const { isLoading, error, data, isSuccess: isPropertySuccess } = useQuery({
    ...configs,
    queryKey: ["propertySearchList", tenantId, filters, auth],
    queryFn: async () => {
      const response = await Digit.PTService.search({ tenantId, filters, auth });
      return response || { Properties: [] };
    },
    select: defaultSelect,
  });

  let consumerCodes = data?.ConsumerCodes?.join(",") || "";
  /**
   * Second query:
   * Fetches pending payment details for the properties returned by the first query.
   *
   * This query depends on the property search response, therefore it is enabled
   * only when property data and consumer codes are available.
   */
  const { isLoading: billLoading, data: billData, isSuccess } = useQuery({
    ...configs,
    queryKey: ["propertySearchBillList", tenantId, filters, data, auth, consumerCodes],
    queryFn: async () => {
      if (!consumerCodes) {
        return { Bill: [] };
      }
      const response = await Digit.PTService.fetchPaymentDetails({ tenantId, consumerCodes, auth });
      return response || { Bill: [] };
    },
    // Prevents unnecessary bill API calls when there are no properties to fetch dues for.
    enabled: (configs?.enabled ?? true) && !!data && !!consumerCodes,
    select: (billResp) => {
      if (!billResp || !billResp.Bill) {
        if (data) {
          data["Bill"] = {};
        }
        return billResp || { Bill: [] };
      }
      if (data) {
        data["Bill"] =
          billResp.Bill.reduce((curr, acc) => {
            if (!acc || !acc.consumerCode) return curr;
            curr[acc.consumerCode] = acc.totalAmount;
            if (data.FormattedData && data.FormattedData[acc.consumerCode]) {
              data.FormattedData[acc.consumerCode]["due"] = acc.totalAmount;
            }
            return curr;
          }, {}) || {};
      }
      return billResp;
    },
  });

  return {
    isLoading: isLoading || billLoading,
    error,
    data,
    billData,
    isSuccess: isPropertySuccess && (consumerCodes ? isSuccess : true),
    revalidate: () => {
      client.invalidateQueries({ queryKey: ["propertySearchBillList", tenantId, filters] });
      client.invalidateQueries({ queryKey: ["propertySearchList", tenantId, filters] });
    },
  };
};

export default usePropertySearchWithDue;

