import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  FormComposer,
  Toast,
} from "@nudmcdgnpm/digit-ui-react-components";
import { updateNDCForm } from "../../../redux/actions/NDCFormActions";
import _ from "lodash";

const NewNDCStepFormTwo = ({
  config,
  onGoNext,
  onBackClick,
  t,
}) => {
  const currentStepData = useSelector((state) =>
    state.ndc.NDCForm.formData &&
    state.ndc.NDCForm.formData[config.key]
      ? state.ndc.NDCForm.formData[config.key]
      : {}
  );

  const checkFormData = useSelector(
    (state) => state.ndc.NDCForm.formData || {}
  );

  const dispatch = useDispatch();

  const stateId = Digit.ULBService.getStateId();

  const [showToast, setShowToast] = useState(false);
  const [error, setError] = useState("");

  const tenantId =
    Digit.ULBService.getCitizenCurrentTenant(true) ||
    Digit.ULBService.getCurrentTenantId();

  const { isLoading, data } = Digit.Hooks.ndc.useNDCDoc(
    stateId,
    "NDC",
    "Documents"
  );

  const id = window.location.pathname.split("/").pop();

  const user = Digit.UserService.getUser();

  const {
    isLoading: propertyLoading,
    data: applicationDetails,
  } = Digit.Hooks.ndc.useSearchEmployeeApplication(
    { applicationNo: id },
    tenantId
  );

  useEffect(() => {
    if (applicationDetails?.Applications?.length) {
      dispatch(
        updateNDCForm(
          "responseData",
          applicationDetails.Applications
        )
      );
    }
  }, [applicationDetails, dispatch]);

  /**
   * Handle Next button.
   */
  const goNext = async (finaldata) => {
    console.log(
      `Data in step ${config.currStepNumber} is:`,
      finaldata
    );

    const missingFields = validation(finaldata);

    if (missingFields.length > 0) {
      setError(
        `${t(
          "NDC_MESSAGE_" +
            missingFields[0]
              .replace(".", "_")
              .toUpperCase()
        )}`
      );

      setShowToast(true);

      setTimeout(() => {
        setShowToast(false);
      }, 3000);

      return;
    }

    const isRealId =
      id && id.startsWith("NDC-");

    if (isRealId) {
      console.log(
        "Updating existing NDC application:",
        id
      );

      await updateApplication(finaldata);
    } else {
      console.log(
        "Creating new NDC application"
      );

      onGoNext();
    }
  };

  /**
   * Update an existing NDC application.
   *
   * Final workflow action is APPLY.
   */
  const updateApplication = async (formData) => {
    try {
      const applicant =
        Digit.UserService.getUser()?.info || {};

      /**
       * Existing application from backend.
       */
      const baseApplication =
        checkFormData?.responseData?.[0] || {};

      if (!baseApplication?.applicationNo) {
        console.error(
          "NDC update failed: application data is missing.",
          baseApplication
        );

        setError(
          t(
            "NDC_MESSAGE_APPLICATION_DATA_NOT_FOUND"
          )
        );

        setShowToast(true);

        return;
      }

      /**
       * Build owners array.
       */
      const existingOwners =
        baseApplication?.owners || [];

      const owners =
        existingOwners.length > 0
          ? existingOwners.map((item) => {
              const obj = JSON.parse(
                JSON.stringify(item)
              );

              delete obj.status;

              return obj;
            })
          : [
              {
                name: applicant?.name,
                mobileNumber:
                  applicant?.mobileNumber,
                gender:
                  checkFormData?.NDCDetails
                    ?.PropertyDetails?.gender,
                emailId:
                  applicant?.emailId,
                type: applicant?.type,
              },
            ];

      /**
       * Build documents array.
       */
      const documents = [];

      const uploadedDocuments =
        formData?.documents?.documents || [];

      uploadedDocuments.forEach((doc) => {
        if (!doc) {
          return;
        }

        documents.push({
          uuid: doc?.documentUid,
          documentType: doc?.documentType,
          documentAttachment:
            doc?.fileStoreId ||
            doc?.filestoreId ||
            doc?.documentAttachment,
        });
      });

      /**
       * Updated application.
       *
       * IMPORTANT:
       * Final workflow action is APPLY.
       */
      const updatedApplication = {
        ...baseApplication,

        workflow: {
          ...baseApplication?.workflow,
          action: "APPLY",
        },

        action: "APPLY",

        owners,

        NdcDetails:
          baseApplication?.NdcDetails,

        Documents: documents,

        active:
          baseApplication?.active !== undefined
            ? baseApplication.active
            : true,

        applicationStatus:
          baseApplication?.applicationStatus ||
          "INITIATED",
      };

      /**
       * Final payload.
       */
      const payload = {
        Applications: [
          updatedApplication,
        ],
      };

      /**
       * Debug.
       */
      console.log(
        "========== NDC APPLY UPDATE =========="
      );

      console.log(
        "Workflow action:",
        updatedApplication?.workflow?.action
      );

      console.log(
        "Application action:",
        updatedApplication?.action
      );

      console.log(
        "Full payload:",
        JSON.stringify(
          payload,
          null,
          2
        )
      );

      console.log(
        "======================================"
      );

      /**
       * Call NDC update API.
       */
      const response =
        await Digit.NDCService.NDCUpdate({
          tenantId,
          details: payload,
        });

      console.log(
        "NDC UPDATE RESPONSE:",
        response
      );

      /**
       * Successful update.
       */
      if (
        response?.ResponseInfo?.status ===
        "successful"
      ) {
        dispatch(
          updateNDCForm(
            "apiData",
            response
          )
        );

        onGoNext();

        return {
          isSuccess: true,
          response,
        };
      }

      /**
       * Backend error.
       */
      console.error(
        "NDC update failed:",
        response
      );

      const backendError =
        response?.Errors?.[0]?.message ||
        response?.Errors?.[0]?.code ||
        t("NDC_MESSAGE_UPDATE_FAILED");

      setError(backendError);
      setShowToast(true);

      return {
        isSuccess: false,
        response,
      };
    } catch (err) {
      console.error(
        "NDC update API exception:",
        err
      );

      setError(
        err?.message ||
          t("NDC_MESSAGE_UPDATE_FAILED")
      );

      setShowToast(true);

      return {
        isSuccess: false,
        error: err,
      };
    }
  };

  /**
   * Close toast.
   */
  const closeToast = () => {
    setShowToast(false);
    setError("");
  };

  /**
   * Validate required documents.
   */
  const validation = (documents) => {
    if (isLoading) {
      return [];
    }

    const ndcDocumentsType =
      data?.NDC?.Documents || [];

    const documentsData =
      documents?.documents?.documents || [];

    const requiredDocs = ndcDocumentsType
      .filter((doc) => doc?.required)
      .map((doc) => doc?.code)
      .filter(Boolean);

    const uploadedDocs = documentsData
      .map((doc) => doc?.documentType)
      .filter(Boolean);

    const missingDocs =
      requiredDocs.filter(
        (requiredDoc) =>
          !uploadedDocs.includes(requiredDoc)
      );

    return missingDocs;
  };

  /**
   * Back button.
   */
  const onGoBack = (formData) => {
    onBackClick(
      config.key,
      formData
    );
  };

  /**
   * Keep Redux state updated.
   */
  const onFormValueChange = (
    setValue = true,
    formData
  ) => {
    if (
      !_.isEqual(
        formData,
        currentStepData
      )
    ) {
      dispatch(
        updateNDCForm(
          config.key,
          formData
        )
      );
    }
  };

  return (
    <React.Fragment>
      <FormComposer
        defaultValues={currentStepData}
        config={config.currStepConfig}
        onSubmit={goNext}
        onFormValueChange={
          onFormValueChange
        }
        label={t(
          `${config.texts.submitBarLabel}`
        )}
        currentStep={
          config.currStepNumber
        }
        onBackClick={onGoBack}
      />

      {showToast && (
        <Toast
          isDleteBtn={true}
          error={true}
          label={error}
          onClose={closeToast}
        />
      )}
    </React.Fragment>
  );
};

export { NewNDCStepFormTwo };