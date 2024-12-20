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
