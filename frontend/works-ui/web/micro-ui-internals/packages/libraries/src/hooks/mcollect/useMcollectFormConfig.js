import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useMcollectFormConfig = {
  getFormConfig: (tenantId, config = {}) =>
    queryTemplate({
      queryKey: ["MCOLLECT_FORM_CONFIG", tenantId],
      queryFn: () =>
        MdmsService.getDataByCriteria(
          tenantId,
          {
            details: {
              tenantId,
              moduleDetails: [
                {
                  moduleName: "mCollect",
                  masterDetails: [{ name: "CreateFieldsConfig" }],
                },
              ],
            },
          },
          "mCollect"
        ),
      select: (d) => d.mCollect.CreateFieldsConfig,
      config,
    }),
};

export default useMcollectFormConfig;