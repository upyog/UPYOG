import {
    CardLabel,
    CardLabelError,
    LabelFieldPair,
    TextInput,
    Toast,
    Row,
    StatusTable,
    Dropdown
    } from "@nudmcdgnpm/digit-ui-react-components";
    import _ from "lodash";
    import React, { useEffect, useState } from "react";
    import { Controller, useForm } from "react-hook-form";
    import { useTranslation } from "react-i18next";
    import { useParams } from "react-router-dom";
    import { checkForNA } from "../utils";


    const createAssetcommonforAll = () => ({

        department: "",
        assignedUser: "",
        designation: "",
        employeeCode : "",
        transferDate: "",
        returnDate: "",
        allocatedDepartment:"",
        key: Date.now(),
    });

    const AssetAssign = ({ config, onSelect, formData, setError, formState, clearErrors }) => {
    const { t } = useTranslation();


    const [assigndetails, setassigndetails] = useState(formData?.assigndetails || [createAssetcommonforAll()]);

    const [focusIndex, setFocusIndex] = useState({ index: -1, type: "" });
    useEffect(() => {
    onSelect(config?.key, assigndetails);

    }, [assigndetails]);



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



    const commonProps = {
        focusIndex,
        allAssets: assigndetails,
        setFocusIndex,
        formData,
        formState,
        setassigndetails,
        t,
        setError,
        clearErrors,
        config,
        departNamefromMDMS
    };

    return (
        <React.Fragment>
        {assigndetails.map((assigndetails, index) => (
            <OwnerForm key={assigndetails.key} index={index} assigndetails={assigndetails} {...commonProps} />
        ))}
        </React.Fragment>
    )
    };
    const OwnerForm = (_props) => {
    const {
        assigndetails,
        focusIndex,
        allAssets,      
        setassigndetails,
        t,
        config,
        setError,
        clearErrors,
        formState,
        setFocusIndex,
        departNamefromMDMS
    } = _props;


    const [showToast, setShowToast] = useState(null);
    const { control, formState: localFormState, watch, setError: setLocalError, clearErrors: clearLocalErrors, setValue, trigger, } = useForm();
    const formValue = watch();
    const { errors } = localFormState;

    const { id: applicationNo } = useParams();
    const tenantId = Digit.ULBService.getCurrentTenantId();
    const { data: applicationDetails } = Digit.Hooks.asset.useAssetApplicationDetail(t, tenantId, applicationNo);
    const [part, setPart] = React.useState({});


    useEffect(() => {
        if (!_.isEqual(part, formValue)) {
        setPart({ ...formValue });
        setassigndetails((prev) => prev.map((o) => (o.key && o.key === assigndetails.key ? { ...o, ...formValue/*, ..._ownerType*/ } : { ...o })));
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

                <StatusTable>
                <Row
                    label={t("ES_ASSET_RESPONSE_CREATE_LABEL")}
                    text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.applicationNo))}`}
                />
                </StatusTable>
                <StatusTable>
                <Row
                    label={t("AST_DEPARTMENT_LABEL")}
                    text={`${t(checkForNA(applicationDetails?.applicationData?.applicationData?.department))}`}
                />
                </StatusTable>
                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_ASSIGNED_USER") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"assignedUser"}
                        defaultValue={assigndetails?.assignedUser}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            autoFocus={focusIndex.index === assigndetails?.key && focusIndex.type === "assignedUser"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assigndetails.key, type: "assignedUser" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.assignedUser ? errors?.assignedUser?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_DESIGNATION") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"designation"}
                        defaultValue={assigndetails?.designation}
                        rules={{
                        required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                        validate: { pattern: (val) => (/^[a-zA-Z\s]*$/.test(val) ? true : t("ERR_DEFAULT_INPUT_FIELD_MSG")) },
                        }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assigndetails?.key && focusIndex.type === "designation"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assigndetails.key, type: "designation" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.designation ? errors?.designation?.message : ""}</CardLabelError>
                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_EMP_CODE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"employeeCode"}
                        defaultValue={assigndetails?.employeeCode}
                        rules={{
                            required: t("CORE_COMMON_REQUIRED_ERRMSG"),
                            validate: {
                                pattern: (val) => 
                                  /^[a-zA-Z0-9\s\-/]+$/.test(val) || t("ERR_DEFAULT_INPUT_FIELD_MSG")
                              },
                          }}
                        render={(props) => (
                        <TextInput
                            value={props.value}
                            // disable={isEditScreen}
                            autoFocus={focusIndex.index === assigndetails?.key && focusIndex.type === "employeeCode"}
                            onChange={(e) => {
                            props.onChange(e.target.value);
                            setFocusIndex({ index: assigndetails.key, type: "employeeCode" });
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
                <CardLabelError style={errorStyle}>{localFormState.touched.employeeCode ? errors?.employeeCode?.message : ""}</CardLabelError>
                <LabelFieldPair>
                <CardLabel className="card-label-smaller">{t("AST_ALLOCATED_DEPARTMENT")}</CardLabel>
                <Controller
                control={control}
                name={"allocatedDepartment"}
                defaultValue={assigndetails?.allocatedDepartment}
              
                render={(props) => (
                    <Dropdown
                    className="form-field"
                    selected={props.value}
                    select={props.onChange}
                    onBlur={props.onBlur}
                    option={departNamefromMDMS}
                    optionKey="i18nKey"
                    t={t}
                    />
                )}
                />
                </LabelFieldPair>
                <CardLabelError style={errorStyle}>{localFormState.touched.allocatedDepartment ? errors?.allocatedDepartment?.message : ""}</CardLabelError>

                <LabelFieldPair>
                    <CardLabel className="card-label-smaller">{t("AST_TRANSFER_DATE") }</CardLabel>
                    <div className="field">
                    <Controller
                        control={control}
                        name={"transferDate"}
                        defaultValue={assigndetails?.transferDate}
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
                <CardLabelError style={errorStyle}>{localFormState.touched.employeeCode ? errors?.employeeCode?.message : ""}</CardLabelError>
                

               

                



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

    export default AssetAssign;