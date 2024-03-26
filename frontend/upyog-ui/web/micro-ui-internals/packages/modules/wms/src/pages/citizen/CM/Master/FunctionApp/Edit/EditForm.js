import React, { useEffect, useState } from "react";
import { newConfig } from "../../../../../../config/contactMaster/FunctionAppConfig";
import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import {useHistory} from 'react-router-dom'

const EditForm=({ tenantId, data })=>{
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(true);
  const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId,"WMS_FUNCTION_APP_UPDATE");
  const [showToast, setShowToast] = useState(false);
  const closeToast = () => {
    setShowToast(false);
  };
  useEffect(()=>{
    if(showToast){
    setTimeout(() => {
      closeToast();
      // history.replace('/upyog-ui/citizen/wms/vendor-sub-type/list')
      history.push('/upyog-ui/citizen/wms/function-app/list')
    }, 5000);
  }
  },[showToast])
  useEffect(()=>{if(isSuccess){setShowToast(true);}else if(isError){alert("Something wrong in updated bank record")}else{}},[isSuccess])
  const defaultValues = {
    WmsFAName:{function_name: data?.function_name},
    WmsFACode:{function_code: data?.function_code},
    WmsFALevel:{function_level: data?.function_level},
    WmsFAStatus:{name: data?.status}
  }

  const onFormValueChange = (setValue = true, formData) => { };
    const onSubmit=async(item)=>{
      const payloadData={"WMSFunctionApplication": [{
        "function_id":data?.function_id,
        "function_name": item?.WmsFAName?.function_name,
        "function_code": item?.WmsFACode?.function_code,
        "function_level": item?.WmsFALevel?.function_level,
        "status": item?.WmsFAStatus?.name,
        "tenantId":tenantId
    }]}
       await mutate(payloadData)
    }
    const config = newConfig?newConfig:newConfig;
return (
    <React.Fragment>

<FormComposer
        // heading={t("HR_COMMON_EDIT_EMPLOYEE_HEADER")}
        heading={"Vendor Class Edit"}
        isDisabled={!canSubmit}
        // label={t("HR_COMMON_BUTTON_SUBMIT")}
        label={"Update"}
        config={config.map((config) => {
          return {
            ...config,
            body: config.body.filter((a) => !a.hideInEmployee),
          };
        })}
        fieldStyle={{ marginRight: 0 }}
        onSubmit={onSubmit}
        defaultValues={defaultValues}
        onFormValueChange={onFormValueChange}
      /> 
      {/* {showToast&&isError && <Toast error={showToast.key} label={t('Something went wrong!')} onClose={closeToast} />}
      {showToast&&isSuccess && <Toast label={t('Record updated successfully')} onClose={closeToast} />} */}

       {/* <FormComposer
            heading="Bank Edit"
            label="Save"
            // config={configs}
            config={configs.map((config) => {
                return {
                ...config,
                body: config.body.filter((a) => !a.hideInEmployee),
                };
            })}
            onSubmit={onSubmit}
            fieldStyle={{ marginRight: 0,"position":"initial" }}
            buttonStyle={{marginRight: 10,"position":"initial"}}
            // childrenAtTheBottom={true}        
        /> */}
      {showToast&&isError && <Toast label={'Something wrong'} onClose={closeToast} />}
      {showToast&&isSuccess && <Toast label={'Record updated successfully'} onClose={closeToast} />}

    </React.Fragment>
)
}

export default EditForm;