import { FormComposer, Loader } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useParams, useLocation } from "react-router-dom";
import { editMaintenanceConfig } from "../../../config/Create/editMaintenanceConfig";
import { convertDateToEpoch } from "../../../utils";

const EditAssetMaintenance = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const [canSubmit, setCanSubmit] = useState(false);
  const defaultValues = {};
  const history = useHistory();
  const { id: applicationNo } = useParams();
  const location = useLocation();
  const assetMaintainanceData = location.state?.data;
  console.log('comming Data from Edit Data:- ', assetMaintainanceData);
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
    console.log("onClick data without submit: ", data)
    const formData = {
        maintenanceId: assetMaintainanceData?.maintenanceId,
        assetId: assetMaintainanceData?.assetId,
        tenantId: tenantId,
        currentLifeOfAsset: data?.editMaintenanceDetails?.[0]?.currentLifeOfAsset,
        warrantyStatus: data?.editMaintenanceDetails?.[0]?.warrantyStatus?.code,    
        assetWarrantyDescription: data?.editMaintenanceDetails?.[0]?.assetWarrantyDescription,
        amcDetails: data?.editMaintenanceDetails?.[0]?.amcDetails,
        assetMaintenanceDate: convertDateToEpoch(data?.editMaintenanceDetails?.[0]?.assetMaintenanceDate),
        assetNextMaintenanceDate: convertDateToEpoch(data?.editMaintenanceDetails?.[0]?.assetNextMaintenanceDate),
        maintenanceType: data?.editMaintenanceDetails?.[0]?.maintenanceType?.code,
        paymentType: data?.editMaintenanceDetails?.[0]?.paymentType?.code,
        costOfMaintenance :data?.editMaintenanceDetails?.[0]?.costOfMaintenance,
        description :data?.editMaintenanceDetails?.[0]?.description,
        vendor:data?.editMaintenanceDetails?.[0]?.vendor,
        maintenanceCycle:data?.editMaintenanceDetails?.[0]?.maintenanceCycle.code,
        partsAddedOrReplaced:data?.editMaintenanceDetails?.[0]?.partsAddedOrReplaced,
        preConditionRemarks: data?.editMaintenanceDetails?.[0]?.preConditionRemarks,
        postConditionRemarks:data?.editMaintenanceDetails?.[0]?.postConditionRemarks,
        isAMCExpired: data?.editMaintenanceDetails?.[0]?.isAMCExpired,
        isWarrantyExpired: data?.editMaintenanceDetails?.[0]?.isWarrantyExpired,
        isLifeOfAssetAffected:data?.editMaintenanceDetails?.[0]?.isLifeOfAssetAffected.code,
        assetMaintenanceIncreasedYear:data?.editMaintenanceDetails?.[0]?.assetMaintenanceIncreasedYear.code,
        documents: [
            {
                "documentType": "ASSET.MAINTENANCE.DOC3",
                "fileStoreId": data?.editMaintenanceDetails?.[0]?.supportingDocumentFile,
                "documentUid": data?.editMaintenanceDetails?.[0]?.supportingDocumentFile
            },
            {
                "documentType": "ASSET.MAINTENANCE.DOC1",
                "fileStoreId": data?.editMaintenanceDetails?.[0]?.preConditionFile,
                "documentUid": data?.editMaintenanceDetails?.[0]?.preConditionFile
            },
            {
              "documentType": "ASSET.MAINTENANCE.DOC2",
              "fileStoreId": data?.editMaintenanceDetails?.[0]?.postConditionFile,
              "documentUid": data?.editMaintenanceDetails?.[0]?.postConditionFile
          }
        ],
        auditDetails: {
          createdBy: "",
          createdTime: "",
          lastModifiedBy: "",
          lastModifiedTime: ""
      }
    };
    
    history.replace("/upyog-ui/employee/asset/assetservice/edit-maintenance", { AssetMaintenance: formData,  applicationNo});

  };

const configs = editMaintenanceConfig;

  return (
    <FormComposer
      heading={t("AST_EDIT_REPAIR_AND_MAINTENANCE")}
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
      defaultValues={defaultValues}
      onFormValueChange={onFormValueChange}

    />
  );
};

export default EditAssetMaintenance;



