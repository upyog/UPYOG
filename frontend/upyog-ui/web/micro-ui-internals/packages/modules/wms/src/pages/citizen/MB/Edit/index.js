import { FormComposer, Toast,Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { newConfig } from "../../../../components/config/mb-config";
import { convertEpochToDate } from "../../../../components/Utils";

const WmsMbEdit = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);

  const isupdate = Digit.SessionStorage.get("isupdate");
  const { id: mbId } = useParams();
  const { tenantId: tenantId } = useParams();
  const [mbName, setMbName] = useState(null);
  const [descriptionOfItem, setDescriptionOfItem] = useState(null);
  const [chapter, setChapter] = useState(null);
  const [itemNo, setItemNo] = useState(null);
  const [unit, setUnit] = useState(null);
  const [rate, setRate] = useState(null);
  const [startDate, setStartDate] = useState(null);  
  const [endDate, setEndDate] = useState(null);

  const [checkfield, setcheck] = useState(false);
  
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("MB_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("MB_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("MB_WMS_MUTATION_SUCCESS_DATA", false);
  const { isLoading, isError, error, data:dataEdit, ...rest } = Digit.Hooks.wms.mb.useWmsMbSearch({ mb_id: mbId }, tenantId, null, isupdate);
  var defaultValues={};
  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
    
  }, []);

 

  const checkMailNameNum = (formData) => {

    const item_no = formData?.WmsMbItemNo?.item_no || '';
    const unit = formData?.WmsMbUnit?.unit || '';
    const rate = formData?.WmsMbRate?.rate|| '';
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
    requestdata.mb_date = Date.parse(input?.WmsMbStartDate?.mb_date);
    requestdata.description_of_item = input?.WmsMbDescriptionOfItem?.description_of_item ? input?.WmsMbDescriptionOfItem?.description_of_item : undefined;
    requestdata.chapter = input?.WmsMbChapter?.name ? input?.WmsMbChapter?.name : undefined;
    //let ScheduleOfRateApplications = [requestdata];
    Digit.WMSService.MBApplications.update(requestdata, tenantId).then((result,err)=>{
      setIsLoading(false);
      history.push("/upyog-ui/citizen/wms/mb-home");
     })
     .catch((e) => {
     console.log("err");
     history.push("/upyog-ui/citizen/wms/mb-home");
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
    WmsMbId: {mb_id:dataEdit[0]?.mb_id},
    WmsMbDescriptionOfItem: { description_of_item: dataEdit[0]?.description_of_item },
    WmsMbChapter: { name: dataEdit[0]?.chapter },
    WmsMbDate: { mb_date: convertEpochToDate(dataEdit[0]?.mb_date )},
  };
 // alert(JSON.stringify(dataEdit[0]?.mb_id));
 }
  const configs = newConfig?newConfig:newConfig;
  return (
    <div>
      <FormComposer
        heading={t("WMS_MB_FORM_CREATE_HEAD")}
        //isDisabled={!canSubmit}
        label={t("WMS_COMMON_BUTTON_SUBMIT")}
        config={configs}
        fieldStyle={{ marginRight: 0 }}
        onSubmit={onSubmit}
        defaultValues={defaultValues}
        //onFormValueChange={onFormValueChange}
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
export default WmsMbEdit;
