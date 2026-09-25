import { queryTemplate } from "../../common/queryTemplate";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useRatingAndFeedbackMDMS = {
    RatingAndFeedBack: (tenantId) =>
    queryTemplate({
      queryKey: [tenantId, "PT_MDMS_RATING_AND_FEEDBACK_VALUES"],
      queryFn: () =>
      MdmsServiceV2.getDataByCriteria(
          tenantId,
          {
            details: {
              tenantId: tenantId,
              moduleDetails: [
                {
                  moduleName: "common-masters",
                  masterDetails: [
                    {
                        name : "RatingAndFeedback",
                    }
                  ],
                },
              ],
            },
          },
          "PT"
        ),
      select: (data) =>  data?.["common-masters"]?.RatingAndFeedback?.reduce((obj, item) => (obj[item.type] = item.value, obj) ,{}),
    }),
};

export default useRatingAndFeedbackMDMS;