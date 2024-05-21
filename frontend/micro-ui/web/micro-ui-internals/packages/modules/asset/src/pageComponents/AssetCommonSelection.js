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
      const { t } = useTranslation();
    
      const { pathname } = useLocation();
      const [assetscommon, setAssets] = useState(formData?.assetscommon || [createAssetcommon()]);

      
      const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
    
      const tenantId = Digit.ULBService.getCurrentTenantId();
      const stateId = Digit.ULBService.getStateId();
    

    
      const { data: AST_Type } = Digit.Hooks.asset.useAssetType(stateId, "ASSET", "assetParentCategory"); // hooks for Asset Parent Category
      let AST_type = [];  //variable name for asset type

      

        
    
    
    
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

     
    
    
      return (
        <React.Fragment>
          <div style={{ marginBottom: "16px" }}>
            <div style={{ border: "1px solid #E3E3E3", padding: "16px", marginTop: "8px" }}>
              {allAssets?.length > 2 ? (
                <div style={{ marginBottom: "16px", padding: "5px", cursor: "pointer", textAlign: "right" }}>
                  X
                </div>
              ) : null}
    
              <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_COMMON_TYPE") + " *"}</CardLabel>
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
              <CardLabelError style={errorStyle}>{localFormState.touched.assetcommonselection ? errors?.assetcommonselection?.message : ""}</CardLabelError>

              {selectedAssetCode === "LAND" && (
                <React.Fragment>
                  <br></br>
                  <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_LAND_DETAILS")}<br></br></div>
                  <br></br>

                  
                <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_LAND_TYPE") + " *"}</CardLabel>
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.LandType ? errors?.LandType?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_LAND_AREA") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Area"}
                      defaultValue={assetscommon?.Area}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.Area ? errors?.Area?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_ACQUISTION_COST_BOOK_VALUE") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"BookValue"}
                      defaultValue={assetscommon?.BookValue}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.BookValue ? errors?.BookValue?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_DATE_DEED_EXECUTION") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"DateofDeedExecution"}
                      defaultValue={assetscommon?.DateofDeedExecution}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "DateofDeedExecution"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "DateofDeedExecution" });
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.DateofDeedExecution ? errors?.DateofDeedExecution?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_DATE_POSSESION") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"DateofPossesion"}
                      defaultValue={assetscommon?.DateofPossesion}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "DateofPossesion"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "DateofPossesion" });
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.DateofPossesion ? errors?.DateofPossesion?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_FROM_DEED_TAKEN") + " *"}</CardLabel>
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.FromWhomDeedTaken ? errors?.FromWhomDeedTaken?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_GOVT_ORDER_NUM") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"GovernmentorderNumber"}
                      defaultValue={assetscommon?.GovernmentorderNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.GovernmentorderNumber ? errors?.GovernmentorderNumber?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COLLECT_ORDER_NUM") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"CollectororderNumber"}
                      defaultValue={assetscommon?.CollectororderNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.CollectororderNumber ? errors?.CollectororderNumber?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COUNCIL_RES_NUM") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"CouncilResolutionNumber"}
                      defaultValue={assetscommon?.CouncilResolutionNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.CouncilResolutionNumber ? errors?.CouncilResolutionNumber?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_AWARD_NUMBER") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"AwardNumber"}
                      defaultValue={assetscommon?.AwardNumber}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.AwardNumber ? errors?.AwardNumber?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COI") + " *"}</CardLabel>
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.OandMCOI ? errors?.OandMCOI?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_OM_TASK") + " *"}</CardLabel>
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.OandMTaskDetail ? errors?.OandMTaskDetail?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_TOTAL_COST") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Totalcost"}
                      defaultValue={assetscommon?.Totalcost}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.Totalcost ? errors?.Totalcost?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_DEPRICIATION_RATE") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"DepriciationRate"}
                      defaultValue={assetscommon?.DepriciationRate}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.DepriciationRate ? errors?.DepriciationRate?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_COST_AFTER_DEPRICIAT") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Costafterdepriciation"}
                      defaultValue={assetscommon?.Costafterdepriciation}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.Costafterdepriciation ? errors?.Costafterdepriciation?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_CURRENT_VALUE") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Currentassetvalue"}
                      defaultValue={assetscommon?.Currentassetvalue}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.Currentassetvalue ? errors?.Currentassetvalue?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_REVENUE_GENERATED") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"Revenuegeneratedbyasset"}
                      defaultValue={assetscommon?.Revenuegeneratedbyasset}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
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
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair>
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.Revenuegeneratedbyasset ? errors?.Revenuegeneratedbyasset?.message : ""}</CardLabelError>*/}

              <br></br>
              <div style={{fontWeight: 'bold', fontSize: '24px'}}>{t("AST_ADDITIONAL_DETAILS")}<br></br></div>
              <br></br> 
              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_OSR_LAND") + " *"}</CardLabel>
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
                  <CardLabel className="card-label-smaller">{t("AST_FENCED") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"isitfenced"}
                      defaultValue={assetscommon?.isitfenced}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "isitfenced"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "isitfenced" });
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.isitfenced ? errors?.isitfenced?.message : ""}</CardLabelError>*/}

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_ANY_BUILTUP") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"AnyBuiltup"}
                      defaultValue={assetscommon?.AnyBuiltup}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "AnyBuiltup"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "AnyBuiltup" });
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
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.AnyBuiltup ? errors?.AnyBuiltup?.message : ""}</CardLabelError>*/}

              {/* <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_TREES_TYPE") + " *"}</CardLabel>
                  <div className="field">
                  <Controller
                      control={control}
                      name={"TypeofTrees"}
                      defaultValue={assetscommon?.TypeofTrees}
                      rules={{
                      required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                      validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                      }}
                      render={(props) => (
                      <TextInput
                          value={props.value}
                          // disable={isEditScreen}
                          autoFocus={focusIndex.index === assetscommon?.key && focusIndex.type === "TypeofTrees"}
                          onChange={(e) => {
                          props.onChange(e.target.value);
                          setFocusIndex({ index: assetscommon.key, type: "TypeofTrees" });
                          }}
                          onBlur={(e) => {
                          setFocusIndex({ index: -1 });
                          props.onBlur(e);
                          }}
                      />
                      )}
                  />
                  </div>
              </LabelFieldPair> */}
              {/*<CardLabelError style={errorStyle}>{localFormState.touched.TypeofTrees ? errors?.TypeofTrees?.message : ""}</CardLabelError>*/}

              
              <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_HOW_ASSET_USE") + " *"}</CardLabel>
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
              {selectedAssetCode === 'BUILDINGS' && <AssetBuildings />}

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