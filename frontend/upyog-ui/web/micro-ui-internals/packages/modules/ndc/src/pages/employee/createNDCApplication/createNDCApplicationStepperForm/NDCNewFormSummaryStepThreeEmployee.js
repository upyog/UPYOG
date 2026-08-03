import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLocation } from "react-router-dom";
import { FormComposer } from "@nudmcdgnpm/digit-ui-react-components";
import NDCSummary from "../../../../pageComponents/NDCSummary";
import { resetNDCForm } from "../../../../redux/actions/NDCFormActions";

const NDCNewFormSummaryStepThreeEmployee = ({ config, onGoNext, onBackClick, t }) => {
  const dispatch = useDispatch();
  const navigate = Digit.Hooks.useCustomNavigate();
  const tenantId = window.localStorage.getItem("CITIZEN.CITY");
  const checkFormData = useSelector((state) => state.ndc.NDCForm.formData || {});

  const formData = useSelector((state) => state.ndc.NDCForm.formData || {});
  // Function to handle the "Next" button click
  const goNext = async (action) => {
    const actionStatus = action?.action;
    try {
      const res = await onSubmit(formData, actionStatus); // wait for the API response

      // Check if the API call was successful
      if (res?.isSuccess) {
        navigate("/upyog-ui/employee/ndc/response/" + res?.response?.Applications?.[0]?.applicationNo);
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
    //     name: `${formData?.NDCDetails?.PropertyDetails?.firstName} ${formData?.NDCDetails?.PropertyDetails?.lastName}`.trim(),
    //     mobileNumber: formData?.NDCDetails?.PropertyDetails?.mobileNumber,
    //     gender: formData?.NDCDetails?.PropertyDetails?.gender,
    //     emailId: formData?.NDCDetails?.PropertyDetails?.email,
    //     type: "CITIZEN",
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
        documentAttachment: doc?.fileStoreId,
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
    console.log("here", data);
    onBackClick(config.key, data);
  };

  return (
    <React.Fragment>
      <NDCSummary formData={formData} goNext={goNext} onGoBack={onGoBack} />
    </React.Fragment>
  );
};

export { NDCNewFormSummaryStepThreeEmployee };
