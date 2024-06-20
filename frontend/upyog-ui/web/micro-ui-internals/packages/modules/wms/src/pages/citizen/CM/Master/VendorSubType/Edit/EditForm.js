import React, { useEffect, useState } from "react";
import { newConfig } from "../../../../../../config/contactMaster/VendorSubTypeConfig";
import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import { useHistory } from "react-router-dom";

const EditForm=({  data, tenantId })=>{
  console.log("data edit ",data)
  const history = useHistory();
  const { isLoading, isError,isSuccess, error, data:record, mutate,...rest } = Digit?.Hooks?.wms?.cm?.useWMSMaster('',"WMS_SUB_TYPE_UPDATE");
  const [showToast, setShowToast] = useState(false);
  const closeToast = () => {
    setShowToast(false);
  };
  useEffect(()=>{
    if(showToast){
    setTimeout(() => {
      closeToast();
      history.replace('/upyog-ui/citizen/wms/vendor-sub-type/list')
      
    }, 5000);
  }
  },[showToast])
    console.log("rest ", rest)
  const [canSubmit, setSubmitValve] = useState(true);
  const defaultValues = {

    WmsCMVendorSubTypeName:{vendor_sub_type: data?.contractor_stype_name},
    WmsCMVendorSubTypeStatus:{name:data?.contractor_stype_status}
  }

  const onFormValueChange = (setValue = true, formData) => { };
    const onSubmit=async(item)=>{
        console.log("item ",item)
        const payloadData={"WMSContractorSubTypeApplication": [{
            "contractor_id":data?.contractor_id,
            "contractor_stype_name": item?.WmsCMVendorSubTypeName?.vendor_sub_type,
            "contractor_stype_status": item?.WmsCMVendorSubTypeStatus?.name,
            "tenantId":tenantId
        }]}
        
       
        await mutate(payloadData)
    }
    useEffect(()=>{if(isSuccess){setShowToast(true);}else if(isError){alert("Something wrong in updated bank record")}else{}},[isSuccess])
    const config = newConfig?newConfig:newConfig;
return (
    <React.Fragment>

<FormComposer
        // heading={t("HR_COMMON_EDIT_EMPLOYEE_HEADER")}
        heading={"Vendor Sub Type Edit"}
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
      {showToast&&isSuccess && <Toast label={'Record updated successfully'} onClose={closeToast} />}

    </React.Fragment>
)
}

export default EditForm;