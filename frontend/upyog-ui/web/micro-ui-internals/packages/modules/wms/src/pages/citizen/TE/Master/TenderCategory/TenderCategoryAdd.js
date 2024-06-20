import React, {useEffect,useState} from "react";
import { newConfig } from "../../../../../components/config/TE/tenderCategory";
import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import {useHistory} from 'react-router-dom'

const TenderCategoryAdd =()=>{
  const history = useHistory();

    const tenantId = Digit.ULBService.getCurrentTenantId();
const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.te?.useWMSTEMaster(tenantId,"WMS_TENDER_CATEGORY_CREATE");
const [showToast, setShowToast] = useState(false);
  const closeToast = () => {
    setShowToast(false);
  };
  useEffect(()=>{
    if(showToast){
    setTimeout(() => {
      closeToast();
      history.replace('/upyog-ui/citizen/wms/tender-entry/tender-category/list')
    }, 5000);
  }
  },[showToast])
  useEffect(()=>{if(isSuccess){setShowToast(true);}else if(isError){alert("Something wrong in updated bank record")}else{}},[isSuccess])
  
    const onSubmit = async(item)=>{
      const payloadData={"WMSTenderCategoryApplication": [{
          "category_name": item?.WmsTenderCategoryName?.tender_category_name,   
          "category_status": item?.WmsTenderCategoryStatus?.name,
          "tenantId":tenantId
    }]}
await mutate(payloadData)
}    
const configs = newConfig?newConfig:newConfig;
    return (
        <React.Fragment>
                <FormComposer
            heading="Tendery Category Add"
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
export default TenderCategoryAdd