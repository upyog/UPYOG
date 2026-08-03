import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { FormComposer } from "@nudmcdgnpm/digit-ui-react-components";
import NDCSummary from "../../../pageComponents/NDCSummary";
import { resetNDCForm } from "../../../redux/actions/NDCFormActions";

// This component is the final step in the NDC application process for citizens. It displays a summary of all the information entered by the user in the previous steps and allows them to review before submission. The user can also go back to edit the details if needed. Upon clicking the "Next" button, it triggers the submission of the application and redirects to a response page based on the API response.
const NDCNewFormSummaryStepThreeCitizen = ({ config, onGoNext, onBackClick, t }) => {
  const dispatch = useDispatch();
  const navigate = Digit.Hooks.useCustomNavigate();
  // const tenantId = Digit.ULBService.getCurrentTenantId();
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const user = Digit.UserService.getUser();

  const formData = useSelector((state) => state.ndc.NDCForm.formData || {});
  // Function to handle the "Next" button click


  const goNext = async (action) => {
    const actionStatus = action?.action;
    try {
      const res = await onSubmit(formData, actionStatus); // wait for the API response
      // Check if the API call was successful
      if (res?.isSuccess) {
        navigate(`/upyog-ui/citizen/ndc/response/${res?.response?.Applications?.[0]?.applicationNo}`);
      } else {
        console.error("Submission failed, not moving to next step.", res?.response);
      }
    } catch (error) {
      alert(`Error: ${error?.message}`);
    }
  };

  function mapToNDCPayload(inputData, actionStatus) {
    const applicant = Digit.UserService.getUser()?.info || {};

    const owners = (inputData?.apiData?.Applications?.[0]?.owners || [])?.map((item) => {
      const obj = JSON.parse(JSON.stringify(item));
      delete obj.status;
      return obj;
    });
    // const owners = [
    //   {
    //     // name: `${data?.PropertyDetails?.firstName} ${data?.PropertyDetails?.lastName}`.trim(),
    //     name: user?.info?.name,
    //     mobileNumber: user?.info?.mobileNumber,
    //     gender: formData?.NDCDetails?.PropertyDetails?.gender,
    //     emailId: user?.info?.emailId,
    //     type: user?.info?.type,
    //   },
    // ];

    // Pick the source of truth for the application
    const baseApplication = formData?.responseData?.[0] || formData?.apiData?.Applications?.[0] || {};


    // Clone and modify workflow action
    const updatedApplication = {
      ...baseApplication,
      workflow: {
        ...baseApplication?.workflow,
        action: actionStatus,
      },
      owners: owners,
      NdcDetails: baseApplication?.NdcDetails,
      Documents: [], // We'll populate below
    };

    (inputData?.DocummentDetails?.documents?.documents || []).forEach((doc) => {
      updatedApplication.Documents.push({
        uuid: doc?.documentUid,
        documentType: doc?.documentType,
        documentAttachment: doc?.filestoreId,
      });
    });

    // Final payload matches update API structure
    const payload = {
      Applications: [updatedApplication],
    };


    return payload;
  }

  const onSubmit = async (data, actionStatus) => {
    const finalPayload = mapToNDCPayload(data, actionStatus);
    // return;
    const response = await Digit.NDCService.NDCUpdate({ tenantId, details: finalPayload });
    dispatch(resetNDCForm());
    if (response?.ResponseInfo?.status === "successful") {
      return { isSuccess: true, response };
    } else {
      return { isSuccess: false, response };
    }
  };

  // Function to handle the "Back" button click
  const onGoBack = (data) => {
    onBackClick(config.key, data);
  };

  return (
    <React.Fragment>
      <NDCSummary formData={formData} goNext={goNext} onGoBack={onGoBack} />
    </React.Fragment>
  );
};

export { NDCNewFormSummaryStepThreeCitizen };
