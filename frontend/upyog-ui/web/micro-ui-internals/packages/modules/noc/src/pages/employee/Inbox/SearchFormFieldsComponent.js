/**
 * Renders search input fields for employee NOC Inbox.
 * Captures NOC Application Number and Source Reference (BPA) ID for inbox filtering.
 */
import React, {Fragment} from "react"
import { CardLabelError, SearchField, TextInput } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

const SearchFormFieldsComponents = ({registerRef, searchFormState, searchFieldComponents}) => {
    const { t } = useTranslation()
    const isMobile = window.Digit.Utils.browser.isMobile();

    if (!isMobile) {
        return <React.Fragment>
            <div className="search-container noc-search-form-fields-component-spacing">
                <div className="search-complaint-container">
                    <div className="complaint-input-container noc-search-form-fields-component-wrapper">
                        <SearchField>
                            <label>{t("NOC_APPLICATION_NUMBER_LABEL")}</label>
                            <TextInput name="applicationNo" inputRef={registerRef("applicationNo").ref} {...registerRef("applicationNo")} />
                        </SearchField>
                        <SearchField>
                            <label>{t("NOC_BPA_APPLICATION_NUMBER_LABEL")}</label>
                            <TextInput name="sourceRefId" inputRef={registerRef("sourceRefId").ref} {...registerRef("sourceRefId")} />
                        </SearchField>
                        <div className="search-action-wrapper noc-search-fullwidth">
                            {searchFieldComponents}
                        </div>
                    </div>
                </div>
            </div>
        </React.Fragment>
    }

    return <>
        <SearchField>
            <label>{t("NOC_APPLICATION_NUMBER_LABEL")}</label>
            <TextInput name="applicationNo" inputRef={registerRef("applicationNo").ref} {...registerRef("applicationNo")} />
        </SearchField>
        <SearchField>
            <label>{t("NOC_BPA_APPLICATION_NUMBER_LABEL")}</label>
            <TextInput name="sourceRefId" inputRef={registerRef("sourceRefId").ref} {...registerRef("sourceRefId")} />
        </SearchField>
    </>
}

export default SearchFormFieldsComponents