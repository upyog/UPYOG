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
} from "@upyog/digit-ui-react-components";

import { useTranslation } from "react-i18next";
import "../../css/pt-inline.css";

const fieldComponents = {
  date: DatePicker,
  mobileNumber: MobileNumber,
  Locality: (props) => (
    <Localities
      tenantId={Digit.ULBService.getCurrentTenantId()}
      selectLocality={props.onChange}
      keepNull={false}
      boundaryType="revenue"
      selected={props.value}
      disableLoader={true}
      sortFn={(a, b) => (a.i18nkey < b.i18nkey ? -1 : 1)}
    />
  ),
};

const SearchApplication = ({ onSearch, type, onClose, searchFields, searchParams, isInboxPage, defaultSearchParams, clearSearch: _clearSearch }) => {
  const { t } = useTranslation();
  const { handleSubmit, reset, watch, control, setError, clearErrors, formState, setValue } = useForm({
    defaultValues: isInboxPage ? searchParams : { locality: null, city: null, ...searchParams },
  });

  const form = watch();

  const formValueEmpty = () => {
    let isEmpty = true;
    Object.keys(form).forEach((key) => {
      if (!["locality", "city"].includes(key) && form[key]) isEmpty = false;
    });

    if (searchFields?.find((e) => e.name === "locality") && !form?.locality?.code) isEmpty = true;
    return isEmpty;
  };

  const mobileView = innerWidth <= 640;

  useEffect(() => {
    searchFields.forEach(({ pattern, name, maxLength, minLength, errorMessages, ...el }) => {
      const value = form[name];
      const error = formState.errors[name];
      if (pattern) {
        if (!new RegExp(pattern).test(value) && !error)
          setError(name, { type: "pattern", message: t(errorMessages?.pattern) || t(`PATTERN_${name.toUpperCase()}_FAILED`) });
        else if (new RegExp(pattern).test(value) && error?.type === "pattern") clearErrors([name]);
      }
      if (minLength) {
        if (value?.length < minLength && !error)
          setError(name, { type: "minLength", message: t(errorMessages?.minLength || `MINLENGTH_${name.toUpperCase()}_FAILED`) });
        else if (value?.length >= minLength && error?.type === "minLength") clearErrors([name]);
      }
      if (maxLength) {
        if (value?.length > maxLength && !error)
          setError(name, { type: "maxLength", message: t(errorMessages?.maxLength || `MAXLENGTH_${name.toUpperCase()}_FAILED`) });
        else if (value?.length <= maxLength && error?.type === "maxLength") clearErrors([name]);
      }
    });
  }, [form, formState, setError, clearErrors]);


  const onSubmitInput = (data) => {
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
      <LinkLabel className={`pt-inline-link-label ${mobileView ? "pt-inline-link-label-mobile" : ""}`} onClick={clearSearch}>
        {t("ES_COMMON_CLEAR_SEARCH")}
      </LinkLabel>
    );
  };

  return (
    <form onSubmit={handleSubmit(onSubmitInput)}>
      <React.Fragment>
        <div className={`search-container pt-inline-search-container ${isInboxPage ? "pt-inline-search-container-inbox" : ""}`}>
          <div className="search-complaint-container">
            {(type === "mobile" || mobileView) && (
              <div className="complaint-header">
                <h2>{t("ES_COMMON_SEARCH_BY")}</h2>
                <span onClick={onClose}>
                  <CloseSvg />
                </span>
              </div>
            )}
            <div className={"complaint-input-container for-pt pt-inline-complaint-grid " + (!isInboxPage ? "for-search" : "")}>
              {searchFields
                ?.filter((e) => true)
                ?.map((input, index) => (
                  <div key={input.name} className="input-fields">
                    {/* <span className={index === 0 ? "complaint-input" : "mobile-input"}> */}
                    <span className={"mobile-input"}>
                      <Label>{t(input.label) + ` ${input.isMendatory ? "*" : ""}`}</Label>
                      {!input.type ? (
                        <Controller
                          render={(props) => {
                            return <TextInput onChange={props.onChange} value={props.value} />;
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                        />
                      ) : (
                        <Controller
                          render={(props) => {
                            const Comp = fieldComponents?.[input.type];
                            return <Comp formValue={form} setValue={setValue} onChange={props.onChange} value={props.value} />;
                          }}
                          name={input.name}
                          control={control}
                          defaultValue={""}
                        />
                      )}
                    </span>
                    {formState?.dirtyFields?.[input.name] ? (
                      <span className="inbox-search-form-error pt-inline-inbox-search-error">
                        {formState?.errors?.[input.name]?.message}
                      </span>
                    ) : null}
                  </div>
                ))}

              {isInboxPage && (
                <div className="input-fields pt-inline-clear-all-wrap">
                  <div>{clearAll()}</div>
                </div>
              )}

              {type === "desktop" && !mobileView && (
                <div className="search-submit-wrapper pt-inline-search-submit-wrapper">
                  <SubmitBar
                    className="submit-bar-search"
                    label={t("ES_COMMON_SEARCH")}
                    disabled={!!Object.keys(formState.errors).length || formValueEmpty()}
                    submit
                  />
                  {!isInboxPage && <div>{clearAll()}</div>}
                </div>
              )}
            </div>
          </div>
        </div>
        {(type === "mobile" || mobileView) && (
          <ActionBar className="clear-search-container">
            <button className="clear-search pt-inline-flex-1">
              {clearAll(mobileView)}
            </button>
            <SubmitBar
              disabled={!!Object.keys(formState.errors).length}
              label={t("ES_COMMON_SEARCH")}
              className="pt-inline-submit-flex"
              submit={true}
            />
          </ActionBar>
        )}
      </React.Fragment>
    </form>
  );
};

export default SearchApplication;
