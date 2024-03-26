import React,{Fragment, useEffect, useState} from "react";
import { newConfig } from "../../../components/config/TE/te-config";
import { FormComposer, Header, Toast } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import useEDCRInbox from "../../../../../../libraries/src/hooks/obps/useEDCRInbox";
import {useHistory} from 'react-router-dom'

const TenderEntryAdd = () =>{ 
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
const {mutate,isSuccess,isError,error} = Digit?.Hooks?.wms?.te?.useWmsTEAdd();
const history = useHistory();

    const [showToast, setShowToast] = useState(null);
    const [isTrue, setisTrue] = useState(false);
    const [imagePath, setimagePath] = useState();
const getDataimg=(d)=>{
  setimagePath(d)
}
    // console.log("imagePath out lifting names",imagePath[0]?.documentUid?.fileStoreId)
    
  //   useEffect(()=>{
  //     const getImg = localStorage.getItem("imagePath");
  //     console.log("imagePath out",getImg)
  //     setimagePath(getImg);

  //     if(localStorage.getItem("imagePath")){console.log("tureee")}else{console.log("Falseee"); 
  //   // setimagePath("Testssss")
  // }
  // },[isTrue])

    // useEffect(()=>{
    //   if(isSuccess){alert("Created Successfully")}else{alert("something wrong")}
    //   },[isSuccess])
    useEffect(()=>{if(isSuccess){setShowToast(true)}else{setShowToast(false)}},[isSuccess])
    const closeToast = () => {
      setShowToast(false);
    };

    useEffect(()=>{
      if(showToast){
      setTimeout(() => {
        closeToast();
        history.push('/upyog-ui/citizen/wms/tender-entry/home')
      }, 5000);
    }
    },[showToast])

    const onSubmit = async(data) => {
      // alert("dddd")
      console.log("payloadData data ",data)  
        let payloadData = {"WMSTenderEntryApplication": [{
          "department_name":data?.WmsTMDepartment?.name,
          "resolution_no":data?.WmsTMResulationNo?.resulation_no,
          "prebid_meeting_date":data?.WmsTEPreBuildMeetingDate?.['Pre-Build-Meeting-Date'],
          "issue_from_date":data?.WmsTEIssueFromDate?.['issue-from-date'],
          "publish_date":data?.WmsTEPublishDate?.['publish_date'],
          "technical_bid_open_date":data?.WmsTETecnicalBidOpenDate?.['technical_bid_open_date'],
          "request_category":data?.WmsTMTenderCategory?.name,
          // "upload_document":imagePath[0]?.documentUid?.fileStoreId,
          "resolution_date":data?.WmsTEResolutionDate?.['resolution-date'],
          "prebid_meeting_location":data?.WmsTMMeetingLocation?.['meeting-location'],
          "issue_till_date":data?.WmsTEIssueTillDate?.['issue-till-date'],
          "financial_bid_open_date":data?.WmsTEFinancialBidOpenDate?.['financial-bid-open-date'],
          "validity":data?.WmsTMValidityOfTenderInDay?.['validity-of-tender-in-days'],
          "project_name":data?.WmsTMProjectName?.name,
          "tenantId": tenantId
        }]};
        console.log("payloadData ",payloadData)
        await mutate(payloadData);
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
            {/* {isSuccess && (
        <Toast
          error={showToast.key}
          label={t(showToast.label)}
          onClose={() => {
            setShowToast(null);
          }}
        />
        )} */}

      {showToast&&isError && <Toast error={showToast.key} label={t('Something went wrong!')} onClose={closeToast} />}
      {showToast&&isSuccess && <Toast label={t('Record submitted successfully')} onClose={closeToast} />}
    </div>
    </>)
}
export default TenderEntryAdd;