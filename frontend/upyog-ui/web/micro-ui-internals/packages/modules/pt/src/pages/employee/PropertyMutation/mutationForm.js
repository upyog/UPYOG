import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { FormComposer, Toast, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import { newConfigMutate } from "../../../config/Mutate/config";


const MutationForm = ({ applicationData, tenantId }) => {
  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);

  const { data: mutationDocs, isLoading } = Digit.Hooks.pt.useMDMS(Digit.ULBService.getStateId(), "PropertyTax", "MutationDocuments");
  const mutation = Digit.Hooks.pt.usePropertyAPI(tenantId, false);
  const defaultValues = {
    originalData: applicationData,
  };

  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", {});

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    sessionStorage.removeItem("EMPLOYEE_MUTATION_TRIGGERED");
  }, []);

  const navigate = Digit.Hooks.useCustomNavigate();

  const onFormValueChange = (setValue, formData, formState) => {
    const errorKeys = Object.keys(formState.errors || {});
    // Exclude "documents" from the generic error check — document errors are
    // managed separately via addError/removeError in SelectDocuments and we
    // validate them directly via formData below.
    const hasOtherErrors = errorKeys.filter((key) => key !== "documents").length > 0;

    // Check that all required documents have been uploaded.
    // SelectDocuments sets formData.documents = { documents: [...uploaded], propertyTaxDocumentsLength: N }
    const uploadedDocs = formData?.documents?.documents || [];
    const requiredDocCount = formData?.documents?.propertyTaxDocumentsLength || 0;
    const allDocsUploaded = requiredDocCount > 0 && uploadedDocs.length >= requiredDocCount;

    if (!hasOtherErrors && formData?.additionalDetails && allDocsUploaded) {
      let { additionalDetails } = formData;
      let {
        documentDate,
        documentNumber,
        documentValue,
        marketValue,
        reasonForTransfer,
      } = additionalDetails || {};
      setSubmitValve(
        !(
          !documentDate ||
          !documentNumber ||
          !documentValue ||
          !marketValue ||
          !reasonForTransfer
        )
      );
    } else {
      setSubmitValve(false);
    }
    if (formData?.ownershipCategory?.code?.includes?.("MULTIPLE")) {
      if (formData?.owners?.length < 2) setSubmitValve(false);
    }
  };


  const onSubmit = (data) => {
    data.originalData.owners = data.originalData?.owners?.filter((owner) => owner.status == "ACTIVE");
    let { additionalDetails } = data;
    let prevDocs =
      data?.originalData?.documents?.filter(
        (oldDoc) => !mutationDocs?.PropertyTax?.MutationDocuments.some((mut) => oldDoc.documentType.includes(mut.code))
      ) || [];
    const submitData = {
      Property: {
        ...data.originalData,
        creationReason: "MUTATION",
        owners: [
          ...data.originalData?.owners?.map((e) => ({
            ...e,
            landlineNumber: data.owners[0].altContactNumber,
            altContactNumber: data.owners[0].altContactNumber,
            status: "INACTIVE",
          })),
          ...data.owners.map((owner, index) => {
            let obj = {};
            let gender = owner.gender.code;
            let ownerType = owner.ownerType.code;
            let relationship = owner.relationship.code;
            let additionalDetails = { ownerSequence: index, ownerName: owner?.name }
            obj.documents = [data?.documents?.documents?.find((e) => e.documentType?.includes("OWNER.IDENTITYPROOF"))];
            if (owner.documents) {
              let { documentUid, documentType } = owner.documents;
              obj.documents = [...obj.documents, { documentUid, documentType: documentType.code, fileStoreId: documentUid }];
            }
            return {
              ...owner,
              gender,
              ownerType,
              relationship,
              inistitutetype: owner?.institution?.type?.code,
              landlineNumber: owner?.altContactNumber,
              ...obj,
              status: "ACTIVE",
              additionalDetails
            };
          }),
        ],
        additionalDetails: {
          ...additionalDetails,
          isMutationInCourt: additionalDetails.isMutationInCourt?.code,
          reasonForTransfer: additionalDetails?.reasonForTransfer.code,
          isPropertyUnderGovtPossession: additionalDetails?.isPropertyUnderGovtPossession?.code,
          documentDate: new Date(additionalDetails?.documentDate).getTime(),
          marketValue: Number(additionalDetails?.marketValue),
          owners: [
            ...data.originalData?.owners?.map((e) => ({
              ...e,
              landlineNumber: data.owners[0].altContactNumber,
              altContactNumber: data.owners[0].altContactNumber,
              status: "INACTIVE",
            })),
            ...data.owners.map((owner, index) => {
              let obj = {};
              let gender = owner.gender.code;
              let ownerType = owner.ownerType.code;
              let relationship = owner.relationship.code;
              let additionalDetails = { ownerSequence: index, ownerName: owner?.name }
              obj.documents = [data?.documents?.documents?.find((e) => e.documentType?.includes("OWNER.IDENTITYPROOF"))];
              if (owner.documents) {
                let { documentUid, documentType } = owner.documents;
                obj.documents = [...obj.documents, { documentUid, documentType: documentType.code, fileStoreId: documentUid }];
              }
              return {
                ...owner,
                gender,
                ownerType,
                relationship,
                inistitutetype: owner?.institution?.type?.code,
                landlineNumber: owner?.altContactNumber,
                ...obj,
                status: "ACTIVE",
                additionalDetails
              };
            }),
          ],
        },
        ownershipCategory: data.ownershipCategory.code,
        documents: [
          ...prevDocs,
          ...data?.documents?.documents.map((e) =>
            e.documentType.includes("OWNER.TRANSFERREASONDOCUMENT") ? { ...e, documentType: e.documentType.split(".")[2] } : e
          ),
        ],
        workflow: { action: "OPEN", businessService: "PT.MUTATION", moduleName: "PT", tenantId: data.originalData.tenantId },
      },
    };

    if (!submitData.Property.ownershipCategory.includes("INDIVIDUAL")) {
      submitData.Property.institution = {
        nameOfAuthorizedPerson: data.owners[0].name,
        name: data.owners[0].institution.name,
        designation: data.owners[0].designation,
        tenantId: data.originalData.tenantId,
        type: data.owners[0].institution.type.code,
      };
    }
    else {
      submitData.Property.institution = null;
    }
    mutation.mutate(
      submitData,
      {
        onSuccess: (responseData) => {
          navigate("/upyog-ui/employee/pt/response", {
            replace: true,
            state: {
              Property: submitData.Property,
              responseData,
              isSuccess: true,
              action: "SUBMIT",
              key: "UPDATE"
            }
          });
        },
        onError: (error) => {
          navigate("/upyog-ui/employee/pt/response", {
            replace: true,
            state: {
              Property: submitData.Property,
              responseData: null,
              isSuccess: false,
              error: error?.response?.data?.Errors?.[0]?.message || error?.message || "Error updating mutation",
              action: "SUBMIT",
              key: "UPDATE"
            }
          });
        }
      }
    );
  };

  if (isLoading || mutation.isPending) {
    return <Loader />;
  }

  const configs = newConfigMutate;
  return (
    <React.Fragment>
      <FormComposer
        heading={t("ES_TITLE_MUTATE_PROPERTY")}
        isDisabled={!canSubmit}
        label={t("ES_COMMON_APPLICATION_SUBMIT")}
        config={configs.map((config) => {
          return {
            ...config,
            body: [
              ...config.body.filter((a) => !a.hideInEmployee),
              {
                withoutLabel: true,
                type: "custom",
                populators: {
                  name: "originalData",
                  component: (props, customProps) => <React.Fragment />,
                },
              },
            ],
          };
        })}
        fieldStyle={{ marginRight: 0 }}
        onSubmit={onSubmit}
        defaultValues={defaultValues}
        onFormValueChange={onFormValueChange}
      />
    </React.Fragment>
  );
};

export default MutationForm;
