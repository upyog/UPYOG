  import {
      CardLabel,
      CardLabelError,
      CloseSvg,
      Dropdown,
      LabelFieldPair,
      LinkButton,
      //MobileNumber,
      TextInput,
      Toast,
    } from "@upyog/digit-ui-react-components";
    import _ from "lodash";
    import React, { useEffect, useMemo, useState } from "react";
    import { Controller, useForm } from "react-hook-form";
    import { useTranslation } from "react-i18next";
    import { useLocation } from "react-router-dom";


  

    
    const createAssetcommon = () => ({

    //   assetcommonselection: "",
    
      key: Date.now(),
    });
    
    const AssetCommonSelection = ({ selectedAssetType, config, onSelect, userType, formData, setError, formState, clearErrors }) => {

        console.log("formData in commonselection page",formData)
      const { t } = useTranslation();
    
      const { pathname } = useLocation();
      const [assetscommon, setAssets] = useState(formData?.assetscommon || [createAssetcommon()]);

      
      const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
    
      const tenantId = Digit.ULBService.getCurrentTenantId();
      const stateId = Digit.ULBService.getStateId();
    

    
      const { data: AST_Type } = Digit.Hooks.asset.useAssetType(stateId, "ASSET", "assetParentCategory"); // hooks for Asset Parent Category
      let AST_type = [];  //variable name for asset type

      console.log("AST_Type",AST_Type);

        
    
    
    
      AST_Type &&
      AST_Type.map((AST_type_mdms) => {
              AST_type.push({
              i18nKey: `AST_TYPE_${AST_type_mdms.code}`,
              code: `${AST_type_mdms.code}`,
              value: `${AST_type_mdms.name}`
            });
    
        });

    

      useEffect(() => {
        onSelect(config?.key, assetscommon);
    
      }, [assetscommon]);



      
    
    
    
      const commonProps = {
        focusIndex,
        allAssets: assetscommon,
        setFocusIndex,
        formData,
        formState,
        setAssets,
        t,
        setError,
        clearErrors,
        config,
        AST_type,
      
      };
    
      return (
        <React.Fragment>
          {assetscommon.map((assetscommon, index) => (
            <OwnerForm key={assetscommon.key} index={index} assetscommon={assetscommon} {...commonProps} />
          ))}
        </React.Fragment>
      )
    };

    
    
    const OwnerForm = (_props) => {
      const {
        assetscommon,
        index,
        focusIndex,
        allAssets,      
        setAssets,
        t,
        formData,
        config,
        setError,
        clearErrors,
        formState,
        AST_type,
        setFocusIndex
      
    
      } = _props;
    
      const [showToast, setShowToast] = useState(null);
      const {
        control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger, } = useForm();
      const formValue = watch();
      const { errors } = localFormState;
      const tenantId = Digit.ULBService.getCurrentTenantId();
    
    
    
      const [part, setPart] = React.useState({});
    
      useEffect(() => {
      
    
        if (!_.isEqual(part, formValue)) {
          setPart({ ...formValue });
          setAssets((prev) => prev.map((o) => (o.key && o.key === assetscommon.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
          trigger();
        }
      }, [formValue]);
    
      useEffect(() => {
        if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors))
          setError(config.key, { type: errors });
        else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
      }, [errors]);
    
      const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };

      const selectedAssetCode = formData?.assetscommon?.[0]?.assetcommonselection?.code;

      console.log("selelelele",selectedAssetCode);

      const commondropvalues = [
        {
            code: "YES",
            i18nKey: "YES"
        },
        {
            code: "NO",
            i18nKey: "NO"
        }
        
      ]



      const surfaceselecttype = [
        {
            code: "RCC",
            i18nKey: "RCC"
        },
        {
            code: "PCC",
            i18nKey: "PCC"
        }
      ]

      const roadselecttype = [
        {
            code: "National Highways",
            i18nKey: "National Highways"
        },
        {
            code: "State Highways",
            i18nKey: "State Highways"
        },
        {
            code: "Rural Roads",
            i18nKey: "Rural Roads"
        },
        {
            code: "District Roads",
            i18nKey: "District Roads"
        },
        {
            code: "Border Roads",
            i18nKey: "Border Roads"
        }

      ]

     
    
    
      return (
        <React.Fragment>
          <div style={{ marginBottom: "16px" }}>
            <div style={{ border: "1px solid #E3E3E3", padding: "16px", marginTop: "8px" }}>
              {allAssets?.length > 2 ? (
                <div style={{ marginBottom: "16px", padding: "5px", cursor: "pointer", textAlign: "right" }}>
                  X
                </div>
              ) : null}
    
              {/* <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_COMMON_TYPE") }</CardLabel>
                <Controller
                  control={control}
                  name={"assetcommonselection"}
                  defaultValue={assetscommon?.assetcommonselection}
                  rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                  render={(props) => (
                    <Dropdown
                      className="form-field"
                      selected={props.value}
                      select={props.onChange}
                      onBlur={props.onBlur}
                      option={AST_type}
                      optionKey="i18nKey"
                      t={t}
                    />
                  )}
                />
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.assetcommonselection ? errors?.assetcommonselection?.message : ""}</CardLabelError> */}

              
              {formData?.assets?.[0]?.assettype?.code === "LAND" && (
                <React.Fragment>
                  <br></br>
                  <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_LAND_DETAILS")}<br></br></div>
                  <br></br>

                  
                <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_LAND_TYPE") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"landType"}
                      defaultValue={assetscommon?.landType}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "landType"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "landType" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.landType ? errors?.landType?.message : ""}</CardLabelError>


              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_LAND_AREA") }</CardLabel>
                <div className="field">
                    <Controller
                    control={control}
                    name={"area"}
                    defaultValue={assetscommon?.area}
                    rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: {
                        pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                        },
                    }}
                    render={(props) => (
                        <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "area"}
                        onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "area" });
                        }}
                        onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                        }}
                        />
                    )}
                    />
                </div>
                </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.area ? errors?.area?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_ACQUISTION_COST_BOOK_VALUE") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"bookValue"}
                      defaultValue={assetscommon?.bookValue}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "bookValue"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "bookValue" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.bookValue ? errors?.bookValue?.message : ""}</CardLabelError>

              
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_DATE_DEED_EXECUTION") }</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"dateOfDeedExecution"}
                    defaultValue={assetscommon?.dateOfDeedExecution}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                    }}
                    render={(props) => (
                    <TextInput
                        type="date"
                        value={props.value}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        }}
                        max={new Date().toISOString().split('T')[0]}
                    />
                    )}
                />
                </div>
            </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.dateOfDeedExecution ? errors?.dateOfDeedExecution?.message : ""}</CardLabelError>

              
                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_DATE_POSSESION") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"dateFfPossession"}
                        defaultValue={assetscommon?.dateFfPossession}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                        }}
                        render={(props) => (
                        <TextInput
                            type="date"
                            value={props.value}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            }}
                            max={new Date().toISOString().split('T')[0]}
                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.dateFfPossession ? errors?.dateFfPossession?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_FROM_DEED_TAKEN") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"fromWhomDeedTaken"}
                      defaultValue={assetscommon?.fromWhomDeedTaken}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "fromWhomDeedTaken"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "fromWhomDeedTaken" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.fromWhomDeedTaken ? errors?.fromWhomDeedTaken?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_GOVT_ORDER_NUM") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"governmentOrderNumber"}
                      defaultValue={assetscommon?.governmentOrderNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "governmentOrderNumber"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "governmentOrderNumber" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.governmentOrderNumber ? errors?.governmentOrderNumber?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COLLECT_ORDER_NUM") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"collectorOrderNumber"}
                      defaultValue={assetscommon?.collectorOrderNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "collectorOrderNumber"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "collectorOrderNumber" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.collectorOrderNumber ? errors?.collectorOrderNumber?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COUNCIL_RES_NUM") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"councilResolutionNumber"}
                      defaultValue={assetscommon?.councilResolutionNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "councilResolutionNumber"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "councilResolutionNumber" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.councilResolutionNumber ? errors?.councilResolutionNumber?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_AWARD_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"awardNumber"}
                      defaultValue={assetscommon?.awardNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "awardNumber"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "awardNumber" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.awardNumber ? errors?.awardNumber?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COI") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"oAndMCOI"}
                      defaultValue={assetscommon?.oAndMCOI}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "oAndMCOI"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "oAndMCOI" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.oAndMCOI ? errors?.oAndMCOI?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_OM_TASK") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"oAndMTaskDetail"}
                      defaultValue={assetscommon?.oAndMTaskDetail}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "oAndMTaskDetail"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "oAndMTaskDetail" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.oAndMTaskDetail ? errors?.oAndMTaskDetail?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_TOTAL_COST") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"totalCost"}
                      defaultValue={assetscommon?.totalCost}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "totalCost"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "totalCost" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          
                          props.onBlur(e);
                          
                          }
                        }
                        placeholder="in Rupees"
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.totalCost ? errors?.totalCost?.message : ""}</CardLabelError>

              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_DEPRICIATION_RATE") }</CardLabel>
                <div className="field">
                    <Controller
                    control={control}
                    name={"depreciationRate"}
                    defaultValue={assetscommon?.depreciationRate}
                    rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: {
                        pattern: (val) => /^[0-9]+(\.[0-9]+)?(%)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                        },
                    }}
                    render={(props) => (
                        <TextInput
                        value={props.value}
                        // disable={isEditScreen}
                        autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "depreciationRate"}
                        onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "depreciationRate" });
                        }}
                        onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                        }}
                        />
                    )}
                    />
                </div>
                </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.depreciationRate ? errors?.depreciationRate?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COST_AFTER_DEPRICIAT") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"costAfterDepreciation"}
                      defaultValue={assetscommon?.costAfterDepreciation}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "costAfterDepreciation"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "costAfterDepreciation" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                          placeholder="in Rupees"
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.costAfterDepreciation ? errors?.costAfterDepreciation?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_CURRENT_VALUE") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"currentAssetValue"}
                      defaultValue={assetscommon?.currentAssetValue}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "currentAssetValue"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "currentAssetValue" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                          placeholder="in Rupees"
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.currentAssetValue ? errors?.currentAssetValue?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_REVENUE_GENERATED") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"revenueGeneratedByAsset"}
                      defaultValue={assetscommon?.revenueGeneratedByAsset}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "revenueGeneratedByAsset"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "revenueGeneratedByAsset" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                          placeholder="In Rupees"
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.revenueGeneratedByAsset ? errors?.revenueGeneratedByAsset?.message : ""}</CardLabelError>

              <br></br>
              <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_ADDITIONAL_DETAILS")}<br></br></div>
              <br></br> 
              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_OSR_LAND") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"osrLand"}
                      defaultValue={assetscommon?.osrLand}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "osrLand"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "osrLand" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.osrLand ? errors?.osrLand?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_FENCED") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"isItFenced"}
                      defaultValue={assetscommon?.isItFenced}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      }}
                      render={(props) => 
                        
                        (
                        <Dropdown
                        className="form-field"
                        selected={props.value}
                        select={props.onChange}
                        onBlur={props.onBlur}
                        option={commondropvalues}
                        style={{ width: "100%" }}
                        optionKey="i18nKey"
                        t={t}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.isItFenced ? errors?.isItFenced?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_ANY_BUILTUP") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"anyBuiltup"}
                      defaultValue={assetscommon?.anyBuiltup}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      }}
                      render={(props) => (
                        <Dropdown
                        className="form-field"
                        selected={props.value}
                        select={props.onChange}
                        onBlur={props.onBlur}
                        option={commondropvalues}
                        style={{ width: "100%" }}
                        optionKey="i18nKey"
                        t={t}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.anyBuiltup ? errors?.anyBuiltup?.message : ""}</CardLabelError>

              
              
              <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_HOW_ASSET_USE") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"howAssetBeingUsed"}
                          defaultValue={assetscommon?.howAssetBeingUsed}
                          rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                          }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              // disable={isEditScreen}
                              autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "howAssetBeingUsed"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: assetscommon.key, type: "howAssetBeingUsed" });
                              }}
                              onBlur={(e) => {
                              setFocusIndex({ index: -1 });
                              props.onBlur(e);
                              }}
                          />
                          )}
                      />
                      </div>
                  </LabelFieldPair>
                </React.Fragment>
              )}  

              {formData?.assets?.[0]?.assettype?.code === 'BUILDING' && (
                <React.Fragment>
                <br></br>
                  <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_BUILDING_DETAILS")}<br></br></div>
                <br></br>

                <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_BUILDING_NO") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"buildingSno"}
                            defaultValue={assetscommon?.buildingSno}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "buildingSno"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "buildingSno" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.buildingSno ? errors?.buildingSno?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_PLOT_AREA") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"plotArea"}
                            defaultValue={assetscommon?.plotArea}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "plotArea"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "plotArea" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.plotArea ? errors?.plotArea?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_PLINTH_AREA") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"plinthArea"}
                            defaultValue={assetscommon?.plinthArea}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "plinthArea"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "plinthArea" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.plinthArea ? errors?.plinthArea?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_FLOORS_NO") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"floorNo"}
                            defaultValue={assetscommon?.floorNo}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "floorNo"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "floorNo" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.floorNo ? errors?.floorNo?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_DIMENSIONS") }</CardLabel>
                    <div className="field">
                        <Controller
                        control={control}
                        name={"dimensions"}
                        defaultValue={assetscommon?.dimensions}
                        rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: {
                            pattern: (val) => /^(\d+x)+\d+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                            },
                        }}
                        render={(props) => (
                            <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "dimensions"}
                            onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "dimensions" });
                            }}
                            onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                            }}
                            />
                        )}
                        />
                    </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.dimensions ? errors?.dimensions?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_AREA_FLOOR") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"floorArea"}
                            defaultValue={assetscommon?.floorArea}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "floorArea"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "floorArea" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.floorArea ? errors?.floorArea?.message : ""}</CardLabelError>

                    <br></br>
                    <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_ADDITIONAL_DETAILS")}<br></br></div>
                    <br></br>

                    <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ACQUISTION_COST_BOOK_VALUE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"bookValue"}
                        defaultValue={assetscommon?.bookValue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "bookValue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "bookValue" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                            placeholder="in Rupees"
                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.bookValue ? errors?.bookValue?.message : ""}</CardLabelError>


                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_TOTAL_COST") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"totalCost"}
                        defaultValue={assetscommon?.totalCost}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "totalCost"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "totalCost" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                            placeholder="in Rupees"

                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.totalCost ? errors?.totalCost?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_DEPRICIATION_RATE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"depreciationRate"}
                        defaultValue={assetscommon?.depreciationRate}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?(%)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                    },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "depreciationRate"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "depreciationRate" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.depreciationRate ? errors?.depreciationRate?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_COST_AFTER_DEPRICIAT") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"costAfterDepreciation"}
                        defaultValue={assetscommon?.costAfterDepreciation}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "costAfterDepreciation"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "costAfterDepreciation" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                            placeholder="in Rupees"

                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.costAfterDepreciation ? errors?.costAfterDepreciation?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_CURRENT_VALUE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"currentAssetValue"}
                        defaultValue={assetscommon?.currentAssetValue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "currentAssetValue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "currentAssetValue" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                            placeholder="in Rupees"

                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.currentAssetValue ? errors?.currentAssetValue?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_REVENUE_GENERATED") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"revenueGeneratedByAsset"}
                        defaultValue={assetscommon?.revenueGeneratedByAsset}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "revenueGeneratedByAsset"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "revenueGeneratedByAsset" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                            placeholder="in Rupees"

                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.revenueGeneratedByAsset ? errors?.revenueGeneratedByAsset?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_HOW_ASSET_USE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"howAssetBeingUsed"}
                        defaultValue={assetscommon?.howAssetBeingUsed}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "howAssetBeingUsed"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "howAssetBeingUsed" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.howAssetBeingUsed ? errors?.howAssetBeingUsed?.message : ""}</CardLabelError>

                    
                </React.Fragment>
              )}

              {formData?.assets?.[0]?.assettype?.code === 'SERVICE' && formData?.assets?.[0]?.assetsubtype?.code === 'ROAD_INFRASTRUCTURE' && (
                <React.Fragment>
                <br></br>
                  <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_ROAD_DETAILS")}<br></br></div>
                <br></br>


                    <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ROAD_TYPE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"roadType"}
                        defaultValue={assetscommon?.roadType}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                            <Dropdown
                            className="form-field"
                            selected={props.value}
                            select={props.onChange}
                            onBlur={props.onBlur}
                            option={roadselecttype}
                            style={{ width: "100%" }}
                            optionKey="i18nKey"
                            t={t}
                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.roadType ? errors?.roadType?.message : ""}</CardLabelError>


              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_TOTAL_LENGTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"totalLength"}
                      defaultValue={assetscommon?.totalLength}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "totalLength"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "totalLength" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.totalLength ? errors?.totalLength?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_ROAD_WIDTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"roadWidth"}
                      defaultValue={assetscommon?.roadWidth}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "roadWidth"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "roadWidth" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.roadWidth ? errors?.roadWidth?.message : ""}</CardLabelError>

              
              <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_SURFACE_TYPE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"surfaceType"}
                        defaultValue={assetscommon?.surfaceType}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        }}
                        render={(props) => (
                            <Dropdown
                            className="form-field"
                            selected={props.value}
                            select={props.onChange}
                            onBlur={props.onBlur}
                            option={surfaceselecttype}
                            style={{ width: "100%" }}
                            optionKey="i18nKey"
                            t={t}
                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.surfaceType ? errors?.surfaceType?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_PROTECTION_LENGTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"protectionLength"}
                      defaultValue={assetscommon?.protectionLength}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "protectionLength"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "protectionLength" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.protectionLength ? errors?.protectionLength?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_DRAINAGE_CHANNEL_LENGTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"drainageLength"}
                      defaultValue={assetscommon?.drainageLength}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "drainageLength"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "drainageLength" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.drainageLength ? errors?.drainageLength?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_FOOTPATH_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"numOfFootpath"}
                      defaultValue={assetscommon?.numOfFootpath}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "numOfFootpath"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "numOfFootpath" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.numOfFootpath ? errors?.numOfFootpath?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_PEDASTRIAN_CROSSING_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"numOfPedastrianCross"}
                      defaultValue={assetscommon?.numOfPedastrianCross}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "numOfPedastrianCross"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "numOfPedastrianCross" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.numOfPedastrianCross ? errors?.numOfPedastrianCross?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_BUSSTOP_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"numOfBusStop"}
                      defaultValue={assetscommon?.numOfBusStop}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "numOfBusStop"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "numOfBusStop" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.numOfBusStop ? errors?.numOfBusStop?.message : ""}</CardLabelError>

              
              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_METROSTATION_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"numOfMetroStation"}
                      defaultValue={assetscommon?.numOfMetroStation}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "numOfMetroStation"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "numOfMetroStation" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.numOfMetroStation ? errors?.numOfMetroStation?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_CYCLETRACK_LENGTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"lengthOfCycletrack"}
                      defaultValue={assetscommon?.lengthOfCycletrack}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "lengthOfCycletrack"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "lengthOfCycletrack" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.lengthOfCycletrack ? errors?.lengthOfCycletrack?.message : ""}</CardLabelError>

              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_LAST_MAINTAINENCE") }</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"lastMaintainence"}
                    defaultValue={assetscommon?.lastMaintainence}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                    }}
                    render={(props) => (
                    <TextInput
                        type="date"
                        value={props.value}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        }}
                        max={new Date().toISOString().split('T')[0]}
                    />
                    )}
                />
                </div>
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.lastMaintainence ? errors?.lastMaintainence?.message : ""}</CardLabelError>

              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_NEXT_MAINTAINENCE") }</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"nextMaintainence"}
                    defaultValue={assetscommon?.nextMaintainence}
                    rules={{
                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                    }}
                    render={(props) => (
                    <TextInput
                        type="date"
                        value={props.value}
                        onChange={(e) => {
                        props.onChange(e.target.value);
                        }}
                        max={new Date(new Date().setFullYear(new Date().getFullYear() + 2)).toISOString().split("T")[0]}
                    />
                    )}
                />
                </div>
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.nextMaintainence ? errors?.nextMaintainence?.message : ""}</CardLabelError>

              <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ACQUISTION_COST_BOOK_VALUE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"bookValue"}
                        defaultValue={assetscommon?.bookValue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "bookValue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "bookValue" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                            placeholder="in Rupees"

                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.bookValue ? errors?.bookValue?.message : ""}</CardLabelError>

                <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_TOTAL_COST") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"totalCost"}
                      defaultValue={assetscommon?.totalCost}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "totalCost"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "totalCost" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                          placeholder="in Rupees"

                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.totalCost ? errors?.totalCost?.message : ""}</CardLabelError>


              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_DEPRICIATION_RATE") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"depreciationRate"}
                      defaultValue={assetscommon?.depreciationRate}
                      rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?(%)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                       },
                        }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "depreciationRate"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "depreciationRate" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.depreciationRate ? errors?.depreciationRate?.message : ""}</CardLabelError>


              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COST_AFTER_DEPRICIAT") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"costAfterDepreciation"}
                      defaultValue={assetscommon?.costAfterDepreciation}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "costAfterDepreciation"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "costAfterDepreciation" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                          placeholder="in Rupees"
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              <CardLabelError style={errorStyle}>{localFormState.touched.costAfterDepreciation ? errors?.costAfterDepreciation?.message : ""}</CardLabelError>



              </React.Fragment>
              )}

              {formData?.assets?.[0]?.assetsubtype?.code === "VEHICLES" && (
                <React.Fragment>
                  <br></br>
                  <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_VEHICLES_DETAILS")}<br></br></div>
                  <br></br>

                  <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_REGISTRATION_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"registrationNumber"}
                      defaultValue={assetscommon?.registrationNumber}
                      rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: {
                          pattern: (val) =>
                            /^(?=.*[a-zA-Z])[a-zA-Z0-9]+([-/][a-zA-Z0-9]+)*$/.test(val) ||
                            t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                        },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "registrationNumber"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "registrationNumber" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
                  </LabelFieldPair>
                  <CardLabelError style={errorStyle}>{localFormState.touched.registrationNumber ? errors?.registrationNumber?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_ENGINE_NUMBER") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"engineNumber"}
                            defaultValue={assetscommon?.engineNumber}
                            rules={{
                                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                validate: {
                                  pattern: (val) =>
                                    /^(?=.*[a-zA-Z])[a-zA-Z0-9]+([-/][a-zA-Z0-9]+)*$/.test(val) ||
                                    t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                                },
                              }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "engineNumber"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "engineNumber" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.engineNumber ? errors?.engineNumber?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_CHASIS_NUMBER") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"chasisNumber"}
                            defaultValue={assetscommon?.chasisNumber}
                            rules={{
                                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                validate: {
                                  pattern: (val) =>
                                    /^(?=.*[a-zA-Z])[a-zA-Z0-9]+([-/][a-zA-Z0-9]+)*$/.test(val) ||
                                    t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                                },
                              }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "chasisNumber"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "chasisNumber" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.chasisNumber ? errors?.chasisNumber?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_PARKING_LOCATION") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"parkingLocation"}
                            defaultValue={assetscommon?.parkingLocation}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "parkingLocation"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "parkingLocation" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.parkingLocation ? errors?.parkingLocation?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ACQUISTION_DATE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"acquisitionDate"}
                        defaultValue={assetscommon?.acquisitionDate}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                        }}
                        render={(props) => (
                        <TextInput
                            type="date"
                            value={props.value}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            }}
                            max={new Date().toISOString().split('T')[0]}
                        />
                        )}
                    />
                    </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.acquisitionDate ? errors?.acquisitionDate?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_ACQUIRED_SOURCE") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"acquiredFrom"}
                            defaultValue={assetscommon?.acquiredFrom}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "acquiredFrom"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "acquiredFrom" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.acquiredFrom ? errors?.acquiredFrom?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_IMPROVEMENT_COST") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"improvementCost"}
                            defaultValue={assetscommon?.improvementCost}
                            rules={{
                                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                                },
                                }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "improvementCost"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "improvementCost" });
                                }}
                                onBlur={(e) => {
                                setFocusIndex({ index: -1 });
                                props.onBlur(e);
                                }}
                                placeholder="in Rupees"
                            />
                            )}
                        />
                        </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.improvementCost ? errors?.improvementCost?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ACQUISTION_COST_BOOK_VALUE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"bookValue"}
                        defaultValue={assetscommon?.bookValue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "bookValue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "bookValue" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                            placeholder="in Rupees"
                        />
                        )}
                    />
                    </div>
                    </LabelFieldPair>
                    <CardLabelError style={errorStyle}>{localFormState.touched.bookValue ? errors?.bookValue?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_CURRENT_VALUE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"currentAssetValue"}
                        defaultValue={assetscommon?.currentAssetValue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "currentAssetValue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "currentAssetValue" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                            placeholder="in Rupees"

                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.currentAssetValue ? errors?.currentAssetValue?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_TOTAL_COST") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"totalCost"}
                        defaultValue={assetscommon?.totalCost}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "totalCost"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "totalCost" });
                            }}
                            onBlur={(e) => {
                            setFocusIndex({ index: -1 });
                            props.onBlur(e);
                            }}
                            placeholder="in Rupees"

                        />
                        )}
                    />
                    </div>
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.totalCost ? errors?.totalCost?.message : ""}</CardLabelError>




                </React.Fragment>
              )}

            </div>
          </div>
          {showToast?.label && (
            <Toast
              label={showToast?.label}
              onClose={(w) => {
                setShowToast((x) => null);
              }}
            />
          )}
        </React.Fragment>
      );
    };
    
    export default AssetCommonSelection;