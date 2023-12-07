import React, { Fragment, createContext, useContext, useEffect, useState } from "react";
import { newConfig } from "../../../components/config/CA/ca-config";
import { Banner, Card, FormComposer, Header, Loader, SubmitBar, Toast } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import Timeline from "../../../components/TimeLineCA";
import { useHistory } from "react-router-dom";
// export const StepContext = createContext()

const ContractAgreementAdd = () => {
  const history = useHistory();
  // console.log("history ",history)
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [showToast, setShowToast] = useState(null);
  const [isTrue, setisTrue] = useState(true);
  const [formSubmitMessag, setFormSubmitMessag] = useState(false);
  const [imagePath, setimagePath] = useState();
  const [draftData, setDraftData] = useState([]);

  const [nextStep, setNextStep] = useState(0);
  const { mutate, isSuccess, isError, error, data: addResponseData, ...rest } = Digit?.Hooks?.wms?.ca?.useWmsCAAdd();
  // console.log("CA create data response",{isSuccess,isError,error, data, rest})
  // console.log("CA create data response",{isSuccess,isError,error})

  // const singleData = Digit?.Hooks?.wms?.ca?.useWmsCAGet(data?.id?data?.id:'2') || {};
  // const singleData = Digit?.Hooks?.wms?.ca?.useWmsCAGet((addResponseData?.id)?addResponseData.id:"47","CASingleList") || {};
  const singleData =
    Digit?.Hooks?.wms?.ca?.useWmsCAGet(addResponseData?.id ? addResponseData.id : localStorage.getItem("resId"), "CASingleList") || {};
  console.log("bankList sttepr single data index singleData ", singleData);

  const { data: getCaSingleData } = singleData;
  // debugger
  console.log("bankList sttepr single data index getCaSingleData ", getCaSingleData);
  console.log("bankList sttepr single data index singleData?.data", singleData?.data);
  console.log(
    "bankList sttepr single data index singleData?.data?.WMSContractAgreementApplication ",
    singleData?.data?.WMSContractAgreementApplication
  );

  // useEffect(() => {
  //   setisTrue(true)
  //   singleData.refetch()
  // }, []);
  // const {mutate:mutateUpdate,isSuccess:successUpdate,isError:errorUpdate, data:dataUpdate} = Digit?.Hooks?.wms?.ca?.useWmsCAUpdate();
  const updateData = Digit?.Hooks?.wms?.ca?.useWmsCAUpdate(addResponseData?.id || localStorage.getItem("resId"));

  console.log("updateData data ", updateData);

  console.log("draftData index ", draftData);
  useEffect(() => {
    if (updateData?.isSuccess && updateData?.data?.status == "pending") {
      setFormSubmitMessag(true);
    }if (updateData?.isSuccess && updateData?.data?.status == "draft") {
      setFormSubmitMessag(false);
    } if (updateData?.isError)  {
      setFormSubmitMessag(true);
    }
  }, [updateData?.isSuccess, !updateData?.isSuccess,updateData?.isError]);

  useEffect(async () => {
    singleData.refetch();
    if (singleData?.data) {
      // await setDraftData([...draftData,singleData?.data?.WMSContractAgreementApplication[0]])
      await setDraftData([singleData?.data?.WMSContractAgreementApplication[0]]);
    }
  }, [singleData?.data, updateData?.isSuccess]);

  const getDataimg = (d) => {
    setimagePath(d);
  };
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
  const [showHideToast, setShowHideToast] = useState(false);
  useEffect(async () => {
    if (isSuccess) {
      setShowToast({ ...showToast, key: "", label: "Successfully data saved as draft" });
      localStorage.setItem("resId", addResponseData?.id);
      setShowHideToast(true);
    }
    if (isError) {
      setShowToast({ ...showToast, key: "error", label: "Somthing went wrong!" });
      setShowHideToast(true);
    }
  }, [isSuccess, isError]);

  useEffect(() => {
    if (updateData?.isSuccess) {
      setShowToast({ ...showToast, key: "", label: "Successfully data saved as draft" });
      setShowHideToast(true);
    }
    if (updateData?.isError) {
      setShowToast({ ...showToast, key: "error", label: "Somthing went wrong!" });
      setShowHideToast(true);
    }
  }, [updateData?.isSuccess, updateData?.isError]);

  const closeToast = () => {
    setShowHideToast(false);
  };
  useEffect(() => {
    if (showHideToast) {
      setTimeout(() => {
        closeToast();
        // history.replace('/upyog-ui/citizen/wms/contract-agreement/list')
      }, 5000);
    }
  }, [showToast]);

  // const [params,setParams]=useState();
  function handleSelect(key, data, skipStep, index, isAddMultiple = false) {
    console.log("onSelect TLInfo key, data, skipStep, index, isAddMultiple = false Index", { key, data, skipStep, index, isAddMultiple });
    console.log("onSelect TLInfo key, data,  Index", { email: data?.email, department: data?.department });

    //   if(key === "formData")
    //   setParams({...data})
    //   else{
    //   setParams({ ...params, ...{ [key]: { ...params[key], ...data } } });
    //   if(key === "isSkip" && data === true)
    //   {
    //     goNext(skipStep, index, isAddMultiple, key, true);
    //   }
    //   else
    //   {
    //     goNext(skipStep, index, isAddMultiple, key);
    //   }
    // }
  }
  // console.log("onSelect TLInfo parmass",params)

  // ######################################################### Start
  let initialData = {
    WMSContractAgreementApplication: [
      {
        contractors:[],
        party2_witness:[],
        agreement:[],
        sDPGBGDetails:[],
        party1Details:[],
        termsAndConditions:[],
      },
    ],
  };
  const [allData, setAllData] = useState(initialData);
  const [draftDataIsTrue, setDraftDataIsTrue] = useState(true);
  useEffect(()=>{
    if(draftDataIsTrue){
    if((draftData[0]?.contractors)?.length>0){
      // allData?.WMSContractAgreementApplication?.forEach((res) => (res.contractors = []));
      // allData?.WMSContractAgreementApplication?.contractors.splice(0, (allData?.contractors)?.length);

    allData?.WMSContractAgreementApplication[0]?.contractors.push(...draftData[0]?.contractors)
    }
    if((draftData[0]?.party2_witness)?.length>0){
      // allData?.WMSContractAgreementApplication?.forEach((res) => (res.party2_witness = []));
      // allData?.WMSContractAgreementApplication?.party2_witness.splice(0, (allData?.party2_witness)?.length);

      allData?.WMSContractAgreementApplication[0]?.party2_witness.push(...draftData[0]?.party2_witness)
    }
    if((draftData[0]?.agreement)?.length>0){
      // allData?.WMSContractAgreementApplication?.agreement.splice(0, (allData?.agreement)?.length);
      allData?.WMSContractAgreementApplication[0]?.agreement.push(...draftData[0]?.agreement)
    }
    if((draftData[0]?.sDPGBGDetails)?.length>0){
      // allData?.WMSContractAgreementApplication?.sDPGBGDetails.splice(0, (allData?.sDPGBGDetails)?.length);
      allData?.WMSContractAgreementApplication[0]?.sDPGBGDetails.push(...draftData[0]?.sDPGBGDetails)
    }
    if((draftData[0]?.party1Details)?.length>0){
      // allData?.WMSContractAgreementApplication?.party1Details.splice(0, (allData?.party1Details)?.length);
      allData?.WMSContractAgreementApplication[0]?.party1Details.push(...draftData[0]?.party1Details)
    }
    if((draftData[0]?.termsAndConditions)?.length>0){
      // allData?.WMSContractAgreementApplication?.termsAndConditions.splice(0, (allData?.termsAndConditions)?.length);
      allData?.WMSContractAgreementApplication[0]?.termsAndConditions.push(...draftData[0]?.termsAndConditions)
    }
  }
  },[draftData])

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
  //payload Data
  function payloadData(item) {
    setDraftDataIsTrue(false)
    if (Object.keys(item)[0] === "party2") {
      // contractors and  party2_witness value assign empty array
      allData?.WMSContractAgreementApplication?.forEach((res) => (res.contractors = []));
      allData?.WMSContractAgreementApplication?.forEach((res) => (res.party2_witness = []));

      //push data in contractors and  party2_witness
      allData?.WMSContractAgreementApplication[0]?.contractors.push(...item?.party2?.contractors);
      allData?.WMSContractAgreementApplication[0]?.party2_witness.push(...item?.party2?.party2_witness);
    }
    if (Object.keys(item)[0] == "agreement") {
      // const removeDuplicate = deleteDuplicateArrayOfObject(
      //   allData?.WMSContractAgreementApplication[0]?.agreement,
      //   item?.agreement
      // );

      // agreement value assign empty array
      allData?.WMSContractAgreementApplication?.forEach((res) => (console.log("res ", res?.agreement), (res.agreement = [])));
      // console.log('res allData ', allData)

      //push data in agreement array
      // allData?.WMSContractAgreementApplication[0]?.agreement.push(...removeDuplicate);
      allData?.WMSContractAgreementApplication[0]?.agreement.push(...item?.agreement);
    }
    if (Object.keys(item)[0] == "sdpgbg") {
      // sDPGBGDetails value assign empty array
      allData?.WMSContractAgreementApplication?.forEach((res) => (console.log("res ", res?.sDPGBGDetails), (res.sDPGBGDetails = [])));
      // console.log('res allData ', allData)

      //push data in agreement array
      // allData?.WMSContractAgreementApplication[0]?.sDPGBGDetails.push(...removeDuplicate);
      allData?.WMSContractAgreementApplication[0]?.sDPGBGDetails.push(...item?.sdpgbg);
    }
    if (Object.keys(item)[0] == "partyOne") {
      // party1Details value assign empty array
      allData?.WMSContractAgreementApplication?.forEach((res) => (console.log("res ", res?.party1Details), (res.party1Details = [])));
      // console.log('res allData ', allData)

      //push data in agreement array
      // allData?.WMSContractAgreementApplication[0]?.party1Details.push(...removeDuplicate);

      allData?.WMSContractAgreementApplication[0]?.party1Details.push(...item?.partyOne);
    }
    if (Object.keys(item)[0] == "termAndCondition") {
      // termsAndConditions value assign empty array
      allData?.WMSContractAgreementApplication?.forEach((res) => (console.log("res ", res?.termsAndConditions), (res.termsAndConditions = [])));
      // console.log('res allData ', allData)

      //push data in agreement array
      // allData?.WMSContractAgreementApplication[0]?.termsAndConditions.push(...removeDuplicate);

      allData?.WMSContractAgreementApplication[0]?.termsAndConditions.push(...item?.termAndCondition);
    }
    setAllData({ ...allData });
  }
  // ######################################################### End
  //All Stteper Error handle on final submit
  const [formError,setFormError]=useState({contractor:false,agreement:false,partyone:false});
 
  const onSubmit = async (item) => {
    console.log("Item item item ", item);

    console.log("CA add data allData third ", allData);
    
    if(item.hasOwnProperty("party2")){  
      // if("party2" in item){  
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
      allData.WMSContractAgreementApplication?.forEach((res) => {
      console.log("Clicked another comp index item Value ", res);
      // if((res.contractors).length>0 || (draftData && (draftData[0]?.contractors)?.length > 0)){
        if((res.contractors).length>0){
        formErrorContrator = false
      }else{
        returnVal =true
        formErrorContrator = true
        // return false
      }
      // if((res.agreement).length>0  || (draftData && (draftData[0]?.agreement)?.length > 0)){
        if((res.agreement).length>0){
        formErrorAgreement = false
      }else{
        returnVal =true
        formErrorAgreement = true
        // return false
      }
      // if((res.party1Details).length>0  || (draftData && (draftData[0]?.party1Details).length > 0)){
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
    // update
    if (addResponseData?.id || localStorage.getItem("resId")) {
      // const getPayload = JSON.parse(localStorage.getItem("formData"))
      // console.log("getPayload ",getPayload)
      let payloadDataUpdate;
      if (Object.keys(item)[0] === "termAndCondition") {
        payloadDataUpdate = { ...allData, status: "pending", id: addResponseData?.id || localStorage.getItem("resId") };
      } else {
        payloadDataUpdate = { ...allData, status: "draft", id: addResponseData?.id || localStorage.getItem("resId") };
      }

      //   let payloadDataUpdate = {
      //     "WMSContractAgreementApplication": [{
      //     "party2":{
      //       "contractors":[
      //         (getPayload?.WMSContractAgreementApplication[0]?.party2?.contractors) ?
      //       [...item?.party2?.contractors]
      //       :
      //       (item?.party2?.contractors) ?
      //       [...item?.party2?.contractors]
      //       :{
      //         "vendor_type": "",
      //         "vendor_name": "",
      //         "represented_by": "",
      //         "primary_party": "",
      //         "upload_doc": ""
      //       }
      //       ],
      //       "party2_witness": [(getPayload?.WMSContractAgreementApplication[0]?.party2?.party2_witness) ?
      //         [...item?.party2?.party2_witness] :
      //         (item?.party2?.party2_witness) ?
      //         [...item?.party2?.party2_witness]
      //         :
      //         {
      //           "witness_name": "",
      //           "address": "",
      //           "aadhaarNo": "",
      //           "upload_doc":""
      //         }
      //       ]
      //     },
      //     "agreement":[getPayload?.WMSContractAgreementApplication[0]?.agreement ?
      //       [...item?.agreement]
      //       :item?.agreement ?
      //       [...item?.agreement]
      //       :{
      //         agreement_no:"",agreement_date: "",department_name_ai: "",loa_no: "",resolution_no:"" ,resolution_date: "",tender_no:"",tender_date: "",agreement_type: "",defect_liability_period: "",contract_period: "",agreement_amount:"",payment_type: ""
      //       }
      //     ],
      //     "sDPGBGDetails":[getPayload?.WMSContractAgreementApplication[0]?.sdpgbg ?
      //     [...item?.sdpgbg]
      //      : item?.sdpgbg ?
      //      [...item?.sdpgbg] :{"account_no":"","bank_branch_ifsc_code":"","deposit_amount": "","deposit_type": "","particulars": "","payment_mode": "","valid_from_date": "","valid_till_date": ""}
      //     ],
      //     "party1Details":[
      //       item?.partyOne ?
      //       [...item?.partyOne]:{
      //         "department_name_party1": "","designation": "","employee_name": "","witness_name_p1": "","address_p1": "","uid_p1": ""
      //       }
      //     ],
      //     "termsAndConditions":[
      //       item?.termAndCondition ?
      //       [...item?.termAndCondition]:{
      //         "terms_and_conditions": ""
      //       }
      //     ]
      //   }],
      //   "id": addResponseData?.id
      // };
      console.log("CA update data payload", payloadDataUpdate);
      // await mutateUpdate(payloadDataUpdate)
      // await localStorage.setItem("formData",JSON.stringify(payloadDataUpdate))
      await updateData?.mutate(payloadDataUpdate);
    } else {
      //add
      let payloadData = { ...allData, status: "draft" };
      // let payloadData = {"WMSContractAgreementApplication": [{
      //   "party2":{
      //     "contractors":[(item?.party2?.contractors)?[...item?.party2?.contractors]:{
      //       "vendor_type": "",
      //       "vendor_name": "",
      //       "represented_by": "",
      //       "primary_party": "",
      //       "upload_doc":""
      //     }
      //     ]
      //     ,
      //     "party2_witness": [(item?.party2?.party2_witness)?[...item?.party2?.party2_witness]:
      //       {
      //         "witness_name": "",
      //         "address": "",
      //         "aadhaarNo": "",
      //         "upload_doc": ""
      //       }
      //     ]
      //   },
      //   "agreement":[item?.agreement?
      //     [...item?.agreement]:{
      //       agreement_no:"",agreement_date: "",department_name_ai: "",loa_no: "",resolution_no:"" ,resolution_date: "",tender_no:"",tender_date: "",agreement_type: "",defect_liability_period: "",contract_period: "",agreement_amount:"",payment_type: ""
      //     }
      //   ],
      //   "sDPGBGDetails":[item?.sdpgbg ?
      //     [...item?.sdpgbg]:{"account_no":"","bank_branch_ifsc_code":"","deposit_amount": "","deposit_type": "","particulars": "","payment_mode": "","valid_from_date": "","valid_till_date": ""}
      //   ],
      //   "party1Details":[
      //     item?.partyOne ?
      //     [...item?.partyOne]:{
      //       "department_name_party1": "","designation": "","employee_name": "","witness_name_p1": "","address_p1": "","uid_p1": ""
      //     }
      //   ],
      //   "termsAndConditions":[
      //     item?.termAndCondition ?
      //     [...item?.termAndCondition]:{
      //       "terms_and_conditions": ""
      //     }
      //   ]
      // }]};
      console.log("CA add data payload", payloadData);
      // localStorage.setItem("formData",JSON.stringify(payloadData))
      await mutate(payloadData);
    }
  };
  const addNewContract = () => {
    history.replace("/upyog-ui/citizen/wms/contract-agreement/add");
    localStorage.clear("resId");
    // alert("link")
  };
  const configs = newConfig ? newConfig : newConfig;
  console.log("draftData index 1 ", draftData[0]);
  console.log("Clicked another comp index fromError 2 ", formError);
  if (!draftData?.length === 0 || Boolean(localStorage.getItem("resId"))) {
    if (draftData?.length === 0) {
      return <Loader />;
    }
    return (
      <>
        {formSubmitMessag ? (
          <Card>
            <Banner
              successful={updateData?.isSuccess}
              message={updateData?.isSuccess ? "Submitted Successfully!" : "Something Went Wrong!"}
              info={updateData?.isSuccess ? "Success info message will come" : "Error info message will come"}
            />
            <button className="submit-bar" type="button" style={{ color: "white" }} onClick={addNewContract}>
              Add New Contract {/* {`${t("PT_SEARCH")}`} */}
            </button>
          </Card>
        ) : (
          <>
            {/* {window.location.href.includes("/citizen") ? <Timeline currentStep={window.location.href.includes("/ws/") ? 1 : 2} flow={window.location.href.includes("/ws/") ? "WS":""} businessService={"WS"} /> : null} */}
            {true ? <Timeline currentStep={nextStep + 1} businessService={"WMS"} /> : null}
            <Card>
              <Header>{t("WMS_CA_ADD_CONTRACT_AGREEMENT")}</Header>
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
                    setShowHideToast(false);
                  }}
                />
              )}
            </Card>
          </>
        )}
      </>
    );
  } else {
    return (
      <>
        {/* {window.location.href.includes("/citizen") ? <Timeline currentStep={window.location.href.includes("/ws/") ? 1 : 2} flow={window.location.href.includes("/ws/") ? "WS":""} businessService={"WS"} /> : null} */}
        {true ? <Timeline currentStep={nextStep + 1} businessService={"WMS"} /> : null}
        <Card>
          <Header>{t("WMS_CA_ADD_CONTRACT_AGREEMENT")}</Header>
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
                setShowHideToast(false);
              }}
            />
          )}
        </Card>
        <style>
        {`
      
      iframe{background-color:blue;display:none}
      `}
      </style>
      </>
    );
  }
};
export default ContractAgreementAdd;
