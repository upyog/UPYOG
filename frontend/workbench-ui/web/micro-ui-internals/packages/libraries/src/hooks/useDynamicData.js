// useQuery import  but not used
// endOfToday, start import  but also not used

import { format, subMonths } from "date-fns";

const useDynamicData = ({moduleCode ,tenantId, filters, t }) => {


    switch(moduleCode){

        default:
            return {isLoading: false, error: false, data: null, isSuccess: false};
    }
    
  };

export default useDynamicData;