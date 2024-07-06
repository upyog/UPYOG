import React, { useState } from "react";
import _ from "lodash";
import { useTranslation } from "react-i18next";
import { CardLabel, Dropdown, LabelFieldPair, TextArea, TextInput } from "@egovernments/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import dropdownOptions from "../../pagecomponents/dropdownOptions.json";

const createMasterDetail = () => ({
  wardNameMaster: "",
  remarkMaster: "",
  wardNameElectoralMaster: "",
  cityNameElectoralMaster: "",
  remarkElectoralMaster: "",
  religion: "",
  remarkReligion: "",
  casteCategory: "",
  remarkCasteCategoty: "",
  bankName: "",
  bankCode: "",
  remarkBank: "",
  qualification: "",
  remarkQualification: "",
  sector: "",
  remarkSector: "",
  sectorCourseName: "",
  courseCode: "",
  courseName: "",
  courseQualification: "",
  courseDuration: "",
  courseTotalCost: "",
  courseNSQFLevel: "",
  courseNQRCode: "",
});

const headers = [
  "S.No",
  "Ward Name",
  "Sub Ward Name",
  "Submitted Applications",
  "Verified Application",
  "Randomized",
  "Approved",
  "Application Name",
  "Address",
  "Gender",
  "Machine",
  "Application Status",
  "Date",
];

export const wardMasterPage = (_props) => {
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
  } = useForm();
  const { t } = useTranslation();
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  const [owner, setOwner] = useState(formData?.owner || [createMasterDetail()]);

  const onSubmit = (data) => {
    setOwner(data);
    console.log(data);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-title">Ward Master</div>
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_City_Name")}</CardLabel>
                <Controller
                  control={control}
                  name={"cityNameMaster"}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  }}
                  render={(props) => (
                    <Dropdown
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                      }}
                      onBlur={props.onBlur}
                      option={dropdownOptions.cityName}
                      optionKey="value"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_Ward_Name"}</CardLabel>
                <Controller
                  control={control}
                  name={"wardNameMaster"}
                  defaultValue={owner?.wardNameMaster}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the wardNameMaster"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "wardNameMaster"}
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
          </div>
          <div className="bmc-card-row">
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <Controller
                  control={control}
                  name={"remarkMaster"}
                  defaultValue={owner?.remarkMaster}
                  render={(props) => (
                    <TextArea
                      value={props.value}
                      placeholder={"Enter the remarkMaster"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "remarkMaster"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "remarkMaster" });
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
          <div className="bmc-card-row" style={{ textAlign: "center" }}>
            <button
              onClick={handleSubmit(onSubmit)}
              type="button"
              className="bmc-card-button"
              style={{
                borderBottom: "3px solid black",
                outline: "none",
              }}
            >
              {t("BMC_Create")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const electoralMasterPage = (_props) => {
  const { index, allOwners, onSelect, formData, formState, setError, clearErrors, config } = _props;
  const {
    control,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    handleSubmit,
  } = useForm();
  const { t } = useTranslation();
  const [owner, setOwner] = useState(formData?.owners || [createMasterDetail()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  const onSubmit = (data) => {
    setOwner(data);
    console.log(data);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-card-row">
            <div className="bmc-title">Electoral Ward Master</div>
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_Ward_Name"}</CardLabel>
                <Controller
                  control={control}
                  name={"wardNameElectoralMaster"}
                  defaultValue={owner?.wardNameElectoralMaster}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the wardNameElectoralMaster"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "wardNameElectoralMaster"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "wardNameElectoralMaster" });
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
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_City_Name")}</CardLabel>
                <Controller
                  control={control}
                  name={"cityNameElectoralMaster"}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  }}
                  render={(props) => (
                    <Dropdown
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                      }}
                      onBlur={props.onBlur}
                      option={dropdownOptions.cityName}
                      optionKey="value"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
          </div>
          <div className="bmc-card-row">
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <Controller
                  control={control}
                  name={"remarkElectoralMaster"}
                  defaultValue={owner?.remarkElectoralMaster}
                  render={(props) => (
                    <TextArea
                      value={props.value}
                      placeholder={"Enter the remarkElectoralMaster"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "remarkElectoralMaster"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "remarkElectoralMaster" });
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
          <div className="bmc-card-row" style={{ textAlign: "center" }}>
            <button
              onSubmit={handleSubmit(onSubmit)}
              type="button"
              className="bmc-card-button"
              style={{
                borderBottom: "3px solid black",
                outline: "none",
              }}
            >
              {t("BMC_Create")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const religionMasterPage = (_props) => {
  const { index, allOwners, onSelect, formData, formState, setOwners, clearErrors, config } = _props;
  const {
    control,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    handleSubmit,
  } = useForm();
  const { t } = useTranslation();
  const [owner, setOwner] = useState(formData?.owners || [createMasterDetail()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  const onSubmit = (data) => {
    setOwner(data);
    console.log(data);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-title">Religion Master</div>
          <div className="bmc-card-row">
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_Religion_Name"}</CardLabel>
                <Controller
                  control={control}
                  name={"religion"}
                  defaultValue={owner?.religion}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the religion"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "religion"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "religion" });
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
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <Controller
                  control={control}
                  name={"remarkReligion"}
                  defaultValue={owner?.remarkReligion}
                  render={(props) => (
                    <TextArea
                      value={props.value}
                      placeholder={"Enter the remarkReligion"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "remarkReligion"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "remarkReligion" });
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
          <div className="bmc-card-row" style={{ textAlign: "center" }}>
            <button
              type="button"
              onSubmit={handleSubmit(onSubmit)}
              className="bmc-card-button"
              style={{
                borderBottom: "3px solid black",
                outline: "none",
              }}
            >
              {t("BMC_Create")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const casteCategoryMasterPage = (_props) => {
  const { index, allOwners, onSelect, formData, formState, setError, clearErrors, config } = _props;
  const {
    control,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    handleSubmit,
  } = useForm();
  const { t } = useTranslation();
  const [owner, setOwner] = useState(formData?.owners || [createMasterDetail()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  const onSubmit = (data) => {
    setOwner(data);
    console.log(data);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-title">Caste Category Master</div>
          <div className="bmc-card-row">
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_Caste_Category"}</CardLabel>
                <Controller
                  control={control}
                  name={"casteCategory"}
                  defaultValue={owner?.casteCategory}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the casteCategory"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "casteCategory"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "casteCategory" });
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
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <Controller
                  control={control}
                  name={"remarkCasteCategoty"}
                  defaultValue={owner?.remarkCasteCategoty}
                  render={(props) => (
                    <TextArea
                      value={props.value}
                      placeholder={"Enter the remarkCasteCategoty"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "remarkCasteCategoty"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "remarkCasteCategoty" });
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
          <div className="bmc-card-row" style={{ textAlign: "center" }}>
            <button
              type="button"
              onSubmit={handleSubmit(onSubmit)}
              className="bmc-card-button"
              style={{
                borderBottom: "3px solid black",
                outline: "none",
              }}
            >
              {t("BMC_Create")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const bankMasterPage = (_props) => {
  const { index, allOwners, onSelect, formData, formState, setError, clearErrors, config } = _props;
  const {
    control,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    handleSubmit,
  } = useForm();
  const { t } = useTranslation();
  const [owner, setOwner] = useState(formData?.owners || [createMasterDetail()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  const onSubmit = (data) => {
    setOwner(data);
    console.log(data);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-title">Bank Master</div>
          <div className="bmc-card-row">
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_Bank_Name"}</CardLabel>
                <Controller
                  control={control}
                  name={"bankName"}
                  defaultValue={owner?.bankName}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the bankName"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "bankName"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "bankName" });
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
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_Bank_Code"}</CardLabel>
                <Controller
                  control={control}
                  name={"bankCode"}
                  defaultValue={owner?.bankCode}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the bankCode"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "bankCode"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "bankCode" });
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
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <Controller
                  control={control}
                  name={"remarkBank"}
                  defaultValue={owner?.remarkBank}
                  render={(props) => (
                    <TextArea
                      value={props.value}
                      placeholder={"Enter the remarkBank"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "remarkBank"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "remarkBank" });
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
          <div className="bmc-card-row" style={{ textAlign: "center" }}>
            <button
              type="button"
              onSubmit={handleSubmit(onSubmit)}
              className="bmc-card-button"
              style={{
                borderBottom: "3px solid black",
                outline: "none",
              }}
            >
              {t("BMC_Create")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const sectorMasterPage = (_props) => {
  const { index, allOwners, onSelect, formData, formState, setError, clearErrors, config } = _props;
  const {
    control,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    handleSubmit,
  } = useForm();
  const { t } = useTranslation();
  const [owner, setOwner] = useState(formData?.owners || [createMasterDetail()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  const onSubmit = (data) => {
    setOwner(data);
    console.log(data);
  };
  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-title">Sector Master</div>
          <div className="bmc-card-row">
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_Sector_Name"}</CardLabel>
                <Controller
                  control={control}
                  name={"sctorname"}
                  defaultValue={owner?.sector}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the sctorname"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "sctorname"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "sctorname" });
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
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <Controller
                  control={control}
                  name={"remarkSector"}
                  defaultValue={owner?.remarkSector}
                  render={(props) => (
                    <TextArea
                      value={props.value}
                      placeholder={"Enter the remarkSector"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "remarkSector"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "remarkSector" });
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
          <div className="bmc-card-row" style={{ textAlign: "center" }}>
            <button
              onSubmit={handleSubmit(onSubmit)}
              type="button"
              className="bmc-card-button"
              style={{
                borderBottom: "3px solid black",
                outline: "none",
              }}
            >
              {t("BMC_Create")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const qualificationMasterPage = (_props) => {
  const { index, allOwners, onSelect, formData, formState, setError, clearErrors, config } = _props;
  const {
    control,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    handleSubmit,
  } = useForm();
  const { t } = useTranslation();
  const [owner, setOwner] = useState(formData?.owners || [createMasterDetail()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  const onSubmit = (data) => {
    setOwner(data);
    console.log(data);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-title">Qualification Master</div>
          <div className="bmc-card-row">
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{"BMC_Ward_Name"}</CardLabel>
                <Controller
                  control={control}
                  name={"qualification"}
                  defaultValue={owner?.qualification}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the qualification"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "qualification"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "qualification" });
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
            <div className="bmc-col2-card">
              <LabelFieldPair>
                <Controller
                  control={control}
                  name={"remarkQualification"}
                  defaultValue={owner?.remarkQualification}
                  render={(props) => (
                    <TextArea
                      value={props.value}
                      placeholder={"Enter the remarkQualification"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "remarkQualification"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "remarkQualification" });
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
          <div className="bmc-card-row" style={{ textAlign: "center" }}>
            <button
              onSubmit={handleSubmit(onSubmit)}
              type="button"
              className="bmc-card-button"
              style={{
                borderBottom: "3px solid black",
                outline: "none",
              }}
            >
              {t("BMC_Create")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const courseMasterPage = (_props) => {
  const { index, allOwners, onSelect, formData, formState, setError, clearErrors, config } = _props;
  const {
    control,
    formState: localFormState,
    watch,
    setError: setLocalError,
    clearErrors: clearLocalErrors,
    setValue,
    trigger,
    handleSubmit,
  } = useForm();
  const { t } = useTranslation();
  const [owner, setOwner] = useState(formData?.owners || [createMasterDetail()]);
  const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });

  const onSubmit = (data) => {
    setOwner(data);
    console.log(data);
  };

  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-title">Course Master</div>
          <div className="bmc-card-row">
            <div className="bmc-col1-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Sector")}</CardLabel>
                <Controller
                  control={control}
                  name={"sectorCourseName"}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  }}
                  render={(props) => (
                    <Dropdown
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                      }}
                      onBlur={props.onBlur}
                      option={dropdownOptions.rationcardtype}
                      optionKey="value"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col1-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Course_Code")}</CardLabel>
                <Controller
                  control={control}
                  name={"courseCode"}
                  defaultValue={owner?.courseCode}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the courseCode"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "courseCode"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "courseCode" });
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
            <div className="bmc-col1-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Course_Name")}</CardLabel>
                <Controller
                  control={control}
                  name={"courseName"}
                  defaultValue={owner?.courseName}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the courseName"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "courseName"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "courseName" });
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
            <div className="bmc-col1-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Course_Qualification")}</CardLabel>
                <Controller
                  control={control}
                  name={"courseQualification"}
                  rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                  }}
                  render={(props) => (
                    <Dropdown
                      selected={props.value}
                      select={(value) => {
                        props.onChange(value);
                      }}
                      onBlur={props.onBlur}
                      option={dropdownOptions.rationcardtype}
                      optionKey="value"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
            </div>
            <div className="bmc-col1-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Course_Duration")}</CardLabel>
                <Controller
                  control={control}
                  name={"courseDuration"}
                  defaultValue={owner?.courseDuration}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the courseDuration"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "courseDuration"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "courseDuration" });
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
            <div className="bmc-col1-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Course_Total_Cost")}</CardLabel>
                <Controller
                  control={control}
                  name={"courseTotalCost"}
                  defaultValue={owner?.courseTotalCost}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the courseTotalCost"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "courseTotalCost"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "courseTotalCost" });
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
            <div className="bmc-col1-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Course_NSQF_Level")}</CardLabel>
                <Controller
                  control={control}
                  name={"courseNSQFLevel"}
                  defaultValue={owner?.courseNSQFLevel}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the courseNSQFLevel"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "courseNSQFLevel"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "courseNSQFLevel" });
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
            <div className="bmc-col1-card">
              <LabelFieldPair>
                <CardLabel className="bmc-label">{t("BMC_Course_NQR_Code")}</CardLabel>
                <Controller
                  control={control}
                  name={"courseNQRCode"}
                  defaultValue={owner?.courseNQRCode}
                  render={(props) => (
                    <TextInput
                      value={props.value}
                      placeholder={"Enter the courseNQRCode"}
                      autoFocus={focusIndex.index === owner?.key && focusIndex.type === "courseNQRCode"}
                      onChange={(e) => {
                        props.onChange(e.target.value);
                        setFocusIndex({ index: owner.key, type: "courseNQRCode" });
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
          <div className="bmc-card-row" style={{ textAlign: "center" }}>
            <button
              onSubmit={handleSubmit(onSubmit)}
              type="button"
              className="bmc-card-button"
              style={{
                borderBottom: "3px solid black",
                outline: "none",
              }}
            >
              {t("BMC_Create")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const wardWiseApplication = () => {
  const { t } = useTranslation();
  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-table-container" style={{ padding: "1rem" }}>
            <div className="bmc-title" style={{ paddingBottom: "2rem", paddingLeft: "0" }}>
              Ward Wise Applications Summary For Machines
            </div>
            <table className="bmc-hover-table">
              <thead>
                <tr>
                  <th>{"BMC_S.No"}</th>
                  <th>{"BMC_Ward_Name"}</th>
                  <th>{"BMC_SubWard_Name"}</th>
                  <th>{"BMC_Submitted_Applications"}</th>
                  <th>{"BMC_Verified_Applications"}</th>
                  <th>{"BMC_Randomized"}</th>
                  <th>{"BMC_Approved"}</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>1</td>
                  <td>Ward Name</td>
                  <td>Sub Ward Name</td>
                  <td>10</td>
                  <td>5</td>
                  <td>5</td>
                  <td>5</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div style={{ textAlign: "center", padding: "1rem" }}>
            <button className="bmc-card-button" style={{ borderBottom: "3px solid black", marginRight: "1rem" }}>
              {t("BMC_Print")}
            </button>
            <button className="bmc-card-master-button" style={{ borderBottom: "3px solid black", outline: "none", marginRight: "5rem" }}>
              {t("BMC_Download_Excel")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const schemeWiseApplication = () => {
  const { t } = useTranslation();
  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-table-container" style={{ padding: "1rem" }}>
            <div className="bmc-title" style={{ paddingBottom: "2rem", paddingLeft: "0" }}>
              Scheme Wise Application Report
            </div>
            <table className="bmc-hover-table">
              <thead>
                <th>{"BMC_S.No"}</th>
                <th>{"BMC_Ward_Name"}</th>
                <th>{"BMC_SubWard_Name"}</th>
                <th>{"BMC_Application_Name"}</th>
                <th>{"BMC_Address"}</th>
                <th>{"BMC_Gender"}</th>
                <th>{"BMC_Machine"}</th>
                <th>{"BMC_Application Status"}</th>
                <th>{"BMC_Date"}</th>
              </thead>
              <tbody>
                <tr>
                  <td>1</td>
                  <td>Ward Name</td>
                  <td>Sub Ward Name</td>
                  <td>Application Name</td>
                  <td>Address</td>
                  <td>Gender</td>
                  <td>Machine</td>
                  <td>Application Status</td>
                  <td>Date</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div style={{ textAlign: "center", padding: "1rem" }}>
            <button className="bmc-card-button" style={{ borderBottom: "3px solid black", marginRight: "1rem" }}>
              {t("BMC_Print")}
            </button>
            <button className="bmc-card-master-button" style={{ borderBottom: "3px solid black", outline: "none", marginRight: "5rem" }}>
              {t("BMC_Download_Excel")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export const courseWiseApplication = () => {
  const { t } = useTranslation();
  return (
    <React.Fragment>
      <div className="bmc-card-full">
        <div className="bmc-row-card-header">
          <div className="bmc-table-container" style={{ padding: "1rem" }}>
            <div className="bmc-title" style={{ paddingBottom: "2rem", paddingLeft: "0" }}>
              Course Wise Report
            </div>
            <table className="bmc-hover-table">
              <thead>
                <th>{"BMC_S.No"}</th>
                <th>{"BMC_Ward_Name"}</th>
                <th>{"BMC_SubWard_Name"}</th>
                <th>{"BMC_Application_Name"}</th>
                <th>{"BMC_Address"}</th>
                <th>{"BMC_Gender"}</th>
                <th>{"BMC_Machine"}</th>
                <th>{"BMC_Application Status"}</th>
                <th>{"BMC_Date"}</th>
              </thead>
              <tbody>
                <tr>
                  <td>1</td>
                  <td>Ward Name</td>
                  <td>Sub Ward Name</td>
                  <td>Application Name</td>
                  <td>Address</td>
                  <td>Gender</td>
                  <td>Machine</td>
                  <td>Application Status</td>
                  <td>Date</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div style={{ textAlign: "center", padding: "1rem" }}>
            <button className="bmc-card-button" style={{ borderBottom: "3px solid black", marginRight: "1rem" }}>
              {t("BMC_Print")}
            </button>
            <button className="bmc-card-master-button" style={{ borderBottom: "3px solid black", outline: "none", marginRight: "5rem" }}>
              {t("BMC_Download_Excel")}
            </button>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};
