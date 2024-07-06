import { CardLabel, CheckBox, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useState } from 'react';
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useHistory, useLocation } from "react-router-dom";
import Timeline from "../components/bmcTimeline";
import RadioButton from "../components/radiobutton";
import Title from "../components/title";
import dropdownOptions from "./dropdownOptions.json";

const ApplicationDetail = () => ({
  rationCard: "",
  machineName: "",
  income: "",
  transgenderId: "",
  udid: "",
  disabilityPercentage: "",
  educationQualification: "",
  disabilityType: "",
  wardName: "",
  subWardName: "",
  religion: "",
  casteCategory: "",
  accountNumber: "",
  ifscCode: "",
  bankName: "",
  branchName: "",
  micrCode: "",
  employee: "",
  service: "",
  business: "",
  domicileofMumbai: "",
  incomeCer: "",
  voterId: "",
  panCard: "",
  bankPassbook: "",
  selfDeclarationMessage: "",
});

const ApplicationDetailFull = (_props) => {
  const { index, allOwners, onSelect, formData, formState, setError, clearErrors, config } = _props;
  const {
    control,
    handleSubmit,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    reset,
  } = useForm();

  const { t } = useTranslation();
  const location = useLocation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const headerLocale = Digit.Utils.locale.getTransformedLocale(tenantId);


  const { selectedScheme, selectedRadio } = location.state || {};
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const [owner, setOwner] = useState(formData?.owner || [ApplicationDetail()]);
  const [bankPassbook, setBankPassBook] = useState("");
  const [domicileofMumbai, setDomicileofMumbai] = useState("");
  const [incomeCer, setIncomeCer] = useState("");
  const [voterId, setVoterId] = useState("");
  const [panCard, setPanCard] = useState("");
  const [business, setBusiness] = useState("");
  const [checkbox1, setCheckbox1] = useState(false);
  const [checkbox2, setCheckbox2] = useState(false);
  const [isCheckedShow, setIsCheckedShow] = useState(false);
  const [rangeValue, setRangeValue] = useState(1);

  const handleChange = (e) => {
    setRangeValue(parseInt(e.target.value));
  };

  const history = useHistory();

  const handleCheckbox = () => {
    setIsCheckedShow(!isCheckedShow);
  };

  const handleCheckbox1Change = (event) => {
    setCheckbox1(event.target.checked);
  };

  const handleCheckbox2Change = (event) => {
    setCheckbox2(event.target.checked);
  };

  const isConfirmButtonEnabled = checkbox1 && checkbox2;

  const onSubmit = (data) => {
    history.push("/digit-ui/citizen/bmc/review");
    const formDataValues = { ...data, bankPassbook, domicileofMumbai, incomeCer, voterId, panCard, business };
    setOwner(formDataValues);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
        <Title text={"BMC Scheme Application Details " + selectedRadio.value + ""} />
        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-title">Scheme Details</div>
            {selectedScheme === "skill developement" && selectedRadio.value === "For Women" && (
              <React.Fragment>
                <div className="bmc-card-row">
                  <div className="bmc-col1-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Ration_Card_Type*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"rationcardtype"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Ration Card"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.rationcardtype}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col1-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Course*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"machine"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Course"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.Course}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col1-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Income*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"income"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Income"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.Income}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                </div>
              </React.Fragment>
            )}
            {selectedScheme === "skill developement" && selectedRadio.value === "For Transgender" && (
              <React.Fragment>
                <div className="bmc-card-row">
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Transgender_Id*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"transgenderid"}
                        defaultValue={owner?.transgenderId}
                        render={(props) => (
                          <TextInput
                            value={props.value}
                            isMandatory={true}
                            placeholder={"Enter the Transgender Id"}
                            autoFocus={focusIndex.index === owner?.key && focusIndex.type === "transgenderId"}
                            onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: owner.key, type: "transgenderId" });
                            }}
                            onBlur={(e) => {
                              setFocusIndex({ index: -1 });
                              props.onBlur(e);
                            }}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Ration_Card_Type*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"rationcardtype"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Ration Card"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.rationcardtype}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Course*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"course"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Course"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.Course}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Income*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"income"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Income"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.Income}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                </div>
              </React.Fragment>
            )}
            {selectedScheme === "skill developement" && selectedRadio.value === "For Divyang" && (
              <React.Fragment>
                <div className="bmc-card-row">
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_UDID_Id*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"udidid"}
                        defaultValue={owner?.udid}
                        render={(props) => (
                          <TextInput
                            value={props.value}
                            isMandatory={true}
                            placeholder={"Enter the udid ID"}
                            autoFocus={focusIndex.index === owner?.key && focusIndex.type === "udid"}
                            onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: owner.key, type: "udid" });
                            }}
                            onBlur={(e) => {
                              setFocusIndex({ index: -1 });
                              props.onBlur(e);
                            }}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Course*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"course"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Course"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.Course}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Disability_Type*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"disabilitytype"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Disability Type"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.disablityType}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                </div>
                <div className="bmc-card-row">
                  <div className="bmc-col2-card">
                    <CardLabel className="bmc-label">{t("BMC_Disability_Percentage*")}</CardLabel>
                    <div className="bmc-range-container">
                      <input
                        type="range"
                        min="1"
                        max="100"
                        className="bmc-range-slider"
                        value={rangeValue}
                        onChange={handleChange}
                        list="tickmarks"
                      />
                      <datalist id="tickmarks">
                        {Array.from({ length: 100 }, (_, i) => (
                          <option key={i} value={i + 1}></option>
                        ))}
                      </datalist>
                      <span className="range-value">Selected value:{rangeValue}</span>
                    </div>
                  </div>
                </div>
              </React.Fragment>
            )}

            {selectedScheme === "empowerment" && selectedRadio.value === "For Women1" && (
              <React.Fragment>
                <div className="bmc-card-row">
                  <div className="bmc-col1-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Ration_Card_Type*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"rationcardtype"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Ration Card"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.rationcardtype}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col1-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Machine*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"machine"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Machine"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.machine}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col1-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Income*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"income"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Income"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.Income}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                </div>
              </React.Fragment>
            )}

            {selectedScheme === "empowerment" && selectedRadio.value === "For Divyang2" && (
              <React.Fragment>
                <div className="bmc-card-row">
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_UDID_Id*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"udidid"}
                        defaultValue={owner?.udid}
                        render={(props) => (
                          <TextInput
                            value={props.value}
                            isMandatory={true}
                            placeholder={"Enter the udid ID"}
                            autoFocus={focusIndex.index === owner?.key && focusIndex.type === "udid"}
                            onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: owner.key, type: "udid" });
                            }}
                            onBlur={(e) => {
                              setFocusIndex({ index: -1 });
                              props.onBlur(e);
                            }}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Machine*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"machine"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Machine"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.machine}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Disability_Type*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"disabilitytype"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Disability Type"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.disablityType}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                </div>
                <div className="bmc-card-row">
                  <div className="bmc-col2-card">
                    <CardLabel className="bmc-label">{t("BMC_Disability_Percentage*")}</CardLabel>
                    <div className="bmc-range-container">
                      <input
                        type="range"
                        min="1"
                        max="100"
                        className="bmc-range-slider"
                        value={rangeValue}
                        onChange={handleChange}
                        list="tickmarks"
                      />
                      <datalist id="tickmarks">
                        {Array.from({ length: 100 }, (_, i) => (
                          <option key={i} value={i + 1}></option>
                        ))}
                      </datalist>
                      <span className="range-value">Selected value:{rangeValue}</span>
                    </div>
                  </div>
                </div>
              </React.Fragment>
            )}

            {selectedScheme === "pension" && (
              <React.Fragment>
                <div className="bmc-card-row">
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_UDID_Id*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"udidid"}
                        defaultValue={owner?.udid}
                        render={(props) => (
                          <TextInput
                            value={props.value}
                            isMandatory={true}
                            placeholder={"Enter the udid ID"}
                            autoFocus={focusIndex.index === owner?.key && focusIndex.type === "udid"}
                            onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: owner.key, type: "udid" });
                            }}
                            onBlur={(e) => {
                              setFocusIndex({ index: -1 });
                              props.onBlur(e);
                            }}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Pension*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"pension"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Pension"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.pension}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                  <div className="bmc-col3-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Disability_Type*")}</CardLabel>
                      <Controller
                        control={control}
                        name={"disabilitytype"}
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <Dropdown
                            placeholder="Select the Disability Type"
                            selected={props.value}
                            select={(value) => {
                              props.onChange(value);
                            }}
                            onBlur={props.onBlur}
                            option={dropdownOptions.disablityType}
                            optionKey="value"
                            t={t}
                            isMandatory={true}
                          />
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                </div>
                <div className="bmc-card-row">
                  <div className="bmc-col2-card">
                    <CardLabel className="bmc-label">{t("BMC_Disability_Percentage*")}</CardLabel>
                    <div className="bmc-range-container">
                      <input
                        type="range"
                        min="1"
                        max="100"
                        className="bmc-range-slider"
                        value={rangeValue}
                        onChange={handleChange}
                        list="tickmarks"
                      />
                      <datalist id="tickmarks">
                        {Array.from({ length: 100 }, (_, i) => (
                          <option key={i} value={i + 1}></option>
                        ))}
                      </datalist>
                      <span className="range-value">Selected value:{rangeValue}</span>
                    </div>
                  </div>
                </div>
              </React.Fragment>
            )}
          </div>
        </div>
        
        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-title">Bank Details</div>
            <div className="bmc-col3-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Bank_Name*")}</CardLabel>
                <Controller
                  control={control}
                  name={"bankName"}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  }}
                  render={(props) => (
                    <Dropdown
                      placeholder={"Select Bank"}
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                      }}
                      onBlur={props.onBlur}
                      option={dropdownOptions.bankName}
                      optionKey="value"
                      t={t}
                      isMandatory={true}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Branch_Name*")}</CardLabel>
                <Controller
                  control={control}
                  name={"branchName"}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  }}
                  render={(props) => (
                    <Dropdown
                      placeholder={"Select Branch"}
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                      }}
                      onBlur={props.onBlur}
                      option={dropdownOptions.bankBranch}
                      optionKey="value"
                      t={t}
                      isMandatory={true}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_Account_Number*"}</CardLabel>
                <Controller
                  control={control}
                  name={"accountNumber"}
                  defaultValue={owner?.accountNumber}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      isMandatory={true}
                      placeholder={"Enter the accountNumber"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "accountNumber"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "wardNameMaster" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_IFSC_Code*"}</CardLabel>
                <Controller
                  control={control}
                  name={"ifscCode"}
                  defaultValue={owner?.ifscCode}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      isMandatory={true}
                      placeholder={"Enter the ifscCode"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "ifscCode"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "ifscCode" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
          </div>
          <div className="bmc-card-row">
            <div className="bmc-col3-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_MICR_Code*"}</CardLabel>
                <Controller
                  control={control}
                  name={"micrCode"}
                  defaultValue={owner?.micrCode}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      isMandatory={true}
                      placeholder={"Enter the micrCode"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "micrCode"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "micrCode" });
                      }}
                      onBlur={(e) => {
                        setFocusIndex({ index: -1 });
                        props.onBlur(e);
                      }}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
          </div>
        </div>
        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-title">Occupation Details</div>
            <div className="bmc-col3-card">
              <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                <CardLabel className="bmc-label">{t("BMC_Occupation*")}</CardLabel>
                <div style={{ display: "flex", flexDirection: "row" }}>
                  <CheckBox
                    label={"Employed"}
                    styles={{ height: "auto", color: "#f47738", fontSize: "18px", marginTop: "1rem", position: "none" }}
                    checked={isCheckedShow}
                    onChange={handleCheckbox}
                  />
                </div>
              </LabelFieldPair>
            </div>
            {isCheckedShow && (
              <div className="bmc-col1-card">
                <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                  <CardLabel className="bmc-label">{t("BMC_Employment Detail*")}</CardLabel>
                  <RadioButton
                    t={t}
                    optionsKey="value"
                    options={[
                      { label: "Service", value: "Service" },
                      { label: "Business", value: "Business" },
                    ]}
                    style={{ display: "flex", flexDirection: "row", gap: "12px", marginTop: "1rem" }}
                    selectedOption={business}
                    onSelect={(value) => setBusiness(value)}
                  />
                </LabelFieldPair>
              </div>
            )}
          </div>
        </div>
        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-title">Document's Details</div>
            <div className="bmc-col3-card">
              <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                <CardLabel className="bmc-label">{t("BMC_Domicile_Certificate_of_Mumbai")}</CardLabel>
                <RadioButton
                  t={t}
                  optionsKey="value"
                  options={[
                    { label: "Yes", value: "Yes" },
                    { label: "No", value: "No" },
                  ]}
                  style={{ display: "flex", flexDirection: "row", gap: "12px", marginTop: "1rem" }}
                  selectedOption={domicileofMumbai}
                  onSelect={(value) => setDomicileofMumbai(value)}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                <CardLabel className="bmc-label">{t("BMC_Income_Certificate")}</CardLabel>
                <RadioButton
                  t={t}
                  optionsKey="value"
                  options={[
                    { label: "Yes", value: "Yes" },
                    { label: "No", value: "No" },
                  ]}
                  style={{ display: "flex", flexDirection: "row", gap: "12px", marginTop: "1rem" }}
                  selectedOption={incomeCer}
                  onSelect={(value) => setIncomeCer(value)}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                <CardLabel className="bmc-label">{t("BMC_Voter_Id_Card")}</CardLabel>
                <RadioButton
                  t={t}
                  optionsKey="value"
                  options={[
                    { label: "Yes", value: "Yes" },
                    { label: "No", value: "No" },
                  ]}
                  style={{ display: "flex", flexDirection: "row", gap: "12px", marginTop: "1rem" }}
                  selectedOption={voterId}
                  onSelect={(value) => setVoterId(value)}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col3-card">
              <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                <CardLabel className="bmc-label">{t("BMC_Pan_Card")}</CardLabel>
                <RadioButton
                  t={t}
                  optionsKey="value"
                  options={[
                    { label: "Yes", value: "Yes" },
                    { label: "No", value: "No" },
                  ]}
                  style={{ display: "flex", flexDirection: "row", gap: "12px", marginTop: "1rem" }}
                  selectedOption={panCard}
                  onSelect={(value) => setPanCard(value)}
                />
              </LabelFieldPair>
            </div>
          </div>
          <div className="bmc-card-row">
            <div className="bmc-col3-card">
              <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                <CardLabel className="bmc-label">{t("BMC_Bank_Passbook")}</CardLabel>
                <RadioButton
                  t={t}
                  optionsKey="value"
                  options={[
                    { label: "Yes", value: "Yes" },
                    { label: "No", value: "No" },
                  ]}
                  style={{ display: "flex", flexDirection: "row", gap: "12px", marginTop: "1rem" }}
                  selectedOption={bankPassbook}
                  onSelect={(value) => setBankPassBook(value)}
                />
              </LabelFieldPair>
            </div>
          </div>
        </div>
        <div className="bmc-card-row">
          <div className="bmc-col-large-header">
            <div className="bmc-card-row">
              <CheckBox
                label={"Agree to pay 5% Contribution."}
                styles={{ height: "auto", color: "#f47738", fontWeight: "bold", fontSize: "18px", marginLeft: "3rem", float: "left" }}
                value={checkbox2}
                onChange={handleCheckbox2Change}
              />
            </div>
            <div className="bmc-card-row">
              <CheckBox
                label={"To the best of my knowledge, the information provided above is correct.."}
                styles={{ height: "auto", color: "#f47738", fontWeight: "bold", fontSize: "18px", marginLeft: "3rem", float: "left" }}
                value={checkbox1}
                onChange={handleCheckbox1Change}
              />
            </div>
          </div>
          <div className="bmc-col-small-header">
            <div style={{ textAlign: "end", paddingBottom: "1rem" }}>
              {/* <Link to="/digit-ui/citizen/bmc/review" style={{ textDecoration: "none" }}> */}
              <button
                type="button"
                onClick={handleSubmit(onSubmit)}
                className="bmc-card-button"
                style={{
                  backgroundColor: isConfirmButtonEnabled ? "#F47738" : "gray",
                  borderBottom: "3px solid black",
                  outline: "none",
                  marginRight: "11rem",
                }}
                disabled={!isConfirmButtonEnabled}
              >
                {t("BMC_Confirm")}
              </button>
              {/* </Link> */}
            </div>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default ApplicationDetailFull;
