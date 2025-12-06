import { Dropdown, UploadFile } from "@upyog/digit-ui-react-components";
import React from "react";

/*
  Configuration for the CHB Approver Application Form
  
  This function, `configCHBApproverApplication`, is used to return a configuration object 
  for rendering the CHB approver form in the application.

  The configuration includes:
  - Dynamic labels for the form's heading, submit, and cancel actions.
  - A comment field where users can provide comments (required).
  - A file upload section that allows users to upload files, with conditional validation 
    depending on whether the action requires a file upload.
*/
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
