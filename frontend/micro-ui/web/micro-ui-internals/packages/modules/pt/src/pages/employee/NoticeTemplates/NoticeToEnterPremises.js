import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, DatePicker, Dropdown, Header, Modal, TextInput } from "@upyog/digit-ui-react-components";


const NoticeToEnterPremises = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { t } = useTranslation();
  const [financialYears, setFinancialYears] = useState([]);
  const [selectedFinancialYear, setSelectedFinancialYear] = useState(null);
  const [submissionDate, setSubmissionDate] = useState();

  const [notice, setNotice] = useState();
  const [showModal, setShowModal] = useState(false)
  const [showDateModal, setShowDateModal] = useState(false);
  const [showMobileModal, setShowMobileModal] = useState(false);
  const [showPenaltyModal, setShowPenaltyModal] = useState(false);
  const handleChangeNotice = (value) => {

  }
  const [name, setName] = useState();
  const onChangeName = (e) => {
    setName(e.target?.value)
  }
  const [propertyAddress, setPropertyAddress] = useState();
  const onChangePtAddress = (e) => {
    setPropertyAddress(e.target?.value)
  }
  const [propertyId, setPropertyId] = useState();
  const onChangePtId = (e) => {
    setPropertyId(e.target?.value)
  }
  const [acknowledgementNo, setAcknowledgementNo] = useState();
  const onChangeAcknowledgementNo=(e)=>{
    setAcknowledgementNo(e.target.value)
  }
  const [returnFormData, setReturnFormData] = useState({
    authorizedPersonName: null,
    authorizedPersonDesignation: null
  });
  const [returnTimeFormData, setReturnTimeFormData] = useState({
    time: null,
    date: null
  });
  const [returnMobileFormData, setReturnMobileFormData] = useState({
    mobileNo: null
  });
  const [returnPenaltyFormData, setReturnPenaltyFormData] = useState({
    penaltyAmount: null
  });
  // const [particulars, setParticulars] = useState();
  // const [asPerReturnFiled, setAsPerReturnFiled] = useState();
  // const [asPerMunicipality, setAsPerMunicipality] = useState();
  // const [remarks, setRemarks] = useState();
  const [tableList, setTableList] = useState([]);
  const [timeMeridian, setTimeMeridian] = useState('');
  const [time, setTime] = useState();
  const [editDate, setEditDate] = useState();

  const onChangeParticulars = (e)=>{
    setParticulars(e.target.value)
  }
  const onChangeAsPerReturnFiled = (e)=>{
    setAsPerReturnFiled(e.target.value)
  }
  const onChangeMunicipality = (e)=>{
    setAsPerMunicipality(e.target.value)
  }
  const onChangeRemarks = (e)=>{
    setRemarks(e.target.value)
  }

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
      propertyAddress: propertyAddress,
      "propertyId": propertyId,
      "acknowledgementNumber": acknowledgementNo,
      assessmentDate: submissionDate,
      "assessmentYear": selectedFinancialYear?.code,
      "noticeType": "Notice to enter Premises",      
      "tenantId": tenantId,      
      "channel": "CITIZEN",
      "noticeComment": tableList,
      authorizedPersonName: returnFormData.authorizedPersonName,
      authorizedPersonDesignation: returnFormData.authorizedPersonDesignation,
      noticeDate: returnTimeFormData?.date,
      noticeTime: returnTimeFormData?.time,
      mobileNo: returnMobileFormData.mobileNo,
      penaltyAmount: returnPenaltyFormData.penaltyAmount
    }
    props.submit(noticeDetails)
  }
  const onCancelNotice = () => {
    
  }
  return (
    <div>
      
      <div  >
        <div className="row">
          <form>
            <div id="form-print">
            {<Header>{t("Notice to Enter Premises")}</Header>}
              <div className="row card" style={{ maxWidth: '100%' }}>
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
                      disable={false}
                    />
                  </div>
                  <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                    <CardLabel>{`${t("Property Address")}`}</CardLabel>
                    <TextInput
                      style={{ background: "#FAFAFA" }}
                      key={'propertyAddress'}
                      name={'propertyAddress'}
                      value={propertyAddress}
                      onChange={(e) => onChangePtAddress(e)}
                      isMandatory={false}
                      disable={false}
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
                      disable={false}
                    />
                  </div>
                  <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                    <CardLabel>{`${t("Return Acknowledgement Number")}`}</CardLabel>
                    <TextInput
                      style={{ background: "#FAFAFA" }}
                      key={'acknowledgementNo'}
                      name={'acknowledgementNo'}
                      value={acknowledgementNo}
                      onChange={(e) => onChangeAcknowledgementNo(e)}
                      isMandatory={false}
                      disable={false}
                    />
                  </div>

                </div>
                <div>
                  <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                    <CardLabel>{`${t("Date of Submission of Annual Return")}`}</CardLabel>
                    
                    <input
                      className={`employee-card-input ${props.disabled ? "disabled" : ""}`}
                      style={{ width: "calc(100%-62px)" }}
                      value={submissionDate ? submissionDate : ""}
                      type="date"
                      onChange={(d) => {
                        setSubmissionDate(d.target.value);
                      }}
                      required={false}
                    />
                  </div>
                  <div className="col-sm-4 assment-yr-cls" style={{ width: '48%', display: 'inline-block', position: 'relative', top: '0px' }}>
                    <CardLabel>{`${t("Assessment Year")}`}</CardLabel>
                    <Dropdown isMandatory optionCardStyles={{ zIndex: 111111 }} selected={selectedFinancialYear} optionKey="name" option={financialYears} select={setSelectedFinancialYear} t={t} />

                  </div>
                </div>
                <hr />
                <div style={{ marginTop: '20px' }}>
                  <p><span style={{ fontWeight: 600 }}>Sub: Notice under Rule 29 of Manipur Municipalities (Property Tax) Rules, 2019 </span>
                    <ul style={{ marginTop: '10px' }} className="notice-txt">
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        Whereas it is necessary to verify the covered area/land area/identity of the owner or occupie of the property, and for this it is necessary to enter the property.
                      </li>
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%' }}>
                            You are here by informed that {returnFormData?.authorizedPersonName && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnFormData?.authorizedPersonName}</span>}{!returnFormData?.authorizedPersonName && <span>__________________</span>}[name of authorized person] designated as {returnFormData?.authorizedPersonDesignation && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnFormData?.authorizedPersonDesignation}</span>}{!returnFormData?.authorizedPersonDesignation && <span>__________________</span>} [exact designation of the search officer] has been authorized to enter your property for the above purpose.
                        </div>
                        
                        <div style={{ width: '40%', display: 'inline' }}>
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
                        </div>
                      </li>

                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%' }}>
                        {returnFormData?.authorizedPersonName && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnFormData?.authorizedPersonName}</span>}{!returnFormData?.authorizedPersonName && <span>__________________</span>} proposes to visit your premises on {returnTimeFormData?.date && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.date}</span>}{!returnTimeFormData?.date && <span>__________________</span>} at {returnTimeFormData?.time && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.time}</span>} {!returnTimeFormData?.time && <span>________________ </span>}in the chamber of the undersigned.
                        </div>
                        <div style={{ width: '40%', display: 'inline' }}>
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
                        </div>
                      </li>

                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%' }}>
                          In case this is not convenient to you, you may kindly contact him at telephone number {returnMobileFormData?.mobileNo && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnMobileFormData?.mobileNo}</span>}{!returnMobileFormData?.mobileNo && <span>__________________</span>}, to fix a suitable time and date.
                        </div>
                        <div style={{ width: '40%', display: 'inline' }}>
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
                        </div>
                      </li>

                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%' }}>
                          In the event, you fail to cooperate with the designated officer or fail to comply with the noyice, a penalty upto Rs {returnPenaltyFormData?.penaltyAmount && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnPenaltyFormData?.penaltyAmount}</span>}{!returnPenaltyFormData?.penaltyAmount && <span>__________________</span>} may be imposed under Rule 29 for each default.
                        </div>
                        <div style={{ width: '40%', display: 'inline' }}>
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
                        </div>
                      </li>
                    </ul>
                  </p>
                </div>
              </div>
              <div className="card" style={{ maxWidth: '100%' }}>
                <div className="row">
                    <div className="" style={{display: "inline-block", width: "90%", paddingLeft: "15px"}}>
                        <span>Date</span>
                        <div>{new Date().toLocaleDateString()}</div>
                    </div>
                    <div className="" style={{display: "inline-block", width: "10%"}}>
                        <span>Place</span>
                        <div>Manipur</div>
                    </div>
                </div>
              </div>
            </div>
            
            <div className="card" style={{ maxWidth: '100%' }}>
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
                <div style={{  display: 'inline' }}>
                  <button onClick={() => onCancelNotice()} className="submit-bar"
                    style={{
                      color: 'white',
                      float: 'right'
                    }}
                  >
                    {t("Cancel")}
                  </button>
                </div>
                <div style={{ display: 'inline' }}>
                  <button onClick={onSubmit} className="submit-bar"
                    style={{
                      color: 'white',
                      float: 'right',
                      marginLeft: '10px'
                    }}
                  >
                    {t("Submit")}
                  </button>
                </div>
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
                <input type="text" className={fieldError.authorizedPersonName ? "form-control error-message" : "form-control"} id="formControlInputAuthorizedPersonName" name="authorizedPersonName" placeholder="Enter Authorized Person Name" value={returnFormData.authorizedPersonName} onChange={handleChangeReturn} required />
                {fieldError.authorizedPersonName &&
                        <span className="error-message">
                            {fieldError.authorizedPersonName}
                        </span>
                    }
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
               
                <label for="formControlInputAuthorizedPersonDesg" class="form-label">Authorized Person Designation*</label>
                <input type="text" className={fieldError.authorizedPersonDesignation ? "form-control error-message" : "form-control"} id="formControlInputAuthorizedPersonDesg" name="authorizedPersonDesignation" placeholder="Enter Authorized Person Designation" value={returnFormData.authorizedPersonDesignation} onChange={handleChangeReturn} required />
                {fieldError.authorizedPersonDesignation &&
                    <span className="error-message">
                        {fieldError.authorizedPersonDesignation}
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
                <input type="date" className={fieldError.date ? "form-control error-message" : "form-control"} id="formControlInputReturnDate" name="date" placeholder="Enter Date" value={returnTimeFormData.date} onChange={handleChangeTimeReturn} required />
                {fieldError.date &&
                    <span className="error-message">
                        {fieldError.date}
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
                  disable={false}
                  type={'number'}
                /> */}
                <label for="formControlInputReturnTime" class="form-label">Time*</label>
                <input type="time" className={fieldError.time ? "form-control error-message" : "form-control"} id="formControlInputReturnTime" name="time" placeholder="Enter Time" value={returnTimeFormData.time} onChange={handleChangeTimeReturn} required />
                {fieldError.time &&
                    <span className="error-message">
                        {fieldError.time}
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
                <input type="text" className={fieldError.mobileNo ? "form-control error-message" : "form-control"} id="formControlInputReturnMobileNo" name="mobileNo" placeholder="Enter Mobile/Telephone No." value={returnMobileFormData.mobileNo} onChange={handleChangeMobileReturn} required />
                {fieldError.mobileNo &&
                    <span className="error-message">
                        {fieldError.mobileNo}
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
