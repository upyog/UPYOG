import { queryTemplate } from "../../common/queryTemplate";

const useNDCDocumentSearch = (data1 = {}, config = {}) => {
  const tenant = Digit.ULBService.getStateId();

  const filesArray =
    data1?.value?.workflowDocs?.map((ob) => ob?.uuid) || [];

  return queryTemplate({
    queryKey: ["ndcDocuments", filesArray],
    queryFn: () => Digit.UploadServices.Filefetch(filesArray, tenant),

    select: (data) => ({
      pdfFiles: data?.data,
    }),

    enabled: filesArray.length > 0,
    config,
  });
};

export default useNDCDocumentSearch;