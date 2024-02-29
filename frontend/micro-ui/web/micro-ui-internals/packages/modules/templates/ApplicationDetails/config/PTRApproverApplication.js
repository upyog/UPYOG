import { Dropdown, UploadFile } from "@upyog/digit-ui-react-components";
import React from "react";

export const configPTRApproverApplication = ({
  t,
  action,
  approvers,
  selectedApprover,
  setSelectedApprover,
  selectFile,
  uploadedFile,
  setUploadedFile,
  assigneeLabel,
  businessService,
}) => {
  return {
    label: {
      heading: `WF_${action?.action}_APPLICATION`,
      submit: `WF_${businessService}_${action?.action}`,
      cancel: "ES_PTR_COMMON_CANCEL",
    },
    form: [
      {
        body: [  
          {
            label: t("ES_PTR_ACTION_COMMENTS"),
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
