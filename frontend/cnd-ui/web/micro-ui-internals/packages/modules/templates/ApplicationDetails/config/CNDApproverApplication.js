/**
 * custom component which will show the Small card so that user can fill their comment and take action or assign any fieldinspector
 * Returning the Header ad lable of the card as well as the comment box
 * 
 */
import { Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
export const configCNDApproverApplication = ({ t, action,approvers, selectedApprover, setSelectedApprover, vendorDescription,selectedVendor, setSelectedVendor, vehicleDescription,selectVehicle,setSelectVehicle}) => {
  return {
    label: {
      heading: `${action?.action}`,
      submit: `SUBMIT`,
      cancel: "CANCEL",
    },
    form: [
      {
        body: [
          ...(
            action?.state === "PENDING_FOR_VEHICLE_DRIVER_ASSIGN"
              ? [
                  {
                    label: t("CND_VENDOR_ASSIGN"),
                    type: "dropdown",
                    isMandatory: true,
                    populators: (
                      <Dropdown
                        option={vendorDescription}
                        t={t}
                        optionKey="i18nKey"
                        select={setSelectedVendor}
                        selected={selectedVendor}
                      />
                    ),
                  },
                  {
                    label: t("CND_EMP_SCHEDULE_PICKUP"),
                    type: "date",
                    isMandatory: true,
                    populators: {
                      name: "date",
                      min: new Date().toISOString().split("T")[0]
                    },
                  },
                ]
              : action?.state === "WASTE_PICKUP_INPROGRESS"
              ? [
                  {
                    label: t("CND_VEHICLE_ASSIGN"),
                    type: "dropdown",
                    populators: (
                      <Dropdown
                        option={vehicleDescription}
                        t={t}
                        optionKey="i18nKey"
                        select={setSelectVehicle}
                        selected={selectVehicle}
                      />
                    ),
                  },
                ]
              : action?.action==="REJECT"?"":
              [
                  {
                    label: t("CND_ASSIGNEE"),
                    type: "dropdown",
                    isMandatory: true,
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
                ]
          ),
          {
            label: t("CND_COMMENTS"),
            isMandatory: true,
            type: "textarea",
            populators: {
              name: "comments",
              validation: {
                required: true,
              },
            },
          },
        ],
      },
    ],
  };
};
