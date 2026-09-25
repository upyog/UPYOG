import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const useTLDocumentSearch = (data1 = {}, config = {}) => {
  const client = useQueryClient();
  const tenant = Digit.ULBService.getStateId();

  let filesArray = window.location.href.includes("/tl/tradelicence/application/")
    ? data1?.value?.tradeLicenseDetail?.applicationDocuments.map((ob) => ob?.fileStoreId)
    : [];
  if (data1?.value?.workflowDocs) filesArray = data1?.value?.workflowDocs?.map((ob) => ob?.fileStoreId);
  if (data1?.value?.owners?.documents["OwnerPhotoProof"]?.fileStoreId) filesArray.push(data1.value.owners.documents["OwnerPhotoProof"].fileStoreId);
  if (data1?.value?.owners?.documents["ProofOfIdentity"]?.fileStoreId) filesArray.push(data1.value.owners.documents["ProofOfIdentity"].fileStoreId);
  if (data1?.value?.owners?.documents["ProofOfOwnership"]?.fileStoreId) filesArray.push(data1.value.owners.documents["ProofOfOwnership"].fileStoreId);

  const { isLoading, error, data } = queryTemplate({ queryKey: [`tlDocuments-${1}`, filesArray], queryFn: () => Digit.UploadServices.Filefetch(filesArray, tenant) });
  return { isLoading, error, data: { pdfFiles: data?.data }, revalidate: () => client.invalidateQueries({ queryKey: [`tlDocuments-${1}`, filesArray] }) };
};

export default useTLDocumentSearch;
