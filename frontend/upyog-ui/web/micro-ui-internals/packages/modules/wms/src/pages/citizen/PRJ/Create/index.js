import { FormComposer, Loader,Header } from "@egovernments/digit-ui-react-components";
import React, {  useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { newConfig } from "../../../../components/config/prj-config";
import { convertEpochToDate, pdfDownloadLink } from "../../../../components/Utils";
const PrjCreate = () => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const history = useHistory();  
  const [isLoading, setIsLoading] = useState(false);
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);
  const onSubmit = (data) => {    
    setIsLoading(true);
    let ProjectApplications = 
      {
        ProjectApplication: [{
          project_name_en: data?.WmsPrjNameEn?.project_name_en ,
          project_name_reg: data?.WmsPrjNameReg?.project_name_reg ,
          project_number: data?.WmsPrjNumber?.project_number ,
          project_description: data?.WmsPrjDescription?.project_description ,
          approval_number: data?.WmsPrjApprovalNo?.approval_number ,
          department: data?.WmsPrjDepartment?.department ,
          project_timeline: data?.WmsPrjTimeLine?.project_timeline ,
          scheme_name: data?.WmsPrjSchemeName?.scheme_name ,
          scheme_no: data?.WmsPrjSchemeNo?.scheme_no ,
          source_of_fund: data?.WmsPrjSourceOfFund?.source_of_fund ,
          approval_date: convertEpochToDate(data?.WmsPrjApprovalDate?.approval_date),
          project_start_date: convertEpochToDate(data?.WmsPrjStartDate?.project_start_date),
          project_end_date: convertEpochToDate(data?.WmsPrjEndDate?.project_end_date),
          status: data?.WmsPrjStatus?.status,
          tenantId:tenantId
        }],
      };
      /* use customiseCreateFormData hook to make some chnages to the Employee object [0].ProjectApplication*/
     Digit.WMSService.ProjectApplications.create(ProjectApplications.ProjectApplication[0], tenantId).then((result,err)=>{
      setIsLoading(false);
       history.push("/upyog-ui/citizen/wms/prj-home");
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
    <Header>{t("WMS_NEW_PRJ_FORM_HEADER")}</Header>
    <FormComposer
              head={t("WMS_PRJ_FORM_CREATE_HEAD")}
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

export default PrjCreate;
