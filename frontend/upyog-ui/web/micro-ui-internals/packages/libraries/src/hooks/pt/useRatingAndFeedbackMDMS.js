import React from "react";
import { useQuery } from "react-query";
import { MdmsService } from "../../services/elements/MDMS";
import { MdmsServiceV2 } from "../../services/elements/MDMSV2";

const useRatingAndFeedbackMDMS = {
    RatingAndFeedBack: (tenantId) =>
    useQuery(
      [tenantId, "PT_MDMS_RATING_AND_FEEDBACK_VALUES"],
      () =>
      MdmsServicV2.getDataByCriteria(
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
      {
        select: (data) =>  data?.["common-masters"]?.RatingAndFeedback?.reduce((obj, item) => (obj[item.type] = item.value, obj) ,{}),
      }
    ),
};

export default useRatingAndFeedbackMDMS;