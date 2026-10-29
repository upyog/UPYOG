import { MdmsService, getGeneralCriteria } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";
import { queryTemplate } from "../../common/queryTemplate";

const useMDMS = (tenantId, moduleCode, type, config = { }, payload = []) => {
  const useFinancialYears = () => {
    return queryTemplate({ queryKey: ["PT_FINANCIAL_YEARLS"], queryFn: () => MdmsServiceV2.getDataByCriteria(tenantId, payload, moduleCode) });
  };
  const useCommonFieldsConfig = () => {
    return queryTemplate({ queryKey: ["COMMON_FIELDS"], queryFn: () => MdmsService.getCommonFieldsConfig(tenantId, moduleCode, type, payload) });
  };

  const usePropertyTaxDocuments = () => {
    return queryTemplate({ queryKey: ["PT_PROPERTY_TAX_DOCUMENTS"], queryFn: () => MdmsServiceV2.getDataByCriteria(tenantId, payload, moduleCode) });
  };

  /*const useGenderDetails = () => {
    return useQuery("PT_GENDER_DETAILS", () => MdmsService.getGenderTypeDetails(tenantId, type, filter), config);
  };*/

  switch (type) {
    case "FINANCIAL_YEARLS":
      return useFinancialYears();
    case "PROPERTY_TAX_DOCUMENTS":
      return usePropertyTaxDocuments();
    case "CommonFieldsConfig":
      return useCommonFieldsConfig();
    /*case "GenderType":
      return useGenderDetails();*/

    default:
      return queryTemplate({ queryKey: [type], queryFn: () => MdmsServiceV2.getDataByCriteria(tenantId, getGeneralCriteria(tenantId, moduleCode, type), moduleCode), config });
  }
};

export default useMDMS;
