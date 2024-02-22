import { FormComposer, Loader,Header } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../../components/config/wsr-config";
import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";
const WmsWsrCreate = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();  
  const [isLoading, setIsLoading] = useState(false);
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const onSubmit = (data) => {
    setIsLoading(true);
    let WMSWorkStatusReportApplications = 
      {
        WMSWorkStatusReportApplication: [{
          project_name: data?.WmsWsrPrjName?.project_name,  
          work_name: data?.WmsWsrWorkName?.work_name,  
          activity_name: data?.WmsWsrActivity?.activity_name,
          employee_name: data?.WmsWsrEmployee?.employee_name,
          role_name: data?.WmsWsrRole?.role_name,
          start_date:data?.WmsWsrStartDate?.start_date,
          end_date:data?.WmsWsrEndDate?.end_date,
          remarks_content: data?.WmsWsrRemarks?.remarks_content,
          tenantId:tenantId
        }],
      };
    
      /* use customiseCreateFormData hook to make some chnages to the Employee object [0].WsreduleOfRateApplication*/
     Digit.WMSService.WSRApplications.create(WMSWorkStatusReportApplications, tenantId).then((result,err)=>{
      setIsLoading(false);
       history.push("/upyog-ui/citizen/wms/wsr-home");
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
    <Header>{t("WMS_NEW_WSR_FORM_HEADER")}</Header>
    <FormComposer
              head={t("WMS_WSR_FORM_CREATE_HEAD")}
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

export default WmsWsrCreate;
