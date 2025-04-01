import React, { Fragment, useState, useEffect } from 'react';
import {
  LabelFieldPair,
  Dropdown,
  CardLabel,
  Loader,
  CardLabelError,
} from '@egovernments/digit-ui-react-components';
import { useTranslation } from 'react-i18next';
import { useForm, Controller } from 'react-hook-form';

function filterObjectsByKeyValuePair(objects, keyValuePairs) {
  // Check if objects and keyValuePairs are valid arrays
  if (
    !Array.isArray(objects) ||
    typeof keyValuePairs !== 'object' ||
    keyValuePairs === null
  ) {
    throw new Error(
      'Invalid input. Please provide valid arrays and an object for key-value pairs.'
    );
  }

  // Convert keyValuePairs object into an array of entries
  const keyValuePairsArray = Object.entries(keyValuePairs);

  // Filter objects based on the presence of key-value pairs
  const filteredObjects = objects?.filter((object) => {
    // Check if object is an object
    if (typeof object === 'object' && object !== null) {
      // Check if all key-value pairs are present in the object
      return keyValuePairsArray.every(([key, value]) => object[key] === value);
    }
    return false; // Ignore non-object elements
  });

  return filteredObjects;
}
function filterObjectsByUniqueKeyValuePair(objects, keyValuePairs) {
  // Check if objects and keyValuePairs are valid arrays
  if (!Array.isArray(objects) || typeof keyValuePairs !== 'object' || keyValuePairs === null) {
    throw new Error('Invalid input. Please provide valid arrays and an object for key-value pairs.');
  }

  // Convert keyValuePairs object into an array of entries
  const keyValuePairsArray = Object.entries(keyValuePairs);

  // Track unique values encountered during filtering
  const uniqueValues = new Set();

  // Filter objects based on the presence of key-value pairs and uniqueness
  const filteredObjects = objects.filter(object => {
    // Check if object is an object
    if (typeof object === 'object' && object !== null) {
      // Check if all key-value pairs are present in the object
      const hasKeyValuePairs = keyValuePairsArray.every(([key, value]) => object[key] === value);

      // Check if the unique value has not been encountered before
      const uniqueValue = JSON.stringify(Object.values(object));
      const isUnique = !uniqueValues.has(uniqueValue);

      // If both conditions are met, add the unique value to the set and include the object in the result
      if (hasKeyValuePairs && isUnique) {
        uniqueValues.add(uniqueValue);
        return true;
      }
    }
    return false; // Ignore non-object elements
  });

  return filteredObjects;
}
function filterByUniqueKey(arrayOfObjects, key) {
  if (!Array.isArray(arrayOfObjects) || typeof key !== 'string') {
    throw new Error('Invalid input. Please provide a valid array of objects and a key.');
  }

  const uniqueValues = new Set();
  return arrayOfObjects.filter(obj => {
    const value = obj[key];

    if (!uniqueValues.has(value)) {
      uniqueValues.add(value);
      return true;
    }

    return false;
  });
}

const TestStandard = ({ control, errors, formData,setValue,getValues,onSelect, ...props }) => {
  
  const formName = 'TestStandard';
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  //plant,process,stage,outputType
  const [plant, setPlant] = useState(Digit.Utils.tqm.getMappedPlants());
  const [process, setProcess] = useState([]);
  const [stage, setStage] = useState([]);
  const [outputType, setOutputType] = useState([]);

  const [cardState,setCardState] = useState(formData?.TestStandard ? formData?.TestStandard : {})

  function displayValue(newValue, criteria, index) {
    setCardState((prevState) => {
      return {
        ...prevState,
        [criteria]: newValue,
      };
    });
  }

  useEffect(() => {
    onSelect('TestStandard', cardState);
  }, [cardState]);

  // const [selectedPlant, setSelectedPlant] = useState(null);
  // const [selectedProcess, setSelectedProcess] = useState(null);
  // const [selectedStage, setSelectedStage] = useState(null);
  // const [selectedOutputType, setSelectedOutputType] = useState(null);

  const {
    isLoading: isTestStandardLoading,
    data: testStandard,
  } = Digit.Hooks.tqm.useCustomMDMSV2({
    tenantId,
    schemaCode: 'PQM.TestStandard',
    changeQueryName: 'TestStandard',
    config: {
      enabled: true,
      select: (data) => {
        return data?.mdms?.map((row) => {
          return {
            ...row.data,
            planti18nKey: Digit.Utils.locale.getTransformedLocale(
              `PQM.Plant_${row?.data?.plant}`
            ),
            processi18nKey: Digit.Utils.locale.getTransformedLocale(
              `PQM.Process_${row?.data?.process}`
            ),
            stagei18nKey: Digit.Utils.locale.getTransformedLocale(
              `PQM.Stage_${row?.data?.stage}`
            ),
            materiali18nKey: Digit.Utils.locale.getTransformedLocale(
              `PQM.Material_${row?.data?.material}`
            ),
          };
        });
      },
    },
    filters: {
      plant: getValues("TestStandard.plantCode")?.plantCode,
    },
  });

  const clearFields = (option) => {
    //if we update a field and fields below them are already filled then we need to clear them
    
    //plant
    if(option === "plant") {
      // setSelectedProcess(null)
      // setSelectedOutputType(null)
      // setSelectedStage(null)
      setValue(`${formName}.processCode`,null)
      setValue(`${formName}.stageCode`,null)
      setValue(`${formName}.materialCode`,null)
      setCardState((prevState) => {
        return {
          ...prevState,
          processCode:null,
          stageCode:null,
          materialCode:null
        }
      })
      // setProcess([])
      setOutputType([])
      setStage([])
    }
    //process
    else if(option === "process") {
      // setSelectedProcess(null)
      // setSelectedOutputType(null)
      // setSelectedStage(null)
      // setValue(`${formName}.processCode`,null)
      setValue(`${formName}.stageCode`,null)
      setValue(`${formName}.materialCode`,null)
      setCardState((prevState) => {
        return {
          ...prevState,
          stageCode:null,
          materialCode:null
        }
      })
      // setProcess([])
      setOutputType([])
      setStage([])
    }
    //stage
    else if(option === "stage") {
      // setSelectedProcess(null)
      // setSelectedOutputType(null)
      // setSelectedStage(null)
      // setValue(`${formName}.processCode`,null)
      // setValue(`${formName}.stageCode`,null)
      setValue(`${formName}.materialCode`,null)
      setCardState((prevState) => {
        return {
          ...prevState,
          materialCode:null
        }
      })
      // setProcess([])
      setOutputType([])
      // setStage([])
    }
    //material
    // else if(option === "material") {
    //   // setSelectedProcess(null)
    //   // setSelectedOutputType(null)
    //   setSelectedStage(null)
    // }
  }

  useEffect(() => {
    if (getValues("TestStandard.plantCode") && testStandard) {
      const getFilteredProcess = filterObjectsByUniqueKeyValuePair(testStandard, {
        plant: getValues("TestStandard.plantCode")?.plantCode,
      });
      
      setProcess(filterByUniqueKey(getFilteredProcess,"process"));
      
    }
    if (getValues("TestStandard.plantCode") && getValues("TestStandard.processCode") && testStandard) {
      const getFilteredStage = filterObjectsByUniqueKeyValuePair(testStandard, {
        plant: getValues("TestStandard.plantCode")?.plantCode,
        process:getValues("TestStandard.processCode")?.process
      });
      setStage(filterByUniqueKey(getFilteredStage,"stage"));
     

    }
    if (getValues("TestStandard.plantCode") && getValues("TestStandard.processCode") && getValues("TestStandard.stageCode") && testStandard) {
      const getFilteredOutputType = filterObjectsByUniqueKeyValuePair(testStandard, {
        plant: getValues("TestStandard.plantCode")?.plantCode,
        process:getValues("TestStandard.processCode")?.process,
        stage:getValues("TestStandard.stageCode")?.stage
      });
      setOutputType(filterByUniqueKey(getFilteredOutputType,"material"));
      
    }
  }, [formData,testStandard]);

  if (isTestStandardLoading) {
    return <Loader />;
  }

  return (
    <div style={{ padding: '0.5rem' }}>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(
          'TQM_PLANT_NAME'
        )} *`}</CardLabel>
        <div className="field">
          <Controller
            control={control}
            name={`${formName}.plantCode`}
            rules={{
              required: true,
            }}
            render={(props) => (
              <Dropdown
                // selected={selectedPlant || props.value}
                selected={cardState?.plantCode || props.value}
                disable={false}
                isMandatory={true}
                option={plant}
                select={(data) => {
                  // setSelectedPlant(data);
                  props.onChange(data);
                  clearFields("plant")
                  displayValue(data,"plantCode")
                  setValue('QualityParameter',{})

                }}
                optionKey="i18nKey"
                t={t}
              />
            )}
          />
          {errors?.[formName]?.plantCode?.type === 'required' && (
            <CardLabelError
              style={{
                width: '100%',
                marginTop: '-15px',
                fontSize: '14px',
                marginBottom: '0.5rem',
              }}
            >
              {t('ES_TQM_REQUIRED')}
            </CardLabelError>
          )}
        </div>
      </LabelFieldPair>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(
          'TQM_TREATMENT_PROCESS'
        )} *`}</CardLabel>
        <div className="field">
          <Controller
            control={control}
            name={`${formName}.processCode`}
            rules={{
              required: true,
            }}
            render={(props) => (
              <Dropdown
                // selected={selectedProcess || props.value}
                selected={cardState?.processCode ||props.value}
                disable={process.length > 0 ? false : true}
                isMandatory={true}
                option={process}
                select={(data) => {
                  // setSelectedProcess(data);
                  props.onChange(data);
                  clearFields("process")
                  displayValue(data,"processCode")
                  setValue('QualityParameter',{})
                }}
                optionKey="processi18nKey"
                t={t}
              />
            )}
          />
          {errors?.[formName]?.processCode?.type === 'required' && (
            <CardLabelError
              style={{
                width: '100%',
                marginTop: '-15px',
                fontSize: '14px',
                marginBottom: '0.5rem',
              }}
            >
              {t('ES_TQM_REQUIRED')}
            </CardLabelError>
          )}
        </div>
      </LabelFieldPair>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(
          'TQM_PROCESS_STAGE'
        )} *`}</CardLabel>
        <div className="field">
          <Controller
            control={control}
            name={`${formName}.stageCode`}
            rules={{
              required: true,
            }}
            render={(props) => (
              <Dropdown
                // selected={selectedStage || props.value}
                selected={cardState?.stageCode ||props.value}
                disable={stage.length > 0 ? false : true}
                isMandatory={true}
                option={stage}
                select={(data) => {
                  // setSelectedStage(data);
                  props.onChange(data);
                  clearFields("stage")
                  displayValue(data,"stageCode")
                  setValue('QualityParameter',{})

                }}
                optionKey="stagei18nKey"
                t={t}
              />
            )}
          />
          {errors?.[formName]?.stageCode?.type === 'required' && (
            <CardLabelError
              style={{
                width: '100%',
                marginTop: '-15px',
                fontSize: '14px',
                marginBottom: '0.5rem',
              }}
            >
              {t('ES_TQM_REQUIRED')}
            </CardLabelError>
          )}
        </div>
      </LabelFieldPair>
      <LabelFieldPair>
        <CardLabel className="card-label-smaller">{`${t(
          'TQM_OUTPUT_TYPE'
        )} *`}</CardLabel>
        <div className="field">
          <Controller
            control={control}
            name={`${formName}.materialCode`}
            rules={{
              required: true,
            }}
            render={(props) => (
              <Dropdown
                // selected={selectedOutputType || props.value}
                selected={cardState?.materialCode ||props.value}
                disable={outputType.length > 0 ? false : true}
                isMandatory={true}
                option={outputType}
                select={(data) => {
                  // setSelectedOutputType(data);
                  props.onChange(data);
                  clearFields("material")
                  displayValue(data,"materialCode")
                  setValue('QualityParameter',{})

                }}
                optionKey="materiali18nKey"
                t={t}
              />
            )}
          />
          {errors?.[formName]?.materialCode?.type === 'required' && (
            <CardLabelError
              style={{
                width: '100%',
                marginTop: '-15px',
                fontSize: '14px',
                marginBottom: '0.5rem',
              }}
            >
              {t('ES_TQM_REQUIRED')}
            </CardLabelError>
          )}
        </div>
      </LabelFieldPair>
    </div>
  );
};

export default TestStandard;
