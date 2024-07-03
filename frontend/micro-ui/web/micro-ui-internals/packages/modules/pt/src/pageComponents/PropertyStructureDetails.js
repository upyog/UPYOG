import { CardLabel, Dropdown, FormStep, LinkButton, Loader, LabelFieldPair, DeleteIcon } from "@nudmcdgnpm/digit-ui-react-components";
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

const PropertyStructureDetails = ({ t, config, onSelect, userType, formData }) => {
  let path = window.location.pathname.split("/");
  let currentFloor = Number(path[path.length - 1]);
  let isFloor = window.location.pathname.includes("new-application/units") || window.location.pathname.includes("/edit-application/units");
  const [fields, setFields] = useState(window.location.pathname.includes("/pt/modify-application/")? formData.propertyStructureDetails: {"usageCategory":"","structureType":"","ageOfProperty":""})
   
  
console.log("formaDataPropertyStructureDetails",formData)

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
  function selectUsageCategory(i, value) {
   let field ={...fields}
   field.usageCategory = value;
    setFields(field);
  }

  function selectageOfProperty(i, value) {
    let field ={...fields}
    field.ageOfProperty=value;
    setFields(field);
    if(userType === "employee")
    {
      onSelect(config.key, field);
    }
  }

  function selectstructureType(i, value) {
    let field ={...fields}
    field.structureType=value;
    setFields(field);
    if(userType === "employee")
    {
      onSelect(config.key, field);
    }
  }



  const goNext = () => {
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
  if (userType === "employee") {
      return (
        <React.Fragment>
          <LabelFieldPair key={0}>
          <CardLabel>{`${t("PT_STRUCTURE_TYPE")}*`}</CardLabel>
            <div className="field">
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
          </LabelFieldPair>
          <LabelFieldPair key={1}>
          <CardLabel>{`${t("PT_AGE_OF_PROPERTY")}*`}</CardLabel>
            <div className="field">
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
          </LabelFieldPair>
        </React.Fragment>
      );
  }
  return (
    <React.Fragment>
    {window.location.href.includes("/citizen") ? <Timeline currentStep={1}/> : null}
    <FormStep
    config={((config.texts.header = getheader()), config)}
      onSelect={goNext}
      onSkip={onSkip}
      t={t}
      isDisabled={!fields.structureType || !fields.ageOfProperty}
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
              {formData?.PropertyType?.code === "VACANT"? 
              <div>
              <CardLabel>{`${t("PT_FORM2_USAGE_TYPE")}*`}</CardLabel>
              <Dropdown
                t={t}
                optionKey="i18nKey"
                isMandatory={config.isMandatory}
                option={catMenu}
                selected={fields?.usageCategory}
                select={(e) => selectUsageCategory(0, e)}
              />
              </div>:""}
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