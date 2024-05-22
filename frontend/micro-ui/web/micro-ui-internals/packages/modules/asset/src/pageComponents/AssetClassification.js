import {
    CardLabel,
    CardLabelError,
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
  // import { stringReplaceAll, CompareTwoObjects } from "../utils";
  
  const createAssetDetails = () => ({
  
   

    assetclassification: "",
    assettype: "",
    assetsubtype: "",
  
    key: Date.now(),
  });
  
  const AssetClassification = ({ config, onSelect, userType, formData, setError, formState, clearErrors }) => {
    const { t } = useTranslation();
  
    const { pathname } = useLocation();
    const [assets, setAssets] = useState(formData?.assets || [createAssetDetails()]);

    console.log("Assets",assets);

    
    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
  
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();
  
  
  
  
    const { data: Menu_Asset } = Digit.Hooks.asset.useAssetClassification(stateId, "ASSET", "assetClassification"); // hook for asset classification Type
  
    const { data: Asset_Type } = Digit.Hooks.asset.useAssetType(stateId, "ASSET", "assetParentCategory"); 

    const { data: Asset_Sub_Type } = Digit.Hooks.asset.useAssetSubType(stateId, "ASSET", "assetCategory");  // hooks for Asset Parent Category

   

    let menu_Asset = [];   //variable name for assetCalssification

    let asset_type = [];  //variable name for asset type

    let asset_sub_type = [];  //variable name for asset sub  parent caregory
    

    Menu_Asset &&
    Menu_Asset.map((asset_mdms) => {
        menu_Asset.push({ i18nKey: `ASSET_CLASS_${asset_mdms.code}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
    });
  
    

      
  
  
  
    Asset_Type &&
    Asset_Type.map((asset_type_mdms) => {
        if (asset_type_mdms.assetClassification == assets[0]?.assetclassification?.code) {
            asset_type.push({
            i18nKey: `ASSET_TYPE_${asset_type_mdms.code}`,
            code: `${asset_type_mdms.code}`,
            value: `${asset_type_mdms.name}`
          });
        }
  
      });


      Asset_Sub_Type &&
      Asset_Sub_Type.map((asset_sub_type_mdms) => {
          if (asset_sub_type_mdms.assetParentCategory == assets[0]?.assettype?.code) {
              asset_sub_type.push({
              i18nKey: `ASSET_SUB_TYPE_${asset_sub_type_mdms.code}`,
              code: `${asset_sub_type_mdms.code}`,
              value: `${asset_sub_type_mdms.name}`
            });
          }
    
        });

    useEffect(() => {
      onSelect(config?.key, assets);
  
    }, [assets]);
  
  
  
  
    const commonProps = {
      focusIndex,
      allAssets: assets,
      setFocusIndex,
      formData,
      formState,
      setAssets,
      t,
      setError,
      clearErrors,
      config,
      menu_Asset,
      asset_type,
      asset_sub_type
    
    };
  
    return (
      <React.Fragment>
        {assets.map((assets, index) => (
          <OwnerForm key={assets.key} index={index} assets={assets} {...commonProps} />
        ))}
      </React.Fragment>
    )
  };
  
  const OwnerForm = (_props) => {
    const {
      assets,
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
      menu_Asset,
      asset_type,
      asset_sub_type
    
  
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
        setAssets((prev) => prev.map((o) => (o.key && o.key === assets.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
        trigger();
      }
    }, [formValue]);
  
    useEffect(() => {
      if (Object.keys(errors).length && !_.isEqual(formState.errors[config.key]?.type || {}, errors))
        setError(config.key, { type: errors });
      else if (!Object.keys(errors).length && formState.errors[config.key]) clearErrors(config.key);
    }, [errors]);
  
    const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };
  
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
              <CardLabel className="card-label-smaller">{t("AST_CATEGORY") + " *"}</CardLabel>
              <Controller
                control={control}
                name={"assetclassification"}
                defaultValue={assets?.assetclassification}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
  
                    className="form-field"
                    selected={props.value}
                    select={props.onChange}
                    onBlur={props.onBlur}
                    option={menu_Asset}
                    optionKey="i18nKey"
                    t={t}
                  />
  
                )}
  
              />
  
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.assetclassification ? errors?.assetclassification?.message : ""}</CardLabelError>
            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("AST_PARENT_CATEGORY") + " *"}</CardLabel>
              <Controller
                control={control}
                name={"assettype"}
                defaultValue={assets?.assettype}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                    className="form-field"
                    selected={props.value}
                    select={props.onChange}
                    onBlur={props.onBlur}
                    option={asset_type}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.assettype ? errors?.assettype?.message : ""}</CardLabelError>
            <LabelFieldPair>
              <CardLabel className="card-label-smaller">{t("AST_SUB_CATEGORY") + " *"}</CardLabel>
              <Controller
                control={control}
                name={"assetsubtype"}
                defaultValue={assets?.assetsubtype}
                rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                render={(props) => (
                  <Dropdown
                    className="form-field"
                    selected={props.value}
                    select={props.onChange}
                    onBlur={props.onBlur}
                    option={asset_sub_type}
                    optionKey="i18nKey"
                    t={t}
                  />
                )}
              />
            </LabelFieldPair>
            <CardLabelError style={errorStyle}>{localFormState.touched.assetsubtype ? errors?.assetsubtype?.message : ""}</CardLabelError>

            {/* {asset_type?.[0]?.code==="LAND" && (

            )} */}
  
          
          
  
  
  
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
  
  export default AssetClassification;