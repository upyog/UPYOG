import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useWSConfigMDMS = {
  WSCreateConfig: (tenantId, config) =>
    queryTemplate({
      queryKey: [tenantId, "FORM_WS_ACTIVATION_CONFIG"],
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
                      name: "WSCreateConfig",
                    },
                  ],
                },
              ],
            },
          },
          "WS"
        ),
      select: (d) => d["ws-services-masters"].WSCreateConfig, config }),

  WSActivationConfig: (tenantId, config) =>
    queryTemplate({
      queryKey: [tenantId, "FORM_WS_ACTIVATION_CONFIG"],
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
                      name: "WSActivationConfig",
                    },
                  ],
                },
              ],
            },
          },
          "WS"
        ),
      select: (d) => d["ws-services-masters"].WSActivationConfig, config }),

  WSDisconnectionConfig: (tenantId, config) =>
    queryTemplate({
      queryKey: [tenantId, "FORM_WS_ACTIVATION_CONFIG"],
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
                      name: "WSDisconnectionConfig",
                    },
                  ],
                },
              ],
            },
          },
          "WS"
        ),
      select: (d) => d["ws-services-masters"].WSDisconnectionConfig, config }),
  
    getFormConfig: (tenantId, config) =>
    queryTemplate({
      queryKey: [tenantId, "FORM_CONFIG"],
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
                      "name": "WSCreateConfig"
                    },
                    {
                      "name": "WSActivationConfig"
                    },
                    {
                      "name": "WSDisconnectionConfig"
                    }
                  ],
                },
              ],
            },
          },
          "ws-services-masters"
        ),
      select: (d) => d?.["ws-services-masters"], config }),
};

export default useWSConfigMDMS;
