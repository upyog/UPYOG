import React, { useState, useEffect } from "react";
import { CardLabel, LabelFieldPair, TextInput, CardLabelError, Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import { useForm, Controller, useWatch } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";

const AddressDetails = ({ config, onSelect, formData, setError, formState, clearErrors }) => {
  if (window.location.href.includes("modify-challan") && sessionStorage.getItem("mcollectEditObject")) {
    formData = JSON.parse(sessionStorage.getItem("mcollectEditObject"));
  }
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const isEdit = pathname.includes("/modify-challan/");
  const cities = Digit.Hooks.mcollect.usemcollectTenants();
  const getCities = () => cities?.filter((e) => e.code === Digit.ULBService.getCurrentTenantId()) || [];

  const stateCode = window?.globalConfigs?.getConfig("STATE_LEVEL_TENANT_ID");
  const mdmsData = Digit.Hooks.mcollect.useCommonMDMS(stateCode, "common-masters", ["HierarchyType"]);
  const type = mdmsData?.data?.["common-masters"]?.["HierarchyType"]?.[0];

  const { data: fetchedLocalities } = Digit.Hooks.useBoundaryLocalities(
    getCities()[0]?.code,
    type?.code?.toLowerCase(),
    { enabled: !!getCities()[0] },
    t
  );

  const [localities, setLocalities] = useState(fetchedLocalities);

  useEffect(() => {
    setLocalities(fetchedLocalities);
  }, [fetchedLocalities]);

  const existing = formData?.[config?.key]?.[0] || formData?.consomerDetails1?.[0] || {};

  const { control, formState: localFormState, watch, setValue, trigger } = useForm({
    defaultValues: {
      doorNo:     existing.doorNo     || "",
      building:   existing.building   || "",
      streetName: existing.streetName || "",
      pincode:    existing.pincode    || "",
      mohalla:    existing.mohalla    || "",
    },
    mode: "onChange",
  });

  const { errors } = localFormState;
  const isMobile = window.Digit.Utils.browser.isMobile();

  // Push current values up to FormComposer whenever any field changes
  const formValue = watch();
  useEffect(() => {
    onSelect(config?.key, [formValue]);
    console.log("ADDRESS DETAILS", formValue);
    // Also keep sessionStorage in sync for tax-head merging in onSubmit
    const stored = JSON.parse(sessionStorage.getItem("mcollectFormData")) || {};
    sessionStorage.setItem("mcollectFormData", JSON.stringify({ ...stored, ...formValue }));
  }, [JSON.stringify(formValue)]);

  // Sync parent error state
  useEffect(() => {
    const hasErrors = Object.keys(errors).length > 0;
    if (hasErrors) {
      setError(config.key, { type: "manual", message: "Address has errors" });
    } else {
      clearErrors(config.key);
    }
  }, [JSON.stringify(errors)]);

  // Reset mohalla when pincode changes (skip initial mount)
  const selectedPincode = useWatch({ control, name: "pincode", defaultValue: "" });
  const isPincodeInitialMount = React.useRef(true);
  useEffect(() => {
    if (isPincodeInitialMount.current) {
      isPincodeInitialMount.current = false;
      return;
    }
    if (!isEdit) setValue("mohalla", "");

    // Filter localities by pincode
    if (selectedPincode) {
      const filtered = fetchedLocalities?.filter((loc) => loc.pincode == selectedPincode);
      setLocalities(filtered?.length ? filtered : fetchedLocalities);
    } else {
      setLocalities(fetchedLocalities);
    }
  }, [selectedPincode]);

  const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };

  return (
    <div style={isMobile ? {} : { marginTop: "-50px" }}>
      <div style={{ marginBottom: "16px" }}>
        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("UC_DOOR_NO_LABEL")}</CardLabel>
          <div className="field">
            <Controller
              control={control}
              name="doorNo"
              render={({ field }) => (
                <TextInput value={field.value} onChange={(e) => field.onChange(e.target.value)} onBlur={field.onBlur} disable={isEdit} />
              )}
            />
          </div>
        </LabelFieldPair>

        <LabelFieldPair>
          <CardLabel className={isMobile ? "card-label-APK" : "card-label-smaller"}>{t("UC_BLDG_NAME_LABEL")}</CardLabel>
          <div className="field">
            <Controller
              control={control}
              name="building"
              render={({ field }) => (
                <TextInput value={field.value} onChange={(e) => field.onChange(e.target.value)} onBlur={field.onBlur} disable={isEdit} />
              )}
            />
          </div>
        </LabelFieldPair>

        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("UC_SRT_NAME_LABEL")}</CardLabel>
          <div className="field">
            <Controller
              control={control}
              name="streetName"
              render={({ field }) => (
                <TextInput value={field.value} onChange={(e) => field.onChange(e.target.value)} onBlur={field.onBlur} disable={isEdit} />
              )}
            />
          </div>
        </LabelFieldPair>

        <LabelFieldPair>
          <CardLabel className="card-label-smaller">{t("UC_PINCODE_LABEL")}</CardLabel>
          <div className="field">
            <Controller
              control={control}
              name="pincode"
              rules={{ validate: { pattern: (val) => (/^[1-9][0-9]{5}$|^$/.test(val) ? true : t("UC_PINCODE_INVALID")) } }}
              render={({ field }) => (
                <TextInput
                  value={field.value}
                  errorStyle={!!(localFormState.touchedFields?.pincode && errors?.pincode?.message)}
                  onChange={(e) => field.onChange(e.target.value)}
                  onBlur={field.onBlur}
                  disable={isEdit}
                />
              )}
            />
          </div>
        </LabelFieldPair>
        <CardLabelError style={errorStyle}>{localFormState.touchedFields?.pincode ? errors?.pincode?.message : ""}</CardLabelError>

        <LabelFieldPair>
          <CardLabel style={{ paddingTop: "10px" }} className="card-label-smaller">
            {t("UC_MOHALLA_LABEL")} <span className="check-page-link-button"> *</span>
          </CardLabel>
          <Controller
            control={control}
            name="mohalla"
            rules={{ required: t("REQUIRED_FIELD") }}
            render={({ field }) => (
              <Dropdown
                className="form-field"
                selected={field.value}
                isMandatory={true}
                option={localities}
                select={field.onChange}
                optionKey="i18nkey"
                onBlur={field.onBlur}
                disable={isEdit}
                t={t}
              />
            )}
          />
        </LabelFieldPair>
      </div>
      <hr style={{ width: "100%", border: "1px solid #D6D5D4", marginTop: "50px", marginBottom: "40px" }} />
    </div>
  );
};

export default AddressDetails;
