import { FormComposer, Loader,Toast } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { disposeConfig } from "../../../config/Create/disposeConfig";
import {convertStringToFloat} from "../../../utils/index"

const DisposeAssetApplication = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [showToast,setShowToast]=useState();
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

    // Toast cleanup (hide after 2 seconds)
    useEffect(() => {
      if (showToast) {
        const timer = setTimeout(() => {
          setShowToast(null);
        }, 2000);
  
        return () => clearTimeout(timer);
      }
    }, [showToast]);
  

  const onFormValueChange = (setValue, formData, formState) => {
    //  setSubmitValve(!Object.keys(formState.errors).length); 
     setSubmitValve(true); 
  };

  const onSubmit = async (data) => {

    const formData = {
        disposalId:"",
        assetId: data?.disposeDetails?.[0]?.assetId,
        tenantId: tenantId,
        lifeOfAsset: data?.disposeDetails?.[0]?.lifeOfAsset,
        isAssetDisposedInFacility: data?.disposeDetails?.[0]?.isAssetDisposedInFacility,
        currentAgeOfAsset: data?.disposeDetails?.[0]?.currentAgeOfAsset,
        disposalDate: convertToEpoch(data?.disposeDetails?.[0]?.disposalDate),
        reasonForDisposal: data?.disposeDetails?.[0]?.reasonForDisposal?.code,
        amountReceived: convertStringToFloat(data?.disposeDetails?.[0]?.amountReceived),
        purchaserName:data?.disposeDetails?.[0]?.purchaserName, 
        paymentMode: data?.disposeDetails?.[0]?.paymentMode,
        receiptNumber: data?.disposeDetails?.[0]?.receiptNumber,
        comments: data?.disposeDetails?.[0]?.comments,
        glCode: '111111',
        assetDisposalStatus: "DISPOSED",
        additionalDetails: {
          approvalNumber: "APPROVAL202501",
          disposedBy: "Jane Smith"
        },
        auditDetail: {
            createdBy: "admin",
            createdTime: 1672531200000,
            lastModifiedBy: "admin",
            lastModifiedTime: 1672534800000
        },
        documents: [
          {
              documentType: "ASSET.DISPOSE.DOC1",
              fileStoreId: data?.disposeDetails?.[0]?.fileStoreId,
              documentUid: data?.disposeDetails?.[0]?.fileStoreId,
              latitude: null,
              longitude: null
          }
      ]
    };
    
    try {
          const applicationDetails = await Digit.ASSETService.assetDisposedCreate({
            AssetDisposal: formData
          });
          if(applicationDetails){
            console.log('success data is coming')
            // setShowToast({ error: false, label: 'Asset Dispose Successfully'});
            history.replace("/upyog-ui/employee/asset/assetservice/asset-dispose-response", { AssetDisposal: applicationDetails, applicationNo }); 
          }
        }
        catch (error) {
          setShowToast({ error: true, label: t("CS_SOMETHING_WENT_WRONG") });
          }
        };
    
 const configs = disposeConfig;    
  return (
    <div>
      <FormComposer
        heading={t("AST_DISPOSE_ASSET")}
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
    {showToast && (
              <Toast
                error={showToast.error}
                warning={showToast.warning}
                label={t(showToast.label)}
                onClose={() => {
                  setShowToast(null);
                }}
              />
      )}
      </div>
  );
};

export default DisposeAssetApplication;



