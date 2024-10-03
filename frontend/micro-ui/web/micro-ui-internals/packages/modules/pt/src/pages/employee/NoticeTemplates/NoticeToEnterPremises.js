import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, DatePicker, Dropdown, Header, Modal, TextInput } from "@upyog/digit-ui-react-components";


const NoticeToEnterPremises = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { t } = useTranslation();
  const [financialYears, setFinancialYears] = useState([]);
  const [selectedFinancialYear, setSelectedFinancialYear] = useState(props?.noticeData && props?.noticeData.assessmentYear? {code: props?.noticeData.assessmentYear, name: props?.noticeData.assessmentYear} : null);
  const [dateOfAnnualRet, setDateOfAnnualRet] = useState(props?.noticeData?.dateOfAnnualRet ? props?.noticeData?.dateOfAnnualRet : props?.noticeData?.dateOfOrder ? props?.noticeData?.dateOfOrder : null);


  const [notice, setNotice] = useState();
  const [showModal, setShowModal] = useState(false)
  const [showDateModal, setShowDateModal] = useState(false);
  const [showMobileModal, setShowMobileModal] = useState(false);
  const [showPenaltyModal, setShowPenaltyModal] = useState(false);
  const handleChangeNotice = (value) => {

  }
  const [name, setName] = useState(props?.noticeData?.ownerName ? props?.noticeData?.ownerName : props?.noticeData?.name ? props?.noticeData?.name : null);
  const onChangeName = (e) => {
    setName(e.target?.value)
  }
  const [address, setAddress] = useState(props?.noticeData?.address ? props?.noticeData?.address : null);
  const onChangePtAddress = (e) => {
    setAddress(e.target?.value)
  }
  const [propertyId, setPropertyId] = useState(props?.noticeData?.propertyId ? props?.noticeData?.propertyId : null);
  const onChangePtId = (e) => {
    setPropertyId(e.target?.value)
  }
  const [acknowledgementNumber, setAcknowledgementNumber] = useState(props?.noticeData?.acknowledgementNumber ? props?.noticeData?.acknowledgementNumber : null);
  const onChangeAcknowledgementNo=(e)=>{
    setAcknowledgementNumber(e.target.value)
  }
  const [returnFormData, setReturnFormData] = useState({
    authorisedpersonName: props?.noticeData?.authorisedpersonName ? props?.noticeData?.authorisedpersonName : null,
    designation: props?.noticeData?.designation ? props?.noticeData?.designation : null
  });
  const [returnTimeFormData, setReturnTimeFormData] = useState({
    entryTime: props?.noticeData?.entryTime ? props?.noticeData?.entryTime : null,
    entryDate: props?.noticeData?.entryDate ? props?.noticeData?.entryDate : null
  });
  const [returnMobileFormData, setReturnMobileFormData] = useState({
    mobilenumber: props?.noticeData?.mobilenumber ? props?.noticeData?.mobilenumber : null
  });
  const [returnPenaltyFormData, setReturnPenaltyFormData] = useState({
    penaltyAmount: props?.noticeData?.penaltyAmount ? props?.noticeData?.penaltyAmount : null
  });
  
  const { isLoading: financialYearsLoading, data: financialYearsData } = Digit.Hooks.pt.useMDMS(
    Digit.ULBService.getStateId(),
    '',
    "FINANCIAL_YEARLS",
    {},
    {
      details: {
        tenantId: Digit.ULBService.getStateId(),
        moduleDetails: [{ moduleName: "egf-master", masterDetails: [{ name: "FinancialYear", filter: "[?(@.module == 'PT')]" }] }],
      },
    }
  );
  useEffect(() => {
    if (financialYearsData && financialYearsData["egf-master"]) {
      setFinancialYears(financialYearsData["egf-master"]?.["FinancialYear"]);
    }
  }, [financialYearsData]);
  const onEditNameAndDesg = (e) => {
    e.preventDefault();
    setShowModal(true)
  }
  const onEditDate = (e) => {
    e.preventDefault();
    setShowDateModal(true)
  }
  const onEditMobile = (e) => {
    e.preventDefault();
    setShowMobileModal(true)
  }
  const onEditPenalty = (e) => {
    e.preventDefault();
    setShowPenaltyModal(true)
  }
  const closeModal=()=>{
    setShowModal(false)
  }
  const closeDateModal=()=>{
    setShowDateModal(false)
  }
  const closeMobileModal=()=>{
    setShowMobileModal(false)
  }
  const closePenaltyModal=()=>{
    setShowPenaltyModal(false)
  }
  const Heading = (props) => {
    return <h1 className="heading-m">{props.label}</h1>;
  };
  const Close = () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#000">
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
  const [fieldError, setFieldError] = useState({})
  const handleChangeTimeReturn = (e) => {
    const { name, value } = e.target;
    setReturnTimeFormData({
        ...returnTimeFormData,
        [name]: value,
    });
    // validateForm(formData)
    const newErrors = validateForm({
      ...returnTimeFormData,
      [name]: value,
  });
    setFieldError(newErrors);
  };
  const handleChangeMobileReturn = (e) => {
    const { name, value } = e.target;
    setReturnMobileFormData({
        ...returnMobileFormData,
        [name]: value,
    });
    // validateForm(formData)
    const newErrors = validateForm({
      ...returnMobileFormData,
      [name]: value,
  });
    setFieldError(newErrors);
  };
  const handleChangePenaltyReturn = (e) => {
    const { name, value } = e.target;
    setReturnPenaltyFormData({
        ...returnPenaltyFormData,
        [name]: value,
    });
    // validateForm(formData)
    const newErrors = validateForm({
      ...returnPenaltyFormData,
      [name]: value,
  });
    setFieldError(newErrors);
  };
  const handleChangeReturn = (e) => {
    const { name, value } = e.target;
    setReturnFormData({
        ...returnFormData,
        [name]: value,
    });
    // validateForm(formData)
    const newErrors = validateForm({
      ...returnFormData,
      [name]: value,
  });
    setFieldError(newErrors);
  };
  const validateForm = (data) => {
    const errors = {};

    var exists = Object.keys(data).forEach(function(k) {
        if(!data[k]) {
          return errors[k] = 'This field is required';
        }
    });

    return errors;
  };
  
  const onSave = ()=>{
    const newErrors = validateForm(returnFormData);
    setFieldError(newErrors);
    if (Object.keys(newErrors).length === 0) {
        
        setShowModal(false);
    } else {
      return;
    }
   
  };
  const onSaveDateTime = ()=>{
    const newErrors = validateForm(returnTimeFormData);
    setFieldError(newErrors);
    if (Object.keys(newErrors).length === 0) {
        setShowDateModal(false);
    } else {
      return;
    }
   
  };
  const onSaveMobileNo = ()=>{
    const newErrors = validateForm(returnMobileFormData);
    setFieldError(newErrors);
    if (Object.keys(newErrors).length === 0) {
        setShowMobileModal(false);
    } else {
      return;
    }
   
  };
  const onSavePenaltyAmount = ()=>{
    const newErrors = validateForm(returnPenaltyFormData);
    setFieldError(newErrors);
    if (Object.keys(newErrors).length === 0) {
        setShowPenaltyModal(false);
    } else {
      return;
    }
   
  };
  const printDiv = (e,divId)=> {
    e.preventDefault();
    // var printContent = document.getElementById(divId);
    // var WinPrint = window.open('', '', 'width=900,height=650');
    // WinPrint.document.write(printContent.innerHTML);
    // WinPrint.document.close();
    // WinPrint.focus();
    // WinPrint.print();
    // WinPrint.close();
    var printContents = document.getElementById(divId).innerHTML;
    var originalContents = document.body.innerHTML;

    document.body.innerHTML = printContents;

    window.print();

    document.body.innerHTML = originalContents;
    return false;
    
  }
  const onSubmit = (e) => {
    e.preventDefault();
    let noticeDetails = {
      name: name,
      address: address,
      "propertyId": propertyId,
      "acknowledgementNumber": acknowledgementNumber,
      dateOfAnnualRet: dateOfAnnualRet,
      "assessmentYear": selectedFinancialYear?.code,
      "noticeType": "Notice to enter Premises",      
      "tenantId": tenantId,      
      "channel": "CITIZEN",
      "noticeComment": [],
      authorisedpersonName: returnFormData.authorisedpersonName,
      designation: returnFormData.designation,
      entryDate: returnTimeFormData?.entryDate,
      entryTime: returnTimeFormData?.entryTime,
      mobilenumber: returnMobileFormData.mobilenumber,
      penaltyAmount: returnPenaltyFormData.penaltyAmount
    }
    props.submit(noticeDetails)
  }
  const onCancelNotice = () => {
    
  }
  const citizenStyle = props?.isCitizen ? { width: "100%" } : {};
  const citizenStyleMaxWidth = props?.isCitizen ? {  } : {maxWidth: "100%"};
  return (
    <div>
      
      <div  >
        <div className="row">
          <form>
            <div id="form-print">
            {<Header>{t("Notice to Enter Premises")}</Header>}
              <div className="row card" style={{ ...citizenStyleMaxWidth }}>
                <div >
                  <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                    <CardLabel>{`${t("Name")}`}</CardLabel>
                    <TextInput
                      style={{ background: "#FAFAFA" }}
                      key={'name'}
                      name={'name'}
                      value={name}
                      onChange={(e) => onChangeName(e)}
                      isMandatory={false}
                      disable={props?.isCitizen ? true : false}
                    />
                  </div>
                  <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                    <CardLabel>{`${t("Property Address")}`}</CardLabel>
                    <TextInput
                      style={{ background: "#FAFAFA" }}
                      key={'address'}
                      name={'address'}
                      value={address}
                      onChange={(e) => onChangePtAddress(e)}
                      isMandatory={false}
                      disable={props?.isCitizen ? true : false}
                    />
                  </div>
                </div>
                <div>
                  <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                    <CardLabel>{`${t("Property ID")}`}</CardLabel>
                    <TextInput
                      style={{ background: "#FAFAFA" }}
                      key={'propertyId'}
                      name={'propertyId'}
                      value={propertyId}
                      onChange={(e) => onChangePtId(e)}
                      isMandatory={false}
                      disable={props?.isCitizen ? true : false}
                    />
                  </div>
                  <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                    <CardLabel>{`${t("Return Acknowledgement Number")}`}</CardLabel>
                    <TextInput
                      style={{ background: "#FAFAFA" }}
                      key={'acknowledgementNumber'}
                      name={'acknowledgementNumber'}
                      value={acknowledgementNumber}
                      onChange={(e) => onChangeAcknowledgementNo(e)}
                      isMandatory={false}
                      disable={props?.isCitizen ? true : false}
                    />
                  </div>

                </div>
                <div>
                  <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                    <CardLabel>{`${t("Date of Submission of Annual Return")}`}</CardLabel>
                    
                    <input
                      className={`employee-card-input ${props.disabled ? "disabled" : ""}`}
                      style={{ width: "calc(100%-62px)" }}
                      value={dateOfAnnualRet ? dateOfAnnualRet : ""}
                      type="date"
                      onChange={(d) => {
                        setDateOfAnnualRet(d.target.value);
                      }}
                      required={false}
                      readOnly={props?.isCitizen ? true : false}
                      disabled={props?.isCitizen ? true : false}
                    />
                  </div>
                  <div className="col-sm-4 assment-yr-cls" style={{ width: '48%', display: 'inline-block', position: 'relative', top: '0px' }}>
                    <CardLabel>{`${t("Assessment Year")}`}</CardLabel>
                    <Dropdown isMandatory optionCardStyles={{ zIndex: 111111 }} selected={selectedFinancialYear} optionKey="name" option={financialYears} select={setSelectedFinancialYear} t={t} 
                    isDisabled={props?.isCitizen ? true : false}
                    disable={props?.isCitizen ? true : false}
                    />

                  </div>
                </div>
                <hr />
                <div style={{ marginTop: '20px' }}>
                  <p><span style={{ fontWeight: 600 }}>Sub: Notice under Rule 29 of Manipur Municipalities (Property Tax) Rules, 2019 </span>
                    <ul style={{ marginTop: '10px' }} className="notice-txt">
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px', ...citizenStyle }}>
                        Whereas it is necessary to verify the covered area/land area/identity of the owner or occupie of the property, and for this it is necessary to enter the property.
                      </li>
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', ...citizenStyle }}>
                            You are here by informed that {returnFormData?.authorisedpersonName && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnFormData?.authorisedpersonName}</span>}{!returnFormData?.authorisedpersonName && <span>__________________</span>}[name of authorized person] designated as {returnFormData?.designation && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnFormData?.designation}</span>}{!returnFormData?.designation && <span>__________________</span>} [exact designation of the search officer] has been authorized to enter your property for the above purpose.
                        </div>
                        
                        {!props?.isCitizen && <div style={{ width: '40%', display: 'inline' }}>
                          <button id="printPageButton" onClick={(e) => onEditNameAndDesg(e)} className="submit-bar"
                            style={{
                              color: 'white',
                              float: 'right',
                              width: '10%',
                              marginTop: "-60px"
                            }}
                          >
                            + {t("Edit")}
                          </button>
                        </div>}
                      </li>

                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', ...citizenStyle }}>
                        {returnFormData?.authorisedpersonName && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnFormData?.authorisedpersonName}</span>}{!returnFormData?.authorisedpersonName && <span>__________________</span>} proposes to visit your premises on {returnTimeFormData?.entryDate && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.entryDate}</span>}{!returnTimeFormData?.entryDate && <span>__________________</span>} at {returnTimeFormData?.entryTime && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.entryTime}</span>} {!returnTimeFormData?.entryTime && <span>________________ </span>}in the chamber of the undersigned.
                        </div>
                        {!props?.isCitizen && <div style={{ width: '40%', display: 'inline' }}>
                          <button id="printPageButton" onClick={(e) => onEditDate(e)} className="submit-bar"
                            style={{
                              color: 'white',
                              float: 'right',
                              width: '10%',
                              marginTop: "-40px"
                            }}
                          >
                            {t("Edit")}
                          </button>
                        </div>}
                      </li>

                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', ...citizenStyle }}>
                          In case this is not convenient to you, you may kindly contact him at telephone number {returnMobileFormData?.mobilenumber && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnMobileFormData?.mobilenumber}</span>}{!returnMobileFormData?.mobilenumber && <span>__________________</span>}, to fix a suitable time and date.
                        </div>
                        {!props?.isCitizen && <div style={{ width: '40%', display: 'inline' }}>
                          <button id="printPageButton" onClick={(e) => onEditMobile(e)} className="submit-bar"
                            style={{
                              color: 'white',
                              float: 'right',
                              width: '10%',
                              marginTop: "-40px"
                            }}
                          >
                            {t("Edit")}
                          </button>
                        </div>}
                      </li>

                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', ...citizenStyle }}>
                          In the event, you fail to cooperate with the designated officer or fail to comply with the notice, a penalty upto Rs {returnPenaltyFormData?.penaltyAmount && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnPenaltyFormData?.penaltyAmount}</span>}{!returnPenaltyFormData?.penaltyAmount && <span>__________________</span>} may be imposed under Rule 29 for each default.
                        </div>
                        {!props?.isCitizen && <div style={{ width: '40%', display: 'inline' }}>
                          <button id="printPageButton" onClick={(e) => onEditPenalty(e)} className="submit-bar"
                            style={{
                              color: 'white',
                              float: 'right',
                              width: '10%',
                              marginTop: "-40px"
                            }}
                          >
                            {t("Edit")}
                          </button>
                        </div>}
                      </li>
                    </ul>
                  </p>
                </div>
              </div>
              <div className="card" style={{ ...citizenStyleMaxWidth }}>
                <div className="row">
                    <div className="" style={{display: "inline-block", width: "90%", paddingLeft: "15px"}}>
                        <span>Date(mm/dd/yyyy)</span>
                        <div>{new Date().toLocaleDateString()}</div>
                    </div>
                    <div className="" style={{display: "inline-block", width: "10%"}}>
                        <span>Place</span>
                        <div>Manipur</div>
                    </div>
                </div>
              </div>
            </div>
            
            <div className="card" style={{ ...citizenStyleMaxWidth }}>
              <div style={{display: 'inline-flex'}}>
                <div style={{ width: '100%', display: 'inline' }}>
                  <button onClick={(e) => printDiv(e,'form-print')} className="submit-bar"
                    style={{
                      color: 'white',
                      float: 'left'
                    }}
                  >
                    {t("Print")}
                  </button>
                </div>
                {!props?.isCitizen && <div style={{  display: 'inline' }}>
                  <button onClick={() => onCancelNotice()} className="submit-bar"
                    style={{
                      color: 'white',
                      float: 'right'
                    }}
                  >
                    {t("Cancel")}
                  </button>
                </div>}
               {!props?.isCitizen && <div style={{ display: 'inline' }}>
                  <button onClick={onSubmit} className="submit-bar"
                    style={{
                      color: 'white',
                      float: 'right',
                      marginLeft: '10px'
                    }}
                  >
                    {t("Submit")}
                  </button>
                </div>}
              </div>
            </div>
          </form>
        </div>
      </div>
      {showModal && <Modal
          headerBarMain={<Heading label={t('Visiting Authority')} />}
          headerBarEnd={<CloseBtn onClick={closeModal} />}
          actionCancelOnSubmit={closeModal}
          hideSubmit={true}
          actionSaveOnSubmit={() => {}}
          formId="modal-action"
          isDisabled={false}
          width={'60%'}
          popupStyles={{width: "50%"}}
        >
          <div >
          <div className="row" style={{padding: "10px"}}>
              <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                
                <label for="formControlInputAuthorizedPersonName" class="form-label">Authorized Person Name*</label>
                <input type="text" className={fieldError.authorisedpersonName ? "form-control error-message" : "form-control"} id="formControlInputAuthorizedPersonName" name="authorisedpersonName" placeholder="Enter Authorized Person Name" value={returnFormData.authorisedpersonName} onChange={handleChangeReturn} required />
                {fieldError.authorisedpersonName &&
                        <span className="error-message">
                            {fieldError.authorisedpersonName}
                        </span>
                    }
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
               
                <label for="formControlInputAuthorizedPersonDesg" class="form-label">Authorized Person Designation*</label>
                <input type="text" className={fieldError.designation ? "form-control error-message" : "form-control"} id="formControlInputAuthorizedPersonDesg" name="designation" placeholder="Enter Authorized Person Designation" value={returnFormData.designation} onChange={handleChangeReturn} required />
                {fieldError.designation &&
                    <span className="error-message">
                        {fieldError.designation}
                    </span>
                }
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
        </Modal> }

        {showDateModal && <Modal
          headerBarMain={<Heading label={t('Visiting Time')} />}
          headerBarEnd={<CloseBtn onClick={closeDateModal} />}
          actionCancelOnSubmit={closeDateModal}
          hideSubmit={true}
          actionSaveOnSubmit={() => {}}
          formId="modal-action"
          isDisabled={false}
          width={'60%'}
        >
          <div>
            <div className="row" style={{padding: "10px"}}>
              <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                {/* <CardLabel>{`${t("Date")}`}</CardLabel>
                <input
                  className={`employee-card-input ${props.disabled ? "disabled" : ""}`}
                  // className={`${props.disabled ? "disabled" : ""}`}
                  style={{ width: "calc(100%-62px)" }}
                  // style={{ right: "6px", zIndex: "100", top: 6, position: "absolute", opacity: 0, width: "100%" }}
                  value={editDate ? editDate : ""}
                  type="date"
                  onChange={(d) => {
                    setEditDate(d.target.value);
                  }}
                  required={false}
                /> */}
                <label for="formControlInputReturnDate" class="form-label">Date*</label>
                <input type="date" className={fieldError.entryDate ? "form-control error-message" : "form-control"} id="formControlInputReturnDate" name="entryDate" placeholder="Enter date" value={returnTimeFormData.entryDate} onChange={handleChangeTimeReturn} required />
                {fieldError.entryDate &&
                    <span className="error-message">
                        {fieldError.entryDate}
                    </span>
                }
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                {/* <CardLabel>{`${t("Time")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'time'}
                  name={'time'}
                  value={time}
                  onChange={(e) => setTime(e.target.value)}
                  isMandatory={true}
                  disable={props?.isCitizen ? true : false}
                  type={'number'}
                /> */}
                <label for="formControlInputReturnTime" class="form-label">Time*</label>
                <input type="time" className={fieldError.entryTime ? "form-control error-message" : "form-control"} id="formControlInputReturnTime" name="entryTime" placeholder="Enter Time" value={returnTimeFormData.entryTime} onChange={handleChangeTimeReturn} required />
                {fieldError.entryTime &&
                    <span className="error-message">
                        {fieldError.entryTime}
                    </span>
                }
              </div>

            </div>
            
            <div className="footer" style={{height: '30px', background: '#ebebeb'}}>
              <div style={{padding: '10px'}}>
                <div style={{ width: '40%', display: 'inline' }}>
                  <button onClick={() => onSaveDateTime()} className="submit-bar"
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
        </Modal> }

        {showMobileModal && <Modal
          headerBarMain={<Heading label={t('Mobile/Telephone Number')} />}
          headerBarEnd={<CloseBtn onClick={closeMobileModal} />}
          actionCancelOnSubmit={closeMobileModal}
          hideSubmit={true}
          actionSaveOnSubmit={() => {}}
          formId="modal-action"
          isDisabled={false}
          width={'60%'}
        >
          <div>
            <div className="row" style={{padding: "10px"}}>
              <div className="col-sm-12" style={{ width: '100%', marginRight: '10px', display: 'inline-block' }}>
                
                <label for="formControlInputReturnMobileNo" class="form-label">Mobile/Telephone No.*</label>
                <input type="text" className={fieldError.mobilenumber ? "form-control error-message" : "form-control"} id="formControlInputReturnMobileNo" name="mobilenumber" placeholder="Enter Mobile/Telephone No." value={returnMobileFormData.mobilenumber} onChange={handleChangeMobileReturn} required />
                {fieldError.mobilenumber &&
                    <span className="error-message">
                        {fieldError.mobilenumber}
                    </span>
                }
              </div>

            </div>
            
            <div className="footer" style={{height: '30px', background: '#ebebeb'}}>
              <div style={{padding: '10px'}}>
                <div style={{ width: '40%', display: 'inline' }}>
                  <button onClick={() => onSaveMobileNo()} className="submit-bar"
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
        </Modal> }
        {showPenaltyModal && <Modal
          headerBarMain={<Heading label={t('Penalty Charges')} />}
          headerBarEnd={<CloseBtn onClick={closePenaltyModal} />}
          actionCancelOnSubmit={closePenaltyModal}
          hideSubmit={true}
          actionSaveOnSubmit={() => {}}
          formId="modal-action"
          isDisabled={false}
          width={'60%'}
        >
          <div>
            <div className="row" style={{padding: "10px"}}>
              <div className="col-sm-12" style={{ width: '100%', marginRight: '10px', display: 'inline-block' }}>
                
                <label for="formControlInputReturnPenalty" class="form-label">Penalty Amount/Chanrges*</label>
                <input type="text" className={fieldError.penaltyAmount ? "form-control error-message" : "form-control"} id="formControlInputReturnPenalty" name="penaltyAmount" placeholder="Enter Penalty Amount/Chanrges" value={returnPenaltyFormData.penaltyAmount} onChange={handleChangePenaltyReturn} required />
                {fieldError.penaltyAmount &&
                    <span className="error-message">
                        {fieldError.penaltyAmount}
                    </span>
                }
              </div>

            </div>
            
            <div className="footer" style={{height: '30px', background: '#ebebeb'}}>
              <div style={{padding: '10px'}}>
                <div style={{ width: '40%', display: 'inline' }}>
                  <button onClick={() => onSavePenaltyAmount()} className="submit-bar"
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
        </Modal> }

    </div>
  );
};

export default NoticeToEnterPremises;
