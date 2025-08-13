import React, { useEffect, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import {
  TextInput,
  Label,
  SubmitBar,
  LinkLabel,
  ActionBar,
  CloseSvg,
  DatePicker,
  MobileNumber
} from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";

/**
 * Mapping of field types to their corresponding components
 * @type {Object.<string, React.Component>}
 */
const fieldComponents = {
  date: DatePicker,
  mobileNumber: MobileNumber,
};

/**
 * SearchApplication component provides search functionality for E-Waste applications
 * with support for both mobile and desktop views.
 *
 * @param {Object} props - Component props
 * @param {Function} props.onSearch - Handler for search submission
 * @param {string} props.type - View type ('mobile' or 'desktop')
 * @param {Function} props.onClose - Handler for closing the search form
 * @param {Array} props.searchFields - Configuration for search form fields
 * @param {Object} props.searchParams - Current search parameters
 * @param {boolean} props.isInboxPage - Flag indicating if component is used in inbox
 * @param {Object} props.defaultSearchParams - Default search parameters
 * @param {Function} props.clearSearch - Handler for clearing search
 * @returns {JSX.Element} Search form component
 */
const SearchApplication = ({
  onSearch,
  type,
  onClose,
  searchFields,
  searchParams,
  isInboxPage,
  defaultSearchParams,
  clearSearch: _clearSearch,
}) => {
  const { t } = useTranslation();
  const { handleSubmit, reset, watch, control, setError, clearErrors, formState, setValue } = useForm({
    defaultValues: isInboxPage ? searchParams : { locality: null, city: null, ...searchParams },
  });

  const form = watch();

  /**
   * Checks if the form has any non-empty values
   * @returns {boolean} True if form is empty, false otherwise
   */
  const formValueEmpty = () => {
    let isEmpty = true;
    Object.keys(form).forEach((key) => {
      if (!["locality", "city"].includes(key) && form[key]) isEmpty = false;
    });
    if (searchFields?.find((e) => e.name === "locality") && !form?.locality?.code) isEmpty = true;
    return isEmpty;
  };

  /**
   * Handles form submission with data processing
   * @param {Object} data - Form data to be submitted
   */
  const onSubmitInput = (data) => {
    if (!data.mobileNumber) delete data.mobileNumber;
    data.delete = [];
    searchFields.forEach((field) => {
      if (!data[field.name]) data.delete.push(field.name);
    });
    onSearch(data);
    if (type === "mobile") onClose();
  };

  /**
   * Clears the search form and resets to default state
   */
  function clearSearch() {
    const resetValues = searchFields.reduce((acc, field) => ({ ...acc, [field?.name]: "" }), {});
    reset(resetValues);
    if (isInboxPage) {
      const _newParams = { ...searchParams };
      _newParams.delete = [];
      searchFields.forEach((e) => {
        _newParams.delete.push(e?.name);
      });
      onSearch({ ..._newParams });
    } else {
      _clearSearch();
    }
  }

  /**
   * Renders the clear all link with appropriate styling
   * @param {boolean} mobileView - Flag indicating if rendering for mobile view
   * @returns {JSX.Element} Clear all link component
   */
   const mobileView = innerWidth<=640;
  const clearAll = (mobileView) => {
    const mobileViewStyles = mobileView ? { margin: 0 } : {};
    return (
      <LinkLabel style={{ display: "inline", margin: "10px", ...mobileViewStyles }} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}> 
      <React.Fragment>
        <div className="search-container" style={{ width: "auto", marginLeft: isInboxPage ? "24px" : "revert" }}>
          <div className="search-complaint-container">
            {/* Render the header for mobile view */}
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2> {/* Translated text for "Search By" */}
                <span onClick={onClose}>
                  <CloseSvg /> {/* Close button */}
                </span>
              </div>
            )}
            <div className={"complaint-input-container for-pt " + (!isInboxPage ? "for-search" : "")} style={{ width: "100%", display: "grid" }}>
              {/* Render the search fields */}
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    <span className={"mobile-input"}>
                      <Label>{t(input.label) + ` ${input.isMendatory ? "*" : ""}`}</Label> {/* Render the field label */}
                      {!input.type ? (
                        <Controller
                          render={(props) => {
                            return <TextInput onChange={props.onChange} value={props.value} />; // Render a text input
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                        />
                      ) : (
                        <Controller
                          render={(props) => {
                            const Comp = fieldComponents?.[input.type]; // Get the component for the field type
                            return <Comp formValue={form} setValue={setValue} onChange={props.onChange} value={props.value} />; // Render the component
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                        />
                      )}
                    </span>
                    {/* Render validation errors */}
                    {formState?.dirtyFields?.[input.name] ? (
                      <span
                        style={{ fontWeight: "700", color: "rgba(212, 53, 28)", paddingLeft: "8px", marginTop: "-20px", fontSize: "12px" }}
                        className="inbox-search-form-error"
                      >
                        {formState?.errors?.[input.name]?.message}
                      </span>
                    ) : null}
                  </div>
                ))}

              {/* Render the "Clear All" link for inbox pages */}
              {isInboxPage && (
                <div style={{ gridColumn: "2/3", textAlign: "right", paddingTop: "10px" }} className="input-fields">
                  <div>{clearAll()}</div>
                </div>
              )}

              {/* Render the submit button for desktop view */}
              {type === "desktop" && !mobileView && (
                <div style={{ maxWidth: "unset", marginLeft: "unset" }} className="search-submit-wrapper">
                  <SubmitBar
                    className="submit-bar-search"
                    label={t("ES_COMMON_SEARCH")} // Translated text for "Search"
                    disabled={!!Object.keys(formState.errors).length || formValueEmpty()} // Disable if there are errors or the form is empty
                    submit
                  />
                  {!isInboxPage && <div>{clearAll()}</div>}
                </div>
              )}
            </div>
          </div>
        </div>
        {/* Render the action bar for mobile view */}
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search" style={{ flex: 1 }}>
              {clearAll(mobileView)}
            </button>
            <SubmitBar disabled={!!Object.keys(formState.errors).length} label={t("ES_COMMON_SEARCH")} style={{ flex: 1 }} submit={true} />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication; // Exporting the component for use in other parts of the application