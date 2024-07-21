import { CardLabel, CheckBox, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useHistory, useLocation } from "react-router-dom";
import Timeline from "../components/bmcTimeline";
import DisabilityCard from "../components/DisabilityCard";
import MultiSelect from "../components/multidropdown";
import QualificationCard from "../components/QualificationCard";
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

  const { schemeHead, selectedScheme, selectedRadio } = location.state || {};
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const [owner, setOwner] = useState(formData?.owner || [ApplicationDetail()]);
  const [bankPassbook, setBankPassBook] = useState("");
  const [domicileofMumbai, setDomicileofMumbai] = useState("");
  const [incomeCer, setIncomeCer] = useState("");
  const [voterId, setVoterId] = useState("");
  const [panCard, setPanCard] = useState("");
  const [service, setService] = useState({ value: "Service", label: "Service" });
  const [checkbox1, setCheckbox1] = useState(false);
  const [checkbox2, setCheckbox2] = useState(false);
  const [isCheckedShow, setIsCheckedShow] = useState(false);
  const [documents, setDocuments] = useState([]);
  const [selectedDocuments, setSelectedDocuments] = useState([]);
  const [qualifications, setQualifications] = useState([]);

  const history = useHistory();

  const processCommonData = (data, headerLocale) => {
    return (
      data?.CommonDetails?.map((item) => ({
        code: item.id,
        name: item.name,
        i18nKey: `${headerLocale}_ADMIN_${item.name}`,
      })) || []
    );
  };

  const qualificationFunction = (data) => {
    const qualificationData = processCommonData(data, headerLocale);
    setQualifications(qualificationData);
    return { qualificationData };
  };
  const getQualification = { CommonSearchCriteria: { Option: "qualification" } };
  Digit.Hooks.bmc.useCommonGet(getQualification, { select: qualificationFunction });

  const documentFunction = (data) => {
    const documentsData = processCommonData(data, headerLocale);
    setDocuments(documentsData);
    return { documentsData };
  };
  const getDocuments = { CommonSearchCriteria: { Option: "document" } };
  Digit.Hooks.bmc.useCommonGet(getDocuments, { select: documentFunction });


  const handleSelect = (e, option) => {
    if (selectedDocuments.some((doc) => doc.code === option.code)) {
      setSelectedDocuments(selectedDocuments.filter((doc) => doc.code !== option.code));
    } else {
      setSelectedDocuments([...selectedDocuments, option]);
    }
  };

  const handleClearSelection = () => {
    setSelectedDocuments([]);
  };

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
  const handleQualificationsUpdate = (updatedQualifications) => {
    //setQualifications(updatedQualifications);
    console.log(updatedQualifications);
  };
  const onSubmit = (data) => {
    history.push("/digit-ui/citizen/bmc/review");
    const formDataValues = { ...data, bankPassbook, domicileofMumbai, incomeCer, voterId, panCard, business };
    setOwner(formDataValues);
  };
  const handleDisabilityUpdate = (updatedDisability) => {
    //setQualifications(updatedQualifications);
    console.log(updatedDisability);
  };
  return (
    <React.Fragment>
      <div className="bmc-card-full">
        {window.location.href.includes("/citizen") ? <Timeline currentStep={4} /> : null}
        <Title text={`Application for ${schemeHead} and ${selectedRadio.value}`} />
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
                </div>
              </React.Fragment>
            )}

            {selectedScheme === "empowerment" && selectedRadio.value === "for Women" && (
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

            {selectedScheme === "empowerment" && selectedRadio.value === "For divyang" && (
              <React.Fragment>
                <div className="bmc-card-row">
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
                </div>
              </React.Fragment>
            )}

            {selectedScheme === "pension" && (
              <React.Fragment>
                <div className="bmc-card-row">
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
                </div>
              </React.Fragment>
            )}
          </div>
        </div>
       

        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-title">Occupation Details</div>
            <div className="bmc-col3-card">
              <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                <CardLabel className="bmc-label">{t("BMC_Occupation*")}</CardLabel>
                <div className="bmc-checkbox" style={{ display: "flex", flexDirection: "row" }}>
                  <CheckBox
                    label={"Employed"}
                    styles={{ height: "auto", color: "#f47738", fontSize: "18px", position: "none" }}
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
                    selectedOption={service}
                    onSelect={(value) => setService(value)}
                    checked={"Service" === service.value}
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
              <MultiSelect
                options={documents}
                optionsKey="i18nKey"
                selected={selectedDocuments}
                onSelect={handleSelect}
                defaultLabel={t("No documents selected")}
                defaultUnit={t("documents selected")}
                t={t}
                isOBPSMultiple={true}
                BlockNumber={true}
              />
            </div>
          </div>
          <div>
            <h3>{t("Selected Documents:")}</h3>
            <ul>
              {selectedDocuments.map((doc) => (
                <li key={doc.value}>{t(doc.i18nKey)}</li>
              ))}
            </ul>
          </div>
          {selectedDocuments.length > 0 && (
            <button className="bmc-card-button-cancel" onClick={handleClearSelection}>
              {t("Clear Selection")}
            </button>
          )}
        </div>
        <QualificationCard
          qualifications={qualifications}
          onUpdate={handleQualificationsUpdate}
          initialRows={dropdownOptions.education}
          AddOption={false}
          AllowRemove={false}
        ></QualificationCard>
        <DisabilityCard onUpdate={handleDisabilityUpdate} initialRows={[]} AllowEdit={false}/>
        <div className="bmc-card-row">
          <div className="bmc-col-large-header">
            <div className="bmc-checkbox">
              <div className="bmc-card-row">
                <CheckBox
                  label={"Agree to pay 5% Contribution."}
                  styles={{ height: "auto", color: "#f47738", fontWeight: "bold", fontSize: "18px", marginLeft: "1rem", float: "left" }}
                  value={checkbox2}
                  onChange={handleCheckbox2Change}
                />
              </div>
              <div className="bmc-card-row">
                <CheckBox
                  label={"To the best of my knowledge, the information provided above is correct.."}
                  styles={{ height: "auto", color: "#f47738", fontWeight: "bold", fontSize: "18px", marginLeft: "1rem", float: "left" }}
                  value={checkbox1}
                  onChange={handleCheckbox1Change}
                />
              </div>
            </div>
          </div>
          <div className="bmc-col-small-header">
            <div style={{ textAlign: "center" }}>
              {/* <Link to="/digit-ui/citizen/bmc/review" style={{ textDecoration: "none" }}> */}
              <button
                type="button"
                onClick={handleSubmit(onSubmit)}
                className="bmc-card-button"
                style={{
                  backgroundColor: isConfirmButtonEnabled ? "#F47738" : "gray",
                  borderBottom: "3px solid black",
                  outline: "none",
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
