import React, { useState, useEffect } from "react";
import {
    FormStep,
    CardLabel,
    TextInput,
    Dropdown,
    CheckBox,
    CloseSvg,
} from "@nudmcdgnpm/digit-ui-react-components";
import i18next from "i18next";


const GCSpecifications = ({ t, config, onSelect, formData, renewApplication }) => {

    const convertToObject = (params) => ({ i18nKey: params, code: params, value: params });

    const user = Digit.UserService.getUser().info;
    const inputStyles = {
        width: user?.type === "EMPLOYEE" ? "50%" : "100%",
    };
    const stateId = Digit.ULBService.getStateId();

    // --- MDMS fetches: master names now match your MDMS files exactly ---
    const { data: collectionTypesData } = Digit.Hooks.useCustomMDMS(stateId, "Garbage", [{ name: "CollectionType" }], {
        select: (data) => data?.["Garbage"]?.["CollectionType"] || [],
    });

    const { data: ownerTypesData } = Digit.Hooks.useCustomMDMS(stateId, "Garbage", [{ name: "OwnerType" }], {
        select: (data) => data?.["Garbage"]?.["OwnerType"] || [],
    });

    const { data: genderData } = Digit.Hooks.useCustomMDMS(stateId, "Garbage", [{ name: "Gender" }], {
        select: (data) => data?.["Garbage"]?.["Gender"] || [],
    });

    const { data: categoriesData } = Digit.Hooks.useCustomMDMS(stateId, "Garbage", [{ name: "Categories" }], {
        select: (data) => data?.["Garbage"]?.["Categories"] || [],
    });

    // SubCategories is its own master, separate from Categories, and carries the nested subcategorytype array
    const { data: subCategoriesData } = Digit.Hooks.useCustomMDMS(stateId, "Garbage", [{ name: "SubCategories" }], {
        select: (data) => data?.["Garbage"]?.["SubCategories"] || [],
    });

    // --- Build dropdown option lists straight from the MDMS shape ---
    const CollectionTypes = collectionTypesData?.map((item) => ({
        i18nKey: item.code,
        code: item.code,
        name: item.name,
    })) || [];

    const OwnerTypes = ownerTypesData?.map((item) => ({
        i18nKey: item.code,
        code: item.code,
        name: item.name,
    })) || [];

    const Genders = genderData?.map((item) => ({
        i18nKey: item.code,
        code: item.code,
        name: item.name,
    })) || [];

    const UniqueCategories = categoriesData?.map((item) => ({
        i18nKey: item.code,
        code: item.code,
        name: item.name,
    })) || [];

    const specsData = formData?.[config?.key] || formData?.gcspecifications || formData?.GCSpecifications || {};

    const [oldGarbageId, setOldGarbageId] = useState(specsData.oldGarbageId || renewApplication?.grbgOldDetails?.oldGarbageId || "");
    const [typeOfCollection, setTypeOfCollection] = useState(specsData.typeOfCollection || convertToObject(renewApplication?.grbgCollectionUnits?.[0]?.unitType) || "");
    const [propertyOwnerType, setPropertyOwnerType] = useState(specsData.propertyOwnerType || convertToObject(renewApplication?.grbgCollectionUnits?.[0]?.ownerType) || "");
    const [name, setName] = useState(specsData.name || renewApplication?.name || "");
    const [phoneNumber, setPhoneNumber] = useState(specsData.phoneNumber || renewApplication?.mobileNumber || "");
    const [gender, setGender] = useState(specsData.gender || convertToObject(renewApplication?.gender) || "");
    const [email, setEmail] = useState(specsData.email || renewApplication?.emailId || "");
    const [category, setCategory] = useState(specsData.category || convertToObject(renewApplication?.grbgCollectionUnits?.[0]?.category) || "");
    const [subCategory, setSubCategory] = useState(specsData.subCategory || convertToObject(renewApplication?.grbgCollectionUnits?.[0]?.subCategory) || "");
    const [subCategoryType, setSubCategoryType] = useState(specsData.subCategoryType ||convertToObject(renewApplication?.grbgCollectionUnits?.[0]?.subCategoryType) || "");
    const [isVariableCalculation, setIsvariablecalculation] = useState(specsData.isVariableCalculation || renewApplication?.isVariableCalculation || false);
    const [isbulkgeneration, setIsbulkgeneration] = useState(specsData.isbulkgeneration || renewApplication?.isbulkgeneration || false);
    const [no_of_units, setNoOfUnits] = useState(specsData.no_of_units || renewApplication?.no_of_units || "");
    const [isAdditional, setIsAdditional] = useState(specsData.isAdditional || renewApplication?.isAdditional || false);
    const [isInheritance, setIsInheritance] = useState(specsData.isInheritance || renewApplication?.grbgCollectionUnits?.[0]?.isInheritance || false);

    // --- Subcategory list: filter SubCategories master by the selected category's code ---
    const UniqueSubCategories = subCategoriesData
        ?.filter((item) => item.category === category?.code)
        ?.map((item) => ({
            i18nKey: item.subcategory,
            code: item.subcategory,
            name: item.name,
        })) || [];

    // --- Subcategory-type list: pulled straight from the matched subcategory's nested array ---
    const matchedSubCategory = subCategoriesData?.find(
        (item) => item.category === category?.code && item.subcategory === subCategory?.code
    );

    const UniqueSubCategoryTypes = matchedSubCategory?.subcategorytype
        ?.filter((typeItem) => typeItem.active !== false)
        ?.map((typeItem) => ({
            i18nKey: typeItem.code,
            code: typeItem.code,
            name: typeItem.name,
            isAdditional: typeItem.isAdditonalCostAdded === true,
        })) || [];

    const handleCategoryChange = (val) => {
        setCategory(val);
        setSubCategory("");
        setSubCategoryType("");
    };

    const handleSubCategoryChange = (val) => {
        setSubCategory(val);
        setSubCategoryType("");
    };

    const handleSubCategoryTypeChange = (val) => {
        const selectedType = UniqueSubCategoryTypes.find((item) => item.code === val?.code);

        setSubCategoryType(val);
        setIsAdditional(selectedType?.isAdditional || false);

        setIsvariablecalculation(false);
        setIsbulkgeneration(false);
        setNoOfUnits("");
    };

    const goNext = () => {
        const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

        if (email && !emailRegex.test(email)) {
            alert("Please enter a valid email address");
            return;
        }

        const data = {
            oldGarbageId,
            typeOfCollection,
            propertyOwnerType,
            name,
            phoneNumber,
            gender,
            email,
            category,
            subCategory,
            subCategoryType,
            isVariableCalculation,
            isbulkgeneration,
            no_of_units,
            isAdditional,
            isInheritance,
        };

        onSelect(config.key, data, false);
    };

    return (
        <React.Fragment>
            <FormStep
                config={config}
                onSelect={goNext}
                t={t}
                isDisabled={
                    !typeOfCollection ||
                    !propertyOwnerType ||
                    !name ||
                    !phoneNumber ||
                    !gender ||
                    !category ||
                    !subCategory ||
                    !subCategoryType
                }
            >
                <div>
                    <CardLabel>{t("GC_OLD_GARBAGE_ID")}</CardLabel>
                    <TextInput
                        value={oldGarbageId}
                        style={inputStyles}
                        onChange={(e) => {
                            const value = e.target.value.replace(/\D/g, "");
                            setOldGarbageId(value);
                            if (!value) setIsInheritance(false);
                        }}
                    />

                    <CardLabel>
                        {t("GC_TYPE_OF_COLLECTION")} <span className="astericColor">*</span>
                    </CardLabel>
                    <Dropdown
                        option={CollectionTypes}
                        optionKey="i18nKey"
                        selected={typeOfCollection}
                        select={setTypeOfCollection}
                        placeholder={t("GC_SELECT_TYPE")}
                        style={inputStyles}
                        t={t}
                    />

                    <CardLabel>
                        {t("GC_OWNER_OR_TENANT")} <span className="astericColor">*</span>
                    </CardLabel>
                    <Dropdown
                        option={OwnerTypes}
                        optionKey="i18nKey"
                        selected={propertyOwnerType}
                        select={setPropertyOwnerType}
                        style={inputStyles}
                        t={t}
                    />

                    <CardLabel>
                        {t("GC_NAME")} <span className="astericColor">*</span>
                    </CardLabel>
                    <TextInput
                        value={name}
                        style={inputStyles}
                        onChange={(e) => setName(e.target.value.replace(/[^A-Za-z ]/g, ""))}
                    />

                    <CardLabel>
                        {t("GC_PHONE_NUMBER")} <span className="astericColor">*</span>
                    </CardLabel>
                    <TextInput
                        type="text"
                        inputMode="numeric"
                        maxLength={10}
                        value={phoneNumber}
                        style={inputStyles}
                        onChange={(e) => {
                            let value = e.target.value.replace(/\D/g, "").slice(0, 10);
                            if (value.length > 0 && !/^[5-9]/.test(value)) value = "";
                            setPhoneNumber(value);
                        }}
                    />

                    <CardLabel>
                        {t("GC_GENDER")} <span className="astericColor">*</span>
                    </CardLabel>
                    <Dropdown
                        option={Genders}
                        optionKey="i18nKey"
                        selected={gender}
                        select={setGender}
                        style={inputStyles}
                        t={t}
                    />

                    <CardLabel>{t("GC_EMAIL")}</CardLabel>
                    <TextInput value={email} style={inputStyles} onChange={(e) => setEmail(e.target.value.trim())} />

                    <CardLabel>
                        {t("GC_CATEGORY")} <span className="astericColor">*</span>
                    </CardLabel>
                    <Dropdown
                        option={UniqueCategories}
                        optionKey="i18nKey"
                        selected={category}
                        select={handleCategoryChange}
                        style={inputStyles}
                        t={t}
                    />

                    <CardLabel>
                        {t("GC_SUB_CATEGORY")} <span className="astericColor">*</span>
                    </CardLabel>
                    <Dropdown
                        option={UniqueSubCategories}
                        optionKey="i18nKey"
                        selected={subCategory}
                        select={handleSubCategoryChange}
                        style={inputStyles}
                        t={t}
                    />

                    <CardLabel>
                        {t("GC_SUB_CATEGORY_TYPE")} <span className="astericColor">*</span>
                    </CardLabel>
                    <Dropdown
                        option={UniqueSubCategoryTypes}
                        optionKey="i18nKey"
                        selected={subCategoryType}
                        select={handleSubCategoryTypeChange}
                        style={inputStyles}
                        t={t}
                    />

                    {oldGarbageId && oldGarbageId.trim().length > 0 && (
                        <CheckBox
                            label={t("GC_IS_INHERITANCE")}
                            checked={isInheritance}
                            onChange={(e) => setIsInheritance(e.target.checked)}
                        />
                    )}

                    {isAdditional && (
                        <>
                            <CardLabel>{t("GC_CALCULATION_TYPE")}</CardLabel>
                            <div style={{ display: "flex", gap: "16px", marginBottom: "16px" }}>
                                <CheckBox
                                    label={t("GC_FIXED")}
                                    checked={isbulkgeneration}
                                    onChange={(e) => {
                                        setIsbulkgeneration(e.target.checked);
                                        if (e.target.checked) setIsvariablecalculation(false);
                                    }}
                                />
                                <CheckBox
                                    label={t("GC_VARIABLE")}
                                    checked={isVariableCalculation}
                                    onChange={(e) => {
                                        setIsvariablecalculation(e.target.checked);
                                        if (e.target.checked) setIsbulkgeneration(false);
                                    }}
                                />
                            </div>
                        </>
                    )}

                    {isAdditional && isVariableCalculation && (
                        <>
                            <CardLabel>{t("GC_NO_OF_UNITS")}</CardLabel>
                            <TextInput
                                value={no_of_units}
                                style={inputStyles}
                                onChange={(e) => setNoOfUnits(e.target.value.replace(/\D/g, ""))}
                            />
                        </>
                    )}
                </div>
            </FormStep>
        </React.Fragment>
    );
};

export default GCSpecifications;