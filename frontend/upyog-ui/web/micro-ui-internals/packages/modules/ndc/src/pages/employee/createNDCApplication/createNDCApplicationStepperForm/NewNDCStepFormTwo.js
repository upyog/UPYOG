import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
//
import { FormComposer, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import { updateNDCForm } from "../../../../redux/actions/NDCFormActions";
import _ from "lodash";

const NewNDCStepFormTwo = ({ config, onGoNext, onBackClick, t }) => {
  const currentStepData = useSelector((state) =>
    state.ndc.NDCForm.formData && state.ndc.NDCForm.formData[config.key] ? state.ndc.NDCForm.formData[config.key] : {}
  );
  const dispatch = useDispatch();
  const stateId = Digit.ULBService.getStateId();
  const [showToast, setShowToast] = useState(false);
  const [error, setError] = useState("");
  const { isLoading, data } = Digit.Hooks.pt.usePropertyMDMS(stateId, "NDC", ["Documents"]);
  function goNext(finaldata) {
    console.log(`Data in step ${config.currStepNumber} is: \n`, finaldata);
    const missingFields = validation(finaldata);
    if (missingFields.length > 0) {
      setError(`${t("NDC_MESSAGE_"+missingFields[0].replace(".", "_").toUpperCase())}`);
      setShowToast(true);
      setTimeout(() => {
        setShowToast(false);
      }, 3000);
      return;
    }
    onGoNext();
    //}
  }

  const closeToast = () => {
    setShowToast(false);
    setError("");
  };

  function validation(documents) {
    if (!isLoading) {
      const ndcDocumentsType = data?.NDC?.Documents || [];
      const documentsData = documents?.documents?.documents || [];

      // Step 1: Extract required document codes from ndcDocumentsType
      const requiredDocs = ndcDocumentsType.filter((doc) => doc.required).map((doc) => doc.code);

      // Step 2: Extract uploaded documentTypes
      const uploadedDocs = documentsData.map((doc) => doc.documentType);


      // Step 3: Identify missing required document codes
      const missingDocs = requiredDocs.filter((reqDoc) => !uploadedDocs.includes(reqDoc));

      return missingDocs;
    }
  }

  function onGoBack(data) {
    onBackClick(config.key, data);
  }

  const onFormValueChange = (setValue = true, data) => {
    if (!_.isEqual(data, currentStepData)) {
      dispatch(updateNDCForm(config.key, data));
    }
  };


  return (
    <React.Fragment>
      <FormComposer
        defaultValues={currentStepData}
        //heading={t("")}
        config={config.currStepConfig}
        onSubmit={goNext}
        onFormValueChange={onFormValueChange}
        //isDisabled={!canSubmit}
        label={t(`${config.texts.submitBarLabel}`)}
        currentStep={config.currStepNumber}
        onBackClick={onGoBack}
      />
      {showToast && <Toast isDleteBtn={true} error={true} label={error} onClose={closeToast} />}
    </React.Fragment>
  );
};

export { NewNDCStepFormTwo };
