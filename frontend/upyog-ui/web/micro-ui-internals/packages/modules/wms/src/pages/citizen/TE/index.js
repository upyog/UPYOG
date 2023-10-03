import React,{Fragment, useEffect, useState} from "react";
import { newConfig } from "../../../components/config/te-config";
import { FormComposer, Header, Toast } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import useEDCRInbox from "../../../../../../libraries/src/hooks/obps/useEDCRInbox";

const TenderEntryAdd = () =>{
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.te?.useWmsTEAdd();

    const [showToast, setShowToast] = useState(null);
    const [isTrue, setisTrue] = useState(false);
    const [imagePath, setimagePath] = useState("");
const getDataimg=(d)=>{
  setimagePath(d)
}
    console.log("imagePath out lifting ",imagePath)
    console.log("imagePath out lifting names",imagePath[0]?.documentUid?.fileStoreId)
    
  //   useEffect(()=>{
  //     const getImg = localStorage.getItem("imagePath");
  //     console.log("imagePath out",getImg)
  //     setimagePath(getImg);

  //     if(localStorage.getItem("imagePath")){console.log("tureee")}else{console.log("Falseee"); 
  //   // setimagePath("Testssss")
  // }
  // },[isTrue])

    useEffect(()=>{
      if(isSuccess){alert("Created Successfully")}else{alert("something wrong")}
      },[isSuccess])

    const onSubmit = async(data) => {
      // setisTrue(true);
        console.log("Add tender entry ",data)
        let ddd=imagePath
        console.log("imagePath in in ",imagePath)

        // setTimeout(async()=>{
         
          // console.log("imagePath in ",{imagePath, ddd})
        let payloadData = {"WMSTenderEntryApplication": [{
          "department_name":data?.WmsTMDepartment?.name,
          "resolution_no":data?.WmsTMResulationNo?.resulation_no,
          "prebid_meeting_date":data?.WmsTEPreBuildMeetingDate?.['Pre-Build-Meeting-Date'],
          "issue_from_date":data?.WmsTEIssueFromDate?.['issue-from-date'],
          "publish_date":data?.WmsTEPublishDate?.['tecnical-bid-open-date'],
          "technical_bid_open_date":data?.WmsTETecnicalBidOpenDate?.['technical_bid_open_date'],
          "request_category":data?.WmsTMTenderCategory?.name,
          // "upload_document":imagePath,
          "upload_document":imagePath[0]?.documentUid?.fileStoreId,
          // :data?.WmsTMBankGuarantee?.bank-guarantee,
          // :data?.WmsTMProvisionalSum?.name,
          "resolution_date":data?.WmsTEResolutionDate?.['resolution-date'],
          "prebid_meeting_location":data?.WmsTMMeetingLocation?.['meeting-location'],
          "issue_till_date":data?.WmsTEIssueTillDate?.['issue-till-date'],
          "financial_bid_open_date":data?.WmsTEFinancialBidOpenDate?.['financial-bid-open-date'],
          "validity":data?.WmsTMValidityOfTenderInDay?.['validity-of-tender-in-days'],
          // :data?.WmsTMAdditionalPerformanceSD?.additional-performance-sd,
          "project_name":data?.WmsTMProjectName?.name,
          "work_no": "WD1234",
          "work_description": "Pipe Fitting",
          "estimated_cost": "5000 INR",
            "tender_type": "Item Rate",
            "tender_fee": 10000,
            "emd": "1000",
            "vendor_class": "Class A",
            "work_duration": "2 Months",
          "tenantId": tenantId
        }]};
console.log(payloadData ,"payload")
        await mutate(payloadData);
        
      // },8000);
      //   setTimeout(()=>{
      //   localStorage.removeItem("imagePath")
      //   // setisTrue(false)
      // },20000)
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
              getDataimg={getDataimg}
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