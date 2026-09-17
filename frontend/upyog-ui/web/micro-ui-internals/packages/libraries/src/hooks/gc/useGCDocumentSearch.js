import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";

const useGCDocumentSearch = ({ application }, config = {}, Code) => {
  const client = useQueryClient();

  const tenant = Digit.ULBService.getStateId();
  const applicationNo = application?.applicationNo;

  const documents = config?.value?.documents?.documents || config?.value;

  const filteredDocs = Array.isArray(documents) ? documents.filter((doc) => doc?.documentType === Code) : [];

  const filesArray = filteredDocs.map((d) => d?.fileStoreId);

  const queryKey = [ "GC_DOCUMENTS", applicationNo, JSON.stringify(filesArray) ];

  const queryFn = () => Digit.UploadServices.Filefetch(filesArray, tenant);

  const query = queryTemplate({ queryKey, queryFn, config });

  return {
    ...query,
    data: { pdfFiles: query?.data?.data },
    revalidate: () => client.invalidateQueries({ queryKey })};
};

export default useGCDocumentSearch;