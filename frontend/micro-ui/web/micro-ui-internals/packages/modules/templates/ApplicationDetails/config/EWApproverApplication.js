import { DatePicker, UploadFile,TextInput } from "@upyog/digit-ui-react-components";
import React, { act } from "react";

export const configEWApproverApplication = ({
  t,
  action,
  approvers,
  selectedDate,
  setSelectedDate,
  selectFile,
  uploadedFile,
  setUploadedFile,
  assigneeLabel,
  businessService,
}) => {
  console.log("action",action)
  return {
    label: {
      heading: `WF_${action?.action}_APPLICATION`,
      submit: `WF_${businessService}_${action?.action}`,
      cancel: "ES_PTR_COMMON_CANCEL",
    },
    form: [
      {
        body: [
          action?.state === "COMPLETIONPENDING" ? (
          {
            label: t("EW_PICKUP_DATE"),
            type: "date",
            isMandatory: true,
            populators: { 
              name: "date",
            
              component: (
                <DatePicker
                  date={selectedDate}
                  onChange={setSelectedDate}
                />
              ),
              }    
          }) : "null",
          action?.state === "REQUESTCOMPLETED" ? (

          {
            label: t("ES_EW_ACTION_TRANSACTION_ID"),
            type: "text",
            isMandatory: true,
            populators: {
              name: "transactionId",
            },
          }): "null",
          action?.state === "REQUESTCOMPLETED" ? (

          {
            label: t("ES_EW_ACTION_FINALAMOUNT"),
            type: "text",
            isMandatory: true,
            populators: {
              name: "finalAmount",
            },
          }): "null",
          {
            label: t("ES_EW_ACTION_COMMENTS"),
            type: "textarea",
            populators: {
              name: "comments",
            },
          },
      
          {
            label: `${t("ES_EW_TEXT")}${action.docUploadRequired ? " *" : ""}`,
            populators: (
              <UploadFile
                id={"workflow-doc"}
                onUpload={selectFile}
                onDelete={() => {
                  setUploadedFile(null);
                }}
                showHint={true}
                hintText={t("PTR_ATTACH_RESTRICTIONS_SIZE")}
                message={uploadedFile ? `1 ${t(`ES_PTR_ACTION_FILEUPLOADED`)}` : t(`ES_PTR_ACTION_NO_FILEUPLOADED`)}
              />
            ),
          },
        ],
      },
    ],
  };
};
