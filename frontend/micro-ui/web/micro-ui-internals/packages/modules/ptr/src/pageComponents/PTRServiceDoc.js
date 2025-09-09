/**
 * PTRServiceDoc Component
 * 
 * Description:
 * This component displays a list of required documents for the Pet Service module.
 * It fetches the document data using a custom hook (`Digit.Hooks.ptr.usePetMDMS`) 
 * and renders the list conditionally based on whether the `isMutation` flag is true or false.
 * 
 * Key Functionalities:
 * - Displays document headers and subheaders with multilingual support using the `t()` function.
 * - Fetches the document list dynamically from the MDMS service based on the current state and tenant ID.
 * - Renders a loader while fetching data and handles both mutation and non-mutation states differently.
 * - Displays document details either as a single line or as individual items based on the mutation flag.
 * - Provides a "Next" button for navigation to the next screen.
 * 
 * Props:
 * - `t`: Translation function for multilingual support.
 * - `config`: Configuration object that controls mutation status.
 * - `onSelect`: Function to navigate to the next screen.
 * - `userType`: Specifies the user type (not used in this component, but passed as a prop).
 * - `formData`: Contains form data (not used in this component, but passed as a prop).
 * 
 * Dependencies:
 * - Digit UI React components (`Card`, `CardHeader`, `CardText`, `Loader`, `SubmitBar`)
 * - Digit MDMS hooks for fetching document data.
 * - Utility functions (`cardBodyStyle`, `stringReplaceAll`)
 * 
 */

import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { cardBodyStyle, stringReplaceAll } from "../utils";
//import { map } from "lodash-es";

const PTRServiceDoc = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");

  
 
  const { isLoading, data: Documentsob = {} } = Digit.Hooks.ptr.usePetMDMS(stateId, "PetService", "Documents");
  
  let docs = Documentsob?.PetService?.Documents;
  function onSave() {}

  function goNext() {
    onSelect();
  }

  return (
    <React.Fragment>
      <Card>
        <CardHeader>{!config.isMutation ? t("PTR_DOC_REQ_SCREEN_HEADER") : t("PT_REQIURED_DOC_TRANSFER_OWNERSHIP")}</CardHeader>
        <div>
          <CardText className={"primaryColor"}>{t("PTR_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("PTR_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("PTR_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
          <CardSubHeader>{t("PTR_DOC_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className={"primaryColor"}>{t("PTR_DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
          <CardText style={{color: 'red'}}>{t('PTR_PDF_AND_JPG_BOTH_FORMAT_ACCEPTED_IN_DOCUMENT_UPLOAD')}</CardText>

          <div>
            {isLoading && <Loader />}
            {Array.isArray(docs)
              ? config?.isMutation
                ? docs.map(({ code, dropdownData }, index) => ( 
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t(code)}
                      </CardSubHeader>
                      <CardText className={"primaryColor"}>{dropdownData.map((dropdownData) => t(dropdownData?.code)).join(", ")}</CardText>
                    </div>
                  ))
                : docs.map(({ code, dropdownData }, index) => ( 
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t(stringReplaceAll(code, ".", "_"))}
                      </CardSubHeader>
                      {dropdownData.map((dropdownData, dropdownIndex) => (
                        <CardText className={"primaryColor"}>
                          {`${dropdownIndex + 1}`}. {t(stringReplaceAll(dropdownData?.code, ".", "_"))}
                        </CardText>
                      ))}
                    </div>
                  ))
              : null}
          </div>
        </div>
        <span>
          <SubmitBar label={t("PTR_COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default PTRServiceDoc;