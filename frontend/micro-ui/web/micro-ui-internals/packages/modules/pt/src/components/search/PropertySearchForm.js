import { CardLabelError, SearchField, SearchForm, SubmitBar, TextInput,Localities,MobileNumber, Dropdown } from "@egovernments/digit-ui-react-components";
import React, { useState } from "react";
import { Controller, useForm } from "react-hook-form";


const SwitchComponent = (props) => {
  let searchBy = props.searchBy;
  return (
    <div className="w-fullwidth PropertySearchFormSwitcher">
      {props.keys.map((key) => (
        <span key={key} className={searchBy === key ? "selected" : "non-selected"} onClick={() => {key === "searchDetail" && !(sessionStorage.getItem("searchDetailValue"))?sessionStorage.setItem("searchDetailValue",1):""; key==="searchId" && sessionStorage.getItem("searchDetailValue") == 1?sessionStorage.setItem("searchDetailValue",2):"";   props.onSwitch(key);props.onReset(); }}>
          {props.t(`PT_SEARCH_BY_${key?.toUpperCase()}`)}
        </span>
      ))}
    </div>
  );
};
const SearchPTID = ({ tenantId, t, onSubmit, onReset, searchBy, PTSearchFields, setSearchBy ,payload}) => {
  const { register, control, handleSubmit, setValue, watch,getValues, reset, formState } = useForm({
    defaultValues: {
      ...payload,
        }
  });
  const stateId = Digit.ULBService.getStateId();
  const { data: usageMenu = {}, isLoading } = Digit.Hooks.pt.usePropertyMDMSV2(stateId, "PropertyTax", [
    "UsageCategory",
    "OccupancyType",
    "Floor",
    "OwnerType",
    "OwnerShipCategory",
    "Documents",
    "SubOwnerShipCategory",
    "OwnerShipCategory",
  ]);
  let usagecat = [];
  //usagecat = usageMenu?.PropertyTax?.UsageCategory?.filter((e) => e?.code !== "MIXED") || [];
  const usageCategoryMajorMenu = () => {
    console.log("usageCategoryMajorMenu")
    const catMenu= [
      {
          "code": "RESIDENTIAL",
          "name": "RESIDENTIAL",
          "i18nKey": "PROPERTYTAX_BILLING_SLAB_RESIDENTIAL",
          "label": "PropertyType"
      },
      {
          "code": "NONRESIDENTIAL.COMMERCIAL",
          "name": "NONRESIDENTIAL.COMMERCIAL",
          "i18nKey": "PROPERTYTAX_BILLING_SLAB_COMMERCIAL",
          "label": "PropertyType"
      },
      {
          "code": "NONRESIDENTIAL.INDUSTRIAL",
          "name": "NONRESIDENTIAL.INDUSTRIAL",
          "i18nKey": "PROPERTYTAX_BILLING_SLAB_INDUSTRIAL",
          "label": "PropertyType"
      },
      {
          "code": "NONRESIDENTIAL.INSTITUTIONAL",
          "name": "NONRESIDENTIAL.INSTITUTIONAL",
          "i18nKey": "PROPERTYTAX_BILLING_SLAB_INSTITUTIONAL",
          "label": "PropertyType"
      },
      {
          "code": "NONRESIDENTIAL.OTHERS",
          "name": "NONRESIDENTIAL.OTHERS",
          "i18nKey": "PROPERTYTAX_BILLING_SLAB_OTHERS",
          "label": "PropertyType"
      }
  ]
    // const catMenu = usagecat
    //   ?.filter((e) => e?.code.split(".").length <= 2 && e?.code !== "NONRESIDENTIAL")
    //   ?.map((item) => {
    //     const arr = item?.code.split(".");
    //     if (arr.length == 2) return { i18nKey: "PROPERTYTAX_BILLING_SLAB_" + arr[1], code: item?.code };
    //     else return { i18nKey: "PROPERTYTAX_BILLING_SLAB_" + item?.code, code: item?.code };
    //   });
    console.log("usageCategoryMajorMenu",catMenu)
    return catMenu;
  };
  const [usageType, setUsageType] = useState();
  let formValue = watch();
  const fields = PTSearchFields?.[searchBy] || {};
  sessionStorage.removeItem("revalidateddone");
  console.log("payload",payload,formValue)
 const setProptype =(e)=>{
  console.log("e",e.code)
  setUsageType
  formValue.propertyType= "bbb"
 }
  return (
    <div className="PropertySearchForm">
      <SearchForm onSubmit={onSubmit} className={"pt-property-search"} handleSubmit={handleSubmit}>
        <SwitchComponent keys={Object.keys(PTSearchFields || {})} searchBy={searchBy} onReset={onReset} t={t} onSwitch={setSearchBy} />
        {fields &&
          Object.keys(fields).map((key) => {
            let field = fields[key];
            let validation = field?.validation || {};
            return (
              <SearchField key={key} className={"pt-form-field"}>
                <label>{t(field?.label)}{`${field?.validation?.required?"*":""}`}</label>
                {field?.type==="custom"? 
                <Controller
                 name= {key}
                defaultValue={formValue?.[key]}
                rules= {field.validation}
                control={control}
                render={(props, customProps) => (
                  <field.customComponent
                    selectLocality={(d) => {
                      props.onChange(d);
                    }}
                    tenantId={tenantId}
                    selected={formValue?.[key]}
                    {...field.customCompProps}
                  />
                )}
                />
            :field?.type === "number"?
            <div>
            <MobileNumber
              name="mobileNumber"
              inputRef={register({
                value: getValues(key),
                shouldUnregister: true,
                ...validation,
              })}
              type="number"
              componentInFront={<div className="employee-card-input employee-card-input--front">+91</div>}
              //maxlength={10}
        />
        </div>
        :field.type === "propertyType"?
        <div>
        <Dropdown
          className="form-field"
          selected={usageType}
          disable={false}
          defaultValue={formValue?.[key]}
          name={key}
          option={usageCategoryMajorMenu()}
          select={(e) => setProptype(e)}
          inputRef={register({
            value: getValues(key),
            shouldUnregister: true,
          })}
          
          optionKey="i18nKey"
          t={t}
        />
         </div>
        :
            <TextInput
                  name={key}
                  type={field?.type}
                  inputRef={register({
                    value: getValues(key),
                    shouldUnregister: true,
                    ...validation,
                  })}
                />}
                <CardLabelError style={{ marginTop: "-10px", marginBottom: "-10px" }}>{t(formState?.errors?.[key]?.message)}</CardLabelError>
              </SearchField>
            );
          })}

       <div className="pt-search-action" >
         <SearchField  className="pt-search-action-reset">
         <p style={{color:"#a82227"}}
            onClick={() => {
              onReset({});
            }}
          >
            {t(`ES_COMMON_CLEAR_ALL`)}
          </p>
           </SearchField>
       <SearchField className="pt-search-action-submit">
          <SubmitBar label={t("ES_COMMON_SEARCH")} submit />
        </SearchField>
       </div>
      </SearchForm>
    </div>
  );
};

export default SearchPTID;
