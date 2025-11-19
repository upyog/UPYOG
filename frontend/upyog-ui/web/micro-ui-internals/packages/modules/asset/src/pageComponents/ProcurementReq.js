import React, { useEffect, useState } from "react";
import { FormStep, TextInput, CardLabel, InfoBannerIcon, Dropdown, TextArea } from "@upyog/digit-ui-react-components";
import { useLocation } from "react-router-dom";
import ProcerementTimeline from "../components/ProcerementTimeline";
import { Controller, useForm } from "react-hook-form";

const ProcurementReq = ({ t, config, onSelect, userType, formData }) => {
  const [procurementReq, setProcurementReq] = useState({});
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  const { control } = useForm();
  const { pathname: url } = useLocation();
  let index = 0;
  let validation = {};

  const { data: Menu_Asset } = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSET", [{ name: "InventoryCategories" }], {
    select: (data) => {
      const formattedData = data?.["ASSET"]?.["InventoryCategories"];
      const activeData = formattedData?.filter((item) => item.active === true);
      return activeData;
    },
  });

  const {  isSuccess, isError, data: { Assets: searchReult, Count: count } = {} } = Digit.Hooks.asset.useASSETSearch(
    { tenantId, filters: {} },
    config
  );

  console.log("listOFIdentificationNO:- ", searchReult);
  let listOFIdentificationNO = [];
  searchReult &&
    searchReult.map((asset) => {
      listOFIdentificationNO.push({ i18nKey: `${asset?.applicationNo}`, value: `${asset?.applicationNo}`, code: `${asset?.applicationNo}` });
    });

  let item_type = [];
  Menu_Asset &&
    Menu_Asset.map((asset_mdms) => {
      item_type.push({ i18nKey: `${asset_mdms.name}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}`, subItems: asset_mdms.subItems });
    });

  const goNext = () => {
    let owner = formData.procurementReq && formData.procurementReq[index];
    procurementReq.owner = owner;
    onSelect(config.key, procurementReq, false, index);
  };

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
    
    setProcurementReq((prevData) => {
      const updatedData = { ...prevData, [fieldName]: value };
      return updatedData;
    });
  };
  // Get subcategory data based on selected parent category
  const getSubCategoryData = () => {
    if (!procurementReq?.parentCategory?.code) return [];
    
    const selectedParent = Menu_Asset?.find(cat => cat.code === procurementReq.parentCategory.code);
    if (!selectedParent?.subItems) return [];
    
    return selectedParent.subItems
      .filter(item => item.active)
      .map(item => ({
        i18nKey: item.name,
        code: item.code,
        value: item.name
      }));
  };

  // Clear subcategory when parent category changes
  useEffect(() => {
    if (procurementReq?.parentCategory) {
      setProcurementReq(prev => ({ ...prev, subCategory: null }));
    }
  }, [procurementReq?.parentCategory]);

  return (
    <React.Fragment>
      {window.location.href.includes("/employee") ? <ProcerementTimeline currentStep={1} /> : null}
      <FormStep config={config} onSelect={goNext} onSkip={onSkip} t={t}>
        <style>
          {`
            .responsive-form-grid {
              display: grid;
              grid-template-columns: repeat(3, 1fr);
              gap: 16px;
              margin-bottom: 16px;
            }
            .responsive-form-grid > div {
              width: 100%;
            }
            .responsive-form-grid .form-field {
              width: 100% !important;
              min-width: 100%;
            }
            @media (max-width: 1024px) {
              .responsive-form-grid {
                grid-template-columns: repeat(2, 1fr);
              }
            }
            @media (max-width: 768px) {
              .responsive-form-grid {
                grid-template-columns: 1fr;
              }
            }
          `}
        </style>

        <div className="responsive-form-grid">
          <div>
            <CardLabel>
              {t("PROC_ITEM_PARENTCAT")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name="parentCategory"
              defaultValue={procurementReq?.parentCategory}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={procurementReq?.parentCategory}
                  select={(e) => handleInputChange(e, "parentCategory")}
                  option={item_type}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>

          <div>
            <CardLabel>
              {t("PROC_ITEM_SUBCAT")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name="subCategory"
              defaultValue={procurementReq?.subCategory}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={procurementReq?.subCategory}
                  select={(e) => handleInputChange(e, "subCategory")}
                  option={getSubCategoryData()}
                  optionKey="i18nKey"
                  placeholder={"Select"}
                  t={t}
                />
              )}
            />
          </div>

          <div>
            <CardLabel>
              {t("PROC_QUANTITY")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <TextInput
              t={t}
              type={"text"}
              isMandatory={false}
              optionKey="i18nKey"
              name="quantity"
              value={procurementReq.quantity || ""}
              placeholder={t("Enter quantity")}
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
              {t("PROC_IDENTIFICATION_NO")} <span style={{ color: "red" }}>*</span>
            </CardLabel>
            <Controller
              control={control}
              name="assetApplicationNumber"
              defaultValue={procurementReq?.assetApplicationNumber}
              rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
              render={(props) => (
                <Dropdown
                  className="form-field"
                  selected={procurementReq?.assetApplicationNumber}
                  select={(e) => handleInputChange(e, "assetApplicationNumber")}
                  option={listOFIdentificationNO}
                  optionKey="i18nKey"
                  placeholder={"Search UAIN"}
                  t={t}
                />
              )}
            />
            
          </div>
        </div>
      </FormStep>
    </React.Fragment>
  );
};

export default ProcurementReq;
