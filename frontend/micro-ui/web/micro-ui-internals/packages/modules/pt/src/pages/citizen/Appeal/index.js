import React, { useEffect, useState } from "react";
import { Redirect, Route, Switch, useHistory, useLocation, useRouteMatch, useParams } from "react-router-dom";
import { newConfigAmalgamate } from "../../../config/Amalgamate/config";

import { useTranslation } from "react-i18next";
// import { Card, Dropdown, FormComposer, Loader, Modal, ResponseComposer, Toast } from "@upyog/digit-ui-react-components";
import Timeline from "../../../components/TLTimeline";
import {
    CardLabel,
    CardLabelError,
    CitizenInfoLabel,
    Dropdown,
    FormStep,
    LabelFieldPair,
    Loader,
    Modal,
    RadioButtons,
    UploadFile,
  } from "@upyog/digit-ui-react-components";
import AppealDetails from "./AppealDetails";
import AppealAcknowledgement from "./AppealAcknowledgement";

  const InputError = ({ message }) => {
    return (
      <p class="flex items-center gap-1 px-2 font-semibold text-red-500 bg-red-100 rounded-md" style="opacity: 1; transform: none;">
        <svg stroke="currentColor" fill="currentColor" stroke-width="0" viewBox="0 0 24 24" height="1em" width="1em" xmlns="http://www.w3.org/2000/svg"><path fill="none" d="M0 0h24v24H0z"></path><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"></path></svg>
        {message}
      </p>
    )
  }

const CitizenAppeal = (props) => {
  const { t } = useTranslation();
  const match = useRouteMatch();
  const { pathname } = useLocation();
  const [params, setParams, clearParams] = Digit.Hooks.useSessionStorage("PT_MUTATE_PROPERTY", {});
  const history = useHistory();
  const [submit, setSubmit] = useState(false);
  // const [formData, setFormData] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [city, setCity] = useState();
  const { propertyIds } = useParams();
  const [showToast, setShowToast] = useState(null);
  const [showErrorToast, setShowErrorToast] = useState(null);
  const [currentStep, setCurrentStep] = useState(1);
  
  const [ownerName, setOwnerName] = useState(null);
  const [propertyAddress, setPropertyAddress] = useState(null);
  const [propertyId, setPropertyId] = useState(propertyIds);
  const [formData, setFormData] = useState({
    assessmentYear: null,
    nameOfAssessingOfficer: null,
    assessingOfficerDesignation: '',
    ruleUnderOrderPassed: '',
    dateOfOrder: '',
    dateOfService: '',
    dateOfPayment: '',
    applicantAddress: '',
    reliefClaimedInAppeal: '',
    statementOfFacts: '',
    groundOfAppeal: ''
  });
  const [fieldError, setFieldError] = useState({});
  // const [assessmentYear, setAssessmentYear] = useState(null);
  const [nameOfAssessingOfficer, setNameOfAssessingOfficer] = useState(null);
  const [assessingOfficerDesignation, setAssessingOfficerDesignation] = useState(null);
  const [ruleUnderOrderPassed, setRuleUnderOrderPassed] = useState(null);
  const [dateOfOrder, setDateOfOrder] = useState(null);
  const [dateOfService, setDateOfService] = useState(null);
  const [dateOfPayment, setDateOfPayment] = useState(null);
  const [applicantAddress, setApplicantAddress] = useState(null);
  const [reliefClaimedInAppeal, setReliefClaimedInAppeal] = useState(null);
  const [statementOfFacts, setStatementOfFacts] = useState(null);
  const [groundOfAppeal, setGroundOfAppeal] = useState(null);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [file, setFile] = useState();
  const [error, setError] = useState(null);
  const [tableList, setTableList] = useState([]);
  const [documentType, setDocumentType] = useState('');
  const [document, setDocument] = useState();
  const [uploadedDocument, setUploadedDocument] = useState(null);
  const [errorDoc, setErrorDoc] = useState(null);


  const tenantId = Digit.ULBService.getCurrentTenantId();
  const allCities = Digit.Hooks.pt.useTenants()?.sort((a, b) => a?.i18nKey?.localeCompare?.(b?.i18nKey));
  let filters = {};
  const auth =true;
  filters.propertyIds=propertyIds;
  const searchArgs = city ? {  filters, auth } : { filters, auth };
  const result = Digit.Hooks.pt.usePropertySearch(searchArgs);
  const template = [{"label":"PT_TOTAL_DUES","key":"total_due","noteStyle":{"fontSize":"24px","fontWeight":"bold"},"notePrefix":"₹ "},{"label":"PT_PROPERTY_PTUID","key":"property_id"},{"label":"PT_OWNERSHIP_INFO_NAME","key":"owner_name"},{"label":"PT_OWNERSHIP_INFO_GENDER","key":"owner_gender"},{"label":"PT_OWNERSHIP_INFO_MOBILE_NO","key":"owner_mobile"},{"label":"PT_OWNERSHIP_INFO_EMAIL_ID","key":"owner_email"},{"label":"PT_PROPERTY_ADDRESS_SUB_HEADER","key":"property_address"},{"label":"PT_DUE_DATE","key":"bil_due__date"}];
  const actionButtonLabel = 'Select'
  const consumerCode = propertyIds && result?.data?.Properties?.map((a) => a.propertyId).join(",");
  const propertyOwnerName = propertyIds && result?.data?.Properties[0]?.owners?.map((a) => a.name).join(",");
  
  
  const ptAddress = propertyIds && result?.data?.Properties[0]?.address;
  let obj = {
    doorNo: ptAddress?.doorNo,
    street: ptAddress?.street,
    dagNo: ptAddress?.dagNo,
    pattaNo: ptAddress?.pattaNo,
    village: ptAddress?.village,
    commonNameOfBuilding: ptAddress?.commonNameOfBuilding,
    principalRoadName: ptAddress?.principalRoadName,
    subSideRoadName: ptAddress?.subSideRoadName,
    typeOfRoad: ptAddress?.typeOfRoad?.i18nkey,
    landmark: ptAddress?.landmark,
    locality: ptAddress?.locality?.i18nkey,
    city: ptAddress?.city?.name,
    pincode: ptAddress?.pincode,
  };
  let addressDetails="";
  for (const key in obj) {
    if (key == "pincode") addressDetails += obj[key] ? obj[key] : "";
    else addressDetails += obj[key] ? t(obj[key]) + ", " : "";
  }
  
  useEffect(()=>{
    setOwnerName(propertyOwnerName)
    setPropertyAddress(addressDetails)
  },[result])


  let fetchBillParams = { consumerCode };


  const paymentDetails = Digit.Hooks.useFetchBillsForBuissnessService(
    { businessService: "PT", ...fetchBillParams, tenantId: city },
    {
      enabled: consumerCode ? true : false,
      retry: false,
    }
  );

  const payment = {};

  paymentDetails?.data?.Bill?.forEach((element) => {
    if (element?.consumerCode) {
      payment[element?.consumerCode] = {
        total_due: element?.totalAmount,
        bil_due__date: new Date(element?.billDetails?.[0]?.expiryDate).toDateString(),
      };
    }
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    console.log("---",name, value)
    setFormData({
        ...formData,
        [name]: value,
    });
    // validateForm(formData)
    const newErrors = validateForm({
      ...formData,
      [name]: value,
  });
    setFieldError(newErrors);
  };
  const validateForm = (data) => {
    console.log("data==",data)
    const errors = {};

    var exists = Object.keys(data).forEach(function(k) {
        if((k!= "assessingOfficerDesignation" && k!= "nameOfAssessingOfficer") && !data[k]) {
          return errors[k] = 'This field is required';
        }
    });
    
    // if (!data.assessmentYear) {
    //     errors.assessmentYear = 'This field is required';
    // }

    // if (!data.email.trim()) {
    //     errors.email = 'Email is required';
    // } else if (!/\S+@\S+\.\S+/.test(data.email)) {
    //     errors.email = 'Email is invalid';
    // }

    // if (!data.password) {
    //     errors.password = 'Password is required';
    // } else if (data.password.length < 8) {
    //     errors.password = `Password must be at 
    //     least 8 characters long`;
    // }

    // if (data.confirmPassword !== data.password) {
    //     errors.confirmPassword = 'Passwords do not match';
    // }

    return errors;
  };

  const onShowModal=(val)=>{
    setShowModal(true)
  }
  const closeModal=()=>{
    setShowModal(false)
  }
  const Heading = (props) => {
    return <h1 className="heading-m">{props.label}</h1>;
  };
  const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="red">
      <path d="M0 0h24v24H0V0z" fill="none" />
      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
    </svg>
  );

  const CloseBtn = (props) => {
    return (
      <div className="icon-bg-secondary" onClick={props.onClick}>
        <Close />
      </div>
    );
  };
//   const onSetPropertyId=(e)=>{
//     setPropertyId(e.target.value);
//     closeToast();
//   }
  let [propertyDetails, setPropertyDetails]=useState([]);
  const [showAcknowledgement, setShowAcknowledgement] =  useState(false);
  const [appealResponse, setAppealResponse] = useState(null);
  const [isSuccess, setIsSuccess] = useState(false);

  const onSubmit = async () => {
    setIsSuccess(false);
    try {
      // TODO: change module in file storage
      const response = await Digit.PTService.appealCreate({Appeal:payloadData});
      if (response?.Appeals?.length > 0) {
        setShowAcknowledgement(true);
        setAppealResponse(response);
        setIsSuccess(true);
      } else {
          setErrorDoc(t("PT_FILE_UPLOAD_ERROR"));
          setShowAcknowledgement(true)
      }
    } catch (err) {
      setShowAcknowledgement(true)
    }
  };
  const closeToast = () => {
    setShowToast(null);
  };
  const closeErrorToast = () => {
    setShowErrorToast(null);
  };
  setTimeout(() => {
    closeErrorToast();
  }, 10000);
  const removeProperty = (result, index)=> {
    propertyDetails.splice(index, 1);
    // setPropertyDetails(pDetails);
  }
  const [payloadData, setPayloadData] = useState({});
  
  let payload = {
    ...formData,
    creationReason:"APPEAL",
    workflow:null,
    tenantId: result?.data?.Properties[0]?.tenantId,
    documents: []
  };
  const goNext =()=>{
    const newErrors = validateForm(formData);
    setFieldError(newErrors);
    if (Object.keys(newErrors).length === 0) {
      payload['ownerName'] = ownerName;
      payload['propertyAddress'] = propertyAddress;
      payload['propertyId'] = propertyId;
      payload['documents'] = JSON.parse(JSON.stringify(tableList));
    if(uploadedFile) {
      payload['documents'].push({documentType: "Copy of Challan", fileStoreId: uploadedFile})
    } else {
      setError('This field is required');
      return;
    }
    setPayloadData(payload)
    setCurrentStep(2);
  } else {
      return;
  }
    
  }
  const previous =()=>{
    setCurrentStep(1)
    setAgree(false)
  }
  

  function selectfile(e) {
    setFile(e.target.files[0]);
  }
  function selectDocument(e) {
    setDocument(e.target.files[0]);
  }

  useEffect(() => {
    (async () => {
      setError(null);
      if (file) {
        if (file.size >= 2000000) {
          setError(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            // TODO: change module in file storage
            const response = await Digit.UploadServices.Filestorage("property-appeal", file, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedFile(response?.data?.files[0]?.fileStoreId);
            } else {
              setError(t("PT_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setError(err?.response?.data?.Errors[0].message);
          }
        }
      }
    })();
  }, [file]);

  useEffect(() => {
    (async () => {
      setErrorDoc(null);
      if (document) {
        if (document.size >= 2000000) {
          setErrorDoc(t("PT_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        } else {
          try {
            // TODO: change module in file storage
            const response = await Digit.UploadServices.Filestorage("property-appeal", document, Digit.ULBService.getStateId());
            if (response?.data?.files?.length > 0) {
              setUploadedDocument(response?.data?.files[0]?.fileStoreId);
            } else {
                setErrorDoc(t("PT_FILE_UPLOAD_ERROR"));
            }
          } catch (err) {
            setErrorDoc(err?.response?.data?.Errors[0].message);
          }
        }
      }
    })();
  }, [document]);

  const [documentTypeError, setDocTypeError] = useState('');
  const onSave = ()=>{
    if(!uploadedDocument || !documentType) {
      setErrorDoc('This field is required');
      setDocTypeError('This field is required');
      return;
    }
    let obj = {
      documentType: documentType?.name,
      documentName: document?.name,
      fileStoreId: uploadedDocument
    };
    let list = tableList || [];
    list.push(obj)
    setTableList(list);
    setDocumentType('');
    setDocument('');
    setUploadedDocument('');
    setErrorDoc('');
    setDocTypeError('')
    setShowModal(false);
  };
  
 const selectForm = ()=>{

 }

 const [agree, setAgree] = useState(false);
  const setdeclarationhandler = (e) => {
    setAgree(e);
    
  };
  
  return (
    <React.Fragment>
        {!showAcknowledgement && (<div>
          {currentStep==1 && (<div>
        {window.location.href.includes("/citizen") ? <Timeline currentStep={currentStep} flow="PT_APPEAL" /> : null}
        <FormStep t={t} onSelect={selectForm}>
        <div className="row appeal-row-cls">
            <div className="col-sm-4">
                <label for="formControlInputNameOfOwner" class="form-label">Name of Owner*</label>
                <input type="text" class="form-control" id="formControlInputNameOfOwner" placeholder="Enter Name of Owner" value={ownerName} readOnly/>
            </div>
            <div className="col-sm-8">
                <label for="formControlInputPtAddress" class="form-label">Property Address*</label>
                <input type="text" class="form-control" id="formControlInputPtAddress" placeholder="Enter Property Address" value={propertyAddress} readOnly/>
            </div>
        </div>
        <div className="row appeal-row-cls">
            <div className="col-sm-4">
                <label for="formControlInputPtId" class="form-label">Property ID*</label>
                <input type="text" class="form-control" id="formControlInputPtId" placeholder="Enter Property ID" value={propertyId} readOnly />
            </div>
            <div className="col-sm-4">
                <label for="formControlInputPtAssessmentYear" class="form-label">Assessment Year*</label>
                <input type="text" className={fieldError.assessmentYear ? "form-control error-message" : "form-control"} id="formControlInputPtAssessmentYear" name="assessmentYear" placeholder="Enter Assessment Year(Ex: 0000-00)" value={formData.assessmentYear} onChange={handleChange} required />
                {fieldError.assessmentYear &&
                        <span className="error-message">
                            {fieldError.assessmentYear}
                        </span>
                    }
            </div>
        </div>
        
        <div className="row appeal-row-cls">
            <div className="col-sm-4">
                <label for="formControlInputPtAssessingOfcrName" class="form-label">Name of Assessing Officer</label>
                {/* <input type="text" class="form-control" id="formControlInputPtAssessingOfcrName" placeholder="Enter Name of Assessing Officer" value={nameOfAssessingOfficer} onChange={(e)=>setNameOfAssessingOfficer(e?.target?.value)} required /> */}
                <input type="text" className={fieldError.nameOfAssessingOfficer ? "form-control error-message" : "form-control"} id="formControlInputPtAssessingOfcrName" placeholder="Enter Name of Assessing Officer" name="nameOfAssessingOfficer" value={formData.nameOfAssessingOfficer} onChange={handleChange} />
                {fieldError.nameOfAssessingOfficer &&
                        <span className="error-message">
                            {fieldError.nameOfAssessingOfficer}
                        </span>
                    }
            </div>
            <div className="col-sm-4">
                <label for="formControlInputPtAssissingDesg" class="form-label">Designation</label>
                {/* <input type="text" class="form-control" id="formControlInputPtAssissingDesg" placeholder="Enter Designation" value={assessingOfficerDesignation} onChange={(e)=>setAssessingOfficerDesignation(e?.target?.value)} required/> */}
                <input type="text" className={fieldError.assessingOfficerDesignation ? "form-control error-message" : "form-control"} id="formControlInputPtAssissingDesg" placeholder="Enter Designation" name="assessingOfficerDesignation" value={formData.assessingOfficerDesignation} onChange={handleChange} />
                {fieldError.assessingOfficerDesignation &&
                        <span className="error-message">
                            {fieldError.assessingOfficerDesignation}
                        </span>
                    }
            </div>
        </div>
        <hr style={{marginBottom: "10px"}} />
        <div>
            <div className="row appeal-row-cls">
                <div className="col-sm-4">
                    <label for="formControlInputRuleOrderPassed" class="form-label">Rule under which order passed*</label>
                    {/* <input type="text" class="form-control" id="formControlInputRuleOrderPassed" placeholder="Enter Rule under which order passed" value={ruleUnderOrderPassed} onChange={(e)=>setRuleUnderOrderPassed(e?.target?.value)} required /> */}
                    <input type="text" className={fieldError.ruleUnderOrderPassed ? "form-control error-message" : "form-control"} id="formControlInputRuleOrderPassed" placeholder="Enter Rule under which order passed" name="ruleUnderOrderPassed" value={formData.ruleUnderOrderPassed} onChange={handleChange} required />
                    {fieldError.ruleUnderOrderPassed &&
                            <span className="error-message">
                                {fieldError.ruleUnderOrderPassed}
                            </span>
                        }
                </div>
                <div className="col-sm-4">
                    <label for="formControlInputDateOfOrder" class="form-label">Date of Order*</label>
                    {/* <input type="date" class="form-control" id="formControlInputDateOfOrder" placeholder="Enter Date of Order" value={dateOfOrder} onChange={(e)=>setDateOfOrder(e?.target?.value)} required /> */}
                    <input type="date" className={fieldError.dateOfOrder ? "form-control error-message" : "form-control"} id="formControlInputDateOfOrder" placeholder="Enter Date of Order" name="dateOfOrder" value={formData.dateOfOrder} onChange={handleChange} required />
                    {fieldError.dateOfOrder &&
                            <span className="error-message">
                                {fieldError.dateOfOrder}
                            </span>
                        }
                </div>
                <div className="col-sm-4">
                    <label for="formControlInputDateOfService" class="form-label">Date of Service*</label>
                    {/* <input type="date" class="form-control" id="formControlInputDateOfService" placeholder="Enter Date of Service" value={dateOfService} onChange={(e)=>setDateOfService(e?.target?.value)} required /> */}
                    <input type="date" className={fieldError.dateOfService ? "form-control error-message" : "form-control"} id="formControlInputDateOfService" placeholder="Enter Date of Service" name="dateOfService" value={formData.dateOfService} onChange={handleChange} required />
                    {fieldError.dateOfService &&
                            <span className="error-message">
                                {fieldError.dateOfService}
                            </span>
                        }
                </div>
            </div>
        </div>
        <div>
            <div style={{fontWeight: "600", marginBottom: "10px"}}>Admitted tax liability under The Manipur Municipality Rules, 2019</div>
            <div>
                <div className="row appeal-row-cls">
                    <div className="col-sm-4">
                        <label for="formControlInputDateOfPayment" class="form-label">Date of Payment*</label>
                        {/* <input type="date" class="form-control" id="formControlInputDateOfPayment" placeholder="Enter Date of Payment" value={dateOfPayment} onChange={(e)=>setDateOfPayment(e?.target?.value)} required /> */}
                        <input type="date" className={fieldError.dateOfPayment ? "form-control error-message" : "form-control"} id="formControlInputDateOfPayment" placeholder="Enter Date of Payment" name="dateOfPayment" value={formData.dateOfPayment} onChange={handleChange} required />
                        {fieldError.dateOfPayment &&
                          <span className="error-message">
                              {fieldError.dateOfPayment}
                          </span>
                        }
                    </div>
                    <div className="col-sm-8" >
                    <label for="formControlInputCopyOfChallan" class="form-label">Copy of Challan*</label>
                    <UploadFile
                        id={"formControlInputCopyOfChallan"}
                        extraStyleName={"citizenAppeal"}
                        accept=".jpg,.png,.pdf"
                        onUpload={selectfile}
                        onDelete={() => {
                            setUploadedFile(null);
                        }}
                        isMandatory={true}
                        message={uploadedFile ? `1 ${t(`PT_ACTION_FILEUPLOADED`)}` : t(`PT_ACTION_NO_FILEUPLOADED`)}
                        error={error}
                        />
                        {error ? <div style={{ width: "100%", fontSize: "15px", color: "red", marginTop: "5px" }}>{error}</div> : ""}
                        <div style={{ disabled: "true", height: "20px", width: "100%" }}></div>
                    </div>
                    
                </div>
            </div>
        </div>
        <div>
            <div style={{fontWeight: "600", marginBottom: "10px", fontSize: "12px"}}>Note: The appeal shall be treated as in case evidance of payment of admitted tax is not enclosed,- Refer Rules 19(4) </div>
            <div>
                <div className="row appeal-row-cls">
                    <div className="col-sm-4">
                        <label for="formControlInputApplicantAddress" class="form-label">Applicant Address*</label>
                        {/* <input type="text" class="form-control" id="formControlInputApplicantAddress" placeholder="Enter Applicant Address" value={applicantAddress} onChange={(e)=>setApplicantAddress(e?.target?.value)} required /> */}
                        <input type="text" className={fieldError.applicantAddress ? "form-control error-message" : "form-control"} id="formControlInputApplicantAddress" placeholder="Enter Applicant Address" name="applicantAddress" value={formData.applicantAddress} onChange={handleChange} required />
                        {fieldError.applicantAddress &&
                          <span className="error-message">
                              {fieldError.applicantAddress}
                          </span>
                        }
                    </div>
                    <div className="col-sm-8" >
                        <label for="formControlInputReliefClaimed" class="form-label">Relief Claimed in Appeal*</label>
                        {/* <input type="text" class="form-control" id="formControlInputReliefClaimed" placeholder="Enter Relief Claimed in Appeal" value={reliefClaimedInAppeal} onChange={(e)=>setReliefClaimedInAppeal(e?.target?.value)} required /> */}
                        <input type="text" className={fieldError.reliefClaimedInAppeal ? "form-control error-message" : "form-control"} id="formControlInputReliefClaimed" placeholder="Enter Relief Claimed in Appeal" name="reliefClaimedInAppeal" value={formData.reliefClaimedInAppeal} onChange={handleChange} required />
                        {fieldError.reliefClaimedInAppeal &&
                          <span className="error-message">
                              {fieldError.reliefClaimedInAppeal}
                          </span>
                        }
                    </div>
                    
                </div>
            </div>
        </div>
        <hr style={{marginBottom: "10px"}} />
        <div className="self-box">
            <div style={{fontWeight: "600", marginBottom: "10px"}}> Statement of Facts</div>
            <div>
                <div className="row appeal-row-cls">
                    <div className="col-sm-12">
                        <label for="formControlInputStatementOfFacts" class="form-label">Comments*</label>
                        <textarea className={fieldError.statementOfFacts ? "form-control error-message" : "form-control"} id="formControlInputStatementOfFacts" rows="3" name="statementOfFacts" value={formData.statementOfFacts} onChange={handleChange} required></textarea>
                        {fieldError.statementOfFacts &&
                          <span className="error-message">
                              {fieldError.statementOfFacts}
                          </span>
                        }
                    </div>
                    
                </div>
            </div>
        </div>
        <hr style={{marginBottom: "10px"}} />
        <div className="self-box">
            <div style={{fontWeight: "600", marginBottom: "10px"}}> Ground of Appeal</div>
            <div>
                <div className="row appeal-row-cls">
                    <div className="col-sm-12">
                        <label for="formControlInputGroundOfAppeal" class="form-label">Comments*</label>
                        {/* <textarea class="form-control" id="formControlInputGroundOfAppeal" rows="3" value={groundOfAppeal} onChange={(e)=>setGroundOfAppeal(e?.target?.value)} required></textarea> */}
                        <textarea className={fieldError.groundOfAppeal ? "form-control error-message" : "form-control"} id="formControlInputGroundOfAppeal" rows="3" name="groundOfAppeal" value={formData.groundOfAppeal} onChange={handleChange} required></textarea>
                        {fieldError.groundOfAppeal &&
                          <span className="error-message">
                              {fieldError.groundOfAppeal}
                          </span>
                        }
                    </div>
                    
                </div>
            </div>
        </div>
        <hr style={{marginBottom: "10px"}} />
        <div className="self-box">
            <div style={{fontWeight: "600", marginBottom: "10px"}}> List of Documents
                    <span style={{float: "right"}}>
                        <button className="submit-bar" onClick={(e)=>setShowModal(true)}><header>Add</header></button>
                    </span>
            </div>
            <div>
                <div className="row appeal-row-cls">
                    <div className="col-sm-12">
                    {tableList && tableList.length>0 &&
                        <div style={{ width: '100%' }}>
                          <table style={{ width: '100%', border: '1px solid #b7b7b7'}}>
                            <tr style={{background: '#eaeaea', lineHeight: '35px'}}>
                              <th style={{paddingLeft: "10px"}}>Sr. No.</th>
                              <th>Document Type</th>
                              <th>Document Name</th>
                            </tr>
                            {tableList.map((e, i)=>{
                              return (<tr>
                                <td style={{paddingLeft: "10px"}}>{i+1}</td>
                                <td>{e?.documentType}</td>
                                <td>{e?.documentName}</td>
                              </tr>)
                            })}
                          </table>
                        </div>}
                    </div>
                    
                </div>
            </div>
        </div>
        </FormStep>
        </div>)}
          
        {currentStep==2 && (<div >
        {window.location.href.includes("/citizen") ? <Timeline currentStep={currentStep} flow="PT_APPEAL" /> : null}
          <AppealDetails formData={payloadData} setdeclarationhandler={setdeclarationhandler} checked={agree}></AppealDetails>
        </div>)}
        <div>
            <div className="appeal-footer">
                {currentStep==1 && (<span style={{float: "right"}}>
                    <button style={{marginTop: "4px"}} className="submit-bar" onClick={goNext}><header>Next</header></button>
                </span>)}
                {currentStep==2 && (<span style={{float: "left"}}>
                    <button style={{marginTop: "4px"}} className="submit-bar" onClick={previous}><header>Previous</header></button>
                </span>)}
                {currentStep==2 && (<span style={{float: "right"}}>
                    <button style={{marginTop: "4px"}} className={`${!agree ? "submit-bar-disabled" : "submit-bar"}`} disabled={!agree ? true:false} onClick={onSubmit}><header>Submit</header></button>
                </span>)}
            </div>
        </div>
        </div>)}
        {showAcknowledgement && (<div>
          <AppealAcknowledgement data={appealResponse} isSuccess={isSuccess}></AppealAcknowledgement>
        </div>)}
        
      
      {/* {<CitizenInfoLabel info={t("CS_FILE_APPLICATION_INFO_LABEL")} text={t("PT_PROPERTY_TYPE_INFO_MSG")} />} */}
      {showModal ? (
            <Modal
            headerBarMain={<Heading label={t('Add Documents')} />}
            headerBarEnd={<CloseBtn onClick={closeModal} />}
            actionCancelOnSubmit={closeModal}
            hideSubmit={true}
            actionSaveOnSubmit={() => {}}
            formId="modal-action"
            isDisabled={false}
            width={'60%'}
          >
            <div>
            <div className="row" style={{ padding: '10px'}}>
                
                <div className="col-sm-12">
                    <CardLabel>{`${t("Document Type")}`}</CardLabel>
                  <Dropdown isMandatory optionCardStyles={{ zIndex: 111111 }} selected={documentType} optionKey="name" option={[{name: 'Owner Identity Doc', value: 'Owner Identity Doc'},{name: 'Property Related Document', value: 'Property Related Document'},{name: 'Supporting Document', value: 'Supporting Document'},{name: 'Notice Copy', value: 'Notice Copy'}]} select={setDocumentType} t={t} />
                  {documentTypeError ? <div style={{ height: "20px", width: "100%", fontSize: "15px", color: "red", marginTop: "5px" }}>{documentTypeError}</div> : ""}
                </div>
                <div className="col-sm-12">
                  <CardLabel>{`${t("")}`}</CardLabel>
                  <input class="form-control" type="file" id="formFile" onChange={(e) => selectDocument(e)} accept=".jpg,.png,.pdf"></input>
                  {errorDoc ? <div style={{ height: "20px", width: "100%", fontSize: "15px", color: "red", marginTop: "5px" }}>{errorDoc}</div> : ""}
                  {/* <UploadFile
                    id={"formControlInputDocument"}
                    extraStyleName={"createProperty"}
                    accept=".jpg,.png,.pdf"
                    onUpload={selectDocument}
                    onDelete={() => {
                        setUploadedDocument(null);
                    }}
                    message={uploadedDocument ? `1 ${t(`PT_ACTION_FILEUPLOADED`)}` : t(`PT_ACTION_NO_FILEUPLOADED`)}
                    error={error}
                    />
                    {error ? <div style={{ height: "20px", width: "100%", fontSize: "20px", color: "red", marginTop: "5px" }}>{error}</div> : ""}
                    <div style={{ disabled: "true", height: "20px", width: "100%" }}></div> */}
  
                </div>
              </div>
              
              <div className="footer" style={{height: '30px', background: '#ebebeb'}}>
                <div style={{padding: '10px'}}>
                  <div style={{ width: '40%', display: 'inline' }}>
                    <button onClick={() => onSave()} className="submit-bar"
                      style={{
                        color: 'white',
                        float: 'right',
                        width: '20%',
                        marginLeft: '10px'
                      }}
                    >
                      {t("Save")}
                    </button>
                  </div>
                  
                </div>
                
              </div>
            </div>
          </Modal>
          ) : null}
    
    </React.Fragment>
  );
};

export default CitizenAppeal;
