import { FormComposer, Loader,Header } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../../components/config/mb-config";
import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";
import Timeline from "../../../../components/MB/Timeline";
const WmsMbCreate = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();  
  const [isLoading, setIsLoading] = useState(false);
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const onSubmit = (data) => {
    //alert(JSON.stringify(data));
    setIsLoading(true);
    let MeasurementBookApplications = 
      {
        MeasurementBookApplication: [{
          description_of_item: data?.WmsMbDescriptionOfItem?.name,  
          chapter: data?.WmsMbChapter?.name,       
          mb_date: convertEpochToDate(data?.WmsMbStartDate?.name),
          tenantId:tenantId
        }],
      };
    
      /* use customiseCreateFormData hook to make some chnages to the Employee object [0].MeasurementBookApplication*/
     Digit.WMSService.MBApplications.create(MeasurementBookApplications.MeasurementBookApplication[0], tenantId).then((result,err)=>{
      setIsLoading(false);
       history.push("/upyog-ui/citizen/wms/mb-home");
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
    <Header>{t("WMS_NEW_MB_FORM_HEADER")}</Header>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={1}  /> : null}

    <FormComposer
              head={t("WMS_MB_FORM_CREATE_HEAD")}
              label={t("WMS_COMMON_SAVE")}
              config={configs}
              onSubmit={onSubmit}
              fieldStyle={{ marginRight: 0 }}
            />

    <FormComposer
              head={t("WMS_MB_FORM_CREATE_HEAD")}
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

export default WmsMbCreate;
