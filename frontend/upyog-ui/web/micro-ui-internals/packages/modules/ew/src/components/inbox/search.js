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
} from "@nudmcdgnpm/digit-ui-react-components";
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
  const mobileView = innerWidth <= 640;
  const clearAll = (mobileView) => {
    const mobileViewStyles = mobileView ? { margin: 0 } : {};
    return (
      <LinkLabel className="display-inline margin-10" style={mobileViewStyles} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className={`search-container ${isInboxPage ? "search-container-auto-margin" : "search-container-auto"}`}>
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
            <div className={`${"complaint-input-container for-pt " + (!isInboxPage ? "for-search" : "")} ew-search-fullwidth`}>
              {/* Render the search fields */}
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    <span className={"mobile-input"}>
                      <Label>{t(input.label) + ` ${input.isMendatory ? "*" : ""}`}</Label> {/* Render the field label */}
                      {!input.type ? (
                        <Controller
                          render={({ field }) => {
                            return <TextInput onChange={field.onChange} value={field.value} />;
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                        />
                      ) : (
                        <Controller
                          render={({ field }) => {
                            const Comp = fieldComponents?.[input.type];
                            return <Comp formValue={form} setValue={setValue} onChange={field.onChange} value={field.value} />;
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
                        className="inbox-search-form-error ew-search-top-spacing"
                      >
                        {formState?.errors?.[input.name]?.message}
                      </span>
                    ) : null}
                  </div>
                ))}

              {/* Render the "Clear All" link for inbox pages */}
              {isInboxPage && (
                <div className="input-fields ew-search-spacing">
                  <div>{clearAll()}</div>
                </div>
              )}

              {/* Render the submit button for desktop view */}
              {type === "desktop" && !mobileView && (
                <div className="search-submit-wrapper ew-search-spacing-2">
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
            <button className="clear-search ew-ewdesktop-inbox-wrapper">
              {clearAll(mobileView)}
            </button>
            <SubmitBar disabled={!!Object.keys(formState.errors).length} label={t("ES_COMMON_SEARCH")} className="ew-ewdesktop-inbox-wrapper" submit={true} />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication; // Exporting the component for use in other parts of the application