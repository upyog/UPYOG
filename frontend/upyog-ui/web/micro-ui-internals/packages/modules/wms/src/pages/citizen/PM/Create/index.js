import { FormComposer, Loader,Header } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../../components/config/pm-config";

const Create = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();  
  const [isLoading, setIsLoading] = useState(false);
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const onSubmit = (data) => {
    
    setIsLoading(true);
    let PhysicalMilestones = 
      {
        PhysicalMilestone: [{
          project_name: data?.WmsPmPrjName?.project_name,
          ml_name: data?.WmsPmMlName?.ml_name,  
          work_name: data?.WmsPmWrkName?.work_name,
          percent: data?.WmsPmPer?.percent, 
          tenantId:tenantId
        }],
      };
      /* use customiseCreateFormData hook to make some chnages to the Employee object [0].PhysicalMilestone*/
     Digit.WMSService.PMApplications.create(PhysicalMilestones, tenantId).then((result,err)=>{
      setIsLoading(false);
      let getdata = {...data , get: result }
       onSelect("", getdata, "", true);
       console.log("daaaa",getdata);
       history.push("/digit-ui/citizen/wms/pm-home");
     })
     .catch((e) => {
     console.log("err");
    });
  };

  /* use newConfig instead of commonFields for local development in case needed */
  const checkMailNameNum = (formData) => {
    const percent = formData?.WmsSorItemNo?.percent || '';
    const validPercent = percent.length == 0 ? true : percent.match(Digit.Utils.getPattern('Amount'));
   return validPercent;
  }
  const onFormValueChange = (setValue = true, formData) => {    
    if (
      checkMailNameNum(formData)
    ) {
      setSubmitValve(true);
    } else {
      setSubmitValve(false);
    }
  };

  if (isLoading) {
    return <Loader />;
  }
  const configs = newConfig?newConfig:newConfig;

  return (
    <div>
    <Header>{t("WMS_NEW_PM_FORM_HEADER")}</Header>
    <FormComposer
              head={t("WMS_PM_FORM_CREATE_HEAD")}
              label={t("WMS_COMMON_SAVE")}
              config={configs}
              onSubmit={onSubmit}
              fieldStyle={{ marginRight: 0 }}
              onFormValueChange={onFormValueChange}
              isDisabled={!canSubmit}
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

export default Create;
import { FormComposer, Loader,Header } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
// import { newConfig } from "../../../../components/config/pm-config";
import { newConfig } from "../../../../components/config/pm-config";


const Create = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();  
  const [isLoading, setIsLoading] = useState(false);
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const onSubmit = (data) => {
    
    setIsLoading(true);
    let PhysicalMilestones = 
      {
        PhysicalMilestone: [{
          project_name: data?.WmsPmPrjName?.project_name,
          ml_name: data?.WmsPmMlName?.ml_name,  
          work_name: data?.WmsPmWrkName?.work_name,
          percent: data?.WmsPmPer?.percent, 
          tenantId:tenantId
        }],
      };
      /* use customiseCreateFormData hook to make some chnages to the Employee object [0].PhysicalMilestone*/
     Digit.WMSService.PMApplications.create(PhysicalMilestones, tenantId).then((result,err)=>{
      setIsLoading(false);
      let getdata = {...data , get: result }
       onSelect("", getdata, "", true);
       console.log("daaaa",getdata);
       history.push("/digit-ui/citizen/wms/pm-home");
     })
     .catch((e) => {
     console.log("err");
    });
  };

  /* use newConfig instead of commonFields for local development in case needed */
  const checkMailNameNum = (formData) => {
    const percent = formData?.WmsSorItemNo?.percent || '';
    const validPercent = percent.length == 0 ? true : percent.match(Digit.Utils.getPattern('Amount'));
   return validPercent;
  }
  const onFormValueChange = (setValue = true, formData) => {    
    if (
      checkMailNameNum(formData)
    ) {
      setSubmitValve(true);
    } else {
      setSubmitValve(false);
    }
  };

  if (isLoading) {
    return <Loader />;
  }
  const configs = newConfig?newConfig:newConfig;

  return (
    <div>
    <Header>{t("WMS_NEW_PM_FORM_HEADER")}</Header>
    <FormComposer
              head={t("WMS_PM_FORM_CREATE_HEAD")}
              label={t("WMS_COMMON_SAVE")}
              config={configs}
              onSubmit={onSubmit}
              fieldStyle={{ marginRight: 0 }}
              onFormValueChange={onFormValueChange}
              isDisabled={!canSubmit}
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

export default Create;
