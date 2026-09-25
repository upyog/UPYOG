import { AddFilled, Button, Header, InboxSearchComposer, Loader, Dropdown,SubmitBar, ActionBar } from "@upyog/workbench-ui-react-components";
import React, { useState, useEffect, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { Config as Configg } from "../../configs/searchMDMSConfig";
import _, { drop } from "lodash";

const toDropdownObj = (master = "", mod = "") => {
  return {
    name: mod || master,
    code: Digit.Utils.locale.getTransformedLocale(mod ? `WBH_MDMS_${master}_${mod}` : `WBH_MDMS_MASTER_${master}`),
  };

  // return {
  //   name: mod || master,
  //   code: mod ? `${mod}` : `${master}`,
  // };
};

const MDMSSearchv2 = () => {
  let Config = _.clone(Configg)
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  
  let {masterName:modulee,moduleName:master,tenantId} = Digit.Hooks.useQueryParams()

  const [availableSchemas, setAvailableSchemas] = useState([]);
  const [currentSchema, setCurrentSchema] = useState(null);
  const [masterName, setMasterName] = useState(null); //for dropdown
  const [moduleName, setModuleName] = useState(null); //for dropdown
  const [masterOptions,setMasterOptions] = useState([])
  const [moduleOptions,setModuleOptions] = useState([])
  const [updatedConfig,setUpdatedConfig] = useState(null)
  tenantId = tenantId || Digit.ULBService.getCurrentTenantId();
  const SchemaDefCriteria = {
    tenantId:tenantId ,
    limit:200
  }
  if(master && modulee ) {
    SchemaDefCriteria.codes = [`${master}.${modulee}`] 
  }
  const { isLoading, data: dropdownData } = Digit.Hooks.useCustomAPIHook({
    url: `/${Digit.Hooks.workbench.getMDMSContextPath()}/schema/v1/_search`,
    params: {},
    body: {
      SchemaDefCriteria
    },
    config: {
      select: (data) => {
        function onlyUnique(value, index, array) {
          return array.indexOf(value) === index;
        }
        
        //when api is working fine change here(thsese are all schemas available in a tenant)
        // const schemas = sampleSchemaResponse.SchemaDefinitions;
        const schemas = data?.SchemaDefinitions
       
      
        const obj = {
          mastersAvailable: [],
          schemas: schemas,  // also retun schemas
        };

        schemas.forEach((schema, idx) => {
          const { code } = schema;
          const splittedString = code.split(".");
          const [master, mod] = splittedString;
          obj[master] = obj[master]?.length > 0 ? [...obj[master], toDropdownObj(master, mod)] : [toDropdownObj(master, mod)];
          obj.mastersAvailable.push(master);
        });
        obj.mastersAvailable = obj.mastersAvailable.filter(onlyUnique);
        obj.mastersAvailable = obj.mastersAvailable.map((mas) => toDropdownObj(mas));

        return obj;
      },
    },
  });

  useEffect(() => {
    if (dropdownData?.schemas) {
      setAvailableSchemas(dropdownData.schemas);
      if (dropdownData.schemas?.length === 1) {
        setCurrentSchema(dropdownData.schemas?.[0]);
      }
    }
  }, [dropdownData]);

  useEffect(() => {
    setMasterOptions(dropdownData?.mastersAvailable)
  }, [dropdownData])

  useEffect(() => {
    setModuleOptions(dropdownData?.[masterName?.name])
  }, [masterName])

  useEffect(() => {
    //here set current schema based on module and master name
    if(masterName?.name && moduleName?.name){
    setCurrentSchema(availableSchemas.filter(schema => schema.code === `${masterName?.name}.${moduleName?.name}`)?.[0])
    }
  }, [moduleName])
  
useEffect(() => {
  if (currentSchema) {
    const dropDownOptions = [];
    const {
      definition: { properties },
    } = currentSchema;
    
    Object.keys(properties)?.forEach((key) => {
      if (properties[key].type === "string" && !properties[key].format) {
        dropDownOptions.push({
          name: key,
          code: key,
          i18nKey: Digit.Utils.locale.getTransformedLocale(`${currentSchema.code}_${key}`)
        });
      }
    });

    const [schemaModule, schemaMaster] = currentSchema.code.split('.');
    const newConfig = _.cloneDeep(Config);
    newConfig.sections.search.uiConfig.fields[0].populators.options = dropDownOptions;
    newConfig.actionLink = `workbench/mdms-add-v2?moduleName=${schemaModule}&masterName=${schemaMaster}`;
    
    newConfig.additionalDetails = {
      currentSchemaCode: currentSchema.code
    }

    newConfig.sections.searchResult.uiConfig.columns = [...dropDownOptions.map(option => {
      return {
        label: option.i18nKey,
        i18nKey: option.i18nKey,
        jsonPath: `data.${option.code}`,
        dontShowNA: true
      }
    }), {
      label: "WBH_ISACTIVE",
      i18nKey: "WBH_ISACTIVE",
      jsonPath: `isActive`,
      additionalCustomization: true
    }]

    newConfig.apiDetails.serviceName = `/${Digit.Hooks.workbench.getMDMSContextPath()}/v2/_search`;

    // Fixed: tenantId and schemaCode were missing from MdmsCriteria in API call.
    // Updated: Explicitly set tenantId and schemaCode in apiDetails requestBody
    // so API receives correct MdmsCriteria instead of empty custom:{} object.
    newConfig.apiDetails.requestBody = {
      ...newConfig.apiDetails.requestBody,
      MdmsCriteria: {
        tenantId: tenantId,              // tenantId add kiya
        schemaCode: currentSchema.code,  // schemaCode add kiya
        filters: {},
        limit: 10,
        offset: 0
      }
    };
  newConfig.apiDetails.changeQueryName = currentSchema.code;

    setUpdatedConfig(newConfig);
  }
}, [currentSchema]);

  const handleAddMasterData = () => {
    const [schemaModule, schemaMaster] = currentSchema?.code?.split('.') || [];
    if (schemaModule && schemaMaster) {
      navigate(`/${window?.contextPath}/employee/workbench/mdms-add-v2?moduleName=${schemaModule}&masterName=${schemaMaster}`);
    }
  }

  const onClickRow = ({original:row}) => {
    const [moduleName,masterName] = row.schemaCode.split(".")
    navigate(`/${window?.contextPath}/employee/workbench/mdms-view?moduleName=${moduleName}&masterName=${masterName}&uniqueIdentifier=${row.uniqueIdentifier}`)
  }

  if (isLoading) return <Loader />;
  return (
    <React.Fragment>
        {/* <Header className="works-header-search">{t(Config?.label)}</Header> */}
      <Header className="digit-form-composer-sub-header">{t(Digit.Utils.workbench.getMDMSLabel(`SCHEMA_` + currentSchema?.code))}</Header>
      {
        updatedConfig && Digit.Utils.didEmployeeHasRole(updatedConfig?.actionRole) &&
        <ActionBar >
          <SubmitBar disabled={false} onSubmit={handleAddMasterData} label={t("WBH_ADD_MDMS")} />
        </ActionBar>
      }
      {updatedConfig && <div className="inbox-search-wrapper">
        <InboxSearchComposer configs={updatedConfig} additionalConfig = {{
          resultsTable:{
            onClickRow
          }
        }}></InboxSearchComposer>
      </div>}
    </React.Fragment>
  );
};

export default MDMSSearchv2;
