import { CardLabel, CardLabelError, LabelFieldPair, TextInput, Toast, Dropdown } from "@nudmcdgnpm/digit-ui-react-components";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";

const editnewDetails = () => ({
  key: Date.now()
});
const EditAssetDetails = ({
  config,
  onSelect,
  formData,
  setError,
  clearErrors
}) => {
  const {
    t
  } = useTranslation();
  const [editNewAssetDetails, seteditAssignDetails] = useState(formData?.editNewAssetDetails || [editnewDetails()]);
  const {
    id: applicationNo
  } = useParams();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const {
    data: applicationDetails
  } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);
  let comingDataFromAPI = applicationDetails?.applicationData?.applicationData;
  const [focusIndex, setFocusIndex] = useState({
    index: -1,
    type: ""
  });
  useEffect(() => {
    onSelect(config?.key, editNewAssetDetails);
  }, [editNewAssetDetails]);

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
    // warrantyTime
  };
  return <React.Fragment>
      {editNewAssetDetails.map((editNewAssetDetails, index) => <OwnerForm key={editNewAssetDetails.key} index={index} editNewAssetDetails={editNewAssetDetails} {...commonProps} />)}
      {/* <OwnerForm key={editNewAssetDetails.key} index={0} editNewAssetDetails={editNewAssetDetails} {...commonProps} /> */}
    </React.Fragment>;
};
const OwnerForm = _props => {
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
    // warrantyTime
  } = _props;
  let formJson = [];

  //  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateTenantId = Digit.ULBService.getStateId();

  // This call with stateTenantId (Get state-level data)
  const stateResponseObject = Digit.Hooks.useEnabledMDMS(stateTenantId, "ASSET", [{
    name: "AssetParentCategoryFields"
  }], {
    select: data => {
      const formattedData = data?.["ASSET"]?.["AssetParentCategoryFields"];
      return formattedData;
    }
  });
  let combinedData;



  // if city level master is not available then fetch  from state-level
   if (stateResponseObject) {
    combinedData = stateResponseObject;
  } else {
    combinedData = [];
  }
  if (Array.isArray(combinedData?.data) && combinedData.data.length > 0) {
    formJson = combinedData.data
      .filter((category) => {
        console.log("category", category);
        const isMatch = category.assetParentCategory === comingDataFromAPI?.assetParentCategory || category.assetParentCategory === "COMMON";
        return isMatch;
      })
      .map((category) => category.fields) // Extract the fields array
      .flat() // Flatten the fields array
      // .filter(({field}) => field?.active === true); // Filter by active status
  } else {
    console.log("combinedData.data is not an array or is empty.");
  }
  const [showToast, setShowToast] = useState(null);
  const {
    control,
    formState: localFormState,
    watch,
    trigger
  } = useForm();
  const formValue = watch();
  const {
    errors
  } = localFormState;
  const [part, setPart] = React.useState({});
  let isEdit = true;
  const convertToObject = String => String ? {
    i18nKey: String,
    code: String,
    value: String
  } : null;
  useEffect(() => {
    if (!_.isEqual(part, formValue)) {
      setPart({
        ...formValue
      });
      seteditAssignDetails(prev => prev.map(o => o.key === editNewAssetDetails.key ? {
        ...o,
        ...formValue
      } : o));
      trigger();
    }
  }, [formValue]);
  useEffect(() => {
    if (Object.keys(errors).length && !_.isEqual(localFormState.errors[config.key]?.type || {}, errors)) setError(config.key, {
      type: errors
    });else if (!Object.keys(errors).length && localFormState.errors[config.key]) clearErrors(config.key);
  }, [errors]);
  const regexPattern = columnType => {
    if (!columnType) {
      return "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
    } else if (columnType === "number") {
      return "^d+(.d+)?$";
    } else if (columnType === "text") {
      return "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
    } else {
      return "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
    }
  };
  const fetchCurrentLocation = name => {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(position => {
        const {
          latitude,
          longitude
        } = position.coords;
        setAssetDetails(prevDetails => ({
          ...prevDetails,
          [name]: `${latitude}, ${longitude}` // Update the specific field
        }));
      }, error => {
        console.error("Error getting location:", error);
      });
    } else {
      alert("Geolocation is not supported by your browser.");
    }
  };

  return <React.Fragment>
      <div className="asset-auto-101">
        <div className="asset-auto-102">

          <React.Fragment>
            {
              formJson?.map((row, index) => (
                <div key={index}>
                  {row.type === "date" ?
                    (
                      <div key={index}>
                        <LabelFieldPair>
                          <CardLabel className="card-label-smaller">{t(row.code)}</CardLabel>
                          <div className="field">
                            <Controller
                              control={control}
                              name={row.name}
                              defaultValue={comingDataFromAPI?.additionalDetails[row.name] || ""}
                              rules={{
                                required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                              }}
                              render={({field}) => (
                                <TextInput
                                  value={field.value}
                                  type={"date"}
                                  max={new Date().toISOString().split("T")[0]}
                                  autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === row.name}
                                  onChange={(e) => {
                                    field.onChange(e.target.value);
                                    setFocusIndex({ index: editNewAssetDetails.key, type: row.name });
                                  }}
                                  onBlur={(e) => {
                                    setFocusIndex({ index: -1 });
                                    field.onBlur(e);
                                  }}
                                />
                              )}
                            />
                          </div>
                        </LabelFieldPair>
                      </div>
                    )
                    : row.type == "dropdown" ?
                      (
                        <div key={index}>
                          <LabelFieldPair>
                            <CardLabel className="card-label-smaller">{t(row.code)}</CardLabel>
                            <Controller
                              control={control}
                              name={row.name}
                              defaultValue={comingDataFromAPI?.additionalDetails[row.name] || ""}
                              render={({field}) => (
                                <Dropdown
                                  className="form-field"
                                  selected={field.value}
                                  select={field.onChange}
                                  onBlur={field.onBlur}
                                  option={row.options}
                                  optionKey="i18nKey"
                                  t={t}
                                />
                              )}
                            />

                          </LabelFieldPair>
                          
                        </div>
                      ) : row.addCurrentLocationButton === true ?
                        (
                          <div key={index}>
                            <LabelFieldPair>
                              <CardLabel className="card-label-smaller">{t(row.code)}</CardLabel>
                              <div className="field">
                                <Controller
                                  control={control}
                                  name={row.name}
                                  defaultValue={comingDataFromAPI?.additionalDetails[row.name] || ""}
                                  rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                                  }}
                                  render={({field}) => (
                                    <TextInput
                                      value={field.value}
                                      type={row.type}
                                      autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === row.name}
                                      onChange={(e) => {
                                        field.onChange(e.target.value);
                                        setFocusIndex({ index: editNewAssetDetails.key, type: row.name });
                                      }}
                                      onBlur={(e) => {
                                        setFocusIndex({ index: -1 });
                                        field.onBlur(e);
                                      }}
                                      onClick={() => {
                                        fetchCurrentLocation(row.name);
                                      }}
                                    />

                                  )}
                                />
                              </div>
                            </LabelFieldPair>
                            
                          </div>
                        ) :
                        (
                          <div key={index}>
                            <LabelFieldPair>
                              <CardLabel className="card-label-smaller">{t(row.code)}</CardLabel>
                              <div className="field">
                                <Controller
                                  control={control}
                                  name={row.name}
                                  defaultValue={comingDataFromAPI?.additionalDetails[row.name]}
                                  rules={{
                                    required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                                    validDate: (val) => (/^\d{4}-\d{2}-\d{2}$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")),
                                  }}
                                  render={({field}) => (
                                    <TextInput
                                      value={field.value}
                                      type={row.type}
                                      autoFocus={focusIndex.index === editNewAssetDetails?.key && focusIndex.type === row.name}
                                      onChange={(e) => {
                                        field.onChange(e.target.value);
                                        setFocusIndex({ index: editNewAssetDetails.key, type: row.name });
                                      }}
                                      onBlur={(e) => {
                                        setFocusIndex({ index: -1 });
                                        field.onBlur(e);
                                      }}
                                    />
                                  )}
                                />
                              </div>
                            </LabelFieldPair>
                            
                          </div>
                        )
                  }
                </div>
              ))}

           
          </React.Fragment>

        </div>
      </div>
      {showToast?.label && <Toast label={showToast?.label} onClose={w => {
      setShowToast(x => null);
    }} />}
    </React.Fragment>;
};
export default EditAssetDetails;
