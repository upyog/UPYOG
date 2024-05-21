import { FormComposer, Loader } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";


import { newConfig } from "../../../config/Create/Assetconfig";

const NewAssetApplication = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  
  

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

  

  const onFormValueChange = (setValue, formData, formState) => {
    
    setSubmitValve(!Object.keys(formState.errors).length); 
  };

  const onSubmit = (data) => {
    console.log("dta for payload console", data);
    const formData = {
      accountId: "",
      tenantId: tenantId,
      assetBookRefNo: data?.assetcommonforall?.[0]?.BookPagereference,
      assetName: data?.assetcommonforall?.[0]?.AssetName,
      description: data?.assetcommonforall?.[0]?.Assetdescription,
      assetClassification:data?.assets[0]?.assetclassification?.value,
      assetParentCategory: data?.assets[0]?.assettype?.code,
      department: data?.assetcommonforall?.[0]?.Department,
      applicationNo: "",
      approvalDate: "",
      applicationDate: "",
      status: "",
      action: "",
      businessService: "asset-create",

      addressDetails: {
        addressLine1:data?.address?.addressLine1,
        addressLine2:data?.address?.addressLine2,
        buildingName:data?.address?.buildingName,
        doorNo:data?.address?.doorNo,
        street:data?.address?.street,
        city:"",
        locality: { code: data?.address?.locality?.code, area: data?.address?.locality?.area },

      },
      documents: data?.documents?.documents,
      workflow : {
        action: "INITIATE",
        businessService: "asset-create",
        moduleName: "asset-services"
      },

      additionalDetails: {
        assetParentCategory: data?.assets[0]?.assettype?.value,
        assetAssetSubCategory: data?.assets[0]?.assetsubtype?.value,
        ...data?.assetscommon?.[0],

        acquisitionProsessionDetails: {
        },
      },   
    };

    history.replace("/digit-ui/employee/asset/assetservice/response", { Asset: formData }); 
    

  };
    

   
  // if (isLoading) {
  //   return <Loader />;
  // }

  /* use newConfig instead of commonFields for local development in case needed */

  
  const configs = newConfig;    

  
  return (
    <FormComposer
      heading={t("ES_TITLE_NEW_ASSET_MANAGEMENT")}
      isDisabled={canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={configs.map((config) => {
       
        return {
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
        };
      })}
      fieldStyle={{ marginRight: 0 }}
      onSubmit={onSubmit}
      defaultValues={defaultValues}
      onFormValueChange={onFormValueChange}
     
    />
  );
};

export default NewAssetApplication;



