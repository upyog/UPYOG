import {UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";

/*
  Configuration for the Water Tanker Approver Application Form
  
  This function, `configWTApproverApplication`, is used to return a configuration object 
  for rendering the WT approver form in the application.

  The configuration includes:
  - Dynamic labels for the form's heading, submit, and cancel actions.
  - A comment field where users can provide comments (required).
  - A file upload section that allows users to upload files, with conditional validation 
    depending on whether the action requires a file upload.
*/
export const configWTApproverApplication = ({
  t,
  action,
  selectFile,
  uploadedFile,
  setUploadedFile,
}) => {
  return {
    label: {
      heading: `WT_${action?.action}`,
      submit: `WT_${action?.action}`,
      cancel: "CS_COMMON_BACK",
    },
    form: [
      {
        body: [  
          {
            label: t("ES_WT_ACTION_COMMENTS") + " *",
            type: "textarea",
            populators: {
              name: "comments",
              validation: {
                required: true,
              },
            },
          },
          {
            label: `${t("ES_WT_ATTACH_FILE")}${action.docUploadRequired ? " *" : ""}`,
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
