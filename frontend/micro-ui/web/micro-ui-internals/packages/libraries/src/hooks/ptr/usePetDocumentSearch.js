/*
  Custom hook to fetch pet-related document data using react-query.

  Parameters:
  - petdetail: Object containing pet details such as tenant ID, property ID, and document information.
  - config: Optional configuration for react-query.

  Returns:
  - isLoading: Boolean indicating if the query is still loading.
  - error: Error object if the query fails.
  - data: Object containing fetched PDF file data.
  - revalidate: Function to re-fetch the document data.
*/

import { useQuery, useQueryClient } from "react-query";

const usePetDocumentSearch = ({ petdetail }, config = {}) => {
  const client = useQueryClient();
  const tenantId = petdetail?.tenantId || Digit.ULBService.getCurrentTenantId();
  const tenant = Digit.ULBService.getStateId();
  const applicationNumber = petdetail?.propertyId;
  const filesArray = petdetail?.documents?.map((value) => value?.filestoreId);
  const { isLoading, error, data } = useQuery([`ptDocuments-${applicationNumber}`, filesArray], () => Digit.UploadServices.Filefetch(filesArray, tenant));
  return { isLoading, error, data: { pdfFiles: data?.data }, revalidate: () => client.invalidateQueries([`ptDocuments-${applicationNumber}`, filesArray]) };
};

export default usePetDocumentSearch;
