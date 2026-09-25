import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const usePropertySearchNew = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if(data.Properties.length > 0)  data.Properties[0].units = data.Properties[0].units || [];
    if(data.Properties.length > 0)  data.Properties[0].units = data.Properties[0].units.filter((unit) => unit.active);
    if(data.Properties.length > 0)  data.Properties[0].owners = data.Properties[0].owners || [];
    if(searchedFrom=="myPropertyCitizen"){
      data.Properties.map(property=>{
        property.owners =property.owners.filter((owner) => owner.status === "ACTIVE");
      })
    }  
    return data;
  };

  const { isLoading, error, data, isSuccess } = queryTemplate({ queryKey: ["propertySearchList", tenantId, filters, auth], queryFn: () => Digit.PTService.search(args), select: defaultSelect, config });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries({ queryKey: ["propertySearchList", tenantId, filters, auth] }) };
};

export default usePropertySearchNew;
