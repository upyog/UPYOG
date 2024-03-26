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
  
    const onSubmit = async(item)=>{
console.log("Account Head Add ",item)
      const payloadData={"WMSPrimaryAccountHeadApplication": [{
          "primary_accounthead_name": item?.WmsAHName?.primary_accounthead_name,   
          "primary_accounthead_accountno": item?.WmsAHAccountno?.primary_accounthead_accountno,   
          "primary_accounthead_location": item?.WmsAHLocation?.primary_accounthead_location,   
          "account_status": item?.WmsAHStatus?.name,
        "tenantId":tenantId
    }]}
await mutate(payloadData)
}    
const configs = newConfig?newConfig:newConfig;
    return (
        <React.Fragment>
                <FormComposer
            heading="Account Head Add"
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