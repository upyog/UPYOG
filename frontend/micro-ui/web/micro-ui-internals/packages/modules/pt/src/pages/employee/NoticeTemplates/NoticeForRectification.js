import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, DatePicker, Dropdown, Header, Modal, TextInput } from "@upyog/digit-ui-react-components";
import { printNotice } from "../../../utils";


const NoticeForRectification = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { t } = useTranslation();
  const [financialYears, setFinancialYears] = useState([]);
  const [selectedFinancialYear, setSelectedFinancialYear] = useState(props?.noticeData && props?.noticeData.assessmentYear? {code: props?.noticeData.assessmentYear, name: props?.noticeData.assessmentYear} : null);
  const [dateOfAnnualRet, setSubmissionDate] = useState(props?.noticeData?.dateOfAnnualRet ? props?.noticeData?.dateOfAnnualRet : null);

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
  const [returnFormData, setReturnFormData] = useState({
    comment: null
  });
  const [returnTimeFormData, setReturnTimeFormData] = useState({
    entryTime: props?.noticeData?.entryTime ? props?.noticeData?.entryTime : null,
    entryDate: props?.noticeData?.entryDate ? props?.noticeData?.entryDate : null
  });
  // const [particulars, setParticulars] = useState();
  // const [asPerReturnFiled, setAsPerReturnFiled] = useState();
  // const [asPerMunicipality, setAsPerMunicipality] = useState();
  // const [remarks, setRemarks] = useState();
  const [tableList, setTableList] = useState(props?.noticeData?.NoticeComment?.length>0 ? props?.noticeData?.NoticeComment : []);

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
          comment: null
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
          comment: null
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
    printNotice(e,divId,t(props?.isCitizen ? props?.noticeData?.tenantId : tenantId));
    // var printContent = document.getElementById(divId);
    // var WinPrint = window.open('', '', 'width=900,height=650');
    // WinPrint.document.write(printContent.innerHTML);
    // WinPrint.document.close();
    // WinPrint.focus();
    // WinPrint.print();
    // WinPrint.close();
    // var printContents = document.getElementById(divId).innerHTML;
    // var originalContents = document.body.innerHTML;

    // document.body.innerHTML = printContents;

    // window.print();

    // document.body.innerHTML = originalContents;
    // return false;
    
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
      "noticeType": "Notice for rectification of mistakes in a Defective Return",      
      "tenantId": tenantId,      
      "channel": "CITIZEN",
      "noticeComment": tableList,
      entryDate: returnTimeFormData?.entryDate,
      entryTime: returnTimeFormData?.entryTime
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
            {<Header>{t("Notice For rectification of mistakes in a Defective Return")}</Header>}
              <div className="row card" style={{...citizenStyleMaxWidth}}>
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
                        setSubmissionDate(d.target.value);
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
                    disable={props?.isCitizen ? true : false} />

                  </div>
                </div>
                <hr />
                <div style={{ marginTop: '20px' }}>
                  <p><span style={{ fontWeight: 600 }}>Sub: Notice under Rule 32 of Manipur Municipalities (Property Tax) Rules, 2019 </span>
                    <ul style={{ marginTop: '10px' }} className="notice-txt">
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', display: 'inline-flex', ...citizenStyle }}>
                            Following mistakes apparent from record has been noted in the return filed under Rule 17/ Rule 18/ Rule 19 :
                        </div>
                        {!props?.isCitizen && <div style={{ width: '40%', display: 'inline' }}>
                          <button id="printPageButton" onClick={(e) => onAddTabData(e)} className="submit-bar"
                            style={{
                              color: 'white',
                              float: 'right',
                              width: '10%'
                            }}
                          >
                            + {t("Add")}
                          </button>
                        </div>}
                      </li>
                      {tableList && tableList.length>0 && <li style={{ marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '100%' }}>
                          <table style={{ width: '100%', border: '1px solid #b7b7b7'}}>
                            <tr style={{background: '#eaeaea', lineHeight: '35px'}}>
                              <th style={{paddingLeft: "10px"}}>Sr. No.</th>
                              <th>Comments</th>
                            </tr>
                            {tableList.map((e, indx)=>{
                              return (<tr>
                                <td style={{paddingLeft: "10px"}}>{indx+1}</td>
                                <td>{e?.comment}</td>
                              </tr>)
                            })}
                          </table>
                        </div>
                      </li>}
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', display: 'inline-flex', ...citizenStyle }}>
                          I therefor propose to pass an order under Sub-Rule(4) of Rule 32 considering the return to be an "invalid return" on the basis of thr above.
                        </div>
                      </li>
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', display: 'inline-flex', ...citizenStyle }}>
                          Before passing the order, you are given an opportunity to fill your objections, if any with supporting evidance.
                        </div>
                      </li>
                      
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', ...citizenStyle }}>
                          You may present your case with all available records either in person or through an authorized representative on {returnTimeFormData?.entryDate && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.entryDate}</span>}{!returnTimeFormData?.entryDate && <span>__________________</span>} at {returnTimeFormData?.entryTime && <span style={{fontWeight: "600", textDecoration: "underline"}}>{returnTimeFormData?.entryTime}</span>} {!returnTimeFormData?.entryTime && <span>________________ </span>}in the chamber of the undersigned.
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
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px', ...citizenStyle }}>
                        In case you fail to appear on the appointed date and time or otherwise explain why the proposed rectification should not be done, the order under Rule 32 shall be passed on above lines.
                      </li>
                    </ul>
                  </p>
                </div>
              </div>
              <div className="card" style={{...citizenStyleMaxWidth}}>
                <div className="row">
                    <div className="" style={{display: "inline-block", width: "87%", paddingLeft: "15px"}}>
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
            
            <div className="card" style={{...citizenStyleMaxWidth}}>
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
                  <button onClick={ onSubmit} className="submit-bar"
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
          headerBarMain={<Heading label={t('Mistakes apparent form record')} />}
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
              <div className="col-sm-12" style={{ width: '100%', marginRight: '10px', display: 'inline-block' }}>
                
                <label for="formControlInputComment" class="form-label">Comments*</label>
                <input type="text" className={fieldError.comment ? "form-control error-message" : "form-control"} id="formControlInputComment" name="comment" placeholder="Enter Comments" value={returnFormData.comment} onChange={handleChangeReturn} required />
                {fieldError.comment &&
                        <span className="error-message">
                            {fieldError.comment}
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
                {/* <div style={{ width: '40%', display: 'inline' }}>
                  <button onClick={() => onSaveAndAdd()} className="submit-bar"
                    style={{
                      color: 'white',
                      float: 'right',
                      width: '30%'
                    }}
                  >
                    {t("Save & Add")}
                  </button>
                </div> */}
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
                <input type="date" className={fieldError.entryDate ? "form-control error-message" : "form-control"} id="formControlInputReturnDate" name="entryDate" placeholder="Enter Date" value={returnTimeFormData.entryDate} onChange={handleChangeTimeReturn} required />
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
                  disable={false}
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

    </div>
  );
};

export default NoticeForRectification;
