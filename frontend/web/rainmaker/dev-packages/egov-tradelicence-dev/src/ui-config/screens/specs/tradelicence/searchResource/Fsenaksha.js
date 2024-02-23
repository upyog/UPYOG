import {
  getCommonCard,
  getCommonTitle,
  getTextField,
  getSelectField,
  getCommonContainer,
  getCommonParagraph,
  getPattern,
  getDateField,
  getLabel,
} from "egov-ui-framework/ui-config/screens/specs/utils";
import { ENAKSHA } from "./functions";

export const Fsenaksha = getCommonCard(
  {
    subHeader: getCommonTitle({
      labelName: "ENAKSHA",
      labelKey: "ENAKSHA",
    }),
    subParagraph: getCommonParagraph({
      labelName: "ENAKSHA",
      labelKey: "ENAKSHA",
    }),
    // appTradeAndMobNumContainer: getCommonContainer({
    //   applicationNo: getTextField({
    //     label: {
    //       labelName: "Receipt No.",
    //       labelKey: "Receipt No",
    //     },
    //     placeholder: {
    //       labelName: "Enter Receipt No.",
    //       labelKey: "Enter Receipt No",
    //     },
    //     gridDefination: {
    //       xs: 12,
    //       sm: 4,
    //     },
    //     required: false,
    //     pattern: /^[a-zA-Z0-9-]*$/i,
    //     errorMessage: "ERR_INVALID_APPLICATION_NO",
    //     jsonPath: "searchScreen.applicationNumber",
    //   }),

          
    // }),
   

    button: getCommonContainer({
      // firstCont: {

      buttonContainer: getCommonContainer({
        firstCont: {
          uiFramework: "custom-atoms",
          componentPath: "Div",
          gridDefination: {
            xs: 12,
            sm: 4,
          },
        },
        searchButton: {
          componentPath: "Button",
          gridDefination: {
            xs: 12,
            sm: 12,
          },
          props: {
            variant: "contained",
            style: {
              color: "white",

              backgroundColor: "#FE7A51",
              borderRadius: "2px",
              width: "80%",
              height: "48px",
            },
          },
          children: {
            buttonLabel: getLabel({
              labelName: "Submit",
              labelKey: "Submit",
            }),
          },
          onClickDefination: {
            action: "condition",
            callBack: ENAKSHA,
          },
        },
        lastCont: {
          uiFramework: "custom-atoms",
          componentPath: "Div",
          gridDefination: {
            xs: 12,
            sm: 4,
          },
        },
      }),
    }),
  },
  {
    style: {
      overflow: "visible",
    },
  }
);
