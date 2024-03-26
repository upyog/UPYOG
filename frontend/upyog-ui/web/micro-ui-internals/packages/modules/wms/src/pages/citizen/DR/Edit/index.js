import { FormComposer, Toast,Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { newConfig } from "../../../../components/config/dr-config";

import { convertEpochToDate } from "../../../../components/Utils";

const WmsDrEdit  = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);

  const isupdate = Digit.SessionStorage.get("isupdate");
  const { id: drId } = useParams();
  const { tenantId: tenantId } = useParams();
  // const [DrName, setDrName] = useState(null);
  // const [descriptionOfItem, setDescriptionOfItem] = useState(null);
  // const [chapter, setChapter] = useState(null);
  // const [itemNo, setItemNo] = useState(null);
  // const [unit, setUnit] = useState(null);
  // const [rate, setRate] = useState(null);
  // const [startDate, setStartDate] = useState(null);  
  // const [endDate, setEndDate] = useState(null);

  const [checkfield, setcheck] = useState(false);
  
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("DR_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("DR_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("DR_WMS_MUTATION_SUCCESS_DATA", false);
  const { isLoading, isError, error, data:dataEdit, ...rest } = Digit.Hooks.wms.dr.useWmsDrSearch({ dr_id: drId }, tenantId, null, isupdate);
  var defaultValues={};
  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
    
  }, []);

 

  const checkMailNameNum = (formData) => {

    const item_no = formData?.WmsDrItemNo?.item_no || '';
    const unit = formData?.WmsDrUnit?.unit || '';
    const rate = formData?.WmsDrRate?.rate|| '';
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
    // requestdata.start_date = Date.parse(input?.WmsDrStartDate?.start_date);
    // requestdata.end_date = Date.parse(input?.WmsDrEndDate?.end_date);
    requestdata.project_name = input?.WmsDrPrjName?.project_name ? input?.WmsDrPrjName?.project_name : undefined;
    requestdata.work_name = input?.WmsDrWorkName?.work_name ? input?.WmsDrWorkName?.work_name : undefined;
    requestdata.milestone_name = input?.WmsDrMLName?.milestone_name ? input?.WmsDrMLName?.milestone_name : undefined;
    requestdata.percent_weightage = input?.WmsDrPercent?.percent_weightage ? input?.WmsDrPercent?.percent_weightage : undefined;
    // requestdata.unit = input?.WmsDrUnit?.unit ? input?.WmsDrUnit?.unit : undefined;
    // requestdata.rate = input?.WmsDrRate?.rate ? input?.WmsDrRate?.rate : undefined;
    //let ScheduleOfRateApplications = [requestdata];
    Digit.WMSService.DRApplications.update(requestdata, tenantId).then((result,err)=>{
      setIsLoading(false);
      history.push("/upyog-ui/citizen/wms/dr-home");
     })
     .catch((e) => {
     console.log("err");
     history.push("/upyog-ui/citizen/wms/dr-home");
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
    WmsDrId: {Dr_id:dataEdit[0]?.Dr_id},
    WmsDrPrjName: { project_name: dataEdit[0]?.project_name },
    WmsDrWorkName: { work_name: dataEdit[0]?.work_name },
    WmsDrMLName: { milestone_name: dataEdit[0]?.milestone_name },
    WmsDrPercent: { percent_weightage: dataEdit[0]?.percent_weightage },
    // WmsDrUnit: { unit: dataEdit[0]?.unit },
    // WmsDrRate: { rate: dataEdit[0]?.rate },
    // WmsDrStartDate: { start_date: convertEpochToDate(dataEdit[0]?.start_date )},
    // WmsDrEndDate: { end_date: convertEpochToDate(dataEdit[0]?.end_date )},
  };
 // alert(JSON.stringify(dataEdit[0]?.Dr_id));
 }
  const configs = newConfig?newConfig:newConfig;
  return (
    <div>
      <FormComposer
        heading={t("WMS_DR_FORM_EDIT_CREATE_HEAD")}
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
export default WmsDrEdit ;
