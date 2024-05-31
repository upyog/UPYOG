import { Dropdown, UploadFile } from "@upyog/digit-ui-react-components";
import React from "react";

export const configASSETApproverApplication = ({
  t,
  action,
  businessService,
}) => {

  
  return {
    label: {
      heading: `WF_${action?.action}_APPLICATION`,
      submit: `WF_${businessService}_${action?.action}`,
      cancel: "ES_AST_COMMON_CANCEL",
    },
    form: [
      {
        body: [  
          {
            label: t("AST_ACTION_COMMENTS"),
            type: "textarea",
            populators: {
              name: "comments",
            },
          },
        ],
      },
    ],
  };
};
