import React, { useEffect, useState } from "react";
import { newConfig } from "../../../../../../config/contactMaster/BankBranchConfig";
import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import { useHistory } from "react-router-dom";

const EditForm=({  data, tenantId })=>{
  const history = useHistory();
  const { isLoading, isError,isSuccess, error, data:record, mutate,...rest } = Digit?.Hooks?.wms?.cm?.useWMSMaster('',"WMS_BANK_UPDATE");
  const [showToast, setShowToast] = useState(false);
  const closeToast = () => {
    setShowToast(false);
  };
  useEffect(()=>{
    if(showToast){
    setTimeout(() => {
      closeToast();
      history.replace('/upyog-ui/citizen/wms/bank/list')
    }, 5000);
  }
  },[showToast])
    console.log("rest ", rest)
  const [canSubmit, setSubmitValve] = useState(true);
  const defaultValues = {
    WmsCMBankBranch:{bank_branch: data?.bank_branch},
    WmsCMBankIFSCCode:{bank_ifsc_code: data?.bank_ifsc_code},
    WmsCMBankName:{bank_name: data?.bank_name},
    WmsCMBankStatus:{name:data?.status}
  }

  const onFormValueChange = (setValue = true, formData) => { };
    const onSubmit=async(item)=>{
        // console.log("item ",item)
        const payloadData={"WMSBankDetailsApplication": [{
            "bank_id":data?.bank_id,
            "bank_name": item?.WmsCMBankName?.bank_name,
            "bank_branch": item?.WmsCMBankBranch?.bank_branch,
            "bank_ifsc_code": item?.WmsCMBankIFSCCode?.bank_ifsc_code,
            "bank_branch_ifsc_code": item?.WmsCMBankName?.bank_name+","+item?.WmsCMBankBranch?.bank_branch+","+item?.WmsCMBankIFSCCode?.bank_ifsc_code,
            "status": item?.WmsCMBankStatus?.name
        }]}
        
       
        await mutate(payloadData)
        setShowToast(true);
    }
    // useEffect(()=>{if(isSuccess){alert("Updated Succefully")}else{alert("Something wrong in updated bank record")}},[isSuccess])
    const config = newConfig?newConfig:newConfig;
return (
    <React.Fragment>

<FormComposer
        // heading={t("HR_COMMON_EDIT_EMPLOYEE_HEADER")}
        heading={"Bank Edit"}
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