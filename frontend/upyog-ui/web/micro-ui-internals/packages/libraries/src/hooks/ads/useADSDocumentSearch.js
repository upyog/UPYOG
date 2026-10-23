import { useQueryClient } from "@tanstack/react-query";
import { queryTemplate } from "../../common/queryTemplate";

const useADSDocumentSearch = ({ application }, config = {}, Code) => {
  const client = useQueryClient();

  const tenantId = application?.tenantId || Digit.ULBService.getCurrentTenantId();
  const tenant = Digit.ULBService.getStateId();
  const bookingId = application?.bookingId;

  const documents = config?.value?.documents?.documents || config?.value;

  const filteredDocs = documents?.filter((doc) => doc?.documentType === Code) || [];

  const filesArray = filteredDocs.map((d) => d?.fileStoreId);

  const queryKey = [ "ADS_DOCUMENTS", bookingId, JSON.stringify(filesArray) ];

  const queryFn = () => Digit.UploadServices.Filefetch(filesArray, tenant);

  const query = queryTemplate({ queryKey, queryFn, config });

  return {
    ...query,
    data: { pdfFiles: query?.data?.data },
    revalidate: () => client.invalidateQueries({ queryKey })};
};

export default useADSDocumentSearch;