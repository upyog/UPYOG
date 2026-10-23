import React, { useState, useCallback } from "react";
import { Card, Header, LabelFieldPair, CardLabel, TextInput, Dropdown, FormComposer, SubmitBar, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { documentsFormConfig } from "../../../config/doc-update";


const Documents = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [canSubmit, setSubmitValve] = useState(false);

  const onFormValueChange = useCallback(
    (setValue, updatedFormData, formState) => {
      if (
        updatedFormData?.documentName &&
        updatedFormData?.docCategory &&
        (updatedFormData?.document.filestoreId || updatedFormData?.document.documentLink) &&
        updatedFormData?.ULB && Object.keys(updatedFormData?.ULB).length
      ) {
        setSubmitValve(true);
      } else {
        setSubmitValve(false);
      }
    },
    [],
  )

  const update = (data) => {
    const fileSize = data.document?.filestoreId?.fileSize ? data.document?.filestoreId?.fileSize : location.state?.DocumentEntity?.fileSize;
    const fileType = data.document?.filestoreId?.fileType ? data.document?.filestoreId?.fileType : location.state?.DocumentEntity?.fileType;
    const DocumentEntity = {
      ...location?.state?.DocumentEntity,
      name: data.documentName,
      description: data?.description.length ? data.description : "",
      category: data.docCategory?.name,
      filestoreId: data.document?.filestoreId?.fileStoreId,
      fileSize,
      fileType,
      documentLink: data.document?.documentLink,
      tenantId: data?.ULB?.code,
    };

    delete DocumentEntity.ULB;
    delete DocumentEntity.docCategory;
    delete DocumentEntity.documentName;
    navigate("/upyog-ui/employee/engagement/documents/update-response", { DocumentEntity });
  };
  return (
    <React.Fragment>
      <Header>{t("ES_ENGAGEMENT_EDIT_DOC")}</Header>
      <FormComposer
        label={t("ES_COMMON_UPDATE")}
        config={documentsFormConfig}
        onSubmit={(data) => {
          update(data);
        }}
        fieldStyle={{}}
        onFormValueChange={onFormValueChange}
        defaultValues={location.state?.DocumentEntity}
        isDisabled={!canSubmit}
      />

    </React.Fragment>
  );
};

export default Documents;
