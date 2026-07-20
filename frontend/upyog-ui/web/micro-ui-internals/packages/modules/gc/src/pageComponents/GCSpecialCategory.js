import React, { useEffect, useState } from "react";
import { RadioButtons, LabelFieldPair, CardLabel, Dropdown, Loader, FormStep } from "@nudmcdgnpm/digit-ui-react-components";
import { useLocation } from "react-router-dom";


const GCSpecialCategory = ({ t, config, onSelect, userType, formData, renewApplication }) => {


    const convertToObject = (params) => {
        if (!params) return "";
        return {
            i18nKey: params,
            code: params,
            value: params,
        };
    };
    const { pathname: url } = useLocation();
    const editScreen = url.includes("/modify-application/");

    const tenantId = Digit.ULBService.getCurrentTenantId();
    const stateId = Digit.ULBService.getStateId();

    const [specialCategory, setSpecialCategory] = useState(
        formData?.[config.key]?.specialCategory ||
        formData?.specialCategory ||
        convertToObject(renewApplication?.grbgCollectionUnits?.[0]?.specialCategory) ||
        ""
    );

    // Master name matches your MDMS file exactly ("SpeacialCategories" — note the typo in the source JSON;
    // consider renaming it to "SpecialCategories" in the MDMS repo, and update this string when you do)
    const { data: getSpecialCategoryData, isLoading } = Digit.Hooks.useCustomMDMS(stateId, "Garbage", [{ name: "SpeacialCategories" }], {
        select: (data) => {
            const options = data?.Garbage?.SpeacialCategories || [];
            return options
                .filter((opt) => opt.active !== false)
                .map((opt) => ({
                    code: opt.code,
                    i18nKey: opt.code,
                    name: opt.name,
                    value: opt.code,
                }))
                .sort((a, b) => a.code?.localeCompare(b.code));
        },
    });

    const onSkip = () => onSelect();

    function setTypeOfCategory(value) {
        setSpecialCategory(value);
    }

    function goNext() {
        onSelect(config.key, { ...formData[config.key], specialCategory });
    }

    useEffect(() => {
        if (userType !== "employee") return;
        if (!specialCategory?.code) return;

        onSelect(config.key, {
            ...formData[config.key],
            specialCategory,
        });
    }, [specialCategory]);

    const inputs = [
        {
            label: "GC_SPECIAL_CATEGORY",
            type: "text",
            name: "specialCategory",
            validation: {},
        },
    ];

    if (isLoading) return <Loader />;

    if (userType === "employee") {
        return inputs?.map((input, index) => {
            return (
                <LabelFieldPair key={index}>
                    <CardLabel className="card-label-smaller" style={editScreen ? { color: "#B1B4B6" } : {}}>
                        {t(input.label)}
                    </CardLabel>
                    <Dropdown
                        className="form-field"
                        selected={getSpecialCategoryData?.length === 1 ? getSpecialCategoryData[0] : specialCategory}
                        disable={getSpecialCategoryData?.length === 1 || editScreen}
                        option={getSpecialCategoryData}
                        select={setTypeOfCategory}
                        optionKey="i18nKey"
                        t={t}
                    />
                </LabelFieldPair>
            );
        });
    }

    return (
        <React.Fragment>
            <FormStep t={t} config={config} onSelect={goNext} onSkip={onSkip} isDisabled={!specialCategory}>
                <div>
                    <RadioButtons
                        t={t}
                        optionsKey="i18nKey"
                        isMandatory={config.isMandatory}
                        options={getSpecialCategoryData || []}
                        selectedOption={specialCategory}
                        onSelect={setTypeOfCategory}
                        labelKey="GC_SPECIAL_CATEGORY"
                        isDependent={true}
                        disabled={editScreen}
                    />
                </div>
            </FormStep>
        </React.Fragment>
    );
};

export default GCSpecialCategory;