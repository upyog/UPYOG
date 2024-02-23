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
import { searchApiCall, ENAKSHA } from "./functions";

export const Obps = getCommonCard(
  {
    subHeader: getCommonTitle({
      labelName: "Online Building Plan",
      labelKey: "Online Building Plan",
    }),
  

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
            sm: 4,
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
              labelName: "Click",
              labelKey: "Click",
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
