import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, CheckBox, DatePicker, Dropdown, Header, Modal, TextInput } from "@upyog/digit-ui-react-components";


const NoticeForImpositionOfPenalty = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { t } = useTranslation();
  const [financialYears, setFinancialYears] = useState([]);
  const [selectedFinancialYear, setSelectedFinancialYear] = useState(props?.noticeData && props?.noticeData.assessmentYear? {code: props?.noticeData.assessmentYear, name: props?.noticeData.assessmentYear} : null);
  const [dateOfAnnualRet, setDateOfAnnualRet] = useState(props?.noticeData?.dateOfAnnualRet ? props?.noticeData?.dateOfAnnualRet : props?.noticeData?.dateOfOrder ? props?.noticeData?.dateOfOrder : null);

  const [notice, setNotice] = useState();
  const [showModal, setShowModal] = useState(false)
  const [showDateModal, setShowDateModal] = useState(false)
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
  const [returnTimeFormData, setReturnTimeFormData] = useState({
    entryTime: props?.noticeData?.entryTime ? props?.noticeData?.entryTime : null,
    entryDate: props?.noticeData?.entryDate ? props?.noticeData?.entryDate : null
  }); 
  const [rulesOfPenalty, setRulesOfPenalty] = useState({
    Rule_23: props?.noticeData?.Rule_23 ? props?.noticeData?.Rule_23 : false,
    Rule_33: props?.noticeData?.Rule_33 ? props?.noticeData?.Rule_33 : false,
    Rule_34: props?.noticeData?.Rule_34 ? props?.noticeData?.Rule_34 : false,
    Rule_36: props?.noticeData?.Rule_36 ? props?.noticeData?.Rule_36 : false,
    Failedtoproducenecessarydocuments: props?.noticeData?.Failedtoproducenecessarydocuments ? props?.noticeData?.Failedtoproducenecessarydocuments : false,
    Willfullyfurnishesincorrectinformation: props?.noticeData?.Willfullyfurnishesincorrectinformation ? props?.noticeData?.Willfullyfurnishesincorrectinformation : false,
    Obstructanyauthorityappointed: props?.noticeData?.Obstructanyauthorityappointed ? props?.noticeData?.Obstructanyauthorityappointed : false
  }); 
const onChangeRuleOfPenalty=(e)=>{
  console.log("====",e)
  const { name, checked } = e.target;
  setRulesOfPenalty({
        ...rulesOfPenalty,
        [name]: checked || false,
    });
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
  
  const onEditDate = (e) => {
    e.preventDefault();
    setShowDateModal(true)
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
 
  const validateForm = (data) => {
    const errors = {};

    var exists = Object.keys(data).forEach(function(k) {
        if(!data[k]) {
          return errors[k] = 'This field is required';
        }
    });

    return errors;
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
  let [count, setCount] = useState(0);
  const printDiv = (e,divId)=> {
    e.preventDefault();
    var printContents = document.getElementById(divId).innerHTML;
    var originalContents = document.body.innerHTML;

    document.body.innerHTML = printContents;

    window.print();
    
    document.body.innerHTML = originalContents;
    setCount(1)
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
      "noticeType": "Notice for Imposition of Penalty",  
      "tenantId": tenantId,      
      "channel": "CITIZEN",
      "noticeComment": [],
      entryDate: returnTimeFormData?.entryDate,
      entryTime: returnTimeFormData?.entryTime,
      Rule_23: rulesOfPenalty?.Rule_23 || false,
      Rule_33: rulesOfPenalty?.Rule_33 || false,
      Rule_34: rulesOfPenalty?.Rule_34 || false,
      Rule_36: rulesOfPenalty?.Rule_36 || false,
      Failedtoproducenecessarydocuments: rulesOfPenalty?.Failedtoproducenecessarydocuments || false,
      Willfullyfurnishesincorrectinformation: rulesOfPenalty?.Willfullyfurnishesincorrectinformation || false,
      Obstructanyauthorityappointed: rulesOfPenalty?.Obstructanyauthorityappointed || false
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
            <div id="form-print-impositionofpenalty">
            {<Header>{t("Notice For Imposition of Penalty")}</Header>}
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
                      disabled={props?.isCitizen ? true : false}
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
                      disabled={props?.isCitizen ? true : false}
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
                      disabled={props?.isCitizen ? true : false}
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
                      disabled={props?.isCitizen ? true : false}
                    />
                  </div>

                </div>
                <div>
                  <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                    <CardLabel>{`${t("Date of Submission of Annual Return")}`}</CardLabel>
                    
                    <input
                      className={`employee-card-input ${props.disabled ? "disabled" : ""}`}
                      style={{ width: "calc(100%-62px)" }}
                      value={dateOfAnnualRet  ? dateOfAnnualRet  : ""}
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
                  <p><span style={{ fontWeight: 600 }}>Sub: Notice under Rule 29 </span>
                    <ul style={{ marginTop: '10px' }} className="notice-txt">
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        Penalty under Rule is leviable for the following reasons: (Tick whichever is applicable)
                        <div style={{marginTop: "10px"}}>
                            <ul>
                                <li>
                                  <CheckBox
                                    className="form-field"
                                    name="Rule_23"
                                    label={`${t("In the event, the person failed to pay the dues as per Rule 23 penalty may be levied;")}`}
                                    onChange={(e) => onChangeRuleOfPenalty(e)}
                                    value={rulesOfPenalty?.Rule_23}
                                    checked={rulesOfPenalty?.Rule_23 || false}
                                    disable={props?.isCitizen ? true : false}
                                    // style={window.location.href.includes("/citizen/") ? { paddingTop: "10px" } : {}}
                                  />
                                </li>
                                <li>
                                  <CheckBox
                                    className="form-field"
                                    name="Rule_33"
                                    label={`${t("Where a regular assessment is made under Rule 33 and the Tax reassessed exceeds the tax paid under self-assessment by more than 20 percent thereof, penalty may be levied on the additional tax charged;")}`}
                                    onChange={(e) => onChangeRuleOfPenalty(e)}
                                    value={rulesOfPenalty?.Rule_33}
                                    checked={rulesOfPenalty?.Rule_33 || false}
                                    disable={props?.isCitizen ? true : false}
                                    // style={window.location.href.includes("/citizen/") ? { paddingTop: "10px" } : {}}
                                  />
                                </li>
                                <li>
                                  <CheckBox
                                    className="form-field"
                                    name="Rule_34"
                                    label={`${t("Where a best judgement assessment is made under Rule 34, penalty may be levied;")}`}
                                    onChange={(e) => onChangeRuleOfPenalty(e)}
                                    value={rulesOfPenalty?.Rule_34}
                                    checked={rulesOfPenalty?.Rule_34 || false}
                                    disable={props?.isCitizen ? true : false}
                                    // style={window.location.href.includes("/citizen/") ? { paddingTop: "10px" } : {}}
                                  />
                                </li>
                                <li>
                                  <CheckBox
                                    className="form-field"
                                    name="Rule_36"
                                    label={`${t("In the event of failure of the person to comply with the notice under Rule 36, penalty may be levied;")}`}
                                    onChange={(e) => onChangeRuleOfPenalty(e)}
                                    value={rulesOfPenalty?.Rule_36}
                                    checked={rulesOfPenalty?.Rule_36 || false}
                                    disable={props?.isCitizen ? true : false}
                                    // style={window.location.href.includes("/citizen/") ? { paddingTop: "10px" } : {}}
                                  />
                                </li>
                                <li>
                                  <CheckBox
                                    className="form-field"
                                    name="Failedtoproducenecessarydocuments"
                                    label={`${t("In the event, when person failed to produce necessary documents and evidence called by the assessing officer or the appellate authority, penalty may be levied;")}`}
                                    onChange={(e) => onChangeRuleOfPenalty(e)}
                                    value={rulesOfPenalty?.Failedtoproducenecessarydocuments}
                                    checked={rulesOfPenalty?.Failedtoproducenecessarydocuments || false}
                                    disable={props?.isCitizen ? true : false}
                                    // style={window.location.href.includes("/citizen/") ? { paddingTop: "10px" } : {}}
                                  />
                                </li>
                                <li>
                                  <CheckBox
                                    className="form-field"
                                    name="Willfullyfurnishesincorrectinformation"
                                    label={`${t("In the event the person knowingly or willfully furnishes incorrect information or documentation;")}`}
                                    onChange={(e) => onChangeRuleOfPenalty(e)}
                                    value={rulesOfPenalty?.Willfullyfurnishesincorrectinformation}
                                    checked={rulesOfPenalty?.Willfullyfurnishesincorrectinformation || false}
                                    disable={props?.isCitizen ? true : false}
                                    // style={window.location.href.includes("/citizen/") ? { paddingTop: "10px" } : {}}
                                  />
                                </li>
                                <li>
                                  <CheckBox
                                    className="form-field"
                                    name="Obstructanyauthorityappointed"
                                    label={`${t("In the event obstruct any authority appointed under Act and these rules in exercise of his powers;")}`}
                                    onChange={(e) => onChangeRuleOfPenalty(e)}
                                    value={rulesOfPenalty?.Obstructanyauthorityappointed}
                                    checked={rulesOfPenalty?.Obstructanyauthorityappointed || false}
                                    disable={props?.isCitizen ? true : false}
                                    // style={window.location.href.includes("/citizen/") ? { paddingTop: "10px" } : {}}
                                  />
                                </li>
                            </ul>
                        </div>
                      </li>
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        Before levying a penalty, you are given an opportunity to show that the above default was for a reasonable cause.
                      </li>
                      
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%' }}>
                            You may present your case with all available records either in person or through an authorized representative on {returnTimeFormData?.entryDate && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.entryDate}</span>}{!returnTimeFormData?.entryDate && <span>__________________</span>} at {returnTimeFormData?.entryTime && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.entryTime}</span>} {!returnTimeFormData?.entryTime && <span>________________ </span>} in the chamber of the undersigned.
                        </div>
                        {!props?.isCitizen && <div style={{ width: '40%', display: 'inline' }}>
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
                        </div>}
                      </li>
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        In case you fail to appear on the appointed date and time or otherwise explain why the penalty should not be levied as above, the penalty shall be levied without any further intimation.
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
                  <button onClick={(e) => printDiv(e,'form-print-impositionofpenalty')} className="submit-bar"
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

        {showDateModal && <Modal
          headerBarMain={<Heading label={t('Select Date & Time')} />}
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
                
                <label for="formControlInputReturnDate" class="form-label">Date*</label>
                <input type="date" className={fieldError.entryDate ? "form-control error-message" : "form-control"} id="formControlInputReturnDate" name="entryDate" placeholder="Enter Date" value={returnTimeFormData.entryDate} onChange={handleChangeTimeReturn} required />
                {fieldError.entryDate &&
                    <span className="error-message">
                        {fieldError.entryDate}
                    </span>
                }
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                
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

    </div>
  );
};

export default NoticeForImpositionOfPenalty;
