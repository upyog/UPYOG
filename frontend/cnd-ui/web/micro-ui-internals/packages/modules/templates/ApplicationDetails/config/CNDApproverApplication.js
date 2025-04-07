/**
 * custom component which will show the Small card so that user can fill their comment and take action or assign any fieldinspector
 * Returning the Header ad lable of the card as well as the comment box
 * 
 */
import { Dropdown, UploadFile } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
export const configCNDApproverApplication = ({ t, action,approvers, selectedApprover, setSelectedApprover }) => {
  return {
    label: {
      heading: `${action?.action}`,
      submit: `SUBMIT`,
      cancel: "CANCEL",
    },
    form: [
      {
        body: [
           {
              label: t("CND_ASSIGNEE"),
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
