import React, { useEffect, useState } from "react";
// import { newConfig } from "../../../../../../config/contactMaster/BankBranchConfig";
import { newConfig } from "../../../../components/config/te-config";

import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import { useHistory } from "react-router-dom";

const EditForm=({  data, tenantId })=>{
  const history = useHistory();
  const { isLoading, isError,isSuccess, error, data:record, mutate,...rest } = Digit?.Hooks?.wms?.te?.useWmsTEEdit();
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
    console.log("data from parent tender edit prefill ", data)
  const [canSubmit, setSubmitValve] = useState(true);
  // const defaultValues = 
  // {
  //   WmsCMBankBranch:{bank_branch: data?.bank_branch},
  //   WmsCMBankIFSCCode:{bank_ifsc_code: data?.bank_ifsc_code},
  //   WmsCMBankName:{bank_name: data?.bank_name},
  //   WmsCMBankStatus:{name:data?.status}
  // }
  const defaultValues = {
    WmsTMDepartment:{"department_name":data?.department_name},
    WmsTMResulationNo:{"resolution_no":data?.resolution_no},
    WmsTEPreBuildMeetingDate:{"prebid_meeting_date":data?.prebid_meeting_date},
    WmsTEIssueFromDate:{"issue_from_date":data?.issue_from_date},
    WmsTEPublishDate:{"publish_date":data?.publish_date},
    WmsTETecnicalBidOpenDate:{"technical_bid_open_date":data?.technical_bid_open_date},
    WmsTMTenderCategory:{"request_category":data?.request_category},
    WmsTEUploadDocuments:{"upload_document":data?.upload_document},
    WmsTEResolutionDate:{"resolution_date":data?.resolution_date},
    WmsTMMeetingLocation:{"prebid_meeting_location":data?.prebid_meeting_location},
    WmsTEIssueTillDate:{"issue_till_date":data?.issue_till_date},
    WmsTEFinancialBidOpenDate:{"financial_bid_open_date":data?.financial_bid_open_date},
    WmsTMValidityOfTenderInDay:{"validity":data?.validity},
    WmsTMProjectName:{"project_name":data?.project_name},
    }
console.log("defaultValues tender ",defaultValues)
  const onFormValueChange = (setValue = true, formData) => { };
    const onSubmit=async(item)=>{
        // console.log("item ",item)
        const payloadData={"WMSTenderEntryApplication": [{
            "bank_id":data?.tender_id,

            "bank_name": item?.WmsCMBankName?.bank_name,
            "bank_branch": item?.WmsCMBankBranch?.bank_branch,
            "bank_ifsc_code": item?.WmsCMBankIFSCCode?.bank_ifsc_code,
            "bank_branch_ifsc_code": item?.WmsCMBankName?.bank_name+","+item?.WmsCMBankBranch?.bank_branch+","+item?.WmsCMBankIFSCCode?.bank_ifsc_code,
            "status": item?.WmsCMBankStatus?.name
        }]}
        
       
        await mutate(payloadData)
        setShowToast(true);
    }
    // useEffect(()=>{if(isSuccess){alert("Updated Succefully")}else{alert("Something wrong in updated bank record")}},[isSuccess])
    const config = newConfig?newConfig:newConfig;
return (
    <React.Fragment>

<FormComposer
        // heading={t("HR_COMMON_EDIT_EMPLOYEE_HEADER")}
        heading={"Tender Entry Edit"}
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
       {showToast&&isError && <Toast error={showToast.key} label={t('Something went wrong!')} onClose={closeToast} />}
      {showToast&&isSuccess && <Toast label={t('Record updated successfully')} onClose={closeToast} />} 








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
      {/* {showToast&&isSuccess && <Toast label={'Record updated successfully'} onClose={closeToast} />} */}

    </React.Fragment>
)
}

export default EditForm;