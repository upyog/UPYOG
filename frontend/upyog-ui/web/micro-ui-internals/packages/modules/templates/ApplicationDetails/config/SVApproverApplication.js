/**
 * custom component which will show the Small card so that user can fill their comment and take action
 * Returning the Header ad lable of the card as well as the comment box
 * 
 */
import { Dropdown, UploadFile } from "@upyog/digit-ui-react-components";
import React from "react";
export const configSVApproverApplication = ({ t, action, selectFile, uploadedFile, setUploadedFile, approvers, selectedApprover, setSelectedApprover, isUploading }) => {
  const LoadingSpinner = () => (
    <div className="loading-spinner" />
  );
  return {
    label: {
      heading: `WF_${action?.action}_APPLICATION`,
      submit: `SV_${action?.action}`,
      cancel: "SV_COMMON_CANCEL",
    },
    form: [
      {
        body: [
          action?.action === "APPROVE" || action?.action === "REJECT" || action?.action === "SENDBACKTOCITIZEN"
            ? ""
            : {
              label: t("SV_ASSIGNEE"),
              type: "dropdown",
              populators: (
                <Dropdown
                  option={approvers}
                  t={t}
                  optionKey="name"
                  select={setSelectedApprover}
                  selected={selectedApprover}
                />
              ),
            },
          {
            label: t("SV_ACTION_COMMENTS"),
            isMandatory: true,
            type: "textarea",
            populators: {
              name: "comments",
              validation: {
                required: true,
              },
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
                message={isUploading ? (
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <LoadingSpinner />
                    <span>Uploading...</span>
                  </div>
                ) : uploadedFile ? `1 ${t(`SV_FILE_UPLOADED`)}` : t(`SV_NO_FILE`)}
              />
            ),
          }
        ],
      },
    ],
  };
};
