import React, { useEffect, useState } from "react";
// import { newConfig } from "../../../../../../config/contactMaster/BankBranchConfig";
import { newConfig } from "../../../../components/config/TE/te-config";

import { FormComposer, Toast } from "@egovernments/digit-ui-react-components";
import { useHistory } from "react-router-dom";
import { convertEpochToDate } from "../../../../components/Utils";

const EditForm=({  data, tenantId })=>{
  const history = useHistory();
  const { isLoading, isError,isSuccess, error, data:record, mutate,...rest } = Digit?.Hooks?.wms?.te?.useWmsTEEdit();
  const [showToast, setShowToast] = useState(false);
  const [imagePath, setimagePath] = useState();
  const onFormValueChange=(setValue, formData, formState)=>{
    console.log("dddddddddddddddd ",{setValue, formData, formState, isSuccess, isError})
    setimagePath(formData?.WmsTEUploadDocuments?.[0]?.fileStoreId)
  }

  const closeToast = () => {
    setShowToast(false);
  };
  useEffect(()=>{
    if(showToast){
    setTimeout(() => {
      closeToast();
      history.replace('/upyog-ui/citizen/wms/tender-entry/home')
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
    WmsTMDepartment:{name:data?.department_name},
    WmsTMResulationNo:{resulation_no:data?.resolution_no},
    WmsTEPreBuildMeetingDate:{["Pre-Build-Meeting-Date"]:convertEpochToDate(data?.prebid_meeting_date)},
    WmsTEIssueFromDate:{"issue-from-date":convertEpochToDate(data?.issue_from_date )},
    WmsTEPublishDate:{publish_date:convertEpochToDate(data?.publish_date)},
    WmsTETecnicalBidOpenDate:{technical_bid_open_date:convertEpochToDate(data?.technical_bid_open_date)},
    WmsTMTenderCategory:{"name":data?.request_category},
    WmsTEUploadDocuments:{"upload_document":data?.upload_document},
    WmsTEResolutionDate:{"resolution-date":convertEpochToDate(data?.resolution_date)},
    WmsTMMeetingLocation:{"meeting-location":data?.prebid_meeting_location},
    WmsTEIssueTillDate:{"issue-till-date":convertEpochToDate(data?.issue_till_date)},
    WmsTEFinancialBidOpenDate:{"financial-bid-open-date":convertEpochToDate(data?.financial_bid_open_date)},
    WmsTMValidityOfTenderInDay:{"validity-of-tender-in-days":data?.validity},
    WmsTMProjectName:{"name":data?.project_name},
    }

    // const onFormValueChange = (setValue = true, formData) => { };
    const onSubmit=async(item)=>{
        console.log("item edit ",item)
        let payloadData = {"WMSTenderEntryApplication": [{
          "tender_id":data?.tender_id,
          "department_name":item?.WmsTMDepartment?.name,
          "resolution_no":item?.WmsTMResulationNo?.resulation_no,
          "prebid_meeting_date":item?.WmsTEPreBuildMeetingDate?.['Pre-Build-Meeting-Date'],
          "issue_from_date":item?.WmsTEIssueFromDate?.['issue-from-date'],
          "publish_date":item?.WmsTEPublishDate?.['publish_date'],
          "technical_bid_open_date":item?.WmsTETecnicalBidOpenDate?.['technical_bid_open_date'],
          "request_category":item?.WmsTMTenderCategory?.name,
          // "upload_document":imagePath[0]?.documentUid?.fileStoreId,
          "upload_document":imagePath,
          "resolution_date":item?.WmsTEResolutionDate?.['resolution-date'],
          "prebid_meeting_location":item?.WmsTMMeetingLocation?.['meeting-location'],
          "issue_till_date":item?.WmsTEIssueTillDate?.['issue-till-date'],
          "financial_bid_open_date":item?.WmsTEFinancialBidOpenDate?.['financial-bid-open-date'],
          "validity":item?.WmsTMValidityOfTenderInDay?.['validity-of-tender-in-days'],
          "project_name":item?.WmsTMProjectName?.name,
          "tenantId": tenantId
        }]};
        console.log("item edit payloadData ",payloadData)

        await mutate(payloadData)
    }
    useEffect(()=>{if(isSuccess){setShowToast(true)}else{setShowToast(false)}},[isSuccess])
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
       {/* {showToast&&isError && <Toast error={showToast.key} label={t('Something went wrong!')} onClose={closeToast} />}
      {showToast&&isSuccess && <Toast label={t('Record updated successfully')} onClose={closeToast} />}  */}

      {showToast&&isError && <Toast error={showToast.key} label={'Something went wrong!'} onClose={closeToast} />}
      {showToast&&isSuccess && <Toast label={'Record updated successfully'} onClose={closeToast} />} 







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