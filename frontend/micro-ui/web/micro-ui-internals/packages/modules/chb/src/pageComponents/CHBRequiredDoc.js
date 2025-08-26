import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { stringReplaceAll } from "../utils";

/**
 * CHBRequiredDoc Component
 * 
 * This component is responsible for displaying the required documents information page for booking an advertisement in the CHB module.
 * It provides details about the required documents, upload restrictions, and document requirements.
 * 
 * Props:
 * - `t`: Translation function for internationalization.
 * - `config`: Configuration object for the form step.
 * - `onSelect`: Callback function triggered when the form step is completed.
 * - `userType`: Type of the user (e.g., employee or citizen).
 * - `formData`: Existing form data to prefill the fields.
 * 
 * Variables:
 * - `tenantId`: The current tenant ID fetched using the Digit ULB Service.
 * - `stateId`: The state ID fetched using the Digit ULB Service.
 * - `Documentsob`: The fetched document metadata from MDMS for the CHB module.
 * - `docs`: The list of required documents extracted from the fetched metadata.
 * 
 * Hooks:
 * - `Digit.Hooks.chb.useChbDocumentsMDMS`: Custom hook to fetch the list of required documents from MDMS.
 * 
 * Logic:
 * - Removes the `docReqScreenByBack` session storage item to ensure a fresh state for the document requirements screen.
 * - Fetches the list of required documents from MDMS based on the state ID and module name.
 * - Displays the required document details, upload restrictions, and additional information using translated text.
 * 
 * Functions:
 * - `onSave`: Placeholder function for saving data (currently empty).
 * - `goNext`: Triggers the `onSelect` callback to proceed to the next step.
 * 
 * Returns:
 * - A React fragment containing a card component that displays the required document details, upload restrictions, and additional information.
 */
const CHBRequiredDoc = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");
  
 
  const { isLoading, data: Documentsob = {}  } = Digit.Hooks.chb.useChbDocumentsMDMS(stateId, "CHB", "Documents");

  let docs = Documentsob?.CHB?.Documents;
  function onSave() {}
  function goNext() {
    onSelect();
  }

  return (
    <React.Fragment>
      <Card>
      <CardHeader>{t("MODULE_CHB")}</CardHeader>
        <div>
         <CardText className={"primaryColor"}>{t("CHB_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("CHB_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("CHB_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
          <CardSubHeader>{t("CHB_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className={"primaryColor"}>{t("CHB_DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t('CHB_UPLOAD_RESTRICTIONS_TYPES')}</CardText>
          <CardText className={"primaryColor"}>{t('CHB_UPLOAD_RESTRICTIONS_SIZE')}</CardText>
          <div>
            {isLoading && <Loader />}
            {Array.isArray(docs)
              ? 
                docs.map(({ code, dropdownData }, index) => ( 
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t("CHB_" + stringReplaceAll(code, ".", "_"))}
                      </CardSubHeader>
                      {dropdownData.map((dropdownData, dropdownIndex) => (
                        <CardText className={"primaryColor"}>
                          {`${dropdownIndex + 1}`}. {t("CHB_" + stringReplaceAll(dropdownData?.code, ".", "_"))}
                        </CardText>
                      ))}
                    </div>
                  ))
              : null}
          </div>
        </div>
        <span>
          <SubmitBar label={t("CHB_COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default CHBRequiredDoc;