import { CardLabel, Dropdown, FormStep, LinkButton, Loader, TextInput, DeleteIcon } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState ,Fragment} from "react";
import Timeline from "../components/TLTimeline";


const getUsageCategory = (usageCategory = "") => {
  let categoryArray = usageCategory?.split(".") || [];
  let tempObj = {};
  tempObj["usageCategoryMajor"] = categoryArray && categoryArray.length > 0 && categoryArray[0];
  tempObj["usageCategoryMinor"] = categoryArray && categoryArray.length > 1 && categoryArray[1];
  tempObj["usageCategorySubMinor"] = categoryArray && categoryArray.length > 2 && categoryArray[2];
  tempObj["usageCategoryDetail"] = categoryArray && categoryArray.length > 3 && categoryArray[3];
  return tempObj;
};

const formatUnits = (units = [], currentFloor, isFloor) => {
  if (!units || units.length == 0) {
    return [
      {
        usageCategory: "",
        unitType: "",
        occupancyType: "",
        structureType: "",
        ageOfProperty: "",
        builtUpArea: null,
        arv: "",
        floorNo: isFloor ? { code: currentFloor, i18nKey: `PROPERTYTAX_FLOOR_${currentFloor}` } : "",
      },
    ];
  }
  return units.map((unit) => {
    let usageCategory = unit?.usageCategory && !(unit?.usageCategory?.includes("NONRESIDENTIAL")) ?  "RESIDENTIAL" : getUsageCategory(unit?.usageCategory)?.usageCategoryMinor;
    return {
      ...unit,
      builtUpArea: unit?.constructionDetail?.builtUpArea,
      usageCategory: unit?.usageCategory ? { code: unit?.usageCategory, i18nKey: `PROPERTYTAX_${unit?.usageCategory}` } : {},
      occupancyType: unit?.occupancyType ? { code: unit.occupancyType, i18nKey: `PROPERTYTAX_OCCUPANCYTYPE_${unit?.occupancyType}` } : "",
      structureType: unit?.structureType ? { code: unit.structureType, i18nKey: `PROPERTYTAX_STRUCTURETYPE_${unit?.structureType}` } : "",
      ageOfProperty: unit?.ageOfProperty ? { code: unit.ageOfProperty, i18nKey: `PROPERTYTAX_AGEOFPROPERTY_${unit?.ageOfProperty}` } : "",
      floorNo: unit?.floorNo || Number.isInteger(unit?.floorNo) ? { code: unit.floorNo, i18nKey: `PROPERTYTAX_FLOOR_${unit?.floorNo}` } : {},
      unitType: unit?.unitType ? { code: unit.unitType, i18nKey: `PROPERTYTAX_BILLING_SLAB_${unit?.unitType?.code || unit?.unitType}` } : "",
    };
  });
};
const SelectPTUnits = React.memo(({ t, config, onSelect, userType, formData }) => {
  let validation = {};
  let path = window.location.pathname.split("/");
  let currentFloor = Number(path[path.length - 1]);
  const [builtUpAreaError, setBuilUpAreaError] = useState(false);
  let isFloor = window.location.pathname.includes("new-application/units") || window.location.pathname.includes("/edit-application/units");
  const [fields, setFields] = useState(
    formatUnits(isFloor ? formData?.units?.filter((ee) => ee.floorNo == currentFloor) : formData?.units, currentFloor, isFloor)
  );

  useEffect(() => {
    setFields(() => formatUnits(isFloor ? formData?.units?.filter((ee) => ee.floorNo == currentFloor) : formData?.units, currentFloor, isFloor));
    return () => {
      setFields(null);
    };
  }, [currentFloor, formData, isFloor]);

  const getheader = () => {
    if (formData?.PropertyType?.i18nKey === "COMMON_PROPTYPE_BUILTUP_SHAREDPROPERTY") {
      return "PT_FLAT_DETAILS_HEADER";
    } else {
      return `PROPERTYTAX_FLOOR_${currentFloor}_DETAILS`;
    }
  };

  const { data: mdmsData, isLoading } = Digit.Hooks.useCommonMDMS(
    Digit.ULBService.getStateId(),
    "PropertyTax",
    ["Floor", "OccupancyType", "UsageCategory", "StructureType", "AgeOfProperty"],
    {
      select: (data) => {
        // let usageCategory = data?.PropertyTax?.UsageCategory?.map((category) => getUsageCategory(category.code))
        //   .filter(
        //     (category) => category.usageCategoryDetail === false && category.usageCategorySubMinor === false && category.usageCategoryMinor !== false
        //   )
        //   .map((category) => ({ code: category.usageCategoryMinor, i18nKey: `PROPERTYTAX_BILLING_SLAB_${category.usageCategoryMinor}` }));
        // let subCategory = Digit.Utils.getUnique(
        //   data?.PropertyTax?.UsageCategory.map((e) => getUsageCategory(e.code))
        //     .filter((e) => e.usageCategoryDetail)
        //     .map((e) => ({
        //       code: e.usageCategoryDetail,
        //       i18nKey: `PROPERTYTAX_BILLING_SLAB_${e.usageCategoryDetail}`,
        //       usageCategorySubMinor: e.usageCategorySubMinor,
        //       usageCategoryMinor: e.usageCategoryMinor,
        //     }))
        // );

        return {
          UsageCategory: data?.PropertyTax?.UsageCategory?.filter((category) => category.active)?.map((category) => ({
            i18nKey: `PROPERTYTAX_${category.code}`,
            code: category.code,
          })),
          Floor: data?.PropertyTax?.Floor?.filter((floor) => floor.active)?.map((floor) => ({
            i18nKey: `PROPERTYTAX_FLOOR_${floor.code}`,
            code: floor.code,
          })),
          OccupancyType: data?.PropertyTax?.OccupancyType?.filter((occupancy) => occupancy.active)?.map((occupancy) => ({
            i18nKey: `PROPERTYTAX_OCCUPANCYTYPE_${occupancy.code}`,
            code: occupancy.code,
          })),
          StructureType: data?.PropertyTax?.StructureType?.filter((structure) => structure.active)?.map((structure) => ({
            i18nKey: `PROPERTYTAX_STRUCTURETYPE_${structure.code}`,
            code: structure.code,
          })),
          AgeOfProperty: data?.PropertyTax?.AgeOfProperty?.filter((age) => age.active)?.map((age) => ({
            i18nKey: `PROPERTYTAX_AGEOFPROPERTY_${age.code}`,
            code: age.code,
          })),
          // UsageCategory: usageCategory,
          // UsageSubCategory: subCategory,
          usageDetails: data?.PropertyTax?.UsageCategory,
        };
      },
      retry: false,
      enable: false,
    }
  );

  function handleAdd() {
    const values = [...fields];
    values.push({
      usageCategory: "",
      unitType: "",
      occupancyType: "",
      structureType: "",
      ageOfProperty: "",
      builtUpArea: null,
      arv: "",
      floorNo: isFloor ? { code: currentFloor, i18nKey: `PROPERTYTAX_FLOOR_${currentFloor}` } : "",
    });
    setFields(values);
  }

  function handleRemove(index) {
    const values = [...fields];
    if (values.length != 1) {
      values.splice(index, 1);
      setFields(values);
    }
  }

  function selectSubUsageCategory(i, value) {
    let units = [...fields];
    units[i].unitType = value;

    setFields(units);
  }

  function selectUsageCategory(i, value) {
    let units = [...fields];
    units[i].usageCategory = value;
    units[i].unitType = "";
    setFields(units);
  }
  function selectFloor(i, value) {
    let units = [...fields];
    units[i].floorNo = value;

    setFields(units);
  }
  function selectOccupancy(i, value) {
    let units = [...fields];
    units[i].occupancyType = value;

    setFields(units);
  }
  function selectStructure(i, value) {
    let units = [...fields];
    units[i].structureType = value;

    setFields(units);
  }
  function selectAgeOfProperty(i, value) {
    let units = [...fields];
    units[i].ageOfProperty = value;

    setFields(units);
  }
  function onChangeRent(i, e) {
    let units = [...fields];
    units[i].arv = e.target.value;
    setFields(units);
  }
  function onChangeArea(i, e) {
    let units = [...fields];
    let value = e.target.value;
    // Allow only numbers with up to 2 decimals
    value = value.replace(/^(\d+(\.\d{0,2})?).*$/, "$1");
    units[i].builtUpArea = value;
    setFields(units);
  }

  const goNext = () => {
    let units = formData?.units || [];

    let unitsdata = fields.map((field) => {
      if(typeof field?.unitType?.code === "object" && field?.unitType?.code?.code)
      {
        field["unitType"].code = field?.unitType?.code?.code;
      }
      let unit = {};
      Object.keys(field)
        .filter((key) => field[key])
        .map((key) => {
          if(typeof field["unitType"] == "object" && field["unitType"].code == undefined)
          {
            field["unitType"] = "";
          }
          if (key === "usageCategory") {
            unit["usageCategory"] = mdmsData?.usageDetails.find(
              (e) =>
                e.code.includes(field[key]?.code) &&
                e.code.includes(typeof field["unitType"] === "object" ? field["unitType"]?.code : field["unitType"])
            )?.code;
          } else if (key === "builtUpArea") {
            unit["constructionDetail"] = { builtUpArea: field[key] };
          } else {
            unit[key] = typeof field[key] == "object" ? field[key]?.code : field[key];
          }
        });
      return unit;
    });
    if (isFloor) {
      units = units?.filter((e) => e.floorNo != currentFloor);
      unitsdata = [...units, ...unitsdata];
    }
    // console.log("unitsData==",unitsdata)
    let totalBuiltupArea = 0;
    if(unitsdata && unitsdata?.length>0) {
      
      unitsdata.map((e)=>{
        let builtupArea = +e?.constructionDetail?.builtUpArea
        if(currentFloor==e?.floorNo){
          totalBuiltupArea = totalBuiltupArea + builtupArea
        }
        
      });
      // console.log("totalBuiltupArea==",totalBuiltupArea,formData)
    }
    // if(totalBuiltupArea > +formData?.landArea?.floorarea) {
    //   setBuilUpAreaError(true);
    //   return;
    // } else {
    //   setBuilUpAreaError(false);
    // }
    if (currentFloor === formData?.noOfFloors?.code || !isFloor) {
      // if(formData?.noOofBasements?.code==1){
      //   onSelect(config.key, unitsdata,false,-1,true);
      // }else if(formData?.noOofBasements?.code==2){
      //   onSelect(config.key, unitsdata,false,(currentFloor===formData?.noOfFloors?.code)?-1:-2,true);
      if (formData?.noOofBasements?.code > 0) {
        onSelect(config.key, unitsdata, false, -1, true);
      } else {
        onSelect(config.key, unitsdata);
      }
    } else {
      if (currentFloor == -1 && formData?.noOofBasements?.code == 2) {
        onSelect(config.key, unitsdata, false, -2, true);
      } else if (currentFloor < 0) {
        onSelect(config.key, unitsdata);
      } else {
        onSelect(config.key, unitsdata, false, currentFloor + 1, true);
      }
    }
  };

  const onSkip = () => onSelect();
  if (isLoading) {
    return <Loader />;
  }

  function isAllowedNext (){
    let valueNotthere=0;
    fields && fields?.map((ob) => {
      
      if((!(ob?.usageCategory) || Object.keys(ob?.usageCategory) == 0) || !(ob?.occupancyType) || !(ob?.builtUpArea) /* || (!(ob?.floorNo)|| Object.keys(ob?.floorNo) == 0 )*/)
      valueNotthere=1;
      // else if(!(ob?.usageCategory?.code === "RESIDENTIAL"))
      // valueNotthere=1;
      else if(ob?.occupancyType?.code === "RENTED" && !(ob?.arv))
      valueNotthere=1;
    })
    if(valueNotthere == 0)
    return false;
    else 
    return true;
  }

  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={1}/> : null}
    <FormStep
    config={((config.texts.header = getheader()), config)}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={isAllowedNext()}
    >
      {fields.map((field, index) => {
        return (
          <div key={`${field}-${index}`}>
            <div
              style={{
                border: "solid",
                borderRadius: "5px",
                padding: "10px",
                paddingTop: "20px",
                marginTop: "10px",
                borderColor: "#f3f3f3",
                background: "#FAFAFA",
              }}
            >
              <LinkButton
                label={<DeleteIcon   style={{ float: "right", position: "relative" }}
                fill={!(fields.length === 1) ? "#494848" : "#FAFAFA"} />}
                style={{ width: "100px", display: "inline" }}
                onClick={(e) => handleRemove(index)}
              />
              <CardLabel>{`${t("PT_FORM2_USAGE_TYPE")}*`}</CardLabel>
              <Dropdown
                t={t}
                optionKey="i18nKey"
                isMandatory={config.isMandatory}
                option={[
                  ...(mdmsData?.UsageCategory ? mdmsData?.UsageCategory : []),
                ]}
                selected={field?.usageCategory}
                select={(e) => selectUsageCategory(index, e)}
              />
              {/* {field?.usageCategory?.code && field.usageCategory.code.includes("RESIDENTIAL") === false && (
                <>
                  <CardLabel>{`${t("PT_FORM2_SUB_USAGE_TYPE")}*`}</CardLabel>
                  <div className={"form-pt-dropdown-only"}>
                    <Dropdown
                      t={t}
                      optionKey="i18nKey"
                      isMandatory={config.isMandatory}
                      option={mdmsData?.UsageSubCategory?.filter((category) => category.usageCategoryMinor === field?.usageCategory?.code)}
                      selected={field?.unitType}
                      select={(e) => selectSubUsageCategory(index, e)}
                    />
                  </div>
                </>
              )} */}
              <CardLabel>{`${t("PT_FORM2_STRUCTURE_TYPE")}*`}</CardLabel>
              <div className={"form-pt-dropdown-only"}>
                <Dropdown
                  t={t}
                  optionKey="i18nKey"
                  isMandatory={config.isMandatory}
                  option={mdmsData?.StructureType}
                  selected={field?.structureType}
                  select={(e) => selectStructure(index, e)}
                />
              </div>
              <CardLabel>{`${t("PT_FORM2_OCCUPANCY")}*`}</CardLabel>
              <div className={"form-pt-dropdown-only"}>
                <Dropdown
                  t={t}
                  optionKey="i18nKey"
                  isMandatory={config.isMandatory}
                  option={mdmsData?.OccupancyType}
                  selected={field?.occupancyType}
                  select={(e) => selectOccupancy(index, e)}
                />
              </div>
              {field?.occupancyType?.code && field.occupancyType.code.includes("RENTED") && (
                <>
                  <CardLabel>{`${t("PT_FORM2_TOTAL_ANNUAL_RENT")}*`}</CardLabel>
                  <TextInput
                    style={{ background: "#FAFAFA" }}
                    t={t}
                    type={"text"}
                    isMandatory={false}
                    optionKey="i18nKey"
                    name="arv"
                    value={field?.arv}
                    onChange={(e) => onChangeRent(index, e)}
                    {...{
                      isRequired: true,
                      pattern: "[0-9]+",
                      type: "text",
                      title: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    }}
                  />
                </>
              )}
              <CardLabel>{`${t("PT_FORM2_AGE_OF_PROPERTY")}*`}</CardLabel>
              <div className={"form-pt-dropdown-only"}>
                <Dropdown
                  t={t}
                  optionKey="i18nKey"
                  isMandatory={config.isMandatory}
                  option={mdmsData?.AgeOfProperty}
                  selected={field?.ageOfProperty}
                  select={(e) => selectAgeOfProperty(index, e)}
                />
              </div>
              <CardLabel>{formData?.PropertyType?.i18nKey === "COMMON_PROPTYPE_BUILTUP_SHAREDPROPERTY" ? `${t("PT_FORM2_BUILT_UP_AREA")}*`:`${t("PT_BUILT_UP_AREA_HEADER")}*`}</CardLabel>
              <TextInput
                style={{ background: "#FAFAFA" }}
                t={t}
                type={"number"}
                isMandatory={false}
                optionKey="i18nKey"
                step="0.01"
                name="builtUpArea"
                value={field?.builtUpArea || ""}
                onChange={(e) => onChangeArea(index, e)}
                {...(validation = { pattern: "^([0-9]){0,8}$", isRequired: true, type: "number", title: t("PT_PLOT_SIZE_ERROR_MESSAGE") })}
                // {...{
                //   isRequired: true,
                //   pattern: "^([0-9]){0,8}$",
                //   type: "number", 
                //   title: t("PT_PLOT_SIZE_ERROR_MESSAGE")
                // }}
              />
              {!isFloor && (
                <>
                  <CardLabel>{`${t("PT_FORM2_SELECT_FLOOR")}*`}</CardLabel>
                  <div className={"form-pt-dropdown-only"}>
                    <Dropdown
                      t={t}
                      optionKey="i18nKey"
                      isMandatory={config.isMandatory}
                      option={mdmsData?.Floor}
                      selected={field?.floorNo}
                      select={(e) => selectFloor(index, e)}
                    />
                  </div>
                </>
              )}
            </div>
          </div>
        );
      })}
      <div style={{ justifyContent: "left", display: "flex", paddingBottom: "15px", color: "#FF8C00" }}>
        <button type="button" style={{ paddingTop: "10px" }} onClick={() => handleAdd()}>
          {`${t("PT_ADD_UNIT")}`}
        </button>
      </div>
      {/* {builtUpAreaError && <div style={{ justifyContent: "left", display: "flex", paddingBottom: "15px", color: "red" }}>
          Total Built-up area of a floor can not be more than the Land Area({formData?.landArea?.floorarea} Sq.Ft.)
      </div>} */}
    </FormStep>
    </React.Fragment>
  );
});
export default SelectPTUnits;
