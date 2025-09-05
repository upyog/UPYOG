import { PTService } from "../../services/elements/PT";
import { useMutation, useQuery, useQueryClient } from "react-query";

const usePropertyAssessmentSearch = ({ tenantId, filters, auth,searchedFrom="" }, config = {}) => {
    const client = useQueryClient();
    const args = tenantId ? { tenantId, filters, auth } : { filters, auth };
    const defaultSelect = (data) => {
      if(data.Properties.length > 0)  data.Properties[0].units = data.Properties[0].units || [];
      if(data.Properties.length > 0)  data.Properties[0].units = data.Properties[0].units.filter((unit) => unit.active);
      if(data.Properties.length > 0)  data.Properties[0].owners = data.Properties[0].owners || [];
      if(searchedFrom=="myPropertyCitizen"){
        data.Properties.map(property=>{
          property.owners =property.owners.filter((owner) => owner.status ===(property.creationReason=="MUTATION" ?"INACTIVE": "ACTIVE"));
        })
      }  
      return data;
    };
    const { isLoading, error, data, isSuccess } = useQuery(["propertyAssessmentSearchList", tenantId, filters], () => Digit.PTService.assessmentSearch(args));
    
      return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["propertyAssessmentSearchList", tenantId, filters, auth]) };
};


export default usePropertyAssessmentSearch;
