import { ASSETSearch } from "../../services/molecules/ASSET/Search";
import { queryTemplate } from "../../common/queryTemplate";

const useAssetApplicationDetail = (
  t,
  tenantId,
  applicationNo,
  config = {},
  userType,
  args
) => {
  const stateTenantId = Digit.ULBService.getStateId();

  const { data: cityResponseObject } =
    Digit.Hooks.useEnabledMDMS(
      stateTenantId,
      "ASSETV2",
      [{ name: "AssetParentCategoryFields" }],
      {
        select: (data) =>
          data?.["ASSETV2"]?.["AssetParentCategoryFields"],
      }
    );

  const combinedData = cityResponseObject || [];

  const queryKey = [
    "ASSET_APPLICATION_DETAIL",
    tenantId,
    applicationNo,
    userType,
    JSON.stringify(combinedData),
    JSON.stringify(args),
  ];

  const queryFn = () =>
    ASSETSearch.applicationDetails(
      t,
      tenantId,
      applicationNo,
      userType,
      combinedData,
      args
    );

  const select = (data) => ({
    applicationData: data,
    applicationDetails: data?.applicationDetails,
  });

  return queryTemplate({
    queryKey,
    queryFn,
    select,
    config,
  });
};

export default useAssetApplicationDetail;