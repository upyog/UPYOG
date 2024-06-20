import React, {useEffect,useState} from "react";
import { newConfig } from "../../../../../config/contactMaster/BankBranchConfig";
import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import { useHistory } from "react-router-dom";

const BankAdd = () =>{
  const history = useHistory();

  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { isLoading, isError,isSuccess, error, data:record, mutate,...rest } = Digit?.Hooks?.wms?.cm?.useWMSMaster('',"WMS_BANK_CREATE");
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

        const payloadData={"WMSBankDetailsApplication": [{
            "bank_name": data?.WmsCMBankName?.bank_name,
            "bank_branch": data?.WmsCMBankBranch?.bank_branch,
            "bank_ifsc_code": data?.WmsCMBankIFSCCode?.bank_ifsc_code,
            "bank_branch_ifsc_code": data?.WmsCMBankName?.bank_name+","+data?.WmsCMBankBranch?.bank_branch+","+data?.WmsCMBankIFSCCode?.bank_ifsc_code,
            "status": data?.WmsCMBankStatus?.name,
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
            heading="Bank Add"
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

export default BankAdd