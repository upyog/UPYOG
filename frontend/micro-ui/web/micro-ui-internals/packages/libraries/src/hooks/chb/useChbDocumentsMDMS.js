import { useQuery } from "react-query";

/**
 * useChbDocumentsMDMS Hook
 * 
 * This custom hook is responsible for fetching document-related master data (MDMS) for the CHB (Community Hall Booking) module.
 * 
 * Parameters:
 * - `tenantId`: The tenant ID for which the MDMS data is being fetched.
 * - `moduleCode`: The module code for CHB (e.g., "CHB").
 * - `type`: The type of MDMS data to fetch (e.g., "Documents").
 * - `config`: Optional configuration object for the `useQuery` hook.
 * 
 * Logic:
 * - If `type` is "Documents", calls `useChbDocumentsRequiredScreen` to fetch required document data.
 * - For other types, calls `_default` to fetch multiple types of MDMS data.
 * - Uses the `useQuery` hook from `react-query` to manage caching and fetching of data.
 * 
 * Returns:
 * - A query object from `react-query` containing the fetched MDMS data, loading state, and error state.
 */
const useChbDocumentsMDMS = (tenantId, moduleCode, type, config = {}) => {

  const useChbDocumentsRequiredScreen = () => {
    return useQuery("CHB_DOCUMENT_REQ_SCREEN", () => Digit.Hooks.useSelectedMDMS().getMasterData(tenantId, moduleCode, "Documents"), config);
  };

  const _default = () => {
    return useQuery([tenantId, moduleCode, type], () => Digit.Hooks.useSelectedMDMS().getMultipleTypes(tenantId, moduleCode, type), config);
  };

  switch (type) {
    
    case "Documents":
      return useChbDocumentsRequiredScreen();
    
    default:
      return _default();
  }
};

export default useChbDocumentsMDMS;
