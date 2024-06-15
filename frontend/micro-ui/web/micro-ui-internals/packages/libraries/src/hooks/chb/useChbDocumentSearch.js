import { useQuery, useQueryClient } from "react-query";

const useChbDocumentSearch = ({ chbdetail }, config = {}) => {
  const client = useQueryClient();
  const tenantId = chbdetail?.tenantId || Digit.ULBService.getCurrentTenantId();
  const tenant = Digit.ULBService.getStateId();
  const applicationNumber = chbdetail?.propertyId;
  const filesArray = chbdetail?.documents?.map((value) => value?.filestoreId);
  const { isLoading, error, data } = useQuery([`chbDocuments-${applicationNumber}`, filesArray], () => Digit.UploadServices.Filefetch(filesArray, tenant));
  return { isLoading, error, data: { pdfFiles: data?.data }, revalidate: () => client.invalidateQueries([`chbDocuments-${applicationNumber}`, filesArray]) };
};

export default useChbDocumentSearch;
