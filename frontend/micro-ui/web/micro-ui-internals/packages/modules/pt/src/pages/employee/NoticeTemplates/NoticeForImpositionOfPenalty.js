import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, DatePicker, Dropdown, Header, Modal, TextInput } from "@upyog/digit-ui-react-components";


const NoticeForImpositionOfPenalty = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { t } = useTranslation();
  const [financialYears, setFinancialYears] = useState([]);
  const [selectedFinancialYear, setSelectedFinancialYear] = useState(null);
  const [submissionDate, setSubmissionDate] = useState();

  const [notice, setNotice] = useState();
  const [showModal, setShowModal] = useState(false)
  const [showDateModal, setShowDateModal] = useState(false)
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
    particulars: null,
    asPerReturnFiled: null,
    asPerMunicipality: null,
    remarks: null
  });
  const [returnTimeFormData, setReturnTimeFormData] = useState({
    time: null,
    date: null
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
      console.log("=====", financialYearsData["egf-master"]?.["FinancialYear"]);
      setFinancialYears(financialYearsData["egf-master"]?.["FinancialYear"]);
    }
  }, [financialYearsData]);
  const onAddTabData = (e) => {
    e.preventDefault();
    setShowModal(true)
  }
  const onEditDate = (e) => {
    e.preventDefault();
    setShowDateModal(true)
  }
  const closeModal=()=>{
    setShowModal(false)
  }
  const closeDateModal=()=>{
    setShowDateModal(false)
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
    console.log("validateForm==",data)
    const errors = {};

    var exists = Object.keys(data).forEach(function(k) {
        if(!data[k]) {
          return errors[k] = 'This field is required';
        }
    });

    return errors;
  };
  const onSaveAndAdd = ()=>{
    const newErrors = validateForm(returnFormData);
    setFieldError(newErrors);
    if (Object.keys(newErrors).length === 0) {
        let obj = {
          ...returnFormData
        };
        let list = tableList || [];
        list.push(obj)
        setTableList(list);
        setReturnFormData({
          particulars: null,
          asPerReturnFiled: null,
          asPerMunicipality: null,
          remarks: null
        });
        // setShowModal(false);
    } else {
      return;
    }
  };
  const onSave = ()=>{
    const newErrors = validateForm(returnFormData);
    setFieldError(newErrors);
    if (Object.keys(newErrors).length === 0) {
        let obj = {
          ...returnFormData
        };
        let list = tableList || [];
        list.push(obj)
        setTableList(list);
        setReturnFormData({
          particulars: null,
          asPerReturnFiled: null,
          asPerMunicipality: null,
          remarks: null
        });
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
  const onSubmit = () => {

  }
  const onCancelNotice = () => {
    
  }
  return (
    <div>
      
      <div  >
        <div className="row">
          <form>
            <div id="form-print">
            {<Header>{t("Notice For Imposition of Penalty")}</Header>}
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
                    {/* <DatePicker
                      isRequired={true}
                      date={submissionDate}
                      onChange={(d) => {
                        setSubmissionDate(d);
                      }}
                    /> */}
                    <input
                      className={`employee-card-input ${props.disabled ? "disabled" : ""}`}
                      // className={`${props.disabled ? "disabled" : ""}`}
                      style={{ width: "calc(100%-62px)" }}
                      // style={{ right: "6px", zIndex: "100", top: 6, position: "absolute", opacity: 0, width: "100%" }}
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
                  <p><span style={{ fontWeight: 600 }}>Sub: Notice under Rule 29 </span>
                    <ul style={{ marginTop: '10px' }} className="notice-txt">
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        Penalty under Rule is leviable for the following reasons: (Tick whichever is applicable)
                        <div>
                            <ul>
                                <li>In the event, the person failed to pay the dues as per Rule 23 penalty may be levied;</li>
                                <li>Where a regular assessment is made under Rule 33 and the Tax reassessed exceeds the tax paid under self-assessment by more than 20 percent thereof, penalty may be levied on the additional tax charged;</li>
                                <li>Where a best judgement assessment is made under Rule 34, penalty may be levied;</li>
                                <li>In the event of failure of the person to comply with the notice under Rule 36, penalty may be levied;</li>
                                <li>In the event, when person failed to produce necessary documents and evidence called by the assessing officer or the appellate authority, penalty may be levied;</li>
                                <li>In the event the person knowingly or willfully furnishes incorrect information or documentation;</li>
                                <li>In the event obstruct any authority appointed under Act and these rules in exercise of his powers;</li>
                            </ul>
                        </div>
                      </li>
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        Before levying a penalty, you are given an opportunity to show that the above default was for a reasonable cause.
                      </li>
                      
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%' }}>
                            You may present your case with all available records either in person or through an authorized representative on {returnTimeFormData?.date && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.date}</span>}{!returnTimeFormData?.date && <span>__________________</span>} at {returnTimeFormData?.time && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.time}</span>} {!returnTimeFormData?.time && <span>________________ </span>} in the chamber of the undersigned.
                        </div>
                        <div style={{ width: '40%', display: 'inline' }}>
                          <button id="printPageButton" onClick={(e) => onEditDate(e)} className="submit-bar"
                            style={{
                              color: 'white',
                              float: 'right',
                              width: '10%',
                              marginTop: "-80px"
                            }}
                          >
                            {t("Edit")}
                          </button>
                        </div>
                      </li>
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        In case you fail to appear on the appointed date and time or otherwise explain why the penalty should not be levied as above, the penalty shall be levied without any further intimation.
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
                  <button onClick={() => onSubmit()} className="submit-bar"
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
          headerBarMain={<Heading label={t('Return found to be incorrect')} />}
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
                {/* <CardLabel>{`${t("Particulars")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'particulars'}
                  name={'particulars'}
                  value={particulars}
                  onChange={(e) => onChangeParticulars(e)}
                  isMandatory={true}
                  disable={false}
                /> */}
                <label for="formControlInputParticulars" class="form-label">Particulars*</label>
                <input type="text" className={fieldError.particulars ? "form-control error-message" : "form-control"} id="formControlInputParticulars" name="particulars" placeholder="Enter Particulars" value={returnFormData.particulars} onChange={handleChangeReturn} required />
                {fieldError.particulars &&
                        <span className="error-message">
                            {fieldError.particulars}
                        </span>
                    }
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                {/* <CardLabel>{`${t("As per Return Filed")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'asPerReturnFiled'}
                  name={'asPerReturnFiled'}
                  value={asPerReturnFiled}
                  onChange={(e) => onChangeAsPerReturnFiled(e)}
                  isMandatory={true}
                  disable={false}
                /> */}
                <label for="formControlInputAsPerReturnFiled" class="form-label">As per Return Filed*</label>
                <input type="text" className={fieldError.asPerReturnFiled ? "form-control error-message" : "form-control"} id="formControlInputAsPerReturnFiled" name="asPerReturnFiled" placeholder="Enter As per Return Filed" value={returnFormData.asPerReturnFiled} onChange={handleChangeReturn} required />
                {fieldError.asPerReturnFiled &&
                    <span className="error-message">
                        {fieldError.asPerReturnFiled}
                    </span>
                }
              </div>
              <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                {/* <CardLabel>{`${t("As per Municipality")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'asPerMunicipality'}
                  name={'asPerMunicipality'}
                  value={asPerMunicipality}
                  onChange={(e) => onChangeMunicipality(e)}
                  isMandatory={true}
                  disable={false}
                /> */}
                <label for="formControlInputAsPerMunicipality" class="form-label">As per Municipality*</label>
                <input type="text" className={fieldError.asPerMunicipality ? "form-control error-message" : "form-control"} id="formControlInputAsPerMunicipality" name="asPerMunicipality" placeholder="Enter As per Municipality" value={returnFormData.asPerMunicipality} onChange={handleChangeReturn} required />
                {fieldError.asPerMunicipality &&
                    <span className="error-message">
                        {fieldError.asPerMunicipality}
                    </span>
                }
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                {/* <CardLabel>{`${t("Remarks")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'remarks'}
                  name={'remarks'}
                  value={remarks}
                  onChange={(e) => onChangeRemarks(e)}
                  isMandatory={true}
                  disable={false}
                /> */}
                <label for="formControlInputReturnRemarks" class="form-label">Remarks*</label>
                <input type="text" className={fieldError.remarks ? "form-control error-message" : "form-control"} id="formControlInputReturnRemarks" name="remarks" placeholder="Enter Remarks" value={returnFormData.remarks} onChange={handleChangeReturn} required />
                {fieldError.remarks &&
                    <span className="error-message">
                        {fieldError.remarks}
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
                <div style={{ width: '40%', display: 'inline' }}>
                  <button onClick={() => onSaveAndAdd()} className="submit-bar"
                    style={{
                      color: 'white',
                      float: 'right',
                      width: '30%'
                    }}
                  >
                    {t("Save & Add")}
                  </button>
                </div>
              </div>
              
            </div>
          </div>
        </Modal> }

        {showDateModal && <Modal
          headerBarMain={<Heading label={t('Select Date & Time')} />}
          headerBarEnd={<CloseBtn onClick={closeModal} />}
          actionCancelOnSubmit={closeModal}
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

    </div>
  );
};

export default NoticeForImpositionOfPenalty;
