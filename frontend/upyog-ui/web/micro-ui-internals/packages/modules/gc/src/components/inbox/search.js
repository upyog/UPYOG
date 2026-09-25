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
  MobileNumber,
  Dropdown,
  Localities,
} from "@nudmcdgnpm/digit-ui-react-components";

import { useTranslation } from "react-i18next";
import "../../css/search.css";

const fieldComponents = {
  mobileNumber: MobileNumber,
  Dropdown:(props) => (
    <Dropdown
      selected={props.value}
      select={props.onChange}
      option={props.options}
      optionKey="i18nKey"
      t={props.t}
    />
  ),
};

/**
 * SearchApplication Component
 * 
 * A dynamic search form for applications, allowing users to filter by various fields
 * such as mobile number, application number, status, and venue drop-downs (e.g., hall codes).
 * The form adapts to both mobile and desktop views, with input validation and error handling.
 * It also includes functionality for clearing search filters.
 * 
 * Props:
 * - `onSearch`: Callback invoked with search data on form submission
 * - `type`: View type - "desktop" or "mobile"
 * - `onClose`: Callback to close the search popup (mobile view)
 * - `searchFields`: Array of field configurations defining the search form inputs
 * - `searchParams`: Current search parameters pre-filling the form
 * - `isInboxPage`: Boolean indicating if the search is rendered within the inbox page
 * - `defaultSearchParams`: Default values used for clearing
 * - `clearSearch`: Callback to reset search entirely
 */

const SearchApplication = ({ onSearch, type, onClose, searchFields, searchParams, isInboxPage, defaultSearchParams, clearSearch: _clearSearch }) => {
  const { t } = useTranslation();
  const { handleSubmit, reset, watch, control, setError, clearErrors, formState, setValue } = useForm({
    defaultValues: isInboxPage ? searchParams : { locality: null, city: null, ...searchParams },
  });
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();

  const { data: venueLists } = Digit.Hooks.useEnabledMDMS(tenantId, "GC", [{ name: "Venues" }],
    {
      select: (data) => {
        const formattedData = data?.["GC"]?.["Venues"]
        return formattedData;
      },
    });

  let venues = [];
    venueLists && venueLists.map((venue) => {
        venues.push({i18nKey: `${venue.code}`, code: `${venue.code}`, value: `${venue.name}`, timeSlots: venue.timeSlot, parentMasterType:venue.parentMasterType});
    });
  

  const form = watch();

  const formValueEmpty = () => {
    let isEmpty = true;
    Object.keys(form).forEach((key) => {
      if (!["locality", "city"].includes(key) && form[key]) isEmpty = false;
    });

    if (searchFields?.find((e) => e.name === "locality") && !form?.locality?.code) isEmpty = true;
    if (form?.mobileNumber && !/^[6-9][0-9]{9}$/.test(form.mobileNumber)) isEmpty = true;
    return isEmpty;
  };

  const mobileView = innerWidth <= 640;


  const onSubmitInput = (data) => {
    if (data.mobileNumber && !/^[6-9][0-9]{9}$/.test(data.mobileNumber)) {
      setError("mobileNumber", { type: "manual", message: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID") });
      return;
    }
    if (!data.mobileNumber) {
      delete data.mobileNumber;
    }

    data.delete = [];

    searchFields.forEach((field) => {
      if (!data[field.name]) data.delete.push(field.name);
    });

    onSearch(data);
    if (type === "mobile") {
      onClose();
    }
  };

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

  const clearAll = (mobileView) => {
    return (
      <LinkLabel className={`gc-clear-search-link ${mobileView ? "mobile-view" : ""}`} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_ALL")}
      </LinkLabel>
    );
  };
  
  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className={`search-container gc-search-container ${isInboxPage ? "is-inbox" : ""}`}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className={`complaint-input-container for-pt gc-inbox-input-container ${!isInboxPage ? "for-search" : "is-inbox"}`}>
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    {/* <span className={index === 0 ? "complaint-input" : "mobile-input"}> */}
                    <span className={"mobile-input"}>
                      <Label>{t(input.label) + ` ${input.isMendatory ? "*" : ""}`}</Label>
                      {!input.type ? (
                        <Controller
                          render={({ field }) => {
                            return <TextInput onChange={field.onChange} value={field.value} />;
                          }}
                          name={input.name}
                          control={control}
                          rules={{
                            ...(input.pattern
                              ? {
                                  pattern: {
                                    value: new RegExp(input.pattern),
                                    message: t(input.errorMessages?.pattern || "CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
                                  },
                                }
                              : {}),
                            ...(input.minLength ? { minLength: { value: input.minLength, message: t(input.errorMessages?.minLength || "CORE_COMMON_INVALID_MIN_LENGTH") } } : {}),
                            ...(input.maxLength ? { maxLength: { value: input.maxLength, message: t(input.errorMessages?.maxLength || "CORE_COMMON_INVALID_MAX_LENGTH") } } : {}),
                          }}
                          defaultValue={""}
                        />
                      ) : (
                        <Controller
                          render={({ field }) => {
                            const Comp = fieldComponents?.[input.type];
                            return <Comp formValue={form} setValue={setValue} onChange={field.onChange} value={field.value} options={venues} t={t}/>;
                          }}
                          name={input.name}
                          control={control}
                          rules={{
                            ...(input.pattern
                              ? {
                                  pattern: {
                                    value: new RegExp(input.pattern),
                                    message: t(input.errorMessages?.pattern || "CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
                                  },
                                }
                              : input.type === "mobileNumber"
                              ? {
                                  pattern: {
                                    value: /^[6-9][0-9]{9}$/,
                                    message: t("CORE_COMMON_APPLICANT_MOBILE_NUMBER_INVALID"),
                                  },
                                }
                              : {}),
                            ...(input.minLength ? { minLength: { value: input.minLength, message: t(input.errorMessages?.minLength || "CORE_COMMON_INVALID_MIN_LENGTH") } } : {}),
                            ...(input.maxLength ? { maxLength: { value: input.maxLength, message: t(input.errorMessages?.maxLength || "CORE_COMMON_INVALID_MAX_LENGTH") } } : {}),
                          }}
                          defaultValue={""}
                        />
                      )}
                    </span>
                    {formState?.dirtyFields?.[input.name] ? (
                      <span className="inbox-search-form-error gc-inbox-search-form-error">
                        {formState?.errors?.[input.name]?.message}
                      </span>
                    ) : null}
                  </div>
                ))}

              {type === "desktop" && !mobileView && (
                <div className={`search-submit-wrapper gc-search-submit-wrapper ${isInboxPage ? "is-inbox" : ""}`}>
                  <SubmitBar
                    className="submit-bar-search"
                    label={t("ES_COMMON_SEARCH")}
                    disabled={!!Object.keys(formState.errors).length || formValueEmpty()}
                    submit
                  />
                  <div className="clear-search">
                    {clearAll()}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
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

export default SearchApplication;