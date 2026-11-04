import { Dropdown, UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";

// modal config for employee workflow actions in inbox and application overview pages. 
// This config is used to render the modal for workflow actions like approve, reject, send back etc.
//  It takes the action as a prop and renders the appropriate fields based on the action. It also handles the file upload functionality for the workflow actions.
export const ModalConfig = ({
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
  let checkCondtions = true;
  if (action?.action == "SENDBACKTOCITIZEN" || action?.action == "APPROVE" || action?.action == "REJECT" || action?.action == "SENDBACK")
    checkCondtions = false;
  if (action.isTerminateState) checkCondtions = false;


  return {
    label: {
      heading: ``,
      submit: `${action?.action}`,
      cancel: "WF_EMPLOYEE_NEWTL_CANCEL",
    },
    form: [
      {
        body: [
          {
            label: !checkCondtions ? null : `${t("WF_ASSIGNEE_NAME_LABEL")} *`,
            placeholder: !checkCondtions ? null : t("WF_ASSIGNEE_NAME_PLACEHOLDER"),
            type: "dropdown",
            populators: !checkCondtions ? null : (
              <Dropdown
                option={approvers}
                autoComplete="off"
                optionKey="name"
                id="fieldInspector"
                select={setSelectedApprover}
                selected={selectedApprover}
              />
            ),
          },
          {
            label: `${t("CS_COMMON_COMMENTS")} *`,
            type: "textarea",
            populators: {
              name: "comments",
            },
          },
          {
            label: t("TL_APPROVAL_CHECKLIST_BUTTON_UP_FILE"),
            populators: (
              <UploadFile
                id={"workflow-doc"}
                // accept=".jpg"
                onUpload={selectFile}
                onDelete={() => {
                  setUploadedFile(null);
                }}
                message={uploadedFile ? `1 ${t(`ES_PT_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
              />
            ),
          },
        ],
      },
    ],
  };
};
