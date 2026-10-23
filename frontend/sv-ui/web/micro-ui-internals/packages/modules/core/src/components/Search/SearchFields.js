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
} from "@nudmcdgnpm/upyog-ui-react-components-lts";
const SearchFields = ({ register, control, reset, tenantId, t, previousPage, formState, isLoading }) => {
const isMobile = window.Digit.Utils.browser.isMobile();

    return (
        <Fragment>
            <SearchField className="pt-form-field">
                <label>{t("AUDIT_FROM_DATE_LABEL")}</label>
                <Controller
                    render={({field}) => <DatePicker date={field.value} onChange={field.onChange} />}
                    name="fromDate"
                    control={control}
                />
            </SearchField>
            <SearchField className="pt-form-field">
                <label>{t("AUDIT_TO_DATE_LABEL")}</label>
                <Controller
                    render={({field}) => <DatePicker date={field.value} onChange={field.onChange} />}
                    name="toDate"
                    control={control}
                />
            </SearchField>
            <SearchField className="pt-search-action-submit">
                <SubmitBar style={{marginTop: isMobile? "510px":"25px", marginLeft:isMobile? "0":"-30px"  ,maxWidth : isMobile? "100%":"240px",
}} label={t("ES_COMMON_APPLY")} submit />
            </SearchField>
        </Fragment>
    );
};
export default SearchFields;
