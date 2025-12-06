import { FormComposer, Loader, Toast } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams } from "react-router-dom";
import { maintenanceConfig } from "../../../config/Create/maintenanceConfig";
import { convertDateToEpoch } from "../../../utils";

const MaintenanceAssetApplication = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const [canSubmit, setCanSubmit] = useState(false);
  const defaultValues = {};
  const history = useHistory();
  const { id: applicationNo } = useParams();
  const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);

  const [_formData, setFormData, _clear] = Digit.Hooks.useSessionStorage("store-data", null);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("EMPLOYEE_MUTATION_SUCCESS_DATA", {});


  const convertToEpoch = (dateString) => {
    const [year, month, day] = dateString.split("-").map(Number);
    return new Date(year, month - 1, day).getTime();
  };

  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
  }, []);



  const onFormValueChange = (setValue, formData, formState) => {
    console.log('here ....', !Object.keys(formState.errors).length)
    console.log('formData ....', formData)
    console.log('formState ....', formState)
    setCanSubmit(!Object.keys(formState.errors).length); 
  };

  const onSubmit = (data) => {
    
    const formData = {
        maintenanceId: "",
        assetId: data?.maintenanceDetails?.[0]?.assetId,
        tenantId: tenantId,
        currentLifeOfAsset: data?.maintenanceDetails?.[0]?.currentLifeOfAsset,
        warrantyStatus: data?.maintenanceDetails?.[0]?.warrantyStatus?.code,    
        assetWarrantyDescription: data?.maintenanceDetails?.[0]?.assetWarrantyDescription,
        amcDetails: data?.maintenanceDetails?.[0]?.amcDetails,
        assetMaintenanceDate: convertDateToEpoch(data?.maintenanceDetails?.[0]?.assetMaintenanceDate),
        assetNextMaintenanceDate: convertDateToEpoch(data?.maintenanceDetails?.[0]?.assetNextMaintenanceDate),
        maintenanceType: data?.maintenanceDetails?.[0]?.maintenanceType?.code,
        paymentType: data?.maintenanceDetails?.[0]?.paymentType?.code,
        costOfMaintenance :data?.maintenanceDetails?.[0]?.costOfMaintenance,
        description :data?.maintenanceDetails?.[0]?.description,
        vendor:data?.maintenanceDetails?.[0]?.vendor,
        maintenanceCycle:data?.maintenanceDetails?.[0]?.maintenanceCycle.code,
        partsAddedOrReplaced:data?.maintenanceDetails?.[0]?.partsAddedOrReplaced,
        preConditionRemarks: data?.maintenanceDetails?.[0]?.preConditionRemarks,
        postConditionRemarks:data?.maintenanceDetails?.[0]?.postConditionRemarks,
        isAMCExpired: data?.maintenanceDetails?.[0]?.isAMCExpired,
        isWarrantyExpired: data?.maintenanceDetails?.[0]?.isWarrantyExpired,
        isLifeOfAssetAffected:data?.maintenanceDetails?.[0]?.isLifeOfAssetAffected.code,
        assetMaintenanceIncreasedYear:data?.maintenanceDetails?.[0]?.assetMaintenanceIncreasedYear.code,
        documents: [
            {
                "documentType": "ASSET.MAINTENANCE.DOC3",
                "fileStoreId": data?.maintenanceDetails?.[0]?.supportingDocumentFile,
                "documentUid": data?.maintenanceDetails?.[0]?.supportingDocumentFile
            },
            {
                "documentType": "ASSET.MAINTENANCE.DOC1",
                "fileStoreId": data?.maintenanceDetails?.[0]?.preConditionFile,
                "documentUid": data?.maintenanceDetails?.[0]?.preConditionFile
            },
            {
              "documentType": "ASSET.MAINTENANCE.DOC2",
              "fileStoreId": data?.maintenanceDetails?.[0]?.postConditionFile,
              "documentUid": data?.maintenanceDetails?.[0]?.postConditionFile
          }
        ],
        auditDetails: {
          createdBy: "",
          createdTime: "",
          lastModifiedBy: "",
          lastModifiedTime: ""
      }
    };
    
    history.replace("/upyog-ui/employee/asset/assetservice/maintenance", { AssetMaintenance: formData,  applicationNo});

  };

const configs = maintenanceConfig;

  return (
    <FormComposer
      heading={t("AST_REPAIR_MAINTENANCE")}
      isDisabled={!canSubmit}
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
      defaultValues={defaultValues}
      onFormValueChange={onFormValueChange}

    />
  );
};

export default MaintenanceAssetApplication;



