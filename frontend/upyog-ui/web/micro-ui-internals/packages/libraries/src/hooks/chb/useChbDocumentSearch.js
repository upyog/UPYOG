import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";

/**
 * useChbDocumentSearch Hook
 * 
 * This custom hook is responsible for fetching documents associated with a CHB (Community Hall Booking) application.
 * 
 * Parameters:
 * - `application`: The application object containing details like `tenantId` and `bookingId`.
 * - `config`: Configuration object containing document details.
 * - `Code`: The document type code to filter documents.
 * - `index`: Index of the document in the list (optional).
 * 
 * Logic:
 * - Extracts documents matching the specified `Code` from the `config` object.
 * - Fetches file URLs for the filtered documents using `Digit.UploadServices.Filefetch`.
 * - Uses the `useQuery` hook from `react-query` to cache and manage the fetched data.
 * 
 * Returns:
 * - An object containing:
 *    - `isLoading`: Boolean indicating whether the query is in progress.
 *    - `error`: Error object if the query fails.
 *    - `data`: Object containing the fetched PDF files.
 *    - `revalidate`: Function to invalidate and refetch the query.
 */

const useChbDocumentSearch = ({ application }, config = {}, Code) => {
  const client = useQueryClient();

  const tenantId =
    application?.tenantId || Digit.ULBService.getCurrentTenantId();
  const tenant = Digit.ULBService.getStateId();
  const bookingId = application?.bookingId;

  const documents =
    config?.value?.documents?.documents || config?.value;

  const filteredDocs =
    documents?.filter((doc) => doc?.documentType === Code) || [];

  const filesArray = filteredDocs.map((d) => d?.fileStoreId);

  const queryKey = [
    "CHB_DOCUMENTS",
    bookingId,
    JSON.stringify(filesArray),
  ];

  const queryFn = () =>
    Digit.UploadServices.Filefetch(filesArray, tenant);

  const query = queryTemplate({
    queryKey,
    queryFn,
    config,
  });

  return {
    ...query,
    data: { pdfFiles: query?.data?.data },
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useChbDocumentSearch;