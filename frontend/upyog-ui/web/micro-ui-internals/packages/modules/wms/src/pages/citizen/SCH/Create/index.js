import { FormComposer, Loader,Header } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../../components/config/sch-config";
import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";
const WmsSchCreate = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();  
  const [isLoading, setIsLoading] = useState(false);
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const onSubmit = (data) => {
    setIsLoading(true);
    let SchemeApplications = 
      {
        SchemeApplication: [{
          scheme_name_en: data?.WmsSchNameEn?.scheme_name_en,
          scheme_name_reg: data?.WmsSchNameReg?.scheme_name_reg,  
          fund: data?.WmsSchFund?.fund,
          source_of_fund: data?.WmsSchSourceOfFund?.source_of_fund,        
          description_of_the_scheme: data?.WmsSchDescriptionOfScheme?.description_of_the_scheme ,
          start_date: convertEpochToDate(data?.WmsSchStartDate?.start_date),
          end_date: convertEpochToDate(data?.WmsSchEndDate?.end_date),
          tenantId:tenantId
        }],
      };
    
      /* use customiseCreateFormData hook to make some chnages to the Employee object [0].ScheduleOfRateApplication*/
     Digit.WMSService.SCHApplications.create(SchemeApplications.SchemeApplication[0], tenantId).then((result,err)=>{
      setIsLoading(false);
       history.push("/upyog-ui/citizen/wms/sch-home");
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
    <Header>{t("WMS_NEW_SCH_FORM_HEADER")}</Header>
    <FormComposer
              head={t("WMS_SCH_FORM_CREATE_HEAD")}
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

export default WmsSchCreate;
