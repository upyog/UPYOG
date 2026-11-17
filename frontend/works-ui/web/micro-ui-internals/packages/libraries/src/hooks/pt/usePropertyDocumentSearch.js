import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const usePropertyDocumentSearch = ({ property }, config = {}) => {
  const client = useQueryClient();
  const tenantId = property?.tenantId || Digit.ULBService.getCurrentTenantId();
  const tenant = Digit.ULBService.getStateId();
  const propertyId = property?.propertyId;
  const filesArray = property?.documents?.map((value) => value?.fileStoreId);
  const { isLoading, error, data } = queryTemplate({ queryKey: [`ptDocuments-${propertyId}`, filesArray], queryFn: () => Digit.UploadServices.Filefetch(filesArray, tenant) });
  return { isLoading, error, data: { pdfFiles: data?.data }, revalidate: () => client.invalidateQueries({ queryKey: [`ptDocuments-${propertyId}`, filesArray] }) };
};

export default usePropertyDocumentSearch;
