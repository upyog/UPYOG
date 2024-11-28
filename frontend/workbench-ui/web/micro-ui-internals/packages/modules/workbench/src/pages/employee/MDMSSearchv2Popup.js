import { AddFilled, Button, Header, InboxSearchComposer, Loader, Dropdown, SubmitBar, ActionBar, Close } from "@egovernments/digit-ui-react-components";
import React, { useState, useEffect, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useHistory, useParams } from "react-router-dom";
import { Config as Configg } from "../../configs/searchMDMSConfigPopup";
import _, { drop } from "lodash";

const toDropdownObj = (master = "", mod = "") => {
    return {
        name: mod || master,
        code: Digit.Utils.locale.getTransformedLocale(mod ? `WBH_MDMS_${master}_${mod}` : `WBH_MDMS_MASTER_${master}`),
    };
};


const MDMSSearchv2Popup = ({ masterNameInherited, moduleNameInherited, onClickSelect }) => {
    let Config = _.clone(Configg)
    const { t } = useTranslation();
    const history = useHistory();

    let { masterName: modulee, moduleName: master, tenantId } = Digit.Hooks.useQueryParams()
    master = masterNameInherited;
    modulee = moduleNameInherited;

    const [availableSchemas, setAvailableSchemas] = useState([]);
    const [currentSchema, setCurrentSchema] = useState(null);
    const [masterName, setMasterName] = useState(null); //for dropdown
    const [moduleName, setModuleName] = useState(null); //for dropdown
    const [masterOptions, setMasterOptions] = useState([])
    const [moduleOptions, setModuleOptions] = useState([])
    const [updatedConfig, setUpdatedConfig] = useState(null)
    const [showDetails, setShowDetails] = useState(false);
    const [selectedRowData, setSelectedRowData] = useState(null);
    tenantId = tenantId || Digit.ULBService.getCurrentTenantId();
    const SchemaDefCriteria = {
        tenantId: tenantId,
        limit: 50
    }
    const handleRowClick = (selectedValue) => {
        setShowDetails(true);
        setSelectedRowData(selectedValue);
    };

    const handleCloseDetails = () => {
        setShowDetails(false);
        setSelectedRowData(null);
    };

    const handleSelect = () => {
        onClickSelect(selectedRowData.original);
        handleCloseDetails();
    }
    const handleSelectForRow = (rowValue) => {
        onClickSelect(rowValue);
        handleCloseDetails();
    }

    if (master && modulee) {
        SchemaDefCriteria.codes = [`${modulee}.${master}`]
    }
    const { isLoading, data: dropdownData } = Digit.Hooks.useCustomAPIHook({
        url: `/${Digit.Hooks.workbench.getMDMSContextPath()}/schema/v1/_search`,
        params: {

        },
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
                setAvailableSchemas(schemas);
                if (schemas?.length === 1) setCurrentSchema(schemas?.[0])
                //now extract moduleNames and master names from this schema
                const obj = {
                    mastersAvailable: [],
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
        setModuleOptions(dropdownData?.[masterName?.name])
        setMasterOptions(dropdownData?.mastersAvailable)
        if (masterName?.name && moduleName?.name) {
            setCurrentSchema(availableSchemas.filter(schema => schema.code === `${masterName?.name}.${moduleName?.name}`)?.[0])
        }
    }, [masterName, dropdownData, moduleName])

    useEffect(() => {
        if (currentSchema) {
            const dropDownOptions = [];
            const {
                definition: { properties },
            } = currentSchema;

            Object.keys(properties)?.forEach((key) => {
                if (properties[key].type === "string" && !properties[key].format) {
                    dropDownOptions.push({
                        // name: key,
                        name: key,
                        code: key,
                        i18nKey: Digit.Utils.locale.getTransformedLocale(`${currentSchema.code}_${key}`)
                    });
                }
            });
            Config.sections.search.uiConfig.fields[0].populators.options = dropDownOptions;
            Config.actionLink = Config.actionLink + `?moduleName=${masterName?.name}&masterName=${moduleName?.name}`;
            // Config.apiDetails.serviceName = `/mdms-v2/v2/_search/${currentSchema.code}`


            Config.additionalDetails = {
                currentSchemaCode: currentSchema.code
            }

            Config.sections.searchResult.uiConfig.columns = [...dropDownOptions.map(option => {
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
                // dontShowNA:true
            }, {
                label: " ",
                i18nKey: " ",
                jsonPath: '',
                additionalCustomization: true,
                dontShowNA: true,
                onClick: handleSelectForRow
            }]
            Config.apiDetails.serviceName = `/${Digit.Hooks.workbench.getMDMSContextPath()}/v2/_search`;
            setUpdatedConfig(Config)
        }
    }, [currentSchema]);

    const handleAddNewClick = () => {
        const isConfirmed = window.confirm(t("WORKBENCH_MDMS_SEARCH_REDIRECTION"));

        if (isConfirmed) {
            history.push(
                `/${window?.contextPath}/employee/workbench/${"mdms-add-v2"}?moduleName=${modulee}&masterName=${master}`
            );
        }
    };

    if (isLoading) return <Loader />;
    return (
        <React.Fragment>
            {updatedConfig && (
                <div className="inbox-search-wrapper">
                    {/* <div className="add-new-container">
                        <span onClick={handleAddNewClick} className="add-new">
                            + Add New
                        </span>
                    </div> */}
                    <InboxSearchComposer
                        configs={updatedConfig}
                        additionalConfig={{
                            resultsTable: {
                                onClickRow: handleRowClick,
                            },
                        }}
                    />
                </div>
            )}

            {showDetails && (
                <div className="modal-wrapper">
                    <div className="option-details">
                        <div className="close-button" onClick={handleCloseDetails}>
                            <Close />
                        </div>
                        <div className="detail-container">
                            {Object.keys(selectedRowData?.original?.data).map((key) => {
                                const value = selectedRowData?.original?.data[key]
                                if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') {
                                    return (
                                        <div className="detail-item" key={key}>
                                            <div className="key">{t(Digit.Utils.locale.getTransformedLocale(`${modulee}.${master}_${key}`))}</div>
                                            <div className="value">{String(value)}</div>
                                        </div>
                                    );
                                }
                                return null;
                            })}
                        </div>
                        <div className="select">
                            <Button label={t("WORKBENCH_LABEL_SELECT")} onButtonClick={handleSelect} />
                        </div>
                    </div>
                </div>
            )}
        </React.Fragment>
    );
};

export default MDMSSearchv2Popup;