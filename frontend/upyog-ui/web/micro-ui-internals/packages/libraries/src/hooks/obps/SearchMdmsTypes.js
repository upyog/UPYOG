import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const createQuery = (tenantId, key, moduleName, masterDetails, select, config = {}) =>
  queryTemplate({
    queryKey: [key, tenantId],
    queryFn: () =>
      MdmsService.getDataByCriteria(
        tenantId,
        {
          details: {
            tenantId,
            moduleDetails: [
              {
                moduleName,
                masterDetails,
              },
            ],
          },
        },
        moduleName
      ),
    select,
    config,
  });

const SearchMdmsTypes = {
  useApplicationTypes: (tenantId, config = {}) =>
    createQuery(
      tenantId,
      "BPA_APPLICATION_TYPE",
      "BPA",
      [{ name: "ApplicationType" }],
      (data) =>
        data?.BPA?.ApplicationType?.map((type) => ({
          code: type.code,
          i18nKey: `WF_BPA_${type.code}`,
        })),
      config
    ),

  useServiceTypes: (tenantId, config = {}) =>
    createQuery(
      tenantId,
      "BPA_SERVICE_TYPE",
      "BPA",
      [{ name: "ServiceType" }],
      (data) =>
        data?.BPA?.ServiceType?.map((type) => ({
          code: type.code,
          i18nKey: `BPA_SERVICETYPE_${type.code}`,
          applicationType: type.applicationType,
        })),
      config
    ),

  useBPAREGServiceTypes: (tenantId, config = {}) =>
    createQuery(
      tenantId,
      "BPAREG_SERVICE_TYPE",
      "StakeholderRegistraition",
      [{ name: "TradeTypetoRoleMapping" }],
      (data) =>
        data?.StakeholderRegistraition?.TradeTypetoRoleMapping?.map((type) => ({
          code: type.tradeType?.split(".")[0],
          i18nKey: `TRADELICENSE_TRADETYPE_${type.tradeType?.split(".")[0]}`,
        })),
      config
    ),

  useBPAServiceTypes: (tenantId, config = {}) =>
    createQuery(
      tenantId,
      "BPA_ROLE_BASED_SERVICE_TYPE",
      "BPA",
      [{ name: "BPAAppicationMapping" }],
      (data) => {
        const userInfo = JSON.parse(
          sessionStorage.getItem("Digit.citizen.userRequestObject") || "{}"
        )?.value?.info;

        return data?.BPA?.BPAAppicationMapping?.filter((item) =>
          item.roles?.some((role) =>
            userInfo?.roles?.some((r) => r.code === role)
          )
        ).map((type) => ({
          code: type.code,
          i18nKey: `BPA_SERVICETYPE_${type.code}`,
          applicationType: type.applicationType,
        }));
      },
      config
    ),

  getFormConfig: (tenantId, config = {}) =>
    createQuery(
      tenantId,
      "BPA_FORM_CONFIG",
      "BPA",
      [
        { name: "BuildingPermitConfig" },
        { name: "EdcrConfig" },
        { name: "InspectionReportConfig" },
        { name: "OCBuildingPermitConfig" },
        { name: "OCEdcrConfig" },
        { name: "StakeholderConfig" },
      ],
      (d) => d.BPA,
      config
    ),
};

export default SearchMdmsTypes;