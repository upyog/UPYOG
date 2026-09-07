import { Dropdown, UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";

export const configFireNocApproverApplication = ({
  t,
  action,
  selectFile,
  uploadedFile,
  setUploadedFile,
  businessService,
}) => {
  return {
    label: {
      heading: `WF_${action?.action}_APPLICATION`,
      submit: `WF_${businessService}_${action?.action}`,
      cancel: "ES_FN_COMMON_CANCEL",
    },
    form: [
      {
        body: [  
          {
            label: t("ES_FN_ACTION_COMMENTS"),
            type: "textarea",
            populators: {
              name: "comments",
            },
          }
        ],
      },
    ],
  };
};
