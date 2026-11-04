import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const usePetDocumentSearch = ({ petdetail }, config = {}) => {
  const client = useQueryClient();
  const tenant = Digit.ULBService.getStateId();
  const applicationNumber = petdetail?.propertyId;
  const filesArray = petdetail?.documents?.map((value) => value?.filestoreId);
  const { isLoading, error, data } = queryTemplate({ queryKey: [`ptDocuments-${applicationNumber}`, filesArray], queryFn: () => Digit.UploadServices.Filefetch(filesArray, tenant) });
  return { isLoading, error, data: { pdfFiles: data?.data }, revalidate: () => client.invalidateQueries({ queryKey: [`ptDocuments-${applicationNumber}`, filesArray] }) };
};

export default usePetDocumentSearch;
