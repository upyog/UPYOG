// Developed for the Purpose of Popup which will render when you click on Deposit direct to the centre
import { Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
export const LocationDetails = ({ t }) => {

  return {
    form: [
      {
        body: [
            {
                label: t("CND_PINCODE"),
                isMandatory: true,
                type: "text",
                populators: {
                  name: "comments",
                  validation: {
                    required: true,
                  },
                },
              },
           {
              label: t("CND_LOCALITY"),
              type: "dropdown",
              populators: (
                <Dropdown
                  option={""}
                  t={t}
                  optionKey="name"
                  select={""}
                  selected={""}
                />
              ),
            }
        ],
      },
    ],
  };
};
