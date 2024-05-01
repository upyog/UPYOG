import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { CardLabel, DatePicker, Dropdown, Header, Modal, TextInput } from "@egovernments/digit-ui-react-components";


const NoticeForAssesment = (props) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { t } = useTranslation();
  const [financialYears, setFinancialYears] = useState([]);
  const [selectedFinancialYear, setSelectedFinancialYear] = useState(null);
  const [submissionDate, setSubmissionDate] = useState();

  const noticeList = [
    { code: '1', name: 'Notice for rectification of mistakes in a Defective Return' },
    { code: '2', name: 'Notice for Assessment' }
  ]
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
  const [particulars, setParticulars] = useState();
  const [asPerReturnFiled, setAsPerReturnFiled] = useState();
  const [asPerMunicipality, setAsPerMunicipality] = useState();
  const [remarks, setRemarks] = useState();
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
  const onSaveAndAdd = ()=>{
    let obj = {
      particulars: particulars,
      asPerReturnFiled: asPerReturnFiled,
      asPerMunicipality: asPerMunicipality,
      remarks: remarks
    };
    let list = tableList || [];
    list.push(obj)
    setTableList(list);
    setParticulars('');
    setAsPerReturnFiled('');
    setAsPerMunicipality('');
    setRemarks('');
    // setShowModal(false);
  };
  const onSave = ()=>{
    let obj = {
      particulars: particulars,
      asPerReturnFiled: asPerReturnFiled,
      asPerMunicipality: asPerMunicipality,
      remarks: remarks
    };
    let list = tableList || [];
    list.push(obj)
    setTableList(list);
    setParticulars('');
    setAsPerReturnFiled('');
    setAsPerMunicipality('');
    setRemarks('');
    setShowModal(false);
  };
  const printDiv = (divId)=> {
    var printContents = document.getElementById(divId).innerHTML;
    var originalContents = document.body.innerHTML;

    document.body.innerHTML = printContents;

    window.print();

    document.body.innerHTML = originalContents;
  }
  const onSubmit = () => {

  }
  const onCancelNotice = () => {
    
  }
  return (
    <div>
      {<Header>{t("Notice For Assessment")}</Header>}
      <div  >
        <div className="row">
          <form>
            <div id="form-print">
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
                  <div className="col-sm-4" style={{ width: '48%', display: 'inline-block', position: 'relative', top: '0px' }}>
                    <CardLabel>{`${t("Assessment Year")}`}</CardLabel>
                    <Dropdown isMandatory optionCardStyles={{ zIndex: 111111 }} selected={selectedFinancialYear} optionKey="name" option={financialYears} select={setSelectedFinancialYear} t={t} />

                  </div>
                </div>
                <hr />
                <div style={{ marginTop: '20px' }}>
                  <p><span style={{ fontWeight: 500 }}>Sub: Notice under Rule 33/ Rule 34 of Manipur Municipalities (Property Tax) Rules, 2019 </span>
                    <ul style={{ marginTop: '10px' }}>
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        This is reference to the Property Tax Return field under Rule 17/Rule 18/Rule 19/ No Return filed under Rule 17/ Rectification of Mistakes under Rule 38
                      </li>
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', display: 'inline-flex' }}>
                          The following information in the return appears to be incorrect / No return has been filed under Rule 17
                        </div>
                        
                        <div style={{ width: '40%', display: 'inline' }}>
                          <button onClick={(e) => onAddTabData(e)} className="submit-bar"
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
                              <th>Particulars</th>
                              <th>As per Return Filed</th>
                              <th>As per Municipality</th>
                              <th>Remarks</th>
                            </tr>
                            {tableList.map((e)=>{
                              console.log("tableList===",tableList);
                              return (<tr>
                                <td>{e?.particulars}</td>
                                <td>{e.asPerReturnFiled}</td>
                                <td>{e.asPerMunicipality}</td>
                                <td>{e.remarks}</td>
                              </tr>)
                            })}
                          </table>
                        </div>
                      </li>}
                      <li style={{ listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        <div style={{ width: '60%', display: 'inline-flex' }}>
                          If therefore purpose to modify the Annual Property Value (APV) and the property tax on the basis of the information available with the municipality. In case, you disagree with the assessment and the process increase, you may case with all available records either in person or through and authorized representative on __________________ at ________________ in the chamber of the undersigned.
                        </div>
                        <div style={{ width: '40%', display: 'inline' }}>
                          <button onClick={(e) => onEditDate(e)} className="submit-bar"
                            style={{
                              color: 'white',
                              float: 'right',
                              width: '10%'
                            }}
                          >
                            {t("Edit")}
                          </button>
                        </div>
                      </li>
                      <li style={{ width: '60%', listStyle: 'auto', marginLeft: '16px', padding: '6px' }}>
                        In case you fail to appear on the appointed date and time or otherwise explain why the APV and tax should not be assessed as above, the assessment will be frames under Rule 33/ Rule 34/ Rule 38 on the basis of the information available with the municipality as indicated above.
                      </li>
                    </ul>
                  </p>
                </div>
              </div>
              <div className="card" style={{ maxWidth: '100%' }}>
                <div style={{display: 'inline-flex'}}>
                  <div style={{width: '100%'}}>
                      <span>Date</span>
                      <div>{new Date().toLocaleDateString()}</div>
                  </div>
                  <div>
                    <span>Place</span>
                    <div>Manipur</div>
                  </div>
                </div>
              </div>
            </div>
            
            <div className="card" style={{ maxWidth: '100%' }}>
              <div style={{display: 'inline-flex'}}>
                <div style={{ width: '100%', display: 'inline' }}>
                  <button onClick={() => printDiv('form-print')} className="submit-bar"
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
          headerBarMain={<Heading label={t('Return appears to be incorrect')} />}
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
          <div className="">
              <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                <CardLabel>{`${t("Particulars")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'particulars'}
                  name={'particulars'}
                  value={particulars}
                  onChange={(e) => onChangeParticulars(e)}
                  isMandatory={true}
                  disable={false}
                />
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                <CardLabel>{`${t("As per Return Filed")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'asPerReturnFiled'}
                  name={'asPerReturnFiled'}
                  value={asPerReturnFiled}
                  onChange={(e) => onChangeAsPerReturnFiled(e)}
                  isMandatory={true}
                  disable={false}
                />
              </div>

            </div>
            <div className="">
              <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                <CardLabel>{`${t("As per Municipality")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'asPerMunicipality'}
                  name={'asPerMunicipality'}
                  value={asPerMunicipality}
                  onChange={(e) => onChangeMunicipality(e)}
                  isMandatory={true}
                  disable={false}
                />
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                <CardLabel>{`${t("Remarks")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'remarks'}
                  name={'remarks'}
                  value={remarks}
                  onChange={(e) => onChangeRemarks(e)}
                  isMandatory={true}
                  disable={false}
                />
              </div>

            </div>
            <div className="footer" style={{height: '50px', background: '#ebebeb'}}>
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
          <div className="row">
              <div className="col-sm-4" style={{ width: '48%', marginRight: '10px', display: 'inline-block' }}>
                <CardLabel>{`${t("Date")}`}</CardLabel>
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
                />
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block' }}>
                <CardLabel>{`${t("Time")}`}</CardLabel>
                <TextInput
                  style={{ background: "#FAFAFA" }}
                  key={'time'}
                  name={'time'}
                  value={time}
                  onChange={(e) => setTime(e.target.value)}
                  isMandatory={true}
                  disable={false}
                  type={'number'}
                />
              </div>
              <div className="col-sm-4" style={{ width: '48%', display: 'inline-block', position: 'relative', top: '-35px' }}>
                <CardLabel>{`${t("")}`}</CardLabel>
                <Dropdown isMandatory optionCardStyles={{ zIndex: 111111 }} selected={timeMeridian} optionKey="name" option={[{name: 'AM', value: 'AM'},{name: 'PM', value: 'PM'}]} select={setTimeMeridian} t={t} />

              </div>

            </div>
            
            <div className="footer" style={{height: '50px', background: '#ebebeb'}}>
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

    </div>
  );
};

export default NoticeForAssesment;
