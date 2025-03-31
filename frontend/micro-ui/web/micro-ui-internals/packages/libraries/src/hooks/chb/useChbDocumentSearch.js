import { useQuery, useQueryClient } from "react-query";

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
const useChbDocumentSearch = ({ application }, config = {}, Code, index) => {
  const client = useQueryClient();
  const tenantId = application?.tenantId || Digit.ULBService.getCurrentTenantId();
  const tenant = Digit.ULBService.getStateId();
  const bookingId = application?.bookingId;
  let newDocs = [];
  config?.value?.documents ? config?.value?.documents?.documents.filter(doc => doc?.documentType === Code /* || doc?.documentType?.includes(Code.split(".")[1]) */).map((ob)=>{
    newDocs.push(ob);
  }) : config?.value.filter(doc => doc?.documentType === Code/* || doc?.documentType?.includes(Code.split(".")[1]) */).map((ob)=>{
    newDocs.push(ob);
  })
  const filesArray = newDocs.map((value) => value?.fileStoreId);
  const { isLoading, error, data } = useQuery([`chbDocuments-${bookingId}`, filesArray], () => Digit.UploadServices.Filefetch(filesArray, tenant));
  return { isLoading, error, data: { pdfFiles: data?.data }, revalidate: () => client.invalidateQueries([`chbDocuments-${bookingId}`, filesArray]) };
  
};
export default useChbDocumentSearch;
