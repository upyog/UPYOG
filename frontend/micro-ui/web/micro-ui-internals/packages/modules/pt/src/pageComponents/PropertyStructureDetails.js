import { CardLabel, Dropdown, FormStep, LinkButton, Loader, TextInput, DeleteIcon } from "@egovernments/digit-ui-react-components";
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

// const formatUnits = (units = [], currentFloor, isFloor) => {
//   if (!units || units.length == 0) {
//     return [
//       {
//         usageCategory: "",
//         ageOfProperty:null,
//         structureType:null,
//       },
//     ];
//   }
//   return units.map((unit) => {
//     let usageCategory = unit?.usageCategory && !(unit?.usageCategory?.includes("NONRESIDENTIAL")) ?  "RESIDENTIAL" : getUsageCategory(unit?.usageCategory)?.usageCategoryMinor;
//     return {
//       ...unit,
//       builtUpArea: unit?.constructionDetail?.builtUpArea,
//       rentedMonths:unit?.rentedMonths,
//       nonRentedMonthsUsage: unit?.nonRentedMonthsUsage,
//       ageOfProperty: unit?.ageOfProperty,
//       structureType: unit?.structureType,
//       usageCategory: usageCategory ? { code: usageCategory, i18nKey: `PROPERTYTAX_BILLING_SLAB_${usageCategory}` } : {},
//       occupancyType: unit?.occupancyType ? { code: unit.occupancyType, i18nKey: `PROPERTYTAX_OCCUPANCYTYPE_${unit?.occupancyType}` } : "",
//       floorNo: unit?.floorNo || Number.isInteger(unit?.floorNo) ? { code: unit.floorNo, i18nKey: `PROPERTYTAX_FLOOR_${unit?.floorNo}` } : {},
//       unitType: unit?.unitType ? { code: unit.unitType, i18nKey: `PROPERTYTAX_BILLING_SLAB_${unit?.unitType?.code || unit?.unitType}` } : "",
//     };
//   });
// };
const PropertyStructureDetails = ({ t, config, onSelect, userType, formData }) => {
  let path = window.location.pathname.split("/");
  let currentFloor = Number(path[path.length - 1]);
  let isFloor = window.location.pathname.includes("new-application/units") || window.location.pathname.includes("/edit-application/units");
  const [fields, setFields] = useState({"usageCategory":"","structureType":"","ageOfProperty":""}||
    formData.PropertyStructureDetails
  );
console.log("formaData",formData)
//   useEffect(() => {
//     setFields(() => formatUnits(isFloor ? formData?.units?.filter((ee) => ee.floorNo == currentFloor) : formData?.units, currentFloor, isFloor));
//     return () => {
//       setFields(null);
//     };
//   }, [currentFloor, formData, isFloor]);

  const getheader = () => {
   
      return `PROPERTYTAX_STRUCTURE_DETAILS`;
    
  };

  const { data: mdmsData, isLoading } = Digit.Hooks.useCommonMDMS(
    Digit.ULBService.getStateId(),
    "PropertyTax",
    ["Floor", "OccupancyType", "UsageCategory"],
    {
      select: (data) => {
        let usageCategory = data?.PropertyTax?.UsageCategory?.map((category) => getUsageCategory(category.code))
          .filter(
            (category) => category.usageCategoryDetail === false && category.usageCategorySubMinor === false && category.usageCategoryMinor !== false
          )
          .map((category) => ({ code: category.usageCategoryMinor, i18nKey: `PROPERTYTAX_BILLING_SLAB_${category.usageCategoryMinor}` }));
        let subCategory = Digit.Utils.getUnique(
          data?.PropertyTax?.UsageCategory.map((e) => getUsageCategory(e.code))
            .filter((e) => e.usageCategoryDetail)
            .map((e) => ({
              code: e.usageCategoryDetail,
              i18nKey: `PROPERTYTAX_BILLING_SLAB_${e.usageCategoryDetail}`,
              usageCategorySubMinor: e.usageCategorySubMinor,
              usageCategoryMinor: e.usageCategoryMinor,
            }))
        );

        return {
          Floor: data?.PropertyTax?.Floor?.filter((floor) => floor.active)?.map((floor) => ({
            i18nKey: `PROPERTYTAX_FLOOR_${floor.code}`,
            code: floor.code,
          })),
          OccupancyType: data?.PropertyTax?.OccupancyType?.filter((occupancy) => occupancy.active)?.map((occupancy) => ({
            i18nKey: `PROPERTYTAX_OCCUPANCYTYPE_${occupancy.code}`,
            code: occupancy.code,
          })),
          UsageCategory: usageCategory,
          UsageSubCategory: subCategory,
          usageDetails: data?.PropertyTax?.UsageCategory,
        };
      },
      retry: false,
      enable: false,
    }
  );

    let ageOfProperty =[
      {
        "i18nKey": "PROPERTYTAX_MONTH>10",
        "name": "greater than 10 years",
        "code": "10",
       "active": true
       },
       {
        "i18nKey": "PROPERTYTAX_MONTH>15",
        "name": "greater than 15 years",
        "code": "15",
       "active": true
       },
       {
        "i18nKey": "PROPERTYTAX_MONTH>25",
        "name": "greater than 24 years",
        "code": "25",
       "active": true
       } 
     ]
     let structureType =[
      {
        "i18nKey": "PERMANENT",
        "name": "Permanent",
        "code": "permanent",
       "active": true
       },
       {
        "i18nKey": "TEMPORARY",
        "name": "Temporary",
        "code": "temporary",
       "active": true
       },
       {
        "i18nKey": "SEMI_PERMANENT",
        "name": "Semi Permanent",
        "code": "semi permanent",
       "active": true
       },
       {
        "i18nKey": "RCC",
        "name": "RCC",
        "code": "RCC",
       "active": true
       }  
     ]
  function selectUsageCategory(i, value) {
   let field ={...fields}
   field.usageCategory = value;
    setFields(field);
  }

  function selectageOfProperty(i, value) {
    let field ={...fields}
    field.ageOfProperty=value;
    setFields(field);
  }

  function selectstructureType(i, value) {
    let field ={...fields}
    field.structureType=value;
    setFields(field);
  }


  const goNext = () => {
    console.log("firlds",fields)
    onSelect(config.key, fields);
    
  };

  const onSkip = () => onSelect();
  if (isLoading) {
    return <Loader />;
  }

  function isAllowedNext (){
  
console.log("fields",fields)
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
      isDisabled={false}
    >
        
          <div key={`unique`}>
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
              {/* <LinkButton
                label={<DeleteIcon   style={{ float: "right", position: "relative" }}
                fill={!(fields.length === 1) ? "#494848" : "#FAFAFA"} />}
                style={{ width: "100px", display: "inline" }}
                onClick={(e) => handleRemove(index)}
              /> */}
              <CardLabel>{`${t("PT_FORM2_USAGE_TYPE")}*`}</CardLabel>
              <Dropdown
                t={t}
                optionKey="i18nKey"
                isMandatory={config.isMandatory}
                option={[
                  ...(mdmsData?.UsageCategory ? mdmsData?.UsageCategory : []),
                  { code: "RESIDENTIAL", i18nKey: "PROPERTYTAX_BILLING_SLAB_RESIDENTIAL" },
                ]}
                selected={fields?.usageCategory}
                select={(e) => selectUsageCategory(0, e)}
              />
              <CardLabel>{`${t("PT_STRUCTURE_TYPE")}*`}</CardLabel>
              <div className={"form-pt-dropdown-only"}>
                <Dropdown
                  t={t}
                  optionKey="i18nKey"
                  isMandatory={config.isMandatory}
                  option={structureType}
                  selected={fields?.structureType}
                  placeholder={"Select structure type"}
                  select={(e) => selectstructureType(1, e)}
                />
              </div>
              <CardLabel>{`${t("PT_AGE_OF_PROPERTY")}*`}</CardLabel>
              <div className={"form-pt-dropdown-only"}>
                <Dropdown
                  t={t}
                  optionKey="i18nKey"
                  isMandatory={config.isMandatory}
                  option={ageOfProperty}
                  selected={fields?.ageOfProperty}
                  placeholder={"Select Age of Property"}
                  select={(e) => selectageOfProperty(2, e)}
                />
              </div>
            </div>
          </div>
        
      
    </FormStep>
    </React.Fragment>
  );
};
export default PropertyStructureDetails;