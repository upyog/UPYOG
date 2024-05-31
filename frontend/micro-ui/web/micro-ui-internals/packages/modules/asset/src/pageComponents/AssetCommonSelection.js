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


    import AssetLand from "./AssetLand";
    import AssetBuildings from "./AssetBuildings";

    
    const createAssetcommon = () => ({

      assetcommonselection: "",
    
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

    // AST_Type && AST_Type.map((AST_type_mdms) => {
    //     AST_type.push(`${AST_type_mdms.code}`);
    //   });

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
                      name={"LandType"}
                      defaultValue={assetscommon?.LandType}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "LandType"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "LandType" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.LandType ? errors?.LandType?.message : ""}</CardLabelError>


              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_LAND_AREA") }</CardLabel>
                <div className="field">
                    <Controller
                    control={control}
                    name={"Area"}
                    defaultValue={assetscommon?.Area}
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
                        autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Area"}
                        onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "Area" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.Area ? errors?.Area?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_ACQUISTION_COST_BOOK_VALUE") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"BookValue"}
                      defaultValue={assetscommon?.BookValue}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "BookValue"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "BookValue" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.BookValue ? errors?.BookValue?.message : ""}</CardLabelError>

              
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_DATE_DEED_EXECUTION") }</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"DateofDeedExecution"}
                    defaultValue={assetscommon?.DateofDeedExecution}
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
              <CardLabelError style={errorStyle}>{localFormState.touched.DateofDeedExecution ? errors?.DateofDeedExecution?.message : ""}</CardLabelError>

              
                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_DATE_POSSESION") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"DateofPossesion"}
                        defaultValue={assetscommon?.DateofPossesion}
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
              <CardLabelError style={errorStyle}>{localFormState.touched.DateofPossesion ? errors?.DateofPossesion?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_FROM_DEED_TAKEN") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"FromWhomDeedTaken"}
                      defaultValue={assetscommon?.FromWhomDeedTaken}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "FromWhomDeedTaken"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "FromWhomDeedTaken" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.FromWhomDeedTaken ? errors?.FromWhomDeedTaken?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_GOVT_ORDER_NUM") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"GovernmentorderNumber"}
                      defaultValue={assetscommon?.GovernmentorderNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "GovernmentorderNumber"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "GovernmentorderNumber" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.GovernmentorderNumber ? errors?.GovernmentorderNumber?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COLLECT_ORDER_NUM") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"CollectororderNumber"}
                      defaultValue={assetscommon?.CollectororderNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "CollectororderNumber"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "CollectororderNumber" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.CollectororderNumber ? errors?.CollectororderNumber?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COUNCIL_RES_NUM") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"CouncilResolutionNumber"}
                      defaultValue={assetscommon?.CouncilResolutionNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "CouncilResolutionNumber"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "CouncilResolutionNumber" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.CouncilResolutionNumber ? errors?.CouncilResolutionNumber?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_AWARD_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"AwardNumber"}
                      defaultValue={assetscommon?.AwardNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "AwardNumber"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "AwardNumber" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.AwardNumber ? errors?.AwardNumber?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COI") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"OandMCOI"}
                      defaultValue={assetscommon?.OandMCOI}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "OandMCOI"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "OandMCOI" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.OandMCOI ? errors?.OandMCOI?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_OM_TASK") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"OandMTaskDetail"}
                      defaultValue={assetscommon?.OandMTaskDetail}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "OandMTaskDetail"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "OandMTaskDetail" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.OandMTaskDetail ? errors?.OandMTaskDetail?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_TOTAL_COST") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Totalcost"}
                      defaultValue={assetscommon?.Totalcost}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Totalcost"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "Totalcost" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.Totalcost ? errors?.Totalcost?.message : ""}</CardLabelError>

              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_DEPRICIATION_RATE") }</CardLabel>
                <div className="field">
                    <Controller
                    control={control}
                    name={"DepriciationRate"}
                    defaultValue={assetscommon?.DepriciationRate}
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
                        autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "DepriciationRate"}
                        onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "DepriciationRate" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.DepriciationRate ? errors?.DepriciationRate?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COST_AFTER_DEPRICIAT") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Costafterdepriciation"}
                      defaultValue={assetscommon?.Costafterdepriciation}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Costafterdepriciation"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "Costafterdepriciation" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.Costafterdepriciation ? errors?.Costafterdepriciation?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_CURRENT_VALUE") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Currentassetvalue"}
                      defaultValue={assetscommon?.Currentassetvalue}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Currentassetvalue"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "Currentassetvalue" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.Currentassetvalue ? errors?.Currentassetvalue?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_REVENUE_GENERATED") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Revenuegeneratedbyasset"}
                      defaultValue={assetscommon?.Revenuegeneratedbyasset}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Revenuegeneratedbyasset"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "Revenuegeneratedbyasset" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.Revenuegeneratedbyasset ? errors?.Revenuegeneratedbyasset?.message : ""}</CardLabelError>

              <br></br>
              <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_ADDITIONAL_DETAILS")}<br></br></div>
              <br></br> 
              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_OSR_LAND") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"OSRLand"}
                      defaultValue={assetscommon?.OSRLand}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "OSRLand"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "OSRLand" });
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.OSRLand ? errors?.OSRLand?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_FENCED") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"isitfenced"}
                      defaultValue={assetscommon?.isitfenced}
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
              <CardLabelError style={errorStyle}>{localFormState.touched.isitfenced ? errors?.isitfenced?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_ANY_BUILTUP") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"AnyBuiltup"}
                      defaultValue={assetscommon?.AnyBuiltup}
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
            <CardLabelError style={errorStyle}>{localFormState.touched.AnyBuiltup ? errors?.AnyBuiltup?.message : ""}</CardLabelError>

              
              
              <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_HOW_ASSET_USE") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"howassetbeingused"}
                          defaultValue={assetscommon?.howassetbeingused}
                          rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                          }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              // disable={isEditScreen}
                              autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "howassetbeingused"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: assetscommon.key, type: "howassetbeingused" });
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
                            name={"Buildingsno"}
                            defaultValue={assetscommon?.Buildingsno}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                            }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Buildingsno"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "Buildingsno" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.Buildingsno ? errors?.Buildingsno?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_PLOT_AREA") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"plotarea"}
                            defaultValue={assetscommon?.plotarea}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "plotarea"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "plotarea" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.plotarea ? errors?.plotarea?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_PLINTH_AREA") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"plintharea"}
                            defaultValue={assetscommon?.plintharea}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "plintharea"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "plintharea" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.plintharea ? errors?.plintharea?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                        <CardLabel className="card-label-smaller">{t("AST_FLOORS_NO") }</CardLabel>
                        <div className="field">
                        <Controller
                            control={control}
                            name={"floorno"}
                            defaultValue={assetscommon?.floorno}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "floorno"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "floorno" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.floorno ? errors?.floorno?.message : ""}</CardLabelError>

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
                            name={"floorarea"}
                            defaultValue={assetscommon?.floorarea}
                            rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                            render={(props) => (
                            <TextInput
                                value={props.value}
                                // disable={isEditScreen}
                                autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "floorarea"}
                                onChange={(e) => {
                                props.onChange(e.target.value);
                                setFocusIndex({ index: assetscommon.key, type: "floorarea" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.floorarea ? errors?.floorarea?.message : ""}</CardLabelError>

                    <br></br>
                    <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_ADDITIONAL_DETAILS")}<br></br></div>
                    <br></br>

                    <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ACQUISTION_COST_BOOK_VALUE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"BookValue"}
                        defaultValue={assetscommon?.BookValue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "BookValue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "BookValue" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.BookValue ? errors?.BookValue?.message : ""}</CardLabelError>


                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_TOTAL_COST") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Totalcost"}
                        defaultValue={assetscommon?.Totalcost}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Totalcost"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "Totalcost" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Totalcost ? errors?.Totalcost?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_DEPRICIATION_RATE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"DepriciationRate"}
                        defaultValue={assetscommon?.DepriciationRate}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?(%)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                    },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "DepriciationRate"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "DepriciationRate" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.DepriciationRate ? errors?.DepriciationRate?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_COST_AFTER_DEPRICIAT") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Costafterdepriciation"}
                        defaultValue={assetscommon?.Costafterdepriciation}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Costafterdepriciation"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "Costafterdepriciation" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Costafterdepriciation ? errors?.Costafterdepriciation?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_CURRENT_VALUE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Currentassetvalue"}
                        defaultValue={assetscommon?.Currentassetvalue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Currentassetvalue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "Currentassetvalue" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Currentassetvalue ? errors?.Currentassetvalue?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_REVENUE_GENERATED") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Revenuegeneratedbyasset"}
                        defaultValue={assetscommon?.Revenuegeneratedbyasset}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Revenuegeneratedbyasset"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "Revenuegeneratedbyasset" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Revenuegeneratedbyasset ? errors?.Revenuegeneratedbyasset?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_HOW_ASSET_USE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"howassetbeingused"}
                        defaultValue={assetscommon?.howassetbeingused}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "howassetbeingused"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "howassetbeingused" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.howassetbeingused ? errors?.howassetbeingused?.message : ""}</CardLabelError>

                    
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
                        name={"RoadType"}
                        defaultValue={assetscommon?.RoadType}
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
                <CardLabelError style={errorStyle}>{localFormState.touched.RoadType ? errors?.RoadType?.message : ""}</CardLabelError>


              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_TOTAL_LENGTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"TotalLength"}
                      defaultValue={assetscommon?.TotalLength}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "TotalLength"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "TotalLength" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.TotalLength ? errors?.TotalLength?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_ROAD_WIDTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"RoadWidth"}
                      defaultValue={assetscommon?.RoadWidth}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "RoadWidth"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "RoadWidth" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.RoadWidth ? errors?.RoadWidth?.message : ""}</CardLabelError>

              
              <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_SURFACE_TYPE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"SurfaceType"}
                        defaultValue={assetscommon?.SurfaceType}
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
              <CardLabelError style={errorStyle}>{localFormState.touched.SurfaceType ? errors?.SurfaceType?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_PROTECTION_LENGTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"ProtectionLength"}
                      defaultValue={assetscommon?.ProtectionLength}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "ProtectionLength"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "ProtectionLength" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.ProtectionLength ? errors?.ProtectionLength?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_DRAINAGE_CHANNEL_LENGTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"DrainageLength"}
                      defaultValue={assetscommon?.DrainageLength}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "DrainageLength"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "DrainageLength" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.DrainageLength ? errors?.DrainageLength?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_FOOTPATH_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"NumofFootpath"}
                      defaultValue={assetscommon?.NumofFootpath}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "NumofFootpath"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "NumofFootpath" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.NumofFootpath ? errors?.NumofFootpath?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_PEDASTRIAN_CROSSING_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"NumofPedastrianCross"}
                      defaultValue={assetscommon?.NumofPedastrianCross}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "NumofPedastrianCross"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "NumofPedastrianCross" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.NumofPedastrianCross ? errors?.NumofPedastrianCross?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_BUSSTOP_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"NumofBusStop"}
                      defaultValue={assetscommon?.NumofBusStop}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "NumofBusStop"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "NumofBusStop" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.NumofBusStop ? errors?.NumofBusStop?.message : ""}</CardLabelError>

              
              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_METROSTATION_NUMBER") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"NumofMetroStation"}
                      defaultValue={assetscommon?.NumofMetroStation}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "NumofMetroStation"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "NumofMetroStation" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.NumofMetroStation ? errors?.NumofMetroStation?.message : ""}</CardLabelError>

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_CYCLETRACK_LENGTH") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"LengthofCycletrack"}
                      defaultValue={assetscommon?.LengthofCycletrack}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "LengthofCycletrack"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "LengthofCycletrack" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.LengthofCycletrack ? errors?.LengthofCycletrack?.message : ""}</CardLabelError>

              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_LAST_MAINTAINENCE") }</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"LastMaintainence"}
                    defaultValue={assetscommon?.LastMaintainence}
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
            <CardLabelError style={errorStyle}>{localFormState.touched.LastMaintainence ? errors?.LastMaintainence?.message : ""}</CardLabelError>

              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_NEXT_MAINTAINENCE") }</CardLabel>
                <div className="field">
                <Controller
                    control={control}
                    name={"NextMaintainence"}
                    defaultValue={assetscommon?.NextMaintainence}
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
            <CardLabelError style={errorStyle}>{localFormState.touched.NextMaintainence ? errors?.NextMaintainence?.message : ""}</CardLabelError>

              <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ACQUISTION_COST_BOOK_VALUE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"BookValue"}
                        defaultValue={assetscommon?.BookValue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "BookValue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "BookValue" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.BookValue ? errors?.BookValue?.message : ""}</CardLabelError>

                <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_TOTAL_COST") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Totalcost"}
                      defaultValue={assetscommon?.Totalcost}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Totalcost"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "Totalcost" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.Totalcost ? errors?.Totalcost?.message : ""}</CardLabelError>


              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_DEPRICIATION_RATE") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"DepriciationRate"}
                      defaultValue={assetscommon?.DepriciationRate}
                      rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?(%)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"),
                       },
                        }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "DepriciationRate"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "DepriciationRate" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.DepriciationRate ? errors?.DepriciationRate?.message : ""}</CardLabelError>


              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COST_AFTER_DEPRICIAT") }</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Costafterdepriciation"}
                      defaultValue={assetscommon?.Costafterdepriciation}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Costafterdepriciation"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "Costafterdepriciation" });
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
              <CardLabelError style={errorStyle}>{localFormState.touched.Costafterdepriciation ? errors?.Costafterdepriciation?.message : ""}</CardLabelError>



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
                        name={"asquistionDate"}
                        defaultValue={assetscommon?.asquistionDate}
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.asquistionDate ? errors?.asquistionDate?.message : ""}</CardLabelError>

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
                        name={"BookValue"}
                        defaultValue={assetscommon?.BookValue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "BookValue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "BookValue" });
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
                    <CardLabelError style={errorStyle}>{localFormState.touched.BookValue ? errors?.BookValue?.message : ""}</CardLabelError>

                    <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_CURRENT_VALUE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Currentassetvalue"}
                        defaultValue={assetscommon?.Currentassetvalue}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Currentassetvalue"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "Currentassetvalue" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Currentassetvalue ? errors?.Currentassetvalue?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_TOTAL_COST") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"Totalcost"}
                        defaultValue={assetscommon?.Totalcost}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => /^[0-9]+(\.[0-9]+)?$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG"), },
                    }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "Totalcost"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assetscommon.key, type: "Totalcost" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.Totalcost ? errors?.Totalcost?.message : ""}</CardLabelError>




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