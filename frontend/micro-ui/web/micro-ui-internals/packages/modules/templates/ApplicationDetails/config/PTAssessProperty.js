import React from "react";
import { Dropdown, RadioButtons } from "@upyog/digit-ui-react-components";

export const configPTAssessProperty = ({ t, action, financialYears, selectedFinancialYear, setSelectedFinancialYear, modeOfPayments, selectedModeofPayment, setSelectedModeofPayment }) => {
  return {
    label: {
      heading: `WF_${action.action}_APPLICATION`,
      submit: `WF_PT.CREATE_${action.action}`,
      cancel: "ES_PT_COMMON_CANCEL",
    },
    form: [
      {
        body: [
          {
            label: t("ES_PT_FINANCIAL_YEARS"),
            isMandatory: true,
            type: "dropdown",
            populators: <Dropdown isMandatory selected={selectedFinancialYear} optionKey="name" option={financialYears} select={setSelectedFinancialYear} t={t} />,
          },
          {
            label: t("ES_PT_MODE_OF_PAYMENTS"),
            isMandatory: true,
            type: "dropdown",
            populators: <Dropdown isMandatory selected={selectedModeofPayment} optionKey="name" option={modeOfPayments} select={setSelectedModeofPayment} t={t} />,
          },
          // {
          //   label: t("ES_PT_FINANCIAL_YEARS"),
          //   isMandatory: true,
          //   type: "radio",
          //   populators: (
          //     <RadioButtons options={financialYears} optionsKey="name" onSelect={setSelectedFinancialYear} selectedOption={selectedFinancialYear} />
          //   ),
          // },
        ],
      },
    ],
  };
};
