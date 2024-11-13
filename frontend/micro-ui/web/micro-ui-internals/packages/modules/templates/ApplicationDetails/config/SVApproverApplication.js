/**
 * custom component which will show the Small card so that user can fill their comment and take action
 * Returning the Header ad lable of the card as well as the comment box
 * 
 */
import { Dropdown, UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
export const configSVApproverApplication = ({t,action,selectFile,uploadedFile,setUploadedFile,approvers,selectedApprover,setSelectedApprover,assigneeLabel}) => {
    return {
      label: {
        heading: `WF_${action?.action}_APPLICATION`,
        submit: `SV_${action?.action}`,
        cancel: "SV_COMMON_CANCEL",
      },
      form: [
        {
          body: [  
            {
              label: t("SV_ASSIGNEE"),
              type: "dropdown",
              populators:(
                <Dropdown
                  option={approvers}
                  t={t}
                  optionKey="name"
                  select={setSelectedApprover}
                  selected={selectedApprover}
                />
              )
            },
            {
              label: t("SV_ACTION_COMMENTS"),
              type: "textarea",
              populators: {
                name: "comments",
              },
            },
            {
              label: `${t("SV_ATTATCH_FILE")}${action.docUploadRequired ? " *" : ""}`,
              populators: (
                <UploadFile
                  id={"workflow-doc-sv"}
                  onUpload={selectFile}
                  onDelete={() => {
                    setUploadedFile(null);
                  }}
                  // showHint={true}
                  // hintText={t("PT_ATTACH_RESTRICTIONS_SIZE")}
                  message={uploadedFile ? `1 ${t(`SV_FILE_UPLOADED`)}` : t(`SV_NO_FILE`)}
                />
              ),
            }
          ],
        },
      ],
    };
  };
  