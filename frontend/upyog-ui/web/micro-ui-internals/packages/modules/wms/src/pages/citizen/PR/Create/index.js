import { FormComposer, Loader,Header } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../../components/config/pr-config";
import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";
const WmsPrCreate = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();  
  const [isLoading, setIsLoading] = useState(false);
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const onSubmit = (data) => {
    setIsLoading(true);
    let WMSProjectRegisterApplications = 
      {
        WMSProjectRegisterApplication: [{
          scheme_name: data?.WmsPrSchName?.scheme_name, 
          project_name: data?.WmsPrPrjName?.project_name, 
          work_type: data?.WmsPrTypeOfWork?.work_type,  
          work_name: data?.WmsPrWorkName?.work_name,  
          estimated_number: data?.WmsPrEstNumber?.estimated_number,  
          estimated_work_cost: data?.WmsPrEstWorkCost?.estimated_work_cost,  
          sanctioned_tender_amount: data?.WmsPrSTA?.sanctioned_tender_amount,
          status_name: data?.WmsPrStatus?.status_name,
          bill_received_till_date: data?.WmsPrBillDate?.bill_received_till_date,
          tenantId:tenantId
        }],
      };
    
      /* use customiseCreateFormData hook to make some chnages to the Employee object [0].PreduleOfRateApplication*/
     Digit.WMSService.PRApplications.create(WMSProjectRegisterApplications, tenantId).then((result,err)=>{
      setIsLoading(false);
       history.push("/upyog-ui/citizen/wms/pr-home");
     })
     .catch((e) => {
     console.log("err");
    });
  };

  

  if (isLoading) {
    return <Loader />;
  }
  const configs = newConfig?newConfig:newConfig;

  return (
    <div>
    <Header>{t("WMS_NEW_PR_FORM_HEADER")}</Header>
    <FormComposer
              head={t("WMS_PR_FORM_CREATE_HEAD")}
              label={t("WMS_COMMON_SAVE")}
              config={configs}
              onSubmit={onSubmit}
              fieldStyle={{ marginRight: 0 }}
            />
            {showToast && (
        <Toast
          error={showToast.key}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
            </div>
  );
};

export default WmsPrCreate;
