import React, { useState } from "react";
import { FormComposer, Header, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";
import { documentsFormConfig } from "../../../config/doc-create";

const Documents = () => {
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const queryClient = useQueryClient();
  const mutation = Digit.Hooks.engagement.useDocCreate();
  const [canSubmit, setSubmitValve] = useState(false);

  const onFormValueChange = (setValue, formData, formState) => {
    if (
      formData?.documentName &&
      formData?.docCategory &&
      (formData?.document.filestoreId || formData?.document.documentLink) &&
      formData?.ULB?.length
    ) {
      setSubmitValve(true);
    } else {
      setSubmitValve(false);
    }
  };

  const onSubmit = (data) => {  
    const DocumentEntity = {
      name: data.documentName,
      description: data?.description.length ? data.description : "",
      category: data.docCategory?.name,
      documentLink: data.document?.documentLink,
      filestoreId: data.document?.filestoreId?.fileStoreId,
      fileSize: data.document?.filestoreId?.fileSize,
      fileType: data.document?.filestoreId?.fileType,
      tenantIds: data.ULB.map((e) => e.code),
    };

    mutation.mutate(DocumentEntity, {
      onSuccess: (responseData) => {
        queryClient.clear();
        navigate("/upyog-ui/employee/engagement/documents/response", {
          state: { isSuccess: true, data: responseData },
        });
      },
      onError: (error) => {
        navigate("/upyog-ui/employee/engagement/documents/response", {
          state: { isSuccess: false, error },
        });
      },
    });
  };

  if (mutation.isPending) {
    return <Loader />;
  }

  return (
    <div>
     <Header>{t("ES_ENGAGEMENT_DOCUMENTS")}</Header>
    <FormComposer
     // heading={t("ES_ENGAGEMENT_DOCUMENTS")}
     label={t("ES_COMMON_APPLICATION_SUBMIT")}
     config={documentsFormConfig}
     onSubmit={onSubmit}
     fieldStyle={{}}
     onFormValueChange={onFormValueChange}
     isDisabled={!canSubmit}
     />
    </div>
  );
};

export default Documents;
