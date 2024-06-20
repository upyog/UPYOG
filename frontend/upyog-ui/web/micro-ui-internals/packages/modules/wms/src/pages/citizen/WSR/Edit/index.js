import { FormComposer, Toast,Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { newConfig } from "../../../../components/config/wsr-config";

import { convertEpochToDate } from "../../../../components/Utils";

const WmsWsrEdit  = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);

  const isupdate = Digit.SessionStorage.get("isupdate");
  const { id: wsrId } = useParams();
  const { tenantId: tenantId } = useParams();
  // const [WsrName, setWsrName] = useState(null);
  // const [descriptionOfItem, setDescriptionOfItem] = useState(null);
  // const [chapter, setChapter] = useState(null);
  // const [itemNo, setItemNo] = useState(null);
  // const [unit, setUnit] = useState(null);
  // const [rate, setRate] = useState(null);
  // const [startDate, setStartDate] = useState(null);  
  // const [endDate, setEndDate] = useState(null);

  const [checkfield, setcheck] = useState(false);
  
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("WSR_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("WSR_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("WSR_WMS_MUTATION_SUCCESS_DATA", false);
  const { isLoading, isError, error, data:dataEdit, ...rest } = Digit.Hooks.wms.wsr.useWmsWsrSearch({ wsr_id: wsrId }, tenantId, null, isupdate);
  var defaultValues={};
  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
    
  }, []);

 

  const checkMailNameNum = (formData) => {

    const item_no = formData?.WmsWsrItemNo?.item_no || '';
    const unit = formData?.WmsWsrUnit?.unit || '';
    const rate = formData?.WmsWsrRate?.rate|| '';
    const validItem_no = item_no.length == 0 ? true : item_no.match(Digit.Utils.getPattern('Amount'));
    const validUnit = unit.length == 0 ? true : unit.match(Digit.Utils.getPattern('Amount'));
    const validRate = rate.length == 0 ? true : rate.match(Digit.Utils.getPattern('Amount'));
    return validItem_no && validUnit && validRate;
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

  const onSubmit = (input) => {

    let requestdata = Object.assign({}, dataEdit[0]);    
    // requestdata.start_date = Date.parse(input?.WmsWsrStartDate?.start_date);
    // requestdata.end_date = Date.parse(input?.WmsWsrEndDate?.end_date);
    requestdata.project_name = input?.WmsWsrPrjName?.project_name ? input?.WmsWsrPrjName?.project_name : undefined;
    requestdata.work_name = input?.WmsWsrWorkName?.work_name ? input?.WmsWsrWorkName?.work_name : undefined;
    requestdata.milestone_name = input?.WmsWsrActivity?.milestone_name ? input?.WmsWsrActivity?.milestone_name : undefined;
    requestdata.percent_weightage = input?.WmsWsrPercent?.percent_weightage ? input?.WmsWsrPercent?.percent_weightage : undefined;
    // requestdata.unit = input?.WmsWsrUnit?.unit ? input?.WmsWsrUnit?.unit : undefined;
    // requestdata.rate = input?.WmsWsrRate?.rate ? input?.WmsWsrRate?.rate : undefined;
    //let ScheduleOfRateApplications = [requestdata];
    Digit.WMSService.WSRApplications.update(requestdata, tenantId).then((result,err)=>{
      setIsLoading(false);
      history.push("/upyog-ui/citizen/wms/wsr-home");
     })
     .catch((e) => {
     console.log("err");
     history.push("/upyog-ui/citizen/wms/wsr-home");
    });
    /* use customiseUpdateFormData hook to make some chnages to the Employee object */
    //ScheduleOfRateApplications=Digit?.Customizations?.WMS?.customiseUpdateFormData?Digit.Customizations.WMS.customiseUpdateFormData(data,ScheduleOfRateApplications):ScheduleOfRateApplications;

    //history.replace("/upyog-ui/citizen/wms/response", { ScheduleOfRateApplications, key: "UPDATE", action: "UPDATE" });
  };
  if (isLoading) {
    return <Loader />;
  }
 else
 {
  defaultValues = {      
    tenantId: tenantId,
    WmsWsrId: {Wsr_id:dataEdit[0]?.Wsr_id},
    WmsWsrPrjName: { project_name: dataEdit[0]?.project_name },
    WmsWsrWorkName: { work_name: dataEdit[0]?.work_name },
    WmsWsrActivity: { milestone_name: dataEdit[0]?.milestone_name },
    WmsWsrPercent: { percent_weightage: dataEdit[0]?.percent_weightage },
    // WmsWsrUnit: { unit: dataEdit[0]?.unit },
    // WmsWsrRate: { rate: dataEdit[0]?.rate },
    // WmsWsrStartDate: { start_date: convertEpochToDate(dataEdit[0]?.start_date )},
    // WmsWsrEndDate: { end_date: convertEpochToDate(dataEdit[0]?.end_date )},
  };
 // alert(JSON.stringify(dataEdit[0]?.Wsr_id));
 }
  const configs = newConfig?newConfig:newConfig;
  return (
    <div>
      <FormComposer
        heading={t("WMS_WSR_FORM_EDIT_CREATE_HEAD")}
        //isDisabled={!canSubmit}
        label={t("WMS_COMMON_BUTTON_SUBMIT")}
        config={configs}
        fieldStyle={{ marginRight: 0 }}
        onSubmit={onSubmit}
        defaultValues={defaultValues}
        onFormValueChange={onFormValueChange}
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
export default WmsWsrEdit ;
