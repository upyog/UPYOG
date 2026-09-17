import { UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";

export const configGCApproverApplication = ({ t, action, selectFile, uploadedFile, setUploadedFile, isUploading }) => {
  const LoadingSpinner = () => <div className="loading-spinner" />;
  return {
    label: {
      heading: `GC_${action?.action}`,
      submit: `GC_${action?.action}`,
      cancel: "CS_COMMON_BACK",
    },
    form: [
      {
        body: [
          {
            label: t("ES_GC_ACTION_COMMENTS") + " *",
            type: "textarea",
            populators: {
              name: "comments",
              validation: { required: true },
            },
          },
          {
            label: `${t("ES_GC_ATTACH_FILE")}${action?.docUploadRequired ? " *" : ""}`,
            populators: (
              <UploadFile
                id={"workflow-doc"}
                onUpload={selectFile}
                onDelete={() => setUploadedFile(null)}
                message={isUploading ? (
                  <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <LoadingSpinner />
                    <span>{t("CS_UPLOADING")}</span>
                  </div>
                ) : uploadedFile ? `1 ${t("CS_ACTION_FILEUPLOADED")}` : t("CS_ACTION_NO_FILEUPLOADED")}
              />
            ),
          },
        ],
      },
    ],
  };
};
