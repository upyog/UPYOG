/*import {
  getCommonHeader,
  getBreak,
  getCommonTitle,
  getCommonParagraph,
  getCommonContainer
} from "egov-ui-framework/ui-config/screens/specs/utils";
import { getCommonGrayCard, getLabelOnlyValue } from "../../utils";
import { footer } from "./footer";
import { getTransformedLocale } from "egov-ui-framework/ui-utils/commons";

const styles = {
  header: {
    color: "gba(0, 0, 0, 0.87)",
    fontFamily: "Roboto",
    fontSize: "20px",
    fontWeight: 500,
    lineHeight: "28px",
    paddingLeft: "5px"
  },
  subHeader: {
    color: "gba(0, 0, 0, 0.87)",
    fontFamily: "Roboto",
    fontSize: "16px",
    fontWeight: 400,
    lineHeight: "19px"
  },
  docs: {
    color: "rgba(0, 0, 0, 0.6)",
    fontFamily: "Roboto",
    fontSize: "14px",
    fontWeight: 400,
    lineHeight: "17px",
    paddingBottom: "24px"
  },
  description: {
    fontFamily: "Roboto",
    color: "rgba(0, 0, 0, 0.87)",
    fontSize: "12px",
    fontWeight: 400,
    letterSpacing: "0.6px",
    lineHeight: "14px"
  }
};

const header = getCommonHeader(
  {
    labelName: "Required Documents-Fire NOC",
    labelKey: "NOC_REQ_DOCS_HEADER"
  },
  {
    style: styles.header
  }
);

const generateDocument = item => {
  // Add header to individual grey cards
  let subHeader =
    item.code &&
    getCommonTitle(
      {
        labelKey: getTransformedLocale(`NOC_${item.code}_HEADING`)
      },
      {
        style: styles.subHeader
      }
    );

  // Add documents in individual grey cards
  let docs = {};
  if (item.hasOwnProperty("dropdownData")) {
    docs = item.dropdownData.reduce((obj, doc) => {
      obj[doc.code] = getLabelOnlyValue(
        {
          labelKey: getTransformedLocale(`NOC_${doc.code}_LABEL`)
        },
        {
          style: styles.docs
        }
      );
      return obj;
    }, {});
  } else if (item.hasOwnProperty("options")) {
    docs = item.options.reduce((obj, doc) => {
      obj[doc.code] = getLabelOnlyValue(
        {
          labelKey: getTransformedLocale(`NOC_${doc.code}_LABEL`)
        },
        {
          style: styles.docs
        }
      );
      return obj;
    }, {});
  }

  // Add description to individual grey cards
  let subParagraph = item.description
    ? getCommonParagraph(
        {
          labelKey: getTransformedLocale(`NOC_${item.description}_NOTE`)
        },
        {
          style: styles.description
        }
      )
    : {};

  return getCommonGrayCard({
    subHeader: subHeader,
    break: getBreak(),
    docs: getCommonContainer({ ...docs }),
    subParagraph: subParagraph
  });
};

export const getRequiredDocuments = documents => {
  let doc = documents.map(item => {
    return generateDocument(item);
  });
  return getCommonContainer(
    {
      header: {
        uiFramework: "custom-atoms",
        componentPath: "Container",
        children: {
          header
        }
      },
      documents: {
        uiFramework: "custom-atoms",
        componentPath: "Container",
        children: {
          ...doc
        },
        props: {
          id: "documents-div"
        }
      },
      footer: {
        uiFramework: "custom-atoms",
        componentPath: "Container",
        children: {
          footer
        }
      }
    },
    {
      style: {
       // paddingBottom: 75
      }
    }
  );
};
 */

/* This Component is developed to show the Document required screen, in the above code, Document parameter have the data, and  It maps over each item in the documents array.
b. For each item, it calls generateDocument to create a UI component.
c. These components are then combined into a larger container structure.  

generateDocument function creates a "card" for each document type.


*/



import React from "react";
import { Card, CardSubHeader, CardText, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { stringReplaceAll } from "../utils";

/* This component is designed to flexibly display document requirements for a Fire NOC application, 
handling different data structures (both dropdownData and options) which come from the MDMS.
It provides a list of required documents, with each main document type (like "OWNER.IDENTITYPROOF")
as a header, followed by the specific document options under it.
For each document, it creates a CardSubHeader with the document's code.
It then checks if dropdownData exists and is an array. If so, it renders those items.
It also checks if options exists and is an array. If so, it renders those items too.
*/

const FNOCServiceDoc = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");

  const docType = "Documents";
  
  const { isLoading, data: Documentsob = {} } = Digit.Hooks.fnoc.useFnocDoc(stateId, "FireNoc", docType);
  let docs = Documentsob?.FireNoc?.Documents;

  function onSave() {}

  function goNext() {
    onSelect();
  }

/* This function takes an array of items (either from dropdownData or options) 
because 2 arrys are there in MDMS level and returns an array of CardText components, each representing a document option.*/

  const renderDocumentItems = (items) => {
    return items.map((item, index) => (
      <CardText key={index} className={"primaryColor"}>
        {`${index + 1}`}. {t(stringReplaceAll(item?.code, ".", "_"))}
      </CardText>
    ));
  };

  return (
    <React.Fragment>
      <Card>
        <div>
          <CardSubHeader>{t("FNOC_DOC_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText style={{color: 'red'}}>{t('FNOC_DOCUMENT_ACCEPTED_PDF_JPG_PNG')}</CardText>

          <div>
            {isLoading && <Loader />}
            {!isLoading && docs && Array.isArray(docs) ? (
              docs.map((doc, index) => ( 
                <div key={index}>
                  <CardSubHeader>
                    {index + 1}. {t(stringReplaceAll(doc.code, ".", "_"))}
                  </CardSubHeader>
                  {doc.dropdownData && Array.isArray(doc.dropdownData) && renderDocumentItems(doc.dropdownData)}
                  {doc.options && Array.isArray(doc.options) && renderDocumentItems(doc.options)}
                </div>
              ))
            ) : null}
          </div>
        </div>
        <span>
          <SubmitBar label={t("COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default FNOCServiceDoc;