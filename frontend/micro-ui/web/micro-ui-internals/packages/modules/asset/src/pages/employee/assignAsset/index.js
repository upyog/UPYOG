import { FormComposer, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams } from "react-router-dom";



import { newAssignConfig } from "../../../config/Create/assignConfig";

const assignAsset = () => {
  
  
  

  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);
  const defaultValues = {};
  const history = useHistory();

   const [_formData, setFormData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
   const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", { });
 

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);

  const { id: applicationNo } = useParams();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);

  

  const onFormValueChange = (setValue, formData, formState) => {
    
    setSubmitValve(!Object.keys(formState.errors).length); 
  };

  const onSubmit = (data) => {
    console.log("dta for payload console", data);
    let assignAsset ={
      assignedUser: data?.assigndetails?.[0]?.assignedUser,
      designation: data?.assigndetails?.[0]?.designation,
      employeeCode: data?.assigndetails?.[0]?.employeeCode,
      transferDate:data?.assigndetails?.[0]?.transferDate,
      returnDate: data?.assigndetails?.[0]?.returnDate,
    }   
    
    const formData = {
      ...applicationDetails?.applicationData?.applicationData,
      assignAsset
    };

    history.replace("/digit-ui/employee/asset/assetservice/response", { Asset: formData }); 
    

  };
    

   
  

  
  const configs = newAssignConfig;    

  
  return (
    <FormComposer
      heading={t("AST_ASSIGN_DETAILS")}
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

export default assignAsset;



