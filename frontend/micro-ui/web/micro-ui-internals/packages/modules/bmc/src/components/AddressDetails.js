import { CardLabel, Dropdown, LabelFieldPair, TextInput } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import ToggleSwitch from "./Toggle";

const AddressDetailCard = ({ onUpdate, initialRows = {}, AllowEdit = false, tenantId }) => {
  const { t } = useTranslation();
  const [isEditable, setIsEditable] = useState(AllowEdit);
  const headerLocale = Digit.Utils.locale.getTransformedLocale(tenantId);
  const {
    control,
    watch,
    formState: { errors, isValid },
    trigger,
    setValue,
    clearErrors
  } = useForm({
    defaultValues: {
      house: initialRows.house || "",
      street: initialRows.street || "",
      landMark: initialRows.landMark || "",
      locality: initialRows.locality || "",
      city: initialRows.city || "",
      subDistrict: initialRows.subDistrict || "",
      district: initialRows.district || "",
      state: initialRows.state || "",
      zoneName: initialRows.zoneName || "",
      pincode: initialRows.pinCode || "",
      blockName: initialRows.blockName || "",
      wardName: initialRows.wardName || "",
    },
    mode: "onChange"
  });

  const processSingleData = (item, headerLocale) => {
    if (!item) return null;
  
    const genderMapping = {
      male: { id: 1, name: 'Male' },
      female: { id: 2, name: 'Female' },
      transgender: { id: 3, name: 'Transgender' },
    };
  
    if (typeof item === 'string') {
      const gender = genderMapping[item.toLowerCase()];
      if (!gender) return null; // Handle cases where the item is not one of the expected values
      return {
        ...gender,
        i18nKey: `${headerLocale}_ADMIN_${gender.name.toUpperCase()}`,
      };
    }
  
    if (typeof item === 'object' && item.id && item.name) {
      return {
        code: item.id,
        name: item.name,
        i18nKey: `${headerLocale}_ADMIN_${item.name}`,
      };
    }
  
    return null; // Handle cases where item is neither a string nor an object with id and name
  };
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

  const selectedZone = watch("zoneName");
  const selectedBlock = watch("blockName");
  const [filteredBlocks, setFilteredBlocks] = useState([]);
  const [filteredWards, setFilteredWards] = useState([]);

  useEffect(() => {
    if (selectedZone && selectedZone.code) {
      const filtered = blocks.filter((block) => block.zoneCode === selectedZone.code);
      setFilteredBlocks(filtered);
      setValue("blockName", null); // Reset block dropdown
      setFilteredWards([]); // Clear wards when zone changes
      setValue("wardName", null); // Reset ward dropdown
    } else {
      setFilteredBlocks([]);
    }
  }, [blocks, selectedZone, setValue]);

  useEffect(() => {
    if (selectedBlock && selectedBlock.code) {
      const filtered = wards.filter((ward) => ward.blockCode === selectedBlock.code && ward.zoneCode === selectedZone.code);
      setFilteredWards(filtered);
      setValue("wardName", null); // Reset ward dropdown
    } else {
      setFilteredWards([]);
    }
  }, [wards, selectedBlock, selectedZone, setValue]);

  const formValues = watch();

  useEffect(() => {
    onUpdate(formValues, isValid);
  }, [formValues, isValid, onUpdate]);

  useEffect(() => {
    trigger(); // Validate the form on mount to show errors if fields are empty
  }, [trigger]);
  
  function splitStringToArray(inputString, delimiter) {
    // Check if the inputString is null or an empty string
    if (!inputString) {
      return [];
    }
    const dataArray = inputString.split(delimiter);
    // Remove the last empty string caused by the trailing delimiter
    const filteredArray = dataArray.filter(element => element !== "");
    return filteredArray;
  }

  useEffect(() => {
  if (initialRows) {
    //const addressArray = splitStringToArray(initialRows?.address,'|')
    const addressArray = splitStringToArray(initialRows?.address, '|');
    // Validate and set form values
    if (addressArray.length >= 8) {
      setValue("house", addressArray[0] || "");
      setValue("street", addressArray[5] || "");
      setValue("landMark", addressArray[3] || "");
      setValue("locality", addressArray[7] || "");
      setValue("subDistrict", addressArray[4] || "");
      setValue("district", addressArray[8] || "");
      setValue("state", addressArray[9] || "");
    } else {
      console.error("Address array does not have enough elements:", addressArray);
    }

    const zonedata = processSingleData(initialRows?.zone, headerLocale);
    const blockdata = processSingleData(initialRows?.ward, headerLocale);
    const warddata = processSingleData(initialRows?.subward, headerLocale);

    
    setValue("city", initialRows.city || "");
    setValue("zoneName", zonedata || "");
    setValue("pincode", initialRows.pinCode || "");
    setValue("blockName", blockdata || "");
    setValue("wardName", warddata || "");

    // Clear errors for fields that received initial values
    if (addressArray[0]) clearErrors("house");
    if (addressArray[5]) clearErrors("street");
    if (addressArray[3]) clearErrors("landMark");
    if (addressArray[7]) clearErrors("locality");
    if (addressArray[4]) clearErrors("subDistrict");
    if (addressArray[8]) clearErrors("district");
    if (addressArray[9]) clearErrors("state");
    if (initialRows.city) clearErrors("city");
    if (initialRows.pinCode) clearErrors("pincode");
    if (zonedata) clearErrors("zoneName");
    if (blockdata) clearErrors("blockName");
    if (warddata) clearErrors("wardName");
  }
}, [initialRows, setValue, headerLocale, clearErrors]);
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
                    />
                    {errors.street && <span style={{ color: "red" }}>{errors.street.message}</span>}
                  </div>
                )}
              />
            </LabelFieldPair>
          </div>
          <div className="bmc-col3-card">
            <LabelFieldPair>
              <CardLabel className="bmc-label">{"BMC_LANDMARK"}</CardLabel>
              <Controller
                control={control}
                name={"landMark"}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                    />
                    {errors.landMark && <span style={{ color: "red" }}>{errors.landMark.message}</span>}
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
                name="pincode"
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    <TextInput
                      disabled={!isEditable}
                      readOnly={!isEditable}
                      value={props.value}
                      onChange={(e) => props.onChange(e.target.value)}
                      onBlur={props.onBlur}
                    />
                    {errors.pincode && <span style={{ color: "red" }}>{errors.pincode.message}</span>}
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
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    {isEditable ? (
                      <Dropdown
                        placeholder={t("SELECT ZONE")}
                        selected={props.value}
                        select={(zone) => props.onChange(zone)}
                        onBlur={props.onBlur}
                        option={zones}
                        optionKey="i18nKey"
                        t={t}
                        isMandatory={true}
                      />
                    ) : (
                      <TextInput readOnly value={props.value?.label || ""} />
                    )}
                    {errors.zoneName && <span style={{ color: "red" }}>{errors.zoneName.message}</span>}
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
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <div>
                    {isEditable ? (
                      <Dropdown
                        placeholder={t("SELECT CASTE CATEGORY")}
                        selected={props.value}
                        select={(block) => props.onChange(block)}
                        onBlur={props.onBlur}
                        option={filteredBlocks}
                        optionKey="i18nKey"
                        t={t}
                        isMandatory={true}
                      />
                    ) : (
                      <TextInput readOnly value={props.value?.label || ""} />
                    )}
                    {errors.blockName && <span style={{ color: "red" }}>{errors.blockName.message}</span>}
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
                        option={filteredWards}
                        optionKey="i18nKey"
                        t={t}
                        isMandatory={true}
                      />
                    ) : (
                      <TextInput readOnly value={props.value?.label || ""} />
                    )}
                    {errors.wardName && <span style={{ color: "red" }}>{errors.wardName.message}</span>}
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
