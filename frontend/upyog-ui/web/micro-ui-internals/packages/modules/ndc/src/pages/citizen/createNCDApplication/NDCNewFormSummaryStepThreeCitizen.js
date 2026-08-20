import React from "react";
import { useDispatch, useSelector } from "react-redux";
import NDCSummary from "../../../pageComponents/NDCSummary";
import { resetNDCForm } from "../../../redux/actions/NDCFormActions";

const NDCNewFormSummaryStepThreeCitizen = ({
  config,
  onGoNext,
  onBackClick,
  t,
}) => {
  const dispatch = useDispatch();

  const navigate =
    Digit.Hooks.useCustomNavigate();

  const tenantId =
    Digit.ULBService.getCitizenCurrentTenant(true) ||
    Digit.ULBService.getCurrentTenantId();

  const formData = useSelector(
    (state) =>
      state.ndc.NDCForm.formData || {}
  );

  /**
   * Final NDC submission.
   *
   * The final workflow action is APPLY.
   */
  const goNext = async (action) => {
    /**
     * Use APPLY as the default action.
     *
     * This prevents workflow.action from
     * becoming null/undefined.
     */
    const actionStatus =
      action?.action || "APPLY";

    console.log(
      "========== NDC SUMMARY SUBMIT =========="
    );

    console.log(
      "Received action:",
      action
    );

    console.log(
      "Workflow action:",
      actionStatus
    );

    try {
      const res = await onSubmit(
        formData,
        actionStatus
      );

      if (res?.isSuccess) {
        const applicationNo =
          res?.response
            ?.Applications?.[0]
            ?.applicationNo;

        console.log(
          "NDC submission successful:",
          applicationNo
        );

        navigate(
          `/upyog-ui/citizen/ndc/response/${applicationNo}`
        );
      } else {
        console.error(
          "NDC submission failed:",
          res?.response
        );
      }
    } catch (error) {
      console.error(
        "NDC submission error:",
        error
      );

      alert(
        `Error: ${
          error?.message ||
          "Something went wrong"
        }`
      );
    }

    console.log(
      "========================================"
    );
  };

  /**
   * Build final NDC update payload.
   */
  const mapToNDCPayload = (
    inputData,
    actionStatus
  ) => {
    /**
     * Existing application is the source
     * of truth.
     */
    const baseApplication =
      inputData?.responseData?.[0] ||
      inputData?.apiData
        ?.Applications?.[0] ||
      {};

    const applicant =
      Digit.UserService.getUser()?.info ||
      {};

    /**
     * Existing owners.
     */
    let owners = (
      inputData?.apiData
        ?.Applications?.[0]
        ?.owners ||
      baseApplication?.owners ||
      []
    ).map((item) => {
      const obj = JSON.parse(
        JSON.stringify(item)
      );

      delete obj.status;

      return obj;
    });

    /**
     * Fallback owner.
     */
    if (
      owners.length === 0 &&
      applicant
    ) {
      owners = [
        {
          name: applicant?.name,
          mobileNumber:
            applicant?.mobileNumber,
          emailId:
            applicant?.emailId,
          type: applicant?.type,
        },
      ];
    }

    /**
     * Final workflow action.
     *
     * APPLY is the required action for
     * final NDC submission.
     */
    const workflowAction =
      actionStatus || "APPLY";

    /**
     * Uploaded documents.
     */
    const documents = [];

    const uploadedDocuments =
      inputData?.DocummentDetails
        ?.documents?.documents ||
      inputData?.documents
        ?.documents ||
      [];

    uploadedDocuments.forEach(
      (doc) => {
        if (!doc) {
          return;
        }

        documents.push({
          uuid: doc?.documentUid,
          documentType:
            doc?.documentType,
          documentAttachment:
            doc?.fileStoreId ||
            doc?.filestoreId ||
            doc?.documentAttachment,
        });
      }
    );

    /**
     * Updated application.
     */
    const updatedApplication = {
      ...baseApplication,

      /**
       * IMPORTANT:
       * Workflow action must be APPLY.
       */
      workflow: {
        ...baseApplication?.workflow,
        action: workflowAction,
      },

      /**
       * Keep application action in sync.
       */
      action: workflowAction,

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

      reason:
        baseApplication?.reason,
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
     * Debug logging.
     */
    console.log(
      "========== NDC APPLY PAYLOAD =========="
    );

    console.log(
      "Workflow action:",
      updatedApplication
        ?.workflow?.action
    );

    console.log(
      "Application action:",
      updatedApplication?.action
    );

    console.log(
      "Workflow:",
      JSON.stringify(
        updatedApplication?.workflow,
        null,
        2
      )
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
      "======================================="
    );

    return payload;
  };

  /**
   * Submit NDC update request.
   */
  const onSubmit = async (
    data,
    actionStatus
  ) => {
    try {
      /**
       * Never allow null/undefined action.
       */
      const finalAction =
        actionStatus || "APPLY";

      const finalPayload =
        mapToNDCPayload(
          data,
          finalAction
        );

      console.log(
        "Calling NDCUpdate with APPLY:"
      );

      console.log(
        JSON.stringify(
          finalPayload,
          null,
          2
        )
      );

      const response =
        await Digit.NDCService.NDCUpdate({
          tenantId,
          details: finalPayload,
        });

      console.log(
        "NDC UPDATE RESPONSE:",
        response
      );

      /**
       * Successful response.
       */
      if (
        response?.ResponseInfo?.status ===
        "successful"
      ) {
        dispatch(
          resetNDCForm()
        );

        return {
          isSuccess: true,
          response,
        };
      }

      /**
       * Backend returned an error.
       */
      console.error(
        "NDC update failed:",
        response
      );

      return {
        isSuccess: false,
        response,
      };
    } catch (error) {
      console.error(
        "NDC update exception:",
        error
      );

      return {
        isSuccess: false,
        error,
      };
    }
  };

  /**
   * Back button.
   */
  const onGoBack = (data) => {
    onBackClick(
      config.key,
      data
    );
  };

  return (
    <React.Fragment>
      <NDCSummary
        formData={formData}
        goNext={goNext}
        onGoBack={onGoBack}
      />
    </React.Fragment>
  );
};

export {
  NDCNewFormSummaryStepThreeCitizen,
};