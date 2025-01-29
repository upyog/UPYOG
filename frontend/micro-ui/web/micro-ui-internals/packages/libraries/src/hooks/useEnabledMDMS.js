import { useQuery } from "react-query";

const useEnabledMDMS = (tenantId, moduleName, masterDetails = [], config = {}) => {
    return useQuery([tenantId, moduleName, masterDetails], () => Digit.Hooks.useSelectedMDMS().getMultipleTypesWithFilter(tenantId, moduleName, masterDetails), config);
};


export default useEnabledMDMS;