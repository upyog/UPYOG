import { queryTemplate } from "../../common/queryTemplate";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";
import { MdmsService } from "../../services/elements/MDMS";

const useMDMS = {
  applicationTypes: (tenantId) =>
    queryTemplate({
      queryKey: [tenantId, "TL_MDMS_APPLICATION_STATUS"],
      queryFn: () =>
        MdmsServiceV2.getDataByCriteria(
          tenantId,
          {
            details: {
              tenantId,
              moduleDetails: [{ moduleName: "TradeLicense", masterDetails: [{ name: "ApplicationType" }] }],
            },
          },
          "TL"
        ),
      select: (data) =>
        data.TradeLicense.ApplicationType?.map((type) => ({
          code: type.code.split(".")[1],
          i18nKey: `TL_APPLICATIONTYPE.${type.code.split(".")[1]}`,
        })),
    }),

  getFormConfig: (tenantId, config) =>
    queryTemplate({
      queryKey: [tenantId, "FORM_CONFIG"],
      queryFn: () =>
        MdmsService.getDataByCriteria(
          tenantId,
          {
            details: {
              tenantId,
              moduleDetails: [{ moduleName: "TradeLicense", masterDetails: [{ name: "CommonFieldsConfig" }] }],
            },
          },
          "TL"
        ),
      select: (d) => d.TradeLicense.CommonFieldsConfig,
      config,
    }),
};

export default useMDMS;
