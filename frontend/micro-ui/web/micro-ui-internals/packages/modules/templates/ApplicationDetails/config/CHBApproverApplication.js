import { Dropdown, UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";

export const configCHBApproverApplication = ({
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
      heading: `CHB_${action?.action}`,
      submit: `CHB_${action?.action}`,
      cancel: "CS_COMMON_BACK",
    },
    form: [
      {
        body: [  
          {
            label: t("ES_CHB_ACTION_COMMENTS") + " *",
            type: "textarea",
            populators: {
              name: "comments",
              validation: {
                required: true,
              },
            },
          },
          {
            label: `${t("ES_CHB_ATTACH_FILE")}${action.docUploadRequired ? " *" : ""}`,
            populators: (
              <UploadFile
                id={"workflow-doc"}
                onUpload={selectFile}
                onDelete={() => {
                  setUploadedFile(null);
                }}
                message={uploadedFile ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
              />
            ),
          },
        ],
      },
    ],
  };
};
