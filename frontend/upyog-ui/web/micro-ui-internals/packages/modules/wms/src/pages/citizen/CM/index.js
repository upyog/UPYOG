import { FormComposer,Toast } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { newConfig } from "../../../config/contactMaster/contactMasterConfig";
import {useHistory} from 'react-router-dom'

const ContrMasterAdd = () =>{
  const tenantId = Digit.ULBService.getCurrentTenantId();
    const { t } = useTranslation();
    const history = useHistory();
  const [showToast, setShowToast] = useState(false);
const [istrue,setistrue] = useState(true)
const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.cm?.useWmsCMAdd();
console.log({isSuccess,isError,error})
  const closeToast = () => {
    setShowToast(false);
  };
  useEffect(()=>{
    if(showToast){
    setTimeout(() => {
      closeToast();
      history.push('cm-home')
    }, 5000);
  }
  },[showToast])
  useEffect(()=>{
    if(isSuccess){
      setShowToast(true)
    }else{setShowToast(false)}
  },[isSuccess])
const onSubmit=async(data)=>{
    console.log("data ",data)
    // let contactCreate ={
        // "RequestInfo": {
        //     "action": "approve",
        //     "apiId": "AP123",
        //     "authToken": "{{token}}",
        //     "correlationId": null,
        //     "did": null,
        //     "key": null,
        //     "msgId": null,
        //     "ts": 0,
        //     "userInfo": {
        //         "emailId": null,
        //         "id": 9836,
        //         "mobileNumber": "9901888381",
        //         "name": "APIAUTO",
        //         "roles": [
        //             {
        //                 "name": "Birth and Death User",
        //                 "code": "BND_CEMP",
        //                 "tenantId": "pg.citya"
        //             }
        //         ],
        //         "tenantId": "pg.citya",
        //         "type": "EMPLOYEE",
        //         "userName": "BDCEMP",
        //         "uuid": "53735c54-d665-48e7-9e66-db67fac50568"
        //     },
        //     "ver": null
        // },
        let contactCreate ={
        "WMSContractorApplication": [
            {
    "pfms_vendor_code":data?.WmsCMPFMSVendorID?.pfms_vendor_code,
    "vendor_name":data?.WmsCMVendorName?.vendor_name,
    "mobile_number":data?.WmsCMMobileNumber?.mobile_number,
    "uid_number":data?.WmsCMUIDNumber?.uid_number,
    "vendor_status":data?.WmsCMVendorStatus?.name,
    "vat_number":data?.WmsCMVATNumber?.VATNumber,
    "epfo_account_number":data?.WmsCMPFAccountNumber?.epfo_account_number,
    "payto":data?.WmsCMPayTo?.payto,
    "email":data?.WmsCMEmailId?.email,
    "gst_number":data?.WmsCMGSTNumber?.gst_number,
    "pan_number":data?.WmsCMPANNumber?.pan_number,
    "bank_account_number":data?.WmsCMBankAccountNumber?.bank_account_number,
    "address":data?.WmsCMAddress?.address,
    "allow_direct_payment":data?.WmsCMAllowDirectPayment?.allow_direct_payment,
    // "allowDirectPayment":data?.WmsCMAllowDirectPayment?.allowDirectPayment,
    "vendor_type":data?.WmsCMVendorType?.name,
    "bank_branch_ifsc_code":data?.WmsCMBankBranchIFSCCode?.name,
    "function":data?.WmsCMFunction?.name,
    "vendor_class":data?.WmsCMVendorClass?.name,
    "vendor_sub_type":data?.WmsCMSubType?.name,
    "primary_account_head":data?.WmsCMPrimaryAccountHead?.name,
    "tenantId": tenantId
    }
        ]
    }
    
    
    // [
    //     {
    //       ScheduleOfRateApplication: {
    //         pfms_vendor_code:data?.WmsChapter?.chapter1,
    //         vendor_type:data?.WmsCMChapter?.chapter
    //       }}]
    console.log("Text Controll View ",contactCreate, tenantId)

  /* use customiseCreateFormData hook to make some chnages to the Employee object */
  await mutate(contactCreate, tenantId)

//   Digit.WMSService.ContractorMaster.create(contactCreate, tenantId).then((result,err)=>{
//     let getdata = {...data , get: result }
//     onSelect("", getdata, "", true);
//     console.log("daaaa",getdata);
//     setShowToast({ key: true, label: "Data Added" });

//   }).catch((e) => {
//     setShowToast({ key: true, label: "Something Wrong" });

//   console.log("err");
//  });

//  history.push("/digit-ui/citizen/wms/sor/response");

//  console.log("getting data",contactCreate)
};

const configs = newConfig?newConfig:newConfig;

return (
    <div>
        <FormComposer
            heading="Contractor Master"
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
      {showToast&&isError && <Toast error={showToast.key} label={t('Something went wrong!')} onClose={closeToast} />}
      {showToast&&isSuccess && <Toast label={t('Record submitted successfully')} onClose={closeToast} />}
      {/* {isSuccess && <Toast label={showToast?.label} onClose={() => closeToast(null)} />} */}

    </div>
)
}
// "":data?.?.name,

export default ContrMasterAdd









