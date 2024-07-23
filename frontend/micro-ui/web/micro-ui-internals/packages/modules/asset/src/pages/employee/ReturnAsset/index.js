import { FormComposer, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams } from "react-router-dom";
import { returnConfig } from "../../../config/Create/returnConfig";

const ReturnAsset = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);
  const defaultValues = {};
  const history = useHistory();
  const { id: applicationNo } = useParams();
  const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);
  
   const [_formData, setFormData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
   const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", { });


  const convertToEpoch = (dateString) => {
    const [year, month, day] = dateString.split("-").map(Number);
    return new Date(year, month - 1, day).getTime();
  };

 

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);

  

  const onFormValueChange = (setValue, formData, formState) => {
    
    setSubmitValve(!Object.keys(formState.errors).length); 
  };

  const onSubmit = (data) => {
    
    const returnDateEpoch = convertToEpoch(data?.returndetails?.[0]?.returnDate);
    const formData = {
      id: applicationDetails?.applicationData?.applicationData?.id,
      tenantId: tenantId,
      applicationNo: applicationDetails?.applicationData?.applicationData?.applicationNo,
      assetAssignment: {
        assignmentId: applicationDetails?.applicationData?.applicationData?.assetAssignment?.assignmentId,
        assetId: "",
        assignedUserName: applicationDetails?.applicationData?.applicationData?.assetAssignment?.assignedUserName,
        designation: applicationDetails?.applicationData?.applicationData?.assetAssignment?.designation || "",
        department: applicationDetails?.applicationData?.applicationData?.assetAssignment?.department || "",    //later we need to send the procured department here.
        assignedDate: applicationDetails?.applicationData?.applicationData?.assetAssignment?.assignedDate,
        isAssigned: false,
        returnDate: returnDateEpoch,
        auditDetails: {
          createdBy: "",
          lastModifiedBy: "",
          createdTime: "",
          lastModifiedTime: ""
        }
      },   
    };

    history.replace("/digit-ui/employee/asset/assetservice/return-response", { Asset: formData }); 
    

  };
    

   
  

  
  const configs = returnConfig;    

  
  return (
    <FormComposer
      heading={t("AST_RETURN_ASSET")}
      isDisabled={!canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={configs.map((config) => {
       
        return {
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
        };
      })}
      fieldStyle={{ marginRight: 0 }}
      cardStyle={{Width: 60}}
      onSubmit={onSubmit}
      defaultValues={defaultValues}
      onFormValueChange={onFormValueChange}
     
    />
  );
};

export default ReturnAsset;



