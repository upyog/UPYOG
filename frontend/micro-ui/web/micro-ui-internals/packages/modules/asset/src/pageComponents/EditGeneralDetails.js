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
    


      const editAssetDetails = () => ({
          financialYear:"",
          assetclassification:"",
          assettype:"",
          assetparentsubCategory:"",
          BookPagereference:"",
          AssetName:"",
          assetsubtype:"",
          Assetdescription:"",
          Department:"",
          sourceOfFinance:"",
          assetclassification:"",
          key: Date.now(),
      });

    

      const EditGeneralDetails = ({ config, onSelect, formData, setError, clearErrors }) => {
      const { t } = useTranslation();
      const stateId = Digit.ULBService.getStateId();
      const [editAssignDetails, seteditAssignDetails] = useState(formData?.editAssignDetails || [editAssetDetails()]);
      const { id:applicationNo } = useParams();
      const tenantId = Digit.ULBService.getCurrentTenantId();
      const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t,tenantId, applicationNo);
      let comingDataFromAPI = applicationDetails?.applicationData?.applicationData

      const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
      useEffect(() => {
      onSelect(config?.key, editAssignDetails);
      }, [editAssignDetails]);


    

      


      const { data: Menu_Asset } = Digit.Hooks.asset.useAssetClassification(stateId, "ASSET", "assetClassification"); // hook for asset classification Type
      
      const { data: Asset_Type } = Digit.Hooks.asset.useAssetType(stateId, "ASSET", "assetParentCategory"); 

      const { data: Asset_Sub_Type } = Digit.Hooks.asset.useAssetSubType(stateId, "ASSET", "assetCategory");  // hooks for Asset Parent Category
      
      const { data: Asset_Parent_Sub_Type } = Digit.Hooks.asset.useAssetparentSubType(stateId, "ASSET", "assetSubCategory");




        const { data: sourceofFinanceMDMS } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "SourceFinance" }],
        {
          select: (data) => {
              const formattedData = data?.["ASSET"]?.["SourceFinance"]
              const activeData = formattedData?.filter(item => item.active === true);
              return activeData;
          },
      });   // Note : used direct custom MDMS to get the Data ,Do not copy and paste without understanding the Context

      let sourcefinance = [];

      sourceofFinanceMDMS && sourceofFinanceMDMS.map((finance) => {
        sourcefinance.push({i18nKey: `AST_${finance.code}`, code: `${finance.code}`, value: `${finance.name}`})
      }) 

      const { data: currentFinancialYear } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "FinancialYear" }],
        {
          select: (data) => {
              const formattedData = data?.["ASSET"]?.["FinancialYear"]
              return formattedData;
          },
      }); 

      let financal = []; 

      currentFinancialYear && currentFinancialYear.map((financialyear) => {
        financal.push({i18nKey: `${financialyear.code}`, code: `${financialyear.code}`, value: `${financialyear.name}`})
      }) 



      const { data: departmentName } = Digit.Hooks.useCustomMDMS(Digit.ULBService.getStateId(), "ASSET", [{ name: "Department" }],
        {
          select: (data) => {
              const formattedData = data?.["ASSET"]?.["Department"]
              const activeData = formattedData?.filter(item => item.active === true);
              return activeData;
          },
      }); 
      let departNamefromMDMS = [];

      departmentName && departmentName.map((departmentname) => {
        departNamefromMDMS.push({i18nKey: `${departmentname.name}`, code: `${departmentname.code}`, value: `${departmentname.name}`})
      }) 

      





        let menu_Asset = [];   //variable name for assetCalssification

        let asset_type = [];  //variable name for asset type

        let asset_sub_type = [];  //variable name for asset sub  parent caregory

        let asset_parent_sub_category = [];

        

        Menu_Asset &&
        Menu_Asset.map((asset_mdms) => {
            menu_Asset.push({ i18nKey: `${asset_mdms.name}`, code: `${asset_mdms.code}`, value: `${asset_mdms.name}` });
        });

        Asset_Type &&
        Asset_Type.map((asset_type_mdms) => {
            if (asset_type_mdms.assetClassification == editAssignDetails?.[0]?.assetclassification?.code) 
              {
                asset_type.push({
                i18nKey: `${asset_type_mdms.name}`,
                code: `${asset_type_mdms.code}`,
                value: `${asset_type_mdms.name}`
              });
            }
      
          });


          Asset_Sub_Type &&
          Asset_Sub_Type.map((asset_sub_type_mdms) => {
              if (asset_sub_type_mdms.assetParentCategory == editAssignDetails?.[0]?.assettype?.code) 
                  {
                  asset_sub_type.push({
                  i18nKey: `${asset_sub_type_mdms.name}`,
                  code: `${asset_sub_type_mdms.code}`,
                  value: `${asset_sub_type_mdms.name}`
                });
              }
        
            });

            Asset_Parent_Sub_Type &&
            Asset_Parent_Sub_Type.map((asset_parent_mdms) => {
              if (asset_parent_mdms.assetCategory ==  editAssignDetails?.[0]?.assetsubtype?.code)
              {
                asset_parent_sub_category.push({
                  i18nKey: `${asset_parent_mdms.name}`,
                  code: `${asset_parent_mdms.code}`,
                  value: `${asset_parent_mdms.name}`
                });
              }
        
            });


      const commonProps = {
          focusIndex,
          allAssets: editAssignDetails,
          setFocusIndex,
          formData,
          seteditAssignDetails,
          t,
          setError,
          clearErrors,
          config,
          menu_Asset,
          asset_type,
          asset_sub_type,
          asset_parent_sub_category,
          departNamefromMDMS,
          financal,
          sourcefinance,
          comingDataFromAPI
          
      };



      return (
          <React.Fragment>
          {editAssignDetails.map((editAssignDetails, index) => (
              <OwnerForm key={editAssignDetails.key} index={index} editAssignDetails={editAssignDetails} {...commonProps} />
          ))}
          </React.Fragment>
      )
      };
      const OwnerForm = (_props) => {
      const {
          editAssignDetails,
          focusIndex,     
          seteditAssignDetails,
          t,
          config,
          setError,
          clearErrors,
          setFocusIndex,
          menu_Asset,
          asset_type,
          asset_sub_type,
          asset_parent_sub_category,
          departNamefromMDMS,
          financal,
          sourcefinance,
          comingDataFromAPI
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
        seteditAssignDetails(prev => prev.map(o => o.key === editAssignDetails.key ? { ...o, ...formValue } : o));
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

              <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_FINANCIAL_YEAR")}</CardLabel>
                  <Controller
                  control={control}
                  name={"financialYear"}
                  defaultValue={convertToObject(comingDataFromAPI?.financialYear)}
                  render={(props) => (
                      <Dropdown
                      className="form-field"
                      selected={props.value}
                      select={props.onChange}
                      onBlur={props.onBlur}
                      disable={false}
                      option={financal}
                      optionKey="i18nKey"
                      t={t}
                      />
                  )}
                  />
                  </LabelFieldPair>
                  <CardLabelError style={errorStyle}>{localFormState.touched.financialYear ? errors?.financialYear?.message : ""}</CardLabelError>
                  <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_SOURCE_FINANCE")}</CardLabel>
                  <Controller
                  control={control}
                  name={"sourceOfFinance"}
                  defaultValue={convertToObject(comingDataFromAPI?.sourceOfFinance)}
                  render={(props) => (
                      <Dropdown
                      className="form-field"
                      selected={props.value}
                      select={props.onChange}
                      onBlur={props.onBlur}
                      option={sourcefinance}
                      optionKey="i18nKey"
                      t={t}
                      />
                  )}
                  />
                  </LabelFieldPair>
                  <CardLabelError style={errorStyle}>{localFormState.touched.sourceOfFinance ? errors?.sourceOfFinance?.message : ""}</CardLabelError>
                  <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_CATEGORY")}</CardLabel>
                  <Controller
                  control={control}
                  name={"assetclassification"}
                  defaultValue={convertToObject(comingDataFromAPI?.assetClassification)}
                  render={(props) => (
                      <Dropdown
                      className="form-field"
                      selected={props.value}
                      select={props.onChange}
                      onBlur={props.onBlur}
                      disable={isEdit}
                      option={menu_Asset}
                      optionKey="i18nKey"
                      t={t}
                      />
                  )}
                  />
                  </LabelFieldPair>
                  <CardLabelError style={errorStyle}>{localFormState.touched.assetclassification ? errors?.assetclassification?.message : ""}</CardLabelError>
                  <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_PARENT_CATEGORY")}</CardLabel>
                  <Controller
                  control={control}
                  name={"assettype"}
                  defaultValue={convertToObject(comingDataFromAPI?.assetParentCategory)}
                  render={(props) => (
                      <Dropdown
                      className="form-field"
                      selected={props.value}
                      select={props.onChange}
                      onBlur={props.onBlur}
                      disable={isEdit}
                      option={asset_type}
                      optionKey="i18nKey"
                      t={t}
                      />
                  )}
                  />
                  </LabelFieldPair>
                  <CardLabelError style={errorStyle}>{localFormState.touched.assettype ? errors?.assettype?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_SUB_CATEGORY")}</CardLabel>
                  <Controller
                  control={control}
                  name={"assetsubtype"}
                  defaultValue={convertToObject(comingDataFromAPI?.assetCategory)}
                  render={(props) => (
                      <Dropdown
                      className="form-field"
                      selected={props.value}
                      select={props.onChange}
                      onBlur={props.onBlur}
                      disable={false}
                      option={asset_sub_type}
                      optionKey="i18nKey"
                      t={t}
                      />
                  )}
                  />
                  </LabelFieldPair>
                  <CardLabelError style={errorStyle}>{localFormState.touched.assetsubtype ? errors?.assetsubtype?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_CATEGORY_SUB_CATEGORY")}</CardLabel>
                  <Controller
                  control={control}
                  name={"assetparentsubCategory"}
                  defaultValue={convertToObject(comingDataFromAPI?.assetSubCategory)}
                  render={(props) => (
                      <Dropdown
                      className="form-field"
                      selected={props.value}
                      select={props.onChange}
                      onBlur={props.onBlur}
                      disable={false}
                      option={asset_parent_sub_category}
                      optionKey="i18nKey"
                      t={t}
                      />
                  )}
                  />
                  </LabelFieldPair>
                  <CardLabelError style={errorStyle}>{localFormState.touched.assetparentsubCategory ? errors?.assetparentsubCategory?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_BOOK_REF_SERIAL_NUM") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"BookPagereference"}
                          defaultValue={comingDataFromAPI?.assetBookRefNo}
                          rules={{
                          required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                          validate: { pattern: (val) => (/^[a-zA-Z0-9/-\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                          }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={false}
                              autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "BookPagereference"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editAssignDetails.key, type: "BookPagereference" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.BookPagereference ? errors?.BookPagereference?.message : ""}</CardLabelError>

                  <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("AST_NAME") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"AssetName"}
                          defaultValue={comingDataFromAPI?.assetName}
                          rules={{
                              required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                              validate: {
                                  pattern: (val) => 
                                    /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG")
                                },
                            }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={false}
                              autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "AssetName"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editAssignDetails.key, type: "AssetName" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.AssetName ? errors?.AssetName?.message : ""}</CardLabelError>
                  
                  <LabelFieldPair>
                      <CardLabel className="card-label-smaller">{t("ASSET_DESCRIPTION") }</CardLabel>
                      <div className="field">
                      <Controller
                          control={control}
                          name={"Assetdescription"}
                          defaultValue={comingDataFromAPI?.description}
                          rules={{
                              required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                              validate: {
                                  pattern: (val) => 
                                    /^[a-zA-Z\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG")
                                },
                            }}
                          render={(props) => (
                          <TextInput
                              value={props.value}
                              disable={false}
                              autoFocus={focusIndex.index === editAssignDetails?.key && focusIndex.type === "Assetdescription"}
                              onChange={(e) => {
                              props.onChange(e.target.value);
                              setFocusIndex({ index: editAssignDetails.key, type: "Assetdescription" });
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
                  <CardLabelError style={errorStyle}>{localFormState.touched.Assetdescription ? errors?.Assetdescription?.message : ""}</CardLabelError>
                  
                  <LabelFieldPair>
                  <CardLabel className="card-label-smaller">{t("AST_DEPARTMENT")}</CardLabel>
                  <Controller
                  control={control}
                  name={"Department"}
                  defaultValue={convertToObject(comingDataFromAPI?.department)}
                  render={(props) => (
                      <Dropdown
                      className="form-field"
                      selected={props.value}
                      disable={false}
                      select={props.onChange}
                      onBlur={props.onBlur}
                      option={departNamefromMDMS}
                      optionKey="i18nKey"
                      t={t}
                      />
                  )}
                  />
                  </LabelFieldPair>
                  <CardLabelError style={errorStyle}>{localFormState.touched.Department ? errors?.Department?.message : ""}</CardLabelError>
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

      export default EditGeneralDetails;