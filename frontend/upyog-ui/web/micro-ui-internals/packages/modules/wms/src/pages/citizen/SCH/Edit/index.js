import { FormComposer, Toast,Loader } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { newConfig } from "../../../../components/config/sch-config";
import { convertEpochToDate } from "../../../../components/Utils";

const WmsSchEdit = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(false);
  const [showToast, setShowToast] = useState(null);

  const isupdate = Digit.SessionStorage.get("isupdate");
  const { id: schId } = useParams();
  const { tenantId: tenantId } = useParams();
  const [schName, setSchName] = useState(null);
  const [descriptionOfItem, setDescriptionOfItem] = useState(null);
  const [chapter, setChapter] = useState(null);
  const [itemNo, setItemNo] = useState(null);
  const [unit, setUnit] = useState(null);
  const [rate, setRate] = useState(null);
  const [startDate, setStartDate] = useState(null);  
  const [endDate, setEndDate] = useState(null);

  const [checkfield, setcheck] = useState(false);
  
  const [errorInfo, setErrorInfo, clearError] = Digit.Hooks.useSessionStorage("SCH_WMS_ERROR_DATA", false);
  const [mutationHappened, setMutationHappened, clear] = Digit.Hooks.useSessionStorage("SCH_WMS_MUTATION_HAPPENED", false);
  const [successData, setsuccessData, clearSuccessData] = Digit.Hooks.useSessionStorage("SCH_WMS_MUTATION_SUCCESS_DATA", false);
  const { isLoading, isError, error, data:dataEdit, ...rest } = Digit.Hooks.wms.sch.useWmsSchSearch({ scheme_id: schId }, tenantId, null, isupdate);
  var defaultValues={};
  useEffect(() => {
    setMutationHappened(false);
    clearSuccessData();
    clearError();
    
  }, []);
  const onSubmit = (input) => {

    let requestdata = Object.assign({}, dataEdit[0]);    
    requestdata.start_date = Date.parse(input?.WmsSchStartDate?.start_date);
    requestdata.end_date = Date.parse(input?.WmsSchEndDate?.end_date);
    requestdata.scheme_name_en = input?.WmsSchNameEn?.scheme_name_en ? input?.WmsSchNameEn?.scheme_name_en : undefined;
    requestdata.scheme_name_reg = input?.WmsSchNameReg?.scheme_name_reg ? input?.WmsSchNameReg?.scheme_name_reg : undefined;
    requestdata.fund = input?.WmsSchFund?.fund ? input?.WmsSchFund?.fund : undefined;
    requestdata.source_of_fund = input?.WmsSchSourceOfFund?.source_of_fund ? input?.WmsSchSourceOfFund?.source_of_fund : undefined;
    requestdata.description_of_the_scheme = input?.WmsSchDescriptionOfScheme?.description_of_the_scheme ? input?.WmsSchDescriptionOfScheme?.description_of_the_scheme : undefined;
    
    Digit.WMSService.SCHApplications.update(requestdata, tenantId).then((result,err)=>{
      setIsLoading(false);
      history.push("/upyog-ui/citizen/wms/sch-home");
     })
     .catch((e) => {
     console.log("err");
     history.push("/upyog-ui/citizen/wms/sch-home");
    });
  };
  if (isLoading) {
    return <Loader />;
  }
 else
 {
  defaultValues = {      
    tenantId: tenantId,
    WmsSchId: {scheme_id:dataEdit[0]?.scheme_id},
    WmsSchNameEn: { scheme_name_en: dataEdit[0]?.scheme_name_en },
    WmsSchNameReg: { scheme_name_reg: dataEdit[0]?.scheme_name_reg },
    WmsSchFund: { fund: dataEdit[0]?.fund },
    WmsSchSourceOfFund: { source_of_fund: dataEdit[0]?.source_of_fund },
    WmsSchDescriptionOfScheme: { description_of_the_scheme: dataEdit[0]?.description_of_the_scheme },
    WmsSchStartDate: { start_date: convertEpochToDate(dataEdit[0]?.start_date )},
    WmsSchEndDate: { end_date: convertEpochToDate(dataEdit[0]?.end_date )},
  };
 }
  const configs = newConfig?newConfig:newConfig;
  return (
    <div>
      <FormComposer
        heading={t("WMS_SCH_FORM_CREATE_HEAD")}
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
export default WmsSchEdit;
