import React, { useState } from "react";
import { newConfig } from "../../../../../../config/contactMaster/VendorTypeConfig";
import { FormComposer } from "@egovernments/digit-ui-react-components";
import {useHistory} from 'react-router-dom'

const EditForm=({ tenantId, data })=>{
  const history = useHistory();
  const [canSubmit, setSubmitValve] = useState(true);
  const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.cm?.useWMSMaster(data?.id,"WMS_V_TYPE_UPDATE");
  console.log({isSuccess,isError,error})
  const defaultValues = {
    WmsCMVType:{vendor_type: data?.name},
    WmsCMVTypeStatus:{name: data?.status},
  }

  const onFormValueChange = (setValue = true, formData) => { };
    const onSubmit=async(data)=>{
        console.log("data ",data)
        const payloadData={
            "name": data?.WmsCMVType?.vendor_type,   
            "status": data?.WmsCMVTypeStatus?.name,   
        }
        console.log("payloadData ",payloadData)
        mutate(payloadData)
        history.push('/list')
    }
    const config = newConfig?newConfig:newConfig;
return (
    <React.Fragment>

<FormComposer
        // heading={t("HR_COMMON_EDIT_EMPLOYEE_HEADER")}
        heading={"Vendor Type Edit"}
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