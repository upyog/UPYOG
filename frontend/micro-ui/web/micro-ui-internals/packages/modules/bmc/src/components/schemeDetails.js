import React, { useEffect, useCallback, useState, useRef } from "react";
import { Controller, useForm } from "react-hook-form";
import isEqual from "lodash.isequal";
import { useTranslation } from "react-i18next";
import { CardLabel, CheckBox, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import MultiSelect from "../components/multidropdown";
import RadioButton from "../components/radiobutton";
import dropdownOptions from "../pagecomponents/dropdownOptions.json";
import ToggleSwitch from "./Toggle";

const SchemeDetailsPage = ({ onUpdate, initialRows = {}, AllowEdit = true, tenantId, scheme, schemeType, selectedScheme, ...props }) => {
  const { config } = props;
  const { t } = useTranslation();
  const [isEditable, setIsEditable] = useState(AllowEdit);
  const headerLocale = Digit.Utils.locale.getTransformedLocale(tenantId);
  const [documents, setDocuments] = useState([]);
  const [selectedDocuments, setSelectedDocuments] = useState(initialRows?.documents || []);

  const {
    control,
    setValue,
    clearErrors,
    trigger,
    watch,
    getValues,
    formState: { errors, isValid },
  } = useForm({
    defaultValues: {
      income: initialRows?.income || "",
      statement: initialRows?.statement || "",
      agreeToPay: initialRows?.agreeToPay || "",
      documents: selectedDocuments || [],
      employed: initialRows?.employed || "",
      Occupation: initialRows?.Occupation || "Service",
    },
    mode: "onChange",
  });

  const formValuesRef = useRef(getValues());
  const formValues = watch();
  const isEmployed = formValues.employed;

  const processCommonData = (data, headerLocale) => {
    return (
      data?.CommonDetails?.map((item) => ({
        code: item.id,
        name: item.name,
        i18nKey: `${headerLocale}_ADMIN_${item.name}`,
      })) || []
    );
  };

  const documentFunction = (data) => {
    const documentsData = processCommonData(data, headerLocale);
    setDocuments(documentsData);
    return { documentsData };
  };

  const getDocuments = { CommonSearchCriteria: { Option: "document" } };
  Digit.Hooks.bmc.useCommonGet(getDocuments, { select: documentFunction });

  const stableOnUpdate = useCallback(
    (values, valid) => {
      onUpdate(values, valid);
    },
    [onUpdate]
  );

  useEffect(() => {
    if (!isEqual(formValuesRef.current, formValues)) {
      formValuesRef.current = formValues;
      stableOnUpdate(formValues, isValid);
    }
  }, [formValues, isValid, stableOnUpdate]);

  useEffect(() => {
    trigger();
  }, [trigger]);

  useEffect(() => {
    if (initialRows) {
      setValue("income", initialRows?.income || "");
      setValue("statement", initialRows?.statement || "");
      setValue("agreeToPay", initialRows?.agreeToPay || "");
      setValue("documents", initialRows?.documents || []);
      setValue("employed", initialRows?.employed || "");
      setValue("Occupation", initialRows?.Occupation || "Service");

      if (initialRows.income) clearErrors("income");
      if (initialRows.statement) clearErrors("statement");
      if (initialRows.agreeToPay) clearErrors("agreeToPay");
      if (initialRows.documents) clearErrors("documents");
      if (initialRows.employed) clearErrors("employed");
      if (initialRows.Occupation) clearErrors("Occupation");
    }
  }, [initialRows, setValue, clearErrors]);

  const handleSelect = (e, option) => {
    const updatedSelectedDocuments = selectedDocuments.some((doc) => doc.code === option.code)
      ? selectedDocuments.filter((doc) => doc.code !== option.code)
      : [...selectedDocuments, option];

    setSelectedDocuments(updatedSelectedDocuments);
    setValue("documents", updatedSelectedDocuments);
  };

  const handleToggle = () => {
    setIsEditable(!isEditable);
  };

  return (
    <React.Fragment>
      <div className="bmc-row-card-header">
        <div className="bmc-card-row">
          <div className="bmc-col-large-header">
            {schemeType && schemeType.type === "machine" ? (
              <React.Fragment>
                {schemeType && (
                  <div className="bmc-title" index={schemeType.key}>
                    Selected Machine: {schemeType?.value}
                  </div>
                )}
              </React.Fragment>
            ) : (
              <React.Fragment>
                {schemeType && (
                  <div className="bmc-title" index={schemeType.key}>
                    Selected Course: {schemeType?.value}
                  </div>
                )}
              </React.Fragment>
            )}
          </div>
          <div className="bmc-col-small-header" style={{ textAlign: "end" }}>
            <ToggleSwitch
              id={"SchemeDetails"}
              isOn={isEditable}
              handleToggle={handleToggle}
              onLabel="Editable"
              offLabel="Readonly"
              disabled={!AllowEdit}
            />
          </div>
        </div>

        <div className="bmc-card-row">
          {selectedScheme ? (
            <div className="bmc-card">
              {selectedScheme && selectedScheme.criteria && selectedScheme.criteria.some((criterion) => criterion.criteriaType) ? (
                <div className="bmc-card-row">
                  <div className="bmc-title">Scheme Details</div>
                  <div className="bmc-col1-card">
                    <CardLabel className="bmc-label">{t("BMC_Documents*")}</CardLabel>
                    <Controller
                      name="documents"
                      control={control}
                      render={(props) => (
                        <div>
                          {isEditable ? (
                            <MultiSelect
                              options={documents}
                              optionsKey="i18nKey"
                              selected={props.value}
                              onSelect={handleSelect}
                              defaultLabel={t("No documents selected")}
                              defaultUnit={t("documents selected")}
                              t={t}
                              isOBPSMultiple={true}
                              BlockNumber={true}
                              disabled={!isEditable}
                            />
                          ) : (
                            <MultiSelect
                              options={documents}
                              optionsKey="i18nKey"
                              selected={props.value}
                              onSelect={handleSelect}
                              defaultLabel={t("No documents selected")}
                              defaultUnit={t("documents selected")}
                              t={t}
                              isOBPSMultiple={true}
                              BlockNumber={true}
                              disabled={!isEditable}
                            />
                          )}
                          {errors.documents && <span style={{ color: "red" }}>{errors.documents.message}</span>}
                        </div>
                      )}
                    />
                  </div>
                  <div className="bmc-col1-card">
                    <LabelFieldPair>
                      <CardLabel className="bmc-label">{t("BMC_Income*")}</CardLabel>
                      <Controller
                        control={control}
                        name="income"
                        rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                          <div>
                            {isEditable ? (
                              <Dropdown
                                placeholder={t("Select the Income")}
                                selected={props.value}
                                select={(value) => props.onChange(value)}
                                onBlur={props.onBlur}
                                option={dropdownOptions.Income}
                                optionKey="value"
                                t={t}
                                isMandatory={true}
                              />
                            ) : (
                              <TextInput readOnly value={props.value?.value || ""} />
                            )}
                          </div>
                        )}
                      />
                    </LabelFieldPair>
                  </div>
                </div>
              ) : (
                <div>No criteria available for the selected scheme.</div>
              )}
            </div>
          ) : (
            <div>Please select a scheme.</div>
          )}
        </div>
        <div className="bmc-card-row">
          <div className="bmc-col2-card" style={{ padding: "0" }}>
            <div className="bmc-title">Occupation Details</div>
            <div className="bmc-col2-card" style={{ padding: "0" }}>
              <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                <CardLabel className="bmc-label">{t("BMC_Occupation*")}</CardLabel>
                <Controller
                  control={control}
                  name="employed"
                  render={(props) => (
                    <div className="bmc-checkbox" style={{ display: "flex", flexDirection: "row" }}>
                      <CheckBox
                        label={t("Employed")}
                        styles={{ height: "auto", color: "#f47738", fontSize: "18px" }}
                        checked={props.value}
                        onChange={(value) => {
                          if (isEditable) {
                            props.onChange(!props.value);
                          }
                        }}
                        disabled={!isEditable}
                      />
                    </div>
                  )}
                />
              </LabelFieldPair>
            </div>
            {isEmployed && (
              <div className="bmc-col1-card">
                <LabelFieldPair t={t} config={config} isMultipleAllow={true}>
                  <CardLabel className="bmc-label">{t("BMC_Employment Detail*")}</CardLabel>
                  <Controller
                    control={control}
                    name="Occupation"
                    render={(props) => (
                      <div>
                        {isEditable ? (
                          <RadioButton
                            t={t}
                            optionsKey="value"
                            disabled={!isEditable}
                            options={[
                              { label: t("Service"), value: "Service" },
                              { label: t("Business"), value: "Business" },
                            ]}
                            style={{ display: "flex", flexDirection: "row", gap: "12px", marginTop: "1rem" }}
                            selectedOption={props.value}
                            onSelect={(value) => props.onChange(value)}
                          />
                        ) : (
                          <RadioButton
                            t={t}
                            optionsKey="value"
                            disabled={!isEditable}
                            options={[
                              { label: t("Service"), value: "Service" },
                              { label: t("Business"), value: "Business" },
                            ]}
                            style={{ display: "flex", flexDirection: "row", gap: "12px", marginTop: "1rem" }}
                            selectedOption={props.value}
                            onSelect={(value) => props.onChange(value)}
                          />
                        )}
                      </div>
                    )}
                  />
                </LabelFieldPair>
              </div>
            )}
          </div>
        </div>
        <div className="bmc-col-large-header" style={{ padding: "0" }}>
          <div className="bmc-checkbox">
            <div className="bmc-card-row">
              <Controller
                control={control}
                name="agreeToPay"
                render={(props) => (
                  <CheckBox
                    label={t("Agree to pay Contribution.")}
                    styles={{ height: "auto", color: "#f47738", fontWeight: "bold", fontSize: "18px", float: "left" }}
                    checked={props.value}
                    onChange={() => {
                      if (isEditable) {
                        props.onChange(!props.value);
                      }
                    }}
                    disabled={!isEditable}
                  />
                )}
              />
            </div>
            <div className="bmc-card-row">
              <Controller
                control={control}
                name="statement"
                render={(props) => (
                  <CheckBox
                    label={t("To the best of my knowledge, the information provided above is correct..")}
                    styles={{ height: "auto", color: "#f47738", fontWeight: "bold", fontSize: "18px", float: "left" }}
                    checked={props.value}
                    onChange={() => {
                      if (isEditable) {
                        props.onChange(!props.value);
                      }
                    }}
                    disabled={!isEditable}
                  />
                )}
              />
            </div>
          </div>
        </div>
      </div>
    </React.Fragment>
  );
};

export default SchemeDetailsPage;
