import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, DatePicker, Dropdown, Header, Modal, TextInput } from "@upyog/digit-ui-react-components";


const NoticeForReassessment = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { t } = useTranslation();
  const [financialYears, setFinancialYears] = useState([]);
  const [selectedFinancialYear, setSelectedFinancialYear] = useState(props?.noticeData && props?.noticeData.assessmentYear? {code: props?.noticeData.assessmentYear, name: props?.noticeData.assessmentYear} : null);
  const [dateOfAnnualRet, setDateOfAnnualRet] = useState(props?.noticeData?.dateOfAnnualRet ? props?.noticeData?.dateOfAnnualRet : props?.noticeData?.dateOfOrder ? props?.noticeData?.dateOfOrder : null);

  const [showModal, setShowModal] = useState(false)
  const [showDateModal, setShowDateModal] = useState(false)
  
  const [name, setName] = useState(props?.noticeData?.ownerName ? props?.noticeData?.ownerName : props?.noticeData?.name ? props?.noticeData?.name : null);
  const onChangeName = (e) => {
    setName(e.target?.value)
  }
  const [address, setAddress] = useState(props?.noticeData?.address ? props?.noticeData?.address : props?.noticeData?.propertyAddress ? props?.noticeData?.propertyAddress : null);
  const onChangePtAddress = (e) => {
    setPropertyAddress(e.target?.value)
  }
  const [propertyId, setPropertyId] = useState(props?.noticeData?.propertyId ? props?.noticeData?.propertyId : null);
  const onChangePtId = (e) => {
    setPropertyId(e.target?.value)
  }
  const [acknowledgementNumber, setAcknowledgementNumber] = useState(props?.noticeData?.acknowledgementNumber ? props?.noticeData?.acknowledgementNumber : props?.noticeData?.acknowldgementNumber ? props?.noticeData?.acknowldgementNumber : null);
  const onChangeAcknowledgementNo=(e)=>{
    setAcknowledgementNumber(e.target.value)
  }
  const [returnFormData, setReturnFormData] = useState({
    particulars: props?.noticeData?.particulars ? props?.noticeData?.particulars : null,
    asPerReturnFiled: props?.noticeData?.asPerReturnFiled ? props?.noticeData?.asPerReturnFiled : null,
    asPerMunicipality: props?.noticeData?.asPerMunicipality ? props?.noticeData?.asPerMunicipality : null,
    remarks: props?.noticeData?.remarks ? props?.noticeData?.remarks : null,
  });
  const [returnTimeFormData, setReturnTimeFormData] = useState({
    resolutionNo: null,
    resolutionDate: null,
    entryTime: props?.noticeData?.entryTime ? props?.noticeData?.entryTime : null,
    entryDate: props?.noticeData?.entryDate ? props?.noticeData?.entryDate : null
  });
  const [tableList, setTableList] = useState([]);
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
    e.preventDefault();
    let noticeDetails = {
      name: name,
      propertyAddress: propertyAddress,
      "propertyId": propertyId,
      "acknowledgementNumber": acknowledgementNo,
      assessmentDate: submissionDate,
      "assessmentYear": selectedFinancialYear?.code,
      "noticeType": "Notice for Re-Assessment",      
      "tenantId": tenantId,      
      "channel": "CITIZEN",
      "noticeComment": tableList,
      resolutionNo: returnTimeFormData.resolutionNo,
      resolutionDate: returnTimeFormData.resolutionDate,
      noticeDate: returnTimeFormData?.date,
      entryTime: returnTimeFormData?.entryTime
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
            {<Header>{t("Notice For Re-Assessment")}</Header>}
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
                  <p><span style={{ fontWeight: 600 }}>Sub: Notice under Rule 35 of Manipur Municipalities (Property Tax) Rules, 2019 </span>
                    <ul style={{ marginTop: '10px' }} className="notice-txt">
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        This is reference to the completion of assessment and order passed under Rule 31/Rule 32/Rule 33/Rule 34 nevertheless, it had been detected there are instance of willful supperssion of information.
                      </li>
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', display: 'inline-flex' }}>
                          The following information in the return found to be incorrect / No return has been filed under Rule 17/ Rule 18/ Rule 19:
                        </div>
                        
                        <div style={{ width: '40%', display: 'inline' }}>
                          <button id="printPageButton" onClick={(e) => onAddTabData(e)} className="submit-bar"
                            style={{
                              color: 'white',
                              float: 'right',
                              width: '10%'
                            }}
                          >
                            + {t("Add")}
                          </button>
                        </div>
                      </li>
                      {tableList && tableList.length>0 && <li style={{ marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '100%' }}>
                          <table style={{ width: '100%', border: '1px solid #b7b7b7'}}>
                            <tr style={{background: '#eaeaea', lineHeight: '35px'}}>
                              <th style={{paddingLeft: "10px"}}>Particulars</th>
                              <th>As per Return Filed</th>
                              <th>As per Municipality</th>
                              <th>Remarks</th>
                            </tr>
                            {tableList.map((e)=>{
                              return (<tr>
                                <td style={{paddingLeft: "10px"}}>{e?.particulars}</td>
                                <td>{e.asPerReturnFiled}</td>
                                <td>{e.asPerMunicipality}</td>
                                <td>{e.remarks}</td>
                              </tr>)
                            })}
                          </table>
                        </div>
                      </li>}
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%' }}>
                          If therefore purpose to initiate a re-assessment under Rule 35, which has the consent of the Board of Councilors vide Resolution no: {returnTimeFormData?.resolutionNo && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.resolutionNo}</span>}{!returnTimeFormData?.resolutionNo && <span>__________________</span>} dated {returnTimeFormData?.resolutionDate && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.resolutionDate}</span>} {!returnTimeFormData?.resolutionDate && <span>________________ </span>}. If therefore processed to the open the earlier assessment and modify the Annual Property Value(APV) and the Property tax on the basis of the information available with the municipality. In case, you disagree with the re-assessment and the proposed increase, you may present your case with all available records either in person or through an authorized representative on {returnTimeFormData?.date && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.date}</span>}{!returnTimeFormData?.date && <span>__________________</span>} at {returnTimeFormData?.entryTime && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.entryTime}</span>} {!returnTimeFormData?.entryTime && <span>________________ </span>} in the chamber of the undersigned.
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
                        In case you fail to appear on the appointed date and time or otherwise explain why the earlier assessment would not be re-openedand the APV and tax should not be assessed as above, the assessment will be frames under Rule 35 on the basis of the information available with the municipality as indicated above.
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
                
                <label for="formControlInputParticulars" class="form-label">Particulars*</label>
                <input type="text" className={fieldError.particulars ? "form-control error-message" : "form-control"} id="formControlInputParticulars" name="particulars" placeholder="Enter Particulars" value={returnFormData.particulars} onChange={handleChangeReturn} required />
                {fieldError.particulars &&
                        <span className="error-message">
                            {fieldError.particulars}
                        </span>
                    }
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                
                <label for="formControlInputAsPerReturnFiled" class="form-label">As per Return Filed*</label>
                <input type="text" className={fieldError.asPerReturnFiled ? "form-control error-message" : "form-control"} id="formControlInputAsPerReturnFiled" name="asPerReturnFiled" placeholder="Enter As per Return Filed" value={returnFormData.asPerReturnFiled} onChange={handleChangeReturn} required />
                {fieldError.asPerReturnFiled &&
                    <span className="error-message">
                        {fieldError.asPerReturnFiled}
                    </span>
                }
              </div>
              <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                
                <label for="formControlInputAsPerMunicipality" class="form-label">As per Municipality*</label>
                <input type="text" className={fieldError.asPerMunicipality ? "form-control error-message" : "form-control"} id="formControlInputAsPerMunicipality" name="asPerMunicipality" placeholder="Enter As per Municipality" value={returnFormData.asPerMunicipality} onChange={handleChangeReturn} required />
                {fieldError.asPerMunicipality &&
                    <span className="error-message">
                        {fieldError.asPerMunicipality}
                    </span>
                }
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                
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
                    
                    <label for="formControlInputResolutionNo" class="form-label">Resolution No.*</label>
                    <input type="text" className={fieldError.resolutionNo ? "form-control error-message" : "form-control"} id="formControlInputResolutionNo" name="resolutionNo" placeholder="Enter Resolution No." value={returnTimeFormData.resolutionNo} onChange={handleChangeTimeReturn} required />
                    {fieldError.resolutionNo &&
                        <span className="error-message">
                            {fieldError.resolutionNo}
                        </span>
                    }
                </div>
                <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                    
                    <label for="formControlInputResolutionDate" class="form-label">Resolution Date*</label>
                    <input type="date" className={fieldError.resolutionDate ? "form-control error-message" : "form-control"} id="formControlInputResolutionDate" name="resolutionDate" placeholder="Enter Resolution Date" value={returnTimeFormData.resolutionDate} onChange={handleChangeTimeReturn} required />
                    {fieldError.resolutionDate &&
                        <span className="error-message">
                            {fieldError.resolutionDate}
                        </span>
                    }
                </div>
              <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                
                <label for="formControlInputReturnDate" class="form-label">Date*</label>
                <input type="date" className={fieldError.date ? "form-control error-message" : "form-control"} id="formControlInputReturnDate" name="date" placeholder="Enter Date" value={returnTimeFormData.date} onChange={handleChangeTimeReturn} required />
                {fieldError.date &&
                    <span className="error-message">
                        {fieldError.date}
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

export default NoticeForReassessment;
