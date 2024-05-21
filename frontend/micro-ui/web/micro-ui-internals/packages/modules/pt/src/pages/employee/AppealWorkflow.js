import { Header, MultiLink } from "@egovernments/digit-ui-react-components";
import _, { forEach } from "lodash";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import ApplicationDetailsTemplate from "../../../../templates/ApplicationDetails";
import { newConfigMutate } from "../../config/Mutate/config";
import TransfererDetails from "../../pageComponents/Mutate/TransfererDetails";
import MutationApplicationDetails from "./MutationApplicatinDetails";
import getPTAcknowledgementData from "../../getPTAcknowledgementData";
import AppealDetails from "../citizen/Appeal/AppealDetails";
import ApplicationDetailsActionBar from "../../../../templates/ApplicationDetails/components/ApplicationDetailsActionBar";
import ApplicationDetailsToast from "../../../../templates/ApplicationDetails/components/ApplicationDetailsToast";
import { useQueryClient } from "react-query";
import ActionModal from "../../../../templates/ApplicationDetails/Modal";
import ApplicationDetailsWarningPopup from "../../../../templates/ApplicationDetails/components/ApplicationDetailsWarningPopup";

const PrimaryDownlaodIconCustom = () => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="#0f4f9e">
      <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z" />
    </svg>
  );

// const assessmentDataSearch =async (tenantId)=>{
//     const assData = await Digit.PTService.assessmentSearch({ tenantId, filters: { assessmentNumbers:'MN-AS-2024-04-14-000289' } });
//     console.log("assData===",assData)
//     return assData?.Assessments;
// } 

const getAppealData = async (tenantId, appealId, setAppDetailsToShow, updateCanFetchBillData) => {
    const appealData = await Digit.PTService.appealSearch({ tenantId, filters: { appealid:appealId } });
    let billData = {};
    console.log("Appeal Workflow===",appealData)
    if (appealData?.Appeals?.length > 0) {
        setAppDetailsToShow(appealData?.Appeals[0])
     
    }
    updateCanFetchBillData({
        loading: false,
        loaded: false,
        canLoad: false,
      });
  };
  const getFileData = async (filesArray,tenantId, setPdfFiles,updateCanFetchFileData) => {
    const fileData = await Digit.UploadServices.Filefetch(filesArray, tenantId);
    console.log("fileData==",fileData)
    if (fileData?.data) {
        setPdfFiles(fileData.data)
     
    }
    updateCanFetchFileData({
        loading: false,
        loaded: false,
        canLoad: false,
      });
  };



const AppealWorkflow = () => {
  const { t } = useTranslation();
  const state = Digit.ULBService.getStateId();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { tenants } = storeData || {};
  const { id: appealId } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [appDetailsToShow, setAppDetailsToShow] = useState({});
  const [showOptions, setShowOptions] = useState(false);
  const [enableAudit, setEnableAudit] = useState(false);
  const [businessService, setBusinessService] = useState("PT.APPEAL");

  const [displayMenu, setDisplayMenu] = useState(false);
  const [selectedAction, setSelectedAction] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [isEnableLoader, setIsEnableLoader] = useState(false);
  const [isWarningPop, setWarningPopUp] = useState(false);
  const queryClient = useQueryClient();

  const [billData, updateCanFetchBillData] = useState({
    loading: false,
    loaded: false,
    canLoad: true,
  });
  
if(appealId && billData?.canLoad) {
    getAppealData(tenantId, appealId, setAppDetailsToShow,updateCanFetchBillData)
}
  console.log("appDetailsToShow===",appDetailsToShow)

  const [pdfFiles, setPdfFiles] = useState({});
  const [fileData, updateCanFetchFileData] = useState({
    loading: false,
    loaded: false,
    canLoad: true,
  });
  
  const filesArray = appDetailsToShow && appDetailsToShow?.documents?.length>0 && appDetailsToShow.documents?.map((value) => value?.fileStoreId) || [];
    if(filesArray?.length && fileData?.canLoad) {
        getFileData(filesArray,Digit.ULBService.getStateId(), setPdfFiles,updateCanFetchFileData)
    }
    if(appDetailsToShow && appDetailsToShow?.documents?.length>0 && pdfFiles && pdfFiles?.fileStoreIds?.length>0) {
        appDetailsToShow?.documents.forEach(element => {
            pdfFiles.fileStoreIds.forEach(file => {
                if(element?.fileStoreId === file.id) {
                    element.url = file?.url
                }
            });
        });
    }

    let workflowDetails = Digit.Hooks.useWorkflowDetails({
        tenantId: tenantId,
        id: appDetailsToShow?.acknowldgementNumber,
        // applicationDetails?.applicationData?.acknowldgementNumber,
        moduleCode: businessService,
        role: "PT_CEMP",
    });
    console.log("workflowDetails==",workflowDetails)
  
  
   console.log("pdfFiles===",pdfFiles)
   const {
    isLoading: updatingApplication,
    isError: updateApplicationError,
    data: updateResponse,
    error: updateError,
    mutate,
  } = Digit.Hooks.pt.useApplicationActions(tenantId, 'PT.APPEAL');

   function onActionSelect(action) {
    console.log("onActionSelect==",action)
    if (action) {
      if(action?.isToast){
        setShowToast({ key: "error", error: { message: action?.toastMessage } });
        setTimeout(closeToast, 5000);
      }
      else if (action?.isWarningPopUp) {
        setWarningPopUp(true);
      } else if (action?.redirectionUrll) {
        if (action?.redirectionUrll?.action === "ACTIVATE_CONNECTION") {
          // window.location.assign(`${window.location.origin}digit-ui/employee/ws/${action?.redirectionUrll?.pathname}`, { data: action?.redirectionUrll?.state });

          history.push(`${action?.redirectionUrll?.pathname}`, JSON.stringify({ data: action?.redirectionUrll?.state, url: `${location?.pathname}${location.search}` }));
        }
        else if (action?.redirectionUrll?.action === "RE-SUBMIT-APPLICATION"){
          history.push(`${action?.redirectionUrll?.pathname}`, { data: action?.redirectionUrll?.state });
        }
        else {
          window.location.assign(`${window.location.origin}/digit-ui/employee/payment/collect/${action?.redirectionUrll?.pathname}`);
        }
      } else if (!action?.redirectionUrl) {
        setShowModal(true);
      } else {
        history.push({
          pathname: action.redirectionUrl?.pathname,
          state: { ...action.redirectionUrl?.state },
        });
      }
    }
    setSelectedAction(action);
    setDisplayMenu(false);
  }
  const closeToast = () => {
    setShowToast(null);
  };
  const closeModal = () => {
    setSelectedAction(null);
    setShowModal(false);
  };

  const closeWarningPopup = () => {
    setWarningPopUp(false);
  };
  const submitAction = async (data, nocData = false, isOBPS = {}) => {
    setIsEnableLoader(true);
    if (typeof data?.customFunctionToExecute === "function") {
      data?.customFunctionToExecute({ ...data });
    }
    if (nocData !== false && nocMutation) {
      const nocPrmomises = nocData?.map((noc) => {
        return nocMutation?.mutateAsync(noc);
      });
      try {
        setIsEnableLoader(true);
        const values = await Promise.all(nocPrmomises);
        values &&
          values.map((ob) => {
            Digit.SessionStorage.del(ob?.Noc?.[0]?.nocType);
          });
      } catch (err) {
        setIsEnableLoader(false);
        let errorValue = err?.response?.data?.Errors?.[0]?.code
          ? t(err?.response?.data?.Errors?.[0]?.code)
          : err?.response?.data?.Errors?.[0]?.message || err;
        closeModal();
        setShowToast({ key: "error", error: { message: errorValue } });
        setTimeout(closeToast, 5000);
        return;
      }
    }
    if (mutate) {
      setIsEnableLoader(true);
      mutate(data, {
        onError: (error, variables) => {
          setIsEnableLoader(false);
          setShowToast({ key: "error", error });
          setTimeout(closeToast, 5000);
        },
        onSuccess: (data, variables) => {
          sessionStorage.removeItem("WS_SESSION_APPLICATION_DETAILS");
          setIsEnableLoader(false);
          if (isOBPS?.bpa) {
            data.selectedAction = selectedAction;
            history.replace(`/digit-ui/employee/obps/response`, { data: data });
          }
          if (isOBPS?.isStakeholder) {
            data.selectedAction = selectedAction;
            history.push(`/digit-ui/employee/obps/stakeholder-response`, { data: data });
          }
          if (isOBPS?.isNoc) {
            history.push(`/digit-ui/employee/noc/response`, { data: data });
          }
          if (data?.Amendments?.length > 0 ){
            //RAIN-6981 instead just show a toast here with appropriate message
          //show toast here and return 
            //history.push("/digit-ui/employee/ws/response-bill-amend", { status: true, state: data?.Amendments?.[0] })
            
            if(variables?.AmendmentUpdate?.workflow?.action.includes("SEND_BACK")){
              setShowToast({ key: "success", label: t("ES_MODIFYSWCONNECTION_SEND_BACK_UPDATE_SUCCESS")})
            } else if (variables?.AmendmentUpdate?.workflow?.action.includes("RE-SUBMIT")){
              setShowToast({ key: "success", label: t("ES_MODIFYSWCONNECTION_RE_SUBMIT_UPDATE_SUCCESS") })
            } else if (variables?.AmendmentUpdate?.workflow?.action.includes("APPROVE")){
              setShowToast({ key: "success", label: t("ES_MODIFYSWCONNECTION_APPROVE_UPDATE_SUCCESS") })
            }
            else if (variables?.AmendmentUpdate?.workflow?.action.includes("REJECT")){
              setShowToast({ key: "success", label: t("ES_MODIFYWSCONNECTION_REJECT_UPDATE_SUCCESS") })
            }            
            return
          }
          setShowToast({ key: "success", action: selectedAction });
          clearDataDetails && setTimeout(clearDataDetails, 3000);
          setTimeout(closeToast, 5000);
          queryClient.clear();
          queryClient.refetchQueries("APPLICATION_SEARCH");
          //push false status when reject
          
        },
      });
    }

    closeModal();
  };

  return (
    <div>
        <div className={"employee-application-details"} style={{ marginBottom: "15px" }}>
      <Header styles={{ marginLeft: "0px", paddingTop: "10px", fontSize: "16px" }}>{t("Property Appeal")}</Header>
      {/* {dowloadOptions && dowloadOptions.length > 0 && (
            <MultiLink
              className="multilinkWrapper employee-mulitlink-main-div"
              onHeadClick={() => setShowOptions(!showOptions)}
              displayOptions={showOptions}
              options={dowloadOptions}
              downloadBtnClassName={"employee-download-btn-className"}
              optionsClassName={"employee-options-btn-className"}
            />
          )} */}
          </div>
          <div>
          <div className="card" style={{minWidth: "100%"}}>
                <div><h2 style={{fontWeight: "600", marginBottom: "20px"}}>Appeal Details</h2></div>
                <div className="row">
                    <div className="col-sm-6">
                        <label>Appeal No</label>
                        <div>{appDetailsToShow?.appealId}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>For the year</label>
                        <div>{appDetailsToShow?.assessmentYear}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Name of Owner</label>
                        <div>{appDetailsToShow?.ownerName}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Property Address</label>
                        <div>{appDetailsToShow?.propertyAddress}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Property Id</label>
                        <div>{appDetailsToShow?.propertyId}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Name of Assessing Officer</label>
                        <div>{appDetailsToShow?.nameOfAssessingOfficer}</div>
                    </div>
                    <div className="col-sm-6">
                        <label>Assessing Officer Designation</label>
                        <div>{appDetailsToShow?.assessingOfficerDesignation}</div>
                    </div>
                </div>
                <hr style={{marginBottom: "10px", marginTop: "10px"}} />
                <div className="row">
                    <div className="col-sm-4">
                        <label>Rule under which order passed</label>
                        <div>{appDetailsToShow?.ruleUnderOrderPassed}</div>
                    </div>
                    <div className="col-sm-4">
                        <label>Date of Order</label>
                        <div>{appDetailsToShow?.dateOfOrder}</div>
                    </div>
                    <div className="col-sm-4">
                        <label>Date of Service</label>
                        <div>{appDetailsToShow?.dateOfService}</div>
                    </div>
                </div>
                <hr style={{marginBottom: "10px", marginTop: "10px"}} />
                <div>
                <div style={{fontWeight: "600", marginBottom: "10px"}}>Admitted tax liability under The Manipur Municipality Rules, 2019</div>
                    <div className="row">
                        <div className="col-sm-4">
                            <label>Date of Payment</label>
                            <div>{appDetailsToShow?.dateOfPayment}</div>
                        </div>
                        <div className="col-sm-4">
                            <label>Applicant Address</label>
                            <div>{appDetailsToShow?.applicantAddress}</div>
                        </div>
                        <div className="col-sm-4">
                            <label>Relief Claimed in Appeal</label>
                            <div>{appDetailsToShow?.reliefClaimedInAppeal}</div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col-sm-12">
                            <label>Statement of Facts</label>
                            <div>{appDetailsToShow?.statementOfFacts}</div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col-sm-12">
                            <label>Ground of Appeal</label>
                            <div>{appDetailsToShow?.groundOfAppeal}</div>
                        </div>
                    </div>
                </div>
                <hr style={{marginBottom: "10px", marginTop: "10px"}} />
                <div style={{fontWeight: "600", marginBottom: "10px"}}>List of Documents</div>
                <div>
                <div className="row appeal-row-cls">
                    <div className="col-sm-12">
                    {appDetailsToShow?.documents && appDetailsToShow?.documents.length>0 &&
                        <div style={{ width: '100%' }}>
                          <table style={{ width: '100%', border: '1px solid #b7b7b7'}}>
                            <tr style={{background: '#eaeaea', lineHeight: '35px'}}>
                              <th style={{paddingLeft: "10px"}}>Sr. No.</th>
                              <th>Document Type</th>
                              <th>Download</th>
                            </tr>
                            {appDetailsToShow?.documents.map((e, i)=>{
                              return (<tr>
                                <td style={{paddingLeft: "10px"}}>{i+1}</td>
                                <td>{e?.documentType}</td>
                                <td><a href={e.url} target="_blank"><PrimaryDownlaodIconCustom /></a></td>
                              </tr>)
                            })}
                          </table>
                        </div>}
                    </div>
                    
                </div>
                </div>
            </div>
          </div>

        <div>
        {showModal ? (
            <ActionModal
              t={t}
              action={selectedAction}
              tenantId={tenantId}
              state={state}
              id={appealId}
              applicationDetails={appDetailsToShow}
              assmentSearchData={appDetailsToShow}
              userRole={'EXECUTING_OFFICER'}
              applicationData={appDetailsToShow}
              closeModal={closeModal}
              submitAction={submitAction}
              actionData={workflowDetails?.data?.timeline}
              businessService={businessService}
              workflowDetails={workflowDetails}
              moduleCode={"PT"}
            />
          ) : null}
          {isWarningPop ? (
            <ApplicationDetailsWarningPopup
              action={selectedAction}
              workflowDetails={workflowDetails}
              businessService={businessService}
              isWarningPop={isWarningPop}
              closeWarningPopup={closeWarningPopup}
            />
          ) : null}
          <ApplicationDetailsToast t={t} showToast={showToast} closeToast={closeToast} businessService={businessService} />
          <ApplicationDetailsActionBar
            workflowDetails={workflowDetails}
            displayMenu={displayMenu}
            onActionSelect={onActionSelect}
            setDisplayMenu={setDisplayMenu}
            businessService={businessService}
            forcedActionPrefix={"WF_EMPLOYEE_ASMT"}
            ActionBarStyle={{float: "right"}}
            MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
          /> 
        </div>
      {/* <ApplicationDetailsTemplate
        applicationDetails={appDetailsToShow}
        assmentSearchData={assmentSearchData}
        isLoading={isLoading}
        isDataLoading={isLoading}
        applicationData={appDetailsToShow?.applicationData}
        mutate={mutate}
        workflowDetails={workflowDetails}
        businessService={businessService}
        moduleCode="PT"
        showToast={showToast}
        setShowToast={setShowToast}
        closeToast={closeToast}
        userRole={'ASSIGNING_OFFICER'}
        timelineStatusPrefix={""}
        forcedActionPrefix={"WF_EMPLOYEE_ASMT"}
        statusAttribute={"state"}
        MenuStyle={{ color: "#FFFFFF", fontSize: "18px" }}
        ActionBarStyle={{float: "right"}}
      /> */}
    
    </div>
  );
};

export default React.memo(AppealWorkflow);
