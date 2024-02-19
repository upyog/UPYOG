import { FormComposer, Loader,Header } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import {newConfig} from "../../../../components/config/pma-config";

import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";
const WmsPmaCreate = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();  
  const [isLoading, setIsLoading] = useState(false);
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const onSubmit = (data) => {
    setIsLoading(true);
    let WMSPhysicalMileStoneActivityApplications = 
      {
        WMSPhysicalMileStoneActivityApplication: [{
          description_of_the_item: data?.WmsPmaDescriptionOfItem?.description_of_the_item, 
          percentage_weightage: data?.WmsPmaPercent?.percentage_weightage, 
          start_date:data?.WmsPmaStartDate?.start_date,
          end_date:data?.WmsPmaEndDate?.end_date,
          tenantId:tenantId,
        }],
      };
    
      /* use customiseCreateFormData hook to make some chnages to the Employee object [0].PmaeduleOfRateApplication*/
     Digit.WMSService.PMAApplications.create(WMSPhysicalMileStoneActivityApplications, tenantId).then((result,err)=>{
      setIsLoading(false);
       history.push("/upyog-ui/citizen/wms/pma-home");
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
    <Header>{t("WMS_NEW_PMA_FORM_HEADER")}</Header>
    <FormComposer
              head={t("WMS_PMA_FORM_CREATE_HEAD")}
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

export default WmsPmaCreate;
