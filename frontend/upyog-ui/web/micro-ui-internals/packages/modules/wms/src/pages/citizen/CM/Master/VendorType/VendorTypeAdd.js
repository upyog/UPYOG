import React, {useEffect,useState} from "react";
import { newConfig } from "../../../../../config/contactMaster/VendorTypeConfig";
import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import {useHistory} from 'react-router-dom'

const VendorTypeAdd =()=>{
  const history = useHistory();

    const tenantId = Digit.ULBService.getCurrentTenantId();
const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId,"WMS_V_TYPE_ADD");
const [showToast, setShowToast] = useState(false);
  const closeToast = () => {
    setShowToast(false);
  };
  useEffect(()=>{
    if(showToast){
    setTimeout(() => {
      closeToast();
      history.replace('/upyog-ui/citizen/wms/vendor-type/list')
    }, 5000);
  }
  },[showToast])
  useEffect(()=>{if(isSuccess){setShowToast(true);}else if(isError){alert("Something wrong in updated bank record")}else{}},[isSuccess])
  
    const onSubmit = async(data)=>{
  console.log({isSuccess,isError,error})

console.log("Vendor Type Add ",data)

        const payloadData={"WMSVendorTypeApplication": [{
            "vendor_type_name": data?.WmsCMVType?.vendor_type,
            "vendor_type_status": data?.WmsCMVTypeStatus?.name,
            "tenantId": tenantId
        }]}
await mutate(payloadData)
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
      {showToast&&isError && <Toast label={'Something wrong'} onClose={closeToast} />}
      {showToast&&isSuccess && <Toast label={'Record added successfully'} onClose={closeToast} />}

        </React.Fragment>
    )
}
export default VendorTypeAdd