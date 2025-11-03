import { FormComposer, Loader } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams, useLocation } from "react-router-dom";
import { editAssetRegistryConfig } from "../../../../config/Create/editAssetRegistryConfig";

const EditAssetRegistryUp = () => {
  const [categoriesWiseData, setCategoriesWiseData] = useState();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();
  const { t } = useTranslation();
  const [canSubmit, setCanSubmit] = useState(false);
  const defaultValues = {};
  const history = useHistory();
  const { id: applicationNo } = useParams();
  const location = useLocation();
  // Use same hook as EditAsset module
  const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationSearch(t, tenantId, applicationNo);
  const [isLoading, setIsLoading] = useState(true);

  const stateResponseObject = Digit.Hooks.useCustomMDMS(stateTenantId, "ASSET", [{ name: "AssetParentCategoryFields" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["AssetParentCategoryFields"];
      return formattedData;
    },
  });

  useEffect(() => {
    let combinedData;
    // if city level master is not available then fetch  from state-level
    if (stateResponseObject?.data) {
      combinedData = stateResponseObject.data;
    } else {
      combinedData = []; // Or an appropriate default value for empty data
      console.log("StateResponseObject data is not unavailable.");
    }
    setCategoriesWiseData(combinedData);
  }, [stateResponseObject]);

  useEffect(() => {
    if (applicationDetails) {
      setIsLoading(false);
    }
  }, [applicationDetails]);

  const [_formData, setFormData, _clear] = Digit.Hooks.useSessionStorage("store-data", null);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", {});

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

  const convertToEpoch = (dateString) => {
    const [year, month, day] = dateString.split("-").map(Number);
    return new Date(year, month - 1, day).getTime();
  };

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);

  const onFormValueChange = (setValue, formData, formState) => {
    setCanSubmit(!Object.keys(formState.errors).length);
  };
  const keysToRemove = [
    "acquisitionCost",
    "acquisitionDate",
    "assetDescription",
    "assetName",
    "assetUniqueNo",
    "assetclassification",
    "assetsubtype",
    "assettype",
    "otherCategory",
  ];
  const onSubmit = (data) => {
    console.log("data on Submit:- ", data);
    const assetDetails = data?.editAssetRegistry || {};
    let formJson = [];
    if (Array.isArray(categoriesWiseData)) {
      // Filter categories based on the selected assetParentCategory
      formJson = categoriesWiseData
        .filter((category) => {
          const isMatch = category.assetParentCategory === assetDetails?.assettype?.code;
          return isMatch;
        })
        .map((category) => category.fields) // Extract the fields array
        .flat() // Flatten the fields array
        .filter((field) => field.active === true) // Filter by active status
        .map((field) => field.name); // Extract only the name field
    }

    const filteredAdditionalDetails = Object.keys(assetDetails)
      .filter((key) => !keysToRemove.includes(key)) // Remove unwanted keys
      .reduce((acc, key) => {
        acc[key] = assetDetails[key]; // Add remaining keys to the accumulator
        return acc;
      }, {});

    const filteredAdditionalDetailsRefine = Object.keys(filteredAdditionalDetails)
      .filter((key) => formJson.includes(key)) // getting only wanted keys
      .reduce((acc, key) => {
        acc[key] = assetDetails[key]; // Add only required keys to the accumulator
        return acc;
      }, {});

    const editData = assetDetails;
    const formData = {
      id: applicationDetails?.applicationData?.applicationData?.id,
      tenantId: tenantId,
      applicationNo: applicationNo,
      assetName: editData?.assetName,
      description: editData?.assetDescription,
      assetClassification: editData?.assetclassification?.code,
      assetParentCategory: editData?.assettype?.code,
      assetCategory: editData?.assetsubtype?.code,
      assetSubCategory: editData?.otherCategory,
      purchaseDate: convertDateToEpoch(editData?.purchaseDate || editData?.acquisitionDate),
      acquisitionCost: editData?.acquisitionCost,
      district: "Test District",
      division: "Test DDR",
      status: "ACTIVE",
      additionalDetails: filteredAdditionalDetailsRefine,

      workflow: {
        action: "SUBMIT",
        businessService: "asset-create",
        moduleName: "asset-services",
      },
    };

    // history.replace("/upyog-ui/employee/asset/assetservice-up/update-response", { Assets: formData, applicationNo });
    history.replace("/upyog-ui/employee/asset/assetservice/edit-response", { Assets: formData });
  };

  if (isLoading) {
    return <Loader />;
  }

  const configs = editAssetRegistryConfig;

  return (
    <FormComposer
      heading={t("EDIT_ASSET_REGISTRY")}
      // isDisabled={!canSubmit}
      label={t("ES_COMMON_APPLICATION_SUBMIT")}
      config={configs.map((config) => {
        return {
          ...config,
          body: config.body.filter((a) => !a.hideInEmployee),
        };
      })}
      fieldStyle={{ marginRight: 0 }}
      cardStyle={{ Width: 60 }}
      onSubmit={onSubmit}
      defaultValues={
        applicationDetails
          ? {
              editAssetRegistry: [
                {
                  assetName: applicationDetails?.applicationData?.applicationData?.assetName,
                  assetDescription: applicationDetails?.applicationData?.applicationData?.description,
                  assetUniqueNo: applicationDetails?.applicationData?.applicationData?.assetUniqueNo,
                  assetclassification: {
                    code: applicationDetails?.applicationData?.applicationData?.assetClassification,
                    value: applicationDetails?.applicationData?.applicationData?.assetClassification,
                  },
                  assettype: {
                    code: applicationDetails?.applicationData?.applicationData?.assetParentCategory,
                    value: applicationDetails?.applicationData?.applicationData?.assetParentCategory,
                  },
                  assetsubtype: {
                    code: applicationDetails?.applicationData?.applicationData?.assetCategory,
                    value: applicationDetails?.applicationData?.applicationData?.assetCategory,
                  },
                  otherCategory: applicationDetails?.applicationData?.applicationData?.assetSubCategory,
                  department: {
                    code: applicationDetails?.applicationData?.applicationData?.department,
                    value: applicationDetails?.applicationData?.applicationData?.department,
                  },
                  purchaseDate: applicationDetails?.applicationData?.applicationData?.purchaseDate,
                  acquisitionDate: applicationDetails?.applicationData?.applicationData?.purchaseDate,
                  acquisitionCost: applicationDetails?.applicationData?.applicationData?.acquisitionCost,

                  applicationNo: applicationDetails?.applicationData?.applicationData?.applicationNo,
                  registrationNumber: applicationDetails?.applicationData?.applicationData?.additionalDetails?.registrationNumber,
                  engineNumber: applicationDetails?.applicationData?.applicationData?.additionalDetails?.engineNumber,
                  chasisNumber: applicationDetails?.applicationData?.applicationData?.additionalDetails?.chasisNumber,
                  eWayBillNo: applicationDetails?.applicationData?.applicationData?.additionalDetails?.eWayBillNo,
                  brand: applicationDetails?.applicationData?.applicationData?.additionalDetails?.brand,
                  quantity: applicationDetails?.applicationData?.applicationData?.additionalDetails?.quantity,

                  lengthInMeter: applicationDetails?.applicationData?.applicationData?.additionalDetails?.lengthInMeter,
                  startLatitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.startLatitude,
                  startLongitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.startLongitude,
                  endLatitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.endLatitude,
                  endLongitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.endLongitude,
                  karyansh: applicationDetails?.applicationData?.applicationData?.additionalDetails?.karyansh,
                  budgetHead: applicationDetails?.applicationData?.applicationData?.additionalDetails?.budgetHead,
                  vehicleUsageType: applicationDetails?.applicationData?.applicationData?.additionalDetails?.vehicleUsageType,
                  useOfProperty: applicationDetails?.applicationData?.applicationData?.additionalDetails?.useOfProperty,
                  ownedBy: applicationDetails?.applicationData?.applicationData?.additionalDetails?.ownedBy,
                  latitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.latitude,
                  longitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.longitude,
                  litigation: applicationDetails?.applicationData?.applicationData?.additionalDetails?.litigation,
                  gataSankhya: applicationDetails?.applicationData?.applicationData?.additionalDetails?.gataSankhya,
                  areaInSquareMeter: applicationDetails?.applicationData?.applicationData?.additionalDetails?.areaInSquareMeter,
                  landRestrictionStatus: applicationDetails?.applicationData?.applicationData?.additionalDetails?.landRestrictionStatus,
                  landOwnershipType: applicationDetails?.applicationData?.applicationData?.additionalDetails?.landOwnershipType,
                  startLatitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.startLatitude,
                  startLongitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.startLongitude,
                  endLatitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.endLatitude,
                  endLongitude: applicationDetails?.applicationData?.applicationData?.additionalDetails?.endLongitude,
                },
              ],
            }
          : {}
      }
      onFormValueChange={onFormValueChange}
    />
  );
};

export default EditAssetRegistryUp;
