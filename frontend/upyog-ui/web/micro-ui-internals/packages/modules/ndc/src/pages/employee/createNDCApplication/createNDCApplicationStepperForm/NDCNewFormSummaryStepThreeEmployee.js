import React from "react";
import { useDispatch, useSelector } from "react-redux";
import NDCSummary from "../../../../pageComponents/NDCSummary";
import { resetNDCForm } from "../../../../redux/actions/NDCFormActions";

const NDCNewFormSummaryStepThreeEmployee = ({ config, onGoNext, onBackClick, t }) => {
  const dispatch = useDispatch();

  const navigate = Digit.Hooks.useCustomNavigate();

  const tenantId = window.localStorage.getItem("CITIZEN.CITY");

  const formData = useSelector((state) => state.ndc.NDCForm.formData || {});

  /**
   * FINAL WORKFLOW ACTION
   *
   * The NDC workflow expects APPLY as the final action.
   * Never allow null / undefined action to reach the API.
   */
  const FINAL_WORKFLOW_ACTION = "APPLY";

  /**
   * Function to handle the "Next" button click.
   */
  const goNext = async (action) => {
    /**
     * Always use APPLY as the final workflow action.
     *
     * Even if NDCSummary sends:
     * - undefined
     * - null
     * - empty action
     *
     * we will still send APPLY.
     */
    const actionStatus = action?.action || FINAL_WORKFLOW_ACTION;

    try {
      const res = await onSubmit(formData, actionStatus);

      /**
       * Move to response page only after
       * successful API response.
       */
      if (res?.isSuccess) {
        navigate("/upyog-ui/employee/ndc/response/" + res?.response?.Applications?.[0]?.applicationNo);
      } else {
        console.error("NDC submission failed. Not moving to response page.", res?.response);
      }
    } catch (error) {
      console.error("Error while submitting NDC application:", error);

      alert(`Error: ${error?.message}`);
    }
  };

  /**
   * Convert Redux form data into the payload
   * expected by NDC Update API.
   */
  function mapToNDCPayload(inputData, actionStatus) {
    const applicant = Digit.UserService.getUser()?.info || {};

    /**
     * Make sure workflow action can NEVER be null/undefined.
     */
    const finalWorkflowAction = actionStatus || FINAL_WORKFLOW_ACTION;

    /**
     * Get owners from existing application data.
     */
    const owners = (inputData?.apiData?.Applications?.[0]?.owners || []).map((item) => {
      const obj = JSON.parse(JSON.stringify(item));

      /**
       * status should not be sent in update payload.
       */
      delete obj.status;

      return obj;
    });

    /**
     * Pick the source of truth for the application.
     */
    const baseApplication = formData?.responseData?.[0] || formData?.apiData?.Applications?.[0] || {};

    /**
     * Build updated application.
     *
     * IMPORTANT:
     * workflow.action is explicitly set to APPLY.
     */
    const updatedApplication = {
      ...baseApplication,

      workflow: {
        ...baseApplication?.workflow,

        /**
         * FINAL ACTION = APPLY
         */
        action: finalWorkflowAction,
      },

      owners: owners,

      NdcDetails: baseApplication?.NdcDetails,

      /**
       * Documents will be populated below.
       */
      Documents: [],
    };

    /**
     * Add uploaded documents.
     */
    (inputData?.DocummentDetails?.documents?.documents || []).forEach((doc) => {
      updatedApplication.Documents.push({
        uuid: doc?.documentUid,
        documentType: doc?.documentType,
        documentAttachment: doc?.fileStoreId,
      });
    });

    /**
     * Final NDC Update API payload.
     */
    const payload = {
      Applications: [updatedApplication],
    };
    return payload;
  }

  /**
   * Submit NDC application.
   */
  const onSubmit = async (data, actionStatus) => {
    /**
     * Never allow null/undefined action.
     */
    const finalAction = actionStatus || FINAL_WORKFLOW_ACTION;

    const finalPayload = mapToNDCPayload(data, finalAction);

    try {
      const response = await Digit.NDCService.NDCUpdate({
        tenantId,
        details: finalPayload,
      });

      /**
       * Reset form only after API call.
       */
      dispatch(resetNDCForm());

      if (response?.ResponseInfo?.status === "successful") {
        return {
          isSuccess: true,
          response,
        };
      }

      return {
        isSuccess: false,
        response,
      };
    } catch (error) {
      console.error("NDC Update API error:", error);

      return {
        isSuccess: false,
        response: error,
      };
    }
  };

  /**
   * Function to handle Back button.
   */
  const onGoBack = (data) => {

    onBackClick(config.key, data);
  };

  return (
    <React.Fragment>
      <NDCSummary formData={formData} goNext={goNext} onGoBack={onGoBack} />
    </React.Fragment>
  );
};

export { NDCNewFormSummaryStepThreeEmployee };
