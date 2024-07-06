import React from "react";
import BMCReviewPage from "../../pagecomponents/bmcReview";

const AadhaarEmployeePage = ({
  focusIndex,
  allOwners: owners,
  setFocusIndex,
  formData,
  formState,
  setOwners,
  t,
  setError,
  clearErrors,
  onSelect,
  userType,
  config,
}) => {
  return (
    <React.Fragment>
      <BMCReviewPage
        focusIndex={focusIndex}
        allOwners={owners}
        setFocusIndex={setFocusIndex}
        formData={formData}
        formState={formState}
        setOwners={setOwners}
        t={t}
        setError={setError}
        clearErrors={clearErrors}
        onSelect={onSelect}
        userType={userType}
        config={config}
      />
    </React.Fragment>
  );
};

export default AadhaarEmployeePage;
