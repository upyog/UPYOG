import {
  getCommonGrayCard,
  getCommonSubHeader,
  getLabel
} from "egov-ui-framework/ui-config/screens/specs/utils";
import { changeStep } from "../applyResource/footer";

export const previewSummary = getCommonGrayCard({
  header: {
    uiFramework: "custom-atoms",
    componentPath: "Container",
    props: {
      style: { marginBottom: "10px" }
    },
    children: {
      header: {
        gridDefination: {
          xs: 8
        },
        ...getCommonSubHeader({
          labelName: "Documents",
          labelKey: "BPA_DOCUMENT_AND_NOC_DETAILS_HEADER"
        })
      },
      editSection: {
        componentPath: "Button",
        props: {
          color: "primary",
          style: {
            marginTop: "-10px",
            marginRight: "-18px"
          }
        },
        gridDefination: {
          xs: 4,
          align: "right"
        },
        children: {
          editIcon: {
            uiFramework: "custom-atoms",
            componentPath: "Icon",
            props: {
              iconName: "edit"
            }
          },
          buttonLabel: getLabel({
            labelName: "Edit",
            labelKey: "BPA_SUMMARY_EDIT"
          })
        },
        onClickDefination: {
          action: "condition",
          callBack: (state, dispatch) => {
            changeStep(state, dispatch, "", 3);
          }
        }
      }
    }
  },
  documentDetailsCard: {
    uiFramework: "custom-containers-local",
    moduleName: "egov-bpa",
    componentPath: "PreviewContainer",
    props: {
      sourceJsonPath: "documentDetailsPreview",
      className: "noc-review-documents",
      buttonLabel: {
        labelName: "UPLOAD FILE",
        labelKey: "NOC_DOCUMENT_DETAILS_BUTTON_UPLOAD_FILE"
      },
      inputProps: {
        accept: "image/*, .pdf, .png, .jpeg",
        multiple: false
      },
      maxFileSize: 5000
    }
  }
});
