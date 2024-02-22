import { FormComposer, Toast,Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { newConfig } from "../../../../components/config/pr-config";

import { convertEpochToDate } from "../../../../components/Utils";

const WmsPrEdit  = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);

  const isupdate = Digit.SessionStorage.get("isupdate");
  const { id: prId } = useParams();
  const { tenantId: tenantId } = useParams();
  // const [PrName, setPrName] = useState(null);
  // const [descriptionOfItem, setDescriptionOfItem] = useState(null);
  // const [chapter, setChapter] = useState(null);
  // const [itemNo, setItemNo] = useState(null);
  // const [unit, setUnit] = useState(null);
  // const [rate, setRate] = useState(null);
  // const [startDate, setStartDate] = useState(null);  
  // const [endDate, setEndDate] = useState(null);

  const [checkfield, setcheck] = useState(false);
  
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("PR_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("PR_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("PR_WMS_MUTATION_SUCCESS_DATA", false);
  const { isLoading, isError, error, data:dataEdit, ...rest } = Digit.Hooks.wms.pr.useWmsPrSearch({ pr_id: prId }, tenantId, null, isupdate);
  var defaultValues={};
  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
    
  }, []);

 

  const checkMailNameNum = (formData) => {

    const item_no = formData?.WmsPrItemNo?.item_no || '';
    const unit = formData?.WmsPrUnit?.unit || '';
    const rate = formData?.WmsPrRate?.rate|| '';
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
    // requestdata.start_date = Date.parse(input?.WmsPrStartDate?.start_date);
    // requestdata.end_date = Date.parse(input?.WmsPrEndDate?.end_date);
    requestdata.project_name = input?.WmsPrPrjName?.project_name ? input?.WmsPrPrjName?.project_name : undefined;
    requestdata.work_name = input?.WmsPrWorkName?.work_name ? input?.WmsPrWorkName?.work_name : undefined;
    requestdata.milestone_name = input?.WmsPrMLName?.milestone_name ? input?.WmsPrMLName?.milestone_name : undefined;
    requestdata.percent_weightage = input?.WmsPrPercent?.percent_weightage ? input?.WmsPrPercent?.percent_weightage : undefined;
    // requestdata.unit = input?.WmsPrUnit?.unit ? input?.WmsPrUnit?.unit : undefined;
    // requestdata.rate = input?.WmsPrRate?.rate ? input?.WmsPrRate?.rate : undefined;
    //let ScheduleOfRateApplications = [requestdata];
    Digit.WMSService.PRApplications.update(requestdata, tenantId).then((result,err)=>{
      setIsLoading(false);
      history.push("/upyog-ui/citizen/wms/pr-home");
     })
     .catch((e) => {
     console.log("err");
     history.push("/upyog-ui/citizen/wms/pr-home");
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
    WmsPrId: {Pr_id:dataEdit[0]?.Pr_id},
    WmsPrSchName :{ scheme_name: dataEdit[0]?.scheme_name },
    WmsPrPrjName: { project_name: dataEdit[0]?.project_name },
    WmsPrTypeOfWork: { work_type: dataEdit[0]?.work_type },
    WmsPrWorkName: { work_name: dataEdit[0]?.work_name },
    WmsPrEstNumber: { estimated_number: dataEdit[0]?.estimated_number },
    WmsPrEstWorkCost: { estimated_work_cost: dataEdit[0]?.estimated_work_cost },
    WmsPrSTA: { sanctioned_tender_amount: dataEdit[0]?.sanctioned_tender_amount },
    WmsPrStatus: { status_name: dataEdit[0]?.status_name },
    WmsPrBillDate: { bill_received_till_date: dataEdit[0]?.bill_received_till_date },
    // WmsPrUnit: { unit: dataEdit[0]?.unit },
    // WmsPrRate: { rate: dataEdit[0]?.rate },
    // WmsPrStartDate: { start_date: convertEpochToDate(dataEdit[0]?.start_date )},
    // WmsPrEndDate: { end_date: convertEpochToDate(dataEdit[0]?.end_date )},
  };
 // alert(JSON.stringify(dataEdit[0]?.Pr_id));
 }
  const configs = newConfig?newConfig:newConfig;
  return (
    <div>
      <FormComposer
        heading={t("WMS_PR_FORM_EDIT_CREATE_HEAD")}
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
export default WmsPrEdit ;
