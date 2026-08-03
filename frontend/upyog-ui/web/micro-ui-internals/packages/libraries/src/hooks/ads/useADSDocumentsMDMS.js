import { queryTemplate } from "../../common/queryTemplate";

const useADSDocumentsMDMS = (tenantId, moduleCode, type, config = {}) => {
  const queryKey = [
    "ADS_MDMS_DOCUMENTS",
    tenantId,
    moduleCode,
    type,
  ];

  const queryFn = () => Digit.Hooks.useSelectedMDMS(moduleCode).getMasterData( tenantId, moduleCode, type );

  return queryTemplate({ queryKey, queryFn, config });
};

export default useADSDocumentsMDMS;