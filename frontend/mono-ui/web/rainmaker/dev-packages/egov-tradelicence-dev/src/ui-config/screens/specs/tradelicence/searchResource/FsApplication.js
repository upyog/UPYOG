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
import { searchApiCall, filestoreid } from "./functions";

export const fsApplication = getCommonCard(
  {
    subHeader: getCommonTitle({
      labelName: "File Store",
      labelKey: "File Store",
    }),
    subParagraph: getCommonParagraph({
      labelName: "Provide at least File Store ID",
      labelKey: "Provide at least File Store ID",
    }),
    appTradeAndMobNumContainer: getCommonContainer({
      applicationNo: getTextField({
        label: {
          labelName: "Receipt No.",
          labelKey: "Receipt No",
        },
        placeholder: {
          labelName: "Enter Receipt No.",
          labelKey: "Enter Receipt No",
        },
        gridDefination: {
          xs: 12,
          sm: 4,
        },
        required: false,
        pattern: /^[a-zA-Z0-9-/]*$/i,
        errorMessage: "ERR_INVALID_APPLICATION_NO",
        jsonPath: "searchScreen.applicationNumber",
      }),

          
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
              labelName: "Submit",
              labelKey: "Submit",
            }),
          },
          onClickDefination: {
            action: "condition",
            callBack: filestoreid,
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
