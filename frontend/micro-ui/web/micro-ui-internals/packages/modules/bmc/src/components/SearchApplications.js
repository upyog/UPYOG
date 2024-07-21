import { CardLabel, Dropdown, LabelFieldPair } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from 'react';
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

const SearchApplications = ({onUpdate}) => {
    const { t } = useTranslation();
    const { control, trigger,clearErrors, formState: { errors, isValid }, } = useForm({
        defaultValues: {
            schemeHead: "",
            scheme: "",
            details: ""
        }
    }
    );
    const [schemeHeads, setSchemeHeads] = useState([]);
    const [schemes, setSchemes] = useState([]);
    const [details, setDetails] = useState([]);
    const [selectedSchemeHead, setSelectedSchemeHead] = useState("");
    const [selectedScheme, setSelectedScheme] = useState("");
    const [selectedDetail, setSelectedDetail] = useState("");
    const getSchemes = { SchemeSearchCriteria: { Status: 1, "sla": 30 } };
    const { data: SCHEME_DATA } = Digit.Hooks.bmc.useSchemesGet(getSchemes);
    //console.log(SCHEME_DATA1.SchemeDetails);

    useEffect(() => {
        if (SCHEME_DATA && SCHEME_DATA.SchemeDetails) {
            const groupedSchemeHeads = [];
            const schemeHeadMap = new Map();

            SCHEME_DATA.SchemeDetails.forEach(event => {
                event.schemeshead.forEach(schemeHead => {
                    if (!schemeHeadMap.has(schemeHead.schemeHead)) {
                        schemeHeadMap.set(schemeHead.schemeHead, {
                            schemeHead: schemeHead.schemeHead,
                            schemeheadDesc: schemeHead.schemeheadDesc,
                            schemeDetails: [...schemeHead.schemeDetails]
                        });
                    } else {
                        const existingSchemeHead = schemeHeadMap.get(schemeHead.schemeHead);
                        existingSchemeHead.schemeDetails.push(...schemeHead.schemeDetails);
                    }
                });
            });

            schemeHeadMap.forEach(value => groupedSchemeHeads.push(value));
            setSchemeHeads(groupedSchemeHeads);
        }
    }, [SCHEME_DATA]);

    const handleSchemeHeadChange = (selected) => {
        setSelectedSchemeHead(selected);
        setSelectedScheme("");
        setSelectedDetail("");
        setDetails([]);
        clearErrors("schemeHead");
        const selectedSchemeDetails = schemeHeads.find(head => head.schemeHead === selected.value)?.schemeDetails || [];
        setSchemes(selectedSchemeDetails);
    };

    const handleSchemeChange = (selected) => {
        setSelectedScheme(selected.value);
        setSelectedDetail("");

        const selectedScheme = schemes.find(scheme => scheme.schemeID === selected.value);
        const details = [...(selectedScheme.courses || []), ...(selectedScheme.machines || [])];
        setDetails(details);
        clearErrors("scheme");
    };

    const handleDetailChange = (selected) => {
        setSelectedDetail(selected.value);
    };

    const schemeHeadOptions = schemeHeads.map(head => ({ value: head.schemeHead, label: head.schemeHead }));
    const schemeOptions = schemes.map(scheme => ({ value: scheme.schemeID, label: scheme.schemeName }));
    const detailOptions = details.map(detail => ({ value: detail.machID || detail.courseID, label: detail.machName || detail.courseName }));

    const handleSearch = () => {
        let searchCriteria = {};

        if (selectedSchemeHead) {
            searchCriteria.schemeHead = selectedSchemeHead;
        }
        if (selectedScheme) {
            searchCriteria.schemeID = selectedScheme;
        }
        if (selectedDetail) {
            searchCriteria.detailID = selectedDetail;
        }

        console.log("Search Criteria: ", searchCriteria);

        if (onUpdate) {
            onUpdate(searchCriteria);
        }
    };

    useEffect(() => {
        trigger(); // Validate the form on mount to show errors if fields are empty
    }, [trigger]);

    return (
        <div>
            <LabelFieldPair>
                <CardLabel className="bmc-label">{t("Scheme Heads")}</CardLabel>
                <Controller
                    control={control}
                    name="schemeHead"
                    rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                    render={({ value, onChange, onBlur }) => (
                        <div>
                            <Dropdown
                                placeholder={t("Select Scheme Head")}
                                selected={value}
                                defaultValue={""}
                                select={(value) => {
                                    onChange(value);
                                    handleSchemeHeadChange(value);
                                }}
                                onBlur={onBlur}
                                option={schemeHeadOptions}
                                optionKey="value"
                                t={t}
                            />
                            {errors.schemeHead && <span style={{ color: "red" }}>{errors.schemeHead.message}</span>}
                        </div>
                    )}
                />
            </LabelFieldPair>

            <LabelFieldPair>
                <CardLabel className="bmc-label">{t("Schemes")}</CardLabel>
                <Controller
                    control={control}
                    name="scheme"
                    rules={{ required: t("CORE_COMMON_REQUIRED_ERRMSG") }}
                    render={({ value, onChange, onBlur }) => (
                        <div>
                            <Dropdown
                                placeholder={t("Select Scheme")}
                                selected={schemeOptions.find(option => option.value === value)}
                                select={(option) => {
                                    onChange(option.value);
                                    handleSchemeChange(option);
                                }}
                                onBlur={onBlur}
                                option={schemeOptions}
                                optionKey="label"
                                t={t}
                                disabled={!selectedSchemeHead}
                            />
                            {errors.scheme && <span style={{ color: "red" }}>{errors.scheme.message}</span>}
                        </div>
                    )}
                />
            </LabelFieldPair>

            <LabelFieldPair>
                <CardLabel className="bmc-label">{t("Details")}</CardLabel>
                <Controller
                    control={control}
                    name="details"
                    render={({ value, onChange, onBlur }) => (
                        <div>
                            <Dropdown
                                placeholder={t("Select Details")}
                                selected={detailOptions.find(option => option.value === value)}
                                select={(option) => {
                                    onChange(option.value);
                                    handleDetailChange(option);
                                }}
                                onBlur={onBlur}
                                option={detailOptions}
                                optionKey="label"
                                t={t}
                                disabled={!selectedScheme}
                            />
                        </div>
                    )}
                />
            </LabelFieldPair>
            <div className="bmc-card-row" style={{ textAlign: "end" }}>
                <button className="bmc-card-button" onClick={handleSearch} style={{ borderBottom: "3px solid black", marginRight: "1rem" }}>
                    {t("Search")}
                </button>
            </div>
        </div>
    );
};

export default SearchApplications;
