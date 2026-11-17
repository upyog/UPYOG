import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useWSMDMSWS = {
  applicationTypes: (tenantId) =>
    queryTemplate({
      queryKey: [tenantId, "WS_WS_SERVICES_MASTERS"],
      queryFn: () =>
        MdmsService.getDataByCriteria(
          tenantId,
          {
            details: {
              tenantId: tenantId,
              moduleDetails: [
                {
                  moduleName: "ws-services-masters",
                  masterDetails: [
                    {
                      name: "ApplicationType",
                    },
                  ],
                },
              ],
            },
          },
          "ws-services-masters"
        ),
      select: (data) =>
          data["ws-services-masters"].ApplicationType.map((type) => ({
            code: type.code,
            i18nKey: `WS_${type.code}`,
          })),
    }),
};

export default useWSMDMSWS;
