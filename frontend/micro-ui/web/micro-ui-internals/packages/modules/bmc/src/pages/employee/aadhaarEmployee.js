import React from "react";
import WorkflowActions from "../../components/Workflow";
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
  //const { businessService, applicationNo, moduleCode } = Digit.Hooks.useQueryParams();
  //console.log(tenantId, businessService, applicationNo, moduleCode);
  console.log(formData);
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
      <WorkflowActions ActionBarStyle={{}} MenuStyle={{}} businessService={"bmc-schemes"} applicationNo={"MH-0001"} tenantId={"mh"} moduleCode={"BMC"} />
    </React.Fragment>
  );
};

export default AadhaarEmployeePage;
