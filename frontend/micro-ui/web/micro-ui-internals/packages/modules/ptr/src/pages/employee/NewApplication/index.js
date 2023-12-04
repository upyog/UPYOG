import { FormComposer, Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../config/Create/config";

const NewApplication = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  //const tenants = Digit.Hooks.pt.useTenants();

  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);
  const defaultValues = {};
  const history = useHistory();
  // delete
   const [_formData, setFormData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
 
   //const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
   //const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", { });
  const { data: commonFields, isLoading } = Digit.Hooks.pt.useMDMS(Digit.ULBService.getStateId(), "PropertyTax", "CommonFieldsConfig");
  // useEffect(() => {
  //   setMutationHappened(false);
  //   clearSuccessData();
  // }, []);

  const onFormValueChange = (setValue, formData, formState) => {
    
    //console.log("data",formData, setValue, setSubmitValve ) ; // to check in console
    
    setSubmitValve(!Object.keys(formState.errors).length);

    // let pincode = formData?.address?.pincode;
    // if (pincode) {
    //   if (!Digit.Utils.getPattern("Pincode").test(pincode)) setSubmitValve(false);
    //   const foundValue = tenants?.find((obj) => obj.pincode?.find((item) => item.toString() === pincode));
    //   if (!foundValue) {
    //     setSubmitValve(false);
    //   }
    // }    // no need for now as this convert pincode into a string = "141010"  
    
  };

  const onPetSubmit = (data) => {
    //console.log("dataaaaaaaaaaaaaaaaaa",data)


    

    const formData = [{
      tenantId,
      ...data?.owners[0],
      // applicantName :data?.owners[0]?.applicantName,
      // mobileNumber  :data?.owners[0]?.mobileNumber,
      // alternateNumber  :data?.owners[0]?.alternateNumber,
      // emailId  :data?.owners[0]?.emailId,
      // fatherName  :data?.owners[0]?.fatherName,
    
        
        //petDetails: data?.pets[0],
      petDetails:{
        ...data?.pets[0],
        petType:data?.pets[0]?.petType?.value,
        breedType:data?.pets[0]?.breedType?.value,
        petGender: data?.pets[0]?.petGender?.value,

      },
        
      address: {
        ...data?.address,
        city:data?.address?.city?.name,
        locality: { code: data?.address?.locality?.code, area: data?.address?.locality?.area },

      },

        //address: data?.address[0],
       

         // Below lines are important as these are 

        // channel: "CFC_COUNTER", // required
        // creationReason: "CREATE", // required
        // applicationSTATUS: "CREATE" // required
        // source: "MUNICIPAL_RECORDS", // required

      documents: data?.documents?.documents,

      workflow : {
        businessService: "ptr",
        action : "APPLY",
        moduleName: "pet-services"
      }
        
    }];

    

   
    //console.log("ddddddddddddddddddddd",formData )
    history.replace("/digit-ui/employee/pt/response", { PetRegistrationApplications: formData }); //current wala
    //history.replace("/digit-ui/employee/pt/response", {PetRegistrationApplications:formData});
    

  };
  if (isLoading) {
    return <Loader />;
  }

  /* use newConfig instead of commonFields for local development in case needed */

  // const configs = commonFields?commonFields:newConfig;
  const configs = commonFields? newConfig: commonFields;    

  
  return (
    <FormComposer
      heading={t("ES_TITLE_NEW_PET_REGISTARTION")}
      //heading="Pet Registration"
      //isDisabled={canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={configs.map((config) => {
        //console.log("jjjjjjjjjjjj",config)
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