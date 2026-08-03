import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

const useOBPSDocumentSearch = (
  { application },
  config = {},
  Code,
  index,
  isNOC = false
) => {
  const client = useQueryClient();

  const tenant =
    Digit.ULBService.getStateId();

  const filesArray =
    config?.value?.documents?.documents
      ?.filter((doc) => doc.documentType === Code)
      ?.map((d) => d.fileStoreId) || [];

  const queryKey = ["OBPS_DOCS", JSON.stringify(filesArray)];

  const query = queryTemplate({
    queryKey,
    queryFn: () =>
      Digit.UploadServices.Filefetch(filesArray, tenant),
  });

  return {
    ...query,
    data: { pdfFiles: query?.data?.data },
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export default useOBPSDocumentSearch;