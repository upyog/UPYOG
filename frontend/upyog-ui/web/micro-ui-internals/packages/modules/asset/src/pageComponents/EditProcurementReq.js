import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, Dropdown } from "@upyog/digit-ui-react-components";
import { Controller, useForm } from "react-hook-form";
import FormGridWrapper from "../utils/FormGridWrapper";

const EditProcurementReq = ({ t, config, onSelect, userType, formData }) => {
  const procurementReqData = formData?.procurementReq || {};
  const [procurementReq, setProcurementReq] = useState(procurementReqData);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  const { control } = useForm();
  let index = 0;

  const { data: Menu_Asset } = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSET", [{ name: "InventoryCategories" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["InventoryCategories"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });

  let item_type = [];
  Menu_Asset &&
    Menu_Asset.map((asset_mdms) => {
      item_type.push({ i18nKey: `${asset_mdms.name}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
    });
  let procurementStatus = [
    { i18nKey: "APPROVED", code: "APPROVED", value: "APPROVED" },
    { i18nKey: "REJECTED", code: "REJECTED", value: "REJECTED" }
  ];

  // Initialize FormComposer data on first render
  useEffect(() => {
    if (onSelect && config && procurementReq) {
      onSelect(config.key, { editProcurementReq: procurementReq });
    }
  }, []);

  // Pre-fill dropdowns based on form data
  useEffect(() => {
    if (procurementReq?.item && item_type.length > 0) {
      // Find matching category for item field
      const matchingCategory = item_type.find((cat) => cat.code === procurementReq.item);

      if (matchingCategory) {
        // Find matching subcategory for itemType field
        let matchingSubCategory = null;
        if (procurementReq?.itemType) {
          const originalCategory = Menu_Asset?.find((cat) => cat.code === procurementReq.item);
          if (originalCategory?.subItems) {
            const subItem = originalCategory.subItems.find((sub) => sub.code === procurementReq.itemType && sub.active);
            if (subItem) {
              matchingSubCategory = {
                i18nKey: subItem.name,
                code: subItem.code,
                value: subItem.name,
              };
            }
          }
        }

        const updatedData = {
          ...procurementReq,
          item: matchingCategory,
          itemType: matchingSubCategory,
        };

        setProcurementReq(updatedData);

        // Update FormComposer immediately
        if (onSelect && config) {
          onSelect(config.key, { editProcurementReq: updatedData });
        }
      }
    }
  }, [item_type.length, procurementReq?.item, procurementReq?.itemType]);

  const goNext = () => {
    let owner = formData.procurementReq && formData.procurementReq[index];
    procurementReq.owner = owner;
    onSelect(config.key, procurementReq, false, index);
  };

  const onSkip = () => onSelect();

  const handleInputChange = (e, name) => {
    const fieldName = e?.target ? e.target.name : name;
    const value = e?.target ? e.target.value : e;

    const updatedData = { ...procurementReq, [fieldName]: value };
    setProcurementReq(updatedData);

    // Update FormComposer data using onSelect like EditAssetVendor
    if (onSelect && config) {
      onSelect(config.key, { editProcurementReq: updatedData });
    }
  };

  const getSubCategoryData = () => {
    if (!procurementReq?.item?.code) return [];
    const selectedParent = Menu_Asset?.find((cat) => cat.code === procurementReq.item.code);
    return (
      selectedParent?.subItems
        ?.filter((item) => item.active)
        .map((item) => ({
          i18nKey: item.name,
          code: item.code,
          value: item.name,
        })) || []
    );
  };

  return (
    <React.Fragment>
      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t}>
        <FormGridWrapper>
          <div>
            <CardLabel>{t("PROC_ITEM_PARENTCAT")}</CardLabel>
            <Controller
              control={control}
              name="item"
              defaultValue={procurementReq?.item}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={procurementReq?.item}
                  select={(e) => handleInputChange(e, "item")}
                  option={item_type}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>

          <div>
            <CardLabel>{t("PROC_ITEM_SUBCAT")}</CardLabel>
            <Controller
              control={control}
              name="itemType"
              defaultValue={procurementReq?.itemType}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={procurementReq?.itemType}
                  select={(e) => handleInputChange(e, "itemType")}
                  option={getSubCategoryData()}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>

          <div>
            <CardLabel>{t("PROC_QUANTITY")}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              name="quantity"
              value={procurementReq.quantity || ""}
              placeholder={t("Enter quantity")}
              onChange={handleInputChange}
              disabled={true}
              readOnly={true}
            />
          </div>
          <div>
            <CardLabel>{t("PROC_IDENTIFICATION_NO")}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              name="assetApplicationNumber"
              value={procurementReq.assetApplicationNumber || ""}
              onChange={handleInputChange}
              disabled={true}
              readOnly={true}
            />
          </div>
          <div>
            <CardLabel>{t("INVENTORY_REQUEST_ID")}</CardLabel>
            <TextInput
              t={t}
              type={"text"}
              name="requestId"
              value={procurementReq.requestId || ""}
              disabled={true}
              readOnly={true}
              onChange={handleInputChange}
            />
          </div>
          <div>
            <CardLabel>{t("PROCUREMENT_STATUS")}</CardLabel>
            <Controller
              control={control}
              name="status"
              defaultValue={procurementReq?.status}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={procurementReq?.status}
                  select={(e) => handleInputChange(e, "status")}
                  option={procurementStatus}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>
          {procurementReq?.status?.code === "APPROVED" && procurementReq?.status?.code === "APPROVED" 
          ? (
            <div>
              <CardLabel>{t("PROCUREMENT_DESCRIPTION")}</CardLabel>
              <TextInput
                t={t}
                type={"text"}
                name="description"
                value={procurementReq.description || ""}
                onChange={handleInputChange}
              />
            </div>
          ) : (
            ""
          )}
        </FormGridWrapper>
      </FormStep>
    </React.Fragment>
  );
};

export default EditProcurementReq;
