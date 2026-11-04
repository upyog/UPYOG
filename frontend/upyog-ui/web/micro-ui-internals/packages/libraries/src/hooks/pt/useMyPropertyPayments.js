import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const getOwnerForPayments = (propertyData,data) => {
  let newPayments = [];
  data && data?.Payments?.map((payment) => {
    let owner = propertyData?.filter((ob) => ob.propertyId === payment?.paymentDetails?.[0]?.bill?.consumerCode)[0]?.owners;
    newPayments.push({...payment,owners:owner});
  })
  data ? data["Payments"] = [...newPayments] : "";
  return data;
}

const useMyPropertyPayments = ({ tenantId, filters, searchedFrom="" }, config = {}) => {
  const client = useQueryClient();

  const paymentargs = tenantId ? { tenantId, filters } : { filters };


  const { isLoading, error, data } = queryTemplate({ queryKey: ["paymentpropertySearchList", tenantId, filters], queryFn: () => Digit.PTService.paymentsearch(paymentargs), config });
  const updatedData = getOwnerForPayments(config?.propertyData, data);

return { isLoading, error, data, revalidate: () => client.invalidateQueries({ queryKey: ["paymentpropertySearchList", tenantId, filters] }) };

};

export default useMyPropertyPayments;