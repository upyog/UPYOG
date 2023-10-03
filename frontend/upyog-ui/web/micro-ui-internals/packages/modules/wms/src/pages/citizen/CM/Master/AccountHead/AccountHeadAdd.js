import React, {useEffect,useState} from "react";
import { newConfig } from "../../../../../config/contactMaster/AccountHeadConfig";
import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import {useHistory} from 'react-router-dom'

const AccountHeadAdd =()=>{
  const history = useHistory();

    const tenantId = Digit.ULBService.getCurrentTenantId();
const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.cm?.useWMSMaster(tenantId,"WMS_ACCOUNT_HEAD_ADD");
const [showToast, setShowToast] = useState(false);
  const closeToast = () => {
    setShowToast(false);
  };
  useEffect(()=>{
    if(showToast){
    setTimeout(() => {
      closeToast();
      history.replace('/upyog-ui/citizen/wms/account-head/list')
    }, 5000);
  }
  },[showToast])
  useEffect(()=>{if(isSuccess){setShowToast(true);}else if(isError){alert("Something wrong in updated bank record")}else{}},[isSuccess])
  
    const onSubmit = async(data)=>{
  console.log({isSuccess,isError,error})

console.log("Account Head Add ",data)

        // const payloadData={"WMSPrimaryAccountHeadApplication": [{
          // "primary_accounthead_name": data?.WmsAHName?.primary_accounthead_name,
          // "primary_accounthead_accountno": data?.WmsAHAccountno?.primary_accounthead_accountno,
          // "primary_accounthead_location": data?.WmsAHLocation?.primary_accounthead_location,
          // "tenantId": tenantId
        // }]}
        const payloadData={
          "primary_accounthead_name": data?.WmsAHName?.primary_accounthead_name,
          "primary_accounthead_accountno": data?.WmsAHAccountno?.primary_accounthead_accountno,
          "primary_accounthead_location": data?.WmsAHLocation?.primary_accounthead_location,
          "tenantId": tenantId  
      }
console.log("Vendor Class Add payloadData ",payloadData)

await mutate(payloadData)
}    
const configs = newConfig?newConfig:newConfig;
    return (
        <React.Fragment>
                <FormComposer
            heading="Vendor Class Add"
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
export default AccountHeadAdd