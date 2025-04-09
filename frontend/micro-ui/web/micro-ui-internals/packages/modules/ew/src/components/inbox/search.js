// Importing necessary components and hooks from external libraries
import React, { useEffect, useState } from "react";
import { useForm, Controller } from "react-hook-form"; // React Hook Form for managing form state
import {
  TextInput,
  Label,
  SubmitBar,
  LinkLabel,
  ActionBar,
  CloseSvg,
  DatePicker,
  MobileNumber
} from "@nudmcdgnpm/digit-ui-react-components"; // UI components for forms, inputs, and actions

import { useTranslation } from "react-i18next"; // Hook for translations

// Mapping of field types to their corresponding components
const fieldComponents = {
  date: DatePicker, // Date picker component for date fields
  mobileNumber: MobileNumber, // Mobile number input component
};

// Component to render the search functionality for E-Waste applications
const SearchApplication = ({
  onSearch, // Function to handle search actions
  type, // Type of view (mobile or desktop)
  onClose, // Function to handle closing the search form
  searchFields, // Fields to display in the search form
  searchParams, // Current search parameters
  isInboxPage, // Flag to indicate if it is an inbox page
  defaultSearchParams, // Default search parameters
  clearSearch: _clearSearch, // Function to clear the search
}) => {
  const { t } = useTranslation(); // Translation hook
  const { handleSubmit, reset, watch, control, setError, clearErrors, formState, setValue } = useForm({
    defaultValues: isInboxPage ? searchParams : { locality: null, city: null, ...searchParams }, // Set default values for the form
  });

  const form = watch(); // Watch the form values for changes

  // Function to check if the form values are empty
  const formValueEmpty = () => {
    let isEmpty = true;
    Object.keys(form).forEach((key) => {
      if (!["locality", "city"].includes(key) && form[key]) isEmpty = false; // Check if any field other than locality and city is filled
    });

    if (searchFields?.find((e) => e.name === "locality") && !form?.locality?.code) isEmpty = true; // Check if locality is empty
    return isEmpty;
  };

  const mobileView = innerWidth <= 640; // Check if the view is mobile based on screen width

  // Function to handle form submission
  const onSubmitInput = (data) => {
    if (!data.mobileNumber) {
      delete data.mobileNumber; // Remove mobile number if it is not provided
    }

    data.delete = []; // Initialize an array to store fields to be deleted

    searchFields.forEach((field) => {
      if (!data[field.name]) data.delete.push(field.name); // Add fields with empty values to the delete array
    });

    onSearch(data); // Call the search function with the form data
    if (type === "mobile") {
      onClose(); // Close the form if the view is mobile
    }
  };

  // Function to clear the search form
  function clearSearch() {
    const resetValues = searchFields.reduce((acc, field) => ({ ...acc, [field?.name]: "" }), {}); // Reset all fields to empty
    reset(resetValues); // Reset the form
    if (isInboxPage) {
      const _newParams = { ...searchParams };
      _newParams.delete = [];
      searchFields.forEach((e) => {
        _newParams.delete.push(e?.name); // Add fields to be deleted
      });
      onSearch({ ..._newParams }); // Call the search function with the updated parameters
    } else {
      _clearSearch(); // Call the clear search function
    }
  }

  // Function to render the "Clear All" link
  const clearAll = (mobileView) => {
    const mobileViewStyles = mobileView ? { margin: 0 } : {}; // Adjust styles for mobile view
    return (
      <LinkLabel style={{ display: "inline", margin: "10px", ...mobileViewStyles }} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")} {/* Translated text for "Clear Search" */}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}> {/* Form submission handler */}
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