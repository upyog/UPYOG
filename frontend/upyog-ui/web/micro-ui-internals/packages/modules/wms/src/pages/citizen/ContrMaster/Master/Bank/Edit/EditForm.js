import React, { useState } from "react";
import { newConfig } from "../../../../../../config/contactMaster/BankBranchConfig";
import { FormComposer } from "@egovernments/digit-ui-react-components";

const EditForm=({ tenantId, data })=>{
    console.log("Edit ", data)
  const [canSubmit, setSubmitValve] = useState(true);
  const defaultValues = {
    WmsCMBankBranch:{bank_branch: data?.bank_branch},
    WmsCMBankIFSCCode:{bank_ifsc_code: data?.bank_ifsc_code},
    WmsCMBankName:{bank_name: data?.bank_name},
    WmsCMBankStatus:{name:data?.name}
  }

  const onFormValueChange = (setValue = true, formData) => { };
    const onSubmit=async(data)=>{
        console.log("data ",data)
        const payloadData={
            "bank_name": data?.WmsCMBankName?.bank_name,
            "bank_branch": data?.WmsCMBankBranch?.bank_branch,
            "bank_ifsc_code": data?.WmsCMBankIFSCCode?.bank_ifsc_code,
            "bank_branch_ifsc_code": data?.WmsCMBankName?.bank_name+","+data?.WmsCMBankBranch?.bank_branch+","+data?.WmsCMBankIFSCCode?.bank_ifsc_code,
            "status": data?.WmsCMBankStatus?.name
        }
        console.log("payloadData ",payloadData)
    }
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
    </React.Fragment>
)
}

export default EditForm;