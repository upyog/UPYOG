import React, { Fragment } from "react";
import { Controller, useWatch } from "react-hook-form";
import {
    TextInput,
    SubmitBar,
    LinkLabel,
    ActionBar,
    CloseSvg,
    DatePicker,
    CardLabelError,
    SearchForm,
    SearchField,
    Dropdown,
    Table,
    Card,
    MobileNumber,
    Loader,
    CardText,
    Header,
} from "@nudmcdgnpm/digit-ui-react-components";
const SearchFields = ({ register, control, reset, tenantId, t, previousPage, formState, isLoading, onClear }) => {
    const isMobile = window.Digit.Utils.browser.isMobile();

    const handleClear = () => {
        if (typeof onClear === "function") {
            onClear();
        } else if (typeof reset === "function") {
            reset({
                offset: 0,
                limit: 10,
                sortOrder: "ASC",
                fromDate: "",
                toDate: "",
            });
        }
    };

    return (
        <>
            <SearchField className="pt-form-field citizen-audit-field">
                <label className="citizen-audit-field-label">{t("AUDIT_FROM_DATE_LABEL")}</label>
                <Controller
                    render={(props) => <DatePicker date={props.value} onChange={props.onChange} />}
                    name="fromDate"
                    control={control}
                />
            </SearchField>
            <SearchField className="pt-form-field citizen-audit-field">
                <label className="citizen-audit-field-label">{t("AUDIT_TO_DATE_LABEL")}</label>
                <Controller
                    render={(props) => <DatePicker date={props.value} onChange={props.onChange} />}
                    name="toDate"
                    control={control}
                />
            </SearchField>
            <SearchField className="pt-search-action-submit citizen-audit-actions-field">
                <div className="citizen-audit-action-group">
                    <button type="button" className="citizen-audit-clear-btn" onClick={handleClear}>
                        {t("ES_COMMON_CLEAR_ALL")}
                    </button>
                    <SubmitBar className={`search-fields-submit-bar ${isMobile ? "mobile-override" : ""}`} label={t("ES_COMMON_APPLY")} submit />
                </div>
            </SearchField>
        </>
    );
};
export default SearchFields;
