import { Surveys } from "../../services/elements/Surveys";
import { useQuery } from "react-query";

const useCfdefinitionsearchresult = (filters, config) => {
    console.log(config,"useCfdefinitionsearchresult")
  return useQuery(`useCfdefinitionsearchresult_search_surveys_${new Date()}`, () => Surveys.cfdefinitionsearch(filters), { ...config });
};

export default useCfdefinitionsearchresult;
