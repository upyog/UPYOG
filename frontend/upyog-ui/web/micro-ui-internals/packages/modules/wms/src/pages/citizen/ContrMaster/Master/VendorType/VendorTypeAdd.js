import React from "react";
import { newConfig } from "../../../../../config/contactMaster/VendorTypeConfig";
import { FormComposer } from "@egovernments/digit-ui-react-components";
const VendorTypeAdd =()=>{
    const tenantId = Digit.ULBService.getCurrentTenantId();
const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId,"WMS_V_TYPE_ADD");

    const onSubmit =(data)=>{
// console.log("Vendor Type Add ",data)
mutate({"name":data?.WmsCMVType?.vendor_type,"status":data?.WmsCMVTypeStatus?.name})
}    
const configs = newConfig?newConfig:newConfig;
    return (
        <React.Fragment>
                <FormComposer
            heading="Vendor Type Add"
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
        /> 
        </React.Fragment>
    )
}
export default VendorTypeAdd