import { FormComposer, Toast,Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { newConfig } from "../../../../components/config/prj-config";
import { convertEpochToDate } from "../../../../components/Utils";

const PrjEdit = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);

  const isupdate = Digit.SessionStorage.get("isupdate");
  const { id: projectId } = useParams();
  const { tenantId: tenantId } = useParams();

  const [checkfield, setcheck] = useState(false);
  
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("PRJ_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("PRJ_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("PRJ_WMS_MUTATION_SUCCESS_DATA", false);
  const { isLoading, isError, error, data:dataEdit, ...rest } = Digit.Hooks.wms.prj.useWmsPrjSearch({ project_id: projectId }, tenantId, null, isupdate);
  var defaultValues={};
  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
  }, []);


  const onSubmit = (input) => {

    let requestdata = Object.assign({}, data);    
    requestdata.project_name_en =  input?.WmsPrjNameEn?.project_name_en ;
    requestdata.project_name_reg =  input?.WmsPrjNameReg?.project_name_reg ;
    requestdata.project_number =  input?.WmsPrjNumber?.project_number ;
    requestdata.project_description =  input?.WmsPrjDescription?.project_description ;
    requestdata.approval_number =  input?.WmsPrjApprovalNo?.approval_number ;
    requestdata.department =  input?.WmsPrjDepartment?.department ;
    requestdata.project_timeline =  input?.WmsPrjTimeLine?.project_timeline ;
    requestdata.scheme_name =  input?.WmsPrjSchemeName?.scheme_name ;
    requestdata.scheme_no =  input?.WmsPrjSchemeNo?.scheme_no ;
    requestdata.source_of_fund =  input?.WmsPrjSourceOfFund?.source_of_fund ;
    requestdata.approval_date =  Date.parse(input?.WmsPrjApprovalDate?.approval_date);
    requestdata.project_start_date =  Date.parse(input?.WmsPrjStartDate?.project_start_date);
    requestdata.project_end_date =  Date.parse(input?.WmsPrjEndDate?.project_end_date);
    requestdata.status =  input?.WmsPrjStatus?.status;
    Digit.WMSService.ProjectApplications.update(requestdata, tenantId).then((result,err)=>{
      setIsLoading(false);
      history.push("/upyog-ui/citizen/wms/prj-home");
     })
     .catch((e) => {
     console.log("err");
     history.push("/upyog-ui/citizen/wms/prj-home");
    });};
  if (isLoading) {
    return <Loader />;
  }
  else
  {
   
    defaultValues = {      
      tenantId: tenantId,
      WmsPrjNameEn:{project_name_en: dataEdit[0]?.project_name_en },
      WmsPrjNameReg:{project_name_reg: dataEdit[0]?.project_name_reg },
      WmsPrjNumber:{ project_number: dataEdit[0]?.project_number },
      WmsPrjDescription:{project_description: dataEdit[0]?.project_description },
      WmsPrjApprovalNo:{approval_number: dataEdit[0]?.approval_number},
      WmsPrjDepartment:{department: dataEdit[0]?.department },
      WmsPrjTimeLine:{project_timeline: dataEdit[0]?.project_timeline },
      WmsPrjSchemeName:{scheme_name: dataEdit[0]?.scheme_name },
      WmsPrjSchemeNo:{scheme_no: dataEdit[0]?.scheme_no},
      WmsPrjSourceOfFund:{source_of_fund: dataEdit[0]?.source_of_fund },
      WmsPrjApprovalDate :{approval_date: convertEpochToDate(dataEdit[0]?.approval_date)},
      WmsPrjStartDate:{project_start_date: convertEpochToDate(dataEdit[0]?.project_start_date)},
      WmsPrjEndDate :{project_end_date: convertEpochToDate(dataEdit[0]?.project_end_date)},
      WmsPrjStatus :{status: dataEdit[0]?.status},
    };
   // alert(JSON.stringify(defaultValues))
  // alert(JSON.stringify(dataEdit[0]?.sor_id));
  }
  const configs = newConfig?newConfig:newConfig;
  return (
    <div>
      <FormComposer
        heading={t("WMS_PRJ_FORM_CREATE_HEAD")}
        //isDisabled={!canSubmit}
        label={t("WMS_COMMON_BUTTON_SUBMIT")}
        config={configs}
        fieldStyle={{ marginRight: 0 }}
        onSubmit={onSubmit}
        defaultValues={defaultValues}
      /> {showToast && (
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
export default PrjEdit;
