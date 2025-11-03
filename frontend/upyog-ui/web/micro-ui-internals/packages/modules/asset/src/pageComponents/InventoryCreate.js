import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, InfoBannerIcon, Dropdown, TextArea } from "@upyog/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import Timeline from "../components/InventoryCreationTimeLine";
import { Controller, useForm } from "react-hook-form";
import FormGridWrapper from "../utils/FormGridWrapper";

const InventoryCreate = ({ t, config, onSelect, userType, formData }) => {
  const [inventory, setInventory] = useState({});

  const [categoriesWiseData, setCategoriesWiseData] = useState();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  const { control } = useForm();
  const { pathname: url } = useLocation();
  let index = 0;
  let validation = {};
  // get Registration no from db
  const { isLoading, isSuccess, isError, data: { ProcurementRequests: searchResult, Count: count } = {} } = Digit.Hooks.asset.useProcurementList({
    tenantId,
    filters: { status: "APPROVED" },
  });
  // get Vendor Details
  const { isLoading: vendorLoading, data: { Vendors: vendorResult } = {} } = Digit.Hooks.asset.useAssetVendorList({
    tenantId,
    filters: { status: "ACTIVE" },
  });
  const goNext = () => {
    let owner = formData.inventory && formData.inventory[index];
    inventory.owner = owner;
    onSelect(config.key, inventory, false, index);
  };

  // Insurance Applicability options
  const insuranceOptions = [
    { i18nKey: "YES", code: "YES", value: "Yes" },
    { i18nKey: "NO", code: "NO", value: "No" },
  ];
  const procurementModeOptions = [
    { i18nKey: "THROUGH_GEM", code: "THROUGH_GEM", value: "Through GeM" },
    { i18nKey: "THROUGH_EPROCUREMENT", code: "THROUGH_EPROCUREMENT", value: "Through eProcurement" },
    { i18nKey: "OTHER", code: "OTHER", value: "Other" },
  ];

  const onSkip = () => onSelect();
  const handleInputChange = (e, name) => {
    let fieldName, value;

    // Case 1: TextInput (event object with target)
    if (e?.target) {
      fieldName = e.target.name;
      value = e.target.value;
    }
    // Case 2: Dropdown (value passed + explicit name)
    else {
      fieldName = name;
      value = e;
    }

    setInventory((prevData) => {
      let updatedData = { ...prevData, [fieldName]: value };

      // Auto-fill fields when requestId is selected
      if (fieldName === "requestId" && value?.code) {
        const selectedRequest = searchResult?.find((req) => req.requestId === value.code);
        if (selectedRequest) {
          updatedData = {
            ...updatedData,
            item: selectedRequest.item,
            itemType: selectedRequest.itemType,
            quantity: selectedRequest.quantity,
            office: selectedRequest.tenantId,
            assetApplicationNumber: selectedRequest.assetApplicationNumber,
            entryDatetime: selectedRequest.auditDetails?.createdTime ? new Date(selectedRequest.auditDetails.createdTime).toLocaleString() : "",
          };
        }
      }

      return updatedData;
    });
  };

  // Create dropdown options from procurement requests
  const procurementOptions =
    searchResult?.map((item) => ({
      i18nKey: item.requestId,
      code: item.requestId,
      value: item.requestId,
    })) || [];

  // Create dropdown options from vendor list
  const vendorOptions =
    vendorResult?.map((vendor) => ({
      i18nKey: vendor.vendorName,
      code: vendor.vendorNumber,
      value: vendor.vendorName,
    })) || [];

  return (
    <React.Fragment>
      {window.location.href.includes("/employee") ? <Timeline currentStep={1} /> : null}
      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t}>
        <FormGridWrapper>
          <div>
            <CardLabel>
              {t("PROCUREMENT_REQUEST_ID")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name="requestId"
              defaultValue={inventory?.requestId}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={inventory?.requestId}
                  select={(e) => handleInputChange(e, "requestId")}
                  option={procurementOptions}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>

          <div>
            <CardLabel>
              {t("INV_OFFICE")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput t={t} type={"text"} name="office" value={inventory.office || ""} disabled={true} readOnly={true} />
          </div>

          <div>
            <CardLabel>
              {t("INV_CATEGORY")} <span style={{ color: "red" }}>*</span>
            </CardLabel>

            <TextInput t={t} type={"text"} name="item" value={inventory.item || ""} disabled={true} readOnly={true} />
          </div>
          <div>
            <CardLabel>
              {t("INV_ITEM_NAME")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput t={t} type={"text"} name="itemType" value={inventory.itemType || ""} disabled={true} readOnly={true} />
          </div>

          <div>
            <CardLabel>
              {t("INV_ITEM_DESCRIPTION")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type="text"
              isMandatory={false}
              optionKey="i18nKey"
              name="itemDescription"
              value={inventory.itemDescription || ""}
              onChange={handleInputChange}
              ValidationRequired={false}
              validation={{
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              }}
            />
          </div>

          <div>
            <CardLabel>
              {t("INV_ATTRIBUTES")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="attributes"
              value={inventory.attributes || ""}
              placeholder={t("Enter Asset Description")}
              ValidationRequired={false}
              onChange={handleInputChange}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>

          <div>
            <CardLabel>
              {t("INV_QUANTITY")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput t={t} type={"text"} name="quantity" value={inventory.quantity || ""} disabled={true} readOnly={true} />
          </div>

          <div>
            <CardLabel>
              {t("INV_UAIN")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"text"}
              name="assetApplicationNumber"
              value={inventory.assetApplicationNumber || ""}
              disabled={true}
              readOnly={true}
            />
          </div>

          <div>
            <CardLabel>
              {t("INV_ENTRY_DATETIME")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput t={t} type={"text"} name="entryDatetime" value={inventory.entryDatetime || ""} disabled={true} readOnly={true} />
          </div>
          <div>
            <CardLabel>
              {t("INV_PURCHASE_DATE")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="purchaseDate"
              value={inventory.purchaseDate || ""}
              ValidationRequired={false}
              onChange={handleInputChange}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_PURCHASE_MODE")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name={"purchaseMode"}
              defaultValue={inventory?.purchaseMode}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={inventory?.purchaseMode}
                  select={(e) => handleInputChange(e, "purchaseMode")}
                  option={procurementModeOptions}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_VENDOR_DETAILS")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name="vendorDetails"
              defaultValue={inventory?.vendorDetails}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={inventory?.vendorDetails}
                  select={(e) => handleInputChange(e, "vendorDetails")}
                  option={vendorOptions}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_DELIVERY_DATE")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"date"}
              isMandatory={false}
              optionKey="i18nKey"
              name="deliveryDate"
              value={inventory.deliveryDate || ""}
              ValidationRequired={false}
              onChange={handleInputChange}
              rules={{
                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
              }}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_END_OF_LIFE")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="endOfLife"
              value={inventory.endOfLife || ""}
              onChange={handleInputChange}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_END_OF_SUPPORT")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="endOfSupport"
              value={inventory.endOfSupport || ""}
              onChange={handleInputChange}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_UNIT_PRICE")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="unitPrice"
              value={inventory.unitPrice || ""}
              onChange={handleInputChange}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_TOTAL_PRICE")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="totalPrice"
              value={inventory.totalPrice || ""}
              onChange={handleInputChange}
              ValidationRequired={false}
              {...(validation = {
                isRequired: true,
                pattern: "^[a-zA-Z0-9/-]*$",
                type: "text",
                title: t("PT_NAME_ERROR_MESSAGE"),
              })}
            />
          </div>
          <div>
            <CardLabel>
              {t("INV_INSURANCE_APPLICABILITY")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name={"insuranceApplicability"}
              defaultValue={inventory?.insuranceApplicability}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={inventory?.insuranceApplicability}
                  select={(e) => handleInputChange(e, "insuranceApplicability")}
                  option={insuranceOptions}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>
        </FormGridWrapper>
      </FormStep>
    </React.Fragment>
  );
};

export default InventoryCreate;
