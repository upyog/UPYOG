import { queryTemplate } from "../../common/queryTemplate";

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
const useChbDocumentsMDMS = (
  tenantId,
  moduleCode,
  type,
  config = {}
) => {
  const queryKey = [
    "CHB_MDMS_DOCUMENTS",
    tenantId,
    moduleCode,
    type,
  ];

  const queryFn = () =>
    Digit.Hooks.useSelectedMDMS(moduleCode).getMasterData(
      tenantId,
      moduleCode,
      type
    );

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useChbDocumentsMDMS;