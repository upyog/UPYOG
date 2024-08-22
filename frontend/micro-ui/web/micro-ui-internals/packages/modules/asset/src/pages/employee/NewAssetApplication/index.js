import { FormComposer, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams } from "react-router-dom";
import { assignConfig } from "../../../config/Create/assignConfig";

const NewAssetApplication = () => {
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
    const assignedDateEpoch = convertToEpoch(data?.assigndetails?.[0]?.transferDate);
    const formData = {
      id: applicationDetails?.applicationData?.applicationData?.id,
      tenantId: tenantId,
      applicationNo: applicationDetails?.applicationData?.applicationData?.applicationNo,
      assetAssignment: {
        assignmentId: "",
        assetApplicaltionNo:"",
        assetId: "",
        assignedUserName: data?.assigndetails?.[0]?.assignedUser,
        designation: data?.assigndetails?.[0]?.designation,
        department: data?.assigndetails?.[0]?.allocatedDepartment?.code,    //later we need to send the procured department here.
        assignedDate: assignedDateEpoch,
        isAssigned: true,
        allocatedDepartment:data?.assigndetails?.[0]?.allocatedDepartment?.code, 
        employeecode:data?.assigndetails?.[0]?.employeeCode,
        auditDetails: {
          createdBy: "",
          lastModifiedBy: "",
          createdTime: "",
          lastModifiedTime: ""
        }
      },   
    };

    history.replace("/digit-ui/employee/asset/assetservice/assign-response", { Assets: formData }); 
    

  };
    

   
  

  
  const configs = assignConfig;    

  
  return (
    <FormComposer
      heading={t("AST_ASSIGN_ASSET")}
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

export default NewAssetApplication;



