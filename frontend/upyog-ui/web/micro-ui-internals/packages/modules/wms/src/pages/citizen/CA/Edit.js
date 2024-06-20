import React, { Fragment, createContext, useContext, useEffect, useState } from "react";
import { newConfig } from "../../../components/config/CA/ca-config";
import { Banner,Card, FormComposer, Header, Loader, Toast } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import Timeline from "../../../components/TimeLineCA";
// export const StepContext = createContext()

const ContractAgreementEdit = () => {
  const history = useHistory();
  const { id } = useParams();
  console.log("history params ", history, id);
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [showToast, setShowToast] = useState(null);
  const [isTrue, setisTrue] = useState(false);
  const [imagePath, setimagePath] = useState();
  const [draftData, setDraftData] = useState([]);
  // const { mutate, isSuccess, isError, error, data: addResponseData, ...rest } = Digit?.Hooks?.wms?.ca?.useWmsCAAdd();
  // console.log("CA create data response",{isSuccess,isError,error, data, rest})
  // console.log("addResponseData ",addResponseData)

  // const singleData = Digit?.Hooks?.wms?.ca?.useWmsCAGet(data?.id?data?.id:'2') || {};
  // const singleData = Digit?.Hooks?.wms?.ca?.useWmsCAGet((addResponseData?.id)?addResponseData.id:"47","CASingleList") || {};
  const singleData = Digit?.Hooks?.wms?.ca?.useWmsCAGet(id, "CASingleList") || {};
  
  const updateData = Digit?.Hooks?.wms?.ca?.useWmsCAUpdate(id);

  useEffect(async () => {
    if (singleData?.data) {
      await setDraftData([...draftData, singleData?.data?.WMSContractAgreementApplication[0]]);
    }
    singleData.refetch();
  }, [singleData?.data]);

  useEffect(() => {
    localStorage.removeItem("resId");
    singleData.refetch();
  }, []);

  const getDataimg = (d) => {
    setimagePath(d);
  };
 
  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    console.log("onSelect TLInfo key, data, skipStep, index, isAddMultiple = false Index", { key, data, skipStep, index, isAddMultiple });
    console.log("onSelect TLInfo key, data,  Index", { email: data?.email, department: data?.department });
  }
 
  
  console.log("draftData index edit ", draftData);
  console.log("singleData edit ", singleData);
  console.log("draftData[0]?.contractors index edit ", draftData[0]?.agreement);

  // console.log("singleData edit singleData?.WMSContractAgreementApplication[0]?.contractors ", singleData && singleData?.WMSContractAgreementApplication[0] && singleData?.WMSContractAgreementApplication[0]?.contractors);
  

  const [showHideToast,setShowHideToast] = useState(false)
    // useEffect(async()=>{
    //   if(isSuccess){
    //     setShowToast({...showToast,key:"",label:"Successfully data update as draft"});
    //     // localStorage.setItem("resId",addResponseData?.id)
    //     setShowHideToast(true)
    //   }
    //   if(isError){
    //     setShowToast({...showToast,key:"error",label:"Somthing went wrong!"});
    //     setShowHideToast(true)
    //     }
    // },[isSuccess,isError])

    useEffect(()=>{
      if(updateData?.isSuccess){
        setShowToast({...showToast,key:"",label:"Successfully data update as draft"});
        setShowHideToast(true)
      }
      if(updateData?.isError){
        setShowToast({...showToast,key:"error",label:"Somthing went wrong!"});
        setShowHideToast(true)
        }
    },[updateData?.isSuccess,updateData?.isError])
    const closeToast = () => {
      setShowHideToast(false)
    };
    useEffect(()=>{
      if(showHideToast){
      setTimeout(() => {
        closeToast();
        // history.replace('/upyog-ui/citizen/wms/contract-agreement/list')
      }, 5000);
    }
    },[showToast])

  // ######################################################### Start
  let initialData = {
    WMSContractAgreementApplication: [
      {
        contractors: [],
        party2_witness: [],
        agreement: [],
        sDPGBGDetails: [],
        party1Details: [],
        termsAndConditions: [],
      },
    ],
  };
  const [allData, setAllData] = useState(initialData);
  // const [draftDataIsTrue, setDraftDataIsTrue] = useState(true);
  // useEffect(()=>{
  //   if(draftDataIsTrue){
  //   if((draftData[0]?.contractors)?.length>0){
  //     // allData?.WMSContractAgreementApplication?.forEach((res) => (res.contractors = []));
  //     // allData?.WMSContractAgreementApplication?.contractors.splice(0, (allData?.contractors)?.length);

  //   allData?.WMSContractAgreementApplication[0]?.contractors.push(...draftData[0]?.contractors)
  //   }
  //   if((draftData[0]?.party2_witness)?.length>0){
  //     // allData?.WMSContractAgreementApplication?.forEach((res) => (res.party2_witness = []));
  //     // allData?.WMSContractAgreementApplication?.party2_witness.splice(0, (allData?.party2_witness)?.length);

  //     allData?.WMSContractAgreementApplication[0]?.party2_witness.push(...draftData[0]?.party2_witness)
  //   }
  //   if((draftData[0]?.agreement)?.length>0){
  //     // allData?.WMSContractAgreementApplication?.agreement.splice(0, (allData?.agreement)?.length);
  //     allData?.WMSContractAgreementApplication[0]?.agreement.push(...draftData[0]?.agreement)
  //   }
  //   if((draftData[0]?.sDPGBGDetails)?.length>0){
  //     // allData?.WMSContractAgreementApplication?.sDPGBGDetails.splice(0, (allData?.sDPGBGDetails)?.length);
  //     allData?.WMSContractAgreementApplication[0]?.sDPGBGDetails.push(...draftData[0]?.sDPGBGDetails)
  //   }
  //   if((draftData[0]?.party1Details)?.length>0){
  //     // allData?.WMSContractAgreementApplication?.party1Details.splice(0, (allData?.party1Details)?.length);
  //     allData?.WMSContractAgreementApplication[0]?.party1Details.push(...draftData[0]?.party1Details)
  //   }
  //   if((draftData[0]?.termsAndConditions)?.length>0){
  //     // allData?.WMSContractAgreementApplication?.termsAndConditions.splice(0, (allData?.termsAndConditions)?.length);
  //     allData?.WMSContractAgreementApplication[0]?.termsAndConditions.push(...draftData[0]?.termsAndConditions)
  //   }
  // }
  // },[draftData])
  // debugger
console.log("allData im ", allData);

  //Filter duplicate value function
  function deleteDuplicateArrayOfObject(array1, array2) {
    const objectKeys = array1[0] === undefined ? "" : Object.keys(array1[0]);

    console.log("onjKey ", objectKeys);
    // console.log('onjKey ', dObject.keys(d));

    // Combine arrays
    const combinedArray = [...array1, ...array2];

    // Custom logic function to determine if two objects are duplicates
    const isDuplicate = (obj1, obj2) => {
      // Your custom logic here
      return obj1.agreement_no === obj2.agreement_no && obj1.loa_no === obj2.loa_no;
    };

    // Filter the combined array based on custom logic to keep only unique objects
    const uniqueArray = combinedArray.filter((obj, index, self) => self.findIndex((o) => isDuplicate(o, obj)) === index);

    // console.log('Unique array of objects:', uniqueArray);
    return uniqueArray;
  }


console.log("allData im sec ", allData);
  //payload Data
  function payloadData(item) {
    // setDraftDataIsTrue(false)
    console.log("item item item item ",item)
    if (Object.keys(item)[0] === "party2") {
      // contractors and  party2_witness value assign empty array
      // debugger
      // allData?.WMSContractAgreementApplication?.forEach((res) => (res.contractors = []));
      // allData?.WMSContractAgreementApplication?.forEach((res) => (res.party2_witness = []));
      
      // console.log("ddddd ",allData?.contractors.splice(0, (allData?.contractors)?.length));
      // allData?.party2_witness.splice(0, (allData?.party2_witness)?.length);
      // console.log("ddddd ",allData?.contractors.splice(0, (allData?.contractors)?.length));
      // allData?.party2_witness.splice(0, (allData?.party2_witness)?.length);

      // console.log("ddddd ",allData?.contractors?.length=0);
      allData?.contractors.splice(0, (allData?.contractors)?.length);
      allData?.party2_witness.splice(0, (allData?.party2_witness)?.length);

// setAllData({...allData,contractors:[],party2_witness:[]});
// console.log("allData allData ",allData)
      //push data in contractors and  party2_witness
      // allData?.WMSContractAgreementApplication[0]?.contractors.push(...item?.party2?.contractors);
      // allData?.WMSContractAgreementApplication[0]?.party2_witness.push(...item?.party2?.party2_witness);
      allData?.contractors.push(...item?.party2?.contractors);
      allData?.party2_witness.push(...item?.party2?.party2_witness);

    }
    if (Object.keys(item)[0] == "agreement") {
      // const removeDuplicate = deleteDuplicateArrayOfObject(
      //   allData?.WMSContractAgreementApplication[0]?.agreement,
      //   item?.agreement
      // );

      // agreement value assign empty array
      // allData?.WMSContractAgreementApplication?.forEach((res) => (console.log("res ", res?.agreement), (res.agreement = [])));
      allData?.agreement.splice(0, (allData?.agreement)?.length);
      // console.log('res allData ', allData)

      //push data in agreement array
      // allData?.WMSContractAgreementApplication[0]?.agreement.push(...removeDuplicate);
      // allData?.WMSContractAgreementApplication[0]?.agreement.push(...item?.agreement);
      allData?.agreement.push(...item?.agreement);
    }
    if (Object.keys(item)[0] == "sdpgbg") {
      // sDPGBGDetails value assign empty array
      // allData?.WMSContractAgreementApplication?.forEach((res) => (console.log("res ", res?.sDPGBGDetails), (res.sDPGBGDetails = [])));
      allData?.sDPGBGDetails.splice(0, (allData?.sDPGBGDetails)?.length);
      
      // console.log('res allData ', allData)

      //push data in agreement array
      // allData?.WMSContractAgreementApplication[0]?.sDPGBGDetails.push(...removeDuplicate);
      // allData?.WMSContractAgreementApplication[0]?.sDPGBGDetails.push(...item?.sdpgbg);
      allData?.sDPGBGDetails.push(...item?.sdpgbg);
    }
    if (Object.keys(item)[0] == "partyOne") {
      // party1Details value assign empty array
      // allData?.WMSContractAgreementApplication?.forEach((res) => (console.log("res ", res?.party1Details), (res.party1Details = [])));
      allData?.party1Details.splice(0, (allData?.party1Details)?.length);
      
      // console.log('res allData ', allData)

      //push data in agreement array
      // allData?.WMSContractAgreementApplication[0]?.party1Details.push(...removeDuplicate);

      // allData?.WMSContractAgreementApplication[0]?.party1Details.push(...item?.partyOne);
      allData?.party1Details.push(...item?.partyOne);
    }
    if (Object.keys(item)[0] == "termAndCondition") {
      // termsAndConditions value assign empty array
      // allData?.WMSContractAgreementApplication?.forEach((res) => (console.log("res ", res?.termsAndConditions), (res.termsAndConditions = [])));
      allData?.termsAndConditions.splice(0, (allData?.termsAndConditions)?.length);

      // console.log('res allData ', allData)

      //push data in agreement array
      // allData?.WMSContractAgreementApplication[0]?.termsAndConditions.push(...removeDuplicate);

      // allData?.WMSContractAgreementApplication[0]?.termsAndConditions.push(...item?.termAndCondition);
      allData?.termsAndConditions.push(...item?.termAndCondition);
    }
    setAllData({ ...allData });
  }
  // ######################################################### End
   //All Stteper Error handle on final submit
   const [formError,setFormError]=useState({contractor:false,agreement:false,partyone:false});
 
  const onSubmit = async (item) => {
    // debugger;
    // const data1 = {...item,}
    console.log("CA add data item ", item);
    // console.log("CA add data item ... ", ...item);
        console.log("CA add data allData third ", allData);

        if(item.hasOwnProperty("party2")){  
          // if("party2" in item){  
            console.log("item?.party2[0].contractors.length ",item?.party2?.contractors.length)
          // if(Object.keys(item && item?.party2)[0] === "contractors"){
            if(Boolean(item?.party2?.contractors.length)){
            setFormError({...formError,contractor:false})
          }else{
          return false
        }
      }
        if (item.hasOwnProperty("agreement")){
          // if ("agreement" in item){
          // if (Object.keys(item)[0] === "agreement") {
          if (Boolean(item?.agreement?.length)) {
          setFormError({...formError,agreement:false})
        }else{
          return false
        }
      }
        if (item.hasOwnProperty("partyOne")) {
          // if ("partyone" in item) {
          // if (Object.keys(item)[0] === "partyOne") {
            if (Boolean(item?.partyOne?.length)) {
          setFormError({...formError,partyone:false})
        }else{
          return false
        }
      }
    // debugger
        if(item.hasOwnProperty("termAndCondition")){ 
          let returnVal=false 

          // if("termAndCondition" in item){  
          if (Object.keys(item)[0] === "termAndCondition") {
          for(let i=0;i<5;i++){
            setNextStep(i)
          }
          let formErrorContrator
          let formErrorAgreement
          let formErrorPartyOne
          // allData.WMSContractAgreementApplication?.forEach((res) => {
            [allData]?.forEach((res) => {
          console.log("Clicked another comp index item Value ", res);
          if((res.contractors).length>0){
            formErrorContrator = false
          }else{
            returnVal =true
            formErrorContrator = true
            // return false
          }
          if((res.agreement).length>0){
            formErrorAgreement = false
          }else{
            returnVal =true
            formErrorAgreement = true
            // return false
          }
          if((res.party1Details).length>0){
            formErrorPartyOne = false
          }else{
            returnVal =true
            formErrorPartyOne = true
            // return false
          }
    
          //stop sbumit while all steps mandatory field not fill.
            // if((res.contractors).length<1){
            // returnVal =true
            // }
            // if((res.agreement).length<1){
            // returnVal =true
            // }
            // if((res.contractors).length<1 && (res.agreement).length<1){
            // returnVal =true
            // }
            // if((res.contractors).length>0 && (res.agreement).length>0){
            // returnVal =false
            // }
          });
          setFormError({...formError,contractor:formErrorContrator,agreement:formErrorAgreement,partyone:formErrorPartyOne})
          if(returnVal){
            return
          }
        }
        
      } 
    await payloadData(item);

    // const getPayload = JSON.parse(localStorage.getItem("formData"))
    // console.log("getPayload ",getPayload)
    // let payloadDataUpdate = {WMSContractAgreementApplication:[ {...allData}] };

    let payloadDataUpdate;
        if(Object.keys(item)[0]==="termAndCondition"){
           payloadDataUpdate = {WMSContractAgreementApplication:[ {...allData}],
            status:"pending",
          }
        }else{
           payloadDataUpdate = {WMSContractAgreementApplication:[ {...allData}],
            status:"draft",
          }
        }
    // let payloadDataUpdate = { ...allData, id: id };
    console.log("CA update data payload", payloadDataUpdate);
  
    await updateData?.mutate(payloadDataUpdate);
  };
  const configs = newConfig ? newConfig : newConfig;
  const [nextStep, setNextStep] = useState(0);

  const [formSubmitMessag, setFormSubmitMessag] = useState(false);
  useEffect(() => {
    if (updateData?.isSuccess && updateData?.data?.status == "pending") {
      setFormSubmitMessag(true);
    }if (updateData?.isSuccess && updateData?.data?.status == "draft") {
      setFormSubmitMessag(false);
    } if (updateData?.isError)  {
      setFormSubmitMessag(true);
    }
  }, [updateData?.isSuccess, !updateData?.isSuccess,updateData?.isError]);
  const goToList = () => {
    history.replace("/upyog-ui/citizen/wms/contract-agreement/list");
    localStorage.clear("resId");
    // alert("link")
  };

  if (draftData?.length === 0) {
    return <Loader />;
  }
  return (
    <>
    {formSubmitMessag ? (
      <Card>
      <Banner
        successful={updateData?.isSuccess}
        message={updateData?.isSuccess ? "Updatted Successfully!" : "Something Went Wrong!"}
        info={updateData?.isSuccess ? "Update success info message will come" : "Error info message will come"}
      />
      <button className="submit-bar" type="button" style={{ color: "white" }} onClick={goToList}>
        Go To List {/* {`${t("PT_SEARCH")}`} */}
      </button>
    </Card>
    ) : (
    <>
      {true ? <Timeline currentStep={nextStep+1} businessService={"WMS"} /> : null}
      <Card>
        <Header>{t("WMS_CA_EDIT_CONTRACT_AGREEMENT")}</Header>
        {configs.map((routeObj, index) => {
          console.log("component ddd routeObj ", routeObj);
          const { component, texts, inputs, key, isSkipEnabled } = routeObj.body[nextStep];
          console.log("component ddd ", component);
          const Component = typeof component === "string" ? Digit.ComponentRegistryService.getComponent(component) : component;
          return (
            // <Route path={`${match.path}/${routeObj.route}`} key={index}>
            // (draftData?.length>0)&&(
            <Component
              // config={{ texts, inputs, key, isSkipEnabled }}
              config={{ texts, inputs, key, isSkipEnabled }}
              onSelect={handleSelect}
              onSubmit={onSubmit}
              onNext={setNextStep}
              // draftData={singleData?.data?.WMSContractAgreementApplication[0]}
              draftData={draftData}
              formError={formError}

              // onSkip={handleSkip}
              // t={t}
              // formData={params}
              // onAdd={handleMultiple}
              // userType="citizen"
            />
            // )
            // </Route>
          );
        })}

        {showHideToast && (
          <Toast
            error={showToast.key}
            label={t(showToast.label)}
            onClose={() => {
              setShowHideToast(false)
            }}
          />
        )}
      </Card>
      {/* </StepContext.Provider> */}
      <style>
      {`
      
      iframe{background-color:blue;display:none}
      `}
      </style>
    </>
  )}
  </>
  );
};
export default ContractAgreementEdit;
