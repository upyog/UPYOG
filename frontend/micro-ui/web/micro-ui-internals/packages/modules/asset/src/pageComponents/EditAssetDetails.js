import {
    CardLabel,
    CardLabelError,
    LabelFieldPair,
    TextInput,
    Toast,
    Dropdown
    } from "@nudmcdgnpm/digit-ui-react-components";
    import _ from "lodash";
    import React, { useEffect, useState } from "react";
    import { Controller, useForm } from "react-hook-form";
    import { useTranslation } from "react-i18next";
    import { useParams } from "react-router-dom";
  


    const editnewDetails = () => ({
        
        key: Date.now(),
    });

  

    const EditAssetDetails = ({ config, onSelect, formData, setError, clearErrors }) => {
    const { t } = useTranslation();
    const [editNewAssetDetails, seteditAssignDetails] = useState(formData?.editNewAssetDetails || [editnewDetails()]);
    const { id:applicationNo } = useParams();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t,tenantId, applicationNo);
    let comingDataFromAPI = applicationDetails?.applicationData?.applicationData

    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });


    useEffect(() => {
    onSelect(config?.key, editNewAssetDetails);
    }, [editNewAssetDetails]);

    const { data: warrantyperiod } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "Warranty" }],
      {
        select: (data) => {
            const formattedData = data?.["ASSET"]?.["Warranty"]
            return formattedData;
        },
    }); 
    let warrantyTime = [];

    warrantyperiod && warrantyperiod.map((warrantytime) => {
      warrantyTime.push({i18nKey: `${warrantytime.name}`, code: `${warrantytime.code}`, value: `${warrantytime.name}`})
    }) 


    const commonProps = {
        focusIndex,
        allAssets: editNewAssetDetails,
        setFocusIndex,
        formData,
        seteditAssignDetails,
        t,
        setError,
        clearErrors,
        config,
        comingDataFromAPI,
        warrantyTime
        
    };



    return (
        <React.Fragment>
        {editNewAssetDetails.map((editNewAssetDetails, index) => (
            <OwnerForm key={editNewAssetDetails.key} index={index} editNewAssetDetails={editNewAssetDetails} {...commonProps} />
        ))}
        </React.Fragment>
    )
    };
    const OwnerForm = (_props) => {
    const {
        editNewAssetDetails,
        focusIndex,     
        seteditAssignDetails,
        t,
        config,
        setError,
        clearErrors,
        setFocusIndex,
        comingDataFromAPI,
        warrantyTime
    } = _props;


    const [showToast, setShowToast] = useState(null);
    const { control, formState: localFormState, watch, trigger} = useForm();
    const formValue = watch();
    const { errors } = localFormState;
    const [part, setPart] = React.useState({});

    let isEdit = true;

    const convertToObject = (String) => String ? { i18nKey: String, code: String, value: String } : null;

    

  useEffect(() => {
    if (!_.isEqual(part, formValue)) {
      setPart({ ...formValue });
      seteditAssignDetails(prev => prev.map(o => o.key === editNewAssetDetails.key ? { ...o, ...formValue } : o));
      trigger();
    }
  }, [formValue]);

    useEffect(() => {
      if (Object.keys(errors).length && !_.isEqual(localFormState.errors[config.key]?.type || {}, errors))
        setError(config.key, { type: errors });
      else if (!Object.keys(errors).length && localFormState.errors[config.key]) 
        clearErrors(config.key);
    }, [errors]);

    

    const errorStyle = { width: "70%", marginLeft: "30%", fontSize: "12px", marginTop: "-21px" };


    

    return (

        
        <React.Fragment>
        <div style={{ marginBottom: "16px" }}>
            <div style={{ border: "1px solid #E3E3E3", padding: "16px", marginTop: "8px" }}>
            {comingDataFromAPI?.assetParentCategory==="IT" && (
            <React.Fragment>
                    <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_BRAND") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"brand"}
                          defaultValue={comingDataFromAPI?.additionalDetails?.brand}
                          rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (val) => (/^[a-zA-Z/-]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                          }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={false}
                              autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === "brand"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editNewAssetDetails.key, type: "brand" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.brand ? errors?.brand?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_INVOICE_DATE") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"invoiceDate"}
                        defaultValue={comingDataFromAPI?.additionalDetails?.invoiceDate}
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
                <CardLabelError style={errorStyle}>{localFormState.touched.invoiceDate ? errors?.invoiceDate?.message : ""}</CardLabelError>

                <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_MANUFACTURER") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"manufacturer"}
                          defaultValue={comingDataFromAPI?.additionalDetails?.manufacturer}
                          rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (val) => (/^[a-zA-Z/-]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                          }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={false}
                              autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === "manufacturer"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editNewAssetDetails.key, type: "manufacturer" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.manufacturer ? errors?.manufacturer?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_PURCHASE_COST") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"purchaseCost"}
                          defaultValue={comingDataFromAPI?.additionalDetails?.purchaseCost}
                          rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (val) => (/^[0-9]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                          }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={false}
                              autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === "purchaseCost"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editNewAssetDetails.key, type: "purchaseCost" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.purchaseCost ? errors?.purchaseCost?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_PURCHASE_ORDER") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"purchaseOrderNumber"}
                          defaultValue={comingDataFromAPI?.additionalDetails?.purchaseOrderNumber}
                          rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (val) => (/^[a-zA-Z0-9/-]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                          }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={false}
                              autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === "purchaseOrderNumber"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editNewAssetDetails.key, type: "purchaseOrderNumber" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.purchaseOrderNumber ? errors?.purchaseOrderNumber?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_PURCHASE_DATE") + " *"}</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"purchaseDate"}
                        defaultValue={comingDataFromAPI?.additionalDetails?.purchaseDate}
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
                <CardLabelError style={errorStyle}>{localFormState.touched.purchaseDate ? errors?.purchaseDate?.message : ""}</CardLabelError>

                <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_CURRENT_LOCATION") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"currentLocation"}
                          defaultValue={comingDataFromAPI?.additionalDetails?.currentLocation}
                          rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (val) => (/^[0-9]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                          }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={true}
                              autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === "currentLocation"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editNewAssetDetails.key, type: "currentLocation" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.purchaseOrderNumber ? errors?.purchaseOrderNumber?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_ASSET_AGE") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"assetAge"}
                          defaultValue={comingDataFromAPI?.additionalDetails?.assetAge}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={false}
                              autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === "assetAge"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editNewAssetDetails.key, type: "assetAge" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.assetAge ? errors?.assetAge?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_WARRANTY")}</CardLabel>
                  <Controller
                  control={control}
                  name={"warranty"}
                  defaultValue={comingDataFromAPI?.additionalDetails?.warranty}
                  render={(props) => (
                      <Dropdown
                      className="form-field"
                      selected={props.value}
                      select={props.onChange}
                      onBlur={props.onBlur}
                      disable={isEdit}
                      option={warrantyTime}
                      optionKey="i18nKey"
                      t={t}
                      />
                  )}
                  />
                  </LabelFieldPair>
                  <CardLabelError style={errorStyle}>{localFormState.touched.assettype ? errors?.assettype?.message : ""}</CardLabelError>



                  <br></br>
                  <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_PINCODE") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"pincode"}
                          defaultValue={comingDataFromAPI?.addressDetails?.pincode}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={true}
                              autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === "pincode"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editNewAssetDetails.key, type: "pincode" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.pincode ? errors?.pincode?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("MYCITY_CODE_LABEL") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"city"}
                          defaultValue={comingDataFromAPI?.addressDetails?.city}
                          rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (val) => (/^[a-zA-Z/-]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                          }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={true}
                              autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === "city"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editNewAssetDetails.key, type: "city" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.city ? errors?.city?.message : ""}</CardLabelError>


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

    export default EditAssetDetails;