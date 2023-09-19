import React from "react";
import { newConfig } from "../../../../../config/contactMaster/BankBranchConfig";
import { FormComposer } from "@egovernments/digit-ui-react-components";
const BankAdd = () =>{
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
    </React.Fragment>)
}

export default BankAdd