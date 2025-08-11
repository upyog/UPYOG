import { useQuery, useQueryClient } from "react-query";

const useChbDocumentSearch = ({ application }, config = {}, Code, index) => {
  const client = useQueryClient();
  const tenantId = application?.tenantId || Digit.ULBService.getCurrentTenantId();
  const tenant = Digit.ULBService.getStateId();
  const bookingId = application?.bookingId;
  let newDocs = [];
  const documents = config?.value?.documents?.documents || config?.value;
  documents?.filter(doc => doc?.documentType === Code).forEach((ob) => {
    newDocs.push(ob);
  });
  const filesArray = newDocs.map((value) => value?.fileStoreId);
  const { isLoading, error, data } = useQuery([`chbDocuments-${bookingId}`, filesArray], () => Digit.UploadServices.Filefetch(filesArray, tenant));
  return { isLoading, error, data: { pdfFiles: data?.data }, revalidate: () => client.invalidateQueries([`chbDocuments-${bookingId}`, filesArray]) };
  
};
export default useChbDocumentSearch;
