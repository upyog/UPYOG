import {UploadFile,Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
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
    selectedVendor, 
    setSelectedVendor, 
    vendorDescription, 
    vehicleDescription,
    selectVehicle,
    setSelectVehicle 
  }) => {
  return {
    label: {
      heading: `RS_${action?.action}`,
      submit: `RS_${action?.action}`,
      cancel: "CS_COMMON_BACK",
    },
    form: [
      {
        body: [
          action?.state === "PENDING_FOR_VEHICLE_DRIVER_ASSIGN" ? (
          {
            label: t("RS_ASSIGN"),
            type: "dropdown",
            isMandatory: true,
            populators:( 
            <Dropdown 
            option={vendorDescription} // Pass the array of objects
            t={t}
            optionKey="i18nKey"
            select={setSelectedVendor} 
            selected={selectedVendor}
            />
          ),
          }
        ) : "null",

          action?.state === "DELIVERY_PENDING" ? (
          {
            label: t("RS_REGISTRATION_NUMBER"),
            isMandatory: true,
            type: "dropdown",
            populators:( 
            <Dropdown 
            option={vehicleDescription} // Pass the array of objects
            t={t}
            optionKey="i18nKey"
            select={setSelectVehicle} 
            selected={selectVehicle}
            />
          ),
          }  ) : "null",
          
          {
            label: t("ES_RS_ACTION_COMMENTS") + " *",
            type: "textarea",
            populators: {
              name: "comments",
              validation: {
                required: true,
              },
            },
          },
          
          {
            label: `${t("ES_RS_ATTACH_FILE")}${action.docUploadRequired ? " *" : ""}`,
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
