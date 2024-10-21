import { CardLabel, FormStep, RadioButtons, TextInput, CheckBox, LinkButton, MobileNumber } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState,useEffect } from "react";
import Timeline from "../components/Timeline";


const SVApplicantDetails = ({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  const user = Digit.UserService.getUser().info;
  const [vendorName, setvendorName] = useState(formData?.owner?.units?.vendorName||formData?.owner?.vendorName||"");
  const [vendorDateOfBirth, setvendorDateOfBirth] = useState(formData?.owner?.units?.vendorDateOfBirth ||formData?.owner?.vendorDateOfBirth || "");
  const [gender, setgender] = useState(formData?.owner?.units?.gender||formData?.owner?.gender || "");
  const [fatherName, setfatherName] = useState(formData?.owner?.units?.fatherName ||formData?.owner?.fatherName|| "");
  const [spouseName, setspouseName] = useState(formData?.owner?.units?.spouseName || formData?.owner?.spouseName|| "");
  const [spousedependent, setspousedependent ] = useState(false)
  const [mobileNumber, setmobileNumber] = useState(formData?.owner?.units?.mobileNumber||formData?.owner?.mobileNumber||"");
  const [spouseDateBirth, setspouseDateBirth] = useState(formData?.owner?.units?.spouseDateBirth ||formData?.owner?.spouseDateBirth|| "");
  const [dependentName, setdependentName] = useState(formData?.owner?.units?.dependentName ||formData?.owner?.dependentName|| "");
  const [dependentDateBirth, setdependentDateBirth] = useState(formData?.owner?.units?.dependentDateBirth ||formData?.owner?.dependentDateBirth|| "");
  const [dependentGender, setdependentGender] = useState(formData?.owner?.units?.dependentGender ||formData?.owner?.dependentGender|| "");
  const [email, setemail] = useState(formData?.owner?.units?.email ||formData?.owner?.email|| "");
  const [tradeNumber, settradeNumber] = useState(formData?.owner?.units?.tradeNumber ||formData?.owner?.tradeNumber|| "");
  const [error, setError]=useState(null);

  const [spouseDependentChecked, setSpouseDependentChecked] = useState(formData?.owner?.spouseDependentChecked || false);
  const [dependentNameChecked, setDependentNameChecked] = useState(formData?.owner?.dependentNameChecked || false);

  const inputStyles = user.type === "EMPLOYEE" ? "50%" : "86%";



  const [fields, setFeilds] = useState((formData?.owner && formData?.owner?.units) || [{ vendorName: user?.name || "", vendorDateOfBirth: "", gender: "", fatherName: "", spouseName: "", mobileNumber: user?.mobileNumber || "", spouseDateBirth: "", dependentName: "", dependentDateBirth: "", dependentGender: "", email:user?.emailId || "", tradeNumber:""}]);

  function handleAdd() {
    const values = [...fields];
    values.push({ vendorName: "", vendorDateOfBirth: "", gender: "", fatherName: "", spouseName: "", mobileNumber: "", spouseDateBirth: "", dependentName: "", dependentDateBirth: "", dependentGender: "", email:"", tradeNumber:""});
    setFeilds(values);
  }
  const validateEmail=(value)=>{
    const emailPattern=/^[a-zA-Z0-9._%+-]+@[a-z.-]+\.(com|org|in)$/;
    if(value===""){
      setError("");
    }
    else if(emailPattern.test(value)){
      setError("");  
    }
    else{
      setError(t("CORE_INVALID_EMAIL_ID_PATTERN"));  
    }
  }

  function handleRemove(index) {
    const values = [...fields];
    if (values.length != 1) {
      values.splice(index, 1);
      setFeilds(values);
    }
  }

  const common = [
    {
      code: "MALE",
      i18nKey: "MALE",
      value:"Male"
    },
    {
      code: "FEMALE",
      i18nKey: "FEMALE",
      value:"Female"
    },
    {
        code: "OTHERS",
        i18nKey: "OTHERS",
        value:"Others"
    },
  ]

  const setSpouseDependentHandler = () => {
    setSpouseDependentChecked(!spouseDependentChecked);
  };

  const setDependentNameHandler = () => {
    setDependentNameChecked(!dependentNameChecked);
  };

  function selectvendorName(i, e) {
    let units = [...fields];
    units[i].vendorName = e.target.value;
    setvendorName(e.target.value);
    setFeilds(units);
  }
  function selectvendorDateOfBirth(i, e) {
    let units = [...fields];
    units[i].vendorDateOfBirth = e.target.value;
    setvendorDateOfBirth(e.target.value);
    setFeilds(units);
  }
  function selectgender(i, value) {
    let units = [...fields];
    units[i].gender = value;
    setgender(value);
    setFeilds(units);
  }
  function selectfatherName(i, e) {
    let units = [...fields];
    units[i].fatherName = e.target.value;
    setfatherName(e.target.value);
    setFeilds(units);
  }
  
  function selectspouseName(i, e) {
    let units = [...fields];
    units[i].spouseName = e.target.value;
    setspouseName(e.target.value);
    setFeilds(units);
  }

  function selectmobileNumber(i, e) {
    let units = [...fields];
    units[i].mobileNumber = e;
    setmobileNumber(e);
    setFeilds(units);
  }

  function selectspouseDateBirth(i, e) {
    let units = [...fields];
    units[i].spouseDateBirth = e.target.value;
    setspouseDateBirth(e.target.value);
    setFeilds(units);
  }

  function selectdependentDateBirth(i, e) {
    let units = [...fields];
    units[i].dependentDateBirth = e.target.value;
    setdependentDateBirth(e.target.value);
    setFeilds(units);
    }   

    function selectdependentName(i, e) {
        let units = [...fields];
        units[i].dependentName = e.target.value;
        setdependentName(e.target.value);
        setFeilds(units);
    }

  function selectdependentGender(i, value) {
    let units = [...fields];
    units[i].dependentGender = value;
    setdependentGender(value);
    setFeilds(units);
  }

  function selectemail(i, e) {
    let units = [...fields];
    units[i].email = e.target.value;
    const value = e.target.value
    setemail(e.target.value);
    validateEmail(value);
    setFeilds(units);
    }
    useEffect(() => {
        if(email){
          validateEmail(email);
        } 
      }, [email])
  
    function selecttradeNumber(i, e) {
        let units = [...fields];
        units[i].tradeNumber = e.target.value;
        settradeNumber(e.target.value);
        setFeilds(units);
    }
  const goNext = () => {
      let ownerDetails = formData.owner || {};
      let ownerStep = { 
        ...ownerDetails, 
        units: fields,
        spouseDependentChecked,
        dependentNameChecked
      };
      onSelect(config.key, { ...formData[config.key], ...ownerStep }, false);
    
  };

  const onSkip = () => onSelect();
  return (
    <React.Fragment>
      {<Timeline currentStep={1}/>}
        <FormStep
          config={config}
          onSelect={goNext}
          onSkip={onSkip}
          t={t}
          isDisabled={!fields[0].vendorName || !fields[0].vendorDateOfBirth || !fields[0].gender || !fields[0].fatherName || (spouseDependentChecked && !fields[0].spouseName) || (dependentNameChecked && !fields[0].dependentName)}
        >
          {fields.map((field, index) => {
            return (
              <div key={`${field}-${index}`}>
                <div
                  style={{
                    border: "solid",
                    borderRadius: "5px",
                    padding: "10px",
                    paddingTop: "20px",
                    marginTop: "10px",
                    borderColor: "#f3f3f3",
                    background: "#FAFAFA",
                    width:inputStyles,
                  }}
                >
                <CardLabel>{`${t("SV_VENDOR_NAME")}`} <span className="astericColor">*</span></CardLabel>
                <LinkButton
                label={
                    <div>
                    <span>
                        <svg
                        style={{ float: "right", position: "relative", bottom: "32px" }}
                        width="24"
                        height="24"
                        viewBox="0 0 24 24"
                        fill="none"
                        xmlns="http://www.w3.org/2000/svg"
                        >
                        <path
                            d="M1 16C1 17.1 1.9 18 3 18H11C12.1 18 13 17.1 13 16V4H1V16ZM14 1H10.5L9.5 0H4.5L3.5 1H0V3H14V1Z"
                            fill={!(fields.length == 1) ? "#494848" : "#FAFAFA"}
                        />
                        </svg>
                    </span>
                    </div>
                }
                style={{ width: "100px", display: "inline" }}
                onClick={(e) => handleRemove(index)}
                />
                <TextInput
                style={{ background: "#FAFAFA"}}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="vendorName"
                value={field?.vendorName}
                onChange={(e) => selectvendorName(index, e)}
                disable={false}
                ValidationRequired={true}
                {...(validation = {
                    isRequired: true,
                    pattern: "^[a-zA-Z ]+$",
                    type: "text",
                    title: t("SV_ENTER_CORRECT_NAME"),
                })}
                />
                <CardLabel>{`${t("SV_REGISTERED_MOB_NUMBER")}`} <span className="astericColor">*</span></CardLabel>
                <MobileNumber
                style={{ background: "#FAFAFA" }}
                value={field?.mobileNumber}
                name="mobileNumber"
                onChange={(e) => selectmobileNumber(index, e)}
                {...{ required: true, 
                    pattern: "[6-9]{1}[0-9]{9}", 
                    type: "tel", 
                    title: t("SV_INVALID_NUMBER") }}
                />
                <CardLabel>{`${t("SV_DATE_OF_BIRTH")}`} <span className="astericColor">*</span></CardLabel>
                <TextInput
                style={{ background: "#FAFAFA" }}
                t={t}
                type={"date"}
                isMandatory={false}
                optionKey="i18nKey"
                name="vendorDateOfBirth"
                value={field?.vendorDateOfBirth}
                onChange={(e) => selectvendorDateOfBirth(index, e)}
                disable={false}
                max={new Date().toISOString().split('T')[0]}
                rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                }}
                />
                <CardLabel>{`${t("SV_GENDER")}`} <span className="astericColor">*</span></CardLabel>
                <RadioButtons
                    t={t}
                    options={common}
                    style={{ display: "flex", flexWrap: "wrap", maxHeight:"30px" }}
                    innerStyles={{ minWidth: "24%" }}
                    optionsKey="i18nKey"
                    name={`gender-${index}`}
                    value={field?.gender}
                    selectedOption={field?.gender}
                    onSelect={(e) => selectgender(index, e)}
                    labelKey="i18nKey"
                    isPTFlow={true}
                />
                <CardLabel>{`${t("SV_FATHER_NAME")}`} <span className="astericColor">*</span></CardLabel>
                <TextInput
                style={{ background: "#FAFAFA" }}
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="fatherName"
                value={field?.fatherName}
                onChange={(e) => selectfatherName(index, e)}
                disable={false}
                ValidationRequired={true}
                {...(validation = {
                    isRequired: true,
                    pattern: "^[a-zA-Z ]+$",
                    type: "text",
                    title: t("SV_ENTER_CORRECT_NAME"),
                })}
                />
                <div>
                <CardLabel>{`${t("SV_SPOUSE_NAME")}`}</CardLabel>
                <TextInput
                style={{ background: "#FAFAFA" }}
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="spouseName"
                value={field?.spouseName}
                onChange={(e) => selectspouseName(index, e)}
                disable={false}
                ValidationRequired={spouseDependentChecked}
                {...(validation = {
                    isRequired: spouseDependentChecked,
                    pattern: "^[a-zA-Z ]+$",
                    type: "text",
                    title: t("SV_ENTER_CORRECT_NAME"),
                })}
                />
                <CheckBox
                    label={t("SV_INVOLVED_IN_VENDING")}
                    onChange={setSpouseDependentHandler}
                    checked={spouseDependentChecked}
                    styles={{ height: "auto", marginBottom:"35px" }}
                    //disabled={!agree}
                />
                </div>
                
                <CardLabel>{`${t("SV_SPOUSE_DATE_OF_BIRTH")}`}</CardLabel>
                <TextInput
                style={{ background: "#FAFAFA" }}
                t={t}
                type={"date"}
                isMandatory={false}
                optionKey="i18nKey"
                name="spouseDateBirth"
                value={field?.spouseDateBirth}
                onChange={(e) => selectspouseDateBirth(index, e)}
                disable={false}
                max={new Date().toISOString().split('T')[0]}
                rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                }}
                />
                <div>
                <CardLabel>{`${t("SV_DEPENDENT_NAME")}`}</CardLabel>
                <TextInput
                style={{ background: "#FAFAFA" }}
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="dependentName"
                value={field?.dependentName}
                onChange={(e) => selectdependentName(index, e)}
                disable={false}
                ValidationRequired={dependentNameChecked}
                {...(validation = {
                    isRequired: dependentNameChecked,
                    pattern: "^[a-zA-Z ]+$",
                    type: "text",
                    title: t("SV_ENTER_CORRECT_NAME"),
                })}
                />
                <CheckBox
                    label={t("SV_INVOLVED_IN_VENDING")}
                    onChange={setDependentNameHandler}
                    checked={dependentNameChecked}
                    styles={{ height: "auto", marginBottom:"35px" }}
                    //disabled={!agree}
                />
                </div>
                <CardLabel>{`${t("SV_DEPENDENT_DATE_OF_BIRTH")}`}</CardLabel>
                <TextInput
                style={{ background: "#FAFAFA" }}
                t={t}
                type={"date"}
                isMandatory={false}
                optionKey="i18nKey"
                name="dependentDateBirth"
                value={field?.dependentDateBirth}
                onChange={(e) => selectdependentDateBirth(index, e)}
                disable={false}
                max={new Date().toISOString().split('T')[0]}
                rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                }}
                />
                <CardLabel>{`${t("SV_DEPENDENT_GENDER")}`}</CardLabel>
                <RadioButtons
                    t={t}
                    options={common}
                    style={{ display: "flex", flexWrap: "wrap", maxHeight:"30px" }}
                    innerStyles={{ minWidth: "24%" }}
                    optionsKey="i18nKey"
                    name={`dependentGender-${index}`}
                    value={field?.dependentGender}
                    selectedOption={field?.dependentGender}
                    onSelect={(e) => selectdependentGender(index, e)}
                    labelKey="i18nKey"
                    isPTFlow={true}
                />
                <CardLabel>{`${t("SV_EMAIL")}`}</CardLabel>
                <TextInput
                style={{ background: "#FAFAFA" }}
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="email"
                value={field?.email}
                onChange={(e) => selectemail(index, e)}
                disable={false}
                />
                <CardLabel>{`${t("SV_TRADE_NUMBER")}`}</CardLabel>
                <TextInput
                style={{ background: "#FAFAFA" }}
                t={t}
                type={"text"}
                isMandatory={false}
                optionKey="i18nKey"
                name="tradeNumber"
                value={field?.tradeNumber}
                onChange={(e) => selecttradeNumber(index, e)}
                disable={false}
                ValidationRequired={true}
                {...(validation = {
                    isRequired: false,
                    pattern: "^[a-zA-Z0-9 ]+$",
                    type: "text",
                    title: t("SV_ENTER_CORRECT_NAME"),
                })}
                />

            <div className="astericColor"  style={{ display: "flex", paddingBottom: "15px", color: "#FF8C00", marginTop:"10px" }}>
            <button type="button" style={{ paddingTop: "10px" }} onClick={() => handleAdd()}>
              {`${t("SV_ADD_DEPENDENT")}`}
            </button>
            </div>
                </div>
              </div>
            );
          })}
          
          
        </FormStep>
      
    </React.Fragment>
  );
};
export default SVApplicantDetails;
