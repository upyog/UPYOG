import React, {useEffect,useState} from "react";
import { newConfig } from "../../../../../config/contactMaster/VendorSubTypeConfig";
import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import { useHistory } from "react-router-dom";

const Add = () =>{
  const history = useHistory();

  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { isLoading, isError,isSuccess, error, data:record, mutate,...rest } = Digit?.Hooks?.wms?.cm?.useWMSMaster('',"WMS_SUB_TYPE_CREATE");
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
    const onSubmit=async(data)=>{
        console.log("data ",data)

        const payloadData={"WMSContractorSubTypeApplication": [{
            "contractor_stype_name": data?.WmsCMBankName?.bank_name,
            "contractor_stype_status": data?.WmsCMBankStatus?.name,
            "tenantId": tenantId
        }]}
        console.log("payloadData ",payloadData)
    await mutate(payloadData)
    setShowToast(true)
    }
    // useEffect(()=>{if(isSuccess){alert("Created Succefully")}else{alert("Something wrong in Created bank record")}},[isSuccess])

    const configs = newConfig?newConfig:newConfig;
    return (
    <React.Fragment>
<FormComposer
            heading="Vendor Sub Type Add"
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
      {showToast&&isSuccess && <Toast label={'Record added successfully'} onClose={closeToast} />}

    </React.Fragment>)
}

export default Add