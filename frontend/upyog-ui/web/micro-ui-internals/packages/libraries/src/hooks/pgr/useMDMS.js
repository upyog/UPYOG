import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const usePGRMDMS = {
  ComplainClosingTime: (tenantId, config = {}) =>
    queryTemplate({
      queryKey: ["PGR_COMPLAIN_IDLE_TIME", tenantId],
      queryFn: () =>
        MdmsService.getDataByCriteria(
          tenantId,
          {
            details: {
              tenantId,
              moduleDetails: [
                {
                  moduleName: "RAINMAKER-PGR",
                  masterDetails: [
                    { name: "ComplainClosingTime" },
                  ],
                },
              ],
            },
          },
          "RAINMAKER-PGR"
        ),
      select: (data) =>
        data["RAINMAKER-PGR"]
          ?.ComplainClosingTime?.[0]
          ?.ComplainMaxIdleTime,
      config,
    }),
};

export default usePGRMDMS;