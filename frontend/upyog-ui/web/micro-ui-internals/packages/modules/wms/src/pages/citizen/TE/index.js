import React,{Fragment, useEffect, useState} from "react";
import { newConfig } from "../../../components/config/te-config";
import { FormComposer, Header, Toast } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import useEDCRInbox from "../../../../../../libraries/src/hooks/obps/useEDCRInbox";

const TenderEntryAdd = () =>{
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.te?.useWmsTEAdd();
useEffect(()=>{
if(isSuccess){alert("Created Successfully")}else{alert("something wrong")}
},[isSuccess])
    const [showToast, setShowToast] = useState(null);
    const onSubmit = async(data) => {
        console.log("Add tender entry ",data)
        let payload = {"WMSTenderEntryApplication": [{
          "department_name":data?.WmsTMDepartment?.name,
          "resolution_no":data?.WmsTMResulationNo?.resulation_no,
          "prebid_meeting_date":data?.WmsTEPreBuildMeetingDate?.['Pre-Build-Meeting-Date'],
          "issue_from_date":data?.WmsTEIssueFromDate?.['issue-from-date'],
          "publish_date":data?.WmsTEPublishDate?.['tecnical-bid-open-date'],
          "technical_bid_open_date":data?.WmsTETecnicalBidOpenDate?.['technical_bid_open_date'],
          "request_category":data?.WmsTMTenderCategory?.name,
          // :data?.WmsTMBankGuarantee?.bank-guarantee,
          // :data?.WmsTMProvisionalSum?.name,
          "resolution_date":data?.WmsTEResolutionDate?.['resolution-date'],
          "prebid_meeting_location":data?.WmsTMMeetingLocation?.['meeting-location'],
          "issue_till_date":data?.WmsTEIssueTillDate?.['issue-till-date'],
          "financial_bid_open_date":data?.WmsTEFinancialBidOpenDate?.['financial-bid-open-date'],
          "validity":data?.WmsTMValidityOfTenderInDay?.['validity-of-tender-in-days'],
          // :data?.WmsTMAdditionalPerformanceSD?.additional-performance-sd,
          "project_name":data?.WmsTMProjectName?.name,
          "tenantId": tenantId
        }]};
console.log(payload ,"payload")
        await mutate(payload)
    }
  const configs = newConfig?newConfig:newConfig;
    return(<>
    <div>
    <Header>{t("WMS_TE_ADD_LABEL")}</Header>
    <FormComposer
              head={t("WMS_TE_ADD_LABEL")}
              label={t("WMS_TE_COMMON_SAVE")}
              config={configs}
              onSubmit={onSubmit}
              fieldStyle={{ marginRight: 0 }}
            />
            {showToast && (
        <Toast
          error={showToast.key}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
      )}
            </div>
    </>)
}
export default TenderEntryAdd;