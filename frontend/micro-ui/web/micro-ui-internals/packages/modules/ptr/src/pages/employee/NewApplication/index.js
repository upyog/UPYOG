  import { FormComposer, Loader } from "@upyog/digit-ui-react-components";
  import React, { useEffect, useState } from "react";
  import { useTranslation } from "react-i18next";
  import { useHistory } from "react-router-dom";
  import { newConfig } from "../../../config/Create/config";

  const NewApplication = () => {
    const tenantId = Digit.ULBService.getCurrentTenantId();
    

    const { t } = useTranslation();
    const [canSubmit, setSubmitValve] = useState(false);
    const defaultValues = {};
    const history = useHistory();

    const [_formData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
    const [setMutationHappened] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
    const [clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", { });
  
    const { data: commonFields } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "PetService", [{ name: "CommonFieldsConfigEmp" }],
    {
      select: (data) => {
          const formattedData = data?.["PetService"]?.["CommonFieldsConfigEmp"]
          return formattedData;
      },
  });  
  

    useEffect(() => {
      setMutationHappened(false);
      clearSuccessData();
    }, []);

    

    const onFormValueChange = (setValue, formData, formState) => {
      
      setSubmitValve(!Object.keys(formState.errors).length); 
    };

    const onPetSubmit = (data) => {
      


      

      const formData = [{
        tenantId,
        ...data?.owners[0],
        petDetails:{
          ...data?.pets[0],
          petType:data?.pets[0]?.petType?.value,
          breedType:data?.pets[0]?.breedType?.value,
          petGender: data?.pets[0]?.petGender?.name,
        },
          
        address: {
          ...data?.address,
          city:data?.address?.city?.name,
          locality: { code: data?.address?.locality?.code, area: data?.address?.locality?.area },

        },


        documents: data?.documents?.documents,

        workflow : {
          businessService: "ptr",   // required
          action : "APPLY",   //required
          moduleName: "pet-services" //required
        }
          
      }];

      history.replace("/digit-ui/employee/ptr/petservice/response", { PetRegistrationApplications: formData }); 
      

    };

    
    const configs = commonFields? commonFields: newConfig;    


    
    return (
      <FormComposer
        heading={t("ES_TITLE_NEW_PET_REGISTARTION")}
        isDisabled={!canSubmit}
        label={t("ES_COMMON_APPLICATION_SUBMIT")}
        config={configs.map((config) => {
        
          return {
            ...config,
            body: config.body.filter((a) => !a.hideInEmployee),
          };
        })}
        fieldStyle={{ marginRight: 0 }}
        onSubmit={onPetSubmit}
        defaultValues={defaultValues}
        onFormValueChange={onFormValueChange}
      
      />
    );
  };

  export default NewApplication;