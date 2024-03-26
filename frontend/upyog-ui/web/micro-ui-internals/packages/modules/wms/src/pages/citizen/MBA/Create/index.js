import { FormComposer, Loader,Header } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import {newConfig} from "../../../../components/config/MBA-config";

import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";
const WmsMBACreate = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();  
  const [isLoading, setIsLoading] = useState(false);
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const onSubmit = (data) => {
    setIsLoading(true);
    let MBAemeApplications = 
      {
        MBAemeApplication: [{
          description_of_item: data?.WmsMBADescriptionOfItem?.description_of_item, 
          percent_weightage: data?.WmsMBAPercent?.percent_weightage, 
          start_date:data?.WmsMBAStartDate?.start_date,
          end_date:data?.WmsMBAEndDate?.end_date,
          tenantId:tenantId
        }],
      };
    
      /* use customiseCreateFormData hook to make some chnages to the Employee object [0].MBAeduleOfRateApplication*/
     Digit.WMSService.MBAApplications.create(MBAemeApplications.MBAemeApplication[0], tenantId).then((result,err)=>{
      setIsLoading(false);
       history.push("/upyog-ui/citizen/wms/MBA-home");
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
    <Header>{t("WMS_NEW_MBA_FORM_HEADER")}</Header>
    <FormComposer
              head={t("WMS_MBA_FORM_CREATE_HEAD")}
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

export default WmsMBACreate;
