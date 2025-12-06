import { FormComposer, Loader } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams } from "react-router-dom";

import { editConfig } from "../../../config/Create/editConfig";

const EditAsset = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const [canSubmit, setSubmitValve] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  
  const history = useHistory();
  const { id: applicationNo } = useParams();
  const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);
   const [_formData, setFormData,_clear] = Digit.Hooks.useSessionStorage("store-data",null);
   const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", { });


  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);

  useEffect(() => {
    if (applicationDetails) {
      setIsLoading(false);
    }
  }, [applicationDetails]);
  
  if (isLoading) {
    return <Loader />;
  }


  
const onFormValueChange = (setValue, formData, formState) => {
    setSubmitValve(!Object.keys(formState.errors).length); 
  };

  /* methid to get date from epoch */
 const convertDateToEpoch = (dateString) => {
  // Parse the date string into a Date object
  const date = new Date(dateString);

  // Check if the date is valid
  if (!isNaN(date)) {
    // Return the epoch time in seconds (divide by 1000 to convert from milliseconds to seconds)
    return Math.floor(date.getTime() / 1000);
  } else {
    return null; // Return null if the input date is invalid
  }
};
const convertStringToFloat = (amountString) => {
  // Remove commas if present and convert to float
  const cleanedString = String(amountString).replace(/,/g, '');
  
  // Convert to float and return
  const floatValue = parseFloat(cleanedString);
  
  // Return the float value, or NaN if conversion fails
  return isNaN(floatValue) ? null : floatValue;
};


  const onSubmit = (data) => {
    
    const formData = {
      accountId: applicationDetails?.applicationData?.applicationData?.accountId,
      tenantId: applicationDetails?.applicationData?.applicationData?.tenantId,
      assetBookRefNo: data?.editAssignDetails?.[0]?.BookPagereference,
      assetName: data?.editAssignDetails?.[0]?.AssetName,
      description: data?.editAssignDetails?.[0]?.Assetdescription,
      assetClassification:data?.editAssignDetails?.[0]?.assetclassification?.code,
      assetParentCategory: data?.editAssignDetails?.[0]?.assettype?.code,
      assetCategory:data?.editAssignDetails?.[0]?.assetsubtype?.code,
      assetSubCategory:data?.editAssignDetails?.[0]?.assetparentsubCategory?.code,
      department: data?.editAssignDetails?.[0]?.Department?.code,
      assetType: data?.editAssignDetails?.[0]?.assetType?.code,
      assetUsage: data?.editAssignDetails?.[0]?.assetUsage?.code,
      modeOfPossessionOrAcquisition: data?.editAssignDetails?.[0]?.modeOfPossessionOrAcquisition?.code,
      invoiceDate: convertDateToEpoch(data?.editAssignDetails?.[0]?.invoiceDate), 
      invoiceNumber: data?.editAssignDetails?.[0]?.invoiceNumber,
      purchaseDate: convertDateToEpoch(data?.editAssignDetails?.[0]?.purchaseDate),
      purchaseOrderNumber: data?.editAssignDetails?.[0]?.purchaseOrderNumber,
      lifeOfAsset: data?.editAssignDetails?.[0]?.lifeOfAsset,
      location: data?.editAssignDetails?.[0]?.location,
      purchaseCost: convertStringToFloat(data?.editAssignDetails?.[0]?.purchaseCost),
      acquisitionCost: convertStringToFloat(data?.editAssignDetails?.[0]?.acquisitionCost),
      bookValue: convertStringToFloat(data?.editAssignDetails?.[0]?.bookValue),
      financialYear: data?.editAssignDetails?.[0]?.financialYear?.code,
      sourceOfFinance: data?.editAssignDetails?.[0]?.sourceOfFinance?.code,
      applicationNo: applicationDetails?.applicationData?.applicationData?.applicationNo,
      approvalDate: "",
      id:applicationDetails?.applicationData?.applicationData?.id,
      applicationDate: "",
      status: applicationDetails?.applicationData?.applicationData?.status,
      action: "",
      businessService: "asset-create",

      addressDetails: {
        addressLine1:applicationDetails?.applicationData?.applicationData?.addressDetails?.addressLine1,
        addressLine2:applicationDetails?.applicationData?.applicationData?.addressDetails?.addressLine2,
        buildingName:applicationDetails?.applicationData?.applicationData?.addressDetails?.buildingName,
        doorNo:applicationDetails?.applicationData?.applicationData?.addressDetails?.doorNo,
        street:applicationDetails?.applicationData?.applicationData?.addressDetails?.street,
        pincode:applicationDetails?.applicationData?.applicationData?.addressDetails?.pincode,
        city:applicationDetails?.applicationData?.applicationData?.addressDetails?.city,
        locality: { code: applicationDetails?.applicationData?.applicationData?.addressDetails?.locality?.code,
                    area: applicationDetails?.applicationData?.applicationData?.addressDetails?.locality?.area,
                    latitude: applicationDetails?.applicationData?.applicationData?.addressDetails?.latitude,
                    longitude: applicationDetails?.applicationData?.applicationData?.addressDetails?.longitude },

      },
      documents: applicationDetails?.applicationData?.applicationData?.documents,
      workflow : {
        action: "SUBMIT",
        businessService: "asset-create",
        moduleName: "asset-services"
      },

      additionalDetails: {
        ...data?.editNewAssetDetails?.[0],

        acquisitionProcessionDetails: {
        },
      }, 
    };
    console.log('Form Data:- ', formData);

    history.replace("/upyog-ui/employee/asset/assetservice/edit-response", { Assets: formData }); 
    

  };
    
const configs = editConfig;    

  
  return (
    <FormComposer
      heading={t("AST_EDIT_ASSET")}
      // isDisabled={canSubmit}
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
      onFormValueChange={onFormValueChange}
    />
  );
};

export default EditAsset;



