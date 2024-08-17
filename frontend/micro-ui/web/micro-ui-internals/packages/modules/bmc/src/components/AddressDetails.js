import { CardLabel, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import isEqual from "lodash.isequal";
import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import ToggleSwitch from "./Toggle";

const AddressDetailCard = ({ onUpdate, initialRows = {}, AllowEdit = false, tenantId }) => {
  const { t } = useTranslation();
  const [isEditable, setIsEditable] = useState(AllowEdit);
  const headerLocale = useMemo(() => Digit.Utils.locale.getTransformedLocale(tenantId), [tenantId]);

  const {
    control,
    watch,
    formState: { errors, isValid },
    trigger,
    setValue,
    clearErrors,
    getValues,
  } = useForm({
    defaultValues: {
      house: initialRows?.address?.house || "",
      street: initialRows?.address?.street || "",
      landmark: initialRows?.address?.landmark || "",
      locality: initialRows?.address?.locality || "",
      city: initialRows?.address?.city || "",
      subDistrict: initialRows?.address?.subDistrict || "",
      district: initialRows?.address?.district || "",
      state: initialRows?.address?.state || "",
      zoneName: initialRows?.UserOtherDetails?.zoneName || "",
      pinCode: initialRows?.address?.pinCode || "",
      blockName: initialRows?.UserOtherDetails?.blockName || "",
      wardName: initialRows?.UserOtherDetails?.wardName || "",
    },
    mode: "onChange",
  });

  const [zones, setZones] = useState([]);
  const [blocks, setBlocks] = useState([]);
  const [wards, setWards] = useState([]);

  Digit.Hooks.useLocation(tenantId, "Zone", {
    select: (data) => {
      const zonesData = [];
      const blocksData = [];
      const wardsData = [];

      data?.TenantBoundary[0]?.boundary.forEach((zone) => {
        zonesData.push({
          code: zone.code,
          name: zone.name,
          i18nKey: `${headerLocale}_ADMIN_${zone.code}`,
        });

        zone.children.forEach((block) => {
          blocksData.push({
            code: block.code,
            name: block.name,
            zoneCode: zone.code,
            i18nKey: `${headerLocale}_ADMIN_${block.code}`,
          });

          block.children.forEach((ward) => {
            wardsData.push({
              code: ward.code,
              name: ward.name,
              zoneCode: zone.code,
              blockCode: block.code,
              i18nKey: `${headerLocale}_ADMIN_${ward.code}`,
            });
          });
        });
      });

      setZones(zonesData);
      setBlocks(blocksData);
      setWards(wardsData);

      return {
        zonesData,
        blocksData,
        wardsData,
      };
    },
  });

  const selectedWard = watch("wardName");
  const [filteredBlocks, setFilteredBlocks] = useState([]);
  const [filteredZones, setFilteredZones] = useState([]);

  useEffect(() => {
    if (selectedWard && selectedWard.code) {
      const selectedBlockCode = wards.find((ward) => ward.code === selectedWard.code)?.blockCode;
      const selectedZoneCode = wards.find((ward) => ward.code === selectedWard.code)?.zoneCode;

      if (selectedBlockCode) {
        const filteredBlocks = blocks.filter((block) => block.code === selectedBlockCode);
        setFilteredBlocks(filteredBlocks);
        setValue("blockName", filteredBlocks[0]?.i18nKey || ""); // Automatically set the block name text field
      } else {
        setFilteredBlocks([]);
        setValue("blockName", ""); // Reset block text field
      }

      if (selectedZoneCode) {
        const filteredZones = zones.filter((zone) => zone.code === selectedZoneCode);
        setFilteredZones(filteredZones);
        setValue("zoneName", filteredZones[0]?.i18nKey || ""); // Automatically set the zone name text field
      } else {
        setFilteredZones([]);
        setValue("zoneName", ""); // Reset zone text field
      }
    } else {
      setFilteredBlocks([]);
      setFilteredZones([]);
    }
  }, [wards, blocks, zones, selectedWard, setValue]);

  useEffect(() => {
    if (!selectedWard) {
      setFilteredBlocks([]);
      setFilteredZones([]);
      setValue("blockName", ""); // Reset block text field
      setValue("zoneName", ""); // Reset zone text field
    }
  }, [selectedWard, setValue]);

  const formValuesRef = useRef(getValues());
  const formValues = watch();

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
    trigger(); // Validate the form on mount to show errors if fields are empty
  }, [trigger]);

  useEffect(() => {
    if (initialRows) {
    
      setValue("house", initialRows?.address?.house || "");
      setValue("street", initialRows?.address?.street || "");
      setValue("landmark", initialRows?.address?.landmark || "");
      setValue("locality", initialRows?.address?.locality || "");
      setValue("subDistrict", initialRows?.address?.subDistrict || "");
      setValue("district", initialRows?.address?.district || "");
      setValue("state", initialRows?.address?.state || "");

      setValue("city", initialRows?.address?.city || "");
      setValue("pinCode", initialRows?.address?.pinCode || "");

      const zonedata = zones.find((zone) => zone.code === initialRows?.UserOtherDetails?.zone) || "";
      const blockdata = blocks.find((block) => block.code === initialRows?.UserOtherDetails?.block) || "";
      const warddata = wards.find((ward) => ward.code === initialRows?.UserOtherDetails?.ward) || "";

      setValue("zoneName", zonedata?.name || "");
      setValue("blockName", blockdata?.name || "");
      setValue("wardName", warddata || "");

      if (initialRows?.address?.house) clearErrors("house");
      if (initialRows?.address?.street) clearErrors("street");
      if (initialRows?.address?.landmark) clearErrors("landmark");
      if (initialRows?.address?.locality) clearErrors("locality");
      if (initialRows?.address?.subDistrict) clearErrors("subDistrict");
      if (initialRows?.address?.district) clearErrors("district");
      if (initialRows?.address?.state) clearErrors("state");
      if (initialRows?.address?.city) clearErrors("city");
      if (initialRows?.address?.pinCode) clearErrors("pinCode");
      if (zonedata) clearErrors("zoneName");
      if (blockdata) clearErrors("blockName");
      if (warddata) clearErrors("wardName");
    }
  }, [initialRows, setValue, headerLocale, clearErrors, zones, blocks, wards]);

  const handleToggle = () => {
    setIsEditable(!isEditable);
  };

  return (
    <React.Fragment>
      <form className="bmc-row-card-header">
        <div className="bmc-card-row">
          <div className="bmc-col-large-header">
            <div className="bmc-title">{t("ADDRESS DETAILS")}</div>
          </div>
          <div className="bmc-col-small-header" style={{ textAlign: "end" }}>
            <ToggleSwitch
              id={"AddressToggle"}
              isOn={isEditable}
              handleToggle={handleToggle}
              onLabel="Editable"
              offLabel="Readonly"
              disabled={!AllowEdit}
            />
          </div>
        </div>
        <div className="bmc-card-row">
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{"BMC_HOUSE"}</CardLabel>
              <Controller
                control={control}
                name={"house"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.house && <span style={{ color: "red" }}>{errors.house.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{"BMC_STREET"}</CardLabel>
              <Controller
                control={control}
                name={"street"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.street && <span style={{ color: "red" }}>{errors.street.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{"BMC_landmark"}</CardLabel>
              <Controller
                control={control}
                name={"landmark"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.landmark && <span style={{ color: "red" }}>{errors.landmark.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{"BMC_LOCALITY"}</CardLabel>
              <Controller
                control={control}
                name={"locality"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.locality && <span style={{ color: "red" }}>{errors.locality.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
        </div>
        <div className="bmc-card-row">
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{"BMC_CITY"}</CardLabel>
              <Controller
                control={control}
                name={"city"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.city && <span style={{ color: "red" }}>{errors.city.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{"BMC_SUBDISTRICT"}</CardLabel>
              <Controller
                control={control}
                name={"subDistrict"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.subDistrict && <span style={{ color: "red" }}>{errors.subDistrict.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{"BMC_DISTRICT"}</CardLabel>
              <Controller
                control={control}
                name={"district"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.district && <span style={{ color: "red" }}>{errors.district.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{"BMC_STATE"}</CardLabel>
              <Controller
                control={control}
                name={"state"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.state && <span style={{ color: "red" }}>{errors.state.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
        </div>
        <div className="bmc-card-row">
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_PINCODE")}</CardLabel>
              <Controller
                control={control}
                name="pinCode"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                      optionKey="i18nKey"
                      t={t}
                    />
                    {errors.pinCode && <span style={{ color: "red" }}>{errors.pinCode.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_WARD_NAME")}</CardLabel>
              <Controller
                control={control}
                name="wardName"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    {isEditable ? (
                      <Dropdown
                        placeholder={t("SELECT SUBWARD")}
                        selected={props.value}
                        select={(ward) => props.onChange(ward)}
                        onBlur={props.onBlur}
                        option={wards}
                        optionKey="i18nKey"
                        t={t}
                        isMandatory={true}
                      />
                    ) : (
                      <TextInput disabled={!isEditable} readOnly={!isEditable} value={props.value?.name || ""} />
                    )}
                    {errors.wardName && <span style={{ color: "red" }}>{errors.wardName.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_ZONENAME")}</CardLabel>
              <Controller
                control={control}
                name="zoneName"
                render={(props) => (
                  <div>
                    <TextInput disabled readOnly value={props.value || ""} />
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{t("BMC_BLOCKNAME")}</CardLabel>
              <Controller
                control={control}
                name="blockName"
                render={(props) => (
                  <div>
                    <TextInput disabled readOnly value={props.value || ""} />
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
        </div>
      </form>
    </React.Fragment>
  );
};

export default AddressDetailCard;
